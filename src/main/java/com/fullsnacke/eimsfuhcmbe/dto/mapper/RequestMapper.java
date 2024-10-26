package com.fullsnacke.eimsfuhcmbe.dto.mapper;

import com.fullsnacke.eimsfuhcmbe.dto.request.RequestRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotDetail;
import com.fullsnacke.eimsfuhcmbe.dto.response.ManagerRequestResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RequestResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.SubjectResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.Request;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.Subject;
import com.fullsnacke.eimsfuhcmbe.enums.RequestStatusEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "examSlot", source = "examSlotId", qualifiedByName = "examSlotIdToExamSlot")
//    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "status", constant = "0")
    @Mapping(target = "requestType", source = "requestType")
    @Mapping(target = "createdBy", source = "invigilator")
    @Mapping(target = "reason", source = "reason")
    Request toEntity(RequestRequestDTO dto);


    @Mapping(target = "examSlotId", source = "examSlot.id")
    @Mapping(target = "startAt", source = "examSlot.startAt")
    @Mapping(target = "endAt", source = "examSlot.endAt")
    @Mapping(target = "semesterName", source = "examSlot.subjectExam.subjectId.semesterId.name")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "requestType", source = "requestType")
    @Mapping(target = "requestId", source = "id")
//    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "note", source = "comment")
    RequestResponseDTO toResponseDTO(Request entity);

    @Named("examSlotIdToExamSlot")
    default ExamSlot examSlotIdToExamSlot(int examSlotId) {
        ExamSlot examSlot = new ExamSlot();
        examSlot.setId(examSlotId);
        return examSlot;
    }

    @Named("examSlotToExamSlotDetail")
    default ExamSlotDetail examSlotToExamSlotDetail(ExamSlot examSlot) {
        return ExamSlotDetail.builder()
                .examSlotId(examSlot.getId())
                .startAt(examSlot.getStartAt())
                .endAt(examSlot.getEndAt())
                .examType(examSlot.getSubjectExam().getExamType())
                .build();
    }

    @Named("subjectToSubjectResponseDTO")
    default SubjectResponseDTO subjectToSubjectResponseDTO(Subject subject) {
        return SubjectResponseDTO.builder()
                .id(subject.getId())
                .code(subject.getCode())
                .name(subject.getName())
                .semesterName(subject.getSemesterId().getName())
                .build();
    }

    @Mapping(target = "requestId", source = "id")
    @Mapping(target = "fuId", source = "createdBy.fuId")
    @Mapping(target = "email", source = "createdBy.email")
    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "examSlotDetail", source = "examSlot", qualifiedByName = "examSlotToExamSlotDetail")
    @Mapping(target = "subject", source = "examSlot.subjectExam.subjectId", qualifiedByName = "subjectToSubjectResponseDTO")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "note", source = "comment")
    @Mapping(target = "requestType", source = "requestType")
    ManagerRequestResponseDTO toManagerRequestResponseDTO(Request entity);



//    @Named("intToString")
//    default String intToString(int value) {
//        RequestStatusEnum status = RequestStatusEnum.fromValue(value);
//        return String.valueOf(status);
//    }
}
