package com.fullsnacke.eimsfuhcmbe.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SemesterRequestDTO {

    @NotNull
    int id;
    @NotNull(message = "Semester name is required")
    String name;

    Instant startAt;
    Instant endAt;
}
