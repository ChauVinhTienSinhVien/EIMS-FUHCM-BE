package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.Subject;
import com.fullsnacke.eimsfuhcmbe.exception.repository.semester.SemesterNotFoundException;
import com.fullsnacke.eimsfuhcmbe.exception.repository.subject.SubjectNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.SubjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    private SubjectRepository subjectRepository;

    public SubjectServiceImpl(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @Override
    public Subject createSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    @Override
    public void deleteSubject(int id) {

    }

    @Override
    public Subject updateSubject(Subject subject, String code) {
        Subject subjectInDB = subjectRepository.findByCode(code);
        if (subjectInDB == null) {
            throw new SubjectNotFoundException("Subject not found");
        }
        subjectInDB.setName(subject.getName());
        subjectInDB.setSemesterId(subject.getSemesterId());
        return subjectRepository.save(subjectInDB);
    }

    @Override
    public Subject findByCode(String code) {
        return subjectRepository.findByCode(code);
    }
}
