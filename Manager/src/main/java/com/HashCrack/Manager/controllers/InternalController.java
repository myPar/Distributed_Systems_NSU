package com.HashCrack.Manager.controllers;

import com.HashCrack.Manager.Data.RequestTable;
import com.HashCrack.Manager.dto.PatchRequestDTO;
import com.HashCrack.Manager.services.PatchRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/api/manager")
public class InternalController {
    private Logger logger = LoggerFactory.getLogger(InternalController.class);
    private final PatchRequestService patchRequestService;

    @Autowired
    public InternalController(RequestTable requestTable) {
        patchRequestService = new PatchRequestService(requestTable);
    }

    @PatchMapping("/hash/crack/request")
    public ResponseEntity<?> patchRequestFromWorker(@RequestBody PatchRequestDTO patchRequestDTO) {
        logger.info("handled request PATCH: RequestId=" + patchRequestDTO.getRequestId() + " word=" + patchRequestDTO.getWord());
        patchRequestService.patchRequest(patchRequestDTO);
        return ResponseEntity.ok().body("");
    }
}
