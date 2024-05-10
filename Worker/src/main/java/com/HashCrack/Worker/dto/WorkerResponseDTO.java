package com.HashCrack.Worker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkerResponseDTO {
    private String word;
    private String requestId;

    public WorkerResponseDTO(String word, String requestId) {
        this.word = word;
        this.requestId = requestId;
    }
}
