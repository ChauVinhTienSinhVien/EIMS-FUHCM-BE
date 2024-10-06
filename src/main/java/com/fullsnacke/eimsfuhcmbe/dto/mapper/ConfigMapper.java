package com.fullsnacke.eimsfuhcmbe.dto.mapper;

import com.fullsnacke.eimsfuhcmbe.dto.request.ConfigRequestDto;
import com.fullsnacke.eimsfuhcmbe.dto.response.ConfigResponseDto;
import com.fullsnacke.eimsfuhcmbe.entity.Config;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ConfigMapper {
    @Mapping(target = "semester", source = "semester", qualifiedByName = "intToSemester")
    Config toEntity(ConfigRequestDto dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "semester", source = "semester", qualifiedByName = "semesterToString")
    ConfigResponseDto toDto(Config entity);

    @Named("intToSemester")
    default Semester intToSemester(int semesterId) {
        Semester semester = new Semester();
        semester.setId(semesterId);
        return semester;
    }

    @Named("semesterToString")
    default String semesterToString(Semester semester) {
        return semester.getName();
    }
}
