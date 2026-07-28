package com.add.log.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RunLogDto {
    private String logId;
    private String runId;
    private String batchId;
    private String sessionId;
    private String eventName;
    private LocalDateTime occurredAt;

    // RUN_STARTED
    private Integer playthroughCount;
    private String startType;
    private String startingStageId;
    private Boolean continueRun;   // is_continue -> continueRun (예약어 회피)

    // RUN_ENDED
    private String endReason;
    private String endingId;
    private String gameOverType;
    private String finalStageId;
    private Long totalRunTimeMs;
    private Integer finalWorkDiscipline;
    private Integer finalAuditRisk;
}
