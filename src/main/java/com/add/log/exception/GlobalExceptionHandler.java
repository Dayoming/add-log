package com.add.log.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 요청 형식 불일치 (400 Bad Request)
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse> handleInvalidRequest(InvalidRequestException ex) {
        ApiResponse<Void> response = ApiResponse.error(HttpStatus.BAD_REQUEST,
                ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 날짜 파싱 오류 (400 Bad Request)
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiResponse> handleDateTimeParse(DateTimeParseException ex) {
        ApiResponse<Void> response = ApiResponse.error(HttpStatus.BAD_REQUEST,
                "날짜 형식이 올바르지 않습니다: " + ex.getParsedString());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 데이터베이스 오류 (500 Internal Server Error)
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse> handleDatabaseException(DataAccessException ex) {
        // 보안을 위해 상세한 SQL 에러 대신 추상화된 메시지를 제공하는 것이 좋습니다.
        ApiResponse<Void> response = ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                "데이터베이스 처리 중 오류가 발생했습니다.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 그 외 예상치 못한 모든 서버 오류 (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleAllException(Exception ex) {
        ApiResponse<Void> response = ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                "서버 내부 오류가 발생했습니다: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
