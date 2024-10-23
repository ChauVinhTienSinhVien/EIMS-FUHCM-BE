package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.configuration.ConfigurationHolder;
import com.fullsnacke.eimsfuhcmbe.dto.mapper.ConfigMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.ConfigRequestDto;
import com.fullsnacke.eimsfuhcmbe.dto.response.ConfigResponseDto;
import com.fullsnacke.eimsfuhcmbe.entity.Config;
import com.fullsnacke.eimsfuhcmbe.enums.ConfigType;
import com.fullsnacke.eimsfuhcmbe.enums.ConfigUnit;
import com.fullsnacke.eimsfuhcmbe.service.ConfigServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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

    @GetMapping("/hourly-rate")
    @Operation(summary = "Get hourly-rate config", description = "Retrieve hourly-rate configuration")
    public ResponseEntity<ConfigResponseDto> getHourlyRateConfig(){
        String hourlyRate = configurationHolder.getConfig(ConfigType.HOURLY_RATE.getValue());
        ConfigResponseDto configResponseDto = ConfigResponseDto.builder()
                        .configType(ConfigType.HOURLY_RATE.getValue())
                        .unit(ConfigUnit.VND.getValue())
                        .value(hourlyRate)
                        .build();
        configResponseDto.setValue(hourlyRate);
        return ResponseEntity.ok(configResponseDto);
    }

    @GetMapping("/allowed-slot")
    @Operation(summary = "Get allowed-slot config", description = "Retrieve allowed-slot configuration")
    public ResponseEntity<ConfigResponseDto> getAllowedSlotConfig(){
        String allowedSlot = configurationHolder.getConfig(ConfigType.ALLOWED_SLOT.getValue());
        ConfigResponseDto configResponseDto = ConfigResponseDto.builder()
                        .configType(ConfigType.ALLOWED_SLOT.getValue())
                        .unit(ConfigUnit.SLOT.getValue())
                        .value(allowedSlot)
                        .build();
        configResponseDto.setValue(allowedSlot);
        return ResponseEntity.ok(configResponseDto);
    }

    @GetMapping("/time-before-exam")
    @Operation(summary = "Get time-before-exam config", description = "Retrieve time-before-exam configuration")
    public ResponseEntity<ConfigResponseDto> getTimeBeforeExamConfig(){
        String timeBeforeExam = configurationHolder.getConfig(ConfigType.TIME_BEFORE_EXAM.getValue());
        ConfigResponseDto configResponseDto = ConfigResponseDto.builder()
                        .configType(ConfigType.TIME_BEFORE_EXAM.getValue())
                        .unit(ConfigUnit.MINUTE.getValue())
                        .value(timeBeforeExam)
                        .build();
        configResponseDto.setValue(timeBeforeExam);
        return ResponseEntity.ok(configResponseDto);
    }

    @GetMapping("/invigilator-room")
    @Operation(summary = "Get invigilator-room config", description = "Retrieve invigilator-room configuration")
    public ResponseEntity<ConfigResponseDto> getInvigilatorRoomConfig(){
        String invigilatorRoom = configurationHolder.getConfig(ConfigType.INVIGILATOR_ROOM.getValue());
        ConfigResponseDto configResponseDto = ConfigResponseDto.builder()
                        .configType(ConfigType.INVIGILATOR_ROOM.getValue())
                        .unit(ConfigUnit.ROOM.getValue())
                        .value(invigilatorRoom)
                        .build();
        return ResponseEntity.ok(configResponseDto);
    }

    @GetMapping("/time-before-open-registration")
    @Operation(summary = "Get time-before-open-registration config", description = "Retrieve time-before-open-registration configuration")
    public ResponseEntity<ConfigResponseDto> getTimeBeforeOpenRegistrationConfig(){
        String timeBeforeOpenRegistration = configurationHolder.getConfig(ConfigType.TIME_BEFORE_OPEN_REGISTRATION.getValue());
        ConfigResponseDto configResponseDto = ConfigResponseDto
                .builder().configType(ConfigType.TIME_BEFORE_OPEN_REGISTRATION.getValue())
                .unit(ConfigUnit.DAY.getValue())
                .value(timeBeforeOpenRegistration)
                .build();
        return ResponseEntity.ok(configResponseDto);
    }

    @GetMapping("/time-before-close-registration")
    @Operation(summary = "Get time-before-close-registration config", description = "Retrieve time-before-close-registration configuration")
    public ResponseEntity<ConfigResponseDto> getTimeBeforeCloseRegistrationConfig(){
        String timeBeforeCloseRegistration = configurationHolder.getConfig(ConfigType.TIME_BEFORE_CLOSE_REGISTRATION.getValue());
        ConfigResponseDto configResponseDto = ConfigResponseDto
                .builder().configType(ConfigType.TIME_BEFORE_CLOSE_REGISTRATION.getValue())
                .unit(ConfigUnit.DAY.getValue())
                .value(timeBeforeCloseRegistration)
                .build();
        configResponseDto.setValue(timeBeforeCloseRegistration);
        return ResponseEntity.ok(configResponseDto);
    }

    @GetMapping("/time-before-close-request")
    @Operation(summary = "Get time-before-close-request config", description = "Retrieve time-before-close-request configuration")
    public ResponseEntity<ConfigResponseDto> getTimeBeforeCloseRequestConfig(){
        String timeBeforeCloseRequest = configurationHolder.getConfig(ConfigType.TIME_BEFORE_CLOSE_REQUEST.getValue());
        ConfigResponseDto configResponseDto = ConfigResponseDto.builder()
                .configType(ConfigType.TIME_BEFORE_CLOSE_REQUEST.getValue())
                .unit(ConfigUnit.DAY.getValue())
                .value(timeBeforeCloseRequest)
                .build();
        configResponseDto.setValue(timeBeforeCloseRequest);
        return ResponseEntity.ok(configResponseDto);
    }

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

    @PutMapping("{id}")
    @Operation(summary = "Update a config", description = "Update a configuration")
    public ResponseEntity<ConfigResponseDto> updateConfig(@PathVariable Integer id, @RequestBody ConfigRequestDto configRequestDto){
        Config config = configMapper.toEntity(configRequestDto);
        config.setId(id);

        Config updatedConfig = configServiceImpl.updateConfig(config);
        ConfigResponseDto configResponseDto = configMapper.toDto(updatedConfig);

        return ResponseEntity.ok(configResponseDto);
    }

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
