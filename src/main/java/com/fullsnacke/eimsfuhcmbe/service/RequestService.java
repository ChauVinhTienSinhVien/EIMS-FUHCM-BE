package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.RequestRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.UpdateStatusRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RequestResponseDTO;

import java.util.List;

public interface RequestService {
    RequestResponseDTO createRequest(RequestRequestDTO request);
    List<RequestResponseDTO> getAllRequestOfCurrentInvigilator();
    List<RequestResponseDTO> getAllRequestByInvigilatorId(String invigilatorId);
    RequestResponseDTO getRequestById(int requestId);
    RequestResponseDTO updateRequestStatus(int requestId, UpdateStatusRequestDTO status);
}
