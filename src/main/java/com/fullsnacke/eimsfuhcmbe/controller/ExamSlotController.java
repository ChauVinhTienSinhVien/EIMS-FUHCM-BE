package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.ExamSlotMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.StudentList;
import com.fullsnacke.eimsfuhcmbe.service.ExamSlotServiceImpl;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
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
    private ModelMapper modelMapper;

    @Autowired
    private ExamSlotMapper examSlotMapper;

    @GetMapping
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

    @PostMapping
    public ResponseEntity<ExamSlotResponseDTO> createExamSlot(@RequestBody @Valid ExamSlotRequestDTO examSlotRequestDTO) {
        ExamSlot examSlot = examSlotMapper.toEntity(examSlotRequestDTO);
        examSlot.setCreatedAt(Instant.now());
        ExamSlot createdExamSlot = examSlotServiceImpl.createExamSlot(examSlot);
        URI uri = URI.create("/examslots/" + createdExamSlot.getId());
        System.out.println(createdExamSlot.getCreatedAt());
        ExamSlotResponseDTO examSlotResponseDTO = examSlotMapper.toDto(createdExamSlot);
        return ResponseEntity.created(uri).body(examSlotResponseDTO);
    }

    @PostMapping("/import")
    public ResponseEntity<List<ExamSlotResponseDTO>> importExamSlot(@RequestBody @Valid List<ExamSlotRequestDTO> examSlotRequestDTOList) {
        List<ExamSlotResponseDTO> examSlotResponseDTOList = new ArrayList<>();
        for (ExamSlotRequestDTO e:examSlotRequestDTOList) {
            ExamSlot examSlot = examSlotServiceImpl.createExamSlot(examSlotMapper.toEntity(e));
            ExamSlotResponseDTO examSlotResponseDTO = examSlotMapper.toDto(examSlot);
            examSlotResponseDTOList.add(examSlotResponseDTO);
        }
        return ResponseEntity.ok(examSlotResponseDTOList);
    }

}