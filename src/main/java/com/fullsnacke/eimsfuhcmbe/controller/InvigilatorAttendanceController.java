package com.fullsnacke.eimsfuhcmbe.controller;


import com.fullsnacke.eimsfuhcmbe.dto.mapper.ExamSlotMapper;
import com.fullsnacke.eimsfuhcmbe.dto.mapper.InvigilatorAttendanceMapper;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAttendanceResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAttendance;
import com.fullsnacke.eimsfuhcmbe.service.InvigilatorAttendanceServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.internal.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/invigilator-attendance")

public class InvigilatorAttendanceController {
    @Autowired
    InvigilatorAttendanceMapper invigilatorAttendanceMapper;

    @Autowired
    ExamSlotMapper examSlotMapper;

    @Autowired
    private InvigilatorAttendanceServiceImpl invigilatorAttendanceService;

    @PostMapping("/add-by-day")
    @Operation(summary = "Add all invigilator attendance by day", description = "Add all invigilator attendance records by day")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> addInvigilatorAttendanceByDay(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date day) {
        LocalDate localDate = day.toInstant().atZone(ZoneOffset.systemDefault()).toLocalDate();
        Instant dayInstant = localDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.addInvigilatorAttendancesByDay(dayInstant);
        List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                .toList();
        return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
    }

    @GetMapping("/exam-slots-by-day")
    @Operation(summary = "Get all exam slots by day", description = "Retrieve a list of all exam slots by day")
    public ResponseEntity<List<ExamSlotResponseDTO>> getExamSlotsByDay(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date day) {
        LocalDate localDate = day.toInstant().atZone(ZoneOffset.systemDefault()).toLocalDate();
        Instant dayInstant = localDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        List<ExamSlot> examSlotList = invigilatorAttendanceService.getExamSlotsByDay(dayInstant);
        List<ExamSlotResponseDTO> examSlotResponseDTOList = examSlotList.stream()
                .map(examSlot -> examSlotMapper.toDto(examSlot))
                .toList();
        return ResponseEntity.ok(examSlotResponseDTOList);
    }

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
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> getTodayInvigilatorAttendance(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date day) {

        LocalDate localDate = day.toInstant().atZone(ZoneOffset.systemDefault()).toLocalDate();
        Instant dayInstant = localDate.atStartOfDay(ZoneOffset.UTC).toInstant();

        List<InvigilatorAttendance> attendanceList = invigilatorAttendanceService.getInvigilatorAttendancesByDay(dayInstant);
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


    @PutMapping("checkin-all")
    @Operation(summary = "Check in all invigilators", description = "Check in all invigilators for an exam slot")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> checkInAll(@RequestBody List<Integer> invigilatorAttendanceIds) {

        List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.checkInAll(invigilatorAttendanceIds);
        List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                .toList();

        return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
    }

    @PutMapping("checkout-all")
    @Operation(summary = "Check out all invigilators", description = "Check out all invigilators for an exam slot")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> checkOutAll(@RequestBody List<Integer> invigilatorAttendanceIds) {

        List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.checkOutAll(invigilatorAttendanceIds);
        List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                .toList();

        return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
    }


    @PutMapping("checkin-all/{examSlotId}")
    @Operation(summary = "Check in all invigilators", description = "Check in all invigilators for an exam slot")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> checkInAllByExamSlot(@PathVariable("examSlotId") Integer examSlotId) {

        List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.checkInByExamSlotId(examSlotId);
        List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                .toList();

        return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
    }

    @PutMapping("checkout-all/{examSlotId}")
    @Operation(summary = "Check out all invigilators", description = "Check out all invigilators for an exam slot")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> checkoutAllByExamSlot(@PathVariable("examSlotId") Integer examSlotId) {

        List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.checkOutByExamSlotId(examSlotId);
        List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                .toList();

        return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
    }
}
