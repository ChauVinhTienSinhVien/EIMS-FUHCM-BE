package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.Room;

import java.util.List;

public interface RoomService {

    List<Room> getAllRoom();
    Room createRoom(Room room);
    Room updateRoom(Room room, int id);
    void deleteRoom(int id);
    List<Room> findByRoomName(String roomName);

}
