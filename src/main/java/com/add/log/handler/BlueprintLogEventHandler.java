package com.add.log.handler;

import com.add.log.dao.LogDao;
import com.add.log.dto.BlueprintLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class BlueprintLogEventHandler implements EventLogHandler {

    private static final Set<String> SUPPORTED_EVENTS = Set.of(
            "BLUEPRINT_FIRST_OPENED",
            "BLUEPRINT_USAGE_SUMMARY"
    );

    @Autowired
    private LogDao logDao;

    private final List<BlueprintLogDto> buffer = new ArrayList<>();

    @Override
    public boolean supports(String eventName) {
        return SUPPORTED_EVENTS.contains(eventName);
    }

    @Override
    public void collect(LogEventContext ctx) {
        Map<String, Object> payload = ctx.getPayload();
        String eventName = ctx.getEventName();

        if ("BLUEPRINT_FIRST_OPENED".equals(eventName)) {
            // payload가 비어있으므로 기본 정보만 담은 행 1건
            buffer.add(buildBaseDto(ctx));

        } else if ("BLUEPRINT_USAGE_SUMMARY".equals(eventName)) {
            Integer totalOpenCount = (Integer) payload.get("total_open_count");
            Integer totalLoadCount = (Integer) payload.get("total_load_count");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> blueprints =
                    (List<Map<String, Object>>) payload.get("blueprints");

            if (blueprints == null || blueprints.isEmpty()) {
                // 혹시 배열이 비어있어도 총합 정보는 남기고 싶다면 행 1건 추가
                BlueprintLogDto dto = buildBaseDto(ctx);
                dto.setTotalOpenCount(totalOpenCount);
                dto.setTotalLoadCount(totalLoadCount);
                buffer.add(dto);
                return;
            }

            for (Map<String, Object> bp : blueprints) {
                BlueprintLogDto dto = buildBaseDto(ctx);
                dto.setTotalOpenCount(totalOpenCount);
                dto.setTotalLoadCount(totalLoadCount);
                dto.setBlueprintId((String) bp.get("blueprint_id"));
                dto.setLoadCount((Integer) bp.get("load_count"));
                buffer.add(dto);
            }
        }
    }

    private BlueprintLogDto buildBaseDto(LogEventContext ctx) {
        BlueprintLogDto dto = new BlueprintLogDto();
        dto.setLogId(ctx.getLogId());
        dto.setRunId(ctx.getRunId());
        dto.setStageId(ctx.getStageId());
        dto.setBatchId(ctx.getBatchId());
        dto.setSessionId(ctx.getSessionId());
        dto.setEventName(ctx.getEventName());
        dto.setOccurredAt(ctx.getOccurredAt());
        return dto;
    }

    @Override
    public void flush() {
        if (!buffer.isEmpty()) {
            logDao.insertBlueprintLogs(buffer);
            buffer.clear();
        }
    }
}