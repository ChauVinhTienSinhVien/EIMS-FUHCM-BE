package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.SubjectRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.SubjectResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Subject;
import com.fullsnacke.eimsfuhcmbe.service.SubjectServiceImpl;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private SubjectServiceImpl subjectServiceImpl;
    private ModelMapper modelMapper;


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
    public ResponseEntity<SubjectResponseDTO> createSubject(@RequestBody @Valid SubjectRequestDTO subjectRequestDTO) {
        Subject subject = modelMapper.map(subjectRequestDTO, Subject.class);
        Subject createdSubject = subjectServiceImpl.createSubject(subject);
        URI uri = URI.create("/subjects/" + createdSubject.getCode());
        SubjectResponseDTO subjectResponseDTO = modelMapper.map(createdSubject, SubjectResponseDTO.class);
        return ResponseEntity.created(uri).body(subjectResponseDTO);
    }

    @PutMapping("/{code}")
    public ResponseEntity<SubjectResponseDTO> updateSubject(@PathVariable("code") String code, @RequestBody @Valid SubjectRequestDTO subjectRequestDTO) {
        Subject subject = modelMapper.map(subjectRequestDTO, Subject.class);
        try {
            Subject updatedSubject = subjectServiceImpl.updateSubject(subject, code);
            SubjectResponseDTO subjectResponseDTO = modelMapper.map(updatedSubject, SubjectResponseDTO.class);
            return ResponseEntity.ok(subjectResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{code}")
    public ResponseEntity<SubjectResponseDTO> findBySubjectCode(@PathVariable("code") String code) {
        Subject subject = subjectServiceImpl.findByCode(code);
        if (subject == null) {
            return ResponseEntity.notFound().build();
        }
        SubjectResponseDTO subjectResponseDTO = modelMapper.map(subject, SubjectResponseDTO.class);
        return ResponseEntity.ok(subjectResponseDTO);
    }

}
