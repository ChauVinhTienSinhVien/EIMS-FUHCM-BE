package com.fullsnacke.eimsfuhcmbe.configuration;

import com.fullsnacke.eimsfuhcmbe.entity.Config;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.enums.ConfigType;
import com.fullsnacke.eimsfuhcmbe.repository.ConfigRepository;
import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

import static com.fullsnacke.eimsfuhcmbe.enums.ConfigType.ALLOWED_SLOT;

@Component
public class ConfigurationHolder {
    private Map<String, String> conStringStringMap;

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @PostConstruct
    public void loadConfigurations() {
        Semester semester = semesterRepository.findFirstByOrderByStartAtDesc();
        conStringStringMap = configRepository.findBySemesterId(semester.getId()).stream()
                .collect(Collectors.toMap(Config::getConfigType, Config::getValue));
    }

    public String getConfig(String configType) {
        return conStringStringMap.get(configType);
    }

    public void reloadConfigurations() {
        loadConfigurations();
    }

    public int getAllowedSlot(){
        return Integer.parseInt(conStringStringMap.get(ConfigType.ALLOWED_SLOT.getValue()));
    }

    public double getHourlyRate(){
        return Double.parseDouble(conStringStringMap.get(ConfigType.HOURLY_RATE.getValue()));
    }

    public int getTimeBeforeExam(){
        return Integer.parseInt(conStringStringMap.get(ConfigType.TIME_BEFORE_EXAM.getValue()));
    }

    public String getInvigilatorRoom(){
        return conStringStringMap.get(ConfigType.INVIGILATOR_ROOM.getValue());
    }
    public int getTimeBeforeOpenRegistration(){
        return Integer.parseInt(conStringStringMap.get(ConfigType.TIME_BEFORE_OPEN_REGISTRATION.getValue()));
    }

    public int getTimeBeforeCloseRegistration(){
        return Integer.parseInt(conStringStringMap.get(ConfigType.TIME_BEFORE_CLOSE_REGISTRATION.getValue()));
    }

    public int getTimeBeforeCloseRequest(){
        return Integer.parseInt(conStringStringMap.get(ConfigType.TIME_BEFORE_CLOSE_REQUEST.getValue()));
    }

    public int getCheckInTimeBeforeExamSlot(){
        return Integer.parseInt(conStringStringMap.get(ConfigType.CHECK_IN_TIME_BEFORE_EXAM_SLOT.getValue()));
    }

    public int getCheckOutTimeAfterExamSlot(){
        return Integer.parseInt(conStringStringMap.get(ConfigType.CHECK_OUT_TIME_AFTER_EXAM_SLOT.getValue()));
    }

    public int getExtraInvigilator(){
        return Integer.parseInt(conStringStringMap.get(ConfigType.EXTRA_INVIGILATOR.getValue()));
    }
}
