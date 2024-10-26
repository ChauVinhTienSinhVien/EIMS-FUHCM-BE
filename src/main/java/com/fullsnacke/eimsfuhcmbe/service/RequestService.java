package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExchangeInvigilatorsRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.RequestRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.UpdateStatusRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ManagerRequestResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RequestResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Request;

import java.util.List;

public interface RequestService {
    RequestResponseDTO createRequest(RequestRequestDTO request);
    List<RequestResponseDTO> getAllRequestOfCurrentInvigilator();
    List<RequestResponseDTO> getAllRequestByInvigilatorId(String invigilatorId);
    RequestResponseDTO getRequestById(int requestId);
    RequestResponseDTO updateRequestStatus(ExchangeInvigilatorsRequestDTO request);
    List<ManagerRequestResponseDTO> getAllRequestBySemester(int semesterId);

    Request createAttendanceUpdateRequest(Request request);
}
