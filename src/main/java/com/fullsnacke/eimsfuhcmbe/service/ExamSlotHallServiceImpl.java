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
    public ExamSlotHall updateExamSlotHall(ExamSlotHall examSlotHall) {

        ExamSlotHall examSlotHall1 = examSlotHallRepository.findExamSlotHallById(examSlotHall.getId());
        if (examSlotHall1 == null)
            throw new EntityNotFoundException("Exam slot hall not found");

//        examSlotRoomRepository.deleteByExamSlotHallId(examSlotHall.getId());
        // Add new ExamSlotRooms
//        for (List<String> roomIds : requestDTO.getRoomIds()) {
//            for (String roomId : roomIds) {
//                Room room = roomRepository.findById(roomId)
//                        .orElseThrow(() -> new RuntimeException("Room not found"));
//                ExamSlotRoom examSlotRoom = new ExamSlotRoom();
//                examSlotRoom.setExamSlot(examSlot);
//                examSlotRoom.setRoom(room);
//                examSlotRoom.setExamSlotHall(examSlotHall);
//                examSlotRoomRepository.save(examSlotRoom);
//            }
//        }
        return null;
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
