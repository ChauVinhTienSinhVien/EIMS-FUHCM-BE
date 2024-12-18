package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.configuration.ConfigurationHolder;
import com.fullsnacke.eimsfuhcmbe.entity.Config;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.enums.ConfigType;
import com.fullsnacke.eimsfuhcmbe.exception.EntityNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.ConfigRepository;
import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConfigServiceImpl implements ConfigService{

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    ConfigurationHolder configurationHolder;

    @Override
    public Config addConfig(Config config) {
        Semester semester = semesterRepository.findById(config.getSemester().getId()).orElseThrow(() -> new RuntimeException("Semester not found"));

        List<Config> configList = configRepository.findBySemesterId(semester.getId());
        for (Config c : configList) {
            if (c.getConfigType().equals(config.getConfigType())) {
                throw new RuntimeException("Config already exists, update this config instead");
            }
        }
        config.setSemester(semester);
        Config addedConfig = configRepository.save(config);
        configurationHolder.reloadConfigurations();
        return addedConfig;
    }

    @Override
    public List<Config> getAllConfig() {
        return configRepository.findAll();
    }

    @Override
    public List<Config> getConfigBySemesterId(Integer semesterId) {
        List<Config> configList = configRepository.findBySemesterId(semesterId);
        if(configList.isEmpty()){
            throw new EntityNotFoundException(Config.class, "Config not found");
        }
        return configList;
    }

    @Override
    @Transactional
    public List<Config> addAllConfigs(List<Config> configList) {
        List<Config> addedConfigs = configRepository.saveAll(configList);
        configurationHolder.reloadConfigurations();
        return addedConfigs;
    }

    @Override
    public Config getConfigBySemesterIdAndConfigType(Integer semesterId, String configType) {
        Config config = configRepository.findBySemesterIdAndConfigType(semesterId, configType);
        if(config == null){
            throw new EntityNotFoundException(Config.class, "Config not found");
        }
        return config;
    }

    @Override
    public Config updateConfig(Config config) {
        Config configInDb = configRepository.findById(config.getId()).orElseThrow(() -> new RuntimeException("Config not found"));
        configInDb.setValue(config.getValue());
        Config updaConfig = configRepository.save(configInDb);
        configurationHolder.reloadConfigurations();
        return updaConfig;
    }

    @Override
    public void deleteConfig(Integer id) {
        Config config = configRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Config.class, "Config not found"));
        configRepository.deleteById(config.getId());
    }

    @Override
    @Transactional
    public List<Config> addAllConfig(List<Config> configList) {
        List<Config> updatedConfigs =  configRepository.saveAll(configList);
        configurationHolder.reloadConfigurations();
        return updatedConfigs;
    }

    @Override
    @Transactional
    public List<Config> updateAllConfig(List<Config> configList) {
        List<Config> updatedConfigs =  configRepository.saveAll(configList);
        configurationHolder.reloadConfigurations();
        return updatedConfigs;
    }

    @Transactional
    public void cloneLastedSemesterConfig(Semester semester, Semester lastestSemester){
        List<Config> oldConfigList = configRepository.findBySemesterId(lastestSemester.getId());
        List<Config> newConfigList = new ArrayList<>();

        for (Config config : oldConfigList) {
            Config newConfig = new Config();
            newConfig.setConfigType(config.getConfigType());
            newConfig.setUnit(config.getUnit());
            newConfig.setValue(config.getValue());
            newConfig.setSemester(semester);
            newConfigList.add(newConfig);
        }

        configRepository.saveAll(newConfigList);
    }

    public List<Config> getConfigOfLatestSemester() {
        return  configRepository.findBySemesterId(semesterRepository.findFirstByOrderByStartAtDesc().getId());
    }
}
