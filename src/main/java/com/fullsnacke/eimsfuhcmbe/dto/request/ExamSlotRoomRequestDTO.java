package com.fullsnacke.eimsfuhcmbe.dto.request;

import java.time.Instant;

public class ExamSlotRoomRequestDTO {

    Integer examSlotRoomId;
    Integer examSlotId;
    String roomId;
    String invigilatorFuId;
    String roomInvigilatorName;
    String examSlotHallFuId;
    String hallInvigilatorName;
    Instant startTime;
    Instant endTime;

}
