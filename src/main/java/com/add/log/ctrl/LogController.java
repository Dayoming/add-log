package com.add.log.ctrl;

import com.add.log.exception.ApiResponse;
import com.add.log.svc.LogSvc;
import com.add.log.dto.LogBatchesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/logs")
public class LogController {

    @Autowired
    private LogSvc logSvc;

    /**
     * 로그 등록
     * @param requestDto
     * @return
     */
    @PostMapping
    public ResponseEntity<ApiResponse<?>> receiveLogs(@RequestBody LogBatchesDto requestDto) {
        return logSvc.receiveLogs(requestDto);
    }
}
