package com.add.log.svc;

import com.add.log.dao.LogDao;
import com.add.log.dto.GameLogDto;
import com.add.log.dto.LogRequestDto;
import com.add.log.exception.ApiResponse;
import com.add.log.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LogSvc {

    @Autowired
    private LogDao logDao;

    public ResponseEntity<ApiResponse<?>> receiveLogs(LogRequestDto requestDto) {
        ApiResponse<Void> response = null;

        // logs가 포함되어 있지 않은 경우 예외 발생
        if (requestDto == null || requestDto.getLogs() == null) {
            throw new InvalidRequestException("요청에 필수 데이터('logs')가 포함되어 있지 않습니다.");
        }

        if (requestDto.getLogs().isEmpty()) {
            response = ApiResponse.success(HttpStatus.OK, "로그가 비어 있습니다.");
            return ResponseEntity.ok(response);
        }

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

        logDao.insertLogs(gameLogs);
        response = ApiResponse.success(HttpStatus.OK, "로그 저장 완료");
        return ResponseEntity.ok(response);
    }

}
