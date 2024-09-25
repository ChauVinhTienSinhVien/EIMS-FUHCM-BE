package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.RoomRequestDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Room;
import com.fullsnacke.eimsfuhcmbe.service.RoomServiceImpl;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
    public ResponseEntity<List<Room>> getAllRooms() {
        List<Room> roomList = roomServiceImpl.getAllRoom();
        if (roomList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(roomList);
        }
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody @Valid RoomRequestDTO roomRequestDTO) {
        Room room = modelMapper.map(roomRequestDTO, Room.class);
        Room createdRoom = roomServiceImpl.createRoom(room);
        URI uri = URI.create("/rooms/" + createdRoom.getId());
        return ResponseEntity.created(uri).body(createdRoom);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable("id") int id, @RequestBody @Valid RoomRequestDTO roomRequestDTO) {
        Room room = modelMapper.map(roomRequestDTO, Room.class);
        room.setId(id);
        Room updatedRoom = roomServiceImpl.updateRoom(room);
        if (updatedRoom == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedRoom);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Room> findByRoomName(@PathVariable("name") String name) {
        Room room = roomServiceImpl.findByRoomName(name);
        if (room == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(room);

    }


}
