package com.fullsnacke.eimsfuhcmbe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.Subject;
import com.fullsnacke.eimsfuhcmbe.enums.RequestTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManagerRequestResponseDTO {
    String requestId;
    String fuId;
    String email;
    String reason;
    ExamSlotDetail examSlotDetail;
    SubjectResponseDTO subject;
    String status;
    String note;
    String requestType = RequestTypeEnum.CANCEL.name();
}
