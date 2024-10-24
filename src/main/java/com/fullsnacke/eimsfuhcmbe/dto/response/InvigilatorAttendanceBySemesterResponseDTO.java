package com.fullsnacke.eimsfuhcmbe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvigilatorAttendanceBySemesterResponseDTO {
    Integer semesterId;
    Integer invigilatorId;
    String firstName;
    String lastName;
    String email;
    String phoneNum;
}
