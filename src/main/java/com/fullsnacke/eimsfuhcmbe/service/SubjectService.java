package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.Subject;

import java.util.List;

public interface SubjectService {

    List<Subject> getAllSubjects();
    Subject createSubject(Subject subject);
    void deleteSubject(int id);
    Subject updateSubject(Subject subject, String code);
    Subject findByCode(String code);

}
