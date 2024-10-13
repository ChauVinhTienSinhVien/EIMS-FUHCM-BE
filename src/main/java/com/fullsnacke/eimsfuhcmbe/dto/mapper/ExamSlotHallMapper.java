package com.fullsnacke.eimsfuhcmbe.dto.mapper;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotHallRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotHallResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotHall;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ExamSlotHallMapper {

    @Mapping(source = "examSlotId", target = "examSlot", qualifiedByName = "intToExamSlot")
    ExamSlotHall toEntity(ExamSlotHallRequestDTO requestDTO);

//    @Mapping(source = "examSlot.id", target = "examSlotId")
//    @Mapping(source = "roomNames", target = "roomNames")
//    ExamSlotHallResponseDTO toDto(ExamSlotHall entity);


    @Named("intToExamSlot")
    default ExamSlot intToExamSlot(Integer examSlotId) {
        ExamSlot examSlot = new ExamSlot();
        examSlot.setId(examSlotId);
        return examSlot;
    }


}
