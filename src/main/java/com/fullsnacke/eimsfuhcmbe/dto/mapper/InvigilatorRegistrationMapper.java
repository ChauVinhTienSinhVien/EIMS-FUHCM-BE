package com.fullsnacke.eimsfuhcmbe.dto.mapper;


import com.fullsnacke.eimsfuhcmbe.dto.response.*;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorRegistration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface InvigilatorRegistrationMapper {

    @Mapping(target = "fuId", source = "invigilator.fuId")
    @Mapping(target = "firstName", source = "invigilator.firstName")
    @Mapping(target = "lastName", source = "invigilator.lastName")
    @Mapping(target = "email", source = "invigilator.email")
    @Mapping(target = "phoneNumber", source = "invigilator.phoneNumber")
    @Mapping(target = "department", source = "invigilator.department")
    @Mapping(target = "role", source = "invigilator.role.id")
    @Mapping(target = "gender", source = "invigilator.gender")
    UserResponseDTO toUserResponseDTO(InvigilatorRegistration invigilatorRegistration);

    @Mapping(target = "examSlotId", source = "id")
    @Mapping(target = "startAt", source = "startAt")
    @Mapping(target = "endAt", source = "endAt")
    @Mapping(target = "userResponseDTOSet", ignore = true)
    ListInvigilatorsByExamSlotResponseDTO toListInvigilatorsByExamSlotResponseDTO(ExamSlot examSlot);

    @Mapping(target = "examSlotId", source = "id")
    @Mapping(target = "startAt", source = "startAt")
    @Mapping(target = "endAt", source = "endAt")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "requiredInvigilators", source = "requiredInvigilators")
    @Mapping(target = "numberOfRegistered", ignore = true)
    ExamSlotDetail toExamSlotDetail(ExamSlot examSlot);

    @Mapping(target = "examSlotId", source = "id")
    @Mapping(target = "startAt", source = "startAt")
    @Mapping(target = "endAt", source = "endAt")
    ExamSlotDetail toExamSlotDetailBasic(ExamSlot examSlot);

    @Named("mapInvigilatorRegistrations")
    default Set<UserResponseDTO> mapInvigilatorRegistrations(Set<InvigilatorRegistration> invigilatorRegistrations) {
        return invigilatorRegistrations.stream()
                .map(this::toUserResponseDTO)
                .collect(Collectors.toSet());
    }
    @Named("mapBasicInvigilatorRegistration")
    default List<UserResponseDTO> mapBasicInvigilatorRegistration(List<InvigilatorRegistration> invigilatorRegistrations) {
        return invigilatorRegistrations.stream()
                .map(reg -> UserResponseDTO.builder()
                        .fuId(reg.getInvigilator().getFuId())
                        .firstName(reg.getInvigilator().getFirstName())
                        .lastName(reg.getInvigilator().getLastName())
                        .email(reg.getInvigilator().getEmail())
                        .build())
                .toList();
    }


    default Set<ExamSlotDetail> mapExamSlotDetails(Set<ExamSlot> examSlots) {
        return examSlots.stream()
                .map(this::toExamSlotDetail)
                .collect(Collectors.toSet());
    }

}

