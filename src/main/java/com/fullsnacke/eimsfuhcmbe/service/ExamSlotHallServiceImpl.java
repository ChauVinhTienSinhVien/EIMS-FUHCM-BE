package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotHallRequestDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotHall;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotRoom;
import com.fullsnacke.eimsfuhcmbe.entity.Room;
import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotHallRepository;
import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotRepository;
import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotRoomRepository;
import com.fullsnacke.eimsfuhcmbe.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExamSlotHallServiceImpl implements ExamSlotHallService {

    @Autowired
    private ExamSlotHallRepository examSlotHallRepository;

    @Autowired
    private ExamSlotRoomRepository examSlotRoomRepository;

    @Autowired
    private ExamSlotRepository examSlotRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public List<ExamSlotHall> getAllExamSlotHall() {
        return examSlotHallRepository.findAll();
    }

    @Override
    public List<ExamSlotHall> addExamSlotHalls(ExamSlotHallRequestDTO examSlotHallRequestDTO) {
        List<ExamSlotHall> examSlotHallList = new ArrayList<>();
        ExamSlot examSlot = examSlotRepository.findExamSlotById(examSlotHallRequestDTO.getExamSlotId());
        if (examSlot == null)
            throw new EntityNotFoundException("Exam slot not found");

        for (List<String> roomIds : examSlotHallRequestDTO.getRoomIds()) {
            ExamSlotHall examSlotHall = new ExamSlotHall();
            examSlotHall.setExamSlot(examSlot);
            examSlotHallRepository.save(examSlotHall);

            for (String roomId : roomIds) {
                Room room = roomRepository.findRoomById(Integer.parseInt(roomId));
                if (room == null)
                    throw new EntityNotFoundException("Room not found");

                ExamSlotRoom examSlotRoom = new ExamSlotRoom();
                examSlotRoom.setRoom(room);
                examSlotRoom.setExamSlotHall(examSlotHall);
                examSlotRoomRepository.save(examSlotRoom);
            }

            examSlotHallList.add(examSlotHall);
        }
        return examSlotHallList;
    }

    @Override
    public List<ExamSlotHall> updateExamSlotHall(ExamSlotHallRequestDTO examSlotHallRequestDTO) {

       ExamSlot examSlot = examSlotRepository.findExamSlotById(examSlotHallRequestDTO.getExamSlotId());
        if (examSlot == null)
            throw new EntityNotFoundException("Exam slot not found with ID: " + examSlotHallRequestDTO.getExamSlotId());

        List<ExamSlotHall> examSlotHallList = examSlotHallRepository.findByExamSlot(examSlot);
        if (examSlotHallList == null)
            throw new EntityNotFoundException("Exam slot hall not found with exam slot ID: " + examSlotHallRequestDTO.getExamSlotId());

        // Delete all related ExamSlotRooms
        for (ExamSlotHall hall : examSlotHallList) {
            List<ExamSlotRoom> examSlotRoomList = examSlotRoomRepository.findByExamSlotHall(hall);
            examSlotRoomRepository.deleteAll(examSlotRoomList);
            examSlotHallRepository.delete(hall);
        }

        // Add new ExamSlotRooms
        List<ExamSlotHall> newExamSlotHallList = new ArrayList<>();
        for (List<String> roomIds : examSlotHallRequestDTO.getRoomIds()) {
            ExamSlotHall examSlotHall = new ExamSlotHall();
            examSlotHall.setExamSlot(examSlot);
            examSlotHallRepository.save(examSlotHall);

            for (String roomId : roomIds) {
                Room room = roomRepository.findRoomById(Integer.parseInt(roomId));
                if (room == null)
                    throw new EntityNotFoundException("Room not found");

                ExamSlotRoom examSlotRoom = new ExamSlotRoom();
                examSlotRoom.setRoom(room);
                examSlotRoom.setExamSlotHall(examSlotHall);
                examSlotRoomRepository.save(examSlotRoom);
            }

            newExamSlotHallList.add(examSlotHall);
        }

        return newExamSlotHallList;
    }

    @Override
    public ExamSlotHall deleteExamSlotHall(int examSlotHallId) {
        return null;
    }

    @Override
    public ExamSlotHall getExamSlotHallById(int examSlotHallId) {
        return null;
    }
}
