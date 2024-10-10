package com.fullsnacke.eimsfuhcmbe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SemesterInvigilatorAssignmentResponseDTO {
    int semesterId;
    String semesterName;
    Set<ExamSlotDetail> examSlotDetailSet;
}
