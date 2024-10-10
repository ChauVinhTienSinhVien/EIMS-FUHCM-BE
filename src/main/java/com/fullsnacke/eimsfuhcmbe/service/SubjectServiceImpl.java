package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.Subject;
import com.fullsnacke.eimsfuhcmbe.exception.repository.subject.SubjectNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
import com.fullsnacke.eimsfuhcmbe.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SemesterRepository semesterRepository;
    @Autowired
    private SubjectRepository subjectRepository;


    @Override
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @Override
    public Subject createSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    @Override
    @Transactional
    public List<Subject> saveAll(List<Subject> subjectList) {
        for (Subject subject:  subjectList) {
            Semester semester = semesterRepository.findSemesterById(subject.getSemesterId().getId());
            subject.setSemesterId(semester);
        }
        return subjectRepository.saveAll(subjectList);
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
    public List<Subject> findSubjectBySemesterId(int semesterId) {
        Semester semester = semesterRepository.findSemesterById(semesterId);
        return subjectRepository.findBySemesterId(semester);
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
