package com.fullsnacke.eimsfuhcmbe.dto.response;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.SubjectExam;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.enums.ExamSlotStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamSlotResponseDTO {

    Integer id;

    SubjectExamDTO subjectExamDTO;

//    Integer subjectExamId; // subjectExam.id
//
//    String examType; // subjectExam.examType
//
//    String subjectName; //subjectExam.duration
//
//    String subjectCode; //subjectExam.subjectId.code

    Instant startAt;

    Instant endAt;

    Integer requiredInvigilators;

    Instant createdAt;

    Integer createdBy;

    ExamSlotStatus status;

    Integer updatedBy;

    Instant updatedAt;

}