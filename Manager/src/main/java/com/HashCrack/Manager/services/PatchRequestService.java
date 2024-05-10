package com.HashCrack.Manager.services;

import com.HashCrack.Manager.Data.RequestTable;
import com.HashCrack.Manager.dto.PatchRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class PatchRequestService {
    public enum PatchStatus{IN_PROGRESS, ALL_RESPONDED}
    private final RequestTable requestTable;
    private final HashMap<String, Integer> workersResponsesMap;   // key - request id, value - count of responded workers
    private int maxWorkersCount = 1;

    public void setWorkersCount(int workersCount) {this.maxWorkersCount = workersCount;}
    @Autowired
    public PatchRequestService(RequestTable requestTable) {
        workersResponsesMap = new HashMap<>();
        this.requestTable = requestTable;
    }

    // add new record if no such key, increment existing count if not. returns count value
    private int incrementWorkersCount(String key) {
        if (!workersResponsesMap.containsKey(key)) {
            workersResponsesMap.put(key, 1);
            return 1;
        }
        Integer previousVal = workersResponsesMap.get(key);
        if (previousVal >= maxWorkersCount) {
            return previousVal;
        }
        workersResponsesMap.put(key, previousVal + 1);

        return previousVal + 1;
    }

    //
    public PatchStatus patchRequest(PatchRequestDTO workerResponse) {
        String word = workerResponse.getWord();
        String requestId = workerResponse.getRequestId();

        int count = incrementWorkersCount(requestId);
        requestTable.trySetReadyStatus(requestId, word);

        if (count == maxWorkersCount) {
            return PatchStatus.ALL_RESPONDED;
        }
        return PatchStatus.IN_PROGRESS;
    }
}
