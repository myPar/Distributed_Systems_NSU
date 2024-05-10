package com.HashCrack.Manager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HashCrackDTO {
    @NotBlank(message = "hash should not be an empty string")
    private String hash;

    @Min(value = 1, message = "word max length should be positive")
    private Integer maxLength;
}
