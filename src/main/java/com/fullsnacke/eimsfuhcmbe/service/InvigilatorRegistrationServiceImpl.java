package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.InvigilatorRegistrationMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorRegistrationRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.RegisterdSlotWithSemesterAndInvigilatorRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.*;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorRegistration;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.enums.ExamSlotRegisterStatusEnum;
import com.fullsnacke.eimsfuhcmbe.exception.AuthenticationProcessException;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import com.fullsnacke.eimsfuhcmbe.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import static com.fullsnacke.eimsfuhcmbe.enums.ConfigType.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvigilatorRegistrationServiceImpl implements InvigilatorRegistrationService {

    InvigilatorRegistrationRepository invigilatorRegistrationRepository;
    ExamSlotRepository examSlotRepository;
    UserRepository userRepository;
    SemesterRepository semesterRepository;
    ConfigService configService;
    InvigilatorRegistrationMapper invigilatorRegistrationMapper;

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRegisteredSlotsBySemester(RegisterdSlotWithSemesterAndInvigilatorRequestDTO request) {
        Semester semester = getSemesterById(request.getSemesterId());
        Set<InvigilatorRegistration> registrations = invigilatorRegistrationRepository
                .findByInvigilatorAndExamSlot_SubjectExam_SubjectId_SemesterId(
                        findInvigilatorByFuId(request.getFuId()), semester);
        invigilatorRegistrationRepository.deleteAll(registrations);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Set<ExamSlotDetail> deleteCurrentInvigilatorRegisteredSlotByExamSlotId(Set<Integer> request) {
        User currentInvigilator = getCurrentUser();
        return deleteSelectedRegisteredSlots(currentInvigilator, request);
    }

//    @Transactional(rollbackFor = Exception.class)
//    public Set<ExamSlotDetail> deleteRegisteredSlotByExamSlotId(InvigilatorRegistrationRequestDTO request) {
//        User invigilator = findInvigilatorByFuId(request.getFuId());
//        return deleteSelectedRegisteredSlots(invigilator, request);
//    }

    @Transactional(rollbackFor = Exception.class)
    protected Set<ExamSlotDetail> deleteSelectedRegisteredSlots(User invigilator, Set<Integer> request) {
        System.out.println("request: " + request.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        System.out.println(invigilator.getFuId());
        Set<InvigilatorRegistration> registrations = invigilatorRegistrationRepository
                .findByInvigilator_FuIdAndExamSlot_IdIn(invigilator.getFuId(), request);
        System.out.println("registrations: " + registrations);
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

    @Transactional(rollbackFor = Exception.class)
    public InvigilatorRegistrationResponseDTO registerExamSlotWithoutFuId(InvigilatorRegistrationRequestDTO request) {
        User invigilator = getCurrentUser();
        return registerExamSlot(invigilator, request);
    }

    @Transactional(rollbackFor = Exception.class)
    public InvigilatorRegistrationResponseDTO registerExamSlotWithFuId(InvigilatorRegistrationRequestDTO request) {
        User invigilator = findInvigilatorByFuId(request.getFuId());
        return registerExamSlot(invigilator, request);
    }

    private InvigilatorRegistrationResponseDTO registerExamSlot(User invigilator, InvigilatorRegistrationRequestDTO request) {

        Set<Integer> requestExamSlotId = request.getExamSlotId();

        validateExamSlotId(requestExamSlotId);

        ExamSlot representativeExamSlot = findRepresentativeExamSlot(requestExamSlotId);

        Semester semester = representativeExamSlot.getSubjectExam().getSubjectId().getSemesterId();

        int allowedSlot = Integer.parseInt(configService.getConfigBySemesterIdAndConfigType(semester.getId(), ALLOWED_SLOT.getValue()).getValue());

        if (requestExamSlotId.size() > allowedSlot) {
            throw new CustomException(ErrorCode.EXCEEDED_ALLOWED_SLOT);
        }

        Set<ExamSlotDetail> slotDetails = checkForOverlappingSlots(invigilator, semester, requestExamSlotId);

        Set<InvigilatorRegistration> registrations = createRegistrations(invigilator, requestExamSlotId);

        invigilatorRegistrationRepository.saveAll(registrations);

        return createResponseDTO(invigilator, semester, slotDetails);
    }

    @Transactional(rollbackFor = Exception.class)
    public RegisteredExamInvigilationResponseDTO getAllCurrentInvigilatorRegisteredSlots() {
        User currentUser = getCurrentUser();
        return getAllRegisteredSlotsByInvigilator(currentUser.getFuId());
    }

    @Transactional(rollbackFor = Exception.class)
    public RegisteredExamInvigilationResponseDTO getAllRegisteredSlotsByInvigilator(String fuId) {
        User invigilator = findInvigilatorByFuId(fuId);

        Set<InvigilatorRegistration> registrations = invigilatorRegistrationRepository.findByInvigilator(invigilator);

        List<SemesterInvigilatorRegistrationResponseDTO> semesterInvigilatorRegistrationList = new ArrayList<>();

        Map<Integer, Map<String, Set<ExamSlotDetail>>> groupedRegistrations = new HashMap<>();

        for (InvigilatorRegistration registration : registrations) {
            Semester semester = registration.getExamSlot().getSubjectExam().getSubjectId().getSemesterId();

            ExamSlotDetail detail = invigilatorRegistrationMapper.toExamSlotDetail(registration.getExamSlot());

            groupedRegistrations
                    .computeIfAbsent(semester.getId(), k -> new HashMap<>())
                    .computeIfAbsent(semester.getName(), k -> new HashSet<>())
                    .add(detail);
        }

        for (Map.Entry<Integer, Map<String, Set<ExamSlotDetail>>> semesterIdEntry : groupedRegistrations.entrySet()) {
            for (Map.Entry<String, Set<ExamSlotDetail>> semesterNameEntry : semesterIdEntry.getValue().entrySet()) {
                SemesterInvigilatorRegistrationResponseDTO semesterInvigilatorRegistrationResponseDTO = SemesterInvigilatorRegistrationResponseDTO.builder()
                        .semesterId(semesterIdEntry.getKey())
                        .semesterName(semesterNameEntry.getKey())
                        .examSlotDetailSet(semesterNameEntry.getValue())
                        .build();
                semesterInvigilatorRegistrationList.add(semesterInvigilatorRegistrationResponseDTO);
            }
        }

        return RegisteredExamInvigilationResponseDTO.builder()
                .fuId(fuId)
                .semesterInvigilatorRegistration(semesterInvigilatorRegistrationList)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public RegisteredExamInvigilationResponseDTO getAllCurrentInvigilatorRegisteredSlotsInSemester(int semesterId) {
        User currentUser = getCurrentUser();
        return getAllRegisteredSlotsInSemesterByInvigilator(semesterId, currentUser.getFuId());
    }

    @Transactional(rollbackFor = Exception.class)
    public RegisteredExamInvigilationResponseDTO getAllRegisteredSlotsInSemesterByInvigilator(int semesterId, String fuId) {
        User invigilator = findInvigilatorByFuId(fuId);
        Semester semester = getSemesterById(semesterId);

        Set<ExamSlot> examSlots = invigilatorRegistrationRepository
                .findByInvigilatorAndExamSlot_SubjectExam_SubjectId_SemesterId(invigilator, semester)
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

    @Transactional(rollbackFor = Exception.class)
    public InvigilatorRegistrationResponseDTO updateRegisterExamSlot(InvigilatorRegistrationRequestDTO request) {
        User invigilator = findInvigilatorByFuId(request.getFuId());

        Set<Integer> requestExamSlotId = request.getExamSlotId();

        validateExamSlotId(request.getExamSlotId());

        ExamSlot representativeExamSlot = findRepresentativeExamSlot(requestExamSlotId);

        Semester semester = representativeExamSlot.getSubjectExam().getSubjectId().getSemesterId();

        deleteExistingRegistration(invigilator, semester);

        Set<ExamSlotDetail> slotDetails = checkForOverlappingSlots(invigilator, semester, requestExamSlotId);

        Set<InvigilatorRegistration> registrations = createRegistrations(invigilator, requestExamSlotId);

        invigilatorRegistrationRepository.saveAll(registrations);

        return createResponseDTO(invigilator, semester, slotDetails);
    }

    public Set<RegisteredExamBySemesterResponseDTO> getRegisteredExamBySemester(int semesterId) {

        Semester semester = getSemesterById(semesterId);

        Set<InvigilatorRegistration> registrations = invigilatorRegistrationRepository
                .findByExamSlot_SubjectExam_SubjectId_SemesterId(semester);

        Map<String, RegisteredExamBySemesterResponseDTO> registeredExamBySemesterMap = new HashMap<>();

        for (InvigilatorRegistration registration : registrations) {
            String fuId = registration.getInvigilator().getFuId();
            RegisteredExamBySemesterResponseDTO registeredExamBySemester = registeredExamBySemesterMap.get(fuId);
            if (registeredExamBySemester == null) {
                registeredExamBySemester = RegisteredExamBySemesterResponseDTO.builder()
                        .fuId(fuId)
                        .examSlotDetails(new HashSet<>())
                        .build();
                registeredExamBySemesterMap.put(fuId, registeredExamBySemester);
            }
            registeredExamBySemester
                    .getExamSlotDetails()
                    .add(invigilatorRegistrationMapper.toExamSlotDetail(registration.getExamSlot()));
        }

        return new HashSet<>(registeredExamBySemesterMap.values());
    }

    public ListInvigilatorsByExamSlotResponseDTO listInvigilatorsByExamSlot(int examSlotId) {
        ExamSlot examSlot = examSlotRepository.findById(examSlotId)
                .orElseThrow(() -> new CustomException(ErrorCode.EXAM_SLOT_NOT_FOUND));

        Set<InvigilatorRegistration> registrations = invigilatorRegistrationRepository.findByExamSlot(examSlot);

        ListInvigilatorsByExamSlotResponseDTO response = invigilatorRegistrationMapper.toListInvigilatorsByExamSlotResponseDTO(examSlot);
        response.setUserResponseDTOSet(invigilatorRegistrationMapper.mapInvigilatorRegistrations(registrations));
        return response;
    }

    public RegisteredExamBySemesterResponseDTO getAllExamSlotsInSemesterWithStatus(int semesterId) {
        User currentUser = getCurrentUser();
        Semester semester = getSemesterById(semesterId);

        // Chuyển đổi allExamSlots từ List sang Set
        Set<ExamSlot> allExamSlots = new HashSet<>(examSlotRepository.findExamSlotBySubjectExam_SubjectId_SemesterId(semester));

        Set<InvigilatorRegistration> registeredSlots = invigilatorRegistrationRepository.findByInvigilatorAndExamSlot_SubjectExam_SubjectId_SemesterId(currentUser, semester);

        // Tạo một Set để lưu trữ kết quả cuối cùng
        Set<ExamSlotDetail> examSlotDetails = new HashSet<>();
        for (ExamSlot examSlot : allExamSlots) {
            String status;
            long count = registeredSlots.stream()
                    .filter(registration -> registration.getExamSlot().equals(examSlot))
                    .count();

            if (registeredSlots.stream().anyMatch(registration -> registration.getExamSlot().equals(examSlot))) {
                status = ExamSlotRegisterStatusEnum.REGISTERED.name();
            } else if (examSlot.getRequiredInvigilators() != 0 && count <= examSlot.getRequiredInvigilators()) {
                status = ExamSlotRegisterStatusEnum.NOT_FULL.name();
            } else {
                status = ExamSlotRegisterStatusEnum.FULL.name();
            }
            ExamSlotDetail examSlotDetail = invigilatorRegistrationMapper.toExamSlotDetail(examSlot);
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
        return examSlotRepository.findById(examSlotIds.iterator().next())
                .orElseThrow(() -> new CustomException(ErrorCode.EXAM_SLOT_NOT_FOUND));
    }

    private Set<ExamSlotDetail> checkForOverlappingSlots(User invigilator, Semester semester, Set<Integer> examSlots) {

        Set<ExamSlotDetail> examSlotDetails = isAnyExamSlotOverlapping(invigilator, semester, examSlots);
        if (examSlotDetails == null) {
            throw new CustomException(ErrorCode.OVERLAP_SLOT);
        }
        return examSlotDetails;
    }

    private Set<InvigilatorRegistration> createRegistrations(User invigilator, Set<Integer> examSlotIds) {
        return examSlotIds.stream()
                .map(examSlotId -> examSlotRepository.findById(examSlotId)
                        .orElseThrow(() -> new CustomException(ErrorCode.EXAM_SLOT_NOT_FOUND)))
                .map(examSlot -> InvigilatorRegistration.builder()
                        .invigilator(invigilator)
                        .examSlot(examSlot)
//                        .createdAt(Instant.now())
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

    private void deleteExistingRegistration(User invigilator, Semester semester) {
        Set<InvigilatorRegistration> existingRegistrations = invigilatorRegistrationRepository
                .findByInvigilatorAndExamSlot_SubjectExam_SubjectId_SemesterId(invigilator, semester);
        invigilatorRegistrationRepository.deleteAll(existingRegistrations);
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

    private Set<ExamSlotDetail> isAnyExamSlotOverlapping(User invigilator, Semester semester, Set<Integer> examSlotIds) {
        //Lấy ra các examSlot đã được đăng ký trước đó của invigilator hiện tại
        Set<InvigilatorRegistration> existingRegistrations =
                invigilatorRegistrationRepository.findRegistrationsWithDetailsByInvigilatorAndSemester(
                        invigilator.getId(), semester.getId());

        if (existingRegistrations.size() + examSlotIds.size() > allowedSlot(semester)) {
            throw new CustomException(ErrorCode.EXCEEDED_ALLOWED_SLOT);
        }
        //Lấy ra ExamSlot của tất cả các examSlotId cần được check và add vô db
        Set<ExamSlot> newExamSlots = new HashSet<>(examSlotRepository.findAllById(examSlotIds));
        Set<ExamSlotDetail> examSlotDetails = new HashSet<>();

        for (ExamSlot newSlot : newExamSlots) {
            if (newSlot.getRequiredInvigilators() <= 0) {
                throw new CustomException(ErrorCode.EXAM_SLOT_FULL);
            }
            for (ExamSlot otherSlot : newExamSlots) {
                if (newSlot.getId().intValue() != otherSlot.getId().intValue() && isOverlapping(newSlot, otherSlot)) {
                    throw new CustomException(ErrorCode.OVERLAP_SLOT_IN_LIST);
                }
            }
            examSlotDetails.add(invigilatorRegistrationMapper.toExamSlotDetail(newSlot));
        }

        //Check overlap in existing registrations
        for (InvigilatorRegistration registration : existingRegistrations) {
            ExamSlot existingSlot = registration.getExamSlot();
            for (ExamSlot newSlot : newExamSlots) {
                if (existingSlot.getId().intValue() == newSlot.getId().intValue()) {
                    throw new CustomException(ErrorCode.EXAM_SLOT_ALREADY_REGISTERED);
                } else if (isOverlapping(existingSlot, newSlot)) {
                    return null;
                }
            }
        }
        return examSlotDetails;
    }

    private boolean isOverlapping(ExamSlot slot1, ExamSlot slot2) {
        return !slot1.getEndAt().isBefore(slot2.getStartAt()) && !slot2.getEndAt().isBefore(slot1.getStartAt());
    }

    private int allowedSlot(Semester semester) {
        return Integer.parseInt(configService.getConfigBySemesterIdAndConfigType(semester.getId(), ALLOWED_SLOT.getValue()).getValue());
    }
}
