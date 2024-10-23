package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAttendance;

import java.util.List;

public interface InvigilatorAttendanceService {

    InvigilatorAttendance checkIn(Integer id);
    InvigilatorAttendance checkOut(Integer id);

    List<InvigilatorAttendance> checkInByExamSlotId(Integer examSlotId);
    List<InvigilatorAttendance> checkOutByExamSlotId(Integer examSlotId);

    List<InvigilatorAttendance> checkInAll(List<Integer> invigilatorAttendanceIds);
    List<InvigilatorAttendance> checkOutAll(List<Integer> invigilatorAttendanceIds);
}

