package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamSlotRepository extends JpaRepository<ExamSlot, Integer> {
    Optional<ExamSlot> findById(int id);

    List<ExamSlot> findExamSlotBySubjectExam_SubjectId_SemesterId(Semester semester);
}


