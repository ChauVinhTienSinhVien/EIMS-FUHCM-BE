package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotRoom;

import java.util.List;

public interface InvigilatorAssignmentService {
    List<ExamSlotRoom> assignInvigilators(int semesterId);
}
