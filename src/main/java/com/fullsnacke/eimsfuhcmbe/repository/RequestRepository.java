package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.Request;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findByCreatedBy(User invigilator);
    List<Request> findByExamSlot_SubjectExam_SubjectId_SemesterId_Id(int semesterId);
}
