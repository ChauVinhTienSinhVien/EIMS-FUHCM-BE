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
    @Mapping(target = "registrationId", source = "id")
    @Mapping(target = "gender", source = "invigilator.gender")
    UserRegistrationResponseDTO toUserRegistrationResponseDto(InvigilatorRegistration invigilatorRegistration);

    @Mapping(target = "examSlotId", source = "id")
    @Mapping(target = "startAt", source = "startAt")
    @Mapping(target = "endAt", source = "endAt")
    @Mapping(target = "userRegistrationResponseDTOSet", ignore = true)
    ListInvigilatorsByExamSlotResponseDTO toListInvigilatorsByExamSlotResponseDTO(ExamSlot examSlot);

    @Mapping(target = "examSlotId", source = "id")
    @Mapping(target = "startAt", source = "startAt")
    @Mapping(target = "endAt", source = "endAt")
    @Mapping(target = "requiredInvigilators", source = "requiredInvigilators")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "numberOfRegistered", ignore = true)
    @Mapping(target = "examType", ignore = true)
    ExamSlotDetail toExamSlotDetailInvigilator(ExamSlot examSlot);

    @Mapping(target = "examSlotId", source = "id")
    @Mapping(target = "subjectCode", source = "subjectExam.subjectId.code")
    @Mapping(target = "examType", source = "subjectExam.examType")
    @Mapping(target = "startAt", source = "startAt")
    @Mapping(target = "endAt", source = "endAt")
    @Mapping(target = "requiredInvigilators", source = "requiredInvigilators")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "numberOfRegistered", ignore = true)
    ExamSlotDetail toExamSlotDetail(ExamSlot examSlot);

    @Mapping(target = "examSlotId", source = "id")
    @Mapping(target = "startAt", source = "startAt")
    @Mapping(target = "endAt", source = "endAt")
    @Mapping(target = "requiredInvigilators", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "numberOfRegistered", ignore = true)
    @Mapping(target = "examType", ignore = true)
    @Mapping(target = "subjectCode", ignore = true)
    ExamSlotDetail toExamSlotDetailBasic(ExamSlot examSlot);

    @Named("mapInvigilatorRegistrations")
    default Set<UserRegistrationResponseDTO> mapInvigilatorRegistrations(Set<InvigilatorRegistration> invigilatorRegistrations) {
        return invigilatorRegistrations.stream()
                .map(this::toUserRegistrationResponseDto)
                .collect(Collectors.toSet());
    }
    @Named("mapBasicInvigilatorRegistration")
    default List<UserRegistrationResponseDTO> mapBasicInvigilatorRegistration(List<InvigilatorRegistration> invigilatorRegistrations) {
        return invigilatorRegistrations.stream()
                .map(reg -> UserRegistrationResponseDTO.builder()
                        .registrationId(reg.getId())
                        .fuId(reg.getInvigilator().getFuId())
                        .firstName(reg.getInvigilator().getFirstName())
                        .lastName(reg.getInvigilator().getLastName())
                        .email(reg.getInvigilator().getEmail())
                        .build())
                .toList();
    }

    @Named("mapCancelExamSlotDetails")
    default Set<ExamSlotDetail> mapCancelExamSlotDetails(Set<ExamSlot> examSlots) {
        return examSlots.stream()
                .map(this::toExamSlotDetailBasic)
                .collect(Collectors.toSet());
    }



    default Set<ExamSlotDetail> mapExamSlotDetails(Set<ExamSlot> examSlots) {
        return examSlots.stream()
                .map(this::toExamSlotDetailInvigilator)
                .collect(Collectors.toSet());
    }



}

