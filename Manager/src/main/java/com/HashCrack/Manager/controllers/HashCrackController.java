package com.HashCrack.Manager.controllers;
import com.HashCrack.Manager.Data.RequestData;
import com.HashCrack.Manager.Data.RequestTable;
import com.HashCrack.Manager.dto.CheckStatusResponseDTO;
import com.HashCrack.Manager.dto.HashCrackDTO;
import com.HashCrack.Manager.dto.HashCrackResponseDTO;
import com.HashCrack.Manager.services.SendTasksService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hash")
public class HashCrackController {
    private final RequestTable requestTable;
    private final SendTasksService sendTasksService;

    @Autowired
    public HashCrackController(RequestTable requestTable) {
        this.requestTable = requestTable;
        this.sendTasksService = new SendTasksService(requestTable);
    }

    @PostMapping("/crack")
    public ResponseEntity<?> crackHash(@RequestBody HashCrackDTO requestDTO) {
        String hash = requestDTO.getHash(); // it will be a key for a request
        try {
            sendTasksService.sendTasks(requestDTO, hash);
        }
        catch (SendTasksService.SendTasksServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body(HashCrackResponseDTO.builder().requestId(hash).build());
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkStatus(@RequestParam @NotNull String requestId) {
        RequestData requestData;
        try {
            requestData = requestTable.checkStatus(requestId);
        }
        catch (RequestTable.RequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        CheckStatusResponseDTO result = CheckStatusResponseDTO.builder()
                                        .status(requestData.getStatus())
                                        .data(requestData.getData())
                                        .build();

        return ResponseEntity.ok().body(result);
    }
}
