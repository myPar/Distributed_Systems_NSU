package com.HashCrack.Manager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckStatusResponseDTO {
    @NotBlank(message = "status can't be an empty string")
    private String status;

    private String data;
}
