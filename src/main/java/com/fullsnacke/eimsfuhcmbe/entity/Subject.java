package com.fullsnacke.eimsfuhcmbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Integer id;

    @Column(name = "code", nullable = false, length = 50, unique = true)
    String code;


    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "semester_id", nullable = false)
    Semester semesterId;

    @Column(name = "name", nullable = false, length = 100)
    String name;
}