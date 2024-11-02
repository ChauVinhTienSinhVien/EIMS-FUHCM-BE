package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.configuration.ConfigurationHolder;
import com.fullsnacke.eimsfuhcmbe.dto.mapper.ConfigMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.ConfigRequestDto;
import com.fullsnacke.eimsfuhcmbe.dto.response.ConfigResponseDto;
import com.fullsnacke.eimsfuhcmbe.entity.Config;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.enums.ConfigType;
import com.fullsnacke.eimsfuhcmbe.enums.ConfigUnit;
import com.fullsnacke.eimsfuhcmbe.repository.ConfigRepository;
import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotRepository;
import com.fullsnacke.eimsfuhcmbe.service.ConfigServiceImpl;
import com.fullsnacke.eimsfuhcmbe.service.ExamSlotServiceImpl;
import com.fullsnacke.eimsfuhcmbe.util.DateValidationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/configs")
@Tag(name = "Configuration Controller", description = "API for Configuration Controller")
public class ConfigController {
    @Autowired
    ConfigServiceImpl configServiceImpl;

    @Autowired
    ConfigMapper configMapper;

    @Autowired
    private ConfigurationHolder configurationHolder;

    @Autowired
    private ExamSlotRepository examSlotRepository;

    @Autowired
    private ConfigRepository configRepository;

    //Manager
    @GetMapping
    @Operation(summary = "Get all configs", description = "Retrieve a list of all configuarations")
    public ResponseEntity<List<ConfigResponseDto>> getAllConfigs() {
        List<Config> configList = configServiceImpl.getAllConfig();
        List<ConfigResponseDto> configResponseDtoList = new ArrayList<>();
        for (Config config : configList) {
            ConfigResponseDto configResponseDto = configMapper.toDto(config);
            configResponseDtoList.add(configResponseDto);
        }

        if (configResponseDtoList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(configResponseDtoList);
        }
    }
    
    //Manager
    @PutMapping("{id}")
    @Operation(summary = "Update a config", description = "Update a configuration")
    public ResponseEntity<ConfigResponseDto> updateConfig(@PathVariable Integer id, @RequestBody ConfigRequestDto configRequestDto){
        Config config = configMapper.toEntity(configRequestDto);
        config.setId(id);

        Config updatedConfig = configRepository.findConfigById(id);

        ExamSlot examSlot = examSlotRepository.findOldestExamSlotBySemester(updatedConfig.getSemester());
        int day =  configurationHolder.getTimeBeforeOpenRegistration();

        if(!DateValidationUtil.isBeforeDeadline(examSlot.getStartAt().toInstant().minus(Duration.ofDays(day)))){
            return ResponseEntity.badRequest().build();
        }

        updatedConfig = configServiceImpl.updateConfig(config);
        ConfigResponseDto configResponseDto = configMapper.toDto(updatedConfig);

        return ResponseEntity.ok(configResponseDto);
    }

    //Invigilator
    //Manager
    @GetMapping("/semester/{semesterId}")
    @Operation(summary = "Get all configs by semester", description = "Retrieve a list of all configuarations by semester")
    public ResponseEntity<List<ConfigResponseDto>> getConfigBySemesterId(@PathVariable Integer semesterId){
        List<Config> configList = configServiceImpl.getConfigBySemesterId(semesterId);

        List<ConfigResponseDto> configResponseDtoList = configList.stream()
                .map(config -> configMapper.toDto(config))
                .toList();

        if(configResponseDtoList.isEmpty()){
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.ok(configResponseDtoList);
        }
    }

    //Manager
    @GetMapping("/latest-semester")
    @Operation(summary = "Get all configs of latest-semester", description = "Retrieve a list of all configuarations of the latest-semester")
    public ResponseEntity<List<ConfigResponseDto>> getConfigOfLatestSemester(){
        List<Config> configList = configServiceImpl.getConfigOfLatestSemester();

        List<ConfigResponseDto> configResponseDtoList = configList.stream()
                .map(config -> configMapper.toDto(config))
                .toList();

        if(configResponseDtoList.isEmpty()){
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.ok(configResponseDtoList);
        }
    }
}
