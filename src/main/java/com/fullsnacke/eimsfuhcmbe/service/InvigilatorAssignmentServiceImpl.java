package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.ExamSlotRoomMapper;
import com.fullsnacke.eimsfuhcmbe.dto.mapper.InvigilatorRegistrationMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.ExchangeInvigilatorsRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.UpdateInvigilatorAssignmentRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotRoomResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.UserRegistrationResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.UserResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.*;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import com.fullsnacke.eimsfuhcmbe.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvigilatorAssignmentServiceImpl implements InvigilatorAssignmentService {

    Logger log = LoggerFactory.getLogger(InvigilatorAssignmentServiceImpl.class);

    SemesterRepository semesterRepository;
    InvigilatorRegistrationRepository invigilatorRegistrationRepository;
    InvigilatorAssignmentRepository invigilatorAssignmentRepository;
    InvigilatorRegistrationMapper invigilatorRegistrationMapper;
    ExamSlotRepository examSlotRepository;
    ExamSlotRoomRepository examSlotRoomRepository;
    ExamSlotRoomMapper examSlotRoomMapper;
    ExamSlotHallRepository examSlotHallRepository;

    @Transactional(rollbackFor = Exception.class)
    public List<ExamSlotRoomResponseDTO> assignInvigilators(List<Integer> examSlotIds) {
        if(examSlotIds.isEmpty()) {
            throw new CustomException(ErrorCode.EXAM_SLOT_ID_MISSING);
        }
//        List<ExamSlot> examSlots = examSlotRepository.findExamSlotsBySemesterWithDetails(semester);
        List<ExamSlot> examSlots = examSlotRepository.findByIdIn(examSlotIds);

        Map<Integer, List<InvigilatorRegistration>> registrationMap = invigilatorRegistrationRepository
                .findUnassignedRegistrationsByExamSlotInOrderByCreatedAtAsc(examSlots)
                .stream()
                .collect(Collectors.groupingBy(reg -> reg.getExamSlot().getId()));
        log.info("Registration Map: {}", registrationMap);
        registrationMap.forEach((examSlotId, registrations) -> {
            log.info("ExamSlot ID: {}, Registrations Size: {}", examSlotId, registrations.size());
        });

        if(registrationMap.isEmpty()) {
            throw new CustomException(ErrorCode.NO_INVIGILATOR_REGISTRATION);
        }

        Map<Integer, List<ExamSlotHall>> hallMap = examSlotHallRepository
                .findByExamSlotIn(examSlots)
                .stream()
                .collect(Collectors.groupingBy(hall -> hall.getExamSlot().getId()));

        log.info("Hall Map: {}", hallMap);
        hallMap.forEach((examSlotId, halls) -> {
            log.info("ExamSlot ID: {}, Halls: {}", examSlotId, halls.size());
        });

        Map<Integer, List<ExamSlotRoom>> roomMap = examSlotRoomRepository
                .findByExamSlotHall_ExamSlotIn(examSlots)
                .stream()
                .collect(Collectors.groupingBy(room -> room.getExamSlotHall().getExamSlot().getId()));

        log.info("Room Map: {}", roomMap);
        roomMap.forEach((examSlotId, rooms) -> {
            log.info("ExamSlot ID: {}, Rooms: {}", examSlotId, rooms.size());
        });

        for (ExamSlot examSlot : examSlots) {
            List<InvigilatorRegistration> registrations = registrationMap.getOrDefault(examSlot.getId(), new ArrayList<>());
            List<ExamSlotHall> halls = hallMap.getOrDefault(examSlot.getId(), new ArrayList<>());
            List<ExamSlotRoom> rooms = roomMap.getOrDefault(examSlot.getId(), new ArrayList<>());

            log.info("Processing ExamSlot ID: {}", examSlot.getId());
            log.info("Registrations: {}", registrations.size());
            log.info("Halls: {}", halls.size());
            log.info("Rooms: {}", rooms.size());

            assignInvigilatorsToRoom(registrations, rooms);
            assignInvigilatorsToHalls(registrations, halls);
        }

        Set<Integer> examSlotRoomIds = roomMap.values().stream()
                .flatMap(List::stream)
                .map(ExamSlotRoom::getId)
                .collect(Collectors.toSet());
        return examSlotRoomRepository.findByIdIn(examSlotRoomIds).stream()
                .map(examSlotRoomMapper::toDto)
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    protected void assignInvigilatorsToRoom(List<InvigilatorRegistration> registrations, List<ExamSlotRoom> examSlotRooms) {
        List<ExamSlotRoom> availableRooms = examSlotRooms.stream()
                .filter(room -> room.getRoomInvigilator() == null)
                .toList();
        log.info("Available Rooms: {}", availableRooms.size());
        availableRooms.forEach(room ->
                log.info("ExamSlotRoom ID: {}", room.getId())
        );

        int assignmentCount = Math.min(registrations.size(), availableRooms.size());
        log.info("Assignment Count: {}", assignmentCount);

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
            log.info("Room: {}", room.getRoom().getRoomName());
            log.info("Invigilator: {}", registration.getInvigilator().getFuId());
        }

        try {
            invigilatorAssignmentRepository.saveAll(assignments);
            examSlotRoomRepository.saveAll(roomsToUpdate);
        } catch (Exception e) {
            log.error("Error in assigning invigilator to room: {}", e.getMessage());
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
            log.info("Hall ID: {}", hall.getId());
            log.info("Invigilator: {}", registration.getInvigilator().getFuId());
        }

        try {
            invigilatorAssignmentRepository.saveAll(assignments);
            examSlotHallRepository.saveAll(hallsToUpdate);
        } catch (Exception e) {
            log.error("Error in assigning invigilator to hall: {}", e.getMessage());
            throw new CustomException(ErrorCode.FAILD_TO_CLASSIFY_INVIGILATOR);
        }

        registrations.subList(0, assignmentCount).clear();
    }


    public List<UserRegistrationResponseDTO> getUnassignedInvigilators(int examSlotId) {
        if(examSlotId <= 0) {
            throw new CustomException(ErrorCode.EXAM_SLOT_ID_MISSING);
        }

        List<InvigilatorRegistration> unassignedList = invigilatorRegistrationRepository.findUnassignedRegistrationsByExamSlot_IdOrderByCreatedAtAsc(examSlotId);

        return invigilatorRegistrationMapper.mapBasicInvigilatorRegistration(unassignedList);
    }

    @Transactional(rollbackFor = Exception.class)
    public String exchangeInvigilators(UpdateInvigilatorAssignmentRequestDTO request) {
        int assignmentId = request.getAssignmentId();
        int newRegistrationId = request.getNewRegistrationId();
        InvigilatorAssignment oldAssignment = invigilatorAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> {
                    log.error("Assignment not found: {}", assignmentId);
                    return new CustomException(ErrorCode.ASSIGNMENT_NOT_FOUND);
                });
        InvigilatorRegistration newInvigilator = invigilatorRegistrationRepository.findById(newRegistrationId)
                .orElseThrow(() -> {
                    log.error("New invigilator not found: {}", newRegistrationId);
                    return new CustomException(ErrorCode.INVIGILATOR_NOT_FOUND);
                });
        exchangeInvigilators(oldAssignment, newInvigilator);
        return "Exchanged successfully";
    }

    private void exchangeInvigilators(InvigilatorAssignment oldAssignment, InvigilatorRegistration newInvigilator) {
        try {
            oldAssignment.setInvigilatorRegistration(newInvigilator);
            invigilatorAssignmentRepository.save(oldAssignment);
        } catch (Exception e) {
            log.error("Error in updating invigilator assignment: {}", e.getMessage());
            throw new CustomException(ErrorCode.EXCHANGE_INVIGILATORS_FAILED);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public String exchangeInvigilators(Request requestEntity, ExchangeInvigilatorsRequestDTO request) {

        int examSlotId = requestEntity.getExamSlot().getId();
        String oldInvigilatorFuId = requestEntity.getCreatedBy().getFuId();

        InvigilatorAssignment oldAssignment = invigilatorAssignmentRepository
                .findByExamSlotIdAndInvigilatorFuId(examSlotId, oldInvigilatorFuId)
                .orElseThrow(() -> {
                    log.error("Old invigilator not found: {}", oldInvigilatorFuId);
                    return new CustomException(ErrorCode.OLD_INVIGILATOR_NOT_FOUND);
                });

        InvigilatorRegistration newInvigilator = invigilatorRegistrationRepository
                .findByExamSlotIdAndInvigilatorFuId(examSlotId, request.getNewInvigilatorFuId())
                .orElseThrow(() -> {
                    log.error("New invigilator not found: {}", request.getNewInvigilatorFuId());
                    return new CustomException(ErrorCode.INVIGILATOR_NOT_FOUND);
                });

        exchangeInvigilators(oldAssignment, newInvigilator);
        return "Exchanged successfully";
    }
}
