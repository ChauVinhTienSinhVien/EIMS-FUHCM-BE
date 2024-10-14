package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.response.SubjectResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.Subject;
import com.fullsnacke.eimsfuhcmbe.entity.SubjectExam;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.exception.repository.subject.SubjectNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
import com.fullsnacke.eimsfuhcmbe.repository.SubjectExamRepository;
import com.fullsnacke.eimsfuhcmbe.repository.SubjectRepository;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubjectExamServiceImpl implements SubjectExamService{

    @Autowired
    SubjectExamRepository subjectExamRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    private SemesterRepository semesterRepository;

    @Override
    public List<SubjectExam> getAllSubjectExam() {
        return subjectExamRepository.findAll();
    }

    @Override
    public SubjectExam createSubjectExam(SubjectExam subjectExam) {
        Subject subject = subjectRepository.findById(subjectExam.getSubjectId().getId())
                .orElseThrow(() -> new SubjectNotFoundException("Subject not found with ID: " + subjectExam.getSubjectId().getId()));

        subjectExam.setSubjectId(subject);

        return subjectExamRepository.save(subjectExam);
    }

    @Override
    public SubjectExam updateSubjectExam(SubjectExam subjectExam) {
        Subject subject = subjectRepository.findSubjectsById(subjectExam.getSubjectId().getId());
        if (subject == null)
            throw new SubjectNotFoundException("Subject not found with ID: " + subjectExam.getSubjectId().getId());

        SubjectExam subjectExamInDB = subjectExamRepository.findSubjectExamById(subjectExam.getId());
        if (subjectExamInDB == null)
            throw new SubjectNotFoundException("SubjectExam not found with ID: " + subject.getId());

        subjectExamInDB.setExamType(subjectExam.getExamType());
        subjectExamInDB.setSubjectId(subjectExam.getSubjectId());
        subjectExamInDB.setDuration(subjectExam.getDuration());

        return subjectExamRepository.save(subjectExamInDB);
    }

    @Override
    public SubjectExam findSubjectExamById(int subjectExamId) {
        return subjectExamRepository.findSubjectExamById(subjectExamId);
    }

    @Override
    public List<SubjectExam> getSubjectExamsBySemesterId(int semesterId) {
        Semester semester = semesterRepository.findSemesterById(semesterId);
        List<Subject> subjects = subjectRepository.findBySemesterId(semester);
        List<SubjectExam> subjectExamList = new ArrayList<>();
        for (Subject s:subjects) {
            List<SubjectExam> subjectExams = subjectExamRepository.findSubjectExamsBySubjectId(s);
            subjectExamList.addAll(subjectExams);
        }
        if (subjectExamList.isEmpty()) {
            throw new RuntimeException("No subjects found for Semester ID: " + semesterId);
        }
        return subjectExamList;
    }

    @Override
    public List<SubjectExam> cloneSubjectExamFromPreviousSemester(Semester semester, Semester previousSemester) {
        List<Subject> oldSubjectList = subjectRepository.findBySemesterId(previousSemester);
        List<Subject> newSubjectList = subjectRepository.findBySemesterId(semester);

        List<SubjectExam> subjectExamList = new ArrayList<>();
        for (int i = 0; i < oldSubjectList.size(); i++) {
            Subject oldSubject = oldSubjectList.get(i);
            Subject newSubject = newSubjectList.get(i);
            List<SubjectExam> subjectExams = subjectExamRepository.findSubjectExamsBySubjectId(oldSubject);

            for (SubjectExam subjectExam: subjectExams) {
                System.out.println(subjectExam.getSubjectId().getName());
                SubjectExam newSubjectExam = new SubjectExam();
                newSubjectExam.setSubjectId(newSubject);
                newSubjectExam.setExamType(subjectExam.getExamType());
                newSubjectExam.setDuration(subjectExam.getDuration());
                newSubjectExam.setStaffId(subjectExam.getStaffId());
                subjectExamList.add(newSubjectExam);
            }
        }
        return subjectExamRepository.saveAll(subjectExamList);
    }


}
