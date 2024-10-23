package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface ExamSlotRepository extends JpaRepository<ExamSlot, Integer> {

    ExamSlot findExamSlotById(int id);

    @Query("SELECT e FROM ExamSlot e " +
            "JOIN FETCH e.subjectExam se " +
            "JOIN FETCH se.subjectId s " +
            "WHERE s.semesterId = :semester " +
            "ORDER BY e.startAt")
    List<ExamSlot> findExamSlotsBySemesterWithDetails(@Param("semester") Semester semester);

    @Query("SELECT DISTINCT ir.examSlot FROM InvigilatorRegistration ir " +
            "JOIN ir.invigilator i " +
            "JOIN ir.examSlot es " +
            "JOIN es.subjectExam se " +
            "JOIN se.subjectId sub " +
            "JOIN sub.semesterId s " +
            "WHERE es.id IN (SELECT DISTINCT esh.examSlot.id FROM ExamSlotHall esh WHERE esh.hallInvigilator IS NOT NULL) " +
            "AND s.id = :semesterId " +
            "AND i.fuId = :fuId")
    List<ExamSlot> findAssignedExamSlotsBySemesterIdAndFuId(
            @Param("semesterId") Integer semesterId,
            @Param("fuId") String fuId);

    @Query("SELECT e FROM ExamSlot e " +
            "JOIN FETCH e.subjectExam se " +
            "JOIN FETCH se.subjectId s " +
            "WHERE s.semesterId = :semester " +
            "AND e.status = :status " +
            "ORDER BY e.startAt")
    List<ExamSlot> findExamSlotsBySemesterWithDetails(@Param("semester") Semester semester, int status);

    @Query("SELECT e FROM ExamSlot e " +
            "WHERE e.subjectExam.subjectId.semesterId = :semester " +
            "AND e.startAt <= :endDate " +
            "ORDER BY e.startAt ASC")
    List<ExamSlot> findExamSlotsBySemesterAndBeforeEndDate(
            @Param("semester") Semester semester,
            @Param("endDate") ZonedDateTime endDate);

    List<ExamSlot> findByIdIn(List<Integer> examSlotIds);

    @Query("SELECT e FROM ExamSlot e " +
            "JOIN FETCH e.subjectExam se " +
            "JOIN FETCH se.subjectId s " +
            "WHERE e.startAt >= :startDate " +
            "AND e.startAt <= :endDate " +
            "AND e.status = :status " +
            "ORDER BY e.startAt")
    List<ExamSlot> findExamSlotsByStartAtBetween(ZonedDateTime startDate, ZonedDateTime endDate, int status);

    @Query("SELECT e FROM ExamSlot e WHERE e.startAt >= :startTime AND e.endAt <= :endTime")
    List<ExamSlot> findExamSlotsByTimeRange(@Param("startTime") ZonedDateTime startTime, @Param("endTime") ZonedDateTime endTime);

    @Query("SELECT e FROM ExamSlot e WHERE e.subjectExam.id = :subjectId AND " +
            "((e.startAt <= :endAt AND e.endAt >= :startAt))")
    List<ExamSlot> findBySubjectAndTime(@Param("subjectId") int subjectId,
                                        @Param("startAt") ZonedDateTime startAt,
                                        @Param("endAt") ZonedDateTime endAt);

    List<ExamSlot> findExamSlotByStatus(int status);

}


