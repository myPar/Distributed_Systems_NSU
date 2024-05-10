package com.HashCrack.Manager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkerTaskDTO {
    @NotNull(message = "necessary field")
    @Min(value = 1, message = "word length has min length=1")
    private Integer wordsCount;

    @NotNull(message = "necessary field")
    @Min(value = 0, message = "start index of words set should be >= 0")
    private Integer startIndex;

    @NotBlank
    private String md5Hash;

    @NotBlank
    private String requestId;
}
