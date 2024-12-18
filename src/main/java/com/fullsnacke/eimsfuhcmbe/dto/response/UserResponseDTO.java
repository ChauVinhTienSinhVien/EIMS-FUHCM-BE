package com.fullsnacke.eimsfuhcmbe.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fullsnacke.eimsfuhcmbe.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    boolean gender;

    boolean isPasswordSet;

    Instant createdAt;

    int role;
}
