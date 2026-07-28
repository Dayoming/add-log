package com.add.log.svc;

import com.add.log.dao.LogDao;
import com.add.log.dto.GameLogDto;
import com.add.log.dto.LogBatchesDto;
import com.add.log.exception.ApiResponse;
import com.add.log.exception.InvalidRequestException;
import com.add.log.handler.EventLogHandler;
import com.add.log.handler.LogEventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LogSvc {

    @Autowired
    private LogDao logDao;

    @Autowired
    private List<EventLogHandler> eventLogHandlers;

    @Transactional
    public ResponseEntity<ApiResponse<?>> receiveLogs(LogBatchesDto requestDto) {
        ApiResponse<Void> response = null;

        // logs가 포함되어 있지 않은 경우 예외 발생
        if (requestDto == null || requestDto.getLogs() == null) {
            throw new InvalidRequestException("요청에 필수 데이터('logs')가 포함되어 있지 않습니다.");
        }

        if (requestDto.getLogs().isEmpty()) {
            response = ApiResponse.success(HttpStatus.OK, "로그가 비어 있습니다.");
            return ResponseEntity.ok(response);
        }

        insertBatchLogs(requestDto);
        insertGameAndEventLogs(requestDto);

        response = ApiResponse.success(HttpStatus.OK, "로그 저장 완료");
        return ResponseEntity.ok(response);
    }

    private void insertBatchLogs(LogBatchesDto requestDto) {
        LogBatchesDto logBatchesDto = new LogBatchesDto();
        logBatchesDto.setBatchId(requestDto.getBatchId());
        logBatchesDto.setClientId(requestDto.getClientId());
        logBatchesDto.setSessionId(requestDto.getSessionId());
        logBatchesDto.setGameVersion(requestDto.getGameVersion());
        logBatchesDto.setSchemaVersion(requestDto.getSchemaVersion());
        logBatchesDto.setSentAt(requestDto.getSentAt());
        logDao.insertBatchLogs(logBatchesDto);
    }

    private void insertGameAndEventLogs(LogBatchesDto requestDto) {
        List<GameLogDto> gameLogs = new ArrayList<>();

        for (Map<String, Object> logMap : requestDto.getLogs()) {
            GameLogDto gameLog = new GameLogDto();
            // 개별 컬럼으로 매핑할 값 추출
            gameLog.setRunId((String) logMap.get("run_id"));
            gameLog.setLogId((String) logMap.get("log_id"));
            gameLog.setBatchId(requestDto.getBatchId());
            gameLog.setEventName((String) logMap.get("event_name"));
            gameLog.setEventVersion((Integer) logMap.get("event_version"));

            if (logMap.get("occurred_at") != null) {
                ZonedDateTime zdt = ZonedDateTime.parse((String) logMap.get("occurred_at"));
                gameLog.setOccurredAt(zdt.toLocalDateTime());
            }

            gameLog.setSequence((Integer) logMap.get("sequence"));
            gameLog.setSessionElapsedMs((Integer) logMap.get("session_elapsed_ms"));
            gameLog.setRunElapsedMs((Integer) logMap.get("run_elapsed_ms"));
            gameLog.setStageElapsedMs((Integer) logMap.get("stage_elapsed_ms"));
            gameLog.setStageId((String) logMap.get("stage_id"));
            gameLog.setPlaythroughCount((Integer) logMap.get("playthrough_count"));

            @SuppressWarnings("unchecked")
            Map<String, Object> payload = (Map<String, Object>) logMap.get("payload");
            gameLog.setPayload(payload);
            gameLogs.add(gameLog);

            // event_name에 맞는 핸들러 찾아서 위임
            String eventName = (String) logMap.get("event_name");

            LogEventContext ctx = new LogEventContext(
                    requestDto.getBatchId(),
                    requestDto.getSessionId(),
                    logMap,
                    ZonedDateTime.parse((String) logMap.get("occurred_at")).toLocalDateTime(),
                    payload
            );

            for (EventLogHandler handler : eventLogHandlers) {
                if (handler.supports(eventName)) {
                    handler.collect(ctx);
                    break;
                }
            }
        }

        logDao.insertGameLogs(gameLogs);

        for (EventLogHandler handler : eventLogHandlers) {
            handler.flush();
        }
    }
}
