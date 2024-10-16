package com.fullsnacke.eimsfuhcmbe.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    private String token;
    private String type = "Bearer";
    private Integer id;
    private String email;
    private String role;
}
