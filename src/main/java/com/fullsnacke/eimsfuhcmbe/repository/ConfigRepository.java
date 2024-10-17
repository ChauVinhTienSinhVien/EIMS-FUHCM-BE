package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.Config;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigRepository extends JpaRepository<Config, Integer> {
    List<Config> findBySemesterId(Integer semesterId);
    Config findBySemesterIdAndConfigType(Integer semesterId, String configType);

}
