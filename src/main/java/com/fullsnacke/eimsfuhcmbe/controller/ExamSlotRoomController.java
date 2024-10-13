package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.ExamSlotHallMapper;
import com.fullsnacke.eimsfuhcmbe.dto.mapper.ExamSlotRoomMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotRomRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotRoomResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotRoom;
import com.fullsnacke.eimsfuhcmbe.service.ExamSlotRoomService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/examslotrooms")
public class ExamSlotRoomController {

    @Autowired
    private ExamSlotRoomService examSlotRoomService;

    @GetMapping
    @Operation(summary = "Retrieve all exam slot rooms", description = "Fetches a list of all exam slot rooms from the system. If no exam slot rooms are found, it will return a 204 No Content response.")
    public List<ExamSlotRoom> getAllExamSlotRooms() {

        List<ExamSlotRoomResponseDTO> examSlotRoomResponseDTOList = new ArrayList<>();
        // ...

        return examSlotRoomService.getAllExamSlotRoom();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve an exam slot room by ID", description = "Fetches an exam slot room from the system by its ID.")
    public ResponseEntity<ExamSlotRoomResponseDTO> getExamSlotRoomById(@PathVariable int id) {
        ExamSlotRoom examSlotRoom = examSlotRoomService.getExamSlotRoomById(id);

        ExamSlotRoomResponseDTO examSlotRoomResponseDTO = ExamSlotRoomMapper.toDto(examSlotRoom);

        return ResponseEntity.ok(examSlotRoomResponseDTO);
    }

    @PostMapping
    @Operation(summary = "Create a new exam slot room", description = "Creates a new exam slot room in the system.")
    public ResponseEntity<ExamSlotRoom> addExamSlotRoom(@RequestBody ExamSlotRomRequestDTO examSlotRomRequestDTO) {
//        ExamSlotRoom examSlotRoom = examSlotRoomService.addExamSlotRoom(examSlotRomRequestDTO);
        return null;
    }

}
