package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.InvigilatorAssignmentMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorAssignmentRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAssignmentResponseDTO;
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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvigilatorAssignmentServiceImpl implements InvigilatorAssignmentService {

    InvigilatorAssignmentRepository invigilatorRegistrationRepository;
    ExamSlotRepository examSlotRepository;
    UserRepository userRepository;
    InvigilatorAssignmentMapper invigilatorAssignmentMapper;

    public InvigilatorAssignmentResponseDTO registerAnExamSlot(InvigilatorAssignmentRequestDTO request){
        User invigilator = userRepository.findByFuId(request.getFuId());
        ExamSlot examSlot = examSlotRepository.findById(request.getExamSlotId()).orElseThrow(
                () -> new InvigilatorAssignException(ErrorCode.EXAM_SLOT_NOT_FOUND)
        );

        if (isExamSlotOverlapping(invigilator, examSlot)) {
            throw new InvigilatorAssignException(ErrorCode.OVERLAP_SLOT);
        }

        InvigilatorAssignment invigilatorAssignment = InvigilatorAssignment.builder()
                .invigilator(invigilator)
                .examSlot(examSlot)
                .role(InvigilatorRoleEnum.IN_ROOM_INVIGILATOR.name())
                .build();
        System.out.println("invigilator: " + invigilatorAssignment.getInvigilator().getFuId() +
                            "\nexamSlot: " + invigilatorAssignment.getExamSlot().getId() +
                            "\nrole: " + invigilatorAssignment.getRole());
        invigilatorRegistrationRepository.save(invigilatorAssignment);
        return invigilatorAssignmentMapper.toInvigilatorAssignmentResponseDTO(invigilatorAssignment);
    }

    private boolean isExamSlotOverlapping(User invigilator, ExamSlot newExamSlot){
        Semester semester = newExamSlot.getSubjectExam().getSubjectId().getSemesterId();
        String examType = newExamSlot.getSubjectExam().getExamType();

        List<InvigilatorAssignment> existingAssignments = invigilatorRegistrationRepository
                .findByInvigilatorAndExamSlot_SubjectExam_SubjectId_SemesterIdAndExamSlot_SubjectExam_ExamType(invigilator, semester, examType);

        for (InvigilatorAssignment assignment : existingAssignments) {
            ExamSlot existingSlot = assignment.getExamSlot();
            if (isOverlapping(existingSlot, newExamSlot)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOverlapping(ExamSlot slot1, ExamSlot slot2){
        System.out.println("Slot1: " + slot1.getStartAt() + " | " + slot1.getEndAt());
        System.out.println("Slot2: " + slot2.getStartAt() + " | " + slot2.getEndAt());
        return !slot1.getEndAt().isBefore(slot2.getStartAt()) && !slot2.getEndAt().isBefore(slot1.getStartAt());
    }

}
