package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotHallRequestDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotHall;

import java.util.List;

public interface ExamSlotHallService {

    List<ExamSlotHall> getAllExamSlotHall();
    List<ExamSlotHall> addExamSlotHalls(ExamSlotHallRequestDTO examSlotHallRequestDTO);
    List<ExamSlotHall> updateExamSlotHall(ExamSlotHallRequestDTO examSlotHallRequestDTO);
    ExamSlotHall deleteExamSlotHall(int examSlotHallId);
    ExamSlotHall getExamSlotHallById(int examSlotHallId);

}
