package com.fullsnacke.eimsfuhcmbe.dto.mapper;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotRomRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotRoomResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotRoom;
import org.mapstruct.Mapping;

public interface ExamSlotRoomMapper {

    @Mapping(source = "examSlotRoomId", target = "id")
    @Mapping(source = "examHallId", target = "examSlot.id")
    @Mapping(source = "roomId", target = "room.id")
//    @Mapping(source = "roomInvigilatorFuId", target = "room.invigilatorFuId")
    ExamSlotRoom toEntity(ExamSlotRomRequestDTO requestDTO);

    @Mapping(source = "id", target = "examSlotRoomId")
    @Mapping(source = "examSlot.id", target = "examSlotId")
    @Mapping(source = "room.id", target = "roomId")
//    @Mapping(source = "room.invigilatorFuId", target = "roomInvigilatorFuId")
    ExamSlotRoomResponseDTO toDto(ExamSlotRoom examSlotRoom);

}
