package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAttendance;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAssignmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvigilatorAttendanceServiceImpl implements InvigilatorAttendanceService {

    @Autowired
    private InvigilatorAssignmentRepository invigilatorAssignmentRepository;

    @Override
    public InvigilatorAttendance createAttendance(InvigilatorAttendance invigilatorAttendance) {
        return null;
    }

    @Override
    public InvigilatorAttendance updateAttendance(InvigilatorAttendance invigilatorAttendance) {
        return null;
    }

    @Override
    public InvigilatorAttendance checkIn(Integer invigilatorAssignmentId) {
        return null;
    }

    @Override
    public InvigilatorAttendance checkOut(Integer invigilatorAssignmentId) {
        return null;
    }

    @Override
    public List<InvigilatorAttendance> checkInAll(List<Integer> invigilatorAssignmentIds) {
        return List.of();
    }

    @Override
    public List<InvigilatorAttendance> checkOutAll(List<Integer> invigilatorAssignmentIds) {
        return List.of();
    }
}
