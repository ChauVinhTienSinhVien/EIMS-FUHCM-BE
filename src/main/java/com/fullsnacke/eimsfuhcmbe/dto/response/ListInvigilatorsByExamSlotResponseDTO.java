package com.fullsnacke.eimsfuhcmbe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListInvigilatorsByExamSlotResponseDTO {
    int examSlotId;
    ZonedDateTime startAt;
    ZonedDateTime endAt;
    Set<UserRegistrationResponseDTO> userRegistrationResponseDTOSet;
}
