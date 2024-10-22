package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.RequestMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.ExchangeInvigilatorsRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorRegistrationRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.RequestRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ManagerRequestResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RequestResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorRegistration;
import com.fullsnacke.eimsfuhcmbe.entity.Request;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.enums.RequestStatusEnum;
import com.fullsnacke.eimsfuhcmbe.enums.RequestTypeEnum;
import com.fullsnacke.eimsfuhcmbe.exception.AuthenticationProcessException;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.assignment.CustomMessageException;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotRepository;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorRegistrationRepository;
import com.fullsnacke.eimsfuhcmbe.repository.RequestRepository;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestServiceImpl implements RequestService {

    Logger log = org.slf4j.LoggerFactory.getLogger(RequestServiceImpl.class);

    RequestRepository requestRepository;
    UserRepository userRepository;
    RequestMapper requestMapper;
    ExamSlotRepository examSlotRepository;
    InvigilatorAssignmentService invigilatorAssignmentService;
    InvigilatorRegistrationService invigilatorRegistrationService;
    InvigilatorRegistrationRepository invigilatorRegistrationRepository;

    @Transactional(rollbackFor = Exception.class)
    public RequestResponseDTO createRequest(RequestRequestDTO request) {
        if (request == null) {
            throw new CustomException(ErrorCode.REQUEST_EMPTY);
        } else if (request.getExamSlotId() == null) {
            throw new CustomException(ErrorCode.EXAM_SLOT_ID_MISSING);
        } else if (request.getReason() == null || request.getReason().isEmpty()) {
            throw new CustomException(ErrorCode.REASON_EMPTY);
        }
//        else if(request.getRequestType() == null) {
//            throw new CustomMessageException(ErrorCode.REQUEST_TYPE_EMPTY);
//        }
        try {
            var currentUser = getCurrentUser();
            log.info("Current User: {}", currentUser.getFuId());
            request.setInvigilator(currentUser);
            request.setRequestType(RequestTypeEnum.CANCEL.name());

            Request entity = requestMapper.toEntity(request);
            setExamSlot(entity.getExamSlot());
            requestRepository.save(entity);

            RequestResponseDTO responseDTO = requestMapper.toResponseDTO(entity);
            responseDTO.setStatus(RequestStatusEnum.fromValue(entity.getStatus()).name());
            return responseDTO;
        } catch (Exception e) {
            log.error("Error occurred while creating request", e);
            throw new CustomException(ErrorCode.REQUEST_CREATION_FAILED);
        }
    }

    public List<RequestResponseDTO> getAllRequestOfCurrentInvigilator() {
        User currentUser = getCurrentUser();
        List<Request> entity = requestRepository.findByCreatedBy(currentUser);

        return getAllRequestsByInvigilator(currentUser);
    }

    public List<RequestResponseDTO> getAllRequestByInvigilatorId(String invigilatorId) {
        User invigilator = userRepository.findByFuId(invigilatorId);
        if (invigilator == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return getAllRequestsByInvigilator(invigilator);
    }

    private List<RequestResponseDTO> getAllRequestsByInvigilator(User invigilator) {
        List<Request> entity = requestRepository.findByCreatedBy(invigilator);

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

    public List<ManagerRequestResponseDTO> getAllRequestBySemester(int semesterId) {
        List<Request> entity = requestRepository.findByExamSlot_SubjectExam_SubjectId_SemesterId_Id(semesterId);
        return entity.stream()
                .map(request -> {
                    ManagerRequestResponseDTO responseDTO = requestMapper.toManagerRequestResponseDTO(request);
                    responseDTO.setStatus(RequestStatusEnum.fromValue(request.getStatus()).name());
                    return responseDTO;
                })
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public RequestResponseDTO updateRequestStatus(ExchangeInvigilatorsRequestDTO request) {
        //find request by id
        log.info("Request ID: {}", request.getRequestId());
        Request entity = requestRepository.findById(request.getRequestId())
                .orElseThrow(() -> new CustomException(ErrorCode.REQUEST_EMPTY));

        //convert status to enum
        log.info("Status entity: {}", entity.getStatus());
        RequestStatusEnum entityStatus = RequestStatusEnum.fromValue(entity.getStatus());
        RequestStatusEnum status;
        try {
            status = RequestStatusEnum.valueOf(request.getStatus().toUpperCase());
            log.info("Status: {}", status);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.REQUEST_STATUS_INVALID);
        }

        log.info("Status: {}", status);
        if (entityStatus != RequestStatusEnum.PENDING) {
            throw new CustomException(ErrorCode.REQUEST_ALREADY_PROCESSED);
        }

        //update status
        if (status == RequestStatusEnum.APPROVED) {
            //CREATE INVIGILATOR REGISTRATION
            InvigilatorRegistration registration = invigilatorRegistrationRepository.findByExamSlotIdAndInvigilatorFuId(entity.getExamSlot().getId(), request.getNewInvigilatorFuId())
                    .orElseGet(() -> {
                        InvigilatorRegistrationRequestDTO registrationRequest = InvigilatorRegistrationRequestDTO.builder()
                                .fuId(request.getNewInvigilatorFuId())
                                .examSlotId(Set.of(entity.getExamSlot().getId()))
                                .build();

                        invigilatorRegistrationService.registerExamSlotWithFuId(registrationRequest);
                        return null;
                    });

            invigilatorAssignmentService.exchangeInvigilators(entity, request);
        }
        //update request
        entity.setStatus(status.getValue());
//        entity.setUpdatedAt(Instant.now());
        entity.setComment(request.getNote());

        //save update request
        requestRepository.save(entity);

        //convert to response dto
        RequestResponseDTO responseDTO = requestMapper.toResponseDTO(entity);
        responseDTO.setStatus(status.name());
        return responseDTO;
    }


    //-----------------------------PRIVATE METHOD--------------------------------

    private void setExamSlot(ExamSlot examSlot) {
        int examSlotId = examSlot.getId();
        examSlot = examSlotRepository.findById(examSlotId)
                .orElseThrow(() -> new CustomMessageException(HttpStatus.BAD_REQUEST, "Exam slot ID " + examSlotId + " not found."));
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
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }


}
