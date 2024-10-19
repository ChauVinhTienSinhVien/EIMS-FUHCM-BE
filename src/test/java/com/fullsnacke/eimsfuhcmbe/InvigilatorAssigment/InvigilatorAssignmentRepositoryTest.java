package com.fullsnacke.eimsfuhcmbe.InvigilatorAssigment;

import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAssignmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.Instant;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class InvigilatorAssignmentRepositoryTest {

    @Autowired
    InvigilatorAssignmentRepository invigilatorAssignmentRepository;

    @Test
    public void testFindByExamSlotId() {
        List<InvigilatorAssignment> invigilatorAssignments = invigilatorAssignmentRepository.findByExamSlotId(6);
        for (InvigilatorAssignment invigilatorAssignment : invigilatorAssignments) {
            System.out.println(invigilatorAssignment.getId());
        }
    }

    @Test
    public void findByExamSlotStartAtInDay() {
        Instant day = Instant.parse("2024-10-25T00:00:00Z");
        List<InvigilatorAssignment> invigilatorAssignments = invigilatorAssignmentRepository.findByExamSlotStartAtInDay(day);
        for (InvigilatorAssignment invigilatorAssignment : invigilatorAssignments) {
            System.out.println(invigilatorAssignment.getId());
        }
    }
}
