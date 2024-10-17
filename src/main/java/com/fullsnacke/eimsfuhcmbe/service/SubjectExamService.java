package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.SubjectExam;

import java.util.List;

public interface SubjectExamService {

    List<SubjectExam> getAllSubjectExam();
    SubjectExam createSubjectExam(SubjectExam subjectExam);
    SubjectExam updateSubjectExam(SubjectExam subjectExam);
    SubjectExam findSubjectExamById(int id);
    List<SubjectExam> getSubjectExamsBySemesterId(int semesterId);
    List<SubjectExam> cloneSubjectExamFromPreviousSemester(Semester previousSemester, Semester semester);
}
