package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.RequestRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RequestResponseDTO;
import com.fullsnacke.eimsfuhcmbe.service.RequestService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RequestController {
    RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestResponseDTO> createRequest(@RequestBody RequestRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(requestService.createRequest(request));
    }

    @GetMapping("/myinfo")
    public ResponseEntity<List<RequestResponseDTO>> getAllRequest() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requestService.getAllRequestOfCurrentInvigilator());
    }

    @GetMapping("myinfo/requestid={requestId}")
    public ResponseEntity<RequestResponseDTO> getRequestById(@PathVariable int requestId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requestService.getRequestById(requestId));
    }


}
