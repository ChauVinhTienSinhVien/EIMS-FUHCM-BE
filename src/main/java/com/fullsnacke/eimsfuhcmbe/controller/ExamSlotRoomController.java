package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.ExamSlotRoomMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotRoomRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotRoomResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotRoom;
import com.fullsnacke.eimsfuhcmbe.service.ExamSlotRoomService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/examslotrooms")
public class ExamSlotRoomController {

    @Autowired
    private ExamSlotRoomService examSlotRoomService;

    @Autowired
    private ExamSlotRoomMapper examSlotRoomMapper;

    @GetMapping
    @Operation(summary = "Retrieve all exam slot rooms", description = "Fetches a list of all exam slot rooms from the system. If no exam slot rooms are found, it will return a 204 No Content response.")
    public List<ExamSlotRoomResponseDTO> getAllExamSlotRooms() {

        List<ExamSlotRoomResponseDTO> examSlotRoomResponseDTOList = new ArrayList<>();
        // ...
        List<ExamSlotRoom> examSlotRoomList = examSlotRoomService.getAllExamSlotRoom();

        for (ExamSlotRoom examSlotRoom : examSlotRoomList) {
            ExamSlotRoomResponseDTO examSlotRoomResponseDTO = examSlotRoomMapper.toDto(examSlotRoom);
            examSlotRoomResponseDTOList.add(examSlotRoomResponseDTO);
        }

        return examSlotRoomResponseDTOList;
//        return examSlotRoomService.getAllExamSlotRoom();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve an exam slot room by ID", description = "Fetches an exam slot room from the system by its ID.")
    public ResponseEntity<ExamSlotRoomResponseDTO> getExamSlotRoomById(@PathVariable int id) {
        ExamSlotRoom examSlotRoom = examSlotRoomService.getExamSlotRoomById(id);

//        ExamSlotRoomResponseDTO examSlotRoomResponseDTO = ExamSlotRoomMapper.toDto(examSlotRoom);
//        return ResponseEntity.ok(examSlotRoomResponseDTO);

        return null;
    }

    @GetMapping("/unavailable-rooms")
    @Operation(summary = "Find available rooms within a time range", description = "Returns a list of rooms that are available for booking within a given time range.")
    public List<String> getAllAvailableRooms(@RequestParam ZonedDateTime startAt, @RequestParam ZonedDateTime endAt) {
        return examSlotRoomService.getAllUnavailableRooms(startAt, endAt);
    }

    @PostMapping
    @Operation(summary = "Create a new exam slot room", description = "Creates a new exam slot room in the system.")
    public ResponseEntity<ExamSlotRoom> addExamSlotRoom(@RequestBody ExamSlotRoomRequestDTO examSlotRomRequestDTO) {
//        ExamSlotRoom examSlotRoom = examSlotRoomService.addExamSlotRoom(examSlotRomRequestDTO);
        return null;
    }

}
