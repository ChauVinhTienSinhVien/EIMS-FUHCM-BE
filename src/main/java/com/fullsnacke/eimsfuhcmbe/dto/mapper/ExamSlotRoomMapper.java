package com.fullsnacke.eimsfuhcmbe.dto.mapper;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotRoomRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotRoomResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotRoom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExamSlotRoomMapper {

//    @Mapping(source = "examSlotRoomId", target = "id")
//    @Mapping(source = "examHallId", target = "examSlot.id")
//    @Mapping(source = "roomId", target = "room.id")
////    @Mapping(source = "roomInvigilatorFuId", target = "room.invigilatorFuId")
//    ExamSlotRoom toEntity(ExamSlotRomRequestDTO requestDTO);

    @Mapping(source = "examSlotRoomId", target = "id")
    @Mapping(source = "examHallId", target = "examSlotHall.id") // Liên kết examHallId với ExamSlotHall
    @Mapping(source = "roomId", target = "room.id") // Liên kết roomId với Room entity
    @Mapping(source = "roomInvigilatorFuId", target = "roomInvigilator.invigilatorRegistration.invigilator.fuId") // Liên kết giám thị
    ExamSlotRoom toEntity(ExamSlotRoomRequestDTO dto);

//    // version 1
//    @Mapping(source = "id", target = "examSlotRoomId")
//    @Mapping(source = "examSlotHall.examSlot.id", target = "examSlotId")
//    @Mapping(source = "room.id", target = "roomId")
//    @Mapping(source = "roomInvigilator.invigilatorRegistration.invigilator.fuId", target = "roomInvigilatorFuId")
//    @Mapping(source = "roomInvigilator.invigilatorRegistration.invigilator.lastName", target = "roomInvigilatorName")
//    @Mapping(source = "examSlotHall.hallInvigilator.invigilatorRegistration.invigilator.fuId", target = "hallInvigilatorFuId")
//    @Mapping(source = "examSlotHall.hallInvigilator.invigilatorRegistration.invigilator.lastName", target = "hallInvigilatorName")
//    ExamSlotRoomResponseDTO toDto(ExamSlotRoom examSlotRoom);

//    // version 2
//    @Mapping(source = "room.roomName", target = "roomName")
//    @Mapping(source = "room.floor", target = "floor")
//    @Mapping(source = "room.campus", target = "campus")
//    @Mapping(source = "roomInvigilator.invigilatorRegistration.invigilator.fuId", target = "invigilatorFuId")
//    @Mapping(source = "roomInvigilator.invigilatorRegistration.invigilator.lastName", target = "invigilatorName")
//    @Mapping(source = "examSlotHall.examSlot.subjectExam.subjectId.name", target = "examSlotSubjectName")
//    @Mapping(source = "examSlotHall.examSlot.startAt", target = "examSlotStartTime")
//    @Mapping(source = "examSlotHall.examSlot.endAt", target = "examSlotEndTime")
//    ExamSlotRoomResponseDTO toDto(ExamSlotRoom examSlotRoom);

    //version 3
    @Mapping(source = "id", target = "examSlotRoomId")
    @Mapping(source = "examSlotHall.examSlot.id", target = "examSlotId")
    @Mapping(source = "examSlotHall.examSlot.subjectExam.subjectId.name", target = "examSlotSubjectName")
    @Mapping(source = "examSlotHall.examSlot.startAt", target = "startAt")
    @Mapping(source = "examSlotHall.examSlot.endAt", target = "endAt")

    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "room.roomName", target = "roomName")
    @Mapping(source = "room.floor", target = "floor")
    @Mapping(source = "room.campus", target = "campus")

    @Mapping(source = "roomInvigilator.invigilatorRegistration.invigilator.fuId", target = "roomInvigilatorFuId")
    @Mapping(source = "roomInvigilator.invigilatorRegistration.invigilator.firstName", target = "roomInvigilatorFirstName")
    @Mapping(source = "roomInvigilator.invigilatorRegistration.invigilator.lastName", target = "roomInvigilatorLastName")

    @Mapping(source = "examSlotHall.hallInvigilator.invigilatorRegistration.invigilator.fuId", target = "hallInvigilatorFuId")
    @Mapping(source = "examSlotHall.hallInvigilator.invigilatorRegistration.invigilator.firstName", target = "hallInvigilatorFirstName")
    @Mapping(source = "examSlotHall.hallInvigilator.invigilatorRegistration.invigilator.lastName", target = "hallInvigilatorLastName")
    ExamSlotRoomResponseDTO toDto(ExamSlotRoom examSlotRoom);

}
