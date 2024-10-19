package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotHall;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface ExamSlotRoomRepository extends JpaRepository<ExamSlotRoom, Integer> {

    List<ExamSlotRoom> findByExamSlotHall(ExamSlotHall examSlotHall);
    List<ExamSlotRoom> findByExamSlotHall_ExamSlot(ExamSlot examSlot);
    List<ExamSlotRoom> findByExamSlotHall_ExamSlotIn(List<ExamSlot> examSlot);
    ExamSlotRoom findExamSlotRoomById(int id);
    @Query("SELECT r.room.id FROM ExamSlotRoom r WHERE r.examSlotHall.examSlot.startAt < :endAt AND r.examSlotHall.examSlot.endAt > :startAt")
    List<String> findAvailableRooms(@Param("startAt") ZonedDateTime startAt, @Param("endAt") ZonedDateTime endAt);

    Set<ExamSlotRoom> findByIdIn(Set<Integer> ids);
}
