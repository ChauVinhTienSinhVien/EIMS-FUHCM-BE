package com.fullsnacke.eimsfuhcmbe.dto.mapper;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotHallRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotHallResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotHall;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExamSlotHallMapper {

    ExamSlotHallMapper INSTANCE = Mappers.getMapper(ExamSlotHallMapper.class);

    @Mapping(source = "examSlotId", target = "examSlot", qualifiedByName = "intToExamSlot")
    ExamSlotHall toEntity(ExamSlotHallRequestDTO requestDTO);

    @Mapping(source = "examSlot.id", target = "examSlotId")
//    @Mapping(source = "hallInvigilator.inviligarotRegistation", target = "hallInvigilatorFuId")
//    @Mapping(source = "hallInvigilator.inviligarotRegistationId", target = "hallInvigilatorName")
    ExamSlotHallResponseDTO toDto(ExamSlotHall examSlotHall);


    @Named("intToExamSlot")
    default ExamSlot intToExamSlot(Integer examSlotId) {
        ExamSlot examSlot = new ExamSlot();
        examSlot.setId(examSlotId);
        return examSlot;
    }


}
