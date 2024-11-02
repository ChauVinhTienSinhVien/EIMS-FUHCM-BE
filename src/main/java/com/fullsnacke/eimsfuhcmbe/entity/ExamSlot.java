package com.fullsnacke.eimsfuhcmbe.entity;

import com.fullsnacke.eimsfuhcmbe.converter.InstantConverter;
import com.fullsnacke.eimsfuhcmbe.converter.ZonedDateTimeCoverter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "exam_slots")
@EntityListeners(AuditingEntityListener.class)
public class ExamSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "subject_exam_id", nullable = false)
    SubjectExam subjectExam;

    @Column(name = "start_at", nullable = false)
    @Convert(converter = ZonedDateTimeCoverter.class)
    ZonedDateTime startAt;

    @Column(name = "end_at", nullable = false)
    @Convert(converter = ZonedDateTimeCoverter.class)
    ZonedDateTime endAt;

    @Column(name = "number_of_students", nullable = false)
    Integer numberOfStudents;

    @Column(name = "required_invigilators")
    Integer requiredInvigilators;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    Instant createdAt;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    @CreatedBy
    User createdBy;

    @Column(name = "status", nullable = false)
    @ColumnDefault("1")
    Integer status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "updated_by")
    User updatedBy;

    @Column(name = "updated_at")
    @LastModifiedDate
    Instant updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approved_by")
    User approvedBy;

    @Column(name = "approved_at")
    @LastModifiedDate
    Instant approvedAt;

}