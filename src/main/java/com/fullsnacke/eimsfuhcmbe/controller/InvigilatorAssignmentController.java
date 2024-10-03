package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorAssignmentRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAssignmentResponseDTO;
import com.fullsnacke.eimsfuhcmbe.service.InvigilatorAssignmentService;
import com.fullsnacke.eimsfuhcmbe.service.InvigilatorAssignmentServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invigilators")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class InvigilatorAssignmentController {

    InvigilatorAssignmentServiceImpl invigilatorAssignmentService;

    @PostMapping
    public ResponseEntity<InvigilatorAssignmentResponseDTO> registerAnExamSlot(@RequestBody InvigilatorAssignmentRequestDTO request) {
        InvigilatorAssignmentResponseDTO invigilatorAssignmentResponseDTO = invigilatorAssignmentService.registerAnExamSlot(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invigilatorAssignmentResponseDTO);
    }

}
