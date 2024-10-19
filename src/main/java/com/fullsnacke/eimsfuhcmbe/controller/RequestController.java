package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExchangeInvigilatorsRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.RequestRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.UpdateStatusRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ManagerRequestResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RequestResponseDTO;
import com.fullsnacke.eimsfuhcmbe.service.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RequestController {
    RequestService requestService;

    //Đã được xài trong send Request của role invigilator
    //INVIGILATOR
    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody RequestRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(requestService.createRequest(request));
    }

    //Đã được xài trong view Request của role invigilator
    //INVIGILATOR
    @GetMapping("/myinfo")
    public ResponseEntity<?> getAllRequest() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requestService.getAllRequestOfCurrentInvigilator());
    }

    //Đang ko xài
    @GetMapping("requestid={requestId}")
    @Operation(summary = "Get request detail by request id")
    public ResponseEntity<?> getRequestById(@PathVariable int requestId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requestService.getRequestById(requestId));
    }


    //MANAGER
    @GetMapping("invigilatorid={invigilatorId}")
    @Operation(summary = "Get all requests by invigilator id")
    public ResponseEntity<?> getAllRequestByInvigilatorId(@PathVariable String invigilatorId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requestService.getAllRequestByInvigilatorId(invigilatorId));
    }

    //MANAGER
    @GetMapping("semesterid={semesterId}")
    @Operation(summary = "Get all requests")
    public ResponseEntity<?> getAllRequestBySemester(@PathVariable("semesterId") int semesterId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requestService.getAllRequestBySemester(semesterId));
    }
    //MANAGER
    @PutMapping
    @Operation(summary = "Update request status", description = "Update request status by request id")
    public ResponseEntity<?> updateRequestStatus(@RequestBody ExchangeInvigilatorsRequestDTO request) {
        log.info("Request ID: {}", request.getRequestId());
        log.info("Request Status: {}", request.getStatus());
        log.info("Request newInvigilatorFuId: {}", request.getNewInvigilatorFuId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requestService.updateRequestStatus(request));
    }

}
