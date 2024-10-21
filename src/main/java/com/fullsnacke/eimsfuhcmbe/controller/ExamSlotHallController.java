package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.ExamSlotHallMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotHallRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotHallResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotHall;
import com.fullsnacke.eimsfuhcmbe.enums.ExamSlotStatus;
import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotRepository;
import com.fullsnacke.eimsfuhcmbe.service.ExamSlotHallServiceImpl;
import com.fullsnacke.eimsfuhcmbe.service.ExamSlotService;
import com.fullsnacke.eimsfuhcmbe.service.ExamSlotServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/examslothalls")
public class ExamSlotHallController {

    @Autowired
    private ExamSlotHallServiceImpl examSlotHallService;

    @Autowired
    private ExamSlotHallMapper examSlotHallMapper;

    @Autowired
    private ExamSlotRepository examSlotRepository;
    @Autowired
    private ExamSlotServiceImpl examSlotService;

    @GetMapping
    @Operation(summary = "Retrieve all exam slot halls", description = "Fetches a list of all exam slot halls from the system. If no exam slot halls are found, it will return a 204 No Content response.")
    public List<ExamSlotHallResponseDTO> getAllExamSlotHall() {
        List<ExamSlotHallResponseDTO> examSlotHallResponseDTOList = new ArrayList<>();

        List<ExamSlotHall> examSlotHallList = examSlotHallService.getAllExamSlotHall();
        for (ExamSlotHall examSlotHall : examSlotHallList) {
            ExamSlotHallResponseDTO examSlotHallResponseDTO = examSlotHallMapper.toDto(examSlotHall);
            examSlotHallResponseDTOList.add(examSlotHallResponseDTO);
        }

        return examSlotHallResponseDTOList;
    }

    @PostMapping
    @Operation(summary = "Create a new exam slot hall", description = "Creates a new exam slot hall in the system.")
    public List<ExamSlotHallResponseDTO> addExamSlotHall(@RequestBody ExamSlotHallRequestDTO requestDTO) {
        List<ExamSlotHall> examSlotHallList = examSlotHallService.addExamSlotHalls(requestDTO);

        List<ExamSlotHallResponseDTO> examSlotHallResponseDTOList = new ArrayList<>();
        for (ExamSlotHall examSlotHall : examSlotHallList) {
            ExamSlotHallResponseDTO examSlotHallResponseDTO = examSlotHallMapper.toDto(examSlotHall);
            examSlotHallResponseDTOList.add(examSlotHallResponseDTO);
        }
        ExamSlot examSlot = examSlotRepository.findExamSlotById(requestDTO.getExamSlotId());
        examSlot.setStatus(2);
        int requiredInvigilators = requestDTO.getRoomIds().size();
        for (List<String> list : requestDTO.getRoomIds()) {
            requiredInvigilators += list.size();
        }
        examSlot.setRequiredInvigilators(requiredInvigilators);
        examSlot.setStatus(ExamSlotStatus.PENDING.getValue());
        examSlotService.updateExamSlotStatus(examSlot, examSlot.getId());
        return examSlotHallResponseDTOList;
    }

    @PutMapping
    @Operation(summary = "Update an exam slot hall", description = "Updates an exam slot hall in the system.")
    public List<ExamSlotHallResponseDTO> updateExamSlotHall(@RequestBody ExamSlotHallRequestDTO requestDTO) {
        List<ExamSlotHall> examSlotHallList = examSlotHallService.updateExamSlotHall(requestDTO);

        List<ExamSlotHallResponseDTO> examSlotHallResponseDTOList = new ArrayList<>();
        for (ExamSlotHall examSlotHall : examSlotHallList) {
            ExamSlotHallResponseDTO examSlotHallResponseDTO = examSlotHallMapper.toDto(examSlotHall);
            examSlotHallResponseDTOList.add(examSlotHallResponseDTO);
        }
        ExamSlot examSlot = examSlotRepository.findExamSlotById(requestDTO.getExamSlotId());
        examSlot.setStatus(2);
        int requiredInvigilators = requestDTO.getRoomIds().size();
        for (List<String> list : requestDTO.getRoomIds()) {
            requiredInvigilators += list.size();
        }
        examSlot.setRequiredInvigilators(requiredInvigilators);
        examSlot.setStatus(ExamSlotStatus.PENDING.getValue());
        examSlotService.updateExamSlotStatus(examSlot, examSlot.getId());
        return examSlotHallResponseDTOList;
    }


}
