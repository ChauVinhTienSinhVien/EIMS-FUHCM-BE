package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface InvigilatorAssignmentRepository extends JpaRepository<InvigilatorAssignment, Integer> {
    Optional<InvigilatorAssignment> findByInvigilatorRegistrationAndIsHallInvigilator(
            InvigilatorRegistration registration, boolean isHallInvigilator);

    @Query("SELECT ia FROM InvigilatorAssignment ia " +
            "JOIN FETCH ia.invigilatorRegistration ir " +
            "JOIN FETCH ir.examSlot es " +
            "JOIN FETCH ir.invigilator i " +
            "WHERE es.id = :examSlotId AND i.fuId = :fuId")
    Optional<InvigilatorAssignment> findByExamSlotIdAndInvigilatorFuId(
            @Param("examSlotId") int examSlotId,
            @Param("fuId") String fuId
    );

    @Query("SELECT ia FROM InvigilatorAssignment ia " +
            "JOIN FETCH ia.invigilatorRegistration ir " +
            "JOIN FETCH ir.examSlot es " +
            "WHERE es.id = :examSlotId")
    List<InvigilatorAssignment> findByExamSlotId(
            @Param("examSlotId") int examSlotId
    );

    @Query("SELECT ia FROM InvigilatorAssignment ia " +
            "JOIN FETCH ia.invigilatorRegistration ir " +
            "JOIN FETCH ir.examSlot es " +
            "WHERE FUNCTION('DATE', es.startAt) = FUNCTION('DATE', :day)")
    List<InvigilatorAssignment> findByExamSlotStartAtInDay(@Param("day") Instant day);

}
