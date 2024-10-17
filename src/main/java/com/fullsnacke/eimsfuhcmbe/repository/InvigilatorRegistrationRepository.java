package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface InvigilatorRegistrationRepository extends JpaRepository<InvigilatorRegistration, Integer> {
    Set<InvigilatorRegistration> findByInvigilatorAndExamSlot_SubjectExam_SubjectId_SemesterId(
            User invigilator, Semester semesterId);

    @Query("SELECT ir FROM InvigilatorRegistration ir " +
            "JOIN FETCH ir.examSlot es " +
            "JOIN FETCH es.subjectExam se " +
            "JOIN FETCH se.subjectId s " +
            "WHERE ir.invigilator.id = :invigilatorId " +
            "AND s.semesterId.id = :semesterId")
    Set<InvigilatorRegistration> findRegistrationsWithDetailsByInvigilatorAndSemester(
            @Param("invigilatorId") Integer invigilatorId,
            @Param("semesterId") Integer semesterId
    );

    Set<InvigilatorRegistration> findByInvigilator(User invigilator);

    @Query("SELECT ir FROM InvigilatorRegistration ir " +
            "JOIN FETCH ir.invigilator i " +
            "WHERE i.fuId = :fuId")
    Optional<InvigilatorRegistration> findByFuId( @Param("fuId") String fuId);

    Set<InvigilatorRegistration> findByExamSlot_SubjectExam_SubjectId_SemesterId(Semester semesterId);

    Set<InvigilatorRegistration> findByExamSlot(ExamSlot examSlot);

    Set<InvigilatorRegistration> findByInvigilator_FuIdAndExamSlot_IdIn(String fuId, Set<Integer> examSlotIds);

    @Query("SELECT ir FROM InvigilatorRegistration ir " +
            "WHERE ir.examSlot IN :examSlots " +
            "AND ir.id NOT IN (SELECT ia.invigilatorRegistration.id FROM InvigilatorAssignment ia)")
    List<InvigilatorRegistration> findUnassignedRegistrationsByExamSlotInOrderByCreatedAtAsc(@Param("examSlots") List<ExamSlot> examSlots);


    @Query("SELECT ir FROM InvigilatorRegistration ir " +
            "WHERE ir.examSlot.id = :examSlotId " +
            "AND ir.id NOT IN (SELECT ia.invigilatorRegistration.id FROM InvigilatorAssignment ia) " +
            "ORDER BY ir.createdAt ASC")
    List<InvigilatorRegistration> findUnassignedRegistrationsByExamSlot_IdOrderByCreatedAtAsc(@Param("examSlotId") int examSlotId);

    @Query("SELECT ir FROM InvigilatorRegistration ir " +
            "JOIN FETCH ir.examSlot es " +
            "JOIN FETCH ir.invigilator i " +
            "WHERE es.id = :examSlotId AND i.fuId = :fuId")
    Optional<InvigilatorRegistration> findByExamSlotIdAndInvigilatorFuId(
            @Param("examSlotId") int examSlotId,
            @Param("fuId") String fuId
    );
}
