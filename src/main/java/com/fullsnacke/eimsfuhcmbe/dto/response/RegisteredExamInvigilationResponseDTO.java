package com.fullsnacke.eimsfuhcmbe.dto.response;

import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisteredExamInvigilationResponseDTO {
    String fuId;
    List<SemesterInvigilatorAssignmentResponseDTO> semesterInvigilatorAssignment;
}
