package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.RequestRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.UpdateStatusRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RequestResponseDTO;
import com.fullsnacke.eimsfuhcmbe.service.RequestService;
import io.swagger.v3.oas.annotations.Operation;
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

    //Đã được xài trong send Request của role invigilator
    @PostMapping
    public ResponseEntity<RequestResponseDTO> createRequest(@RequestBody RequestRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(requestService.createRequest(request));
    }

    //Đã được xài trong view Request của role invigilator
    @GetMapping("/myinfo")
    public ResponseEntity<List<RequestResponseDTO>> getAllRequest() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requestService.getAllRequestOfCurrentInvigilator());
    }

    //Đang ko xài
    @GetMapping("requestid={requestId}")
    @Operation(summary = "Get request detail by request id")
    public ResponseEntity<RequestResponseDTO> getRequestById(@PathVariable int requestId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requestService.getRequestById(requestId));
    }


    @GetMapping("invigilatorid={invigilatorId}")
    @Operation(summary = "Get all requests by invigilator id")
    public ResponseEntity<List<RequestResponseDTO>> getAllRequestByInvigilatorId(@PathVariable String invigilatorId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requestService.getAllRequestByInvigilatorId(invigilatorId));
    }

    @PostMapping("requestid={requestId}")
    @Operation(summary = "Update request status", description = "Update request status by request id")
    public ResponseEntity<RequestResponseDTO> updateRequestStatus(@PathVariable int requestId, @RequestBody UpdateStatusRequestDTO status) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requestService.updateRequestStatus(requestId, status));
    }

}
