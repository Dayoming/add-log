package com.add.log.handler;

import java.time.LocalDateTime;
import java.util.Map;

public class LogEventContext {
    private final String batchId;
    private final String sessionId;
    private final String clientId;
    private final Map<String, Object> logMap;
    private final LocalDateTime occurredAt;
    private final Map<String, Object> payload;

    public LogEventContext(String batchId, String sessionId, String clientId, Map<String, Object> logMap,
                           LocalDateTime occurredAt, Map<String, Object> payload) {
        this.batchId = batchId;
        this.sessionId = sessionId;
        this.clientId = clientId;
        this.logMap = logMap;
        this.occurredAt = occurredAt;
        this.payload = payload;
    }

    public String getBatchId() { return batchId; }
    public String getSessionId() { return sessionId; }
    public String getClientId() { return clientId; }
    public String getEventName() { return (String) logMap.get("event_name"); }
    public String getLogId() { return (String) logMap.get("log_id"); }
    public String getRunId() { return (String) logMap.get("run_id"); }
    public String getStageId() { return (String) logMap.get("stage_id"); }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public Integer getPlaythroughCount() { return (Integer) logMap.get("playthrough_count"); }
    public Map<String, Object> getPayload() { return payload; }
}
