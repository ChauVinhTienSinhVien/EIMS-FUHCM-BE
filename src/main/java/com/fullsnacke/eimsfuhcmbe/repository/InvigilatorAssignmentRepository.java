package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface InvigilatorAssignmentRepository extends JpaRepository<InvigilatorAssignment, Integer> {
    Set<InvigilatorAssignment> findByInvigilatorAndExamSlot_SubjectExam_SubjectId_SemesterId(
            User invigilator, Semester semesterId);

    Set<InvigilatorAssignment> findByInvigilator(User invigilator);
}
