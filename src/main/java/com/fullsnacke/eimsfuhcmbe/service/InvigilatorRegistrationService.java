package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorRegistrationRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.RegisterdSlotWithSemesterAndInvigilatorRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.*;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorRegistration;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface InvigilatorRegistrationService {
    InvigilatorRegistrationResponseDTO registerExamSlotWithFuId(InvigilatorRegistrationRequestDTO request);

    InvigilatorRegistrationResponseDTO registerExamSlotWithoutFuId(InvigilatorRegistrationRequestDTO request);

    RegisteredExamInvigilationResponseDTO getAllRegisteredSlotsInSemesterByInvigilator(int semesterId, String fuId);

    RegisteredExamInvigilationResponseDTO getAllCurrentInvigilatorRegisteredSlotsInSemester(int semesterId);

    RegisteredExamBySemesterResponseDTO getAllExamSlotsInSemesterWithStatus(int semesterId);

    Set<ExamSlotDetail> deleteCurrentInvigilatorRegisteredSlotByExamSlotId(Set<Integer> examSlotId);

    Set<ExamSlotDetail> getCancellableExamSlots(int semesterId);

}

