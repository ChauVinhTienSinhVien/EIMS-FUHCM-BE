package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotRequestDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.Subject;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.exception.repository.examslot.ExamSlotNotFoundException;
import com.fullsnacke.eimsfuhcmbe.exception.repository.subject.SubjectNotFoundException;
import com.fullsnacke.eimsfuhcmbe.exception.repository.subjectexam.SubjectExamNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotRepository;
import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ExamSlotServiceImpl implements ExamSlotService {

    @Autowired
    private ExamSlotRepository examSlotRepository;

    @Autowired
    private SemesterRepository semesterRepository;
    @Autowired
    private UserRepository userRepository;

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
    public List<ExamSlot> getExamSlotsBySemesterId(int semesterId) {
        Semester semester = semesterRepository.findById(semesterId).orElseThrow(() -> new RuntimeException("Semester not found"));

        return examSlotRepository.findExamSlotBySubjectExam_SubjectId_SemesterId(semester);
    }
}