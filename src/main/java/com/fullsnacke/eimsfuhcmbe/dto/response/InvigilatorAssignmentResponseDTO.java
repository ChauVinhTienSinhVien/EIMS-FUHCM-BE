package com.fullsnacke.eimsfuhcmbe.dto.response;

import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvigilatorAssignmentResponseDTO {
    String fuId;
    Semester semester;
    Set<ExamSlotDetail> examSlots;
}
