package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExchangeInvigilatorsRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.UpdateInvigilatorAssignmentRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotDetail;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotRoomResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.UserRegistrationResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.UserResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotRoom;
import com.fullsnacke.eimsfuhcmbe.entity.Request;

import java.util.List;
import java.util.Set;

public interface InvigilatorAssignmentService {
    List<ExamSlotRoomResponseDTO> assignInvigilators(List<Integer> examSlotIds);
    List<UserRegistrationResponseDTO> getUnassignedInvigilators(int examSlotId);
    String exchangeInvigilators(UpdateInvigilatorAssignmentRequestDTO request);
    String exchangeInvigilators(Request requestEntity, ExchangeInvigilatorsRequestDTO request);
    Set<ExamSlotDetail> getAllExamSlotsInSemesterWithStatus(int semesterId);


}
