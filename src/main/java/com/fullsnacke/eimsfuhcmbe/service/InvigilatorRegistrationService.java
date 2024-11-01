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

    List<InvigilatorRegistration> getAllRegistrationsInTimeRange(Instant start, Instant end);

    RegisteredExamInvigilationResponseDTO getAllCurrentInvigilatorRegisteredSlots();

    RegisteredExamInvigilationResponseDTO getAllRegisteredSlotsByInvigilator(String fuId);

    RegisteredExamInvigilationResponseDTO getAllRegisteredSlotsInSemesterByInvigilator(int semesterId, String fuId);

    RegisteredExamInvigilationResponseDTO getAllCurrentInvigilatorRegisteredSlotsInSemester(int semesterId);

    InvigilatorRegistrationResponseDTO updateRegisterExamSlot(InvigilatorRegistrationRequestDTO request);

    boolean deleteRegisteredSlotsBySemester(RegisterdSlotWithSemesterAndInvigilatorRequestDTO request);

    Set<RegisteredExamBySemesterResponseDTO> getRegisteredExamBySemester(int semesterId);

    ListInvigilatorsByExamSlotResponseDTO listInvigilatorsByExamSlot(int examSlotId);

    RegisteredExamBySemesterResponseDTO getAllExamSlotsInSemesterWithStatus(int semesterId);

//    Set<ExamSlotDetail> deleteRegisteredSlotByExamSlotId(InvigilatorRegistrationRequestDTO request);

    Set<ExamSlotDetail> deleteCurrentInvigilatorRegisteredSlotByExamSlotId(Set<Integer> examSlotId);

    Set<ExamSlotDetail> getCancellableExamSlots(int semesterId);

}

