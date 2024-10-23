package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.configuration.ConfigurationHolder;
import com.fullsnacke.eimsfuhcmbe.entity.*;
import com.fullsnacke.eimsfuhcmbe.enums.InvigilatorAttendanceStatus;
import com.fullsnacke.eimsfuhcmbe.exception.AuthenticationProcessException;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotRepository;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAssignmentRepository;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAttendanceRepository;
import com.fullsnacke.eimsfuhcmbe.util.DateValidationUtil;
import com.fullsnacke.eimsfuhcmbe.util.SecurityUntil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InvigilatorAttendanceServiceImpl implements InvigilatorAttendanceService {

    @Autowired
    private InvigilatorAssignmentRepository invigilatorAssignmentRepository;

    @Autowired
    private InvigilatorAttendanceRepository invigilatorAttendanceRepository;

    @Autowired
    ExamSlotRepository examSlotRepository;

    @Autowired
    ConfigurationHolder configurationHolder;

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

    public List<ExamSlot> getExamSlotsBySemester(Integer semesterId) {
        return invigilatorAttendanceRepository.findExamSlotBySemesterId(semesterId);
    }

    public List<ExamSlot> getCheckedAttendanceExamSlotsByDay(Instant day) {
        List<ExamSlot> examSlotList = invigilatorAttendanceRepository.findExamSlotByStartAtInDay(day);
        List<ExamSlot> checkAttendanceExamSlots = new ArrayList<>();

        for (ExamSlot examSlot : examSlotList) {
            List<InvigilatorAttendance> invigilatorAttendances = invigilatorAttendanceRepository.findByExamSlotId(examSlot.getId());
            boolean isAllChecked = true;
            for (InvigilatorAttendance invigilatorAttendance : invigilatorAttendances) {
                if(invigilatorAttendance.getCheckIn() == null || invigilatorAttendance.getCheckOut() == null){
                    isAllChecked = false;
                    break;
                }
            }
            if(isAllChecked){
                checkAttendanceExamSlots.add(examSlot);
            }
        }
        return checkAttendanceExamSlots;
    }

    public List<InvigilatorAttendance> getInvigilatorAttendancesByDay(Instant day) {
        return invigilatorAttendanceRepository.findByExamSlotStartAtInDay(day);
    }
    public List<InvigilatorAttendance> getInvigilatorAttendances() {
        return invigilatorAttendanceRepository.findAll();
    }


    @Override
    public InvigilatorAttendance checkIn(Integer id) {

        InvigilatorAttendance invigilatorAttendanceInDb = invigilatorAttendanceRepository.findById(id).orElse(null);

        if(invigilatorAttendanceInDb != null && isCheckIn(invigilatorAttendanceInDb)){
            Instant now = Instant.now();
            Instant startAt = invigilatorAttendanceInDb.getInvigilatorAssignment().getInvigilatorRegistration().getExamSlot().getStartAt().toInstant();

            if(DateValidationUtil.isAfterTimeLimit(startAt)){
                invigilatorAttendanceInDb.setCheckIn(now);
                invigilatorAttendanceRepository.save(invigilatorAttendanceInDb);
            }else{
                throw new CustomException(ErrorCode.CHECKIN_TIME_IS_NOT_VALID);
            }
        }
        return invigilatorAttendanceInDb;
    }

    @Override
    public InvigilatorAttendance checkOut(Integer id) {

        InvigilatorAttendance invigilatorAttendanceInDb = invigilatorAttendanceRepository.findById(id).orElse(null);

        if(invigilatorAttendanceInDb != null && isCheckOut(invigilatorAttendanceInDb) && !isCheckIn(invigilatorAttendanceInDb)) {
            Instant now = Instant.now();
            Instant endAt = invigilatorAttendanceInDb.getInvigilatorAssignment().getInvigilatorRegistration().getExamSlot().getEndAt().toInstant();
            if (DateValidationUtil.isAfterTimeLimit(endAt)) {
                invigilatorAttendanceInDb.setCheckOut(now);
                invigilatorAttendanceRepository.save(invigilatorAttendanceInDb);
            } else {
                throw new CustomException(ErrorCode.CHECKOUT_TIME_IS_NOT_VALID);
            }
        }
        return invigilatorAttendanceInDb;
    }

    @Override
    @Transactional
    public List<InvigilatorAttendance> checkInByExamSlotId(Integer examSlotId) {
        ExamSlot checkInExamSlot = examSlotRepository.findById(examSlotId).orElse(null);
        System.out.println(Instant.now());
        if(checkInExamSlot == null){
            throw new CustomException(ErrorCode.EXAM_SLOT_NOT_FOUND);
        }
        if(!DateValidationUtil.isAfterTimeLimit(checkInExamSlot.getStartAt().toInstant())){
            throw new CustomException(ErrorCode.CHECKIN_TIME_IS_NOT_VALID);
        }
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
        ExamSlot checkInExamSlot = examSlotRepository.findById(examSlotId).orElse(null);
        if(checkInExamSlot == null){
            throw new CustomException(ErrorCode.EXAM_SLOT_NOT_FOUND);
        }
        if(!DateValidationUtil.isAfterTimeLimit(checkInExamSlot.getEndAt().toInstant())){
            throw new CustomException(ErrorCode.CHECKOUT_TIME_IS_NOT_VALID);
        }
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
                Instant startAt = invigilatorAttendance.getInvigilatorAssignment().getInvigilatorRegistration().getExamSlot().getStartAt().toInstant();
                Instant endAt = invigilatorAttendance.getInvigilatorAssignment().getInvigilatorRegistration().getExamSlot().getEndAt().toInstant();

                startAt = startAt.minus(configurationHolder.getCheckInTimeBeforeExamSlot(), ChronoUnit.MINUTES);

                if(DateValidationUtil.isWithinTimeRange(startAt, endAt)){
                    invigilatorAttendance.setCheckOut(Instant.now());
                    invigilatorAttendances.add(invigilatorAttendance);
                }else {
                    throw new CustomException(ErrorCode.CHECKIN_TIME_IS_NOT_VALID);
                }
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
                Instant endAt = invigilatorAttendance.getInvigilatorAssignment().getInvigilatorRegistration().getExamSlot().getEndAt().toInstant();
                if(DateValidationUtil.isWithinTimeRange(endAt, endAt.plus(configurationHolder.getCheckOutTimeAfterExamSlot(), ChronoUnit.MINUTES))){
                    invigilatorAttendance.setCheckOut(Instant.now());
                    invigilatorAttendances.add(invigilatorAttendance);
                }else {
                    throw new CustomException(ErrorCode.CHECKOUT_TIME_IS_NOT_VALID);
                }
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

    @Transactional
    public List<InvigilatorAttendance> managerApproveByExamSlotId(Integer examSlotId) {
        List<InvigilatorAttendance> invigilatorAttendances = invigilatorAttendanceRepository.findByExamSlotId(examSlotId);
        for (InvigilatorAttendance invigilatorAttendance : invigilatorAttendances) {
            if(invigilatorAttendance.getStatus() == InvigilatorAttendanceStatus.PENDING.getValue()){
                invigilatorAttendance.setStatus(InvigilatorAttendanceStatus.APPROVED.getValue());
            }
        }
        invigilatorAttendanceRepository.saveAll(invigilatorAttendances);
        return invigilatorAttendances;
    }

    @Transactional
    public List<InvigilatorAttendance> managerRejectByExamSlotId(Integer examSlotId) {
        List<InvigilatorAttendance> invigilatorAttendances = invigilatorAttendanceRepository.findByExamSlotId(examSlotId);
        for (InvigilatorAttendance invigilatorAttendance : invigilatorAttendances) {
            if(invigilatorAttendance.getStatus() == InvigilatorAttendanceStatus.PENDING.getValue()){
                invigilatorAttendance.setStatus(InvigilatorAttendanceStatus.REJECTED.getValue());
            }
        }
        invigilatorAttendanceRepository.saveAll(invigilatorAttendances);
        return  invigilatorAttendances;
    }

    public List<InvigilatorAttendance> getCurrentUserInvigilatorAttendance() {
        Optional<User> currentUser = SecurityUntil.getLoggedInUser();
        if(currentUser.isEmpty()){
            throw new AuthenticationProcessException(ErrorCode.USER_NOT_FOUND);
        }
        System.out.println("currentUser.get().getId() = " + currentUser.get().getId());
        for (InvigilatorAttendance invigilatorAttendance : invigilatorAttendanceRepository.findInvigilatorAttendanceByInvigilatorId(currentUser.get().getId())) {
            System.out.println("invigilatorAttendance = " + invigilatorAttendance);
        }
        return invigilatorAttendanceRepository.findInvigilatorAttendanceByInvigilatorId(currentUser.get().getId());
    }


    public List<InvigilatorAttendance> managerApproveAll(List<Integer> invigilatorAttendanceIds) {
        List<InvigilatorAttendance> invigilatorAttendances = new ArrayList<>();

        for (Integer invigilatorAttendanceId : invigilatorAttendanceIds) {
            InvigilatorAttendance invigilatorAttendance = invigilatorAttendanceRepository.findById(invigilatorAttendanceId).orElse(null);
            if(invigilatorAttendance != null && invigilatorAttendance.getStatus() == InvigilatorAttendanceStatus.PENDING.getValue()){
                invigilatorAttendance.setStatus(InvigilatorAttendanceStatus.APPROVED.getValue());
                invigilatorAttendances.add(invigilatorAttendance);
            }
        }

        invigilatorAttendanceRepository.saveAll(invigilatorAttendances);
        return invigilatorAttendances;
    }

    @Transactional
    public List<InvigilatorAttendance> managerRejectAll(List<Integer> invigilatorAttendanceIds) {
        List<InvigilatorAttendance> invigilatorAttendances = new ArrayList<>();

        for (Integer invigilatorAttendanceId : invigilatorAttendanceIds) {
            InvigilatorAttendance invigilatorAttendance = invigilatorAttendanceRepository.findById(invigilatorAttendanceId).orElse(null);
            if(invigilatorAttendance != null && invigilatorAttendance.getStatus() == InvigilatorAttendanceStatus.PENDING.getValue()){
                invigilatorAttendance.setStatus(InvigilatorAttendanceStatus.REJECTED.getValue());
                invigilatorAttendances.add(invigilatorAttendance);
            }
        }

        invigilatorAttendanceRepository.saveAll(invigilatorAttendances);
        return invigilatorAttendances;
    }

    public List<InvigilatorAttendance> getInvigilatorAttendancesByInvigilatorIdAndSemesterId(Integer invigilatorId, Integer semesterId) {
        return invigilatorAttendanceRepository.findInvigilatorAttendanceByInvigilatorIdAndSemesterIdAndApprove(invigilatorId, semesterId);
    }



    public List<InvigilatorAttendance> getCurrentUserInvigilatorAttendanceByDay(Instant day) {
        Optional<User> currentUser = SecurityUntil.getLoggedInUser();
        if(currentUser.isEmpty()){
            throw new AuthenticationProcessException(ErrorCode.USER_NOT_FOUND);
        }
        return invigilatorAttendanceRepository.findInvigilatorAttendanceByInvigilatorIdAndDay(currentUser.get().getId(), day);
    }

    public List<InvigilatorAttendance> getUserInvigilatorAttendanceBySemesterIdAndApproved(Integer invigilatorId, Integer semesterId) {
        return invigilatorAttendanceRepository.findInvigilatorAttendanceByInvigilatorIdAndSemesterIdAndApprove(invigilatorId, semesterId);
    }

    public List<InvigilatorAttendance> getCurrentUserInvigilatorAttendanceBySemesterIdAndApproved(Integer semesterId) {
        Optional<User> currentUser = SecurityUntil.getLoggedInUser();
        if(currentUser.isEmpty()){
            throw new AuthenticationProcessException(ErrorCode.USER_NOT_FOUND);
        }
        return invigilatorAttendanceRepository.findInvigilatorAttendanceByInvigilatorIdAndSemesterIdAndApprove(currentUser.get().getId(), semesterId);
    }

    public List<InvigilatorAttendance> getCurrentUserInvigilatorAttendanceBySemesterId(Integer semesterId) {
        Optional<User> currentUser = SecurityUntil.getLoggedInUser();
        if(currentUser.isEmpty()){
            throw new AuthenticationProcessException(ErrorCode.USER_NOT_FOUND);
        }
        return invigilatorAttendanceRepository.findInvigilatorAttendanceByInvigilatorIdAndSemesterId(currentUser.get().getId(), semesterId);
    }
}
