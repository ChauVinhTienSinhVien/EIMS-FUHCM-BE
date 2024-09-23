package com.fullsnacke.eimsfuhcmbe.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "invigilator_assignments")
public class InvigilatorAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_slot_id", nullable = false)
    private ExamSlot examSlot;

    @Column(name = "role", nullable = false, length = 50)
    private String role;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ExamSlot getExamSlot() {
        return examSlot;
    }

    public void setExamSlot(ExamSlot examSlot) {
        this.examSlot = examSlot;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}