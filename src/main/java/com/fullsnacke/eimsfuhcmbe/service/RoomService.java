package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.Room;

import java.util.List;

public interface RoomService {

    List<Room> getAllRoom();
    Room createRoom(Room room);
    Room updateRoom(Room room);
    void deleteRoom(Room room);
    Room findByRoomName(String roomName);

}
