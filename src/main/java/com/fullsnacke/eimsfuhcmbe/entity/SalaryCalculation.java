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

    @Column(name = "total_time", nullable = false)
    Integer totalTime;

    @Column(name = "hourly_rate", nullable = false, precision = 10, scale = 2)
    BigDecimal hourlyRate;

    @Column(name = "total_salary", nullable = false, precision = 10, scale = 2)
    BigDecimal totalSalary;

    @Column(name = "status", nullable = false)
    Integer status;

    @Column(name = "updated_at")
    Instant updatedAt;
}