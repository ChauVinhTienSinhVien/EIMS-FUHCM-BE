package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.SemesterRequestDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.exception.repository.semester.SemesterNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SemesterServiceImpl implements SemesterService {


    private SemesterRepository semesterRepository;

    @Autowired
    public SemesterServiceImpl(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

    @Override
    public List<Semester> getAllSemesters() {
        return semesterRepository.findAll();
    }

    @Override
    public Semester createSemester(Semester semester) {
        return semesterRepository.save(semester);
    }

    @Override
    public Semester updateSemester(Semester semester, int id) {

        Semester semesterInDB = semesterRepository.findById(id).orElse(null);
        if (semesterInDB == null)
            throw new SemesterNotFoundException("Semester not found");
        semesterInDB.setName(semester.getName());
        semesterInDB.setEndAt(semester.getEndAt());
        semesterInDB.setStartAt(semester.getStartAt());
        return semesterRepository.save(semesterInDB);
    }

    @Override
    public Semester findSemesterByName(String name) {
        return semesterRepository.findSemesterByName(name);
    }
}