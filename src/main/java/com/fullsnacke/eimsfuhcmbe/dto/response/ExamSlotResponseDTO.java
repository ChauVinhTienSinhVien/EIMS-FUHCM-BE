package com.fullsnacke.eimsfuhcmbe.dto.response;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.SubjectExam;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamSlotResponseDTO {

    Integer id;

    SubjectExam subjectExamId;

    ZonedDateTime startAt;

    ZonedDateTime endAt;

    Integer requiredInvigilators;

    Instant createdAt;

    Integer createdBy;

    Integer status;

    Integer updatedBy;

    Instant updatedAt;

}