package com.fullsnacke.eimsfuhcmbe.enums;

public enum ConfigType {
    HOURLY_RATE("hourly_rate", ConfigUnit.VND.getValue()),
    ALLOWED_SLOT("allowed_slot", ConfigUnit.SLOT.getValue()),

    TIME_BEFORE_EXAM("time_before_exam", ConfigUnit.MINUTE.getValue()),
    CHECK_IN_TIME_BEFORE_EXAM_SLOT("check_in_time_before_exam_slot", ConfigUnit.MINUTE.getValue()),
    CHECK_OUT_TIME_AFTER_EXAM_SLOT("check_out_time_after_exam_slot", ConfigUnit.MINUTE.getValue()),

    TIME_BEFORE_OPEN_REGISTRATION("time_before_open_registration", ConfigUnit.DAY.getValue()),
    TIME_BEFORE_CLOSE_REGISTRATION("time_before_close_registration", ConfigUnit.DAY.getValue()),
    TIME_BEFORE_CLOSE_REQUEST("time_before_close_request", ConfigUnit.DAY.getValue()),

    EXTRA_INVIGILATOR("extra_invigilator", ConfigUnit.PEOPLE.getValue()),
    INVIGILATOR_ROOM("invigilator_room", ConfigUnit.ROOM.getValue());


    private final String value;
    private final String unit;

    ConfigType(String value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public String getValue() {
        return value;
    }
    public String getUnit() { return unit; }
}
