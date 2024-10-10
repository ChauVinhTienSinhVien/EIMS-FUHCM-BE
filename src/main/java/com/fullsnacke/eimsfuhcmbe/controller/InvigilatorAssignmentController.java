package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorAssignmentRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.RegisterdSlotWithSemesterAndInvigilatorRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAssignmentResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ListInvigilatorsByExamSlotResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RegisteredExamBySemesterResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RegisteredExamInvigilationResponseDTO;
import com.fullsnacke.eimsfuhcmbe.service.InvigilatorAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/invigilators")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class InvigilatorAssignmentController {

    InvigilatorAssignmentService invigilatorAssignmentService;

    @PostMapping
    @Operation(summary = "Register Exam Slots", description = "Save the invigilator customEx for the exam slot")
    public ResponseEntity<InvigilatorAssignmentResponseDTO> registerExamSlot(@RequestBody InvigilatorAssignmentRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invigilatorAssignmentService.registerExamSlot(request));
    }

    @GetMapping("/myinfo")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots for the current invigilator")
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllRegisteredSlot(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorAssignmentService.getAllCurrentInvigilatorRegisteredSlots());
    }

    @GetMapping("/{fuId}")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots by fuID")
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllRegisteredSlot(@PathVariable("fuId") String fuId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorAssignmentService.getAllRegisteredSlotsByInvigilator(fuId));
    }

    @GetMapping("/semesterid={semesterId}/invigilator={fuId}")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots by semester and fuId")
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllRegisteredSlotsInSemesterByInvigilator(@PathVariable("semesterId") int semesterId, @PathVariable("fuId") String fuId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorAssignmentService.getAllRegisteredSlotsInSemesterByInvigilator(semesterId, fuId));
    }

    @GetMapping("/myinfo/semesterid={semesterId}")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots by semester of current invigilator")
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllCurrentInvigilatorRegisteredSlotsInSemester(@PathVariable("semesterId") int semesterId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorAssignmentService.getAllCurrentInvigilatorRegisteredSlotsInSemester(semesterId));
    }

    @GetMapping("/semesterid={semesterId}")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots by semester of all invigilators")
    public ResponseEntity<Set<RegisteredExamBySemesterResponseDTO>> getAllRegisteredSlotsInSemester(@PathVariable("semesterId") @RequestBody int semesterId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorAssignmentService.getRegisteredExamBySemester(semesterId));
    }

    @GetMapping("/examslotid={examSlotId}")
    @Operation(summary = "Get All Invigilators", description = "Get all the invigilators by exam slot")
    public ResponseEntity<ListInvigilatorsByExamSlotResponseDTO> listInvigilatorsByExamSlot(@PathVariable("examSlotId") int examSlotId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorAssignmentService.listInvigilatorsByExamSlot(examSlotId));
    }

    @PutMapping
    @Operation(summary = "Update Registered Slot", description = "Update the registered slot by fuId")
    public ResponseEntity<InvigilatorAssignmentResponseDTO> updateRegisteredSlot(@RequestBody InvigilatorAssignmentRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorAssignmentService.updateRegisterExamSlot(request));
    }

    @DeleteMapping
    @Operation(summary = "Delete Registered Slot", description = "Delete the registered slot by fuId and semester")
    public ResponseEntity<Boolean> deleteAssignmentBySemester(@RequestBody RegisterdSlotWithSemesterAndInvigilatorRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorAssignmentService.deleteAssignmentBySemester(request));
    }

    @GetMapping("/register/semesterid={semesterId}")
    public ResponseEntity<RegisteredExamBySemesterResponseDTO> getAllExamSlotsInSemesterWithStatus(@PathVariable("semesterId") int semesterId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorAssignmentService.getAllExamSlotsInSemesterWithStatus(semesterId));
    }






}
