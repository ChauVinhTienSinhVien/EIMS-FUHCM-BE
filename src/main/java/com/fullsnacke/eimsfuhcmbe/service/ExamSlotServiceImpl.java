package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotRequestDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotRepository;

import java.util.List;

public class ExamSlotServiceImpl implements ExamSlotService {

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
    public ExamSlot updateExamSlotExamSlot(ExamSlotRequestDTO examSlotRequestDTO, int id) {
//        ExamSlot examSlotInDB = examSlotRepository.findById(id).orElse(null);
//
//        examSlotInDB.setSubjectExamId(examSlot.getSubjectExamId());
//        examSlotInDB.setStatus(examSlot.getStatus());
//        examSlotInDB.setStartAt(examSlot.getStartAt());
//        examSlotInDB.setEndAt(examSlot.getEndAt());
//        examSlotInDB.setUpdateBy(examSlot.getUpdateBy());
//        examSlotInDB.setUpdateAt(examSlot.getUpdateAt());

        return null;
    }

    @Override
    public ExamSlot findById(int id) {
        return examSlotRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteExamSlot(int id) {

    }
}
