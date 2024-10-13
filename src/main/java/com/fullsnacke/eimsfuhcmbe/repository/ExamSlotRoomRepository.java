package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamSlotRoomRepository extends JpaRepository<ExamSlotRoom, Integer> {

    ExamSlotRoom findExamSlotRoomById(int id);
}
