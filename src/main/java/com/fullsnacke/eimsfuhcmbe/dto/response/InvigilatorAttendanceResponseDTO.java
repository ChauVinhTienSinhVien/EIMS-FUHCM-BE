package com.fullsnacke.eimsfuhcmbe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvigilatorAttendanceResponseDTO {
    Integer id;
    Instant checkIn;
    Instant checkOut;
    String status;
    Instant updatedAt;
    Integer updatedBy;
    Instant startAt;
    Instant endAt;
    Integer examSlotId;
    String invigilatorId;
    String invigilatorFuId;
    String invigilatorFirstName;
    String invigilatorLastName;
    String invigilatorEmail;
    String invigilatorPhone;
}
