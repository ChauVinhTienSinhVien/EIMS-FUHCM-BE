package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.service.InvigilatorAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('invigilator_assignment:create')")
    @Operation(summary = "Assign invigilators to exam slots")
    public ResponseEntity<?> assignInvigilatorToRoom(@RequestParam List<Integer> examSlotId) {
        return ResponseEntity.ok(invigilatorAssignmentService.assignInvigilators(examSlotId));
    }

    //MANAGER
    @GetMapping("/unassigned/invigilators/examslotid={examSlotId}")
    @PreAuthorize("hasAuthority('invigilator_assignment:read')")
    @Operation(summary = "Get unassigned invigilators for a given exam slot")
    public ResponseEntity<?> getUnassignedInvigilators(@PathVariable("examSlotId") int examSlotId) {
        return ResponseEntity.ok(invigilatorAssignmentService.getUnassignedInvigilators(examSlotId));
    }

    //MANAGER
    @GetMapping("/assigned/invigilators/examslotid={examSlotId}")
    @PreAuthorize("hasAuthority('invigilator_assignment:read')")
    @Operation(summary = "Get assigned invigilators for a given exam slot")
    public ResponseEntity<?> getAssignedInvigilators(@PathVariable("examSlotId") int examSlotId) {
        return ResponseEntity.ok(invigilatorAssignmentService.getAssignedInvigilators(examSlotId));
    }

    //INVIGILATOR
    @GetMapping("/myinfo/scheduled")
    @PreAuthorize("hasAuthority('invigilator_assignment:read')")
    @Operation(summary = "Get all exam slots that an invigilator is assigned to")
    public ResponseEntity<?> getScheduledExamSlots(@RequestParam int semesterId) {
        return ResponseEntity.ok(invigilatorAssignmentService.getAllExamSlotsAssignedInSemester(semesterId));
    }

    //INVIGILATOR
    @GetMapping("/examslots/semesterid={semesterId}")
    @PreAuthorize("hasAuthority('invigilator_assignment:read')")
    @Operation(summary = "Get all exam slots in a semester with status", description = "If invigilators was assigned to the exam slot then status will be ASSIGNED otherwise UNASSIGNED")
    public ResponseEntity<?> getAllExamSlotsInSemesterWithStatus(@PathVariable("semesterId") int semesterId) {
        return ResponseEntity.ok(invigilatorAssignmentService.getAllExamSlotsInSemesterWithStatus(semesterId));
    }

    //INVIGILATOR
    @GetMapping("report")
    @PreAuthorize("hasAuthority('invigilator_assignment:read')")
    public ResponseEntity<?> getInvigilatorAssignmentReport(@RequestParam int semesterId) {
        return ResponseEntity.ok(invigilatorAssignmentService.getInvigilatorAssignmentReport(semesterId));
    }
}
