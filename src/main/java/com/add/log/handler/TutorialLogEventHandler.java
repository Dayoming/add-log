package com.add.log.handler;

import com.add.log.dao.LogDao;
import com.add.log.dto.TutorialLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class TutorialLogEventHandler implements EventLogHandler {

    private static final Set<String> SUPPORTED_EVENTS = Set.of(
            "TUTORIAL_STEP_COMPLETED",
            "TUTORIAL_COMPLETED",
            "TUTORIAL_SKIPPED"
    );

    @Autowired
    private LogDao logDao;

    private final List<TutorialLogDto> buffer = new ArrayList<>();

    @Override
    public boolean supports(String eventName) {
        return SUPPORTED_EVENTS.contains(eventName);
    }

    @Override
    public void collect(LogEventContext ctx) {
        Map<String, Object> payload = ctx.getPayload();
        String eventName = ctx.getEventName();

        TutorialLogDto dto = new TutorialLogDto();
        dto.setLogId(ctx.getLogId());
        dto.setRunId(ctx.getRunId());
        dto.setStageId(ctx.getStageId());
        dto.setBatchId(ctx.getBatchId());
        dto.setSessionId(ctx.getSessionId());
        dto.setEventName(eventName);
        dto.setOccurredAt(ctx.getOccurredAt());

        dto.setTutorialId((String) payload.get("tutorial_id"));

        if ("TUTORIAL_STEP_COMPLETED".equals(eventName)) {
            dto.setStepId((String) payload.get("step_id"));
            dto.setStepOrder((Integer) payload.get("step_order"));

            Number stepTime = (Number) payload.get("step_time_ms");
            dto.setStepTimeMs(stepTime != null ? stepTime.longValue() : null);

        } else if ("TUTORIAL_COMPLETED".equals(eventName)) {
            Number totalTime = (Number) payload.get("total_tutorial_time_ms");
            dto.setTotalTutorialTimeMs(totalTime != null ? totalTime.longValue() : null);

            dto.setCompletedStepCount((Integer) payload.get("completed_step_count"));

        } else if ("TUTORIAL_SKIPPED".equals(eventName)) {
            dto.setSkippedAtStepId((String) payload.get("skipped_at_step_id"));
            dto.setSkippedAtStepOrder((Integer) payload.get("skipped_at_step_order"));

            Number elapsedBeforeSkip = (Number) payload.get("elapsed_before_skip_ms");
            dto.setElapsedBeforeSkipMs(elapsedBeforeSkip != null ? elapsedBeforeSkip.longValue() : null);
        }

        buffer.add(dto);
    }

    @Override
    public void flush() {
        if (!buffer.isEmpty()) {
            logDao.insertTutorialLogs(buffer);
            buffer.clear();
        }
    }
}
