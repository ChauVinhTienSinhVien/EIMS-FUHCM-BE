package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.RoomRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.RoomResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Room;
import com.fullsnacke.eimsfuhcmbe.exception.repository.room.RoomNotFoundException;
import com.fullsnacke.eimsfuhcmbe.service.RoomServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private RoomServiceImpl roomServiceImpl;
    private ModelMapper modelMapper;

    public RoomController(RoomServiceImpl roomServiceImpl, ModelMapper modelMapper) {
        this.roomServiceImpl = roomServiceImpl;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @Operation(summary = "Get all rooms", description = "Retrieve a list of all rooms")
    public ResponseEntity<List<Room>> getAllRooms() {
        List<Room> roomList = roomServiceImpl.getAllRoom();
        if (roomList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(roomList);
        }
    }

    @PostMapping
    @Operation(summary = "Add a room", description = "Add a new room")
    public ResponseEntity<RoomResponseDTO> createRoom(@RequestBody @Valid RoomRequestDTO roomRequestDTO) {
        Room room = modelMapper.map(roomRequestDTO, Room.class);
        Room createdRoom = roomServiceImpl.createRoom(room);
        URI uri = URI.create("/rooms/" + createdRoom.getId());
        RoomResponseDTO roomResponseDTO = modelMapper.map(createdRoom, RoomResponseDTO.class);
        return ResponseEntity.created(uri).body(roomResponseDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a room", description = "Update a room by ID")
    public ResponseEntity<RoomResponseDTO> updateRoom(@PathVariable("id") int id, @RequestBody @Valid RoomRequestDTO roomRequestDTO) {
        Room room = modelMapper.map(roomRequestDTO, Room.class);
        try {
            Room updatedRoom = roomServiceImpl.updateRoom(room, id);
            RoomResponseDTO roomResponseDTO = modelMapper.map(updatedRoom, RoomResponseDTO.class);
            return ResponseEntity.ok(roomResponseDTO);
        } catch (RoomNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{name}")
    @Operation(summary = "Get room by name", description = "Retrieve a room by name")
    public ResponseEntity<List<RoomResponseDTO>> findByRoomName(@PathVariable("name") String name) {
        List<Room> roomList = roomServiceImpl.findByRoomName(name);
        if (roomList == null) {
            return ResponseEntity.notFound().build();
        }

        List<RoomResponseDTO> roomResponseDTOList = new ArrayList<>();
        for (Room r : roomList) {
            RoomResponseDTO roomResponseDTO = modelMapper.map(r, RoomResponseDTO.class);
            roomResponseDTOList.add(roomResponseDTO);
        }

        return ResponseEntity.ok(roomResponseDTOList);

    }


}
