package com.add.log.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventQuestLogDto {
    private String logId;
    private String runId;
    private String stageId;
    private String batchId;
    private String sessionId;
    private String eventName;
    private LocalDateTime occurredAt;

    private String contentType;
    private String eventId;
    private String choiceId;
    private Boolean hasQuest;
    private String resultType;
}
