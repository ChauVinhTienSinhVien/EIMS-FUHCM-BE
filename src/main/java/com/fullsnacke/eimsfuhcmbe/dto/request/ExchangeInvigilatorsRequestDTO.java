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
    String newInvigilatorFuId;
    int requestId;
    String status; //REJECTED or APPROVED
    String note;
}
