package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvigilatorAssignmentRepository extends JpaRepository<ExamSlot, Integer> {

}
