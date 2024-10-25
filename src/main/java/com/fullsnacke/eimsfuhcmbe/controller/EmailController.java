package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.service.EmailService;
import com.fullsnacke.eimsfuhcmbe.service.ExcelFileService;
import com.fullsnacke.eimsfuhcmbe.service.ExcelFileServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/email")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EmailController {
    EmailService emailService;
    ExcelFileService excelFileService;

    @GetMapping()
    public ResponseEntity<?> sendAttendanceAndTotalHoursReportToInvigilator(@RequestParam int semesterId, @RequestParam List<String> toEmails) {
        List<String> failedEmails = emailService.sendAttendanceAndHoursMailMessageInListEmails(semesterId, toEmails);
//        excelFileService.generateAttendanceAndTotalHoursExcelFileForSemester(semesterId, "nganvhheimsfuhcm@gmail.com");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(failedEmails.size() == 0 ? "Emails sent successfully" : "Failed emails: " + failedEmails);
    }
}
