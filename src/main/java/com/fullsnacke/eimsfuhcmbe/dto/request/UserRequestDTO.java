package com.fullsnacke.eimsfuhcmbe.dto.request;

import com.fullsnacke.eimsfuhcmbe.entity.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequestDTO implements Serializable {


    @NotNull
    String fuId;
    @NotNull
    String firstName;

    @NotNull
    String lastName;

    @NotNull
    @Email
    String email;
    String phoneNumber;
    String department;
    Boolean gender;
    Role role;

}
