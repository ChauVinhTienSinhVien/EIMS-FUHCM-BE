package com.fullsnacke.eimsfuhcmbe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamSlotRoomResponseDTO {

    Integer examSlotRoomId;
    Integer examSlotId;
    String roomId;
    String roomInvigilatorFuId;
    String roomInvigilatorName;
    String hallInvigilatorFuId;
    String hallInvigilatorName;

}
