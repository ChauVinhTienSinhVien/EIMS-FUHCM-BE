package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorRegistrationRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotDetail;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorRegistrationResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RegisteredExamBySemesterResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RegisteredExamInvigilationResponseDTO;
import com.fullsnacke.eimsfuhcmbe.exception.repository.assignment.CustomMessageException;
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

import java.util.Set;

@RestController
@RequestMapping("/invigilators")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class InvigilatorRegistrationController {

    InvigilatorRegistrationService invigilatorRegistrationService;

    //INVIGILATOR
    @PostMapping
    @PreAuthorize("hasAuthority('invigilator_registration:create')")
    @Operation(summary = "Register Exam Slots", description = "Save the invigilator customEx for the exam slot")
    public ResponseEntity<InvigilatorRegistrationResponseDTO> registerExamSlotWithoutFuId(@RequestBody InvigilatorRegistrationRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invigilatorRegistrationService.registerExamSlotWithoutFuId(request));
    }

    //INVIGILATOR
    @GetMapping("/myinfo/semesterid={semesterId}")
    @PreAuthorize("hasAuthority('invigilator_registration:read')")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots by semester of current invigilator")
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllCurrentInvigilatorRegisteredSlotsInSemester(@PathVariable("semesterId") int semesterId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.getAllCurrentInvigilatorRegisteredSlotsInSemester(semesterId));
    }

    //INVIGILATOR
    @DeleteMapping("/myinfo/register")
    @PreAuthorize("hasAuthority('invigilator_registration:delete')")
    @Operation(summary = "Delete Registered Slot by ExamSlot Id")
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
    @PreAuthorize("hasAuthority('invigilator_registration:read')")
    @Operation(summary = "Get All Exam Slots in Semester with Status", description = "If invigilators was assigned to the exam slot then status will be ASSIGNED otherwise UNASSIGNED")
    public ResponseEntity<RegisteredExamBySemesterResponseDTO> getAllExamSlotsInSemesterWithStatus(@PathVariable("semesterId") int semesterId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.getAllExamSlotsInSemesterWithStatus(semesterId));
    }

    //INVIGILATOR
    @GetMapping("cancel")
    @PreAuthorize("hasAuthority('invigilator_registration:read')")
    @Operation(summary = "Get all exam slots that can be cancelled in a semester")
    public ResponseEntity<Set<ExamSlotDetail>> getListCancelInSemester(@RequestParam int semesterId) {
        return ResponseEntity.ok(invigilatorRegistrationService.getCancellableExamSlots(semesterId));
    }
}
