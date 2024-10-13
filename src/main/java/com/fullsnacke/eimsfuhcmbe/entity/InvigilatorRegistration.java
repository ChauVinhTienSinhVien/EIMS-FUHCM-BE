package com.fullsnacke.eimsfuhcmbe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "invigilator_registrations")
public class InvigilatorRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "exam_slot_id", nullable = false)
    ExamSlot examSlot;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "invigilator_id", nullable = false)
    User invigilator;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    Instant createdAt;
}
