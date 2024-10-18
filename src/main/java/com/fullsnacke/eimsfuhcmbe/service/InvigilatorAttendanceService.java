package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAttendance;

import java.util.List;

public interface InvigilatorAttendanceService {
    InvigilatorAttendance createAttendance(InvigilatorAttendance invigilatorAttendance);
    InvigilatorAttendance updateAttendance(InvigilatorAttendance invigilatorAttendance);
    InvigilatorAttendance checkIn(Integer invigilatorAssignmentId);
    InvigilatorAttendance checkOut(Integer invigilatorAssignmentId);

    List<InvigilatorAttendance> checkInAll(List<Integer> invigilatorAssignmentIds);
    List<InvigilatorAttendance> checkOutAll(List<Integer> invigilatorAssignmentIds);
}
