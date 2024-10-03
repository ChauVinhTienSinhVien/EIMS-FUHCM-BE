package com.fullsnacke.eimsfuhcmbe.dto.request;

import com.fullsnacke.eimsfuhcmbe.entity.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "fuId must not be blank")
    String fuId;

    @NotBlank(message = "firstName must not be blank")
    String firstName;

    @NotBlank(message = "lastName must not be blank")
    String lastName;

    @NotBlank(message = "email must not be blank")
    @Email
    String email;
    String phoneNumber;
    String department;
    Boolean gender;

    String passWord;

    int role;

}
