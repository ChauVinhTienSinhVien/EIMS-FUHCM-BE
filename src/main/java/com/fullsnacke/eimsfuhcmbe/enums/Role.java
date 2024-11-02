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
                    EXAM_SLOT_WRITE
            )
    ),

    STAFF(
            Set.of(
                    EXAM_SLOT_READ,
                    EXAM_SLOT_CREATE,
                    EXAM_SLOT_WRITE,
                    EXAM_SLOT_DELETE
            )
    ),
    INVIGILATOR(
            Set.of(
                    CONFIG_READ
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
