package com.fullsnacke.eimsfuhcmbe.InvigilatorAssigment;

import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAttendance;
import com.fullsnacke.eimsfuhcmbe.service.InvigilatorAttendanceService;
import com.fullsnacke.eimsfuhcmbe.service.InvigilatorAttendanceServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@SpringBootTest
public class InvigilatorAttendanceServiceTest {

    @Autowired
    InvigilatorAttendanceServiceImpl invigilatorAttendanceService;

    @Test
    public void testAddInvigilatorAttendancesByDay() {
        List<InvigilatorAttendance> invigilatorAttendanceList = invigilatorAttendanceService.addInvigilatorAttendancesByDay();
        for (InvigilatorAttendance invigilatorAttendance : invigilatorAttendanceList) {
            System.out.println(invigilatorAttendance.getId());
        }
    }

}
