package com.fullsnacke.eimsfuhcmbe.dto.mapper;


import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAssignmentResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RegisteredExamInvigilationResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface InvigilatorAssignmentMapper {

    //    @Mapping(source = "invigilator.fuId", target = "fuId")
//    @Mapping(target = "examSlotId", ignore = true)
//    @Mapping(source = "examSlot.subjectExam.examType", target = "examType")
//    @Mapping(source = "examSlot.subjectExam.subjectId.semesterId.id", target = "semesterId")
//    InvigilatorAssignmentResponseDTO toInvigilatorAssignmentResponseDTO (InvigilatorAssignment invigilatorAssignment);
//
//    @Mapping(source = "examSlot.subjectExam.subjectId.semesterId", target = "semester")
//    @Mapping(target = "examSlots", ignore = true)
//    RegisteredExamInvigilationResponseDTO toRegisteredExamInvigilationDTO(InvigilatorAssignment invigilatorAssignment);
}
