package com.add.log.util;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * HTTP 요청/응답 로깅 필터
 * @description 모든 HTTP 요청 및 응답을 로깅하는 필터 클래스. 요청 URL, 헤더, 본문 및 응답 상태 코드를 기록
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingFilter implements Filter {

    private static final Logger CALL_LOGGER = LoggerFactory.getLogger("com.add.log.CALL");
    // 캐시 제한 크기 설정 - 1MB
    private static final int CACHE_LIMIT = 1024 * 1024;
    // 로그 키 생성 문자열
    private static final String ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    // 로그 키 포맷
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request, CACHE_LIMIT);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);

        String logKey = generateLogKey();
        String requestTime = LocalDateTime.now().format(FORMATTER);

        try {
            // 다음 필터 또는 컨트롤러로 요청 전달
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            logStart(logKey, requestTime, requestWrapper);
            String responseTime = LocalDateTime.now().format(FORMATTER);
            logEnd(logKey, responseTime, responseWrapper);

            // 캐싱해둔 내용 클라이언트에 전달
            responseWrapper.copyBodyToResponse();
        }
    }

    /**
     * 요청 시작 로그 기록
     *
     * @param logKey  요청 식별 키
     * @param requestTime 요청 시간
     * @param request HTTP 요청 객체
     */
    private void logStart(String logKey, String requestTime, ContentCachingRequestWrapper request) {
        // CALL LOG
        CALL_LOGGER.info(String.format("[%s]=============================   START CALL LOG  ==================================================", logKey));
        CALL_LOGGER.info(String.format("[%s][REQUEST] [%s] URI : %s", logKey, requestTime, request.getRequestURI()));

        // HTTP Header 출력
        try {
            String headersJson = getHeadersAsJson(request);
            CALL_LOGGER.info(String.format("[%s]     [HTTP HEADER] %s", logKey, headersJson));
        } catch (Exception e) {
            CALL_LOGGER.error(String.format("[%s] Error while logging headers", logKey), e);
        }
        // HTTP Body 출력
        String requestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
        CALL_LOGGER.info(String.format("[%s]     [HTTP BODY] %s", logKey, requestBody));
    }

    /**
     * 요청 종료 로그 기록
     *
     * @param logKey       요청 식별 키
     * @param responseTime 응답 시간
     * @param response     HTTP 응답 객체
     */
    private void logEnd(String logKey, String responseTime, ContentCachingResponseWrapper response) {
        int statusCode = response.getStatus(); // HTTP 상태 코드

        // 성공 또는 실패 여부 결정
        String statusMessage = (statusCode >= 200 && statusCode < 300) ? "성공" : "실패";
        String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);

        // CALL 로그 기록 (응답 본문 및 성공/실패 메시지 포함)
        CALL_LOGGER.info(String.format("[%s][RESPONSE] [%s] %d %s %s", logKey, responseTime, statusCode, statusMessage, responseBody));
        CALL_LOGGER.info(String.format("[%s]=============================   END CALL LOG    ==================================================", logKey));
    }

    /**
     * 로그 키 생성 (YYYYMMDDHHMMSSSSS + 랜덤 8자리 문자열)
     * @return 로그 키 문자열
     */
    private String generateLogKey() {
        // 랜덤 4자리 문자열 생성
        String randomPart1 = generateRandomString(4);
        String randomPart2 = generateRandomString(4);

        return LocalDateTime.now().format(FORMATTER) + randomPart1 + randomPart2;
    }

    /**
     * 랜덤 문자열 생성 함수
     * @param length 생성할 문자열 길이
     * @return 랜덤 문자열
     */
    private static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHANUMERIC_CHARS.length());
            sb.append(ALPHANUMERIC_CHARS.charAt(index));
        }

        return sb.toString();
    }

    private String getHeadersAsJson(HttpServletRequest request) throws Exception {
        Map<String, String> headers = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }
        return objectMapper.writeValueAsString(headers);
    }
}
