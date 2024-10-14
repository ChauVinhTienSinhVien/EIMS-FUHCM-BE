package com.fullsnacke.eimsfuhcmbe.dto.mapper;

import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorRegistration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvigilatorAssignmentMapper {
//    @Mapping(target = "invigilatorRegistration", source = "registration")
//    InvigilatorAssignment toEntity(InvigilatorRegistration registration);
}
