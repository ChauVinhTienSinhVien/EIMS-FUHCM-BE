package com.fullsnacke.eimsfuhcmbe.dto.mapper;

import com.fullsnacke.eimsfuhcmbe.dto.request.SubjectRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.SubjectResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.Subject;
import com.fullsnacke.eimsfuhcmbe.service.SemesterService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    @Mapping(target = "semesterId", source = "semesterName", qualifiedByName = "nameToSemester")
    Subject toEntity(SubjectRequestDTO dto);

    @Mapping(target = "semesterName", source = "semesterId.name")
    SubjectResponseDTO toDto(Subject entity);

    @Named("nameToSemester")
    default Semester nameToSemester(String semesterName, @Context SemesterService semesterService) {
        return semesterService.findSemesterByName(semesterName);
    }

}
