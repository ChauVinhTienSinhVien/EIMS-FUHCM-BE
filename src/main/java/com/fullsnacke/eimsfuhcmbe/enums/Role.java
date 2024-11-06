package com.fullsnacke.eimsfuhcmbe.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fullsnacke.eimsfuhcmbe.enums.Permission.*;

@Getter
@RequiredArgsConstructor
public enum Role {

    MANAGER(
            Set.of(
                    CONFIG_READ,
                    CONFIG_WRITE,

                    EMAIL_CREATE,

                    EXAM_SLOT_READ,
                    EXAM_SLOT_WRITE,

                    INVIGILATOR_ASSIGNMENT_READ,

                    INVIGILATOR_ATTENDANCE_READ,
                    INVIGILATOR_ATTENDANCE_WRITE,
                    INVIGILATOR_ATTENDANCE_CREATE,

                    REQUEST_READ,
                    REQUEST_WRITE,

                    SEMESTER_READ,
                    SEMESTER_WRITE,
                    SEMESTER_CREATE,

                    USER_CREATE,
                    USER_READ,
                    USER_WRITE,
                    USER_DELETE
            )
    ),

    STAFF(
            Set.of(
                    EXAM_SLOT_READ,
                    EXAM_SLOT_CREATE,
                    EXAM_SLOT_WRITE,
                    EXAM_SLOT_DELETE,

                    EXAM_SLOT_HALL_READ,
                    EXAM_SLOT_HALL_CREATE,
                    EXAM_SLOT_HALL_WRITE,
                    EXAM_SLOT_HALL_DELETE,

                    INVIGILATOR_ASSIGNMENT_CREATE,
                    INVIGILATOR_ASSIGNMENT_READ,

                    INVIGILATOR_ATTENDANCE_READ,
                    INVIGILATOR_ATTENDANCE_WRITE,
                    INVIGILATOR_ATTENDANCE_CREATE,

                    ROOM_READ,
                    ROOM_WRITE,
                    ROOM_CREATE,
                    ROOM_DELETE,

                    SEMESTER_READ,

                    SUBJECT_CREATE,
                    SUBJECT_READ,
                    SUBJECT_WRITE,
                    SUBJECT_DELETE,

                    SUBJECT_EXAM_CREATE,
                    SUBJECT_EXAM_READ,
                    SUBJECT_EXAM_WRITE,
                    SUBJECT_EXAM_DELETE

            )
    ),
    INVIGILATOR(
            Set.of(
                    CONFIG_READ,

                    INVIGILATOR_ATTENDANCE_READ,

                    INVIGILATOR_REGISTRATION_CREATE,
                    INVIGILATOR_REGISTRATION_READ,
                    INVIGILATOR_REGISTRATION_WRITE,
                    INVIGILATOR_REGISTRATION_DELETE,

                    REQUEST_CREATE,
                    REQUEST_READ,
                    REQUEST_WRITE,
                    REQUEST_DELETE,

                    SEMESTER_READ
            )
    );


    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
