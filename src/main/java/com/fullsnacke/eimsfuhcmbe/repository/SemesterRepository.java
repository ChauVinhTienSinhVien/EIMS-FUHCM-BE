package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Date;

public interface SemesterRepository extends JpaRepository<Semester, Integer>{

    Semester findSemesterByName(String name);
    Semester findFirstByOrderByStartAtDesc();
    Semester findFirstByEndAtBeforeOrderByEndAtAsc(Date endAt);
    Semester findSemesterById(Integer semesterId);
}