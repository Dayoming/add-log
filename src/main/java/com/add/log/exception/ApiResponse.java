package com.add.log.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String code;
    private final String message;
    private final T data;

    private ApiResponse(int status, String code, String message, T data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ApiResponse<Void> success(HttpStatus status, String message) {
        return new ApiResponse<>(status.value(), "SUCCESS", message, null);
    }

    public static <T> ApiResponse<T> success(HttpStatus status, String message, T data) {
        return new ApiResponse<>(status.value(), "SUCCESS", message, data);
    }

    public static ApiResponse<Void> error(HttpStatus status, String message) {
        return new ApiResponse<>(status.value(), status.getReasonPhrase(), message, null);
    }
}
