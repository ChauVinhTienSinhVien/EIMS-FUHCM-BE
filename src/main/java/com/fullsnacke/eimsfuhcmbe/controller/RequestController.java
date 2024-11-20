package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.RequestMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.ExchangeInvigilatorsRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.RequestRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.UpdateStatusRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ManagerRequestResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RequestResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RequestTypeResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Request;
import com.fullsnacke.eimsfuhcmbe.enums.RequestTypeEnum;
import com.fullsnacke.eimsfuhcmbe.service.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RequestController {
    RequestService requestService;
    RequestMapper requestMapper;

    //INVIGILATOR
    @PostMapping
    @PreAuthorize("hasAuthority('request:create')")
    @Operation(summary = "Create request", description = "Create request for invigilator")
    public ResponseEntity<?> createRequest(@RequestBody RequestRequestDTO request) {
        String requestType = request.getRequestType().toLowerCase();
        ResponseEntity<?> responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request type");

        if(requestType.equalsIgnoreCase(RequestTypeEnum.UPDATE_ATTENDANCE.name())) {
            Request updatedRequest = requestMapper.toEntity(request);
            RequestResponseDTO responseDTO = requestMapper.toResponseDTO(requestService.createAttendanceUpdateRequest(updatedRequest));
            responseEntity = ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(responseDTO);
        }else if(requestType.equalsIgnoreCase(RequestTypeEnum.CANCEL.name())) {
            RequestResponseDTO responseDTO = requestService.createRequest(request);
            responseEntity = ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(responseDTO);
        }
        return responseEntity;
    }

    //INVIGILATOR
    @GetMapping("/myinfo")
    @PreAuthorize("hasAuthority('request:read')")
    @Operation(summary = "Get all requests of current invigilator")
    public ResponseEntity<?> getAllRequest() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requestService.getAllRequestOfCurrentInvigilator());
    }

    //INVIGILATOR / MANAGER
    @GetMapping("/request-types")
    @PreAuthorize("hasAuthority('request:read')")
    @Operation(summary = "Get all request types")
    public ResponseEntity<RequestTypeResponseDTO> getAllRequestTypes() {
        List<String> requestTypes = new ArrayList<>();
        Arrays.stream(RequestTypeEnum.values()).forEach(requestTypeEnum -> requestTypes.add(requestTypeEnum.name()));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(RequestTypeResponseDTO.builder().requestTypes(requestTypes).build());
    }

    //MANAGER
    @GetMapping("requestid={requestId}")
    @PreAuthorize("hasAuthority('request:read')")
    @Operation(summary = "Get request detail by request id")
    public ResponseEntity<?> getRequestById(@PathVariable int requestId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requestService.getRequestById(requestId));
    }

    //MANAGER
    @GetMapping("invigilatorid={invigilatorId}")
    @PreAuthorize("hasAuthority('request:read')")
    @Operation(summary = "Get all requests by invigilator id")
    public ResponseEntity<?> getAllRequestByInvigilatorId(@PathVariable String invigilatorId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requestService.getAllRequestByInvigilatorId(invigilatorId));
    }

    //MANAGER
    @GetMapping("semesterid={semesterId}")
    @PreAuthorize("hasAuthority('request:read')")
    @Operation(summary = "Get all requests by semester id for manager view request page")
    public ResponseEntity<?> getAllRequestBySemester(@PathVariable("semesterId") int semesterId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requestService.getAllRequestBySemester(semesterId));
    }

    //MANAGER
    @PutMapping
    @PreAuthorize("hasAuthority('request:write')")
    @Operation(summary = "Update request status", description = "Update request status by request id")
    public ResponseEntity<?> updateRequestStatus(@RequestBody ExchangeInvigilatorsRequestDTO request) {
        log.info("Request ID: {}", request.getRequestId());
        log.info("Request Status: {}", request.getStatus());
        log.info("Request newInvigilatorFuId: {}", request.getNewInvigilatorFuId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requestService.updateRequestStatus(request));
    }

    //MANAGER
    @PutMapping("/update-attendance/status")
    @PreAuthorize("hasAuthority('request:write')")
    public ResponseEntity<?> updateAttendanceStatus(@RequestBody UpdateStatusRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requestService.updateAttendanceStatus(request));
    }
}
