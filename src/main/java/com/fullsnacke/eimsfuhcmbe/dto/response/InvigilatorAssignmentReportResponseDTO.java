package com.fullsnacke.eimsfuhcmbe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvigilatorAssignmentReportResponseDTO {
    int totalAssigned;
    int totalInvigilatedSlots;
    int totalRequiredInvigilationSlots;
    int totalNonInvigilatedSlots;

    int totalAssignedHours;
    double totalInvigilatedHours;
    double totalRequiredInvigilationHours;


}
