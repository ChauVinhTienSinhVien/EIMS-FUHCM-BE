package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Semester updateSemester(Semester semester) {
        return null;
    }

    @Override
    public List<Semester> findSemesterByNameLike(String name) {
        return semesterRepository.findSemesterByNameLike(name);
    }
}