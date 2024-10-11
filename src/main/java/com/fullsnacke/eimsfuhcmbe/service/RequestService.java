package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.RequestRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RequestResponseDTO;

import java.util.List;

public interface RequestService {
    RequestResponseDTO createRequest(RequestRequestDTO request);
    List<RequestResponseDTO> getAllRequestByInvigilator();
}
