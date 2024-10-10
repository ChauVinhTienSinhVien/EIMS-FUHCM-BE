package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.SemesterRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.SemesterResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Config;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.enums.ConfigType;
import com.fullsnacke.eimsfuhcmbe.exception.repository.semester.SemesterNotFoundException;
import com.fullsnacke.eimsfuhcmbe.service.ConfigServiceImpl;
import com.fullsnacke.eimsfuhcmbe.service.SemesterServiceImpl;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/semesters")
public class SemesterController {

    @Autowired
    private SemesterServiceImpl semesterServiceImpl;
    @Autowired
    private ConfigServiceImpl configServiceImpl;
    @Autowired
    private ModelMapper modelMapper;


    @GetMapping
    public ResponseEntity<List<SemesterResponseDTO>> getAllSemesters() {
        List<Semester> semesterList = semesterServiceImpl.getAllSemesters();
        List<SemesterResponseDTO> semesterResponseDTOS = new ArrayList<>();

        for (Semester semester : semesterList) {
            SemesterResponseDTO semesterResponseDTO = new SemesterResponseDTO();
            semesterResponseDTO.setId(semester.getId());
            semesterResponseDTO.setName(semester.getName());
            semesterResponseDTO.setStartAt(semester.getStartAt());
            semesterResponseDTO.setEndAt(semester.getEndAt());

            Config hourlyRateConfig = configServiceImpl.getConfigBySemesterIdAndConfigType(semester.getId(), ConfigType.HOURLY_RATE.getValue());
            Config allowedSlotConfig = configServiceImpl.getConfigBySemesterIdAndConfigType(semester.getId(), ConfigType.ALLOWED_SLOT.getValue());

            semesterResponseDTO.setHourlyConfig(hourlyRateConfig.getValue());
            semesterResponseDTO.setAllowedSlotConfig(Integer.parseInt(allowedSlotConfig.getValue()));

            semesterResponseDTOS.add(semesterResponseDTO);
        }

        if (semesterList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(semesterResponseDTOS);
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
        //Semester semesterUpdate = modelMapper.map(semesterRequestDTO, Semester.class);
        Semester semester  = semesterServiceImpl.findSemesterById(id);

        if (semester == null) {
            return ResponseEntity.notFound().build();
        }

        Semester semesterUpdate = new Semester();

        semesterUpdate.setName(semesterRequestDTO.getName());
        semesterUpdate.setStartAt(semesterRequestDTO.getStartAt());
        semesterUpdate.setEndAt(semesterRequestDTO.getEndAt());

        List<Config> configList = configServiceImpl.getConfigBySemesterId(semester.getId());

        for (Config config : configList) {
            if (config.getConfigType().equals(ConfigType.HOURLY_RATE.getValue())) {
                config.setValue(semesterRequestDTO.getHourlyConfig());
            } else if (config.getConfigType().equals(ConfigType.ALLOWED_SLOT.getValue())) {
                config.setValue(semesterRequestDTO.getAllowedSlotConfig());
            }
        }

        configServiceImpl.updateAllConfig(configList);

        try {
            Semester updatedSemester = semesterServiceImpl.updateSemester(semesterUpdate, id);
            //SemesterResponseDTO semesterResponseDTO = modelMapper.map(updatedSemester, SemesterResponseDTO.class);

            SemesterResponseDTO semesterResponseDTO = new SemesterResponseDTO();
            semesterResponseDTO.setId(updatedSemester.getId());
            semesterResponseDTO.setName(updatedSemester.getName());
            semesterResponseDTO.setStartAt(updatedSemester.getStartAt());
            semesterResponseDTO.setEndAt(updatedSemester.getEndAt());

            for (Config config : configList) {
                if (config.getConfigType().equals(ConfigType.HOURLY_RATE.getValue())) {
                    semesterResponseDTO.setHourlyConfig(config.getValue());
                } else if (config.getConfigType().equals(ConfigType.ALLOWED_SLOT.getValue())) {
                    semesterResponseDTO.setAllowedSlotConfig(Integer.parseInt(config.getValue()));
                }
            }

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
