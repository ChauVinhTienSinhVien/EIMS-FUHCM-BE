package com.fullsnacke.eimsfuhcmbe.dto.mapper;

import com.fullsnacke.eimsfuhcmbe.dto.request.SubjectRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.SubjectResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    @Mapping(target = "semesterId", source = "semesterId", qualifiedByName = "intToSemester")
    Subject toEntity(SubjectRequestDTO dto);

    @Mapping(target = "semesterName", source = "semesterId.name")
    @Mapping(target = "semesterId", source = "semesterId.id")
    SubjectResponseDTO toDto(Subject entity);

    @Named("intToSemester")
    default Semester intToSemester(int semesterId) {
        Semester semester = new Semester();
        semester.setId(semesterId);
        return semester;
    }

}
