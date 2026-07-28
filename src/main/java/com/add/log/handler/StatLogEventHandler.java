package com.add.log.handler;

import com.add.log.dao.LogDao;
import com.add.log.dto.StatLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class StatLogEventHandler implements EventLogHandler {

    private static final Set<String> SUPPORTED_EVENTS =
            Set.of("STAT_CHANGED");

    @Autowired
    private LogDao logDao;

    private final List<StatLogDto> buffer = new ArrayList<>();

    @Override
    public boolean supports(String eventName) {
        return SUPPORTED_EVENTS.contains(eventName);
    }

    @Override
    public void collect(LogEventContext ctx) {
        Map<String, Object> payload = ctx.getPayload();

        StatLogDto dto = new StatLogDto();
        dto.setLogId(ctx.getLogId());
        dto.setRunId(ctx.getRunId());
        dto.setStageId(ctx.getStageId());
        dto.setBatchId(ctx.getBatchId());
        dto.setSessionId(ctx.getSessionId());
        dto.setEventName(ctx.getEventName());
        dto.setOccurredAt(ctx.getOccurredAt());

        dto.setStatType((String) payload.get("stat_type"));
        dto.setBeforeValue((Integer) payload.get("before_value"));
        dto.setRequestedDelta((Integer) payload.get("requested_delta"));
        dto.setAppliedDelta((Integer) payload.get("applied_delta"));
        dto.setAfterValue((Integer) payload.get("after_value"));
        dto.setSourceType((String) payload.get("source_type"));
        dto.setSourceId((String) payload.get("source_id"));
        dto.setSourceActionId((String) payload.get("source_action_id"));
        dto.setReasonId((String) payload.get("reason_id"));
        dto.setCausedGameOver((Boolean) payload.get("caused_game_over"));

        buffer.add(dto);
    }

    @Override
    public void flush() {
        if (!buffer.isEmpty()) {
            logDao.insertStatLogs(buffer);
            buffer.clear();
        }
    }
}
