package com.fullsnacke.eimsfuhcmbe.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamSlotDTO {

//    List<String> roomNames;
//    Instant startTime;
//    Instant endTime;
    String hallInvigilatorFuId;
    String hallInvigilatorName;

    public ExamSlotDTO(String invigilatorFuId, String getInvigilatorName) {
//        this.roomNames = roomNames;
//        this.startTime = startTime;
//        this.endTime = endTime;
        this.hallInvigilatorFuId = invigilatorFuId;
        this.hallInvigilatorName = getInvigilatorName;
    }

}
