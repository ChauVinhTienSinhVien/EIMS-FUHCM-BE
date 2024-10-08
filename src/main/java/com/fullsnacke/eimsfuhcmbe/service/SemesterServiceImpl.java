package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.SemesterRequestDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Config;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.enums.ConfigType;
import com.fullsnacke.eimsfuhcmbe.exception.repository.semester.SemesterNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.ConfigRepository;
import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SemesterServiceImpl implements SemesterService {


    private SemesterRepository semesterRepository;
    private ConfigServiceImpl configServiceImpl;

    @Autowired
    public SemesterServiceImpl(SemesterRepository semesterRepository, ConfigServiceImpl configServiceImpl) {

        this.semesterRepository = semesterRepository;
        this.configServiceImpl   = configServiceImpl;
    }

    @Override
    public List<Semester> getAllSemesters() {
        return semesterRepository.findAll();
    }

    @Override
    @Transactional
    public Semester createSemester(Semester semester) {
        Semester lastestSemester =  semesterRepository.findFirstByOrderByStartAtDesc();
        Semester createdSemester = semesterRepository.save(semester);
        configServiceImpl.cloneLastedSemesterConfig(createdSemester, lastestSemester);
        return createdSemester;
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

    @Override
    public Semester findSemesterById(int id) {
        return semesterRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteSemesterById(int id) {
        Optional<Semester> optionalSemester = semesterRepository.findById(id);

        if (optionalSemester.isPresent()) {
            semesterRepository.delete(optionalSemester.get());
        } else {
            throw new SemesterNotFoundException("Semester not found with ID: " + id);
        }
    }


}