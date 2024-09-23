package com.fullsnacke.eimsfuhcmbe.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "student_lists")
public class StudentList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "student_id", nullable = false, length = 8)
    private String studentId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_slot_room_id", nullable = false)
    private ExamSlotRoom examSlotRoom;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ExamSlotRoom getExamSlotRoom() {
        return examSlotRoom;
    }

    public void setExamSlotRoom(ExamSlotRoom examSlotRoom) {
        this.examSlotRoom = examSlotRoom;
    }

}