package com.fullsnacke.eimsfuhcmbe.dto.mapper;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.SubjectExamDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.SubjectExam;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.enums.ExamSlotStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ExamSlotMapper {

    @Mapping(target = "subjectExam", source = "subjectExamId", qualifiedByName = "intToSubjectExam")
    @Mapping(target = "updatedBy", source = "updatedBy", qualifiedByName = "intToUser")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToInt")
    ExamSlot toEntity(ExamSlotRequestDTO dto);

    @Mapping(target = "subjectExamDTO", source = "subjectExam", qualifiedByName = "mapToSubjectExamDTO")
    @Mapping(target = "createdBy", source = "createdBy.id")
    @Mapping(target = "updatedBy", source = "updatedBy.id")
    @Mapping(target = "status", source = "status", qualifiedByName = "intToStatus")
    ExamSlotResponseDTO toDto(ExamSlot entity);

    @Named("intToSubjectExam")
    default SubjectExam intToSubjectExam(Integer subjectExamId) {
        SubjectExam subjectExam = new SubjectExam();
        subjectExam.setId(subjectExamId);
        return subjectExam;
    }

    @Named("intToUser")
    default User intToUser(Integer userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }

    @Named("mapToSubjectExamDTO")
    default SubjectExamDTO mapToSubjectExamDTO(SubjectExam subjectExam) {
        return new SubjectExamDTO(subjectExam.getId(),
                subjectExam.getDuration(),
                subjectExam.getExamType(),
                subjectExam.getSubjectId().getName(),
                subjectExam.getSubjectId().getCode()
        );
    }

    @Named("statusToInt")
    default Integer statusToInt(ExamSlotStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case NEEDS_ROOM_ASSIGNMENT -> 0;
            case PENDING -> 1;
            case APPROVED -> 2;
            case REJECTED -> 3;
            default -> throw new IllegalArgumentException("Unknown ExamSlotStatus: " + status);
        };
    }

    @Named("intToStatus")
    default ExamSlotStatus intToStatus(Integer statusValue) {
        if (statusValue == null) {
            return null;
        }
        return switch (statusValue) {
            case 0 -> ExamSlotStatus.NEEDS_ROOM_ASSIGNMENT;
            case 1 -> ExamSlotStatus.PENDING;
            case 2 -> ExamSlotStatus.APPROVED;
            case 3 -> ExamSlotStatus.REJECTED;
            default -> throw new IllegalArgumentException("Unknown status value: " + statusValue);
        };
    }


}