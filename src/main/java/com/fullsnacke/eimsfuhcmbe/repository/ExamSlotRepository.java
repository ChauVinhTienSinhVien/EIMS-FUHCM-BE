package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamSlotRepository extends JpaRepository<ExamSlot, Integer> {
    ExamSlot findExamSlotById(int id);

    List<ExamSlot> findExamSlotBySubjectExam_SubjectId_SemesterId(Semester semester);
}


