package com.fullsnacke.eimsfuhcmbe.enums;

public enum ExamSlotStatus {

    NEEDS_ROOM_ASSIGNMENT(1),
    PENDING(2),
    APPROVED(3),
    REJECTED(4);

    private final int value;

    ExamSlotStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ExamSlotStatus fromValue(int value) {
        for (ExamSlotStatus status : ExamSlotStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid ExamSlotStatus value: " + value);
    }

}
