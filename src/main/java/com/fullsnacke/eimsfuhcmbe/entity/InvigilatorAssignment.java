package com.fullsnacke.eimsfuhcmbe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "invigilator_assignments")
@EntityListeners(AuditingEntityListener.class)
public class InvigilatorAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Integer id;

    @OneToOne
    @JoinColumn(name = "invigilator_registration_id", nullable = false)
    InvigilatorRegistration invigilatorRegistration;

    @Column(name = "is_hall_invigilator")
    Boolean isHallInvigilator;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    Instant createdAt;

}