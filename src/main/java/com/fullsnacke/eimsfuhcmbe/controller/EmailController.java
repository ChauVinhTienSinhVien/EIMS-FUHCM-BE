package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EmailController {
    EmailService emailService;

    @GetMapping("/send/attendance&totalhours")
    public ResponseEntity<?> sendAttendanceAndTotalHoursReport(@RequestParam int semesterId) {
        emailService.sendAttendanceAndHoursMailMessage("shinkiriloveforever@gmail.com", semesterId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Sent Attendance and Total Hours Report Successfully!!!");
    }

    @GetMapping("/send/simple")
    public ResponseEntity<?> sendSimpleMail() {
        emailService.sendSimpleMailMessage("NganVu", "shinkiriloveforever@gmail.com");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Sent Simple Mail Successfully!!!");
    }
}
