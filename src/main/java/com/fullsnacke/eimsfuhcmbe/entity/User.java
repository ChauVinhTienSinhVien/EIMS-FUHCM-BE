package com.fullsnacke.eimsfuhcmbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;


@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Integer id;

    @Column(name = "fu_id", nullable = false, length = 10, unique = true)
    String fuId;

    @Column(name = "email", nullable = false, length = 50, unique = true)
    String email;

    @Column(name = "password", length = 128)
    String password;

    @Column(name = "phone_number", length = 20, unique = true)
    String phoneNumber;

    @Column(name = "first_name", nullable = false, length = 50)
    String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    String lastName;

    @Column(name = "department", length = 50)
    String department;

    @ColumnDefault("b'1'")
    @Column(name = "gender")
    Boolean gender;

    @Column(name = "created_at")
    @CreatedDate
    Instant createdAt;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    Boolean isDeleted;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    Role role;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        com.fullsnacke.eimsfuhcmbe.enums.Role roleEnum = com.fullsnacke.eimsfuhcmbe.enums.Role.valueOf(role.getName().toUpperCase());
        return roleEnum.getAuthorities();
    }

    @Override
    public String getUsername() {
        return email;
    }

    public boolean isPasswordSet() {
        return password != null;
    }
}