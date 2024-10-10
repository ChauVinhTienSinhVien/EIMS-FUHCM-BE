package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorAssignmentRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.RegisterdSlotWithSemesterAndInvigilatorRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAssignmentResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ListInvigilatorsByExamSlotResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RegisteredExamBySemesterResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RegisteredExamInvigilationResponseDTO;

import java.util.Set;

public interface InvigilatorAssignmentService {
    InvigilatorAssignmentResponseDTO registerExamSlot(InvigilatorAssignmentRequestDTO request);

    RegisteredExamInvigilationResponseDTO getAllCurrentInvigilatorRegisteredSlots();

    RegisteredExamInvigilationResponseDTO getAllRegisteredSlotsByInvigilator(String fuId);

    RegisteredExamInvigilationResponseDTO getAllRegisteredSlotsInSemesterByInvigilator(int semesterId, String fuId);

    RegisteredExamInvigilationResponseDTO getAllCurrentInvigilatorRegisteredSlotsInSemester(int semesterId);

    InvigilatorAssignmentResponseDTO updateRegisterExamSlot(InvigilatorAssignmentRequestDTO request);

    boolean deleteAssignmentBySemester(RegisterdSlotWithSemesterAndInvigilatorRequestDTO request);

    Set<RegisteredExamBySemesterResponseDTO> getRegisteredExamBySemester(int semesterId);

    ListInvigilatorsByExamSlotResponseDTO listInvigilatorsByExamSlot(int examSlotId);

    RegisteredExamBySemesterResponseDTO getAllExamSlotsInSemesterWithStatus(int semesterId);
}

