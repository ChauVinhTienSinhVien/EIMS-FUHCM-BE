package com.fullsnacke.eimsfuhcmbe.enums;

import lombok.Getter;

@Getter
public enum InvigilatorRoleEnum {
    HALL_INVIGILATOR(0),
    IN_ROOM_INVIGILATOR(1)
    ;

    private int roleNumber;

    InvigilatorRoleEnum(int roleNumber) {
        this.roleNumber = roleNumber;
    }
}
