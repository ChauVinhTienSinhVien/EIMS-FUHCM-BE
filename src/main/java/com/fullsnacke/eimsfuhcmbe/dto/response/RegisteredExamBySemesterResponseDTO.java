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
public class RegisteredExamBySemesterResponseDTO {
    String fuId;
    String firstName;
    String lastName;
    Set<ExamSlotDetail> examSlotDetails;
}
