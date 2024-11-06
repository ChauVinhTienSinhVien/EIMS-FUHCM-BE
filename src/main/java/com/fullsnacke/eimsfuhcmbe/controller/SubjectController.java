package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.SubjectMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.SubjectRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.SubjectResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.Subject;
import com.fullsnacke.eimsfuhcmbe.service.SemesterServiceImpl;
import com.fullsnacke.eimsfuhcmbe.service.SubjectServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    @Autowired
    private SubjectServiceImpl subjectServiceImpl;

    @Autowired
    private SubjectMapper subjectMapper;

    //STAFF
    @GetMapping
    @PreAuthorize("hasAuthority('subject:read')")
    @Operation(summary = "Get all subjects", description = "Retrieve a list of all subjects")
    public ResponseEntity<List<SubjectResponseDTO>> getAllSubjects() {
        List<Subject> subjectList = subjectServiceImpl.getAllSubjects();
        if (subjectList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<SubjectResponseDTO> subjectResponseDTOList = new ArrayList<>();
            for (Subject subject:subjectList) {
                SubjectResponseDTO subjectResponseDTO = subjectMapper.toDto(subject);
                subjectResponseDTOList.add(subjectResponseDTO);
            }
            return ResponseEntity.ok(subjectResponseDTOList);
        }
    }

    //STAFF
    @PostMapping
    @PreAuthorize("hasAuthority('subject:create')")
    @Operation(summary = "Add a subject", description = "Add a new subject")
    public ResponseEntity<?> createSubject(@RequestBody @Valid SubjectRequestDTO subjectRequestDTO) {
        Subject subject = subjectMapper.toEntity(subjectRequestDTO);

        System.out.println("Semester: " + subject.getSemesterId());

        if (subject.getSemesterId() == null) {
            return ResponseEntity.badRequest().body("Semester not found with ID" + subjectRequestDTO.getSemesterId());
        }


        Subject createdSubject = subjectServiceImpl.createSubject(subject);
        URI uri = URI.create("/subjects/" + createdSubject.getCode());

        SubjectResponseDTO subjectResponseDTO = subjectMapper.toDto(createdSubject);

        return ResponseEntity.created(uri).body(subjectResponseDTO);
    }

    //STAFF
    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('subject:create')")
    @Operation(summary = "Add multiple subjects", description = "Add multiple subjects")
    public ResponseEntity<List<SubjectResponseDTO>> importSubjects(@RequestBody @Valid List<SubjectRequestDTO> subjectRequestDTOList) {
        List<Subject> subjectList = subjectRequestDTOList.stream()
                .map(subjectRequestDTO ->subjectMapper.toEntity(subjectRequestDTO))
                .toList();

        List<Subject> addedSubjects = subjectServiceImpl.saveAll(subjectList);

        List<SubjectResponseDTO> subjectResponseDTOList = new ArrayList<>();
        for (Subject subject:addedSubjects) {
            System.out.println(subject.getSemesterId().getName());
            SubjectResponseDTO subjectResponseDTO = subjectMapper.toDto(subject);
            subjectResponseDTOList.add(subjectResponseDTO);
        }
        return ResponseEntity.ok(subjectResponseDTOList);
    }

    //STAFF
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('subject:write')")
    @Operation(summary = "Update a subject", description = "Update an existing subject")
    public ResponseEntity<?> updateSubject(@PathVariable("id") int id, @RequestBody @Valid SubjectRequestDTO subjectRequestDTO) {
        try {

            Subject subject = subjectMapper.toEntity(subjectRequestDTO);
            subject.setId(id);
            if (subject.getSemesterId() == null) {
                return ResponseEntity.badRequest().body("Semester not found with ID" + subjectRequestDTO.getSemesterId());
            } // tạo exception mới?


            Subject updatedSubject = subjectServiceImpl.updateSubject(subject);

            SubjectResponseDTO subjectResponseDTO = subjectMapper.toDto(updatedSubject);
            return ResponseEntity.ok(subjectResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //STAFF
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('subject:read')")
    @Operation(summary = "Get a subject by id", description = "Retrieve a subject by id")
    public ResponseEntity<SubjectResponseDTO> findBySubjectCode(@PathVariable("id") int id) {
        Subject subject = subjectServiceImpl.findSubjectById(id);
        if (subject == null) {
            return ResponseEntity.notFound().build();
        }
        SubjectResponseDTO subjectResponseDTO = subjectMapper.toDto(subject);
        return ResponseEntity.ok(subjectResponseDTO);
    }

    //STAFF
    @GetMapping("/by-semester/{semesterId}")
    @PreAuthorize("hasAuthority('subject:read')")
    @Operation(summary = "Get subjects by semester id", description = "Retrieve a list of subjects by semester id")
    public ResponseEntity<List<SubjectResponseDTO>> getSubjectBySemesterId(@PathVariable("semesterId") int semesterId) {
        List<Subject> subjectList = subjectServiceImpl.findSubjectBySemesterId(semesterId);

        if (subjectList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<SubjectResponseDTO> subjectResponseDTOList = new ArrayList<>();
        for (Subject subject : subjectList) {
            SubjectResponseDTO subjectResponseDTO = subjectMapper.toDto(subject);
            subjectResponseDTOList.add(subjectResponseDTO);
        }

        return ResponseEntity.ok(subjectResponseDTOList);
    }

    //STAFF
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('subject:delete')")
    @Operation(summary = "Delete a subject by id", description = "Delete a subject by id")
    public ResponseEntity<?> deleteSubject(@PathVariable("id") int id) {
        try {
            subjectServiceImpl.deleteSubject(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
