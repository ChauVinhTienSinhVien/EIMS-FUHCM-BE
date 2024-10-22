package com.fullsnacke.eimsfuhcmbe.repository;

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

    @Query("SELECT ia FROM InvigilatorAttendance ia " +
            "JOIN FETCH ia.invigilatorAssignment iaa " +
            "JOIN FETCH iaa.invigilatorRegistration ir " +
            "JOIN FETCH ir.examSlot es " +
            "JOIN FETCH es.subjectExam se " +
            "JOIN FETCH se.subjectId s " +
            "WHERE s.semesterId.id = :semesterId " +
            "AND ia.checkIn IS NOT NULL " +
            "AND ia.checkOut IS NOT NULL")
    List<InvigilatorAttendance> findCompletedAttendancesBySemesterId(@Param("semesterId") Integer semesterId);
}
