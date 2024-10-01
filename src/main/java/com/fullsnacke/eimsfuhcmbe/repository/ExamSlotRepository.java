package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamSlotRepository extends JpaRepository<Room, Integer> {
}
