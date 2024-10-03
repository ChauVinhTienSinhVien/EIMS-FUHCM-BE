package com.fullsnacke.eimsfuhcmbe.dto.mapper;


import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAssignmentResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvigilatorAssignmentMapper {

    InvigilatorAssignmentResponseDTO toInvigilatorAssignmentResponseDTO (InvigilatorAssignment invigilatorAssignment);
}
