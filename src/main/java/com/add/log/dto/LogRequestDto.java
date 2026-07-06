package com.add.log.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class LogRequestDto {
    private String clientId;
    private String sessionId;
    private String runId;
    private String playerId;
    private String gameVersion;
    private String platform;
    private String language;
    private String sentAt;

    private List<Map<String, Object>> logs;
}
