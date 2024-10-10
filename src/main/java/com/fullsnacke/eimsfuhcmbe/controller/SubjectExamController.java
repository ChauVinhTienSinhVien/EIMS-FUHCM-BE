package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.SubjectExamMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.SubjectExamRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.SubjectExamResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.Subject;
import com.fullsnacke.eimsfuhcmbe.entity.SubjectExam;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.exception.repository.subjectexam.SubjectExamNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
import com.fullsnacke.eimsfuhcmbe.repository.SubjectRepository;
import com.fullsnacke.eimsfuhcmbe.service.SubjectExamServiceImpl;
import com.fullsnacke.eimsfuhcmbe.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/subjectexams")
public class SubjectExamController {

    @Autowired
    private SubjectExamServiceImpl subjectExamServiceImpl;

    @Autowired
    private SubjectExamMapper subjectExamMapper;

    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private SemesterRepository semesterRepository;

    @GetMapping
    public ResponseEntity<List<SubjectExamResponseDTO>> getAllSubjectExams() {
        List<SubjectExam> subjectExamList = subjectExamServiceImpl.getAllSubjectExam();
        if (subjectExamList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<SubjectExamResponseDTO> subjectExamResponseDTOList = new ArrayList<>();
            for (SubjectExam subjectExam : subjectExamList) {
                SubjectExamResponseDTO subjectExamResponseDTO = subjectExamMapper.toDto(subjectExam);
                subjectExamResponseDTOList.add(subjectExamResponseDTO);
            }
            return ResponseEntity.ok(subjectExamResponseDTOList);
        }
    }

    @PostMapping
    public ResponseEntity<?> createSubjectExam(@RequestBody @Valid SubjectExamRequestDTO subjectExamRequestDTO) {
        SubjectExam subjectExam = subjectExamMapper.toEntity(subjectExamRequestDTO);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User currentUser = userServiceImpl.getUserByEmail(email);

        subjectExam.setStaffId(currentUser);

        SubjectExam createdSubjectExam = subjectExamServiceImpl.createSubjectExam(subjectExam);
        URI uri = URI.create("/subject-exams/" + createdSubjectExam.getId());

        SubjectExamResponseDTO subjectExamResponseDTO = subjectExamMapper.toDto(createdSubjectExam);

        return ResponseEntity.created(uri).body(subjectExamResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectExamResponseDTO> updateSubjectExam(@PathVariable("id") int id, @RequestBody @Valid SubjectExamRequestDTO subjectExamRequestDTO) {
        SubjectExam subjectExam = subjectExamMapper.toEntity(subjectExamRequestDTO);
        try {
            subjectExam.setId(id);
            Subject subject = subjectRepository.findSubjectsById(subjectExam.getSubjectId().getId());
            subjectExam.setSubjectId(subject);
            System.out.println(subjectExam.getSubjectId().getName() + " id của subject");

            SubjectExam updatedSubjectExam = subjectExamServiceImpl.updateSubjectExam(subjectExam);
            System.out.println(updatedSubjectExam.getSubjectId().getSemesterId() + " id của semester");
            SubjectExamResponseDTO subjectExamResponseDTO = subjectExamMapper.toDto(updatedSubjectExam);
            return ResponseEntity.ok(subjectExamResponseDTO);
        } catch (SubjectExamNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectExamResponseDTO> findSubjectExamById(@PathVariable("id") int id) {
        SubjectExam subjectExam = subjectExamServiceImpl.findSubjectExamById(id);
        if (subjectExam == null) {
            return ResponseEntity.notFound().build();
        }
        SubjectExamResponseDTO subjectExamResponseDTO = subjectExamMapper.toDto(subjectExam);
        return ResponseEntity.ok(subjectExamResponseDTO);
    }

    @GetMapping("/by-semester/{semesterId}")
    public ResponseEntity<List<SubjectExamResponseDTO>> findSubjectExamBySemesterId(@PathVariable("semesterId") int semesterId) {
        List<SubjectExam> subjectExamList = subjectExamServiceImpl.getSubjectExamsBySemesterId(semesterId);

        if (subjectExamList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<SubjectExamResponseDTO> subjectExamResponseDTOList = new ArrayList<>();
        for (SubjectExam s:subjectExamList) {
            SubjectExamResponseDTO subjectExamResponseDTO = subjectExamMapper.toDto(s);
            subjectExamResponseDTOList.add(subjectExamResponseDTO);
        }

        return ResponseEntity.ok(subjectExamResponseDTOList);
    }

}
