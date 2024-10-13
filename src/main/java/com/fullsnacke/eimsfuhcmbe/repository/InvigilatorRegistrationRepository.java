package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface InvigilatorRegistrationRepository extends JpaRepository<InvigilatorRegistration, Integer> {
    Set<InvigilatorRegistration> findByInvigilatorAndExamSlot_SubjectExam_SubjectId_SemesterId(
            User invigilator, Semester semesterId);

    Set<InvigilatorRegistration> findByInvigilator(User invigilator);

    Set<InvigilatorRegistration> findByExamSlot_SubjectExam_SubjectId_SemesterId(Semester semesterId);

    Set<InvigilatorRegistration> findByExamSlot(ExamSlot examSlot);
}
