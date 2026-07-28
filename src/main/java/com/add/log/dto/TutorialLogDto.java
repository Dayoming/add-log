package com.add.log.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TutorialLogDto {
    private String logId;
    private String runId;
    private String stageId;
    private String batchId;
    private String sessionId;
    private String eventName;
    private LocalDateTime occurredAt;

    private String tutorialId;

    // TUTORIAL_STEP_COMPLETED
    private String stepId;
    private Integer stepOrder;
    private Long stepTimeMs;

    // TUTORIAL_COMPLETED
    private Long totalTutorialTimeMs;
    private Integer completedStepCount;

    // TUTORIAL_SKIPPED
    private String skippedAtStepId;
    private Integer skippedAtStepOrder;
    private Long elapsedBeforeSkipMs;
}
