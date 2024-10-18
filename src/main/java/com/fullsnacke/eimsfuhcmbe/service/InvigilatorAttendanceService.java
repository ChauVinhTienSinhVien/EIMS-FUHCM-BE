package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAttendance;

import java.util.List;

public interface InvigilatorAttendanceService {
    InvigilatorAttendance createAttendance(InvigilatorAttendance invigilatorAttendance);
    InvigilatorAttendance updateAttendance(InvigilatorAttendance invigilatorAttendance);
    InvigilatorAttendance checkIn(Integer id);
    InvigilatorAttendance checkOut(Integer id);

    List<InvigilatorAttendance> checkInAll(Integer examSlotId);
    List<InvigilatorAttendance> checkOutAll(Integer examSlotId);
}
