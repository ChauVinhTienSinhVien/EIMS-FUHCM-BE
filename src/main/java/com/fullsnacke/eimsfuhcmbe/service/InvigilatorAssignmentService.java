package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExchangeInvigilatorsRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.UpdateInvigilatorAssignmentRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.*;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotRoom;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import com.fullsnacke.eimsfuhcmbe.entity.Request;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public interface InvigilatorAssignmentService {
    List<ExamSlotRoomResponseDTO> assignInvigilators(List<Integer> examSlotIds);
    List<UserRegistrationResponseDTO> getUnassignedInvigilators(int examSlotId);
    List<InvigilatorAssignmentResponseDTO> getAssignedInvigilators(int examSlotId);
    String exchangeInvigilators(Request requestEntity, ExchangeInvigilatorsRequestDTO request);
    Set<ExamSlotDetail> getAllExamSlotsInSemesterWithStatus(int semesterId);
    List<InvigilatorAssignmentResponseDTO> getAllExamSlotsAssignedInSemester (int semesterId);

    //dashboard
    List<InvigilatorAssignment> getAllAssignmentsInTimeRange(Instant start, Instant end);
    List<InvigilatorAssignment> managerApproveInvigilatorAssignments(List<Integer> invigilatorAssignmentIds);

    InvigilatorAssignmentReportResponseDTO getInvigilatorAssignmentReport(int semesterId);

}
