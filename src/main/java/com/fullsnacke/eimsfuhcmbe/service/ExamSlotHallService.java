package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotHallRequestDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotHall;

import java.util.List;

public interface ExamSlotHallService {

    List<ExamSlotHall> getAllExamSlotHall();
    List<ExamSlotHall> addExamSlotHalls(ExamSlotHallRequestDTO examSlotHallRequestDTO);
    ExamSlotHall updateExamSlotHall(ExamSlotHall examSlotHall);
    ExamSlotHall deleteExamSlotHall(int examSlotHallId);
    ExamSlotHall getExamSlotHallById(int examSlotHallId);

}
