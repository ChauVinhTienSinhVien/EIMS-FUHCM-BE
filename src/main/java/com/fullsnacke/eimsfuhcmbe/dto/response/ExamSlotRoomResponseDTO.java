package com.fullsnacke.eimsfuhcmbe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamSlotRoomResponseDTO {

    //version 1
//    Integer examSlotRoomId;
//    Integer examSlotId;
//    String roomId;
//    String roomInvigilatorFuId;
//    String roomInvigilatorName;
//    String hallInvigilatorFuId;
//    String hallInvigilatorName;


//    // version 2
//    Integer id;
//    String roomName;
//    int floor;
//    String campus;
//    String invigilatorFuId;
//    String invigilatorName;
//    String examSlotSubjectName;
//    String examSlotStartTime;
//    String examSlotEndTime;

    // version 3
    Integer examSlotRoomId;

    Integer examSlotId;
    String examSlotSubjectName;
    ZonedDateTime startAt;
    ZonedDateTime endAt;

    String roomId;
    String roomName;
    int floor;
    String campus;

    int roomInvigilatorAssignmentId;
    String roomInvigilatorFuId;
    String roomInvigilatorFirstName;
    String roomInvigilatorLastName;

    int hallInvigilatorAssignmentId;
    String hallInvigilatorFuId;
    String hallInvigilatorFirstName;
    String hallInvigilatorLastName;

}
