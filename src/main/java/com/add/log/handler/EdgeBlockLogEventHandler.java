package com.add.log.handler;

import com.add.log.dao.LogDao;
import com.add.log.dto.EdgeBlockLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class EdgeBlockLogEventHandler implements EventLogHandler {

    private static final Set<String> SUPPORTED_EVENTS = Set.of(
            "EDGE_BLOCK_CRAFTED",
            "EDGE_BLOCK_USAGE_SUMMARY"
    );

    @Autowired
    private LogDao logDao;

    private final List<EdgeBlockLogDto> buffer = new ArrayList<>();

    @Override
    public boolean supports(String eventName) {
        return SUPPORTED_EVENTS.contains(eventName);
    }

    @Override
    public void collect(LogEventContext ctx) {
        Map<String, Object> payload = ctx.getPayload();
        String eventName = ctx.getEventName();

        if ("EDGE_BLOCK_CRAFTED".equals(eventName)) {
            EdgeBlockLogDto dto = buildBaseDto(ctx);
            dto.setEdgeBlockId((String) payload.get("edge_block_id"));
            dto.setUsageCount(null);
            buffer.add(dto);

        } else if ("EDGE_BLOCK_USAGE_SUMMARY".equals(eventName)) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> edgeBlocks =
                    (List<Map<String, Object>>) payload.get("edge_blocks");

            if (edgeBlocks == null) {
                return;
            }

            for (Map<String, Object> block : edgeBlocks) {
                EdgeBlockLogDto dto = buildBaseDto(ctx);
                dto.setEdgeBlockId((String) block.get("edge_block_id"));
                dto.setUsageCount((Integer) block.get("usage_count"));
                buffer.add(dto);
            }
        }
    }

    private EdgeBlockLogDto buildBaseDto(LogEventContext ctx) {
        EdgeBlockLogDto dto = new EdgeBlockLogDto();
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
            logDao.insertEdgeBlockLogs(buffer);
            buffer.clear();
        }
    }
}
