package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.InvigilatorAssignmentMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorAssignmentRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotDetail;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAssignmentResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RegisteredExamInvigilationResponseDTO;
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
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvigilatorAssignmentServiceImpl implements InvigilatorAssignmentService {

    InvigilatorAssignmentRepository invigilatorRegistrationRepository;
    ExamSlotRepository examSlotRepository;
    UserRepository userRepository;


    public InvigilatorAssignmentResponseDTO registerExamSlot(InvigilatorAssignmentRequestDTO request) {
        User invigilator = userRepository.findByFuId(request.getFuId());
        if (invigilator == null) {
            throw new InvigilatorAssignException(ErrorCode.USER_NOT_FOUND);
        }

        Set<Integer> requestExamSlotId = request.getExamSlotId();

        validateExamSlotId(requestExamSlotId);

        ExamSlot representativeExamSlot = findRepresentativeExamSlot(requestExamSlotId);

        Semester semester = representativeExamSlot.getSubjectExam().getSubjectId().getSemesterId();
        String examType = representativeExamSlot.getSubjectExam().getExamType();

        checkForOverlappingSlots(invigilator, semester, examType, requestExamSlotId);

        Set<InvigilatorAssignment> assignments = createAssignments(invigilator, requestExamSlotId);

        invigilatorRegistrationRepository.saveAll(assignments);

        return createResponseDTO(invigilator, examType, semester, requestExamSlotId);
    }

    public RegisteredExamInvigilationResponseDTO getAllRegisteredSlot() {
        User currentUser = getCurrentUser();

        Set<InvigilatorAssignment> assignments = invigilatorRegistrationRepository.findByInvigilator(currentUser);

        Map<String, Map<String, Set<ExamSlotDetail>>> groupedAssignments = new HashMap<>();

        for (InvigilatorAssignment assignment : assignments) {
            String semester = assignment.getExamSlot().getSubjectExam().getSubjectId().getSemesterId().getName();
            String examType = assignment.getExamSlot().getSubjectExam().getExamType();

            ExamSlotDetail detail = ExamSlotDetail.builder()
                    .examSlotId(assignment.getExamSlot().getId())
                    .startAt(assignment.getExamSlot().getStartAt())
                    .endAt(assignment.getExamSlot().getEndAt())
                    .build();

            groupedAssignments
                    .computeIfAbsent(semester, k -> new HashMap<>())
                    .computeIfAbsent(examType, k -> new HashSet<>())
                    .add(detail);
        }
        RegisteredExamInvigilationResponseDTO responseDTO = new RegisteredExamInvigilationResponseDTO();
        for (Map.Entry<String, Map<String, Set<ExamSlotDetail>>> semesterEntry : groupedAssignments.entrySet()) {
            for (Map.Entry<String, Set<ExamSlotDetail>> examTypeEntry : semesterEntry.getValue().entrySet()) {
                return RegisteredExamInvigilationResponseDTO.builder()
                        .semester(semesterEntry.getKey())
                        .examType(examTypeEntry.getKey())
                        .examSlots(examTypeEntry.getValue())
                        .build();
            }
        }

        return responseDTO;



//         assignments.stream()
//                .map(assignment -> RegisteredExamInvigilationResponseDTO.builder()
//                        .examSlotId(assignment.getExamSlot().getId())
//                        .startAt(assignment.getExamSlot().getStartAt())
//                        .endAt(assignment.getExamSlot().getEndAt())
//                        .build())
//                .collect(Collectors.toSet());
//        return invigilatorAssignmentMapper.toRegisteredExamInvigilationDTO()
//         invigilatorRegistrationRepository.findByInvigilator(user).stream()
//                .map(invigilatorAssignmentMapper::toRegisteredExamInvigilationDTO)
//                .collect(Collectors.toSet());
    }

    public InvigilatorAssignmentResponseDTO updateRegisterExamSlot(InvigilatorAssignmentRequestDTO request) {
        User invigilator = userRepository.findByFuId(request.getFuId());
        if (invigilator == null) {
            throw new InvigilatorAssignException(ErrorCode.USER_NOT_FOUND);
        }

        Set<Integer> requestExamSlotId = request.getExamSlotId();

        validateExamSlotId(request.getExamSlotId());

        ExamSlot representativeExamSlot = findRepresentativeExamSlot(requestExamSlotId);

        Semester semester = representativeExamSlot.getSubjectExam().getSubjectId().getSemesterId();
        String examType = representativeExamSlot.getSubjectExam().getExamType();

        deleteExistingAssignments(invigilator, semester, examType);

        checkForOverlappingSlots(invigilator, semester, examType, requestExamSlotId);

        Set<InvigilatorAssignment> assignments = createAssignments(invigilator, requestExamSlotId);
        System.out.println(assignments);

        invigilatorRegistrationRepository.saveAll(assignments);

        return createResponseDTO(invigilator, examType, semester, requestExamSlotId);
    }

    private User findInvigilatorByFuId(String fuId) {
        User invigilator = userRepository.findByFuId(fuId);
        if (invigilator == null) {
            throw new InvigilatorAssignException(ErrorCode.USER_NOT_FOUND);
        }
        return invigilator;
    }

    private void validateExamSlotId(Set<Integer> examSlotIds){
        if (examSlotIds == null || examSlotIds.isEmpty()) {
            throw new InvigilatorAssignException(ErrorCode.EXAM_SLOT_SET_EMPTY);
        }
    }

    private ExamSlot findRepresentativeExamSlot(Set<Integer> examSlotIds) {
        return examSlotRepository.findById(examSlotIds.iterator().next())
                .orElseThrow(() -> new InvigilatorAssignException(ErrorCode.EXAM_SLOT_NOT_FOUND));
    }

    private void checkForOverlappingSlots(User invigilator, Semester semester, String examType, Set<Integer> examSlotIds) {
        if (isAnyExamSlotOverlapping(invigilator, semester, examType, examSlotIds)) {
            throw new InvigilatorAssignException(ErrorCode.OVERLAP_SLOT);
        }
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

    private InvigilatorAssignmentResponseDTO createResponseDTO(User invigilator, String examType, Semester semester, Set<Integer> examSlotIds) {
        return InvigilatorAssignmentResponseDTO.builder()
                .examType(examType)
                .semesterId(semester.getId())
                .fuId(invigilator.getFuId())
                .examSlotId(examSlotIds)
                .build();
    }

    private void deleteExistingAssignments(User invigilator, Semester semester, String examType) {
        List<InvigilatorAssignment> existingAssignments = invigilatorRegistrationRepository
                .findByInvigilatorAndExamSlot_SubjectExam_SubjectId_SemesterIdAndExamSlot_SubjectExam_ExamType(invigilator, semester, examType);
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

    private boolean isAnyExamSlotOverlapping(User invigilator, Semester semester, String examType, Set<Integer> examSlotIds) {
        //Lấy ra các examSlot đã được đăng ký trước đó của invigilator hiện tại
        List<InvigilatorAssignment> existingAssignments = invigilatorRegistrationRepository
                .findByInvigilatorAndExamSlot_SubjectExam_SubjectId_SemesterIdAndExamSlot_SubjectExam_ExamType(invigilator, semester, examType);

        //Lấy ra ExamSlot của tất cả các examSlotId cần được check và add vô db
        Set<ExamSlot> newExamSlots = new HashSet<>(examSlotRepository.findAllById(examSlotIds));

        //Check overlap
        for (InvigilatorAssignment assignment : existingAssignments) {
            ExamSlot existingSlot = assignment.getExamSlot();
            for (ExamSlot newSlot : newExamSlots) {
                if (isOverlapping(existingSlot, newSlot)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isOverlapping(ExamSlot slot1, ExamSlot slot2) {
        System.out.println("Slot1: " + slot1.getStartAt() + " | " + slot1.getEndAt());
        System.out.println("Slot2: " + slot2.getStartAt() + " | " + slot2.getEndAt());
        return !slot1.getEndAt().isBefore(slot2.getStartAt()) && !slot2.getEndAt().isBefore(slot1.getStartAt());
    }
}
