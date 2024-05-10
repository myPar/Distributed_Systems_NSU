package com.HashCrack.Manager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatchRequestDTO {
    @NotBlank(message =  "request id can't be an empty string")
    String requestId;
    String word;
}
