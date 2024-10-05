package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.Subject;
import com.fullsnacke.eimsfuhcmbe.exception.repository.subject.SubjectNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.SubjectRepository;
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
    public Subject updateSubject(Subject subject) {
        int id = subject.getId();
        Subject subjectInDB = subjectRepository.findSubjectsById(id);
        if (subjectInDB == null) {
            throw new SubjectNotFoundException("Subject not found");
        }
        return subjectRepository.save(subject);
    }

    @Override
    public Subject findSubjectById(int id) {
        return subjectRepository.findSubjectsById(id);
    }

    @Override
    public void deleteSubject(int id) {
        Subject subject = subjectRepository.findSubjectsById(id);
        if (subject != null) {
            subjectRepository.delete(subject);
        } else {
            throw new SubjectNotFoundException("Subject not found with ID: " + id);
        }
    }
}
