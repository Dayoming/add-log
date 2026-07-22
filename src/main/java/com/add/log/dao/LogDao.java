package com.add.log.dao;

import com.add.log.dto.GameLogsDto;
import com.add.log.dto.LogBatchesDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LogDao {
    // 배치 로그 등록
    void insertBatchLogs(LogBatchesDto logBatches);
    // 게임 로그 등록
    void insertGameLogs(List<GameLogsDto> gameLogs);
}
