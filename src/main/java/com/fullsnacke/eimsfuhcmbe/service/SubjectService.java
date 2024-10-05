package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.Subject;

import java.util.List;

public interface SubjectService {

    List<Subject> getAllSubjects();
    Subject createSubject(Subject subject);
    Subject updateSubject(Subject subject);
    Subject findSubjectById(int id);
    void deleteSubject(int id);

}
