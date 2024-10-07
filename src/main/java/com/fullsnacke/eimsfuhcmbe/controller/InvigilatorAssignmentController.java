package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorAssignmentRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.RegisterdSlotWithSemesterAndInvigilatorRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAssignmentResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RegisteredExamBySemester;
import com.fullsnacke.eimsfuhcmbe.dto.response.RegisteredExamInvigilationResponseDTO;
import com.fullsnacke.eimsfuhcmbe.service.InvigilatorAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/invigilators")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class InvigilatorAssignmentController {

    InvigilatorAssignmentService invigilatorAssignmentService;

    @PostMapping
    @Operation(summary = "Register Exam Slots", description = "Save the invigilator assignment for the exam slot")
    public ResponseEntity<InvigilatorAssignmentResponseDTO> registerExamSlot(@RequestBody InvigilatorAssignmentRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invigilatorAssignmentService.registerExamSlot(request));
    }

    @GetMapping
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots for the current invigilator")
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllRegisteredSlot(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorAssignmentService.getAllCurrentInvigilatorRegisteredSlots());
    }

    @GetMapping("/{fuId}")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots by fuID")
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllRegisteredSlot(@PathVariable("fuId") @RequestBody String fuId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorAssignmentService.getAllRegisteredSlotsByInvigilator(fuId));
    }

    @GetMapping("/semester")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots by semester and fuId")
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllRegisteredSlotsInSemesterByInvigilator(@RequestBody RegisterdSlotWithSemesterAndInvigilatorRequestDTO request){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorAssignmentService.getAllRegisteredSlotsInSemesterByInvigilator(request));
    }

    @GetMapping("/semester/{semesterId}")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots by semester of current invigilator")
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllCurrentInvigilatorRegisteredSlotsInSemester(@RequestBody @PathVariable("semesterId") int semesterId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorAssignmentService.getAllCurrentInvigilatorRegisteredSlotsInSemester(semesterId));
    }

    @GetMapping("/examSlots-invigilators/{semesterId}")
    @Operation(summary = "Get All Registered Slots", description = "Get all the registered slots by semester of all invigilators")
    public ResponseEntity<Set<RegisteredExamBySemester>> getAllRegisteredSlotsInSemester(@PathVariable("semesterId") @RequestBody int semesterId){
        return ResponseEntity
                .body(invigilatorAssignmentService.getRegisteredExamBySemester(semesterId));
    }

    @PutMapping
    @Operation(summary = "Update Registered Slot", description = "Update the registered slot by fuId")
    public ResponseEntity<InvigilatorAssignmentResponseDTO> updateRegisteredSlot(@RequestBody InvigilatorAssignmentRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorAssignmentService.updateRegisterExamSlot(request));
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteAssignmentBySemester(@RequestBody InvigilatorAssignmentRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorAssignmentService.deleteAssignmentBySemester(request));
    }






}
