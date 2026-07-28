package com.add.log.handler;

import com.add.log.dao.LogDao;
import com.add.log.dto.EventQuestLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class EventQuestLogEventHandler implements EventLogHandler {

    private static final Set<String> SUPPORTED_EVENTS = Set.of(
            "EVENT_QUEST_OFFERED",
            "EVENT_QUEST_OPENED",
            "EVENT_CHOICE_SELECTED",
            "EVENT_QUEST_RESOLVED"
    );

    @Autowired
    private LogDao logDao;

    private final List<EventQuestLogDto> buffer = new ArrayList<>();

    @Override
    public boolean supports(String eventName) {
        return SUPPORTED_EVENTS.contains(eventName);
    }

    @Override
    public void collect(LogEventContext ctx) {
        Map<String, Object> payload = ctx.getPayload();

        EventQuestLogDto dto = new EventQuestLogDto();
        dto.setLogId(ctx.getLogId());
        dto.setRunId(ctx.getRunId());
        dto.setStageId(ctx.getStageId());
        dto.setBatchId(ctx.getBatchId());
        dto.setSessionId(ctx.getSessionId());
        dto.setEventName(ctx.getEventName());
        dto.setOccurredAt(ctx.getOccurredAt());

        // 공통 필드
        dto.setContentType((String) payload.get("content_type"));
        dto.setEventId((String) payload.get("event_id"));

        // 이벤트별 필드 (없으면 payload.get()이 null 반환하므로 분기 없이도 안전)
        dto.setChoiceId((String) payload.get("choice_id"));
        dto.setHasQuest((Boolean) payload.get("has_quest"));
        dto.setResultType((String) payload.get("result_type"));

        buffer.add(dto);
    }

    @Override
    public void flush() {
        if (!buffer.isEmpty()) {
            logDao.insertEventQuestLogs(buffer);
            buffer.clear();
        }
    }
}
