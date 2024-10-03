package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.InvigilatorAssignmentMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorAssignmentRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAssignmentResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.enums.InvigilatorRoleEnum;
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
                () -> new IllegalArgumentException("Exam slot not found")
        );
        InvigilatorAssignment invigilatorAssignment = InvigilatorAssignment.builder()
                .invigilator(invigilator)
                .examSlot(examSlot)
                .role(InvigilatorRoleEnum.IN_ROOM_INVIGILATOR.name())
                .build();
        invigilatorAssignment = invigilatorRegistrationRepository.save(invigilatorAssignment);
        return invigilatorAssignmentMapper.toInvigilatorAssignmentResponseDTO(invigilatorAssignment);
    }

    private boolean isExamSlotOverlapping(User invigilator, ExamSlot newExamSlot){
        List<InvigilatorAssignment> existingAssignments = invigilatorRegistrationRepository.findByInvigilator(invigilator);

        for (InvigilatorAssignment assignment : existingAssignments) {
            ExamSlot existingSlot = assignment.getExamSlot();
            if (isOverlapping(existingSlot, newExamSlot)) {
                return true;
            }
        }
        return false;

    }

    private boolean isOverlapping(ExamSlot slot1, ExamSlot slot2){
        return !slot1.getEndAt().isBefore(slot2.getStartAt()) && !slot2.getEndAt().isBefore(slot1.getStartAt());
    }

}
