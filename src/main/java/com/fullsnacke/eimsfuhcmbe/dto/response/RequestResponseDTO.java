package com.fullsnacke.eimsfuhcmbe.dto.response;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class RequestResponseDTO {
    int requestId;
    int examSlotId;
    ZonedDateTime startAt;
    ZonedDateTime endAt;
    String semesterName;
    Instant createdAt;
    String reason;
    String status;
    String requestType;
    Instant updatedAt;
    String note;
}
