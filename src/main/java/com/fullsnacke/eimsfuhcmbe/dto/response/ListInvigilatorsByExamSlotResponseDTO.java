package com.fullsnacke.eimsfuhcmbe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListInvigilatorsByExamSlotResponseDTO {
    int examSlotId;
    Instant startAt;
    Instant endAt;
    Set<UserResponseDTO> userResponseDTOSet;
}
