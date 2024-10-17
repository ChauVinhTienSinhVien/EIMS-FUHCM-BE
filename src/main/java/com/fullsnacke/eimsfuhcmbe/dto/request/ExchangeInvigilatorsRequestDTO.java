package com.fullsnacke.eimsfuhcmbe.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExchangeInvigilatorsRequestDTO {
    int examSlotId;
    String oldInvigilatorFuId;
    String newInvigilatorFuId;
    int requestId;
    String status; //REJECTED or ACCEPTED
}
