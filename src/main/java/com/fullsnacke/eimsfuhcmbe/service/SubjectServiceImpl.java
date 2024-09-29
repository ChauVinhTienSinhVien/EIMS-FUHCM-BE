package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.Subject;
import com.fullsnacke.eimsfuhcmbe.exception.repository.semester.SemesterNotFoundException;
import com.fullsnacke.eimsfuhcmbe.exception.repository.subject.SubjectNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.SubjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Subject updateSubject(Subject subject, int id) {
        Subject subjectInDB = subjectRepository.findById(id).orElse(null);
        if (subjectInDB == null) {
            throw new SubjectNotFoundException("Subject not found");
        }
        subjectInDB.setName(subject.getName());
        subjectInDB.setSemester(subject.getSemester());
        return subjectRepository.save(subjectInDB);
    }

    @Override
    public Subject findSubjectById(int id) {
        return subjectRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteSubject(int id) {
        Optional<Subject> optionalSubject = subjectRepository.findById(id);
        if (optionalSubject.isPresent()) {
            subjectRepository.delete(optionalSubject.get());
        } else {
            throw new SubjectNotFoundException("Subject not found with ID: " + id);
        }
    }
}
