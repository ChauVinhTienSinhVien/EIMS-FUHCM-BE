package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.ExamSlotMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotDetail;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotResponseDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotSummaryByTimeRangeDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotSummaryDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.Room;
import com.fullsnacke.eimsfuhcmbe.entity.SubjectExam;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.enums.ExamSlotStatus;
import com.fullsnacke.eimsfuhcmbe.exception.repository.examslot.ExamSlotNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotRepository;
import com.fullsnacke.eimsfuhcmbe.repository.SubjectExamRepository;
import com.fullsnacke.eimsfuhcmbe.service.ExamSlotServiceImpl;
import com.fullsnacke.eimsfuhcmbe.service.InvigilatorAssignmentServiceImpl;
import com.fullsnacke.eimsfuhcmbe.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/examslots")
public class ExamSlotController {

    @Autowired
    private ExamSlotServiceImpl examSlotServiceImpl;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private ExamSlotMapper examSlotMapper;

    @Autowired
    private SubjectExamRepository examRepository;

    @Autowired
    private SubjectExamRepository subjectExamRepository;
    @Autowired
    private InvigilatorAssignmentServiceImpl invigilatorAssignmentServiceImpl;

    @GetMapping
    @Operation(summary = "Retrieve all exam slots", description = "Fetches a list of all exam slots from the system. If no exam slots are found, it will return a 204 No Content response.")
    public ResponseEntity<List<ExamSlotResponseDTO>> getAllExamSlots() {
        List<ExamSlot> examSlotList = examSlotServiceImpl.getAllExamSlot();
        if (examSlotList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<ExamSlotResponseDTO> examSlotResponseDTOList = new ArrayList<>();
            for (ExamSlot e:examSlotList) {
                ExamSlotResponseDTO examSlotResponseDTO = examSlotMapper.toDto(e);
//                SubjectExam subjectExam = subjectExamRepository.findSubjectExamById(examSlotResponseDTO.getSubjectExamId().getId());
//                examSlotResponseDTO.setSubjectExamId(subjectExam.getId());
                examSlotResponseDTOList.add(examSlotResponseDTO);
            }
            return ResponseEntity.ok(examSlotResponseDTOList);
        }
    }

    @GetMapping("/using-rooms/{examSlotId}")
    @Operation(summary = "Find rooms that are currently in use", description = "Returns a list of rooms that are currently in use.")
    public List<List<Room>> getAllUsingRooms(@PathVariable int examSlotId) {
        return examSlotServiceImpl.getHallForExamSlot(examSlotId);
    }

    @GetMapping("/by-semester/{semesterId}")
    @Operation(summary = "Retrieve all exam slots by semester ID", description = "Fetches a list of all exam slots from the system based on the semester ID. If no exam slots are found, it will return a 204 No Content response.")
    public ResponseEntity<List<ExamSlotResponseDTO>> getExamSlotsBySemesterId(@PathVariable("semesterId") int semesterId) {
        List<ExamSlot> examSlotList = examSlotServiceImpl.getExamSlotsBySemesterId(semesterId);

        if (examSlotList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<ExamSlotResponseDTO> examSlotResponseDTOList = new ArrayList<>();

            for (ExamSlot e:examSlotList) {
                System.out.println(e.getStartAt());
                ExamSlotResponseDTO examSlotResponseDTO = examSlotMapper.toDto(e);
//                SubjectExam subjectExam = subjectExamRepository.findSubjectExamById(examSlotResponseDTO.getSubjectExamId().getId());
//                examSlotResponseDTO.setSubjectExamId(subjectExam);
                examSlotResponseDTOList.add(examSlotResponseDTO);
            }
            return ResponseEntity.ok(examSlotResponseDTOList);
        }
    }

    @PostMapping
    @Operation(summary = "Create a new exam slot", description = "Creates a new exam slot based on the provided data in the request body. The created exam slot is returned with a 201 Created response.")
    public ResponseEntity<ExamSlotResponseDTO> createExamSlot(@RequestBody @Valid ExamSlotRequestDTO examSlotRequestDTO) {
        ExamSlot examSlot = examSlotMapper.toEntity(examSlotRequestDTO);


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User currentUser = userServiceImpl.getUserByEmail(email);

        examSlot.setCreatedAt(Instant.now());
        examSlot.setCreatedBy(currentUser);
        SubjectExam subjectExam = subjectExamRepository.findSubjectExamById(examSlot.getSubjectExam().getId());
        examSlot.setSubjectExam(subjectExam);

        examSlot.setStatus(ExamSlotStatus.NEEDS_ROOM_ASSIGNMENT.getValue());
        ExamSlot createdExamSlot = examSlotServiceImpl.createExamSlot(examSlot);
        URI uri = URI.create("/examslots/" + createdExamSlot.getId());
        System.out.println(createdExamSlot.getCreatedAt());
        ExamSlotResponseDTO examSlotResponseDTO = examSlotMapper.toDto(createdExamSlot);
//        SubjectExam subjectExam = subjectExamRepository.findSubjectExamById(examSlotResponseDTO.getSubjectExamId().getId());
//        examSlotResponseDTO.setSubjectExamId(subjectExam);
        return ResponseEntity.created(uri).body(examSlotResponseDTO);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Bulk import exam slots", description = "Accepts a list of exam slot data in the request body and creates multiple exam slots in the system. Returns a list of the created exam slots.")
    public ResponseEntity<List<ExamSlotResponseDTO>> importExamSlot(@RequestBody @Valid List<ExamSlotRequestDTO> examSlotRequestDTOList) {

        List<ExamSlot> createdExamSlots = new ArrayList<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userServiceImpl.getUserByEmail(email);

        for (ExamSlotRequestDTO examSlotRequestDTO : examSlotRequestDTOList) {
            ExamSlot examSlot = examSlotMapper.toEntity(examSlotRequestDTO);
            examSlot.setCreatedAt(Instant.now());
            examSlot.setCreatedBy(currentUser);
            SubjectExam subjectExam = subjectExamRepository.findSubjectExamById(examSlot.getSubjectExam().getId());
            examSlot.setSubjectExam(subjectExam);
            examSlot.setStatus(ExamSlotStatus.NEEDS_ROOM_ASSIGNMENT.getValue());

            createdExamSlots.add(examSlotServiceImpl.createExamSlot(examSlot));
        }

        List<ExamSlotResponseDTO> examSlotResponseDTOList = createdExamSlots.stream()
                .map(examSlotMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(examSlotResponseDTOList);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an exam slot", description = "Updates an existing exam slot by its ID with the new data provided in the request body. Returns the updated exam slot or a 404 Not Found response if the slot doesn't exist.")
    public ResponseEntity<ExamSlotResponseDTO> updateExamSlot(@PathVariable("id") int id,@RequestBody @Valid ExamSlotRequestDTO examSlotRequestDTO) {
        try{
            ExamSlot existingExamSlot = examSlotServiceImpl.findById(id);
            if (existingExamSlot == null) {
                return ResponseEntity.notFound().build();
            }

            if (existingExamSlot.getApprovedBy() != null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }


            ExamSlot examSlot = examSlotMapper.toEntity(examSlotRequestDTO);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User currentUser = userServiceImpl.getUserByEmail(email);

            examSlot.setCreatedAt(existingExamSlot.getCreatedAt());
            examSlot.setCreatedBy(existingExamSlot.getCreatedBy());
            examSlot.setUpdatedBy(currentUser);
            examSlot.setUpdatedAt(Instant.now());
            SubjectExam subjectExam = subjectExamRepository.findSubjectExamById(examSlot.getSubjectExam().getId());
            examSlot.setSubjectExam(subjectExam);

            ExamSlot updateExamSlot =  examSlotServiceImpl.updateExamSlot(examSlot, id);
            ExamSlotResponseDTO examSlotResponseDTO = examSlotMapper.toDto(updateExamSlot);
//            SubjectExam subjectExam = subjectExamRepository.findSubjectExamById(examSlotResponseDTO.getSubjectExamId().getId());
//            examSlotResponseDTO.setSubjectExamId(subjectExam);
            return ResponseEntity.ok(examSlotResponseDTO);
        } catch (ExamSlotNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/manager-update/{id}")
    @Operation(summary = "Update an exam slot", description = "Updates an existing exam slot by its ID with the new data provided in the request body. Returns the updated exam slot or a 404 Not Found response if the slot doesn't exist.")
    public ResponseEntity<ExamSlotResponseDTO> managerUpdateExamSlot(@PathVariable("id") int id,@RequestBody @Valid ExamSlotRequestDTO examSlotRequestDTO) {
        try{
            ExamSlot existingExamSlot = examSlotServiceImpl.findById(id);
            if (existingExamSlot == null) {
                return ResponseEntity.notFound().build();
            }

            if (existingExamSlot.getApprovedBy() != null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }


            ExamSlot examSlot = examSlotMapper.toEntity(examSlotRequestDTO);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User currentUser = userServiceImpl.getUserByEmail(email);

            examSlot.setCreatedAt(existingExamSlot.getCreatedAt());
            examSlot.setCreatedBy(existingExamSlot.getCreatedBy());
            examSlot.setUpdatedBy(existingExamSlot.getUpdatedBy());
            examSlot.setUpdatedAt(existingExamSlot.getUpdatedAt());
            examSlot.setApprovedBy(currentUser);
            examSlot.setApprovedAt(Instant.now());

            SubjectExam subjectExam = subjectExamRepository.findSubjectExamById(examSlot.getSubjectExam().getId());
            examSlot.setSubjectExam(subjectExam);

            ExamSlot updateExamSlot =  examSlotServiceImpl.managerUpdateExamSlot(examSlot, id);
            ExamSlotResponseDTO examSlotResponseDTO = examSlotMapper.toDto(updateExamSlot);

            if (updateExamSlot.getStatus() == ExamSlotStatus.REJECTED.getValue()) {
                examSlotServiceImpl.removeExamSlotHall(updateExamSlot.getId());
            }

            return ResponseEntity.ok(examSlotResponseDTO);
        } catch (ExamSlotNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }



    @GetMapping("/{id}")
    @Operation(summary = "Retrieve an exam slot by ID", description = "Fetches a specific exam slot based on its ID. Returns the exam slot data if found, otherwise returns a 404 Not Found.")
    public ResponseEntity<ExamSlotResponseDTO> getExamSlotById(@PathVariable("id") int id) {
        ExamSlot examSlot = examSlotServiceImpl.findById(id);
        if (examSlot == null) {
            return ResponseEntity.notFound().build();
        } else {
            ExamSlotResponseDTO examSlotResponseDTO = examSlotMapper.toDto(examSlot);
            return ResponseEntity.ok(examSlotResponseDTO);
        }
    }

    @GetMapping("/dashboard/exam-slot-summary/in-time-range")
    @Operation(summary = "Retrieve exam slots within a time range", description = "Fetches a list of exam slots that fall within the specified start and end times.")
    public ResponseEntity<List<ExamSlotSummaryByTimeRangeDTO>> getExamSlotsInTimeRange(
            @RequestParam("startTime") ZonedDateTime startTime,
            @RequestParam("endTime") ZonedDateTime endTime) {
        List<ExamSlot> examSlotList = examSlotServiceImpl.getExamSlotsInTimeRange(startTime, endTime);
        if (examSlotList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            // Group by date and count the slots per day
            Map<LocalDate, Long> slotsByDate = examSlotList.stream()
                    .collect(Collectors.groupingBy(
                            slot -> slot.getStartAt().toLocalDate(), // Extract just the LocalDate part
                            Collectors.counting()
                    ));

            // Convert the map to a list of ExamSlotSummaryByDateDTO
            List<ExamSlotSummaryByTimeRangeDTO> summaryList = slotsByDate.entrySet().stream()
                    .map(entry -> new ExamSlotSummaryByTimeRangeDTO(entry.getKey().toString(), entry.getValue().intValue()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(summaryList);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an exam slot by ID", description = "Deletes a specific exam slot based on its ID. Returns a 204 No Content if successful, or a 404 Not Found if the exam slot does not exist.")
    public ResponseEntity<?> deleteExamSlotById(@PathVariable("id") int id) {
        try {
            examSlotServiceImpl.deleteExamSlot(id);
            return ResponseEntity.noContent().build();
        } catch (ExamSlotNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/dashboard/to-day")
    @Operation(summary = "Retrieve exam slots for today", description = "Fetches a list of exam slots that are scheduled for today.")
    public ResponseEntity<List<ExamSlotResponseDTO>> getExamSlotsForToday() {
        ZonedDateTime startTime = ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime endTime = ZonedDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        List<ExamSlot> examSlotListInTime = examSlotServiceImpl.getExamSlotsInTimeRange(startTime, endTime);
        List<ExamSlot> examSlotList = new ArrayList<>();
        for (ExamSlot e:examSlotListInTime) {
            if (e.getStatus() == ExamSlotStatus.APPROVED.getValue()) {
                examSlotList.add(e);
            }
        }
        if (examSlotList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<ExamSlotResponseDTO> examSlotResponseDTOList = examSlotList.stream()
                    .map(examSlotMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(examSlotResponseDTOList);
        }
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "Retrieve exam slots by status", description = "Fetches a list of exam slots based on the status provided in the request. If no exam slots are found, it will return a 204 No Content response.")
    public ResponseEntity<List<ExamSlotResponseDTO>> getExamSlotsByStatus(@PathVariable("status") String status) {
        List<ExamSlot> examSlotList = examSlotServiceImpl.getExamSlotsByStatus(ExamSlotStatus.valueOf(status).getValue());
        if (examSlotList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<ExamSlotResponseDTO> examSlotResponseDTOList = examSlotList.stream()
                    .map(examSlotMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(examSlotResponseDTOList);
        }

    }

    @GetMapping("/status")
    @Operation(summary = "Retrieve exam slots status in a date range", description = "Fetches a list of exam slots based on the start and end date provided in the request parameters. Returns the exam slots data if found, otherwise returns a 404 Not Found.")
    public ResponseEntity<List<ExamSlotDetail>> getExamSlotsStatusIn (@RequestParam() LocalDate startAt, @RequestParam LocalDate endAt) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(examSlotServiceImpl.getExamSlotsStatusIn(startAt, endAt));
    }

    @GetMapping("/dashboard/invigilation-summary")
    @Operation(summary = "Retrieve exam slots summary", description = "Fetches a summary of exam slots based on the semester ID provided in the request. Returns the exam slots data if found, otherwise returns a 404 Not Found.")
    public ResponseEntity<List<ExamSlotSummaryDTO>> getExamSlotsSummary(@RequestParam("startTime") ZonedDateTime startTime,@RequestParam("endTime") ZonedDateTime endTime) {
        List<ExamSlot> examSlotList = examSlotServiceImpl.getExamSlotsInTimeRange(startTime, endTime);
        if (examSlotList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<ExamSlotSummaryDTO> examSlotSummaryDTOList = new ArrayList<>();
        for (ExamSlot e:examSlotList) {
            if (e.getStatus() == ExamSlotStatus.APPROVED.getValue()) {
                ExamSlotSummaryDTO examSlotSummaryDTO = new ExamSlotSummaryDTO();
                ExamSlotResponseDTO examSlotResponseDTO = examSlotMapper.toDto(e);
                examSlotSummaryDTO.setExamSlot(examSlotResponseDTO);
                int totalInvigilatorsAssigned = invigilatorAssignmentServiceImpl.getAssignedInvigilators(e.getId()).size();
                examSlotSummaryDTO.setTotalInvigilatorsAssigned(totalInvigilatorsAssigned);
                int totalInvigilatorsRegistered = invigilatorAssignmentServiceImpl.getUnassignedInvigilators(e.getId()).size();
                examSlotSummaryDTO.setTotalInvigilatorsRegistered(totalInvigilatorsRegistered);
                examSlotSummaryDTOList.add(examSlotSummaryDTO);
            }
        }

        return ResponseEntity.ok(examSlotSummaryDTOList);
    }

}