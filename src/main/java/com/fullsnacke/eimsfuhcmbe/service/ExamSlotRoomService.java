package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotRoom;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

public interface ExamSlotRoomService {

    List<ExamSlotRoom> getAllExamSlotRoom();
    ExamSlotRoom addExamSlotRoom(ExamSlotRoom examSlotRoom);
    ExamSlotRoom addExamSlotRooms(List<ExamSlotRoom> examSlotRooms);
    ExamSlotRoom updateExamSlotRoom(ExamSlotRoom examSlotRoom);
    ExamSlotRoom deleteExamSlotRoom(int examSlotRoomId);
    ExamSlotRoom getExamSlotRoomById(int examSlotRoomId);
    List<String> getAllUnavailableRooms(ZonedDateTime startAt, ZonedDateTime endAt);
    List<ExamSlotRoom> getExamSlotRoomByExamSlotId(Integer examSlotId);

}
