package com.fullsnacke.eimsfuhcmbe.controller;


import com.fullsnacke.eimsfuhcmbe.dto.mapper.InvigilatorAttendanceMapper;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAttendanceResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAttendance;
import com.fullsnacke.eimsfuhcmbe.service.InvigilatorAttendanceServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/invigilator-attendance")

public class InvigilatorAttendanceController {
    @Autowired
    InvigilatorAttendanceMapper invigilatorAttendanceMapper;

    @Autowired
    private InvigilatorAttendanceServiceImpl invigilatorAttendanceService;

    @GetMapping
    @Operation(summary = "Get all invigilator attendance", description = "Retrieve a list of all invigilator attendance records")
    public ResponseEntity<List<InvigilatorAttendanceResponseDTO>> getAllInvigilatorAttendance() {
        List<InvigilatorAttendance> attendanceList = invigilatorAttendanceService.getInvigilatorAttendances();

        if (attendanceList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<InvigilatorAttendanceResponseDTO> attendanceResponseDTOList = attendanceList.stream()
                    .map(attendance -> invigilatorAttendanceMapper.toResponseDTO(attendance))
                    .toList();
            return ResponseEntity.ok(attendanceResponseDTOList);
        }
    }
}
