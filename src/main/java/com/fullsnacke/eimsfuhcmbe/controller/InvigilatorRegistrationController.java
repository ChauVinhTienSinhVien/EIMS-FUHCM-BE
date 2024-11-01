package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorRegistrationRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.RegisterdSlotWithSemesterAndInvigilatorRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.*;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorRegistration;
import com.fullsnacke.eimsfuhcmbe.exception.repository.assignment.CustomMessageException;
import com.fullsnacke.eimsfuhcmbe.service.InvigilatorAssignmentService;
import com.fullsnacke.eimsfuhcmbe.service.InvigilatorRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/invigilators")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class InvigilatorRegistrationController {

    InvigilatorRegistrationService invigilatorRegistrationService;
    InvigilatorAssignmentService invigilatorAssignmentService;

    //INVIGILATOR
    @PostMapping
    @Operation(summary = "Register Exam Slots", description = "Save the invigilator customEx for the exam slot")
    public ResponseEntity<InvigilatorRegistrationResponseDTO> registerExamSlotWithoutFuId(@RequestBody InvigilatorRegistrationRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invigilatorRegistrationService.registerExamSlotWithoutFuId(request));
    }

    //MANAGER
    @PostMapping("/register")
    @Operation(summary = "Register Exam Slots", description = "Save the invigilator customEx for the exam slot")
    public ResponseEntity<InvigilatorRegistrationResponseDTO> registerExamSlotWithFuId(@RequestBody InvigilatorRegistrationRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invigilatorRegistrationService.registerExamSlotWithFuId(request));
    }

    //INVIGILATOR
    @GetMapping("/myinfo")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots for the current invigilator")
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllRegisteredSlot() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.getAllCurrentInvigilatorRegisteredSlots());
    }

    //MANAGER/STAFF
    @GetMapping("/{fuId}")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots by fuID")
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllRegisteredSlot(@PathVariable("fuId") String fuId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.getAllRegisteredSlotsByInvigilator(fuId));
    }

    //MANAGER/STAFF
    @GetMapping("/semesterid={semesterId}/invigilator={fuId}")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots by semester and fuId")
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllRegisteredSlotsInSemesterByInvigilator(@PathVariable("semesterId") int semesterId, @PathVariable("fuId") String fuId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.getAllRegisteredSlotsInSemesterByInvigilator(semesterId, fuId));
    }

    //INVIGILATOR
    @GetMapping("/myinfo/semesterid={semesterId}")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots by semester of current invigilator")
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllCurrentInvigilatorRegisteredSlotsInSemester(@PathVariable("semesterId") int semesterId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.getAllCurrentInvigilatorRegisteredSlotsInSemester(semesterId));
    }

    //MANAGER/STAFF
    @GetMapping("/semesterid={semesterId}")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots by semester of all invigilators")
    public ResponseEntity<Set<RegisteredExamBySemesterResponseDTO>> getAllRegisteredSlotsInSemester(@PathVariable("semesterId") @RequestBody int semesterId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.getRegisteredExamBySemester(semesterId));
    }

    //MANAGER/STAFF
    @GetMapping("/examslotid={examSlotId}")
    @Operation(summary = "Get All Invigilators", description = "Get all the invigilators by exam slot")
    public ResponseEntity<ListInvigilatorsByExamSlotResponseDTO> listInvigilatorsByExamSlot(@PathVariable("examSlotId") int examSlotId) {
        System.out.println("examSlotId = " + examSlotId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.listInvigilatorsByExamSlot(examSlotId));
    }

    //INVIGILATOR
    @PutMapping
    @Operation(summary = "Update Registered Slot", description = "Update the registered slot by fuId")
    public ResponseEntity<InvigilatorRegistrationResponseDTO> updateRegisteredSlot(@RequestBody InvigilatorRegistrationRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.updateRegisterExamSlot(request));
    }

    //TOI DUNG DE TEST
    @DeleteMapping
    @Operation(summary = "Delete Registered Slot", description = "Delete the registered slot by fuId and semester")
    public ResponseEntity<Boolean> deleteRegisteredSlotsBySemester(@RequestBody RegisterdSlotWithSemesterAndInvigilatorRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.deleteRegisteredSlotsBySemester(request));
    }
//
//    @DeleteMapping("/register")
//    @Operation(summary = "Delete Registered Slot by ExamSlot Id")
//    public ResponseEntity<Set<ExamSlotDetail>> deleteRegisteredSlotByExamSlotId(@RequestBody InvigilatorRegistrationRequestDTO request) {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(invigilatorRegistrationService.deleteRegisteredSlotByExamSlotId(request));
//    }

    //INVIGILATOR
    @DeleteMapping("/myinfo/register")
    @Operation(summary = "Delete Registered Slot by ExamSlot Id")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteCurrentInvigilatorRegisteredSlotByExamSlotId(@RequestParam Set<Integer> id) {
        Logger log = LogManager.getLogger(InvigilatorRegistrationController.class);
        log.info("Received a request to delete the registered slot with ID: {}", id);

        try {
            Set<ExamSlotDetail> result = invigilatorRegistrationService.deleteCurrentInvigilatorRegisteredSlotByExamSlotId(id);
            log.info("Already delete {} slot(s)", result.size());
            return ResponseEntity.ok(result);
        } catch (CustomMessageException e) {
            log.error("Error occurred while deleting a registered slot: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //INVIGILATOR
    @GetMapping("/register/semesterid={semesterId}")
    public ResponseEntity<RegisteredExamBySemesterResponseDTO> getAllExamSlotsInSemesterWithStatus(@PathVariable("semesterId") int semesterId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.getAllExamSlotsInSemesterWithStatus(semesterId));
    }

    @GetMapping("cancel")
    public ResponseEntity<Set<ExamSlotDetail>> getListCancelInSemester(@RequestParam int semesterId) {
        return ResponseEntity.ok(invigilatorRegistrationService.getCancellableExamSlots(semesterId));
    }

    //DASHBOARD
    @GetMapping("/dashboard/time-in-range")
    @Operation(summary = "Retrieve invigilator registrations within a time range", description = "Fetches a list of invigilator registrations within the specified start and end times.")
    public ResponseEntity<List<InvigilatorRegistration>> getInvigilatorRegistrationsInTimeRange(
            @RequestParam("startTime") ZonedDateTime startTime,
            @RequestParam("endTime") ZonedDateTime endTime) {

        List<InvigilatorRegistration> registrations = invigilatorRegistrationService.getAllRegistrationsInTimeRange(startTime.toInstant(), endTime.toInstant());

        if (registrations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(registrations);
    }


}
