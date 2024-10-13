package com.fullsnacke.eimsfuhcmbe.dto.response;

import java.time.Instant;
import java.util.List;

public class ExamSlotHallResponseDTO {

    int examSlotHallId;
    String hallInvigilatorFuId;
    String hallInvigilatorName;
    // ExamSlotDTO examSlotDTO;
    List<String> roomNames;
    Instant startTime;
    Instant endTime;
    String invigilatorFuId;
    String getInvigilatorName;

}
