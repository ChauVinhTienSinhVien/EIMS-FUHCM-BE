package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.RequestMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.RequestRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RequestResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.Request;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.enums.RequestStatusEnum;
import com.fullsnacke.eimsfuhcmbe.enums.RequestTypeEnum;
import com.fullsnacke.eimsfuhcmbe.exception.AuthenticationProcessException;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotRepository;
import com.fullsnacke.eimsfuhcmbe.repository.RequestRepository;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestServiceImpl implements RequestService {
    RequestRepository requestRepository;
    UserRepository userRepository;
    RequestMapper requestMapper;
    ExamSlotRepository examSlotRepository;

    @Transactional(rollbackFor = Exception.class)
    public RequestResponseDTO createRequest(RequestRequestDTO request) {
        if(request == null) {
            throw new CustomException(ErrorCode.REQUEST_EMPTY);
        } else if(request.getExamSlotId() == null) {
            throw new CustomException(ErrorCode.EXAM_SLOT_ID_MISSING);
        } else if(request.getReason() == null || request.getReason().isEmpty()) {
            throw new CustomException(ErrorCode.REASON_EMPTY);
        }
//        else if(request.getRequestType() == null) {
//            throw new CustomException(ErrorCode.REQUEST_TYPE_EMPTY);
//        }
        try {
            var currentUser = getCurrentUser();
            request.setInvigilator(currentUser);
            request.setRequestType(RequestTypeEnum.CANCEL.name());

            Request entity = requestMapper.toEntity(request);
            setExamSlot(entity.getExamSlot());
            requestRepository.save(entity);

            RequestResponseDTO responseDTO = requestMapper.toResponseDTO(entity);
            responseDTO.setStatus(RequestStatusEnum.fromValue(entity.getStatus()).name());
            return responseDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.REQUEST_CREATION_FAILED);
        }
    }

    public List<RequestResponseDTO> getAllRequestByInvigilator() {
        User currentUser = getCurrentUser();
        List<Request> entity = requestRepository.findByCreatedBy(currentUser);

        return entity.stream()
                .map(request -> {
                    RequestResponseDTO responseDTO = requestMapper.toResponseDTO(request);
                    responseDTO.setStatus(RequestStatusEnum.fromValue(request.getStatus()).name());
                    return responseDTO;
                })
                .toList();
    }

    public RequestResponseDTO getRequestById(int requestId) {
        Request entity = requestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException(ErrorCode.REQUEST_EMPTY));

        RequestResponseDTO responseDTO = requestMapper.toResponseDTO(entity);
        responseDTO.setStatus(RequestStatusEnum.fromValue(entity.getStatus()).name());
        return responseDTO;
    }



    private void setExamSlot(ExamSlot examSlot) {
        if (examSlot == null) {
            throw new CustomException(ErrorCode.EXAM_SLOT_NOT_FOUND);
        }
        examSlot = examSlotRepository.findById(examSlot.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.EXAM_SLOT_NOT_FOUND));
    }

    private User getCurrentUser() {
        var context = SecurityContextHolder.getContext();
        if (context == null || context.getAuthentication() == null) {
            throw new AuthenticationProcessException(ErrorCode.AUTHENTICATION_CONTEXT_NOT_FOUND);
        }

        Authentication authentication = context.getAuthentication();

        String email = authentication.getName();
        if (email == null || email.isEmpty()) {
            throw new AuthenticationProcessException(ErrorCode.AUTHENTICATION_EMAIL_MISSING);
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException(ErrorCode.USER_NOT_FOUND));
    }


}
