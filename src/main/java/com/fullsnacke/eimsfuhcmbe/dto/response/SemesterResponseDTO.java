package com.fullsnacke.eimsfuhcmbe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SemesterResponseDTO {


    int id;
    String name;

    Date startAt;
    Date endAt;

    String hourlyConfig;
    int allowedSlotConfig;

}
