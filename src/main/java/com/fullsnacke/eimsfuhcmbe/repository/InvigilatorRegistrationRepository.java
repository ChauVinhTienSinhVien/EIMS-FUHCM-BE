package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotDetail;
import com.fullsnacke.eimsfuhcmbe.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface InvigilatorRegistrationRepository extends JpaRepository<InvigilatorRegistration, Integer> {

    @Query("SELECT DISTINCT ir FROM InvigilatorRegistration ir " +
            "JOIN FETCH ir.invigilator i " +
            "JOIN FETCH ir.examSlot es " +
            "JOIN FETCH es.subjectExam se " +
            "JOIN FETCH se.subjectId s " +
            "WHERE i = :invigilator AND s.semesterId = :semesterId")
    Set<InvigilatorRegistration> findByInvigilatorAndSemesterWithDetails(
            @Param("invigilator") User invigilator,
            @Param("semesterId") Semester semesterId);

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

    @Query("SELECT ir FROM InvigilatorRegistration ir JOIN FETCH ir.invigilator i WHERE i = :invigilator")
    Set<InvigilatorRegistration> findByInvigilatorWithDetails(@Param("invigilator") User invigilator);

    @Query("SELECT ir FROM InvigilatorRegistration ir " +
            "JOIN FETCH ir.invigilator i " +
            "WHERE i.fuId = :fuId")
    Optional<InvigilatorRegistration> findByFuId(@Param("fuId") String fuId);

    @Query("SELECT ir FROM InvigilatorRegistration ir " +
            "JOIN FETCH ir.examSlot es " +
            "JOIN FETCH es.subjectExam se " +
            "JOIN FETCH se.subjectId s " +
            "WHERE s.semesterId = :semesterId")
    Set<InvigilatorRegistration> findBySemesterWithDetails(@Param("semesterId") Semester semesterId);

    Set<InvigilatorRegistration> findByExamSlot(ExamSlot examSlot);

    @Query("SELECT ir FROM InvigilatorRegistration ir " +
            "JOIN FETCH ir.invigilator i " +
            "JOIN FETCH ir.examSlot es " +
            "WHERE i.fuId = :fuId AND es.id IN :examSlotIds")
    Set<InvigilatorRegistration> findByInvigilatorFuIdAndExamSlotIdsWithDetails(
            @Param("fuId") String fuId,
            @Param("examSlotIds") Set<Integer> examSlotIds);

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
    int countByExamSlot(ExamSlot examSlot);

    @Query("SELECT ir FROM InvigilatorRegistration ir WHERE ir.createdAt BETWEEN :startTime AND :endTime")
    List<InvigilatorRegistration> findAllByTimeRange(@Param("startTime") Instant startTime, @Param("endTime") Instant endTime);

    @Query("SELECT ir FROM InvigilatorRegistration ir " +
            "JOIN FETCH ir.examSlot es " +
            "WHERE es.subjectExam.subjectId.semesterId = :semester AND ir.invigilator = :invigilator AND ir.id NOT IN (SELECT ia.invigilatorRegistration.id FROM InvigilatorAssignment ia)")
    Set<ExamSlot> findCancellableExamSlotsBySemesterId(
            @Param("semester") Semester semester,
            @Param("invigilator") User invigilator
    );

}
