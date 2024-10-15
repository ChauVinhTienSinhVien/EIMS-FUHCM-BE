package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface InvigilatorAssignmentRepository extends JpaRepository<InvigilatorAssignment, Integer> {
    Optional<InvigilatorAssignment> findByInvigilatorRegistrationAndIsHallInvigilator(
            InvigilatorRegistration registration, boolean isHallInvigilator);
}
