package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAttendance;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorRegistration;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAssignmentRepository;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAttendanceRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvigilatorAttendanceServiceImpl implements InvigilatorAttendanceService {

    @Autowired
    private InvigilatorAssignmentRepository invigilatorAssignmentRepository;

    @Autowired
    private InvigilatorAttendanceRepository invigilatorAttendanceRepository;

    @Transactional
    public List<InvigilatorAttendance> addInvigilatorAttendancesByDay() {
        System.out.println("InvigilatorAttendanceServiceImpl.addInvigilatorAttendancesByDay");
        Instant day = Instant.parse("2024-10-25T00:00:00Z");
        //List<InvigilatorAttendance> invigilatorAttendances = invigilatorAttendanceRepository.findByExamSlotStartAtInDay(day);
        List<InvigilatorAttendance> invigilatorAttendances = invigilatorAttendanceRepository.findAll();
        if(!invigilatorAttendances.isEmpty()){
            return invigilatorAttendances;
        }

        //List<InvigilatorAssignment> invigilatorAssignments = invigilatorAssignmentRepository.findByExamSlotStartAtInDay(day);
        List<InvigilatorAssignment> invigilatorAssignments = invigilatorAssignmentRepository.findAll();
        for (InvigilatorAssignment invigilatorRegistration : invigilatorAssignments) {
            System.out.println(invigilatorRegistration.getId());
            InvigilatorAttendance invigilatorAttendance = InvigilatorAttendance
                    .builder()
                    .invigilatorAssignment(invigilatorRegistration)
                    .status(1)
                    .build();
            invigilatorAttendances.add(invigilatorAttendance);
        }

        invigilatorAttendanceRepository.saveAll(invigilatorAttendances);
        return invigilatorAttendances;
    }

    public List<InvigilatorAttendance> getInvigilatorAttendancesByDay(Instant day) {
        return invigilatorAttendanceRepository.findByExamSlotStartAtInDay(day);
    }
    public List<InvigilatorAttendance> getInvigilatorAttendances() {
        return invigilatorAttendanceRepository.findAll();
    }


    @Override
    public InvigilatorAttendance createAttendance(InvigilatorAttendance invigilatorAttendance) {
        return null;
    }

    @Override
    public InvigilatorAttendance updateAttendance(InvigilatorAttendance invigilatorAttendance) {
        return null;
    }

    @Override
    public InvigilatorAttendance checkIn(InvigilatorAttendance invigilatorAttendance) {

        InvigilatorAttendance invigilatorAttendanceInDb = invigilatorAttendanceRepository.findById(invigilatorAttendance.getId()).orElse(null);

        if(invigilatorAttendanceInDb != null){
            invigilatorAttendanceInDb.setCheckIn(Instant.now());
            invigilatorAttendanceRepository.save(invigilatorAttendanceInDb);
        }
        return invigilatorAttendanceInDb;
    }

    @Override
    public InvigilatorAttendance checkOut(InvigilatorAttendance invigilatorAttendance) {

        InvigilatorAttendance invigilatorAttendanceInDb = invigilatorAttendanceRepository.findById(invigilatorAttendance.getId()).orElse(null);

        if(invigilatorAttendanceInDb != null){
            invigilatorAttendanceInDb.setCheckOut(Instant.now());
            invigilatorAttendanceRepository.save(invigilatorAttendanceInDb);
        }
        return invigilatorAttendanceInDb;
    }

    @Override
    public List<InvigilatorAttendance> checkInAll(List<InvigilatorAttendance> invigilatorAttendanceList) {
        List<InvigilatorAttendance> invigilatorAttendances = new ArrayList<>();
        for (InvigilatorAttendance invigilatorAttendance : invigilatorAttendanceList) {
            InvigilatorAttendance invigilatorAttendanceInDb = invigilatorAttendanceRepository.findById(invigilatorAttendance.getId()).orElse(null);
            if(invigilatorAttendanceInDb != null){
                invigilatorAttendanceInDb.setCheckIn(Instant.now());
                invigilatorAttendances.add(invigilatorAttendanceInDb);
            }
        }
        return invigilatorAttendances;
    }

    @Override
    public List<InvigilatorAttendance> checkOutAll(List<InvigilatorAttendance> invigilatorAttendanceList) {
        List<InvigilatorAttendance> invigilatorAttendances = new ArrayList<>();
        for (InvigilatorAttendance invigilatorAttendance : invigilatorAttendanceList) {
            InvigilatorAttendance invigilatorAttendanceInDb = invigilatorAttendanceRepository.findById(invigilatorAttendance.getId()).orElse(null);
            if(invigilatorAttendanceInDb != null){
                invigilatorAttendanceInDb.setCheckOut(Instant.now());
                invigilatorAttendances.add(invigilatorAttendanceInDb);
            }
        }
        return invigilatorAttendances;
    }
}
