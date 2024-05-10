package com.HashCrack.Manager.Data;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class RequestTable {
    @NoArgsConstructor
    public static class RequestException extends RuntimeException {
        public RequestException(String message) {super(message);}
    }
    private final HashMap<String, RequestData> requestsStatusMap;
    private final long timeout;// in seconds

    @Autowired
    public RequestTable() {
        requestsStatusMap = new HashMap<>();
        timeout = 100;
    }

    public synchronized void addNewRequest(String key) {
        String status = RequestData.IN_PROGRESS;
        Long creationTime = System.currentTimeMillis();

        RequestData newRequestDTO = RequestData.builder().status(status).creationTime(creationTime).build();

        requestsStatusMap.put(key, newRequestDTO);
    }

    // private method so, can't be called by several threads simultaneously
    private boolean timeoutExceeded(String key) {
        Long currentTime = System.currentTimeMillis();
        long delta = currentTime - requestsStatusMap.get(key).getCreationTime();

        return delta > timeout * 1000;
    }

    // returns RequestData and updates it's status if necessary
    public synchronized RequestData checkStatus(String key) throws RequestException {
        RequestData requestData = requestsStatusMap.get(key);

        if (requestData == null) {
            throw new RequestException("No request with id=" + key);
        }

        String currentStatus = requestData.getStatus();

        if (!currentStatus.equals(RequestData.IN_PROGRESS)) {
            return requestData;
        }
        if (timeoutExceeded(key)) {
            requestData.setStatus(RequestData.ERROR);
        }
        return requestData;
    }

    // set READY status to request with specified key (id) if possible
    public synchronized void trySetReadyStatus(String key, String word) {
        RequestData requestData = checkStatus(key);

        if (requestData.getStatus().equals(RequestData.IN_PROGRESS) && !word.isEmpty()) {
            requestData.setData(word);
            requestData.setStatus(RequestData.READY);
        }
    }
}
