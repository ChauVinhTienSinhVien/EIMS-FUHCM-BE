package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface InvigilatorAttendanceRepository extends JpaRepository<InvigilatorAttendance, Integer> {

    @Query("SELECT ia FROM InvigilatorAttendance ia " +
            "JOIN FETCH ia.invigilatorAssignment iaa " +
            "JOIN FETCH iaa.invigilatorRegistration ir " +
            "JOIN FETCH ir.examSlot es " +
            "WHERE FUNCTION('DATE', es.startAt) = FUNCTION('DATE', :day)")
    List<InvigilatorAttendance> findByExamSlotStartAtInDay(@Param("day") Instant day);

    @Query("SELECT ia FROM InvigilatorAttendance ia " +
            "JOIN FETCH ia.invigilatorAssignment iaa " +
            "JOIN FETCH iaa.invigilatorRegistration ir " +
            "JOIN FETCH ir.examSlot es " +
            "WHERE ir.examSlot.id = :examSlotId")
    List<InvigilatorAttendance> findByExamSlotId(@Param("examSlotId") Integer examSlotId);

    @Query("SELECT es FROM InvigilatorAttendance ia " +
            "JOIN ia.invigilatorAssignment iaa " +
            "JOIN iaa.invigilatorRegistration ir " +
            "JOIN ir.examSlot es " +
            "WHERE FUNCTION('DATE', es.startAt) = FUNCTION('DATE', :day)")
    List<ExamSlot> findExamSlotByStartAtInDay(@Param("day") Instant day);

    @Query("SELECT ia FROM InvigilatorAttendance ia " +
            "JOIN FETCH ia.invigilatorAssignment iaa " +
            "JOIN FETCH iaa.invigilatorRegistration ir " +
            "WHERE ir.invigilator.id = :invigilatorId")
    List<InvigilatorAttendance> findInvigilatorAttendanceByInvigilatorId(Integer invigilatorId);

    @Query("SELECT ia FROM InvigilatorAttendance ia " +
            "JOIN FETCH ia.invigilatorAssignment iaa " +
            "JOIN FETCH iaa.invigilatorRegistration ir " +
            "JOIN FETCH ir.examSlot es " +
            "WHERE ir.invigilator.id = :id AND FUNCTION('DATE', es.startAt) = FUNCTION('DATE', :day)")
    List<InvigilatorAttendance> findInvigilatorAttendanceByInvigilatorIdAndDay(@Param("id") Integer id, @Param("day") Instant day);

    @Query("SELECT es FROM InvigilatorAttendance ia " +
            "JOIN ia.invigilatorAssignment iaa " +
            "JOIN iaa.invigilatorRegistration ir " +
            "JOIN ir.examSlot es " +
            "WHERE FUNCTION('DATE', es.startAt) = FUNCTION('DATE', :day) AND ia.checkIn != NULL AND ia.checkOut != NULL")
    List<ExamSlot> findCheckedAttendanceExamSlotsByDay(Instant day);

    @Query("SELECT ia FROM InvigilatorAttendance ia " +
            "JOIN FETCH ia.invigilatorAssignment iaa " +
            "JOIN FETCH iaa.invigilatorRegistration ir " +
            "JOIN FETCH ir.examSlot es " +
            "WHERE ir.invigilator.id = :invigilatorId AND es.subjectExam.subjectId.semesterId.id = :semesterId AND ia.checkIn IS NOT NULL AND ia.checkOut IS NOT NULL")
    List<InvigilatorAttendance> findInvigilatorAttendanceByInvigilatorIdAndSemesterId(Integer invigilatorId, Integer semesterId);
}
