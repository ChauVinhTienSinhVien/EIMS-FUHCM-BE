package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.Semester;

import java.util.List;

public interface SemesterService {

    List<Semester> getAllSemesters();
    Semester createSemester(Semester semester);
    Semester updateSemester(Semester semester);
    List<Semester> findSemesterByNameLike(String name);

}
