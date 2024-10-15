package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorRegistrationRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.RegisterdSlotWithSemesterAndInvigilatorRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.*;
import com.fullsnacke.eimsfuhcmbe.exception.repository.assignment.CustomException;
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

    @PostMapping
    @Operation(summary = "Register Exam Slots", description = "Save the invigilator customEx for the exam slot")
    public ResponseEntity<InvigilatorRegistrationResponseDTO> registerExamSlotWithoutFuId(@RequestBody InvigilatorRegistrationRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invigilatorRegistrationService.registerExamSlotWithoutFuId(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Register Exam Slots", description = "Save the invigilator customEx for the exam slot")
    public ResponseEntity<InvigilatorRegistrationResponseDTO> registerExamSlotWithFuId(@RequestBody InvigilatorRegistrationRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invigilatorRegistrationService.registerExamSlotWithFuId(request));
    }

    @GetMapping("/myinfo")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots for the current invigilator")
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllRegisteredSlot(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.getAllCurrentInvigilatorRegisteredSlots());
    }

    @GetMapping("/{fuId}")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots by fuID")
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllRegisteredSlot(@PathVariable("fuId") String fuId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.getAllRegisteredSlotsByInvigilator(fuId));
    }

    @GetMapping("/semesterid={semesterId}/invigilator={fuId}")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots by semester and fuId")
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllRegisteredSlotsInSemesterByInvigilator(@PathVariable("semesterId") int semesterId, @PathVariable("fuId") String fuId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.getAllRegisteredSlotsInSemesterByInvigilator(semesterId, fuId));
    }

    @GetMapping("/myinfo/semesterid={semesterId}")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots by semester of current invigilator")
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllCurrentInvigilatorRegisteredSlotsInSemester(@PathVariable("semesterId") int semesterId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.getAllCurrentInvigilatorRegisteredSlotsInSemester(semesterId));
    }

    @GetMapping("/semesterid={semesterId}")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots by semester of all invigilators")
    public ResponseEntity<Set<RegisteredExamBySemesterResponseDTO>> getAllRegisteredSlotsInSemester(@PathVariable("semesterId") @RequestBody int semesterId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.getRegisteredExamBySemester(semesterId));
    }

    @GetMapping("/examslotid={examSlotId}")
    @Operation(summary = "Get All Invigilators", description = "Get all the invigilators by exam slot")
    public ResponseEntity<ListInvigilatorsByExamSlotResponseDTO> listInvigilatorsByExamSlot(@PathVariable("examSlotId") int examSlotId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.listInvigilatorsByExamSlot(examSlotId));
    }

    @PutMapping
    @Operation(summary = "Update Registered Slot", description = "Update the registered slot by fuId")
    public ResponseEntity<InvigilatorRegistrationResponseDTO> updateRegisteredSlot(@RequestBody InvigilatorRegistrationRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.updateRegisterExamSlot(request));
    }

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

    @DeleteMapping("/myinfo/register")
    @Operation(summary = "Delete Registered Slot by ExamSlot Id")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteCurrentInvigilatorRegisteredSlotByExamSlotId(@RequestParam Set<Integer> id) {
        Logger log = LogManager.getLogger(InvigilatorRegistrationController.class);
        log.info("Nhận yêu cầu xóa slot đã đăng ký với ID: {}", id);

        try {
            Set<ExamSlotDetail> result = invigilatorRegistrationService.deleteCurrentInvigilatorRegisteredSlotByExamSlotId(id);
            log.info("Đã xóa thành công {} slot", result.size());
            return ResponseEntity.ok(result);
        } catch (CustomException e) {
            log.error("Lỗi khi xóa slot đã đăng ký: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/register/semesterid={semesterId}")
    public ResponseEntity<RegisteredExamBySemesterResponseDTO> getAllExamSlotsInSemesterWithStatus(@PathVariable("semesterId") int semesterId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorRegistrationService.getAllExamSlotsInSemesterWithStatus(semesterId));
    }






}
