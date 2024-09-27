package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.SemesterRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.SemesterResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.exception.repository.semester.SemesterNotFoundException;
import com.fullsnacke.eimsfuhcmbe.service.SemesterServiceImpl;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/semesters")
public class SemesterController {

    private SemesterServiceImpl semesterServiceImpl;
    private ModelMapper modelMapper;

    public SemesterController(SemesterServiceImpl semesterServiceImpl, ModelMapper modelMapper) {
        this.semesterServiceImpl = semesterServiceImpl;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<Semester>> getAllSemesters() {
        List<Semester> semesterList = semesterServiceImpl.getAllSemesters();
        if (semesterList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(semesterList);
        }
    }

    @PostMapping
    public ResponseEntity<SemesterResponseDTO> createSemester(@RequestBody @Valid SemesterRequestDTO semesterRequestDTO) {
        Semester semester = modelMapper.map(semesterRequestDTO, Semester.class);
        Semester createdSemester  = semesterServiceImpl.createSemester(semester);
        URI uri = URI.create("/semesters/" + createdSemester.getId());
        SemesterResponseDTO semesterResponseDTO = modelMapper.map(createdSemester, SemesterResponseDTO.class);
        return ResponseEntity.created(uri).body(semesterResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SemesterResponseDTO> updateSemester(@PathVariable("id") int id, @RequestBody @Valid SemesterRequestDTO semesterRequestDTO) {
        Semester semesterUpdate = modelMapper.map(semesterRequestDTO, Semester.class);
        try {
            Semester updatedSemester = semesterServiceImpl.updateSemester(semesterUpdate, id);
            SemesterResponseDTO semesterResponseDTO = modelMapper.map(updatedSemester, SemesterResponseDTO.class);
            return ResponseEntity.ok(semesterResponseDTO);
        } catch (SemesterNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<SemesterResponseDTO> findSemesterByName(@PathVariable("name") String name){
        Semester semester = semesterServiceImpl.findSemesterByName(name);
        if(semester == null) {
            return ResponseEntity.notFound().build();
        }
        SemesterResponseDTO semesterResponseDTO = modelMapper.map(semester, SemesterResponseDTO.class);
        return ResponseEntity.ok(semesterResponseDTO);

    }

}
