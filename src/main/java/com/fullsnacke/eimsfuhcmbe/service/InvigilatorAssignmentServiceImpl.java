package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorAssignmentRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAssignmentResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotRepository;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAssignmentRepository;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvigilatorAssignmentServiceImpl implements InvigilatorAssignmentService {

    InvigilatorAssignmentRepository invigilatorRegistrationRepository;
    ExamSlotRepository examSlotRepository;
    UserRepository userRepository;

    public InvigilatorAssignmentResponseDTO registerAnExamSlot(InvigilatorAssignmentRequestDTO request){
        User invigilator = userRepository.findByFuId(request.getFuId());
        ExamSlot examSlot = examSlotRepository.findById(request.getExamSlotId()).orElseThrow(
                () -> new IllegalArgumentException("Exam slot not found")
        );
        InvigilatorAssignment invigilatorAssignment = InvigilatorAssignment.builder()
                .invigilator(invigilator)
                .examSlot(examSlot)
                .build();

        return ;
    }

}
