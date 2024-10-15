package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.service.InvigilatorAssignmentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assignment")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class InvigilatorAssignmentController {
    InvigilatorAssignmentService invigilatorAssignmentService;

    @GetMapping("classify/semesterid={semesterId}")
    public ResponseEntity<?> assignInvigilatorToRoom(@PathVariable("semesterId") int semesterId) {
        return ResponseEntity.ok(invigilatorAssignmentService.assignInvigilators(semesterId));
    }
}
