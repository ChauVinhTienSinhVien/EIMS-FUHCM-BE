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
public class SemesterInvigilatorRegistrationResponseDTO {
    int semesterId;
    String semesterName;
    int allowedSlots;
    Set<ExamSlotDetail> examSlotDetailSet;
}
