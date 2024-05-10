package com.HashCrack.Worker.taskExecutor;

import com.HashCrack.Worker.dto.ManagerTaskDTO;
import com.HashCrack.Worker.dto.WorkerResponseDTO;
import com.HashCrack.Worker.services.ProcessTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// just linear task executor in one thread
public class TaskExecutor {
    private static Logger logger = LoggerFactory.getLogger(TaskExecutor.class);
    public static String managerHostname = "http://manager:8080";
    public static String endpoint = "/internal/api/manager/hash/crack/request";

    public static void configManagerEndpoint(String managerHostname) {
        managerHostname = managerHostname;
    }
    private final ThreadPoolExecutor executor;

    public TaskExecutor() {
        int threadsCount = Runtime.getRuntime().availableProcessors() * 10;

        executor = new ThreadPoolExecutor(threadsCount, threadsCount, 100, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>());
        executor.allowCoreThreadTimeOut(true);  // allow terminating of core threads
    }
    private static class ExecutorTask implements Runnable {
        private final ManagerTaskDTO task;
        private final ProcessTaskService processTaskService;

        private ExecutorTask(ManagerTaskDTO task) {
            this.task = task;
            processTaskService = new ProcessTaskService();
        }
        @Override
        public void run() {
            String result = processTaskService.processTask(this.task);

            logger.info("task result=" + result);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());   // config rest template for supporting PATCH method

            String managerUrl = TaskExecutor.managerHostname + endpoint;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            WorkerResponseDTO responseDTO = new WorkerResponseDTO(result, task.getRequestId());
            HttpEntity<WorkerResponseDTO> requestEntity = new HttpEntity<>(responseDTO, headers);

            restTemplate.exchange(managerUrl, HttpMethod.PATCH, requestEntity, WorkerResponseDTO.class);
        }
    }
    public void execute(ManagerTaskDTO task) {
        logger.info("new task supplied: " + task.getRequestId() +
                " start index=" + task.getStartIndex() +
                " words count=" + task.getWordsCount() +
                " hash=" + task.getMd5Hash());
        executor.execute(new ExecutorTask(task));
    }
}
