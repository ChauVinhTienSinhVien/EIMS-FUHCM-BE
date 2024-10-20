package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.UpdateInvigilatorAssignmentRequestDTO;
import com.fullsnacke.eimsfuhcmbe.service.InvigilatorAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignment")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class InvigilatorAssignmentController {
    InvigilatorAssignmentService invigilatorAssignmentService;


    //SYSTEM
    @GetMapping
    @Operation(summary = "Assign invigilators to exam slots")
    public ResponseEntity<?> assignInvigilatorToRoom(@RequestParam List<Integer> examSlotId) {
        return ResponseEntity.ok(invigilatorAssignmentService.assignInvigilators(examSlotId));
    }

    //MANAGER
    @GetMapping("/unassigned/invigilators/examslotid={examSlotId}")
    @Operation(summary = "Get unassigned invigilators for a given exam slot")
    public ResponseEntity<?> getUnassignedInvigilators(@PathVariable("examSlotId") int examSlotId) {
        return ResponseEntity.ok(invigilatorAssignmentService.getUnassignedInvigilators(examSlotId));
    }

    //STAFF
    @PutMapping
    @Operation(summary = "Exchange a unassigned invigilator with a assigned invigilator")
    public ResponseEntity<?> exchangeInvigilators(@RequestBody UpdateInvigilatorAssignmentRequestDTO request) {
        return ResponseEntity.ok(invigilatorAssignmentService.exchangeInvigilators(request));
    }

    @GetMapping("/examslots/semesterid={semesterId}")
    @Operation(summary = "Get all exam slots in a semester with status", description = "If invigilators was assigned to the exam slot then status will be ASSIGNED otherwise UNASSIGNED")
    public ResponseEntity<?> getAllExamSlotsInSemesterWithStatus(@PathVariable("semesterId") int semesterId) {
        return ResponseEntity.ok(invigilatorAssignmentService.getAllExamSlotsInSemesterWithStatus(semesterId));
    }
}
