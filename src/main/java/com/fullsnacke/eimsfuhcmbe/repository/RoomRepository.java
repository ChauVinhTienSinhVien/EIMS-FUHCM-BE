package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    List<Room> findByRoomNameLike(String roomName);
    Room findRoomById(Integer id);
    Room findRoomByRoomName(String roomName);

}
