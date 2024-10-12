package com.fullsnacke.eimsfuhcmbe.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    USER_READ("user:read"),
    USER_WRITE("user:write"),
    USER_CREATE("user:create"),
    USER_DELETE("user:delete"),

    MANAGER_READ("manager:read"),
    MANAGER_WRITE("manager:write"),
    MANAGER_CREATE("manager:create"),
    MANAGER_DELETE("manager:delete"),

    STAFF_READ("staff:read"),
    STAFF_WRITE("staff:write"),
    STAFF_CREATE("staff:create"),
    STAFF_DELETE("staff:delete"),

    INVIGILATOR_READ("invigilator:read"),
    INVIGILATOR_WRITE("invigilator:write"),
    INVIGILATOR_CREATE("invigilator:create"),
    INVIGILATOR_DELETE("invigilator:delete"),

    SEMESTER_READ("semester:read"),
    SEMESTER_WRITE("semester:write"),
    SEMESTER_CREATE("semester:create"),
    SEMESTER_DELETE("semester:delete"),

    CONFIG_READ("config:read"),
    CONFIG_WRITE("config:write"),
    CONFIG_CREATE("config:create"),
    CONFIG_DELETE("config:delete"),

    SUBJECT_READ("subject:read"),
    SUBJECT_WRITE("subject:write"),
    SUBJECT_CREATE("subject:create"),
    SUBJECT_DELETE("subject:delete"),

    EXAM_READ("exam:read"),
    EXAM_WRITE("exam:write"),
    EXAM_CREATE("exam:create"),
    EXAM_DELETE("exam:delete"),

    EXAM_SLOT_READ("exam_slot:read"),
    EXAM_SLOT_WRITE("exam_slot:write"),
    EXAM_SLOT_CREATE("exam_slot:create"),
    EXAM_SLOT_DELETE("exam_slot:delete"),

    EXAM_SLOT_HALL_READ("exam_slot_hall:read"),
    EXAM_SLOT_HALL_WRITE("exam_slot_hall:write"),
    EXAM_SLOT_HALL_CREATE("exam_slot_hall:create"),
    EXAM_SLOT_HALL_DELETE("exam_slot_hall:delete"),

    INVIGILATOR_ASSIGNMENT_READ("invigilator_assignment:read"),
    INVIGILATOR_ASSIGNMENT_WRITE("invigilator_assignment:write"),
    INVIGILATOR_ASSIGNMENT_CREATE("invigilator_assignment:create"),
    INVIGILATOR_ASSIGNMENT_DELETE("invigilator_assignment:delete"),

    EXAM_SLOT_ROOM_READ("exam_slot_room:read"),
    EXAM_SLOT_ROOM_WRITE("exam_slot_room:write"),
    EXAM_SLOT_ROOM_CREATE("exam_slot_room:create"),
    EXAM_SLOT_ROOM_DELETE("exam_slot_room:delete"),
    ;


    @Getter
    private final String permission;

}
