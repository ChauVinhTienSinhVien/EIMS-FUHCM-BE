package com.fullsnacke.eimsfuhcmbe.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "subject_exams")
public class SubjectExam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "exam_type", nullable = false, length = 50)
    private String examType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

}