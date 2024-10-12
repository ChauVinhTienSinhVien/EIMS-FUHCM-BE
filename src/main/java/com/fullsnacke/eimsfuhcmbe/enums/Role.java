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
                    USER_READ,
                    USER_WRITE,
                    USER_CREATE,
                    USER_DELETE,

                    MANAGER_READ,
                    MANAGER_WRITE,
                    MANAGER_CREATE,
                    MANAGER_DELETE,

                    STAFF_READ,
                    STAFF_WRITE,
                    STAFF_CREATE,
                    STAFF_DELETE,

                    INVIGILATOR_READ,
                    INVIGILATOR_WRITE,
                    INVIGILATOR_CREATE,
                    INVIGILATOR_DELETE,

                    SEMESTER_READ,
                    SEMESTER_WRITE,
                    SEMESTER_CREATE,
                    SEMESTER_DELETE,

                    CONFIG_READ,
                    CONFIG_WRITE,
                    CONFIG_CREATE,
                    CONFIG_DELETE,

                    SUBJECT_READ,
                    SUBJECT_WRITE,
                    SUBJECT_CREATE,
                    SUBJECT_DELETE,

                    EXAM_READ,
                    EXAM_WRITE,
                    EXAM_CREATE,
                    EXAM_DELETE,

                    EXAM_SLOT_READ,
                    EXAM_SLOT_WRITE,
                    EXAM_SLOT_CREATE,
                    EXAM_SLOT_DELETE,

                    EXAM_SLOT_HALL_READ,
                    EXAM_SLOT_HALL_WRITE,
                    EXAM_SLOT_HALL_CREATE,
                    EXAM_SLOT_HALL_DELETE,

                    INVIGILATOR_ASSIGNMENT_READ,
                    INVIGILATOR_ASSIGNMENT_WRITE,
                    INVIGILATOR_ASSIGNMENT_CREATE,
                    INVIGILATOR_ASSIGNMENT_DELETE,

                    EXAM_SLOT_ROOM_READ,
                    EXAM_SLOT_ROOM_WRITE,
                    EXAM_SLOT_ROOM_CREATE,
                    EXAM_SLOT_ROOM_DELETE

            )
    ),

    STAFF(
            Set.of(
                    SUBJECT_READ,
                    SUBJECT_WRITE,
                    SUBJECT_CREATE,
                    SUBJECT_DELETE,

                    EXAM_READ,
                    EXAM_WRITE,
                    EXAM_CREATE,
                    EXAM_DELETE,

                    EXAM_SLOT_READ,
                    EXAM_SLOT_WRITE,
                    EXAM_SLOT_CREATE,
                    EXAM_SLOT_DELETE,

                    EXAM_SLOT_HALL_READ,
                    EXAM_SLOT_HALL_WRITE,
                    EXAM_SLOT_HALL_CREATE,
                    EXAM_SLOT_HALL_DELETE,

                    INVIGILATOR_ASSIGNMENT_READ,
                    INVIGILATOR_ASSIGNMENT_WRITE,
                    INVIGILATOR_ASSIGNMENT_CREATE,
                    INVIGILATOR_ASSIGNMENT_DELETE,

                    EXAM_SLOT_ROOM_READ,
                    EXAM_SLOT_ROOM_WRITE,
                    EXAM_SLOT_ROOM_CREATE,
                    EXAM_SLOT_ROOM_DELETE
            )
    ),
    INVIGILATOR(
            Set.of(
                    INVIGILATOR_ASSIGNMENT_READ,
                    INVIGILATOR_ASSIGNMENT_WRITE,
                    INVIGILATOR_ASSIGNMENT_CREATE,
                    INVIGILATOR_ASSIGNMENT_DELETE,

                    EXAM_SLOT_ROOM_READ
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
