package com.add.log.handler;

import com.add.log.dao.LogDao;
import com.add.log.dto.SessionLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class SessionLogEventHandler implements EventLogHandler {

    private static final Set<String> SUPPORTED_EVENTS =
            Set.of("SESSION_STARTED", "SESSION_ENDED");

    @Autowired
    private LogDao logDao;

    private final List<SessionLogDto> buffer = new ArrayList<>();

    @Override
    public boolean supports(String eventName) {
        return SUPPORTED_EVENTS.contains(eventName);
    }

    @Override
    public void collect(LogEventContext ctx) {
        Map<String, Object> payload = ctx.getPayload();
        String eventName = ctx.getEventName();

        SessionLogDto dto = new SessionLogDto();
        dto.setLogId(ctx.getLogId());
        dto.setBatchId(ctx.getBatchId());
        dto.setSessionId(ctx.getSessionId());
        dto.setEventName(eventName);
        dto.setOccurredAt(ctx.getOccurredAt());

        if ("SESSION_STARTED".equals(eventName)) {
            dto.setPlatform((String) payload.get("platform"));
            dto.setOperatingSystem((String) payload.get("operating_system"));
            dto.setSystemLanguage((String) payload.get("system_language"));
            dto.setSessionCount((Integer) payload.get("session_count"));
            dto.setIsFirstSession((Boolean) payload.get("is_first_session"));
        } else if ("SESSION_ENDED".equals(eventName)) {
            dto.setEndReason((String) payload.get("end_reason"));
            Number totalTime = (Number) payload.get("total_session_time_ms");
            dto.setTotalSessionTimeMs(totalTime != null ? totalTime.longValue() : null);
        }

        buffer.add(dto);
    }

    @Override
    public void flush() {
        if (!buffer.isEmpty()) {
            logDao.insertSessionLogs(buffer);
            buffer.clear();
        }
    }
}
