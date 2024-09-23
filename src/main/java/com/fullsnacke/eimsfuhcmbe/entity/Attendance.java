package com.fullsnacke.eimsfuhcmbe.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "check_in")
    private Instant checkIn;

    @Column(name = "check_out")
    private Instant checkOut;

    @Column(name = "status", nullable = false)
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instant getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Instant checkIn) {
        this.checkIn = checkIn;
    }

    public Instant getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Instant checkOut) {
        this.checkOut = checkOut;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}