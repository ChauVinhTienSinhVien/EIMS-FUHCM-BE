package com.fullsnacke.eimsfuhcmbe.controller;


import com.fullsnacke.eimsfuhcmbe.configuration.ConfigurationHolder;
import com.fullsnacke.eimsfuhcmbe.dto.mapper.ExamSlotMapper;
import com.fullsnacke.eimsfuhcmbe.dto.mapper.InvigilatorAttendanceMapper;
import com.fullsnacke.eimsfuhcmbe.dto.mapper.UserMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorAttendanceRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAttendanceListResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAttendanceResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.UserResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAttendance;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.enums.ConfigType;
import com.fullsnacke.eimsfuhcmbe.service.InvigilatorAttendanceServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
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

    @Autowired
    private ConfigurationHolder configurationHolder;

    @Autowired
    private UserMapper userMapper;


    @PostMapping("/staff/add-by-day")
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

    @GetMapping("/staff/exam-slots-by-day")
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

    @GetMapping("/staff/today")
    @Operation(summary = "Get today invigilator attendance", description = "Retrieve a list of all today's invigilator attendance records")
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

    @GetMapping("/staff/day")
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

    @GetMapping("/staff/exam-slot/{id}")
    @Operation(summary = "Get all invigilator attendance by exam slot", description = "Retrieve a list of all invigilator attendance records by exam slot")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> getInvigilatorAttendanceByExamSlot(@PathVariable("id") Integer examSlotId) {
        List<InvigilatorAttendance> attendanceList = invigilatorAttendanceService.getInvigilatorAttendancesByExamSlotId(examSlotId);

        if (attendanceList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<InvigilatorAttendanceResponseDTO> attendanceResponseDTOList = attendanceList.stream()
                    .map(attendance -> invigilatorAttendanceMapper.toResponseDTO(attendance))
                    .toList();
            return ResponseEntity.ok(attendanceResponseDTOList);
        }
    }

    @PutMapping("/staff/checkin/{id}")
    @Operation(summary = "Staff Check in invigilator by InvigilatorAttendanceId", description = "Check in an invigilator by InvigilatorAttendanceId")
    public ResponseEntity<InvigilatorAttendanceResponseDTO> checkIn(@PathVariable("id") Integer id) {
        InvigilatorAttendance invigilatorAttendance = invigilatorAttendanceService.checkIn(id);
        InvigilatorAttendanceResponseDTO invigilatorAttendanceResponseDTO = invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance);
        return ResponseEntity.ok(invigilatorAttendanceResponseDTO);
    }

    @PutMapping("/staff/checkout/{id}")
    @Operation(summary = "Staff Check out invigilator", description = "Check out an invigilator by InvigilatorAttendanceId")
    public ResponseEntity<InvigilatorAttendanceResponseDTO> checkOut(@PathVariable("id") Integer id) {
        InvigilatorAttendance invigilatorAttendance = invigilatorAttendanceService.checkOut(id);
        InvigilatorAttendanceResponseDTO invigilatorAttendanceResponseDTO = invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance);
        return ResponseEntity.ok(invigilatorAttendanceResponseDTO);
    }


    @PutMapping("/staff/checkin-all")
    @Operation(summary = "Staff Check in a list of InvigilatorAttendance", description = "Staff Check in a list of InvigilatorAttendance by list of InvigilatorAttendanceIds")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> checkInAll(@RequestBody List<Integer> invigilatorAttendanceIds) {

        List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.checkInAll(invigilatorAttendanceIds);
        List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                .toList();

        return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
    }

    @PutMapping("/staff/checkout-all")
    @Operation(summary = "Staff Check out a list of InvigilatorAttendance", description = "Check out a list of InvigilatorAttendances by list of InvigilatorAttendanceIds")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> checkOutAll(@RequestBody List<Integer> invigilatorAttendanceIds) {

        List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.checkOutAll(invigilatorAttendanceIds);
        List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                .toList();

        return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
    }


    @PutMapping("/staff/checkin-all/{examSlotId}")
    @Operation(summary = "Staff check in all invigilators by examSlotId", description = "Check in all invigilators for an exam slot")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> checkInAllByExamSlot(@PathVariable("examSlotId") Integer examSlotId) {

        List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.checkInByExamSlotId(examSlotId);
        List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                .toList();

        return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
    }

    @PutMapping("/staff/checkout-all/{examSlotId}")
    @Operation(summary = "Staff check out all invigilators by examSlotId", description = "Check out all invigilators for an exam slot")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> checkoutAllByExamSlot(@PathVariable("examSlotId") Integer examSlotId) {

        List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.checkOutByExamSlotId(examSlotId);
        List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                .toList();

        return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
    }

    @GetMapping("/manager/exam-slots-by-day")
    @Operation(summary = "Manager get all of attendance examSlot", description = "Retrieve a list of all attendance examSlot")
    public ResponseEntity<List<ExamSlotResponseDTO>> managerGetAttendanceExamSlotsByDay(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date day) {
        LocalDate localDate = day.toInstant().atZone(ZoneOffset.systemDefault()).toLocalDate();
        Instant dayInstant = localDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        List<ExamSlot> examSlotList = invigilatorAttendanceService.getExamSlotsByDay(dayInstant);
        List<ExamSlotResponseDTO> examSlotResponseDTOList = examSlotList.stream()
                .map(examSlot -> examSlotMapper.toDto(examSlot))
                .toList();
        return ResponseEntity.ok(examSlotResponseDTOList);
    }

    @GetMapping("/manager/exam-slot-by-semester/{semesterId}")
    @Operation(summary = "Manager get all exam slots by semesterId", description = "Retrieve a list of all exam slots by semesterId")
    public ResponseEntity<List<ExamSlotResponseDTO>> managerGetExamSlotsBySemesterId(@PathVariable Integer semesterId) {
        List<ExamSlot> examSlotList = invigilatorAttendanceService.getExamSlotsBySemester(semesterId);
        List<ExamSlotResponseDTO> examSlotResponseDTOList = examSlotList.stream()
                .map(examSlot -> examSlotMapper.toDto(examSlot))
                .toList();
        return ResponseEntity.ok(examSlotResponseDTOList);
    }

    @GetMapping("/manager/exam-slot/{id}")
    @Operation(summary = "Manager get all invigilator attendance by exam slot", description = "Retrieve a list of all invigilator attendance records by exam slot")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> getInvigilatorCheckedAttendanceByExamSlot(@PathVariable("id") Integer examSlotId) {
        List<InvigilatorAttendance> attendanceList = invigilatorAttendanceService.getInvigilatorAttendancesByExamSlotId(examSlotId);

        if (attendanceList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<InvigilatorAttendanceResponseDTO> attendanceResponseDTOList = attendanceList.stream()
                    .map(attendance -> invigilatorAttendanceMapper.toResponseDTO(attendance))
                    .toList();
            return ResponseEntity.ok(attendanceResponseDTOList);
        }
    }

    @PutMapping("manager/approve/{examSlotId}")
    @Operation(summary = "Manager approve invigilator attendance", description = "Manager approve invigilator attendance for an exam slot")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> managerApprove(@PathVariable("examSlotId") Integer examSlotId) {

        List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.managerApproveByExamSlotId(examSlotId);
        List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                .toList();

        return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
    }

    @PutMapping("manager/reject/{examSlotId}")
    @Operation(summary = "Manager reject invigilator attendance", description = "Manager reject invigilator attendance for an exam slot")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> managerReject(@PathVariable("examSlotId") Integer examSlotId) {

        List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.managerRejectByExamSlotId(examSlotId);
        List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                .toList();

        return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
    }

    @PutMapping("manager/update/{id}")
    @Operation(summary = "Manager update invigilator attendance", description = "Manager update invigilator attendance")
    public ResponseEntity<InvigilatorAttendanceResponseDTO> managerUpdate(@PathVariable("id") Integer id, @RequestBody InvigilatorAttendanceRequestDTO invigilatorAttendanceRequestDTO) {
        boolean isCheckIn = invigilatorAttendanceRequestDTO.isCheckIn();
        boolean isCheckOut = invigilatorAttendanceRequestDTO.isCheckOut();
        InvigilatorAttendance invigilatorAttendance = invigilatorAttendanceService.managerUpdate(id, isCheckIn, isCheckOut);
        InvigilatorAttendanceResponseDTO invigilatorAttendanceResponseDTO = invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance);
        return ResponseEntity.ok(invigilatorAttendanceResponseDTO);
    }

    @PutMapping("manager/approve-all")
    @Operation(summary = "Manager approve a list of invigilator attendance", description = "Manager approve a list of invigilator attendance")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> managerApproveAll(@RequestBody List<Integer> invigilatorAttendanceIds) {

        List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.managerApproveAll(invigilatorAttendanceIds);
        List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                .toList();

        return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
    }

    @PutMapping("manager/reject-all")
    @Operation(summary = "Manager approve a list of invigilator attendance", description = "Manager approve a list of invigilator attendance")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> managerRejectAll(@RequestBody List<Integer> invigilatorAttendanceIds) {

        List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.managerRejectAll(invigilatorAttendanceIds);
        List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                .toList();

        return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
    }

    @GetMapping("manager/report/invigilator/{semesterId}")
    @Operation(summary = "Get all checked invigilator attendance by semesterId", description = "Retrieve a list of all checked invigilator attendance records by SemesterId")
    public ResponseEntity<List<InvigilatorAttendanceListResponseDTO>> getApprovedInvigilatorAttendanceBySemesterId(@PathVariable Integer semesterId) {
        List<User> invigilatorList = invigilatorAttendanceService.getInvigilatorBySemesterId(semesterId);
        List<InvigilatorAttendanceListResponseDTO> invigilatorAttendanceListResponseDTOList = new ArrayList<>();
        for (User invigilator : invigilatorList) {
            List<InvigilatorAttendance> attendanceList = invigilatorAttendanceService.getUserInvigilatorAttendanceBySemesterIdAndApproved(invigilator.getId(), semesterId);
            double hourlyRate = Double.parseDouble(configurationHolder.getConfig(ConfigType.HOURLY_RATE.getValue()));
            System.out.println("hourlyRate 1: " + hourlyRate);

            if (attendanceList.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                List<InvigilatorAttendanceResponseDTO> attendanceResponseDTOList = attendanceList.stream()
                        .map(attendance -> invigilatorAttendanceMapper.toResponseDTO(attendance))
                        .toList();
                InvigilatorAttendanceListResponseDTO invigilatorAttendanceListResponseDTO = InvigilatorAttendanceListResponseDTO.builder()
                        .invigilatorAttendanceList(attendanceResponseDTOList)
                        .hourlyRate(hourlyRate)
                        .build();
                invigilatorAttendanceListResponseDTO.setTotalHours();
                invigilatorAttendanceListResponseDTO.setPreCalculatedInvigilatorFree();
                invigilatorAttendanceListResponseDTO.setFuId(invigilator.getFuId());
                invigilatorAttendanceListResponseDTO.setFirstName(invigilator.getFirstName());
                invigilatorAttendanceListResponseDTO.setLastName(invigilator.getLastName());
                invigilatorAttendanceListResponseDTO.setEmail(invigilator.getEmail());
                invigilatorAttendanceListResponseDTO.setPhoneNum(invigilator.getPhoneNumber());
                invigilatorAttendanceListResponseDTO.setId(invigilator.getId());
                invigilatorAttendanceListResponseDTOList.add(invigilatorAttendanceListResponseDTO);
            }
        }
        return ResponseEntity.ok(invigilatorAttendanceListResponseDTOList);
    }

    @GetMapping("/invigilator/report/{semesterId}")
    @Operation(summary = "Get all checked invigilator attendance by semesterId", description = "Retrieve a list of all checked invigilator attendance records of the current invigilator by SemesterId")
    public ResponseEntity<InvigilatorAttendanceListResponseDTO> getApprovedInvigilatorAttendanceBSemesterId(@PathVariable Integer semesterId) {
        List<InvigilatorAttendance> attendanceList = invigilatorAttendanceService.getCurrentUserInvigilatorAttendanceBySemesterIdAndApproved(semesterId);
        double hourlyRate = Double.parseDouble(configurationHolder.getConfig(ConfigType.HOURLY_RATE.getValue()));
        System.out.println("hourlyRate: " + hourlyRate);

        if (attendanceList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<InvigilatorAttendanceResponseDTO> attendanceResponseDTOList = attendanceList.stream()
                    .map(attendance -> invigilatorAttendanceMapper.toResponseDTO(attendance))
                    .toList();
            InvigilatorAttendanceListResponseDTO invigilatorAttendanceListResponseDTO = InvigilatorAttendanceListResponseDTO.builder()
                    .invigilatorAttendanceList(attendanceResponseDTOList)
                    .hourlyRate(hourlyRate)
                    .build();
            invigilatorAttendanceListResponseDTO.setTotalHours();
            invigilatorAttendanceListResponseDTO.setPreCalculatedInvigilatorFree();
            return ResponseEntity.ok(invigilatorAttendanceListResponseDTO);
        }
    }

    @GetMapping("/invigilator/{semesterId}")
    @Operation(summary = "Get invigilator attendance by semesterId", description = "Retrieve a list of invigilator attendances by semesterId")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> getInvigilatorAttendanceBySemesterId( @PathVariable Integer semesterId) {

        List<InvigilatorAttendance> attendanceList = invigilatorAttendanceService.getCurrentUserInvigilatorAttendanceBySemesterId(semesterId);

        if (attendanceList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<InvigilatorAttendanceResponseDTO> attendanceResponseDTOList = attendanceList.stream()
                    .map(attendance -> invigilatorAttendanceMapper.toResponseDTO(attendance))
                    .toList();
            return ResponseEntity.ok(attendanceResponseDTOList);
        }
    }


    @GetMapping("/invigilator/today")
    @Operation(summary = "Get today invigilator attendance", description = "Retrieve a list of all today's invigilator attendance records of the current invigilator")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> getCurrentUserTodayInvigilatorAttendance() {
        Instant day = Instant.now();
        List<InvigilatorAttendance> attendanceList = invigilatorAttendanceService.getCurrentUserInvigilatorAttendanceByDay(day);

        if (attendanceList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<InvigilatorAttendanceResponseDTO> attendanceResponseDTOList = attendanceList.stream()
                    .map(attendance -> invigilatorAttendanceMapper.toResponseDTO(attendance))
                    .toList();
            return ResponseEntity.ok(attendanceResponseDTOList);
        }
    }

    @GetMapping("/invigilator/day")
    @Operation(summary = "Get all invigilator attendance by day", description = "Retrieve a list of all invigilator attendance records by day of the current invigilator")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> getCurrentUserInvigilatorAttendanceByDay(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date day) {

        LocalDate localDate = day.toInstant().atZone(ZoneOffset.systemDefault()).toLocalDate();
        Instant dayInstant = localDate.atStartOfDay(ZoneOffset.UTC).toInstant();

        List<InvigilatorAttendance> attendanceList = invigilatorAttendanceService.getCurrentUserInvigilatorAttendanceByDay(dayInstant);

        if (attendanceList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<InvigilatorAttendanceResponseDTO> attendanceResponseDTOList = attendanceList.stream()
                    .map(attendance -> invigilatorAttendanceMapper.toResponseDTO(attendance))
                    .toList();
            return ResponseEntity.ok(attendanceResponseDTOList);
        }
    }

}
