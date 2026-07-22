package com.add.log.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class LogBatchesDto {
    private String batchId;
    private String clientId;
    private String sessionId;
    private String runId;
    private String gameVersion;
    private String schemaVersion;
    private LocalDateTime sentAt;

    private List<Map<String, Object>> logs;
}
