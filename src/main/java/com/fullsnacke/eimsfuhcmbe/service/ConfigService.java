package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.Config;

import java.util.List;

public interface ConfigService {
    Config addConfig(Config config);

    List<Config> getAllConfig();

    List<Config> getConfigBySemesterId(Integer semesterId);

    Config getConfigBySemesterIdAndConfigType(Integer semesterId, String configType);

    Config updateConfig(Config config);

    void deleteConfig(Integer id);
}
