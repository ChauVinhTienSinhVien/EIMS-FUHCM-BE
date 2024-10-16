package com.fullsnacke.eimsfuhcmbe.enums;

public enum ConfigType {
    HOURLY_RATE("hourly_rate"),
    ALLOWED_SLOT("allowed_slot"),
    TIME_BEFORE_EXAM("time_before_exam"),
    INVIGILATOR_ROOM("invigilator_room");


    private final String value;

    ConfigType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
