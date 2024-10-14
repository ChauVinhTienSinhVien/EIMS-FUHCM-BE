package com.fullsnacke.eimsfuhcmbe.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvigilatorRegistrationRequestDTO {
    String fuId;
    Set<Integer> examSlotId;
}
