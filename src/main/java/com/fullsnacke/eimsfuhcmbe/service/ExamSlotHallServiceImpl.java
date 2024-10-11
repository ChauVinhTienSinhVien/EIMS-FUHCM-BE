package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotHallRequestDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotHall;
import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotHallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExamSlotHallServiceImpl implements ExamSlotHallService {

    @Autowired
    private ExamSlotHallRepository examSlotHallRepository;

    @Override
    public List<ExamSlotHall> getAllExamSlotHall() {
        return examSlotHallRepository.findAll();
    }

    @Override
    public List<ExamSlotHall> addExamSlotHalls(ExamSlotHallRequestDTO examSlotHallRequestDTO) {
        List<ExamSlotHall> examSlotHalls = new ArrayList<>();

        return null;
    }

    @Override
    public ExamSlotHall updateExamSlotHall(ExamSlotHall examSlotHall) {
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
