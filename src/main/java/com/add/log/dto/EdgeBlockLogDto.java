package com.add.log.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EdgeBlockLogDto {
    private String logId;
    private String runId;
    private String stageId;
    private String batchId;
    private String sessionId;
    private String eventName;
    private LocalDateTime occurredAt;

    private String edgeBlockId;
    private Integer usageCount;
}
