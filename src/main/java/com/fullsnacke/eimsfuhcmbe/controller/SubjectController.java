package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.SubjectRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.SubjectResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.Subject;
import com.fullsnacke.eimsfuhcmbe.service.SemesterServiceImpl;
import com.fullsnacke.eimsfuhcmbe.service.SubjectServiceImpl;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private SubjectServiceImpl subjectServiceImpl;
    private ModelMapper modelMapper;
    @Autowired
    private SemesterServiceImpl semesterServiceImpl;


    public SubjectController(SubjectServiceImpl subjectServiceImpl, ModelMapper modelMapper) {
        this.subjectServiceImpl = subjectServiceImpl;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponseDTO>> getAllSubjects() {
        List<Subject> subjectList = subjectServiceImpl.getAllSubjects();
        if (subjectList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<SubjectResponseDTO> subjectResponseDTOList = subjectList.stream()
                    .map(subject -> modelMapper.map(subject, SubjectResponseDTO.class))
                    .toList();
            return ResponseEntity.ok(subjectResponseDTOList);
        }
    }

    @PostMapping
    public ResponseEntity<?> createSubject(@RequestBody @Valid SubjectRequestDTO subjectRequestDTO) {
        Subject subject = new Subject();
        subject.setName(subjectRequestDTO.getName());
        subject.setCode(subjectRequestDTO.getCode());


        Semester semester = semesterServiceImpl.findSemesterById(subjectRequestDTO.getSemesterId());
        if (semester == null) {
            return ResponseEntity.badRequest().body("Semester not found with ID" + subjectRequestDTO.getSemesterId());
        }

        subject.setSemesterId(semester);

        // kiểm tra subject đã tồn tại chưa?

        Subject createdSubject = subjectServiceImpl.createSubject(subject);
        URI uri = URI.create("/subjects/" + createdSubject.getCode());

        SubjectResponseDTO subjectResponseDTO = modelMapper.map(createdSubject, SubjectResponseDTO.class);
        subjectResponseDTO.setSemesterId(createdSubject.getSemesterId().getId());

        return ResponseEntity.created(uri).body(subjectResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubject(@PathVariable("id") int id, @RequestBody @Valid SubjectRequestDTO subjectRequestDTO) {
        try {

            Subject subject = new Subject();
            subject.setName(subjectRequestDTO.getName());
            subject.setCode(subjectRequestDTO.getCode());

            Semester semester = semesterServiceImpl.findSemesterById(subjectRequestDTO.getSemesterId());
            if (semester == null) {
                return ResponseEntity.badRequest().body("Semester not found with ID" + subjectRequestDTO.getSemesterId());
            } // tạo exception mới?

            subject.setSemesterId(semester);

            Subject updatedSubject = subjectServiceImpl.updateSubject(subject, id);

            SubjectResponseDTO subjectResponseDTO = modelMapper.map(updatedSubject, SubjectResponseDTO.class);
            subjectResponseDTO.setSemesterId(updatedSubject.getSemesterId().getId());
            return ResponseEntity.ok(subjectResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponseDTO> findBySubjectCode(@PathVariable("id") int id) {
        Subject subject = subjectServiceImpl.findSubjectById(id);
        if (subject == null) {
            return ResponseEntity.notFound().build();
        }
        SubjectResponseDTO subjectResponseDTO = modelMapper.map(subject, SubjectResponseDTO.class);
        return ResponseEntity.ok(subjectResponseDTO);
    }

}
