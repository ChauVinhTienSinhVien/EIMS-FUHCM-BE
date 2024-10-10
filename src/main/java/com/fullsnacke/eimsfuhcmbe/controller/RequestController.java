package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.RequestRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RequestResponseDTO;
import com.fullsnacke.eimsfuhcmbe.service.RequestService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/requests")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RequestController {
    RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestResponseDTO> createRequest(@RequestBody RequestRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(requestService.createRequest(request));
    }
}
