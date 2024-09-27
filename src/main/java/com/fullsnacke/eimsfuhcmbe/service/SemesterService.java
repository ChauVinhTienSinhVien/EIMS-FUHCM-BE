package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.SemesterRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.SemesterResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;

import java.util.List;

public interface SemesterService {

    List<Semester> getAllSemesters();
    Semester createSemester(Semester semester);
    Semester updateSemester(Semester semester, int id);
    Semester findSemesterByName(String name);

}
