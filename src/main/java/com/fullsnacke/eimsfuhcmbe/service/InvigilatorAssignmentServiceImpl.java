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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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

    @Transactional(rollbackFor = Exception.class)
    public List<ExamSlotRoom> assignInvigilatorToRoom(int semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEMESTER_NOT_FOUND));
        System.out.println("Semester: " + semester.getName());

        List<ExamSlot> examSlots = examSlotRepository.findExamSlotBySubjectExam_SubjectId_SemesterId(semester);
        System.out.println("Exam Slots: " + examSlots.stream().map(ExamSlot::getId).collect(Collectors.toSet()));

        for (ExamSlot examSlot : examSlots) {
            Set<InvigilatorRegistration> invigilatorRegistrations = invigilatorRegistrationRepository
                    .findByExamSlotOrderByCreatedAtAsc(examSlot);
            System.out.println("Invigilator Registrations: " + invigilatorRegistrations.stream().map(InvigilatorRegistration::getInvigilator).map(User::getFuId).collect(Collectors.toSet()));
            List<ExamSlotHall> examSlotHalls = examSlotHallRepository.findByExamSlot(examSlot);
            System.out.println("Exam Slot Halls: " + examSlotHalls.stream().map(ExamSlotHall::getId).collect(Collectors.toSet()));
            List<ExamSlotRoom> examSlotRooms = examSlotRoomRepository.findByExamSlotHall_ExamSlot(examSlot);
            System.out.println("Exam Slot Rooms: " + examSlotRooms.stream().map(ExamSlotRoom::getRoom).map(Room::getRoomName).collect(Collectors.toSet()));

            assignInvigilatorsToRoom(invigilatorRegistrations, examSlotRooms);
            assignInvigilatorsToHalls(invigilatorRegistrations, examSlotHalls);

        }
        return examSlotRoomRepository.findByExamSlotHall_ExamSlotIn(examSlots);
    }

    private void assignInvigilatorsToRoom(Set<InvigilatorRegistration> registrations, List<ExamSlotRoom> examSlotRooms) {
        List<InvigilatorRegistration> availableInvigilators = new ArrayList<>(registrations);
        List<ExamSlotRoom> availableRooms = new ArrayList<>(examSlotRooms);

        for (int i = 0; i < availableRooms.size() && availableRooms.size() <= availableInvigilators.size(); i++) {
            ExamSlotRoom room = examSlotRooms.get(i);
            InvigilatorRegistration registration = availableInvigilators.get(i);

            InvigilatorAssignment assignment = InvigilatorAssignment.builder()
                    .invigilatorRegistration(registration)
                    .isHallInvigilator(false)
                    .build();
            invigilatorAssignmentRepository.save(assignment);
            room.setRoomInvigilator(assignment);
            examSlotRoomRepository.save(room);

            System.out.println("Phòng: " + room.getRoom().getRoomName());
            System.out.println("Phân công: " + registration.getInvigilator().getFuId());
        }
        registrations.removeAll(availableInvigilators.subList(0, Math.min(availableRooms.size(), availableInvigilators.size())));
//        Iterator<InvigilatorRegistration> registrationIterator = registrations.iterator();
//        for (ExamSlotRoom examSlotRoom : examSlotRooms) {
//            System.out.println("Room: " + examSlotRoom.getRoom().getRoomName());
//            if (registrationIterator.hasNext() && examSlotRoom.getRoomInvigilator() == null) {
//                InvigilatorRegistration registration = registrationIterator.next();
//                InvigilatorAssignment assignment = InvigilatorAssignment.builder()
//                        .invigilatorRegistration(registration)
//                        .isHallInvigilator(false)
//                        .build();
//                System.out.println("Assignment: " + assignment.getInvigilatorRegistration().getInvigilator().getFuId());
//                invigilatorAssignmentRepository.save(assignment);
//                examSlotRoom.setRoomInvigilator(assignment);
//                registrations.remove(registration);
//            }
//        }
    }

    private void assignInvigilatorsToHalls(Set<InvigilatorRegistration> registrations, List<ExamSlotHall> examSlotHalls) {
        Iterator<InvigilatorRegistration> registrationIterator = registrations.iterator();
        for (ExamSlotHall examSlotHall : examSlotHalls) {
            if (registrationIterator.hasNext() && examSlotHall.getHallInvigilator() == null) {
                InvigilatorRegistration registration = registrationIterator.next();
                InvigilatorAssignment assignment = InvigilatorAssignment.builder()
                        .invigilatorRegistration(registration)
                        .isHallInvigilator(true)
                        .build();
                invigilatorAssignmentRepository.save(assignment);
                examSlotHall.setHallInvigilator(assignment);
                examSlotHallRepository.save(examSlotHall);
                registrations.remove(registration);
            }
        }
    }

}
