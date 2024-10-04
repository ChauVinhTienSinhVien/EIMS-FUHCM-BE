package com.fullsnacke.eimsfuhcmbe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvigilatorAssignmentResponseDTO {
    Integer invigilatorAssignmentId;
    String fuId;
    Integer examSlotId;
    String examType;
    int semesterId;
}
