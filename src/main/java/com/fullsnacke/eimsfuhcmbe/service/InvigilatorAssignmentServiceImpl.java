package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.ExamSlotRoomMapper;
import com.fullsnacke.eimsfuhcmbe.dto.mapper.InvigilatorAssignmentMapper;
import com.fullsnacke.eimsfuhcmbe.dto.mapper.InvigilatorRegistrationMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.ExchangeInvigilatorsRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.request.UpdateInvigilatorAssignmentRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.*;
import com.fullsnacke.eimsfuhcmbe.entity.*;
import com.fullsnacke.eimsfuhcmbe.enums.ExamSlotInvigilatorStatus;
import com.fullsnacke.eimsfuhcmbe.enums.ExamSlotStatus;
import com.fullsnacke.eimsfuhcmbe.enums.InvigilatorAssignmentStatus;
import com.fullsnacke.eimsfuhcmbe.exception.AuthenticationProcessException;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.assignment.CustomMessageException;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import com.fullsnacke.eimsfuhcmbe.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.Instant;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
    InvigilatorAssignmentMapper invigilatorAssignmentMapper;
    InvigilatorRegistrationMapper invigilatorRegistrationMapper;
    ExamSlotRepository examSlotRepository;
    ExamSlotRoomRepository examSlotRoomRepository;
    ExamSlotRoomMapper examSlotRoomMapper;
    ExamSlotHallRepository examSlotHallRepository;
    UserRepository userRepository;
    InvigilatorAttendanceServiceImpl invigilatorAttendanceService;
    private final InvigilatorAttendanceRepository invigilatorAttendanceRepository;


    @Transactional(rollbackFor = Exception.class)
    public List<ExamSlotRoomResponseDTO> assignInvigilators(List<Integer> examSlotIds) {
        if (examSlotIds.isEmpty()) {
            throw new CustomException(ErrorCode.EXAM_SLOT_ID_MISSING);
        }

        List<ExamSlot> examSlots = examSlotRepository.findByIdIn(examSlotIds);

        Map<Integer, List<InvigilatorRegistration>> registrationMap = invigilatorRegistrationRepository
                .findUnassignedRegistrationsByExamSlotInOrderByCreatedAtAsc(examSlots)
                .stream()
                .collect(Collectors.groupingBy(reg -> reg.getExamSlot().getId()));
        log.info("Registration Map: {}", registrationMap);
        registrationMap.forEach((examSlotId, registrations) -> {
            log.info("ExamSlot ID: {}, Registrations Size: {}", examSlotId, registrations.size());
        });

        if (registrationMap.isEmpty()) {
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
            if(halls.get(0).getHallInvigilator() != null){
                log.error("Hall already has invigilator");
                throw new CustomMessageException(HttpStatus.BAD_REQUEST, "Exam slot already has assigned");
            }
            if (halls.size() + rooms.size() > registrations.size()) {
                log.error("Not enough halls and rooms for invigilators");
                throw new CustomMessageException(HttpStatus.NOT_FOUND, "Insufficient number of invigilators available for exam slot ID: " + examSlot.getId());
            }

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
                    .status(InvigilatorAssignmentStatus.PENDING.getValue())
                    .build();
            assignments.add(assignment);

            room.setRoomInvigilator(assignment);
            roomsToUpdate.add(room);
            log.info("Room: {}", room.getRoom().getRoomName());
            log.info("Invigilator: {}", registration.getInvigilator().getFuId());
        }

        try {
            invigilatorAssignmentRepository.saveAll(assignments);
            invigilatorAttendanceService.addInvigilatorAttendances(assignments);
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
                    .status(InvigilatorAssignmentStatus.PENDING.getValue())
                    .build();
            assignments.add(assignment);

            hall.setHallInvigilator(assignment);
            hallsToUpdate.add(hall);
            log.info("Hall ID: {}", hall.getId());
            log.info("Invigilator: {}", registration.getInvigilator().getFuId());
        }

        try {
            invigilatorAssignmentRepository.saveAll(assignments);
            invigilatorAttendanceService.addInvigilatorAttendances(assignments);
            examSlotHallRepository.saveAll(hallsToUpdate);
        } catch (Exception e) {
            log.error("Error in assigning invigilator to hall: {}", e.getMessage());
            throw new CustomException(ErrorCode.FAILD_TO_CLASSIFY_INVIGILATOR);
        }

        registrations.subList(0, assignmentCount).clear();
    }


    public List<UserRegistrationResponseDTO> getUnassignedInvigilators(int examSlotId) {
        if (examSlotId <= 0) {
            throw new CustomException(ErrorCode.EXAM_SLOT_ID_MISSING);
        }

        List<InvigilatorRegistration> unassignedList = invigilatorRegistrationRepository.findUnassignedRegistrationsByExamSlot_IdOrderByCreatedAtAsc(examSlotId);

        return invigilatorRegistrationMapper.mapBasicInvigilatorRegistration(unassignedList);
    }

    public List<InvigilatorAssignmentResponseDTO> getAssignedInvigilators(int examSlotId) {
        if (examSlotId <= 0) {
            throw new CustomException(ErrorCode.EXAM_SLOT_ID_MISSING);
        }

        List<InvigilatorAssignment> assignedList = invigilatorAssignmentRepository.findInvigilatorAssignmentByInvigilatorRegistration_ExamSlot_Id(examSlotId);
        Map<Integer, InvigilatorAssignment> assignmentMap = assignedList.stream()
                .collect(Collectors.toMap(InvigilatorAssignment::getId, assignment -> assignment));

        List<InvigilatorAssignmentResponseDTO> responseDTOList = invigilatorAssignmentMapper.mapInvigilatorAssignments(assignedList);
        for (InvigilatorAssignmentResponseDTO assignment : responseDTOList) {
            assignment.setStatus(InvigilatorAssignmentStatus.fromValue(assignmentMap.get(assignment.getAssignmentId()).getStatus()).name());
        }
        return responseDTOList;
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
                    return new CustomMessageException(HttpStatus.NOT_FOUND, "Invigilator with FuId " + oldInvigilatorFuId + " not found.");
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


    public Set<ExamSlotDetail> getAllExamSlotsInSemesterWithStatus(int semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEMESTER_NOT_FOUND));

        // Chuyển đổi allExamSlots từ List sang Set
        Set<ExamSlot> allExamSlots = new HashSet<>(examSlotRepository.findExamSlotsBySemesterWithDetails(semester, ExamSlotStatus.APPROVED.getValue()));

        // Tạo một Set để lưu trữ kết quả cuối cùng
        Set<ExamSlotDetail> examSlotDetails = new HashSet<>();
        for (ExamSlot examSlot : allExamSlots) {
            // Lấy một ExamSlotHall làm đại diện
            int assignedInvigilators = invigilatorRegistrationRepository.countByExamSlot(examSlot);
            Optional<ExamSlotHall> representativeHall = examSlotHallRepository.findFirstByExamSlot(examSlot);

            String status;
            if (representativeHall.isPresent() && representativeHall.get().getHallInvigilator() != null) {
                status = ExamSlotInvigilatorStatus.ASSIGNED.name();
            } else {
                status = ExamSlotInvigilatorStatus.UNASSIGNED.name();
            }
            ExamSlotDetail examSlotDetail = invigilatorRegistrationMapper.toExamSlotDetail(examSlot);
            examSlotDetail.setStatus(status);
            examSlotDetails.add(examSlotDetail);
            examSlotDetail.setNumberOfRegistered(assignedInvigilators);
        }

        return examSlotDetails;
    }

    public List<InvigilatorAssignmentResponseDTO> getAllExamSlotsAssignedInSemester(int semesterId) {
        var semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEMESTER_NOT_FOUND));

        List<InvigilatorAssignment> assignedList = invigilatorAssignmentRepository.findBySemesterIdAndInvigilatorIdAndStatus(semester.getId(), getCurrentUser().getId());
        Map<Integer, InvigilatorAssignment> assignmentMap = assignedList.stream()
                .collect(Collectors.toMap(InvigilatorAssignment::getId, assignment -> assignment));

        List<InvigilatorAssignmentResponseDTO> responseDTOList = invigilatorAssignmentMapper.mapInvigilatorAssignments(assignedList);
        for (InvigilatorAssignmentResponseDTO assignment : responseDTOList) {
            assignment.setStatus(InvigilatorAssignmentStatus.fromValue(assignmentMap.get(assignment.getAssignmentId()).getStatus()).name());
        }
        return responseDTOList;
    }

    @Override
    @Transactional
    public List<InvigilatorAssignment> managerApproveInvigilatorAssignments(List<Integer> invigilatorAssignmentIds) {
        List<InvigilatorAssignment> invigilatorAssignments = invigilatorAssignmentRepository.findByIdIn(invigilatorAssignmentIds);
        if (invigilatorAssignments.isEmpty()) {
            throw new CustomException(ErrorCode.ASSIGNMENT_NOT_FOUND);
        } else {
            for (InvigilatorAssignment invigilatorAssignment : invigilatorAssignments) {
                invigilatorAssignment.setStatus(InvigilatorAssignmentStatus.APPROVED.getValue());
                invigilatorAssignment.setApprovedBy(getCurrentUser());
                invigilatorAssignment.setApprovedAt(Instant.now());
            }
            if (invigilatorAssignmentRepository.saveAll(invigilatorAssignments).isEmpty()) {
                throw new CustomException(ErrorCode.FAIL_TO_APPROVE_ASSIGNMENT);
            } else {
                invigilatorAttendanceService.addInvigilatorAttendances(invigilatorAssignments);
                return invigilatorAssignments;
            }
        }
    }

    private User getCurrentUser() {
        var context = SecurityContextHolder.getContext();
        if (context == null || context.getAuthentication() == null) {
            throw new AuthenticationProcessException(ErrorCode.AUTHENTICATION_CONTEXT_NOT_FOUND);
        }

        Authentication authentication = context.getAuthentication();

        String email = authentication.getName();
        if (email == null || email.isEmpty()) {
            throw new AuthenticationProcessException(ErrorCode.AUTHENTICATION_EMAIL_MISSING);
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    //dashboard
    @Override
    public List<InvigilatorAssignment> getAllAssignmentsInTimeRange(Instant startTime, Instant endTime) {
        return invigilatorAssignmentRepository.findAllByTimeRange(startTime, endTime);
    }

    @Override
    public InvigilatorAssignmentReportResponseDTO getInvigilatorAssignmentReport(int semesterId) {
        var semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEMESTER_NOT_FOUND));

        User currentUser = getCurrentUser();

        List<InvigilatorAttendance> attendances = invigilatorAttendanceRepository.findBySemesterAndInvigilator(semester, currentUser);

        int totalAssignments = attendances.size();
        int totalInvigilatedSlots = 0;
        int totalRequiredSlots = 0;
        int totalNonInvigilatedSlots = 0;

        double totalAssignedHours = 0;
        double totalInvigilatedHours = 0;
        double totalRequiredInvigilationHours = 0;

        for (InvigilatorAttendance attendance : attendances) {
            ExamSlot examSlot = attendance.getInvigilatorAssignment().getInvigilatorRegistration().getExamSlot();
            if (attendance.getInvigilatorAssignment().getInvigilatorRegistration().getExamSlot().getStartAt().isAfter(ZonedDateTime.now())) {
                if (attendance.getCheckIn() != null && attendance.getCheckOut() != null) {
                    totalInvigilatedSlots++;
                    totalInvigilatedHours += (examSlot.getStartAt().until(examSlot.getEndAt(), ChronoUnit.HOURS) + examSlot.getStartAt().until(examSlot.getEndAt(), ChronoUnit.MINUTES) / 60.0);
                } else {
                    totalNonInvigilatedSlots++;
                }
            } else {
                totalRequiredSlots++;
                totalRequiredInvigilationHours += (examSlot.getStartAt().until(examSlot.getEndAt(), ChronoUnit.HOURS) + examSlot.getStartAt().until(examSlot.getEndAt(), ChronoUnit.MINUTES) / 60.0);
            }
            totalAssignedHours += (examSlot.getStartAt().until(examSlot.getEndAt(), ChronoUnit.HOURS) + examSlot.getStartAt().until(examSlot.getEndAt(), ChronoUnit.MINUTES) / 60.0);
        }

        return InvigilatorAssignmentReportResponseDTO.builder()
                .totalAssigned(totalAssignments)
                .totalInvigilatedSlots(totalInvigilatedSlots)
                .totalRequiredInvigilationSlots(totalRequiredSlots)
                .totalNonInvigilatedSlots(totalNonInvigilatedSlots)
                .totalAssignedHours(totalAssignedHours)
                .totalInvigilatedHours(totalInvigilatedHours)
                .totalRequiredInvigilationHours(totalRequiredInvigilationHours)
                .build();
    }


}
