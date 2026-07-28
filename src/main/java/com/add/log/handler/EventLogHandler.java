package com.add.log.handler;

public interface EventLogHandler {
    boolean supports(String eventName);
    void collect(LogEventContext ctx);
    void flush();
}
