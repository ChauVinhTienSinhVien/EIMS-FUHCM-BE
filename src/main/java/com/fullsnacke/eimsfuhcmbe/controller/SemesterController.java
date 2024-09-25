package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.SemesterRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.SemesterResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.UserResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Room;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.User;
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
    public ResponseEntity<Semester> createSemester(@RequestBody @Valid SemesterRequestDTO semesterRequestDTO) {
        Semester semester = modelMapper.map(semesterRequestDTO, Semester.class);
        Semester createdSemester  = semesterServiceImpl.createSemester(semester);
        URI uri = URI.create("/semesters/" + createdSemester.getId());
        return ResponseEntity.created(uri).body(createdSemester);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Semester> updateSemester(@PathVariable("name") int id, @RequestBody @Valid SemesterRequestDTO semesterRequestDTO) {
        Semester semester = modelMapper.map(semesterRequestDTO, Semester.class);
        semester.setId(id);
        Semester updatedSemester = semesterServiceImpl.updateSemester(semester);
        if (updatedSemester == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedSemester);
    }

    @GetMapping("/{name}")
    public ResponseEntity<List<Semester>> findSemesterByNameLike(@PathVariable("name") String name){
        List<Semester> semesterList = semesterServiceImpl.findSemesterByNameLike(name);
        if(semesterList == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(semesterList);
        }
    }

}
