package com.HashCrack.Manager.Data;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RequestData {
    // status values:
    public static String IN_PROGRESS = "IN_PROGRESS";
    public static String READY = "READY";
    public static String ERROR = "ERROR";

    private Long creationTime;
    private String status;
    private String data;
}
