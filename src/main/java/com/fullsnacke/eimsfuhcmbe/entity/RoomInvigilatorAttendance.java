package com.fullsnacke.eimsfuhcmbe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Table(name = "room_invigilator_attendances")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomInvigilatorAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Integer id;

    @Column(name = "check_in")
    Instant checkIn;

    @Column(name = "check_out")
    Instant checkOut;

    @Column(name = "status", nullable = false)
    Integer status;

    @Column(name = "updated_at")
    Instant updatedAt;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "updated_by", nullable = false)
    User updatedBy;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "exam_slot_room_id", nullable = false)
    ExamSlotRoom examSlotRoom;

}