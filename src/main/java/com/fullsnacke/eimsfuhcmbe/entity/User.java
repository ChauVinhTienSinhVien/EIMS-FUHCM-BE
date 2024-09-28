package com.fullsnacke.eimsfuhcmbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Integer id;

    @Column(name = "fu_id", nullable = false, length = 10)
    String fuId;

    @Column(name = "first_name", nullable = false, length = 50)
    String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    String lastName;

    @Column(name = "email", nullable = false, length = 50, unique = true)
    String email;

    @Column(name = "password", length = 128)
    String password;

    @Column(name = "phone_number", length = 20, unique = true)
    String phoneNumber;

    @Column(name = "department", length = 50)
    String department;

    @ColumnDefault("b'1'")
    @Column(name = "gender")
    Boolean gender;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "role_id")
    Role role;

}