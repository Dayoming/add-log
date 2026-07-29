package com.add.log.dao;

import com.add.log.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LogDao {
    // 배치 로그 등록
    void insertBatchLogs(LogBatchesDto logBatches);
    // 게임 로그 등록
    void insertGameLogs(List<GameLogDto> gameLogs);
    // 세션 로그 등록
    void insertSessionLogs(List<SessionLogDto> sessionLogs);
    // 런 로그 등록
    void insertRunLogs(List<RunLogDto> runLogs);
    // 스테이지 로그 등록
    void insertStageLogs(List<StageLogDto> stageLogs);
    // 스탯 로그 등록
    void insertStatLogs(List<StatLogDto> statLogs);
    // 이벤트 로그 등록
    void insertEventQuestLogs(List<EventQuestLogDto> eventQuestLogs);
    // 튜토리얼 로그 등록
    void insertTutorialLogs(List<TutorialLogDto> tutorialLogs);
    // 프로토콜 제작 및 사용 로그 등록
    void insertEdgeBlockLogs(List<EdgeBlockLogDto> edgeBlockLogs);
    // 청사진 이용 로그 등록
    void insertBlueprintLogs(List<BlueprintLogDto> blueprintLogs);

    // logId 조회
    List<String> findExistingLogIds(List<String> logIds);
}
