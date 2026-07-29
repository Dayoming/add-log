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
import java.util.*;
import java.util.stream.Collectors;

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

        List<Map<String, Object>> logsToProcess = filterNewLogs(requestDto.getLogs());

        if (logsToProcess.isEmpty()) {
            response = ApiResponse.success(HttpStatus.OK, "이미 저장된 로그입니다.");
            return ResponseEntity.ok(response);
        }

        insertGameAndEventLogs(requestDto);

        response = ApiResponse.success(HttpStatus.OK, "로그 저장 완료");
        return ResponseEntity.ok(response);
    }

    // 같은 요청 안의 중복 log_id 제거, 이미 DB에 저장된 log_id 제외
    private List<Map<String, Object>> filterNewLogs(List<Map<String, Object>> logs) {
        // 요청 내 중복 제거
        Map<String, Map<String, Object>> dedupedByLogId = new LinkedHashMap<>();
        for (Map<String, Object> logMap : logs) {
            String logId = (String) logMap.get("log_id");
            dedupedByLogId.putIfAbsent(logId, logMap);
        }

        // 이미 저장된 log_id 조회 후 제외
        List<String> candidateLogIds = new ArrayList<>(dedupedByLogId.keySet());
        List<String> existingLogIds = logDao.findExistingLogIds(candidateLogIds);
        Set<String> existingSet = new HashSet<>(existingLogIds);

        return dedupedByLogId.values().stream()
                .filter(logMap -> !existingSet.contains((String) logMap.get("log_id")))
                .collect(Collectors.toList());
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
                    requestDto.getClientId(),
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
