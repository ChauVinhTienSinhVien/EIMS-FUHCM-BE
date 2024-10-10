package com.fullsnacke.eimsfuhcmbe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisteredExamBySemesterResponseDTO {
    String fuId;
    String firstName;
    String lastName;
    Set<ExamSlotDetail> examSlotDetails;
}
