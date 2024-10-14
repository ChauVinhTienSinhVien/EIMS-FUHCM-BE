package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamSlotRoomRepository extends JpaRepository<ExamSlotRoom, Integer> {
    List<ExamSlotRoom> findByExamSlotHall_ExamSlot(ExamSlot examSlot);
    List<ExamSlotRoom> findByExamSlotHall_ExamSlotIn(List<ExamSlot> examSlot);
    ExamSlotRoom findExamSlotRoomById(int id);
}
