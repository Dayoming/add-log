package com.add.log.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StageLogDto {
    private String logId;
    private String runId;
    private String stageId;
    private String batchId;
    private String sessionId;
    private String eventName;
    private LocalDateTime occurredAt;

    // STAGE_STARTED
    private Integer stageAttempt;
    private Boolean loadedGame;

    // STAGE_ENDED
    private String endReason;
    private Long totalStageTimeMs;
    private String nextStageId;
    private Integer finalWorkDiscipline;
    private Integer finalAuditRisk;
}
