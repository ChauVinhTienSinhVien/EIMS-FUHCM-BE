package com.fullsnacke.eimsfuhcmbe.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectRequestDTO {

    @NotNull(message = "Subject code is required")
    String code;

    String name;

    int semesterId;
}
