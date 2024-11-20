package com.fullsnacke.eimsfuhcmbe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamSlotDetail{
    int examSlotId;
    String subjectCode;
    String examType;
    ZonedDateTime startAt;
    ZonedDateTime endAt;
    String status;
    Integer requiredInvigilators;
    Integer numberOfRegistered;
}
