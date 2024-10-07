package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotRequestDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.Subject;
import com.fullsnacke.eimsfuhcmbe.exception.repository.examslot.ExamSlotNotFoundException;
import com.fullsnacke.eimsfuhcmbe.exception.repository.subject.SubjectNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExamSlotServiceImpl implements ExamSlotService {

    @Autowired
    private ExamSlotRepository examSlotRepository;

    @Override
    public List<ExamSlot> getAllExamSlot() {
        return examSlotRepository.findAll();
    }

    @Override
    public ExamSlot createExamSlot(ExamSlot examSlot) {
        return examSlotRepository.save(examSlot);
    }

    @Override
    public ExamSlot updateExamSlotExamSlot(ExamSlot examSlotInRequest) {
        int id = examSlotInRequest.getId();
        ExamSlot examSlotInDB = findById(id);

        return examSlotRepository.save(examSlotInRequest);
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

}