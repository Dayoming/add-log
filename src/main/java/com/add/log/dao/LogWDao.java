package com.add.log.dao;

import com.add.log.dto.GameLogDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LogWDao {
    // 로그 등록
    void insertLogs(List<GameLogDto> gameLogs);
}
