package com.add.log.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatLogDto {
    private String logId;
    private String runId;
    private String stageId;
    private String batchId;
    private String sessionId;
    private String eventName;
    private LocalDateTime occurredAt;

    private String statType;
    private Integer beforeValue;
    private Integer requestedDelta;
    private Integer appliedDelta;
    private Integer afterValue;
    private String sourceType;
    private String sourceId;
    private String sourceActionId;
    private String reasonId;
    private Boolean causedGameOver;
}
