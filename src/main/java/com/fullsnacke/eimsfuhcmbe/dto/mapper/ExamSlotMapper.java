package com.fullsnacke.eimsfuhcmbe.dto.mapper;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.SubjectExam;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ExamSlotMapper {

    @Mapping(target = "subjectExam", source = "subjectExamId", qualifiedByName = "intToSubjectExam")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "intToUser")
    @Mapping(target = "updatedBy", source = "updatedBy", qualifiedByName = "intToUser")
    ExamSlot toEntity(ExamSlotRequestDTO dto);

    @Mapping(target = "subjectExamId", source = "subjectExam.id", qualifiedByName = "intToSubjectExam")
    @Mapping(target = "createdBy", source = "createdBy.id")
    @Mapping(target = "updatedBy", source = "updatedBy.id")
    ExamSlotResponseDTO toDto(ExamSlot entity);

    @Named("intToSubjectExam")
    default SubjectExam intToSubjectExam(int subjectExamId) {
        SubjectExam subjectExam = new SubjectExam();
        subjectExam.setId(subjectExamId);
        return subjectExam;
    }

    @Named("intToUser")
    default User intToUser(int userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }

//    @Named("userToInt")
//    default int userToInt(User user) {
//        return user.getId();
//    }

}