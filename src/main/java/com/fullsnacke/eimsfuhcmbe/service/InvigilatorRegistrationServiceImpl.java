package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.configuration.ConfigurationHolder;
import com.fullsnacke.eimsfuhcmbe.dto.mapper.InvigilatorRegistrationMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorRegistrationRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.RegisterdSlotWithSemesterAndInvigilatorRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.*;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorRegistration;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.enums.ExamSlotInvigilatorStatus;
import com.fullsnacke.eimsfuhcmbe.enums.ExamSlotRegisterStatusEnum;
import com.fullsnacke.eimsfuhcmbe.enums.ExamSlotStatus;
import com.fullsnacke.eimsfuhcmbe.exception.AuthenticationProcessException;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.assignment.CustomMessageException;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import com.fullsnacke.eimsfuhcmbe.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.fullsnacke.eimsfuhcmbe.enums.ConfigType.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvigilatorRegistrationServiceImpl implements InvigilatorRegistrationService {

    Logger log = org.slf4j.LoggerFactory.getLogger(InvigilatorRegistrationServiceImpl.class);

    InvigilatorRegistrationRepository invigilatorRegistrationRepository;
    ExamSlotRepository examSlotRepository;
    UserRepository userRepository;
    SemesterRepository semesterRepository;
    ConfigService configService;
    ConfigurationHolder configurationHolder;
    InvigilatorRegistrationMapper invigilatorRegistrationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Set<ExamSlotDetail> deleteCurrentInvigilatorRegisteredSlotByExamSlotId(Set<Integer> request) {
        User currentInvigilator = getCurrentUser();
        return deleteSelectedRegisteredSlots(currentInvigilator, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvigilatorRegistrationResponseDTO registerExamSlotWithoutFuId(InvigilatorRegistrationRequestDTO request) {
        User invigilator = getCurrentUser();

        return registerExamSlot(invigilator, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvigilatorRegistrationResponseDTO registerExamSlotWithFuId(InvigilatorRegistrationRequestDTO request) {
        User invigilator = findInvigilatorByFuId(request.getFuId());
        return registerExamSlot(invigilator, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisteredExamInvigilationResponseDTO getAllCurrentInvigilatorRegisteredSlotsInSemester(int semesterId) {
        User currentUser = getCurrentUser();
        return getAllRegisteredSlotsInSemesterByInvigilator(semesterId, currentUser.getFuId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisteredExamInvigilationResponseDTO getAllRegisteredSlotsInSemesterByInvigilator(int semesterId, String fuId) {
        User invigilator = findInvigilatorByFuId(fuId);
        Semester semester = getSemesterById(semesterId);

        Set<ExamSlot> examSlots = invigilatorRegistrationRepository
                .findByInvigilatorAndSemesterWithDetails(invigilator, semester)
                .stream()
                .map(InvigilatorRegistration::getExamSlot)
                .collect(Collectors.toSet());

        SemesterInvigilatorRegistrationResponseDTO semesterInvigilatorRegistrationResponseDTO = SemesterInvigilatorRegistrationResponseDTO.builder()
                .semesterId(semester.getId())
                .semesterName(semester.getName())
                .allowedSlots(Integer.parseInt(configService.getConfigBySemesterIdAndConfigType(semester.getId(), ALLOWED_SLOT.getValue()).getValue()))
                .examSlotDetailSet(invigilatorRegistrationMapper.mapExamSlotDetails(examSlots))
                .build();

        return RegisteredExamInvigilationResponseDTO.builder()
                .fuId(fuId)
                .semesterInvigilatorRegistration(Collections.singletonList(semesterInvigilatorRegistrationResponseDTO))
                .build();
    }

    @Override
    public RegisteredExamBySemesterResponseDTO getAllExamSlotsInSemesterWithStatus(int semesterId) {
        User currentUser = getCurrentUser();
        Semester semester = getSemesterById(semesterId);
        int openRegistration = configurationHolder.getTimeBeforeOpenRegistration();

        // Tính toán ngày bắt đầu
        ZonedDateTime endDay = ZonedDateTime.now().plusDays(openRegistration);

        // Chuyển đổi allExamSlots từ List sang Set
        Set<ExamSlot> allExamSlots = new HashSet<>(examSlotRepository.findExamSlotsBySemesterAndBeforeEndDate(semester, endDay, ExamSlotStatus.APPROVED.getValue()));

        Set<InvigilatorRegistration> registeredSlots = invigilatorRegistrationRepository.findByInvigilatorAndSemesterWithDetails(currentUser, semester);

        // Tạo một Set để lưu trữ kết quả cuối cùng
        Set<ExamSlotDetail> examSlotDetails = new HashSet<>();
        for (ExamSlot examSlot : allExamSlots) {
            String status;

            int count = invigilatorRegistrationRepository.countByExamSlot(examSlot);

            if (registeredSlots.stream().anyMatch(registration -> registration.getExamSlot().equals(examSlot))) {
                status = ExamSlotRegisterStatusEnum.REGISTERED.name();
            } else if (examSlot.getRequiredInvigilators() != 0 && count < examSlot.getRequiredInvigilators()) {
                status = ExamSlotRegisterStatusEnum.NOT_FULL.name();
            } else {
                status = ExamSlotRegisterStatusEnum.FULL.name();
            }
            ExamSlotDetail examSlotDetail = invigilatorRegistrationMapper.toExamSlotDetailInvigilator(examSlot);
            examSlotDetail.setStatus(status);
            examSlotDetails.add(examSlotDetail);
            examSlotDetail.setNumberOfRegistered((int) count);
            examSlotDetail.setRequiredInvigilators(examSlot.getRequiredInvigilators());
        }

        return RegisteredExamBySemesterResponseDTO.builder()
                .fuId(currentUser.getFuId())
                .examSlotDetails(examSlotDetails)
                .build();
    }

    @Override
    public Set<ExamSlotDetail> getCancellableExamSlots(int semesterId) {
        User currentUser = getCurrentUser();
        Semester semester = getSemesterById(semesterId);

        Set<InvigilatorRegistration> registrations = invigilatorRegistrationRepository
                .findCancellableExamSlotsBySemesterId(semester, currentUser);

        return invigilatorRegistrationMapper.mapCancelExamSlotDetails(registrations
                .stream()
                .map(InvigilatorRegistration::getExamSlot)
                .collect(Collectors.toSet()));
    }

    //-------------PRIVATE METHODS----------------

    private Semester getSemesterById(int semesterId) {
        return semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEMESTER_NOT_FOUND));
    }

    private User findInvigilatorByFuId(String fuId) {
        User invigilator = userRepository.findByFuId(fuId);
        if (invigilator == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        return invigilator;
    }

    private void validateExamSlotId(Set<Integer> examSlotIds) {
        if (examSlotIds == null || examSlotIds.isEmpty()) {
            throw new CustomException(ErrorCode.EXAM_SLOT_SET_EMPTY);
        }
    }

    private ExamSlot findRepresentativeExamSlot(Set<Integer> examSlotIds) {
        int examSlotId = examSlotIds.iterator().next();
        return examSlotRepository.findById(examSlotId)
                .orElseThrow(() -> new CustomMessageException(HttpStatus.BAD_REQUEST, "Exam slot ID " + examSlotId + " not found."));
    }

    private Set<InvigilatorRegistration> createRegistrations(User invigilator, Set<Integer> examSlotIds) {
        return examSlotIds.stream()
                .map(examSlotId -> examSlotRepository.findById(examSlotId)
                        .orElseThrow(() -> new CustomMessageException(HttpStatus.BAD_REQUEST, "Exam slot ID " + examSlotId + " not found.")))
                .map(examSlot -> InvigilatorRegistration.builder()
                        .invigilator(invigilator)
                        .examSlot(examSlot)
                        .build())
                .collect(Collectors.toSet());
    }

    private InvigilatorRegistrationResponseDTO createResponseDTO(User invigilator, Semester semester, Set<ExamSlotDetail> slotDetails) {
        return InvigilatorRegistrationResponseDTO.builder()
                .fuId(invigilator.getFuId())
                .semester(semester)
                .examSlots(slotDetails)
                .build();
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

    private Set<ExamSlotDetail> validateAndGetNonOverlappingExamSlotsDetail(User invigilator, Semester semester, Set<Integer> examSlotIds) {
        //Lấy ra các examSlot đã được đăng ký trước đó của invigilator hiện tại
        Set<InvigilatorRegistration> existingRegistrations = getExistingRegistrations(invigilator, semester);

        if (existingRegistrations.size() + examSlotIds.size() > allowedSlot(semester)) {
            throw new CustomException(ErrorCode.EXCEEDED_ALLOWED_SLOT);
        }
        //Lấy ra ExamSlot của tất cả các examSlotId cần được check và add vô db
        Set<ExamSlot> newExamSlots = new HashSet<>(examSlotRepository.findAllById(examSlotIds));
        Set<ExamSlotDetail> examSlotDetails = new HashSet<>();

        validateNewExamSlots(newExamSlots, examSlotDetails);

        checkOverlapWithExistingRegistrations(existingRegistrations, newExamSlots);
        return examSlotDetails;
    }

    private Set<InvigilatorRegistration> getExistingRegistrations(User invigilator, Semester semester) {
        return invigilatorRegistrationRepository.findRegistrationsWithDetailsByInvigilatorAndSemester(
                invigilator.getId(), semester.getId());
    }

    private void validateNewExamSlots(Set<ExamSlot> newExamSlots, Set<ExamSlotDetail> examSlotDetails) {
        for (ExamSlot newSlot : newExamSlots) {
            validateRequiredInvigilators(newSlot);
            checkOverlapWithinNewSlots(newSlot, newExamSlots);
            examSlotDetails.add(invigilatorRegistrationMapper.toExamSlotDetailInvigilator(newSlot));
        }
    }

    private void validateRequiredInvigilators(ExamSlot slot) {
        if (slot.getRequiredInvigilators() <= 0) {
            throw new CustomMessageException(HttpStatus.CONFLICT, "Exam slot ID " + slot.getId() + " is full.");
        }
    }

    private void checkOverlapWithinNewSlots(ExamSlot currentSlot, Set<ExamSlot> allNewSlots) {
        for (ExamSlot otherSlot : allNewSlots) {
            if (currentSlot.getId().intValue() != otherSlot.getId().intValue() && isOverlapping(currentSlot, otherSlot)) {
                throw new CustomMessageException(HttpStatus.CONFLICT, "Exam slot ID " + currentSlot.getId() + " overlaps with  " + otherSlot.getId() + " in the registration list.");
            }
        }
    }

    private void checkOverlapWithExistingRegistrations(Set<InvigilatorRegistration> existingRegistrations, Set<ExamSlot> newExamSlots) {
        for (InvigilatorRegistration registration : existingRegistrations) {
            ExamSlot existingSlot = registration.getExamSlot();
            for (ExamSlot newSlot : newExamSlots) {
                if (existingSlot.getId().intValue() == newSlot.getId().intValue()) {
                    throw new CustomMessageException(HttpStatus.CONFLICT, "Exam slot ID " + existingSlot.getId() + " already registered.");
                } else if (isOverlapping(existingSlot, newSlot)) {
                    throw new CustomMessageException(HttpStatus.CONFLICT, "Exam slot ID " + existingSlot.getId() + " overlaps with another existing slot in the registration list.");
                }
            }
        }
    }

    private boolean isOverlapping(ExamSlot slot1, ExamSlot slot2) {
        return !slot1.getEndAt().isBefore(slot2.getStartAt()) && !slot2.getEndAt().isBefore(slot1.getStartAt());
    }

    private int allowedSlot(Semester semester) {
        return Integer.parseInt(configService.getConfigBySemesterIdAndConfigType(semester.getId(), ALLOWED_SLOT.getValue()).getValue());
    }

    private InvigilatorRegistrationResponseDTO registerExamSlot(User invigilator, InvigilatorRegistrationRequestDTO request) {

        Set<Integer> requestExamSlotId = request.getExamSlotId();

        validateExamSlotId(requestExamSlotId);

        ExamSlot representativeExamSlot = findRepresentativeExamSlot(requestExamSlotId);

        Semester semester = representativeExamSlot.getSubjectExam().getSubjectId().getSemesterId();

        int allowedSlot = configurationHolder.getAllowedSlot();
        if (requestExamSlotId.size() > allowedSlot) {
            throw new CustomException(ErrorCode.EXCEEDED_ALLOWED_SLOT);
        }

        Set<ExamSlotDetail> slotDetails = validateAndGetNonOverlappingExamSlotsDetail(invigilator, semester, requestExamSlotId);

        Set<InvigilatorRegistration> registrations = createRegistrations(invigilator, requestExamSlotId);

        invigilatorRegistrationRepository.saveAll(registrations);

        return createResponseDTO(invigilator, semester, slotDetails);
    }

    @Transactional(rollbackFor = Exception.class)
    protected Set<ExamSlotDetail> deleteSelectedRegisteredSlots(User invigilator, Set<Integer> request) {
        log.info("request: {}", request.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        log.info("invigilator: {}", invigilator.getFuId());
        Set<InvigilatorRegistration> registrations = invigilatorRegistrationRepository
                .findByInvigilatorFuIdAndExamSlotIdsWithDetails(invigilator.getFuId(), request);
        log.info("registrations: {}", registrations);
        if (registrations.isEmpty()) {
            throw new CustomException(ErrorCode.NO_REGISTRATION_FOUND);
        }

        try {
            invigilatorRegistrationRepository.deleteAll(registrations);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.DELETE_REGISTRATIONS_FAILED);
        }

        return invigilatorRegistrationMapper
                .mapExamSlotDetails(registrations
                        .stream().map(InvigilatorRegistration::getExamSlot)
                        .collect(Collectors.toSet()));
    }
}
