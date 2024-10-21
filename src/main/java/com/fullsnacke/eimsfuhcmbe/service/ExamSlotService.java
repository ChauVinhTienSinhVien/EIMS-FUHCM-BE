package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.Room;

import java.time.ZonedDateTime;
import java.util.List;

public interface ExamSlotService {
    List<ExamSlot> getAllExamSlot();
    ExamSlot createExamSlot(ExamSlot examSlot);
    ExamSlot updateExamSlotExamSlot(ExamSlot examSlot, int id);
    ExamSlot findById(int id);
    void deleteExamSlot(int id);
    List<ExamSlot> getExamSlotsBySemesterId(int semesterId);
    List<List<Room>> getHallForExamSlot(int examSlotId);
    List<ExamSlot> getExamSlotsInTimeRange(ZonedDateTime startTime, ZonedDateTime endTime);

}