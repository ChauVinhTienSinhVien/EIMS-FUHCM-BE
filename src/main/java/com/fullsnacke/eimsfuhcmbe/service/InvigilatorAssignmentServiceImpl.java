package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.InvigilatorAssignmentMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorAssignmentRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAssignmentResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RegisteredExamInvigilationResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.enums.InvigilatorRoleEnum;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvigilatorAssignmentServiceImpl implements InvigilatorAssignmentService {

    InvigilatorAssignmentRepository invigilatorRegistrationRepository;
    ExamSlotRepository examSlotRepository;
    UserRepository userRepository;
    InvigilatorAssignmentMapper invigilatorAssignmentMapper;


    public InvigilatorAssignmentResponseDTO registerExamSlot(InvigilatorAssignmentRequestDTO request){
        User invigilator = userRepository.findByFuId(request.getFuId());

        if(invigilator == null){
            throw new InvigilatorAssignException(ErrorCode.USER_NOT_FOUND);
        }

        if(request.getExamSlotId() == null || request.getExamSlotId().isEmpty()) {
            throw new InvigilatorAssignException(ErrorCode.EXAM_SLOT_SET_EMPTY);
        }

        ExamSlot representativeExamSlot = examSlotRepository.findById(request.getExamSlotId().iterator().next())
                .orElseThrow(() -> new InvigilatorAssignException(ErrorCode.EXAM_SLOT_NOT_FOUND)
                );

        Semester semester = representativeExamSlot.getSubjectExam().getSubjectId().getSemesterId();
        String examType = representativeExamSlot.getSubjectExam().getExamType();

        if (isAnyExamSlotOverlapping(invigilator, semester, examType, request.getExamSlotId())) {
            throw new InvigilatorAssignException(ErrorCode.OVERLAP_SLOT);
        }

        Set<InvigilatorAssignment> assignments = new HashSet<>();
        for (Integer examSlotId : request.getExamSlotId()) {
            ExamSlot examSlot = examSlotRepository.findById(examSlotId)
                    .orElseThrow(() -> new InvigilatorAssignException(ErrorCode.EXAM_SLOT_NOT_FOUND));

            InvigilatorAssignment assignment = InvigilatorAssignment.builder()
                    .invigilator(invigilator)
                    .examSlot(examSlot)
                    .role(InvigilatorRoleEnum.IN_ROOM_INVIGILATOR.name())
                    .build();
            assignments.add(assignment);
        }

        invigilatorRegistrationRepository.saveAll(assignments);

        return InvigilatorAssignmentResponseDTO.builder()
                .examType(examType)
                .semesterId(semester.getId())
                .fuId(invigilator.getFuId())
                .examSlotId(request.getExamSlotId())
                .build();
    }

    private boolean isAnyExamSlotOverlapping(User invigilator, Semester semester, String examType, Set<Integer> examSlotIds){
        //Lấy ra các examSlot đã được đăng ký trước đó của invigilator hiện tại
        List<InvigilatorAssignment> existingAssignments = invigilatorRegistrationRepository
                .findByInvigilatorAndExamSlot_SubjectExam_SubjectId_SemesterIdAndExamSlot_SubjectExam_ExamType(invigilator, semester, examType);

        //Lấy ra ExamSlot của tất cả các examSlotId cần được check và add vô db
        Set<ExamSlot> newExamSlots = new HashSet<>(examSlotRepository.findAllById(examSlotIds));

        //Check overlap
        for(InvigilatorAssignment assignment : existingAssignments){
            ExamSlot existingSlot = assignment.getExamSlot();
            for (ExamSlot newSlot : newExamSlots){
                if(isOverlapping(existingSlot, newSlot)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isOverlapping(ExamSlot slot1, ExamSlot slot2){
        System.out.println("Slot1: " + slot1.getStartAt() + " | " + slot1.getEndAt());
        System.out.println("Slot2: " + slot2.getStartAt() + " | " + slot2.getEndAt());
        return !slot1.getEndAt().isBefore(slot2.getStartAt()) && !slot2.getEndAt().isBefore(slot1.getStartAt());
    }

    public List<RegisteredExamInvigilationResponseDTO> getAllRegisteredSlot() {
        var context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String email = authentication.getName();
        System.out.println(context);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvigilatorAssignException(ErrorCode.USER_NOT_FOUND));

        List<InvigilatorAssignment> assignments = invigilatorRegistrationRepository.findByInvigilator(user);

        return assignments.stream()
                .map(assignment -> RegisteredExamInvigilationResponseDTO.builder()
                        .examSlotId(assignment.getExamSlot().getId())
                        .startAt(assignment.getExamSlot().getStartAt())
                        .endAt(assignment.getExamSlot().getEndAt())
                        .build())
                .collect(Collectors.toList());
    }

}
