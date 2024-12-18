package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.ExamSlotRoomMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotRoomRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotRoomResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotRoom;
import com.fullsnacke.eimsfuhcmbe.service.ExamSlotRoomService;
import com.fullsnacke.eimsfuhcmbe.service.ExamSlotRoomServiceImpl;
import com.fullsnacke.eimsfuhcmbe.service.ExamSlotService;
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

    //STAFF
    @GetMapping
    @PreAuthorize("hasAuthority('exam_slot_room:read')")
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
    }

    //STAFF
    @GetMapping("/unavailable-rooms")
    @PreAuthorize("hasAuthority('exam_slot_room:read')")
    @Operation(summary = "Find available rooms within a time range", description = "Returns a list of rooms that are available for booking within a given time range.")
    public List<String> getAllAvailableRooms(@RequestParam ZonedDateTime startAt, @RequestParam ZonedDateTime endAt) {
        return examSlotRoomService.getAllAvailableRooms(startAt, endAt);
    }

    //STAFF
    @GetMapping("/dashboard/exam-slot/{examSlotId}")
    @PreAuthorize("hasAuthority('exam_slot_room:read')")
    @Operation(summary = "Retrieve all exam slot rooms by exam slot ID", description = "Fetches a list of all exam slot rooms by exam slot ID from the system. If no exam slot rooms are found, it will return a 204 No Content response.")
    public List<String> getExamSlotRoomsByExamSlotId(@PathVariable int examSlotId) {
        List<String> examSlotRoomResponseDTOList = new ArrayList<>();
        List<ExamSlotRoom> examSlotRoomList = examSlotRoomService.getExamSlotRoomByExamSlotId(examSlotId);

        for (ExamSlotRoom examSlotRoom : examSlotRoomList) {

            examSlotRoomResponseDTOList.add(examSlotRoom.getRoom().getRoomName());
        }

        return examSlotRoomResponseDTOList;
    }

}
