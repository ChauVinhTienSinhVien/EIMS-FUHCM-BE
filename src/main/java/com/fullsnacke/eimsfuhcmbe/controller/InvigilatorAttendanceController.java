package com.fullsnacke.eimsfuhcmbe.controller;


import com.fullsnacke.eimsfuhcmbe.dto.mapper.InvigilatorAttendanceMapper;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAttendanceResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAttendance;
import com.fullsnacke.eimsfuhcmbe.service.InvigilatorAttendanceServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/invigilator-attendance")

public class InvigilatorAttendanceController {
    @Autowired
    InvigilatorAttendanceMapper invigilatorAttendanceMapper;

    @Autowired
    private InvigilatorAttendanceServiceImpl invigilatorAttendanceService;

    @GetMapping
    @Operation(summary = "Get all invigilator attendance", description = "Retrieve a list of all invigilator attendance records")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> getAllInvigilatorAttendance() {
        List<InvigilatorAttendance> attendanceList = invigilatorAttendanceService.getInvigilatorAttendances();

        if (attendanceList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<InvigilatorAttendanceResponseDTO> attendanceResponseDTOList = attendanceList.stream()
                    .map(attendance -> invigilatorAttendanceMapper.toResponseDTO(attendance))
                    .toList();
            return ResponseEntity.ok(attendanceResponseDTOList);
        }
    }

    @GetMapping("/today")
    @Operation(summary = "Get all invigilator attendance by day", description = "Retrieve a list of all invigilator attendance records by day")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> getTodayInvigilatorAttendance() {
        Instant day = Instant.now();
        List<InvigilatorAttendance> attendanceList = invigilatorAttendanceService.getInvigilatorAttendancesByDay(day);

        if (attendanceList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<InvigilatorAttendanceResponseDTO> attendanceResponseDTOList = attendanceList.stream()
                    .map(attendance -> invigilatorAttendanceMapper.toResponseDTO(attendance))
                    .toList();
            return ResponseEntity.ok(attendanceResponseDTOList);
        }
    }

    @GetMapping("/day")
    @Operation(summary = "Get all invigilator attendance by day", description = "Retrieve a list of all invigilator attendance records by day")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> getTodayInvigilatorAttendance(@RequestParam("day") Instant day) {
        List<InvigilatorAttendance> attendanceList = invigilatorAttendanceService.getInvigilatorAttendancesByDay(day);
        if (attendanceList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<InvigilatorAttendanceResponseDTO> attendanceResponseDTOList = attendanceList.stream()
                    .map(attendance -> invigilatorAttendanceMapper.toResponseDTO(attendance))
                    .toList();
            return ResponseEntity.ok(attendanceResponseDTOList);
        }
    }

    @PutMapping("checkin/{id}")
    @Operation(summary = "Check in invigilator", description = "Check in an invigilator")
    public ResponseEntity<InvigilatorAttendanceResponseDTO> checkIn(@PathVariable("id") Integer id) {
        InvigilatorAttendance invigilatorAttendance = invigilatorAttendanceService.checkIn(id);
        InvigilatorAttendanceResponseDTO invigilatorAttendanceResponseDTO = invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance);
        return ResponseEntity.ok(invigilatorAttendanceResponseDTO);
    }

    @PutMapping("checkout/{id}")
    @Operation(summary = "Check out invigilator", description = "Check out an invigilator")
    public ResponseEntity<InvigilatorAttendanceResponseDTO> checkOut(@PathVariable("id") Integer id) {
        InvigilatorAttendance invigilatorAttendance = invigilatorAttendanceService.checkOut(id);
        InvigilatorAttendanceResponseDTO invigilatorAttendanceResponseDTO = invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance);
        return ResponseEntity.ok(invigilatorAttendanceResponseDTO);
    }

    @PutMapping("checkin-all/{examSlotId}")
    @Operation(summary = "Check in all invigilators", description = "Check in all invigilators for an exam slot")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> checkInAll(@PathVariable("examSlotId") Integer examSlotId) {

        List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.checkInAll(examSlotId);
        List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                .toList();

        return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
    }

    @PutMapping("checkout-all/{examSlotId}")
    @Operation(summary = "Check out all invigilators", description = "Check out all invigilators for an exam slot")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> checkoutAll(@PathVariable("examSlotId") Integer examSlotId) {

        List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.checkOutAll(examSlotId);
        List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                .toList();

        return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
    }
}
