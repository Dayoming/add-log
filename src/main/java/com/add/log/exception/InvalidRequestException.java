package com.add.log.exception;

/**
 * 커스텀 예외 정의
 */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
