package com.fullsnacke.eimsfuhcmbe.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponseDTO {
    @JsonIgnore
    private String token;
    private Integer id;
    private String email;
    private String role;
    private boolean isPasswordSet;
}
