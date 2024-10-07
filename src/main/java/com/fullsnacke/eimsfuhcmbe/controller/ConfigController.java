package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.ConfigMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.ConfigRequestDto;
import com.fullsnacke.eimsfuhcmbe.dto.response.ConfigResponseDto;
import com.fullsnacke.eimsfuhcmbe.dto.response.UserResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Config;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.enums.ConfigType;
import com.fullsnacke.eimsfuhcmbe.enums.ConfigUnit;
import com.fullsnacke.eimsfuhcmbe.service.ConfigServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/configs")
@Tag(name = "Configuration Controller", description = "API for Configuration Controller")
public class ConfigController {
    @Autowired
    ConfigServiceImpl configServiceImpl;

    @Autowired
    ConfigMapper configMapper;

    @GetMapping
    @Operation(summary = "Get all configs", description = "Retrieve a list of all configuarations")
    public ResponseEntity<List<Config>> getAllConfigs(){
        List<Config> configList = configServiceImpl.getAllConfig();

        if(configList.isEmpty()){
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.ok(configList);
        }
    }

    @PostMapping("/hourly-rate")
    @Operation(summary = "Add a  hourly-rate config", description = "Add a new  hourly-rate configuration")
    public ResponseEntity<ConfigResponseDto> addHourlyRateConfig(@RequestBody ConfigRequestDto configRequestDto){
        Config config = configMapper.toEntity(configRequestDto);

        config.setConfigType(ConfigType.HOURLY_RATE.getValue());
        config.setUnit(ConfigUnit.VND.getValue());

        Config addedConfig = configServiceImpl.addConfig(config);
        URI uri = URI.create("/configs/" + addedConfig.getId());

        ConfigResponseDto configResponseDto = configMapper.toDto(addedConfig);

        return ResponseEntity.created(uri).body(configResponseDto);
    }

    @PostMapping("/allowed-slot")
    @Operation(summary = "Add a config allowed-slot", description = "Add a new allowed-slot configuration")
    public ResponseEntity<ConfigResponseDto> addAllowedSlotConfig(@RequestBody ConfigRequestDto configRequestDto){
        Config config = configMapper.toEntity(configRequestDto);

        config.setConfigType(ConfigType.ALLOWED_SLOT.getValue());
        config.setUnit(ConfigUnit.SLOT.getValue());

        Config addedConfig = configServiceImpl.addConfig(config);
        URI uri = URI.create("/configs/" + addedConfig.getId());

        ConfigResponseDto configResponseDto = configMapper.toDto(addedConfig);
        return ResponseEntity.created(uri).body(configResponseDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a allowed-slot config", description = "Update allowed-slot configuration")
    public ResponseEntity<ConfigResponseDto> updateAllowedSlotConfig(@PathVariable Integer id, @RequestBody ConfigRequestDto configRequestDto){
        Config config = configMapper.toEntity(configRequestDto);
        config.setId(id);

        Config updatedConfig = configServiceImpl.updateConfig(config);

        System.out.println(updatedConfig.getId());

        ConfigResponseDto configResponseDto = configMapper.toDto(updatedConfig);

        System.out.println(configResponseDto.getId());
        return ResponseEntity.ok(configResponseDto);
    }

    @GetMapping("/semester/{semesterId}")
    @Operation(summary = "Get all configs by semester", description = "Retrieve a list of all configuarations by semester")
    public ResponseEntity<List<Config>> getConfigBySemesterId(@PathVariable Integer semesterId){
        List<Config> configList = configServiceImpl.getConfigBySemesterId(semesterId);

        if(configList.isEmpty()){
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.ok(configList);
        }
    }
}
