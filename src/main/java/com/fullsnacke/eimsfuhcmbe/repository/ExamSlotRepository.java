package com.fullsnacke.eimsfuhcmbe.repository;


import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExamSlotRepository extends JpaRepository<ExamSlot, Integer> {

    ExamSlot findById(int id);

}
