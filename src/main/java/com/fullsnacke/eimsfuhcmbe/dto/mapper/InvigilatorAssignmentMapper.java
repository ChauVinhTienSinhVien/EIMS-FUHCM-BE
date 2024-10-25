package com.fullsnacke.eimsfuhcmbe.dto.mapper;

import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAssignmentResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.UserRegistrationResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorRegistration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface InvigilatorAssignmentMapper {
    @Mapping(target = "fuId", source = "invigilatorRegistration.invigilator.fuId")
    @Mapping(target = "firstName", source = "invigilatorRegistration.invigilator.firstName")
    @Mapping(target = "lastName", source = "invigilatorRegistration.invigilator.lastName")
    @Mapping(target = "email", source = "invigilatorRegistration.invigilator.email")
    @Mapping(target = "phoneNumber", source = "invigilatorRegistration.invigilator.phoneNumber")
    @Mapping(target = "department", source = "invigilatorRegistration.invigilator.department")
    @Mapping(target = "assignmentId", source = "id")
    @Mapping(target = "gender", source = "invigilatorRegistration.invigilator.gender")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "examSlotId", source = "invigilatorRegistration.examSlot.id")
    @Mapping(target = "startAt", source = "invigilatorRegistration.examSlot.startAt")
    @Mapping(target = "endAt", source = "invigilatorRegistration.examSlot.endAt")
    InvigilatorAssignmentResponseDTO toInvigilatorAssignmentResponseDto (InvigilatorAssignment assignment);

    @Named("mapInvigilatorAssignments")
    default List<InvigilatorAssignmentResponseDTO> mapInvigilatorAssignments(List<InvigilatorAssignment> assignments) {
        return assignments.stream()
                .map(this::toInvigilatorAssignmentResponseDto)
                .toList();
    }
}
