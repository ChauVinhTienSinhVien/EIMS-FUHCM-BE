package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExchangeInvigilatorsRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.UserResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotRoom;

import java.util.List;

public interface InvigilatorAssignmentService {
    List<ExamSlotRoom> assignInvigilators(int semesterId);
    List<UserResponseDTO> getUnassignedInvigilators(int examSlotId);
    boolean exchangeInvigilators(ExchangeInvigilatorsRequestDTO request);
}
