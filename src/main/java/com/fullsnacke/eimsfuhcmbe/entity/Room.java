package com.fullsnacke.eimsfuhcmbe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    int id;

    @Column(name = "room_name", nullable = false, length = 50)
    String roomName;

    @Column(name = "capacity", nullable = false)
    Integer capacity;
}