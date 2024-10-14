package com.fullsnacke.eimsfuhcmbe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamSlotHallResponseDTO {

    int id;
    int examSlotId;
    int hallInvigilatorId;
    String hallInvigilatorFuId;
    String hallInvigilatorName;
//    // ExamSlotDTO examSlotDTO;
//    List<String> roomNames;
//    Instant startTime;
//    Instant endTime;
//    String invigilatorFuId;
//    String getInvigilatorName;

}
