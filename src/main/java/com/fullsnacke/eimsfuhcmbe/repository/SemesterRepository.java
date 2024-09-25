package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SemesterRepository extends JpaRepository<Semester, Integer>{

    List<Semester> findSemesterByNameLike(String name);

}