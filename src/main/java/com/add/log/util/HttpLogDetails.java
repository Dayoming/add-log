package com.add.log.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HttpLogDetails {
    private String method;
    private String uri;
    private String clientIp;
    private String requestBody;
    private int responseStatus;
    private String responseBody;
    private long executionTimeMs;
}
