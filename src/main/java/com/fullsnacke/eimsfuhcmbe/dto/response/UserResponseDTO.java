package com.fullsnacke.eimsfuhcmbe.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fullsnacke.eimsfuhcmbe.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    @NotNull
    private String fuId;
    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @Email
    private String email;
    private String phoneNumber;
    private String department;
    private Boolean gender;

    @JsonIgnore
    private Role role;
}
