package com.fullsnacke.eimsfuhcmbe.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamSlotRoomRequestDTO {

    Integer examSlotRoomId;
    Integer examHallId;
    String roomId;
    String roomInvigilatorFuId;

}
