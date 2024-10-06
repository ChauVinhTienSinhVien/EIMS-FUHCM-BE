package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorAssignmentRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.RegisterdSlotWithSemesterAndInvigilatorRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAssignmentResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RegisteredExamInvigilationResponseDTO;

import java.util.List;

public interface InvigilatorAssignmentService {
    InvigilatorAssignmentResponseDTO registerExamSlot(InvigilatorAssignmentRequestDTO request);

    RegisteredExamInvigilationResponseDTO getAllCurrentInvigilatorRegisteredSlots();

    RegisteredExamInvigilationResponseDTO getAllRegisteredSlotsByInvigilator(String fuId);

    RegisteredExamInvigilationResponseDTO getAllRegisteredSlotsInSemesterByInvigilator(RegisterdSlotWithSemesterAndInvigilatorRequestDTO request);

    RegisteredExamInvigilationResponseDTO getAllCurrentInvigilatorRegisteredSlotsInSemester(int semesterId);

    InvigilatorAssignmentResponseDTO updateRegisterExamSlot(InvigilatorAssignmentRequestDTO request);

    boolean deleteAssignmentBySemester(InvigilatorAssignmentRequestDTO request);

}

