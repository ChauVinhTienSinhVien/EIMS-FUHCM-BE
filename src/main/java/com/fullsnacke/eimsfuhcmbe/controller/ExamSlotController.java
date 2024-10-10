package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.ExamSlotMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.exception.repository.examslot.ExamSlotNotFoundException;
import com.fullsnacke.eimsfuhcmbe.service.ExamSlotServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/examslots")
public class ExamSlotController {

    @Autowired
    private ExamSlotServiceImpl examSlotServiceImpl;

    @Autowired
    private ExamSlotMapper examSlotMapper;

    @GetMapping
    @Operation(summary = "Retrieve all exam slots", description = "Fetches a list of all exam slots from the system. If no exam slots are found, it will return a 204 No Content response.")
    public ResponseEntity<List<ExamSlotResponseDTO>> getAllExamSlots() {
        List<ExamSlot> examSlotList = examSlotServiceImpl.getAllExamSlot();
        if (examSlotList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<ExamSlotResponseDTO> examSlotResponseDTOList = new ArrayList<>();
            for (ExamSlot e:examSlotList) {
                ExamSlotResponseDTO examSlotResponseDTO = examSlotMapper.toDto(e);
                System.out.println(examSlotResponseDTO.getCreatedAt());
                examSlotResponseDTOList.add(examSlotResponseDTO);
            }
            return ResponseEntity.ok(examSlotResponseDTOList);
        }
    }

    @GetMapping("/by-semester/{semesterId}")
    @Operation(summary = "Retrieve all exam slots by semester ID", description = "Fetches a list of all exam slots from the system based on the semester ID. If no exam slots are found, it will return a 204 No Content response.")
    public ResponseEntity<List<ExamSlotResponseDTO>> getExamSlotsBySemesterId(@PathVariable("semesterId") int semesterId) {
        List<ExamSlot> examSlotList = examSlotServiceImpl.getExamSlotsBySemesterId(semesterId);

        if (examSlotList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<ExamSlotResponseDTO> examSlotResponseDTOList = new ArrayList<>();
            for (ExamSlot e:examSlotList) {
                ExamSlotResponseDTO examSlotResponseDTO = examSlotMapper.toDto(e);
                examSlotResponseDTOList.add(examSlotResponseDTO);
            }
            return ResponseEntity.ok(examSlotResponseDTOList);
        }
    }

    @PostMapping
    @Operation(summary = "Create a new exam slot", description = "Creates a new exam slot based on the provided data in the request body. The created exam slot is returned with a 201 Created response.")
    public ResponseEntity<ExamSlotResponseDTO> createExamSlot(@RequestBody @Valid ExamSlotRequestDTO examSlotRequestDTO) {
        ExamSlot examSlot = examSlotMapper.toEntity(examSlotRequestDTO);
        examSlot.setCreatedAt(Instant.now());
        ExamSlot createdExamSlot = examSlotServiceImpl.createExamSlot(examSlot);
        URI uri = URI.create("/examslots/" + createdExamSlot.getId());
        System.out.println(createdExamSlot.getCreatedAt());
        ExamSlotResponseDTO examSlotResponseDTO = examSlotMapper.toDto(createdExamSlot);
        return ResponseEntity.created(uri).body(examSlotResponseDTO);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Bulk import exam slots", description = "Accepts a list of exam slot data in the request body and creates multiple exam slots in the system. Returns a list of the created exam slots.")
    public ResponseEntity<List<ExamSlotResponseDTO>> importExamSlot(@RequestBody @Valid List<ExamSlotRequestDTO> examSlotRequestDTOList) {
        List<ExamSlotResponseDTO> examSlotResponseDTOList = new ArrayList<>();
        for (ExamSlotRequestDTO e:examSlotRequestDTOList) {
            ExamSlot examSlot = examSlotServiceImpl.createExamSlot(examSlotMapper.toEntity(e));
            ExamSlotResponseDTO examSlotResponseDTO = examSlotMapper.toDto(examSlot);
            examSlotResponseDTOList.add(examSlotResponseDTO);
        }
        return ResponseEntity.ok(examSlotResponseDTOList);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an exam slot", description = "Updates an existing exam slot by its ID with the new data provided in the request body. Returns the updated exam slot or a 404 Not Found response if the slot doesn't exist.")
    public ResponseEntity<ExamSlotResponseDTO> updateExamSlot(@PathVariable("id") int id,@RequestBody @Valid ExamSlotRequestDTO examSlotRequestDTO) {
        try{
            ExamSlot examSlot = examSlotMapper.toEntity(examSlotRequestDTO);
            examSlot.setId(id);
            ExamSlotResponseDTO examSlotResponseDTO = examSlotMapper.toDto(examSlotServiceImpl.updateExamSlotExamSlot(examSlot));
            return ResponseEntity.ok(examSlotResponseDTO);
        } catch (ExamSlotNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve an exam slot by ID", description = "Fetches a specific exam slot based on its ID. Returns the exam slot data if found, otherwise returns a 404 Not Found.")
    public ResponseEntity<ExamSlotResponseDTO> getExamSlotById(@PathVariable("id") int id) {
        ExamSlot examSlot = examSlotServiceImpl.findById(id);
        if (examSlot == null) {
            return ResponseEntity.notFound().build();
        } else {
            ExamSlotResponseDTO examSlotResponseDTO = examSlotMapper.toDto(examSlot);
            return ResponseEntity.ok(examSlotResponseDTO);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an exam slot by ID", description = "Deletes a specific exam slot based on its ID. Returns a 204 No Content if successful, or a 404 Not Found if the exam slot does not exist.")
    public ResponseEntity<?> deleteExamSlotById(@PathVariable("id") int id) {
        try {
            examSlotServiceImpl.deleteExamSlot(id);
            return ResponseEntity.noContent().build();
        } catch (ExamSlotNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

}