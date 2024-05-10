package com.HashCrack.Worker.controllers;
import com.HashCrack.Worker.dto.ManagerTaskDTO;
import com.HashCrack.Worker.taskExecutor.TaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/api/worker/hash/crack")
public class ManagerRequestController {
    private Logger logger = LoggerFactory.getLogger(ManagerRequestController.class);
    private final TaskExecutor taskExecutor;

    public ManagerRequestController() {
        this.taskExecutor = new TaskExecutor();
    }

    @PostMapping("/task")
    public ResponseEntity<?> processTask(@RequestBody ManagerTaskDTO requestDTO) {
        logger.info("handled request POST: requestId=" + requestDTO.getRequestId() +
                    " start index=" + requestDTO.getStartIndex() +
                    " words count=" + requestDTO.getWordsCount() +
                    " hash=" + requestDTO.getMd5Hash());
        taskExecutor.execute(requestDTO);

        return ResponseEntity.ok().body("");
    }
}
