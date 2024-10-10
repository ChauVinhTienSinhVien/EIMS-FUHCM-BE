package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.RequestRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RequestResponseDTO;

public interface RequestService {
    RequestResponseDTO createRequest(RequestRequestDTO request);
}
