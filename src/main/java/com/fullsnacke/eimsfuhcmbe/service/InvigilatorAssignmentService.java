package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorAssignmentRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAssignmentResponseDTO;

import java.util.Set;

public interface InvigilatorAssignmentService {
    InvigilatorAssignmentResponseDTO registerExamSlot(InvigilatorAssignmentRequestDTO request);
}
