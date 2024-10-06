package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorAssignmentRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAssignmentResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RegisteredExamInvigilationResponseDTO;
import com.fullsnacke.eimsfuhcmbe.service.InvigilatorAssignmentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invigilators")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class InvigilatorAssignmentController {

    InvigilatorAssignmentService invigilatorAssignmentService;

    @PostMapping
    public ResponseEntity<InvigilatorAssignmentResponseDTO> registerExamSlot(@RequestBody InvigilatorAssignmentRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invigilatorAssignmentService.registerExamSlot(request));
    }

    @GetMapping
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllRegisteredSlot(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorAssignmentService.getAllCurrentInvigilatorRegisteredSlots());
    }

    @GetMapping("/{fuId}")
    public ResponseEntity<RegisteredExamInvigilationResponseDTO> getAllRegisteredSlot(@PathVariable("fuId") @RequestBody String fuId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invigilatorAssignmentService.getAllRegisteredSlotsByInvigilator(fuId));
    }

    @PutMapping
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
