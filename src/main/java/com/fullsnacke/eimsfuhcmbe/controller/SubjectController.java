package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.SubjectMapper;
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

    @Autowired
    private SubjectServiceImpl subjectServiceImpl;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SubjectMapper subjectMapper;




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
        Subject subject = subjectMapper.toEntity(subjectRequestDTO);

        System.out.println("Semester: " + subject.getSemesterId());

        if (subject.getSemesterId() == null) {
            return ResponseEntity.badRequest().body("Semester not found with ID" + subjectRequestDTO.getSemesterName());
        }


        Subject createdSubject = subjectServiceImpl.createSubject(subject);
        URI uri = URI.create("/subjects/" + createdSubject.getCode());

        SubjectResponseDTO subjectResponseDTO = subjectMapper.toDto(createdSubject);

        return ResponseEntity.created(uri).body(subjectResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubject(@PathVariable("id") int id, @RequestBody @Valid SubjectRequestDTO subjectRequestDTO) {
        try {

            Subject subject = subjectMapper.toEntity(subjectRequestDTO);
            subject.setId(id);
            if (subject.getSemesterId() == null) {
                return ResponseEntity.badRequest().body("Semester not found with ID" + subjectRequestDTO.getSemesterName());
            } // tạo exception mới?


            Subject updatedSubject = subjectServiceImpl.updateSubject(subject);

            SubjectResponseDTO subjectResponseDTO = subjectMapper.toDto(updatedSubject);
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
        SubjectResponseDTO subjectResponseDTO = subjectMapper.toDto(subject);
        return ResponseEntity.ok(subjectResponseDTO);
    }

}
