package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.Subject;

import java.util.List;

public interface SubjectService {

    List<Subject> getAllSubjects();
    Subject createSubject(Subject subject);
    List<Subject> saveAll(List<Subject> subjectList);
    Subject updateSubject(Subject subject);
    Subject findSubjectById(int id);
    void deleteSubject(int id);
    List<Subject> findSubjectBySemesterId(int id);

    List<Subject> cloneSubjectFromPreviousSemester(Semester previousSemester,Semester semester);

    List<Subject> addListOfSubjects(List<Subject> subjects);
}
