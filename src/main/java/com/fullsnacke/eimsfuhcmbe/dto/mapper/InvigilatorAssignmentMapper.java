package com.fullsnacke.eimsfuhcmbe.dto.mapper;


import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAssignmentResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvigilatorAssignmentMapper {

    @Mapping(source = "id", target = "invigilatorAssignmentId")
    @Mapping(source = "invigilator.fuId", target = "fuId")
    @Mapping(source = "examSlot.id", target = "examSlotId")
    InvigilatorAssignmentResponseDTO toInvigilatorAssignmentResponseDTO (InvigilatorAssignment invigilatorAssignment);
}
