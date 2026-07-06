package com.add.log.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GameLogDto {
    private String runId;
    private String eventType;
    private Integer submitTurn;
    private LocalDateTime timestamp;
    private Object log;
}
