package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotRequestDTO;
import com.fullsnacke.eimsfuhcmbe.entity.*;
import com.fullsnacke.eimsfuhcmbe.exception.repository.examslot.ExamSlotNotFoundException;
import com.fullsnacke.eimsfuhcmbe.exception.repository.subject.SubjectNotFoundException;
import com.fullsnacke.eimsfuhcmbe.exception.repository.subjectexam.SubjectExamNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ExamSlotServiceImpl implements ExamSlotService {

    @Autowired
    private ExamSlotRepository examSlotRepository;

    @Autowired
    private SemesterRepository semesterRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExamSlotHallRepository examSlotHallRepository;
    @Autowired
    private ExamSlotRoomRepository examSlotRoomRepository;

    @Override
    public List<ExamSlot> getAllExamSlot() {
        return examSlotRepository.findAll();
    }

    @Override
    public ExamSlot createExamSlot(ExamSlot examSlot) {
        return examSlotRepository.save(examSlot);
    }

    @Override
    public ExamSlot updateExamSlotExamSlot(ExamSlot examSlotInRequest, int id) {
//        int id = examSlotInRequest.getId();
        ExamSlot examSlotInDB =examSlotRepository.findExamSlotById(id);

        User user = userRepository.findUserById(examSlotInRequest.getUpdatedBy().getId());

        if (examSlotInDB == null)
            throw new EntityNotFoundException("ExamSlot not found with ID: " + id);

        if (user.getRole().getId() == 1)
            examSlotInDB.setUpdatedBy(user);

        examSlotInDB.setStartAt(examSlotInRequest.getStartAt());
        examSlotInDB.setEndAt(examSlotInRequest.getEndAt());

        return examSlotRepository.save(examSlotInDB);
    }

    public ExamSlot managerUpdateExamSlot(ExamSlot examSlotInRequest, int id) {
//        int id = examSlotInRequest.getId();
        ExamSlot examSlotInDB =examSlotRepository.findExamSlotById(id);

        User user = userRepository.findUserById(examSlotInRequest.getUpdatedBy().getId());

        if (examSlotInDB == null)
            throw new EntityNotFoundException("ExamSlot not found with ID: " + id);

        if (user.getRole().getId() == 1)
            examSlotInDB.setUpdatedBy(user);

        examSlotInDB.setStatus(examSlotInRequest.getStatus());
        examSlotInDB.setUpdatedBy(examSlotInRequest.getUpdatedBy());

        return examSlotRepository.save(examSlotInDB);
    }

    @Override
    public ExamSlot findById(int id) {
        return examSlotRepository.findById(id)
                .orElseThrow(() -> new ExamSlotNotFoundException("Exam Slot not found with ID: " + id));
    }

    @Override
    public void deleteExamSlot(int id) {
        ExamSlot examSlot = findById(id);
            examSlotRepository.delete(examSlot);
    }

    @Override
    public List<List<String>> getHallForExamSlot(int examSlotId) {
        ExamSlot examSlot = examSlotRepository.findExamSlotById(examSlotId);
        List<ExamSlotHall> examSlotHallList = examSlotHallRepository.findByExamSlot(examSlot);
        if (examSlotHallList == null) {
            return new ArrayList<>();
        }

        List<List<String>> result = new ArrayList<>();
        for (ExamSlotHall hall : examSlotHallList) {
            List<ExamSlotRoom> rooms = examSlotRoomRepository.findByExamSlotHall(hall);
            if (rooms == null) {
                return new ArrayList<>();
            }
            List<String> roomNames = new ArrayList<>();
            for (ExamSlotRoom room : rooms) {
                roomNames.add(room.getRoom().getRoomName());
            }
            result.add(roomNames);
        }
        return result;
    }

    @Override
    public List<ExamSlot> getExamSlotsBySemesterId(int semesterId) {
        Semester semester = semesterRepository.findById(semesterId).orElseThrow(() -> new RuntimeException("Semester not found"));

        return examSlotRepository.findExamSlotsBySemesterWithDetails(semester);
    }
}