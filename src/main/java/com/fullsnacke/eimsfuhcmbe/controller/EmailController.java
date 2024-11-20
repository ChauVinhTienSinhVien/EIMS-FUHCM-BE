package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.service.EmailService;
import com.fullsnacke.eimsfuhcmbe.service.ExcelFileService;
import com.fullsnacke.eimsfuhcmbe.service.ExcelFileServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    //Manager
    @GetMapping()
    @PreAuthorize("hasAuthority('email:create')")
    @Operation(summary = "Send attendance and total hours report to invigilator and response a list of failed emails if it not null" )
    public ResponseEntity<?> sendAttendanceAndTotalHoursReportToInvigilator(@RequestParam int semesterId, @RequestParam List<String> toEmails) {
        List<String> failedEmails = emailService.sendAttendanceAndHoursMailMessageInListEmails(semesterId, toEmails);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(failedEmails.size() == 0 ? "Emails sent successfully" : "Failed emails: " + failedEmails);
    }
}
