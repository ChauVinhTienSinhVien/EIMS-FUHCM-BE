package com.fullsnacke.eimsfuhcmbe.repository;


import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlotHall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamSlotHallRepository extends JpaRepository<ExamSlotHall, Integer> {
    List<ExamSlotHall> findByExamSlot(ExamSlot examSlot);
    List<ExamSlotHall> findByExamSlotIn (List<ExamSlot> examSlots);
    ExamSlotHall findExamSlotHallById(int id);

}
