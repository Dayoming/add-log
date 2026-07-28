package com.add.log.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GameLogDto {
    private String logId;
    private String batchId;
    private String runId;
    private String eventName;
    private Integer eventVersion;
    private LocalDateTime occurredAt;
    private Integer sequence;
    private Integer sessionElapsedMs;
    private Integer runElapsedMs;
    private Integer stageElapsedMs;
    private String stageId;
    private Integer playthroughCount;
    private Object payload;
}
