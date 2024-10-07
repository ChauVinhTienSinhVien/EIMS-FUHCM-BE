package com.fullsnacke.eimsfuhcmbe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "exam_slot_id", nullable = false)
    ExamSlot examSlot;

    @Column(name = "created_at", nullable = false)
    Instant createdAt;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    User createdBy;

    @Column(name = "reason")
    String reason;

    @Column(name = "status", nullable = false)
    Integer status;

    @Column(name = "updated_at")
    Instant updatedAt;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "updated_by")
    User updatedBy;

    @Column(name = "request_type", nullable = false, length = 50)
    String requestType;

}