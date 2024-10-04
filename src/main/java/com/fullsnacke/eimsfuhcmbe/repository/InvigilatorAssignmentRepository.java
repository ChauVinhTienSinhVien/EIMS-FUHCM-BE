package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvigilatorAssignmentRepository extends JpaRepository<InvigilatorAssignment, Integer> {
//    List<InvigilatorAssignment> findByInvigilator(User invigilator);
List<InvigilatorAssignment> findByInvigilatorAndExamSlot_SubjectExam_SubjectId_SemesterIdAndExamSlot_SubjectExam_ExamType(
        User invigilator, Semester semesterId, String examType);
}
