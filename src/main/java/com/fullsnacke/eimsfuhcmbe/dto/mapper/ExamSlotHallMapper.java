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


//    // version 1
//    @Mapping(source = "examSlot.id", target = "examSlotId")
////    @Mapping(source = "hallInvigilator.inviligarotRegistation", target = "hallInvigilatorFuId")
////    @Mapping(source = "hallInvigilator.inviligarotRegistationId", target = "hallInvigilatorName")
//    ExamSlotHallResponseDTO toDto(ExamSlotHall examSlotHall);


    // version 2
    @Mapping(source = "id", target = "id")
    @Mapping(source = "examSlot.id", target = "examSlotId")
    @Mapping(source = "hallInvigilator.invigilatorRegistration.invigilator.id", target = "hallInvigilatorId")
    @Mapping(source = "hallInvigilator.invigilatorRegistration.invigilator.fuId", target = "hallInvigilatorFuId")
    @Mapping(source = "hallInvigilator.invigilatorRegistration.invigilator.firstName", target = "hallInvigilatorFirstName")
    @Mapping(source = "hallInvigilator.invigilatorRegistration.invigilator.lastName", target = "hallInvigilatorLastName")
    ExamSlotHallResponseDTO toDto(ExamSlotHall examSlotHall);



    @Named("intToExamSlot")
    default ExamSlot intToExamSlot(Integer examSlotId) {
        ExamSlot examSlot = new ExamSlot();
        examSlot.setId(examSlotId);
        return examSlot;
    }


}
