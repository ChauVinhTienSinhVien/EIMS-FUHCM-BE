package com.fullsnacke.eimsfuhcmbe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "invigilator_attendances")
@EntityListeners(AuditingEntityListener.class)
public class InvigilatorAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Integer id;

    @Column(name = "check_in")
    Instant checkIn;

    @Column(name = "check_out")
    Instant checkOut;

    @Column(name = "status", nullable = false, columnDefinition = "int default 1")
    Integer status;

    @Column(name = "updated_at")
    @LastModifiedDate
    Instant updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "updated_by")
    @LastModifiedBy
    User updatedBy;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "invigilator_assignment_id", nullable = false)
    InvigilatorAssignment invigilatorAssignment;
}
