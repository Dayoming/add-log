package com.add.log.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessionLogDto {
    private String logId;
    private String batchId;
    private String sessionId;
    private String eventName;
    private LocalDateTime occurredAt;
    private String platform;
    private String operatingSystem;
    private String systemLanguage;
    private Integer sessionCount;
    private Boolean isFirstSession;
    private String endReason;
    private Long totalSessionTimeMs;
}
