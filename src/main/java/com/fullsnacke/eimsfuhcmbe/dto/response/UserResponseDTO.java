package com.fullsnacke.eimsfuhcmbe.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fullsnacke.eimsfuhcmbe.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDTO {
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

    int role;
}
