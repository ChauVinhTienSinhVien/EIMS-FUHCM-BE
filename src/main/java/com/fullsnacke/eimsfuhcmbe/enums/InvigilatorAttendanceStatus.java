package com.fullsnacke.eimsfuhcmbe.enums;

import lombok.Getter;

@Getter
public enum InvigilatorAttendanceStatus {
    PENDING(1),
    APPROVED(2),
    REJECTED(3);

    private final int value;

    InvigilatorAttendanceStatus(int value) {
        this.value = value;
    }

    public static InvigilatorAttendanceStatus fromValue(int value) {
        for (InvigilatorAttendanceStatus status : InvigilatorAttendanceStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid InvigilatorAttendanceStatus value: " + value);
    }
}
