package com.add.log.svc;

import com.add.log.dao.LogWDao;
import com.add.log.dto.GameLogDto;
import com.add.log.dto.LogRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LogSvc {

    @Autowired
    private LogWDao logWDao;

    private Logger log = LoggerFactory.getLogger(getClass());

    public ResponseEntity<String> receiveLogs(LogRequestDto requestDto) {
        log.info("requestDto: " + requestDto);
        List<GameLogDto> gameLogs = new ArrayList<>();

        for (Map<String, Object> logMap : requestDto.getLogs()) {
            GameLogDto gameLog = new GameLogDto();

            // 개별 컬럼으로 매핑할 값 추출
            gameLog.setRunId((String) logMap.get("run_id"));
            gameLog.setEventType((String) logMap.get("event_type"));
            gameLog.setSubmitTurn((Integer) logMap.get("submit_turn"));

            if (logMap.get("timestamp") != null) {
                ZonedDateTime zdt = ZonedDateTime.parse((String) logMap.get("timestamp"));
                gameLog.setTimestamp(zdt.toLocalDateTime());
            }

            // 로그 원문 그대로 저장
            gameLog.setLog(logMap);
            gameLogs.add(gameLog);
        }

        logWDao.insertLogs(gameLogs);
        return ResponseEntity.ok("Logs saved successfully");
    }

}
