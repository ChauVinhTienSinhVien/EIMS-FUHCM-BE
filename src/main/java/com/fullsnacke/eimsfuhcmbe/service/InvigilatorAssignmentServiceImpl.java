package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.*;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import com.fullsnacke.eimsfuhcmbe.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvigilatorAssignmentServiceImpl implements InvigilatorAssignmentService {

    SemesterRepository semesterRepository;
    InvigilatorRegistrationRepository invigilatorRegistrationRepository;
    InvigilatorAssignmentRepository invigilatorAssignmentRepository;
    ExamSlotRepository examSlotRepository;
    ExamSlotRoomRepository examSlotRoomRepository;
    ExamSlotHallRepository examSlotHallRepository;

//    @Transactional(rollbackFor = Exception.class)
//    public List<ExamSlotRoom> assignInvigilators(int semesterId) {
//        Semester semester = (Semester) semesterRepository.findById(semesterId)
//                .orElseThrow(() -> new CustomException(ErrorCode.SEMESTER_NOT_FOUND));
//        System.out.println("Semester: " + semester.getName());
//
//        List<ExamSlot> examSlots = examSlotRepository.findExamSlotBySubjectExam_SubjectId_SemesterId(semester);
//        System.out.println("Exam Slots: " + examSlots.stream().map(ExamSlot::getId).collect(Collectors.toSet()));
//
//        for (ExamSlot examSlot : examSlots) {
//            List<InvigilatorRegistration> invigilatorRegistrations = invigilatorRegistrationRepository
//                    .findByExamSlotOrderByCreatedAtAsc(examSlot);
//            System.out.println("Invigilator Registrations: " + invigilatorRegistrations.stream().map(InvigilatorRegistration::getInvigilator).map(User::getFuId).toList());
//            System.out.println("Registration size: " + invigilatorRegistrations.size());
//            List<ExamSlotHall> examSlotHalls = examSlotHallRepository.findByExamSlot(examSlot);
//            System.out.println("Exam Slot Halls: " + examSlotHalls.stream().map(ExamSlotHall::getId).collect(Collectors.toSet()));
//            List<ExamSlotRoom> examSlotRooms = examSlotRoomRepository.findByExamSlotHall_ExamSlot(examSlot);
//            System.out.println("Exam Slot Rooms: " + examSlotRooms.stream().map(ExamSlotRoom::getRoom).map(Room::getRoomName).collect(Collectors.toSet()));
//
//            assignInvigilatorsToRoom(invigilatorRegistrations, examSlotRooms);
//            assignInvigilatorsToHalls(invigilatorRegistrations, examSlotHalls);
//        }
//        return examSlotRoomRepository.findByExamSlotHall_ExamSlotIn(examSlots);
//    }

    @Transactional(rollbackFor = Exception.class)
    public List<ExamSlotRoom> assignInvigilators(int semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEMESTER_NOT_FOUND));

        List<ExamSlot> examSlots = examSlotRepository.findExamSlotBySubjectExam_SubjectId_SemesterId(semester);

        Map<Integer, List<InvigilatorRegistration>> registrationMap = invigilatorRegistrationRepository
                .findByExamSlotInOrderByCreatedAtAsc(examSlots)
                .stream()
                .collect(Collectors.groupingBy(reg -> reg.getExamSlot().getId()));

        System.out.println("Registration Map:");
        registrationMap.forEach((examSlotId, registrations) -> {
            System.out.println("ExamSlot ID: " + examSlotId + ", Registrations: " + registrations.size());
        });

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

    protected void assignInvigilatorsToRoom(List<InvigilatorRegistration> registrations, List<ExamSlotRoom> examSlotRooms) {
        List<ExamSlotRoom> availableRooms = examSlotRooms.stream()
                .filter(room -> room.getRoomInvigilator() == null)
                .collect(Collectors.toList());

        int assignmentCount = Math.min(examSlotRooms.size(), availableRooms.size());
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

        invigilatorAssignmentRepository.saveAll(assignments);
        examSlotRoomRepository.saveAll(roomsToUpdate);

        registrations.subList(0, assignmentCount).clear();
    }

    @Transactional(rollbackFor = Exception.class)
    protected void assignInvigilatorsToHalls(List<InvigilatorRegistration> registrations, List<ExamSlotHall> examSlotHalls) {
        List<ExamSlotHall> availableHalls = examSlotHalls.stream()
                .filter(hall -> hall.getHallInvigilator() == null)
                .collect(Collectors.toList());

        int assignmentCount = Math.min(examSlotHalls.size(), availableHalls.size());
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

        invigilatorAssignmentRepository.saveAll(assignments);
        examSlotHallRepository.saveAll(hallsToUpdate);

        registrations.subList(0, assignmentCount).clear();
    }

//    @Transactional(rollbackFor = Exception.class)
//    protected void assignInvigilatorsToRoom(List<InvigilatorRegistration> registrations, List<ExamSlotRoom> examSlotRooms) {
//        List<InvigilatorRegistration> availableInvigilators = new ArrayList<>(registrations);
//        List<ExamSlotRoom> availableRooms = new ArrayList<>(examSlotRooms);
//
//        for (int i = 0; i < availableRooms.size() && availableRooms.size() <= availableInvigilators.size(); i++) {
//            ExamSlotRoom room = examSlotRooms.get(i);
//            InvigilatorRegistration registration = availableInvigilators.get(i);
//
//            InvigilatorAssignment assignment = InvigilatorAssignment.builder()
//                    .invigilatorRegistration(registration)
//                    .isHallInvigilator(false)
//                    .build();
//            assignments.add(assignment);
//            try {
//                invigilatorAssignmentRepository.save(assignment);
//                room.setRoomInvigilator(assignment);
//                examSlotRoomRepository.save(room);
//                System.out.println("Phòng: " + room.getRoom().getRoomName());
//                System.out.println("Phân công: " + registration.getInvigilator().getFuId());
//            } catch (Exception e) {
//                System.out.println("Lỗi khi phân công giám thị cho phòng: " + e.getMessage());
//                throw new CustomException(ErrorCode.FAILD_TO_CLASSIFY_INVIGILATOR);
//            }
//        }
//        System.out.println("Remaining Invigilator Registration Size Before Classify Room: " + registrations.size());
//        registrations.removeAll(availableInvigilators.subList(0, Math.min(availableRooms.size(), availableInvigilators.size())));
//        System.out.println("Remaining Invigilator Registration Size After Classify Room: " + registrations.size());
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    protected void assignInvigilatorsToHalls
//    (List<InvigilatorRegistration> registrations, List<ExamSlotHall> examSlotHalls) {
//        List<InvigilatorRegistration> availableInvigilators = new ArrayList<>(registrations);
//        List<ExamSlotHall> availableHalls = new ArrayList<>(examSlotHalls);
//
//        System.out.println("Remaining Invigilator Registration Size Before Classify Room: " + registrations.size());
//
//        for (int i = 0; i < availableHalls.size() && availableHalls.size() <= availableInvigilators.size(); i++) {
//            ExamSlotHall hall = examSlotHalls.get(i);
//            InvigilatorRegistration registration = availableInvigilators.get(i);
//
//            InvigilatorAssignment assignment = InvigilatorAssignment.builder()
//                    .invigilatorRegistration(registration)
//                    .isHallInvigilator(true)
//                    .build();
//
//            try {
//                invigilatorAssignmentRepository.save(assignment);
//                hall.setHallInvigilator(assignment);
//                examSlotHallRepository.save(hall);
//                System.out.println("Hội trường: " + hall.getId());
//                System.out.println("Phân công: " + registration.getInvigilator().getFuId());
//            } catch (Exception e) {
//                System.out.println("Lỗi khi phân công giám thị cho hội trường: " + e.getMessage());
//                throw new CustomException(ErrorCode.FAILD_TO_CLASSIFY_INVIGILATOR);
//            }
//        }
//        registrations.removeAll(availableInvigilators.subList(0, Math.min(availableHalls.size(), availableInvigilators.size())));
//        System.out.println("Remaining Invigilator Registration Size After Classify Room: " + registrations.size());
//    }

}
