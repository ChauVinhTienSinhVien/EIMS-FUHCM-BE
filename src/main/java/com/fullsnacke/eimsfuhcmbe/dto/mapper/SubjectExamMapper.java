package com.fullsnacke.eimsfuhcmbe.dto.mapper;

import com.fullsnacke.eimsfuhcmbe.dto.request.SubjectExamRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.SubjectExamResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Subject;
import com.fullsnacke.eimsfuhcmbe.entity.SubjectExam;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SubjectExamMapper {

    @Mapping(target = "subjectId", source = "subjectId", qualifiedByName = "intToSubject")
    SubjectExam toEntity(SubjectExamRequestDTO dto);

    @Mapping(target = "subjectName", source = "subjectId.name")
    @Mapping(target = "subjectCode", source = "subjectId.code")
    @Mapping(target = "semesterName", source = "subjectId.semesterId.name")
    @Mapping(target = "semesterId", source = "subjectId.semesterId.id")
    SubjectExamResponseDTO toDto(SubjectExam entity);

    @Named("intToSubject")
    default Subject intToSubject(int subjectId) {
        Subject subject = new Subject();
        subject.setId(subjectId);
        return subject;
    }

    @Named("intToUser")
    default User intToUser(int staffId) {
        User user = new User();
        user.setId(staffId);
        return user;
    }

}
