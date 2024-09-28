package com.fullsnacke.eimsfuhcmbe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "salary_calculations")
public class SalaryCalculation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "invigilator_id", nullable = false)
    User invigilator;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "semester_id", nullable = false)
    Semester semester;

    @Column(name = "total_time", nullable = false)
    Integer totalTime;

    @Column(name = "hourly_rate", nullable = false, precision = 10, scale = 2)
    BigDecimal hourlyRate;

    @Column(name = "total_salary", nullable = false, precision = 10, scale = 2)
    BigDecimal totalSalary;

    @Column(name = "status", nullable = false)
    Integer status;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    User updatedBy;

    @Column(name = "updated_at")
    Instant updatedAt;
}