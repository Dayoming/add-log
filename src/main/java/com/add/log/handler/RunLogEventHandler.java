package com.add.log.handler;

import com.add.log.dao.LogDao;
import com.add.log.dto.RunLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class RunLogEventHandler implements EventLogHandler {

    private static final Set<String> SUPPORTED_EVENTS =
            Set.of("RUN_STARTED", "RUN_ENDED");

    @Autowired
    private LogDao logDao;

    private final List<RunLogDto> buffer = new ArrayList<>();

    @Override
    public boolean supports(String eventName) {
        return SUPPORTED_EVENTS.contains(eventName);
    }

    @Override
    public void collect(LogEventContext ctx) {
        Map<String, Object> payload = ctx.getPayload();
        String eventName = ctx.getEventName();

        RunLogDto dto = new RunLogDto();
        dto.setLogId(ctx.getLogId());
        dto.setRunId(ctx.getRunId());
        dto.setBatchId(ctx.getBatchId());
        dto.setSessionId(ctx.getSessionId());
        dto.setEventName(eventName);
        dto.setOccurredAt(ctx.getOccurredAt());

        if ("RUN_STARTED".equals(eventName)) {
            dto.setPlaythroughCount((Integer) payload.get("playthrough_count"));
            dto.setStartType((String) payload.get("start_type"));
            dto.setStartingStageId((String) payload.get("starting_stage_id"));
            dto.setContinueRun((Boolean) payload.get("is_continue"));
        } else if ("RUN_ENDED".equals(eventName)) {
            dto.setEndReason((String) payload.get("end_reason"));
            dto.setEndingId((String) payload.get("ending_id"));
            dto.setGameOverType((String) payload.get("game_over_type"));
            dto.setFinalStageId((String) payload.get("final_stage_id"));

            Number totalTime = (Number) payload.get("total_run_time_ms");
            dto.setTotalRunTimeMs(totalTime != null ? totalTime.longValue() : null);

            dto.setFinalWorkDiscipline((Integer) payload.get("final_work_discipline"));
            dto.setFinalAuditRisk((Integer) payload.get("final_audit_risk"));
        }

        buffer.add(dto);
    }

    @Override
    public void flush() {
        if (!buffer.isEmpty()) {
            logDao.insertRunLogs(buffer);
            buffer.clear();
        }
    }
}
