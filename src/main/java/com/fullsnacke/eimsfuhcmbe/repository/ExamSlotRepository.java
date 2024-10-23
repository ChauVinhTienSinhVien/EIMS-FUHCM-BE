package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface ExamSlotRepository extends JpaRepository<ExamSlot, Integer> {

    ExamSlot findExamSlotById(int id);

    @Query("SELECT e FROM ExamSlot e " +
            "JOIN FETCH e.subjectExam se " +
            "JOIN FETCH se.subjectId s " +
            "WHERE s.semesterId = :semester " +
            "ORDER BY e.startAt")
    List<ExamSlot> findExamSlotsBySemesterWithDetails(@Param("semester") Semester semester);

    List<ExamSlot> findExamSlotBySubjectExam_SubjectId_SemesterId(Semester semester);

    @Query("SELECT e FROM ExamSlot e " +
            "WHERE e.subjectExam.subjectId.semesterId = :semester " +
            "AND e.startAt <= :endDate " +
            "ORDER BY e.startAt ASC")
    List<ExamSlot> findExamSlotsBySemesterAndBeforeEndDate(
            @Param("semester") Semester semester,
            @Param("endDate") ZonedDateTime endDate);

    List<ExamSlot> findByIdIn(List<Integer> examSlotIds);

    @Query("SELECT e FROM ExamSlot e WHERE e.startAt >= :startTime AND e.endAt <= :endTime")
    List<ExamSlot> findExamSlotsByTimeRange(@Param("startTime") ZonedDateTime startTime, @Param("endTime") ZonedDateTime endTime);

}


