package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAttendance;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAssignmentRepository;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAttendanceRepository;
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

    @Transactional
    public List<InvigilatorAttendance> addInvigilatorAttendancesByDay(Instant day) {
        List<InvigilatorAttendance> invigilatorAttendances = invigilatorAttendanceRepository.findByExamSlotStartAtInDay(day);

        if(!invigilatorAttendances.isEmpty()){
            return invigilatorAttendances;
        }

        List<InvigilatorAssignment> invigilatorAssignments = invigilatorAssignmentRepository.findByExamSlotStartAtInDay(day);

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

    public List<ExamSlot> getExamSlotsByDay(Instant day) {
        return invigilatorAttendanceRepository.findExamSlotByStartAtInDay(day);
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
    public InvigilatorAttendance checkIn(Integer id) {

        InvigilatorAttendance invigilatorAttendanceInDb = invigilatorAttendanceRepository.findById(id).orElse(null);

        if(invigilatorAttendanceInDb != null && isCheckIn(invigilatorAttendanceInDb)){
            invigilatorAttendanceInDb.setCheckIn(Instant.now());
            invigilatorAttendanceRepository.save(invigilatorAttendanceInDb);
        }
        return invigilatorAttendanceInDb;
    }

    @Override
    public InvigilatorAttendance checkOut(Integer id) {

        InvigilatorAttendance invigilatorAttendanceInDb = invigilatorAttendanceRepository.findById(id).orElse(null);

        if(invigilatorAttendanceInDb != null && isCheckOut(invigilatorAttendanceInDb) && !isCheckIn(invigilatorAttendanceInDb)){
            invigilatorAttendanceInDb.setCheckOut(Instant.now());
            invigilatorAttendanceRepository.save(invigilatorAttendanceInDb);
        }
        return invigilatorAttendanceInDb;
    }

    @Override
    @Transactional
    public List<InvigilatorAttendance> checkInByExamSlotId(Integer examSlotId) {
        List<InvigilatorAttendance> invigilatorAttendances = invigilatorAttendanceRepository.findByExamSlotId(examSlotId);
        for (InvigilatorAttendance invigilatorAttendance : invigilatorAttendances) {
            if(isCheckIn(invigilatorAttendance)){
                invigilatorAttendance.setCheckIn(Instant.now());
            }
        }
        invigilatorAttendanceRepository.saveAll(invigilatorAttendances);
        return invigilatorAttendances;
    }

    @Override
    @Transactional
    public List<InvigilatorAttendance> checkOutByExamSlotId(Integer examSlotId) {
        List<InvigilatorAttendance> invigilatorAttendances = invigilatorAttendanceRepository.findByExamSlotId(examSlotId);
        for (InvigilatorAttendance invigilatorAttendance : invigilatorAttendances) {
            if(isCheckOut(invigilatorAttendance)  && !isCheckIn(invigilatorAttendance)){
                invigilatorAttendance.setCheckOut(Instant.now());
            }
        }
        invigilatorAttendanceRepository.saveAll(invigilatorAttendances);
        return invigilatorAttendances;
    }

    @Override
    @Transactional
    public List<InvigilatorAttendance> checkInAll(List<Integer> invigilatorAttendanceIds) {
        List<InvigilatorAttendance> invigilatorAttendances = new ArrayList<>();

        for (Integer invigilatorAttendanceId : invigilatorAttendanceIds) {
            InvigilatorAttendance invigilatorAttendance = invigilatorAttendanceRepository.findById(invigilatorAttendanceId).orElse(null);
            if(invigilatorAttendance != null){
                invigilatorAttendances.add(invigilatorAttendance);
            }
        }

        for (InvigilatorAttendance invigilatorAttendance : invigilatorAttendances) {
            if(isCheckIn(invigilatorAttendance)){
                invigilatorAttendance.setCheckIn(Instant.now());
            }
        }

        invigilatorAttendanceRepository.saveAll(invigilatorAttendances);
        return invigilatorAttendances;
    }

    @Override
    @Transactional
    public List<InvigilatorAttendance> checkOutAll(List<Integer> invigilatorAttendanceIds) {
        List<InvigilatorAttendance> invigilatorAttendances = new ArrayList<>();

        for (Integer invigilatorAttendanceId : invigilatorAttendanceIds) {
            InvigilatorAttendance invigilatorAttendance = invigilatorAttendanceRepository.findById(invigilatorAttendanceId).orElse(null);
            if(invigilatorAttendance != null){
                invigilatorAttendances.add(invigilatorAttendance);
            }
        }

        for (InvigilatorAttendance invigilatorAttendance : invigilatorAttendances) {
            if(isCheckOut(invigilatorAttendance)  && !isCheckIn(invigilatorAttendance)){
                invigilatorAttendance.setCheckOut(Instant.now());
            }
        }

        invigilatorAttendanceRepository.saveAll(invigilatorAttendances);
        return invigilatorAttendances;
    }

    private boolean isCheckIn(InvigilatorAttendance invigilatorAttendance) {
        return invigilatorAttendance.getCheckIn() == null;
    }

    private boolean isCheckOut(InvigilatorAttendance invigilatorAttendance) {
        return invigilatorAttendance.getCheckOut() == null;
    }

    public List<InvigilatorAttendance> getInvigilatorAttendancesByExamSlotId(Integer examSlotId) {
        return invigilatorAttendanceRepository.findByExamSlotId(examSlotId);
    }
}
