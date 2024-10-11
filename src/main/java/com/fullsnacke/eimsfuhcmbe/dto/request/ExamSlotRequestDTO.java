package com.fullsnacke.eimsfuhcmbe.dto.request;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.SubjectExam;
import com.fullsnacke.eimsfuhcmbe.enums.ExamSlotStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Date;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamSlotRequestDTO {

//    int subjectExamId;
//
//    int status;
//
//    int createdBy;
//
//    int updatedBy;
//
//    Instant createdAt;
//
//    Instant updatedAt;
//
//    Instant startAt;
//
//    Instant endAt;

    Integer id;

    Integer subjectExamId;

    Instant startAt;

    Instant endAt;

    Integer requiredInvigilators;

    Instant createdAt;

    Integer createdBy;

    ExamSlotStatus status;

    Integer updatedBy;

    Instant updatedAt;

}