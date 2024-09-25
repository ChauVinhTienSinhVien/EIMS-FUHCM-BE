package com.fullsnacke.eimsfuhcmbe.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SemesterResponseDTO {

    int i;
    String name;

    Instant startAt;
    Instant endAt;

}
