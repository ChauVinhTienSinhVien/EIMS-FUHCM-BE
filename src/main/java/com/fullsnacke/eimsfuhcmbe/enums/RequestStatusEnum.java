package com.fullsnacke.eimsfuhcmbe.enums;

public enum RequestStatusEnum {
    PENDING(0),
    APPROVED(1),
    REJECTED(2);

    private final int value;

    RequestStatusEnum(int value) {
        this.value = value;
    }

    public static RequestStatusEnum fromValue(int value) {
        for (RequestStatusEnum status : RequestStatusEnum.values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
