package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotHallRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotHallResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotHall;
import com.fullsnacke.eimsfuhcmbe.service.ExamSlotHallServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/examslothalls")
public class ExamSlotHallController {

    @Autowired
    private ExamSlotHallServiceImpl examSlotHallService;

    @GetMapping
    @Operation(summary = "Retrieve all exam slot halls", description = "Fetches a list of all exam slot halls from the system. If no exam slot halls are found, it will return a 204 No Content response.")
//    public List<ExamSlotHallResponseDTO> getAllExamSlotHall() {
//        List<ExamSlotHallResponseDTO> examSlotHallResponseDTOList = new ArrayList<>();
//
//
//
//        return examSlotHallResponseDTOList;
//    }
    public List<ExamSlotHall> getAllExamSlotHall() {
        return examSlotHallService.getAllExamSlotHall();
    }

    @PostMapping
    @Operation(summary = "Create a new exam slot hall", description = "Creates a new exam slot hall in the system.")
//    public ExamSlotHallResponseDTO addExamSlotHall(@RequestBody ExamSlotHallRequestDTO requestDTO) {
//        ExamSlotHall examSlotHall = examSlotHallService.addExamSlotHall(requestDTO);
//        return new ExamSlotHallResponseDTO();
//    }
    public List<ExamSlotHall> addExamSlotHalls(@RequestBody ExamSlotHallRequestDTO requestDTO) {

        return examSlotHallService.addExamSlotHalls(requestDTO);
    }


}
