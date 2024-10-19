package com.fullsnacke.eimsfuhcmbe.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SemesterRequestDTO {

    @NotNull(message = "Semester name is required")
    String name;

    Date startAt;
    Date endAt;
}
