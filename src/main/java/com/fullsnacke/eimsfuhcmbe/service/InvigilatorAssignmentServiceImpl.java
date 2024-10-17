package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.InvigilatorRegistrationMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.ExchangeInvigilatorsRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.UserResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.*;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import com.fullsnacke.eimsfuhcmbe.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvigilatorAssignmentServiceImpl implements InvigilatorAssignmentService {

    SemesterRepository semesterRepository;
    InvigilatorRegistrationRepository invigilatorRegistrationRepository;
    InvigilatorAssignmentRepository invigilatorAssignmentRepository;
    InvigilatorRegistrationMapper invigilatorRegistrationMapper;
    ExamSlotRepository examSlotRepository;
    ExamSlotRoomRepository examSlotRoomRepository;
    ExamSlotHallRepository examSlotHallRepository;

    @Transactional(rollbackFor = Exception.class)
    @ExceptionHandler(CustomException.class)
    public List<ExamSlotRoom> assignInvigilators(int semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEMESTER_NOT_FOUND));

        List<ExamSlot> examSlots = examSlotRepository.findExamSlotBySubjectExam_SubjectId_SemesterId(semester);

        Map<Integer, List<InvigilatorRegistration>> registrationMap = invigilatorRegistrationRepository
                .findUnassignedRegistrationsByExamSlotInOrderByCreatedAtAsc(examSlots)
                .stream()
                .collect(Collectors.groupingBy(reg -> reg.getExamSlot().getId()));

        System.out.println("Registration Map:");
        registrationMap.forEach((examSlotId, registrations) -> {
            System.out.println("ExamSlot ID: " + examSlotId + ", Registrations: " + registrations.size());
        });

        if(registrationMap.isEmpty()) {
            throw new CustomException(ErrorCode.NO_INVIGILATOR_REGISTRATION);
        }

        Map<Integer, List<ExamSlotHall>> hallMap = examSlotHallRepository
                .findByExamSlotIn(examSlots)
                .stream()
                .collect(Collectors.groupingBy(hall -> hall.getExamSlot().getId()));

        System.out.println("Hall Map:");
        hallMap.forEach((examSlotId, halls) -> {
            System.out.println("ExamSlot ID: " + examSlotId + ", Halls: " + halls.size());
        });

        Map<Integer, List<ExamSlotRoom>> roomMap = examSlotRoomRepository
                .findByExamSlotHall_ExamSlotIn(examSlots)
                .stream()
                .collect(Collectors.groupingBy(room -> room.getExamSlotHall().getExamSlot().getId()));

        System.out.println("Room Map:");
        roomMap.forEach((examSlotId, rooms) -> {
            System.out.println("ExamSlot ID: " + examSlotId + ", Rooms: " + rooms.size());
        });

        for (ExamSlot examSlot : examSlots) {
            List<InvigilatorRegistration> registrations = registrationMap.getOrDefault(examSlot.getId(), new ArrayList<>());
            List<ExamSlotHall> halls = hallMap.getOrDefault(examSlot.getId(), new ArrayList<>());
            List<ExamSlotRoom> rooms = roomMap.getOrDefault(examSlot.getId(), new ArrayList<>());

            System.out.println("Processing ExamSlot ID: " + examSlot.getId());
            System.out.println("Registrations: " + registrations.size());
            System.out.println("Halls: " + halls.size());
            System.out.println("Rooms: " + rooms.size());

            assignInvigilatorsToRoom(registrations, rooms);
            assignInvigilatorsToHalls(registrations, halls);
        }

        return examSlotRoomRepository.findByExamSlotHall_ExamSlotIn(examSlots);
    }

    @Transactional(rollbackFor = Exception.class)
    protected void assignInvigilatorsToRoom(List<InvigilatorRegistration> registrations, List<ExamSlotRoom> examSlotRooms) {
        List<ExamSlotRoom> availableRooms = examSlotRooms.stream()
                .filter(room -> room.getRoomInvigilator() == null)
                .toList();

        int assignmentCount = Math.min(registrations.size(), availableRooms.size());
        List<InvigilatorAssignment> assignments = new ArrayList<>();
        List<ExamSlotRoom> roomsToUpdate = new ArrayList<>();

        for (int i = 0; i < assignmentCount; i++) {
            ExamSlotRoom room = availableRooms.get(i);
            InvigilatorRegistration registration = registrations.get(i);

            InvigilatorAssignment assignment = InvigilatorAssignment.builder()
                    .invigilatorRegistration(registration)
                    .isHallInvigilator(false)
                    .build();
            assignments.add(assignment);

            room.setRoomInvigilator(assignment);
            roomsToUpdate.add(room);
            System.out.println("Phòng: " + room.getRoom().getRoomName() +
                    "\nGiám thị: " + registration.getInvigilator().getFuId());
        }

        try {
            invigilatorAssignmentRepository.saveAll(assignments);
            examSlotRoomRepository.saveAll(roomsToUpdate);
        } catch (Exception e) {
            System.out.println("Lỗi khi phân công giám thị cho phòng: " + e.getMessage());
            throw new CustomException(ErrorCode.FAILD_TO_CLASSIFY_INVIGILATOR);
        }

        registrations.subList(0, assignmentCount).clear();
    }

    @Transactional(rollbackFor = Exception.class)
    protected void assignInvigilatorsToHalls(List<InvigilatorRegistration> registrations, List<ExamSlotHall> examSlotHalls) {
        List<ExamSlotHall> availableHalls = examSlotHalls.stream()
                .filter(hall -> hall.getHallInvigilator() == null)
                .toList();

        int assignmentCount = Math.min(registrations.size(), availableHalls.size());
        List<InvigilatorAssignment> assignments = new ArrayList<>();
        List<ExamSlotHall> hallsToUpdate = new ArrayList<>();

        for (int i = 0; i < assignmentCount; i++) {
            ExamSlotHall hall = availableHalls.get(i);
            InvigilatorRegistration registration = registrations.get(i);

            InvigilatorAssignment assignment = InvigilatorAssignment.builder()
                    .invigilatorRegistration(registration)
                    .isHallInvigilator(true)
                    .build();
            assignments.add(assignment);

            hall.setHallInvigilator(assignment);
            hallsToUpdate.add(hall);
            System.out.println("Hội trường ID: " + hall.getId() +
                    "\nGiám thị: " + registration.getInvigilator().getFuId());
        }

        try {
            invigilatorAssignmentRepository.saveAll(assignments);
            examSlotHallRepository.saveAll(hallsToUpdate);
        } catch (Exception e) {
            System.out.println("Lỗi khi phân công giám thị cho hội trường: " + e.getMessage());
            throw new CustomException(ErrorCode.FAILD_TO_CLASSIFY_INVIGILATOR);
        }

        registrations.subList(0, assignmentCount).clear();
    }


    public List<UserResponseDTO> getUnassignedInvigilators(int examSlotId) {
        if(examSlotId <= 0) {
            throw new CustomException(ErrorCode.EXAM_SLOT_ID_MISSING);
        }

        List<InvigilatorRegistration> unassignedList = invigilatorRegistrationRepository.findUnassignedRegistrationsByExamSlot_IdOrderByCreatedAtAsc(examSlotId);

        return invigilatorRegistrationMapper.mapBasicInvigilatorRegistration(unassignedList);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean exchangeInvigilators(ExchangeInvigilatorsRequestDTO request) {

        InvigilatorAssignment oldAssignment = invigilatorAssignmentRepository.findByExamSlotIdAndInvigilatorFuId(request.getExamSlotId(), request.getOldInvigilatorFuId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVIGILATOR_NOT_FOUND));

        InvigilatorRegistration newInvigilator = invigilatorRegistrationRepository.findByExamSlotIdAndInvigilatorFuId(request.getExamSlotId(), request.getNewInvigilatorFuId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVIGILATOR_NOT_FOUND));

        try {
            oldAssignment.setInvigilatorRegistration(newInvigilator);
            invigilatorAssignmentRepository.save(oldAssignment);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.EXCHANGE_INVIGILATORS_FAILED);
        }

        return true;
    }



}
