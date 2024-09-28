package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.Room;
import com.fullsnacke.eimsfuhcmbe.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private RoomRepository roomRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public List<Room> getAllRoom() {
        return roomRepository.findAll();
    }

    @Override
    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public Room updateRoom(Room room, int id) {

        Room roomInDB = roomRepository.findById(id).orElse(null);
        if (roomInDB == null) {
            throw new EntityNotFoundException("Room not found");
        }
        roomInDB.setRoomName(room.getRoomName());
        roomInDB.setCapacity(room.getCapacity());

        return roomRepository.save(roomInDB);
    }

    @Override
    public void deleteRoom(Room room) {
        roomRepository.delete(room);
    }

    @Override
    public Room findByRoomName(String roomName) {
        return roomRepository.findByRoomName(roomName);
    }


}
