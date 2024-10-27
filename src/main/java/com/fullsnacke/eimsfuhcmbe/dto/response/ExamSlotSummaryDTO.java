package com.fullsnacke.eimsfuhcmbe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamSlotSummaryDTO {

    ExamSlotResponseDTO examSlot;
    int totalInvigilatorsRegistered;
    int totalInvigilatorsAssigned;

}
