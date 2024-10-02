package com.fullsnacke.eimsfuhcmbe.controller;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/examslots")
public class ExamSlotController {

    private final ExamSlotServiceImpl examSlotServiceImpl;
    private final ModelMapper modelMapper;

    public ExamSlotController(ExamSlotServiceImpl examSlotServiceImpl, ModelMapper modelMapper) {
        this.examSlotServiceImpl = examSlotServiceImpl;
        this.modelMapper = modelMapper;
    }

}
