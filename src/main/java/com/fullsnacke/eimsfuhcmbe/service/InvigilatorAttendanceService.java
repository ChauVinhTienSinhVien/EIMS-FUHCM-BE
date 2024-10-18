package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAttendance;

import java.util.List;

public interface InvigilatorAttendanceService {
    InvigilatorAttendance createAttendance(InvigilatorAttendance invigilatorAttendance);
    InvigilatorAttendance updateAttendance(InvigilatorAttendance invigilatorAttendance);
    InvigilatorAttendance checkIn(InvigilatorAttendance invigilatorAttendance);
    InvigilatorAttendance checkOut(InvigilatorAttendance invigilatorAttendance);

    List<InvigilatorAttendance> checkInAll(List<InvigilatorAttendance> invigilatorAttendanceList);
    List<InvigilatorAttendance> checkOutAll(List<InvigilatorAttendance> invigilatorAttendanceList);
}
