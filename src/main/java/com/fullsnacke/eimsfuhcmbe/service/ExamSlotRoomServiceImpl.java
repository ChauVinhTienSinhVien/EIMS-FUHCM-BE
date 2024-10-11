package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotRoom;
import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamSlotRoomServiceImpl implements ExamSlotRoomService {

    @Autowired
    private ExamSlotRoomRepository examSlotRoomRepository;

    @Override
    public List<ExamSlotRoom> getAllExamSlotRoom() {
        return examSlotRoomRepository.findAll();
    }

    @Override
    public ExamSlotRoom addExamSlotRoom(ExamSlotRoom examSlotRoom) {
        return null;
    }

    @Override
    public ExamSlotRoom addExamSlotRooms(List<ExamSlotRoom> examSlotRooms) {
        return null;
    }

    @Override
    public ExamSlotRoom updateExamSlotRoom(ExamSlotRoom examSlotRoom) {
        return null;
    }

    @Override
    public ExamSlotRoom deleteExamSlotRoom(int examSlotRoomId) {
        return null;
    }

    @Override
    public ExamSlotRoom getExamSlotRoomById(int examSlotRoomId) {
        return null;
    }
}
