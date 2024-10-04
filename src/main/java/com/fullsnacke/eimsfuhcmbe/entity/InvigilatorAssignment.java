package com.fullsnacke.eimsfuhcmbe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "invigilator_assignments")
public class InvigilatorAssignment {
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

    @Column(name = "role", nullable = false, length = 50)
    String role;

}