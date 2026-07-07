package com.add.log.exception;

/**
 * 커스텀 예외 정의 - runId 중복
 */
public class DuplicateRequestException extends RuntimeException {
    public DuplicateRequestException(String message) {
        super(message);
    }
}
