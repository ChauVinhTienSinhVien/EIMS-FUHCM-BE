package com.fullsnacke.eimsfuhcmbe.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {

    CONFIG_READ("config:read"),
    CONFIG_WRITE("config:write"),
    CONFIG_DELETE("config:delete"),

    USER_READ("user:read"),
    USER_WRITE("user:write"),
    USER_CREATE("user:create"),
    USER_DELETE("user:delete"),

    EMAIL_CREATE("email:create"),

    EXAM_SLOT_READ("exam_slot:read"),
    EXAM_SLOT_WRITE("exam_slot:write"),
    EXAM_SLOT_CREATE("exam_slot:create"),
    EXAM_SLOT_DELETE("exam_slot:delete"),

    EXAM_SLOT_HALL_READ("exam_slot_hall:read"),
    EXAM_SLOT_HALL_WRITE("exam_slot_hall:write"),
    EXAM_SLOT_HALL_CREATE("exam_slot_hall:create"),
    EXAM_SLOT_HALL_DELETE("exam_slot_hall:delete"),

    EXAM_SLOT_ROOM_READ("exam_slot_room:read"),

    INVIGILATOR_REGISTRATION_READ("invigilator_registration:read"),
    INVIGILATOR_REGISTRATION_WRITE("invigilator_registration:write"),
    INVIGILATOR_REGISTRATION_CREATE("invigilator_registration:create"),
    INVIGILATOR_REGISTRATION_DELETE("invigilator_registration:delete"),

    INVIGILATOR_ASSIGNMENT_READ("invigilator_assignment:read"),
    INVIGILATOR_ASSIGNMENT_WRITE("invigilator_assignment:write"),
    INVIGILATOR_ASSIGNMENT_CREATE("invigilator_assignment:create"),
    INVIGILATOR_ASSIGNMENT_DELETE("invigilator_assignment:delete"),

    REQUEST_READ("request:read"),
    REQUEST_WRITE("request:write"),
    REQUEST_CREATE("request:create"),
    REQUEST_DELETE("request:delete"),

    ROOM_READ("room:read"),
    ROOM_WRITE("room:write"),
    ROOM_CREATE("room:create"),
    ROOM_DELETE("room:delete"),

    SEMESTER_READ("semester:read"),
    SEMESTER_WRITE("semester:write"),
    SEMESTER_CREATE("semester:create"),
    SEMESTER_DELETE("semester:delete"),

    SUBJECT_READ("subject:read"),
    SUBJECT_WRITE("subject:write"),
    SUBJECT_CREATE("subject:create"),
    SUBJECT_DELETE("subject:delete"),

    EXAM_READ("exam:read"),
    EXAM_WRITE("exam:write"),
    EXAM_CREATE("exam:create"),
    EXAM_DELETE("exam:delete"),

    ;


    private final String permission;

}
