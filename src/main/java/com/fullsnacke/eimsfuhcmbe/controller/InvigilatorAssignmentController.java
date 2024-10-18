package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import com.fullsnacke.eimsfuhcmbe.service.InvigilatorAssignmentService;
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
    @GetMapping()
    public ResponseEntity<?> assignInvigilatorToRoom(@RequestParam List<Integer> examSlotId) {
        return ResponseEntity.ok(invigilatorAssignmentService.assignInvigilators(examSlotId));
    }

    //MANAGER
    @GetMapping("/unassigned/invigilators/examslotid={examSlotId}")
    public ResponseEntity<?> getUnassignedInvigilators(@PathVariable("examSlotId") int examSlotId) {
        return ResponseEntity.ok(invigilatorAssignmentService.getUnassignedInvigilators(examSlotId));
    }
}
