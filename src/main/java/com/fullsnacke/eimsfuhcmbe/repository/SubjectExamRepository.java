package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.Subject;
import com.fullsnacke.eimsfuhcmbe.entity.SubjectExam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectExamRepository extends JpaRepository<SubjectExam, Integer> {

    SubjectExam findSubjectExamById(int id);
    List<SubjectExam> findSubjectExamsBySubjectId(Subject subjectId);


}
