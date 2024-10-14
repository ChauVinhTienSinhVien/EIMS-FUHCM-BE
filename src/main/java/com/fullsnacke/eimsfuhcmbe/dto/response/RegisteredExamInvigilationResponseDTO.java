package com.fullsnacke.eimsfuhcmbe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisteredExamInvigilationResponseDTO {
    String fuId;
    List<SemesterInvigilatorRegistrationResponseDTO> semesterInvigilatorRegistration;
}
