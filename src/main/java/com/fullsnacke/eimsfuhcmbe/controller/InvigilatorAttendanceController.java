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


        //Staff
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

        //Manager
        //Staff
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

        //Staff
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

        //Staff
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

        //Staff
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

        @GetMapping("/manager/by-status/{status}")
        @Operation(summary = "Get all invigilator attendance by status", description = "Retrieve a list of all invigilator attendance records by status")
        public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> getInvigilatorAttendanceByStatus(@PathVariable("status") int status) {
            List<InvigilatorAttendance> attendanceList = invigilatorAttendanceService.getInvigilatorAttendancesByStatus(status);

            if (attendanceList.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                List<InvigilatorAttendanceResponseDTO> attendanceResponseDTOList = attendanceList.stream()
                        .map(attendance -> invigilatorAttendanceMapper.toResponseDTO(attendance))
                        .toList();
                return ResponseEntity.ok(attendanceResponseDTOList);
            }
        }

        //Staff
        @PutMapping("/staff/checkin/{id}")
        @Operation(summary = "Staff Check in invigilator by InvigilatorAttendanceId", description = "Check in an invigilator by InvigilatorAttendanceId")
        public ResponseEntity<InvigilatorAttendanceResponseDTO> checkIn(@PathVariable("id") Integer id) {
            InvigilatorAttendance invigilatorAttendance = invigilatorAttendanceService.checkIn(id);
            InvigilatorAttendanceResponseDTO invigilatorAttendanceResponseDTO = invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance);
            return ResponseEntity.ok(invigilatorAttendanceResponseDTO);
        }

        //Staff
        @PutMapping("/staff/checkout/{id}")
        @Operation(summary = "Staff Check out invigilator", description = "Check out an invigilator by InvigilatorAttendanceId")
        public ResponseEntity<InvigilatorAttendanceResponseDTO> checkOut(@PathVariable("id") Integer id) {
            InvigilatorAttendance invigilatorAttendance = invigilatorAttendanceService.checkOut(id);
            InvigilatorAttendanceResponseDTO invigilatorAttendanceResponseDTO = invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance);
            return ResponseEntity.ok(invigilatorAttendanceResponseDTO);
        }

        //Staff
        @PutMapping("/staff/checkin-all")
        @Operation(summary = "Staff Check in a list of InvigilatorAttendance", description = "Staff Check in a list of InvigilatorAttendance by list of InvigilatorAttendanceIds")
        public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> checkInAll(@RequestBody List<Integer> invigilatorAttendanceIds) {

            List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.checkInAll(invigilatorAttendanceIds);
            List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                    .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                    .toList();

            return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
        }

        //Staff
        @PutMapping("/staff/checkout-all")
        @Operation(summary = "Staff Check out a list of InvigilatorAttendance", description = "Check out a list of InvigilatorAttendances by list of InvigilatorAttendanceIds")
        public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> checkOutAll(@RequestBody List<Integer> invigilatorAttendanceIds) {

            List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.checkOutAll(invigilatorAttendanceIds);
            List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                    .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                    .toList();

            return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
        }

        //Staff
        @PutMapping("/staff/checkin-all/{examSlotId}")
        @Operation(summary = "Staff check in all invigilators by examSlotId", description = "Check in all invigilators for an exam slot")
        public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> checkInAllByExamSlot(@PathVariable("examSlotId") Integer examSlotId) {

            List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.checkInByExamSlotId(examSlotId);
            List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                    .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                    .toList();

            return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
        }

        //Staff
        @PutMapping("/staff/checkout-all/{examSlotId}")
        @Operation(summary = "Staff check out all invigilators by examSlotId", description = "Check out all invigilators for an exam slot")
        public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> checkoutAllByExamSlot(@PathVariable("examSlotId") Integer examSlotId) {

            List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.checkOutByExamSlotId(examSlotId);
            List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                    .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                    .toList();

            return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
        }

        //Manager
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

        //Manager
        @GetMapping("/manager/exam-slot-by-semester/{semesterId}")
        @Operation(summary = "Manager get all exam slots by semesterId", description = "Retrieve a list of all exam slots by semesterId")
        public ResponseEntity<List<ExamSlotResponseDTO>> managerGetExamSlotsBySemesterId(@PathVariable Integer semesterId) {
            List<ExamSlot> examSlotList = invigilatorAttendanceService.getExamSlotsBySemester(semesterId);
            List<ExamSlotResponseDTO> examSlotResponseDTOList = examSlotList.stream()
                    .map(examSlot -> examSlotMapper.toDto(examSlot))
                    .toList();
            return ResponseEntity.ok(examSlotResponseDTOList);
        }

        //Manager
        @GetMapping("/manager/exam-slot/{examSlotId}")
        @Operation(summary = "Manager get all invigilator attendance by exam slot", description = "Retrieve a list of all invigilator attendance records by exam slot")
        public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> getInvigilatorCheckedAttendanceByExamSlot(@PathVariable("examSlotId") Integer examSlotId) {
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

        //Manager
        @PutMapping("manager/approve/{examSlotId}")
        @Operation(summary = "Manager approve invigilator attendance", description = "Manager approve invigilator attendance for an exam slot")
        public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> managerApprove(@PathVariable("examSlotId") Integer examSlotId) {

            List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.managerApproveByExamSlotId(examSlotId);
            List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                    .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                    .toList();

            return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
        }

        //Manager
        @PutMapping("manager/reject/{examSlotId}")
        @Operation(summary = "Manager reject invigilator attendance", description = "Manager reject invigilator attendance for an exam slot")
        public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> managerReject(@PathVariable("examSlotId") Integer examSlotId) {

            List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.managerRejectByExamSlotId(examSlotId);
            List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceResponseDTOList = invigilatorAttendanceList.stream()
                    .map(invigilatorAttendance -> invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance))
                    .toList();

            return ResponseEntity.ok(invigilatorAttendanceResponseDTOList);
        }

        //Manager
        @PutMapping("manager/update/{attendanceId}")
        @Operation(summary = "Manager update invigilator attendance", description = "Manager update invigilator attendance")
        public ResponseEntity<InvigilatorAttendanceResponseDTO> managerUpdate(@PathVariable("attendanceId") Integer id, @RequestBody InvigilatorAttendanceRequestDTO invigilatorAttendanceRequestDTO) {
            boolean isCheckIn = invigilatorAttendanceRequestDTO.isCheckIn();
            boolean isCheckOut = invigilatorAttendanceRequestDTO.isCheckOut();
            InvigilatorAttendance invigilatorAttendance = invigilatorAttendanceService.managerUpdate(id, isCheckIn, isCheckOut);
            InvigilatorAttendanceResponseDTO invigilatorAttendanceResponseDTO = invigilatorAttendanceMapper.toResponseDTO(invigilatorAttendance);
            return ResponseEntity.ok(invigilatorAttendanceResponseDTO);
        }

        //Manager
        @GetMapping("manager/report/invigilator/{semesterId}")
        @Operation(summary = "Get all checked invigilator attendance by semesterId", description = "Retrieve a list of all checked invigilator attendance records by SemesterId")
        public ResponseEntity<List<InvigilatorAttendanceListResponseDTO>> getApprovedInvigilatorAttendanceBySemesterId(@PathVariable Integer semesterId) {
            List<User> invigilatorList = invigilatorAttendanceService.getInvigilatorBySemesterId(semesterId);
            for (User invigilator : invigilatorList) {
                System.out.println("invigilator: " + invigilator.getFirstName());
            }
            List<InvigilatorAttendanceListResponseDTO> invigilatorAttendanceListResponseDTOList = new ArrayList<>();
            for (User invigilator : invigilatorList) {
                List<InvigilatorAttendance> attendanceList = invigilatorAttendanceService.getUserInvigilatorAttendanceBySemesterIdAndApproved(invigilator.getId(), semesterId);
                double hourlyRate = Double.parseDouble(configurationHolder.getConfig(ConfigType.HOURLY_RATE.getValue()));
                System.out.println("hourlyRate 1: " + hourlyRate);

                if (!attendanceList.isEmpty()) {
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

        //Invigilator
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

        //Invigilator
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

}
