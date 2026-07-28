package com.add.log.handler;

import com.add.log.dao.LogDao;
import com.add.log.dto.StageLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class StageLogEventHandler implements EventLogHandler {

    private static final Set<String> SUPPORTED_EVENTS =
            Set.of("STAGE_STARTED", "STAGE_ENDED");

    @Autowired
    private LogDao logDao;

    private final List<StageLogDto> buffer = new ArrayList<>();

    @Override
    public boolean supports(String eventName) {
        return SUPPORTED_EVENTS.contains(eventName);
    }

    @Override
    public void collect(LogEventContext ctx) {
        Map<String, Object> payload = ctx.getPayload();
        String eventName = ctx.getEventName();

        StageLogDto dto = new StageLogDto();
        dto.setLogId(ctx.getLogId());
        dto.setRunId(ctx.getRunId());
        dto.setStageId(ctx.getStageId());
        dto.setBatchId(ctx.getBatchId());
        dto.setSessionId(ctx.getSessionId());
        dto.setEventName(eventName);
        dto.setOccurredAt(ctx.getOccurredAt());

        if ("STAGE_STARTED".equals(eventName)) {
            dto.setStageAttempt((Integer) payload.get("stage_attempt"));
            dto.setLoadedGame((Boolean) payload.get("is_loaded_game"));
        } else if ("STAGE_ENDED".equals(eventName)) {
            dto.setEndReason((String) payload.get("end_reason"));

            Number totalTime = (Number) payload.get("total_stage_time_ms");
            dto.setTotalStageTimeMs(totalTime != null ? totalTime.longValue() : null);

            dto.setNextStageId((String) payload.get("next_stage_id"));
            dto.setFinalWorkDiscipline((Integer) payload.get("final_work_discipline"));
            dto.setFinalAuditRisk((Integer) payload.get("final_audit_risk"));
        }

        buffer.add(dto);
    }

    @Override
    public void flush() {
        if (!buffer.isEmpty()) {
            logDao.insertStageLogs(buffer);
            buffer.clear();
        }
    }
}
