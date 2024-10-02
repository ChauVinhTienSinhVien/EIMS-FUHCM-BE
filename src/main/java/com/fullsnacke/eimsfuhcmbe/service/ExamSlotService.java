package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotRequestDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;

import java.util.List;

public interface ExamSlotService {

    List<ExamSlot> getAllExamSlot();
    ExamSlot createExamSlot(ExamSlot examSlot);
    ExamSlot updateExamSlotExamSlot(ExamSlotRequestDTO examSlotRequestDTO, int id);
    ExamSlot findById(int id);
    void deleteExamSlot(int id);

}
