package com.fullsnacke.eimsfuhcmbe.dto.response;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
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

    int id;
    int subjectExamId;

    int status;

    int createdBy;

    int updateBy;

    Instant createAt;

    Instant updateAt;

    Instant startAt;

    Instant endAt;

}
