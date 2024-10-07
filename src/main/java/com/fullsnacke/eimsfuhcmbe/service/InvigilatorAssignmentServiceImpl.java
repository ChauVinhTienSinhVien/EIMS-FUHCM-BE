package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorAssignmentRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.RegisterdSlotWithSemesterAndInvigilatorRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotDetail;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAssignmentResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RegisteredExamInvigilationResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.SemesterInvigilatorAssignmentResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.enums.InvigilatorRoleEnum;
import com.fullsnacke.eimsfuhcmbe.exception.AuthenticationProcessException;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.assignment.InvigilatorAssignException;
import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotRepository;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAssignmentRepository;
import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvigilatorAssignmentServiceImpl implements InvigilatorAssignmentService {

    InvigilatorAssignmentRepository invigilatorRegistrationRepository;
    ExamSlotRepository examSlotRepository;
    UserRepository userRepository;
    SemesterRepository semesterRepository;

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAssignmentBySemester(InvigilatorAssignmentRequestDTO request) {
        ExamSlot representativeExamSlot = findRepresentativeExamSlot(request.getExamSlotId());
        Set<InvigilatorAssignment> assignments = invigilatorRegistrationRepository
                .findByInvigilatorAndExamSlot_SubjectExam_SubjectId_SemesterId(
                        findInvigilatorByFuId(request.getFuId()),
                        examSlotRepository.findById(representativeExamSlot.getId()).get().getSubjectExam().getSubjectId().getSemesterId());
        invigilatorRegistrationRepository.deleteAll(assignments);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public InvigilatorAssignmentResponseDTO registerExamSlot(InvigilatorAssignmentRequestDTO request) {
        User invigilator = findInvigilatorByFuId(request.getFuId());

        Set<Integer> requestExamSlotId = request.getExamSlotId();

        validateExamSlotId(requestExamSlotId);

        ExamSlot representativeExamSlot = findRepresentativeExamSlot(requestExamSlotId);

        Semester semester = representativeExamSlot.getSubjectExam().getSubjectId().getSemesterId();

        Set<ExamSlotDetail> slotDetails = checkForOverlappingSlots(invigilator, semester, requestExamSlotId);

        Set<InvigilatorAssignment> assignments = createAssignments(invigilator, requestExamSlotId);

        invigilatorRegistrationRepository.saveAll(assignments);

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

        Set<InvigilatorAssignment> assignments = invigilatorRegistrationRepository.findByInvigilator(invigilator);

        List<SemesterInvigilatorAssignmentResponseDTO> semesterInvigilatorAssignmentList = new ArrayList<>();

        Map<Integer, Map<String, Set<ExamSlotDetail>>> groupedAssignments = new HashMap<>();

        for (InvigilatorAssignment assignment : assignments) {
            Semester semester = assignment.getExamSlot().getSubjectExam().getSubjectId().getSemesterId();

            ExamSlotDetail detail = ExamSlotDetail.builder()
                    .examSlotId(assignment.getExamSlot().getId())
                    .startAt(assignment.getExamSlot().getStartAt())
                    .endAt(assignment.getExamSlot().getEndAt())
                    .build();

            groupedAssignments
                    .computeIfAbsent(semester.getId(), k -> new HashMap<>())
                    .computeIfAbsent(semester.getName(), k -> new HashSet<>())
                    .add(detail);
        }

        for (Map.Entry<Integer, Map<String, Set<ExamSlotDetail>>> semesterIdEntry : groupedAssignments.entrySet()) {
            for (Map.Entry<String, Set<ExamSlotDetail>> semesterNameEntry : semesterIdEntry.getValue().entrySet()) {
                SemesterInvigilatorAssignmentResponseDTO semesterInvigilatorAssignmentResponseDTO = SemesterInvigilatorAssignmentResponseDTO.builder()
                        .semesterId(semesterIdEntry.getKey())
                        .semesterName(semesterNameEntry.getKey())
                        .examSlotDetailSet(semesterNameEntry.getValue())
                        .build();
                semesterInvigilatorAssignmentList.add(semesterInvigilatorAssignmentResponseDTO);
            }
        }

        return RegisteredExamInvigilationResponseDTO.builder()
                .fuId(fuId)
                .semesterInvigilatorAssignment(semesterInvigilatorAssignmentList)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public RegisteredExamInvigilationResponseDTO getAllCurrentInvigilatorRegisteredSlotsInSemester(int semesterId) {
        User currentUser = getCurrentUser();
        return getAllRegisteredSlotsInSemesterByInvigilator(RegisterdSlotWithSemesterAndInvigilatorRequestDTO.builder()
                .fuId(currentUser.getFuId())
                .semesterId(semesterId)
                .build());
    }

    @Transactional(rollbackFor = Exception.class)
    public RegisteredExamInvigilationResponseDTO getAllRegisteredSlotsInSemesterByInvigilator(RegisterdSlotWithSemesterAndInvigilatorRequestDTO request) {
        User invigilator = findInvigilatorByFuId(request.getFuId());
        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new InvigilatorAssignException(ErrorCode.SEMESTER_NOT_FOUND));

        Set<ExamSlot> examSlots = invigilatorRegistrationRepository
                .findByInvigilatorAndExamSlot_SubjectExam_SubjectId_SemesterId(invigilator, semester)
                .stream()
                .map(InvigilatorAssignment::getExamSlot)
                .collect(Collectors.toSet());

        SemesterInvigilatorAssignmentResponseDTO semesterInvigilatorAssignmentResponseDTO = SemesterInvigilatorAssignmentResponseDTO.builder()
                .semesterId(semester.getId())
                .semesterName(semester.getName())
                .examSlotDetailSet(examSlots.stream()
                        .map(examSlot -> ExamSlotDetail.builder()
                                .examSlotId(examSlot.getId())
                                .startAt(examSlot.getStartAt())
                                .endAt(examSlot.getEndAt())
                                .build())
                        .collect(Collectors.toSet()))
                .build();

        return RegisteredExamInvigilationResponseDTO.builder()
                .fuId(request.getFuId())
                .semesterInvigilatorAssignment(Collections.singletonList(semesterInvigilatorAssignmentResponseDTO))
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public InvigilatorAssignmentResponseDTO updateRegisterExamSlot(InvigilatorAssignmentRequestDTO request) {
        User invigilator = findInvigilatorByFuId(request.getFuId());

        Set<Integer> requestExamSlotId = request.getExamSlotId();

        validateExamSlotId(request.getExamSlotId());

        ExamSlot representativeExamSlot = findRepresentativeExamSlot(requestExamSlotId);

        Semester semester = representativeExamSlot.getSubjectExam().getSubjectId().getSemesterId();

        deleteExistingAssignments(invigilator, semester);

        Set<ExamSlotDetail> slotDetails = checkForOverlappingSlots(invigilator, semester, requestExamSlotId);

        Set<InvigilatorAssignment> assignments = createAssignments(invigilator, requestExamSlotId);

        invigilatorRegistrationRepository.saveAll(assignments);

        return createResponseDTO(invigilator, semester, slotDetails);
    }

    private User findInvigilatorByFuId(String fuId) {
        User invigilator = userRepository.findByFuId(fuId);
        if (invigilator == null) {
            throw new InvigilatorAssignException(ErrorCode.USER_NOT_FOUND);
        }
        return invigilator;
    }

    private void validateExamSlotId(Set<Integer> examSlotIds) {
        if (examSlotIds == null || examSlotIds.isEmpty()) {
            throw new InvigilatorAssignException(ErrorCode.EXAM_SLOT_SET_EMPTY);
        }
    }

    private ExamSlot findRepresentativeExamSlot(Set<Integer> examSlotIds) {
        return examSlotRepository.findById(examSlotIds.iterator().next())
                .orElseThrow(() -> new InvigilatorAssignException(ErrorCode.EXAM_SLOT_NOT_FOUND));
    }

    //    private void checkForOverlappingSlots(User invigilator, Semester semester, String examType, Set<Integer> examSlotIds) {
//        if (isAnyExamSlotOverlapping(invigilator, semester, examType, examSlotIds)) {
//            throw new InvigilatorAssignException(ErrorCode.OVERLAP_SLOT);
//        }
//    }

    private Set<ExamSlotDetail> checkForOverlappingSlots(User invigilator, Semester semester, Set<Integer> examSlots) {

        Set<ExamSlotDetail> examSlotDetails = isAnyExamSlotOverlapping(invigilator, semester, examSlots);
        if (examSlotDetails == null) {
            throw new InvigilatorAssignException(ErrorCode.OVERLAP_SLOT);
        }
        return examSlotDetails;
    }

    private Set<InvigilatorAssignment> createAssignments(User invigilator, Set<Integer> examSlotIds) {
//        Set<InvigilatorAssignment> assignments = new HashSet<>();
//        for (Integer examSlotId : examSlotIds) {
//            ExamSlot examSlot = examSlotRepository.findById(examSlotId)
//                    .orElseThrow(() -> new InvigilatorAssignException(ErrorCode.EXAM_SLOT_NOT_FOUND));
//
//            InvigilatorAssignment assignment = InvigilatorAssignment.builder()
//                    .invigilator(invigilator)
//                    .examSlot(examSlot)
//                    .role(InvigilatorRoleEnum.IN_ROOM_INVIGILATOR.name())
//                    .build();
//            assignments.add(assignment);
//        }
        return examSlotIds.stream()
                .map(examSlotId -> examSlotRepository.findById(examSlotId)
                        .orElseThrow(() -> new InvigilatorAssignException(ErrorCode.EXAM_SLOT_NOT_FOUND)))
                .map(examSlot -> InvigilatorAssignment.builder()
                        .invigilator(invigilator)
                        .examSlot(examSlot)
                        .role(InvigilatorRoleEnum.IN_ROOM_INVIGILATOR.name())
                        .build())
                .collect(Collectors.toSet());
    }

    private InvigilatorAssignmentResponseDTO createResponseDTO(User invigilator, Semester semester, Set<ExamSlotDetail> slotDetails) {
        return InvigilatorAssignmentResponseDTO.builder()
                .fuId(invigilator.getFuId())
                .semester(semester)
                .examSlots(slotDetails)
                .build();
    }

    private void deleteExistingAssignments(User invigilator, Semester semester) {
        Set<InvigilatorAssignment> existingAssignments = invigilatorRegistrationRepository
                .findByInvigilatorAndExamSlot_SubjectExam_SubjectId_SemesterId(invigilator, semester);
        invigilatorRegistrationRepository.deleteAll(existingAssignments);
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
                .orElseThrow(() -> new InvigilatorAssignException(ErrorCode.USER_NOT_FOUND));
    }

//    private boolean isAnyExamSlotOverlapping(User invigilator, Semester semester, String examType, Set<Integer> examSlotIds) {
//        //Lấy ra các examSlot đã được đăng ký trước đó của invigilator hiện tại
//        List<InvigilatorAssignment> existingAssignments = invigilatorRegistrationRepository
//                .findByInvigilatorAndExamSlot_SubjectExam_SubjectId_SemesterIdAndExamSlot_SubjectExam_ExamType(invigilator, semester, examType);
//
//        //Lấy ra ExamSlot của tất cả các examSlotId cần được check và add vô db
//        Set<ExamSlot> newExamSlots = new HashSet<>(examSlotRepository.findAllById(examSlotIds));
//
//        //Check overlap
//        for (InvigilatorAssignment assignment : existingAssignments) {
//            ExamSlot existingSlot = assignment.getExamSlot();
//            for (ExamSlot newSlot : newExamSlots) {
//                if (isOverlapping(existingSlot, newSlot)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    private Set<ExamSlotDetail> isAnyExamSlotOverlapping(User invigilator, Semester semester, Set<Integer> examSlotIds) {
        //Lấy ra các examSlot đã được đăng ký trước đó của invigilator hiện tại
        Set<InvigilatorAssignment> existingAssignments = invigilatorRegistrationRepository
                .findByInvigilatorAndExamSlot_SubjectExam_SubjectId_SemesterId(invigilator, semester);

        //Lấy ra ExamSlot của tất cả các examSlotId cần được check và add vô db
        Set<ExamSlot> newExamSlots = new HashSet<>(examSlotRepository.findAllById(examSlotIds));
        Set<ExamSlotDetail> examSlotDetails = new HashSet<>();

        for (ExamSlot newSlot : newExamSlots) {
            for (ExamSlot otherSlot : newExamSlots) {
                if (newSlot.getId().intValue() != otherSlot.getId().intValue() && isOverlapping(newSlot, otherSlot)) {
                    throw new InvigilatorAssignException(ErrorCode.OVERLAP_SLOT_IN_LIST);
                }
            }
            examSlotDetails.add(ExamSlotDetail.builder()
                    .examSlotId(newSlot.getId())
                    .startAt(newSlot.getStartAt())
                    .endAt(newSlot.getEndAt())
                    .build());
        }
        //Check overlap in existing assignments
        for (InvigilatorAssignment assignment : existingAssignments) {
            ExamSlot existingSlot = assignment.getExamSlot();
            for (ExamSlot newSlot : newExamSlots) {
                if (existingSlot.getId().intValue() == newSlot.getId().intValue()) {
                    throw new InvigilatorAssignException(ErrorCode.EXAM_SLOT_ALREADY_REGISTERED);
                } else if (isOverlapping(existingSlot, newSlot)) {
                    return null;
                }
            }
        }
        return examSlotDetails;
    }

    private boolean isOverlapping(ExamSlot slot1, ExamSlot slot2) {
        System.out.println("Slot1: " + slot1.getStartAt() + " | " + slot1.getEndAt());
        System.out.println("Slot2: " + slot2.getStartAt() + " | " + slot2.getEndAt());
        return !slot1.getEndAt().isBefore(slot2.getStartAt()) && !slot2.getEndAt().isBefore(slot1.getStartAt());
    }
}
