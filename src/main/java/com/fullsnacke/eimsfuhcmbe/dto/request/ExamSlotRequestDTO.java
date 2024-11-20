package com.fullsnacke.eimsfuhcmbe.dto.request;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.SubjectExam;
import com.fullsnacke.eimsfuhcmbe.enums.ExamSlotStatus;
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
public class ExamSlotRequestDTO {

    Integer subjectExamId;

    ZonedDateTime startAt;

    ZonedDateTime endAt;

//    Integer requiredInvigilators;

    ExamSlotStatus status;

    Integer numberOfStudents;

}