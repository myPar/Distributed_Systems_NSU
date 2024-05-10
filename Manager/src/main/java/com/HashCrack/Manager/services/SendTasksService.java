package com.HashCrack.Manager.services;

import com.HashCrack.Manager.Data.RequestTable;
import com.HashCrack.Manager.dto.HashCrackDTO;
import com.HashCrack.Manager.dto.WorkerTaskDTO;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class SendTasksService {
    @NoArgsConstructor
    public static class SendTasksServiceException extends Exception {
        public SendTasksServiceException(String message) {
            super(message);
        }
    }
    private int[] ports;
    private String[] workersURLs;
    private RequestTable requestTable;
    private static final int startPort = 5050;
    private static final String baseWorkerEndpoint = "/internal/api/worker/hash/crack/task";
    private static String baseWorkerHostName = "http://worker";
    private static final int alphabetCardinality = 36;
    private static int workersCount = 2;

    public static void configWorkersCount(int count) {
        workersCount = count;
    }
    public static void configBaseWorkerHostName(String hostName) {
        baseWorkerHostName = hostName;
    }
    private final RestTemplate restTemplate;

    @Autowired
    public SendTasksService(RequestTable requestTable) {
        this.requestTable = requestTable;
        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());   // custom http client, which supports PATCH request

        ports = new int[workersCount];
        // init ports
        for (int i = 0; i < workersCount; i++) {
            ports[i] = startPort + i;
        }
        // init urls
        workersURLs = new String[workersCount];
        for (int i = 0; i < workersCount; i++) {
            workersURLs[i] = baseWorkerHostName + ":" + ports[i] + baseWorkerEndpoint;
        }
    }

    private int calcWordsSetCardinality(int maxLength) {
        int result = 0;

        for (int i = 1; i <= maxLength; i++) {
            result += Math.pow(alphabetCardinality, i);
        }
        return result;
    }

    private WorkerTaskDTO[] createTasks(HashCrackDTO hashCrackDTO, String requestId) {
        WorkerTaskDTO[] tasks = new WorkerTaskDTO[workersCount];
        String hash = hashCrackDTO.getHash();

        int maxLength = hashCrackDTO.getMaxLength();
        int wordsSetCardinality = calcWordsSetCardinality(maxLength);
        int countPerWorker = wordsSetCardinality / workersCount;
        int remain = wordsSetCardinality % workersCount;
        int stIdx = 0;

        for (int i = 0; i < workersCount; i++) {
            int wordsCount = countPerWorker;

            if (remain > 0) {
                wordsCount += 1;
                remain -= 1;
            }
            tasks[i] = WorkerTaskDTO.builder()
                       .requestId(requestId)
                       .md5Hash(hash)
                       .startIndex(stIdx)
                       .wordsCount(wordsCount)
                       .build();
            stIdx += wordsCount;
        }
        return tasks;
    }

    public void sendTasks(HashCrackDTO hashCrackDTO, String requestId) throws SendTasksServiceException {
        requestTable.addNewRequest(requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        WorkerTaskDTO[] tasks = createTasks(hashCrackDTO, requestId);

        for (int i = 0; i < workersCount; i++) {
            String url = workersURLs[i];
            WorkerTaskDTO task = tasks[i];

            HttpEntity<WorkerTaskDTO> entity = new HttpEntity<>(task, headers);

            try {
                restTemplate.exchange(url, HttpMethod.POST, entity, WorkerTaskDTO.class);
            }
            catch (RestClientException e) {
                throw new SendTasksServiceException("on worker " + i + " - " + e.getMessage());
            }
        }
    }
}
