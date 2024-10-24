package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.assignment.CustomMessageException;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String ATTENDANCE_AND_TOTAL_HOURS_REPORT = "Attendance and Total Hours Report";
    public static final String ATTENDANCE_AND_TOTAL_HOURS_XLSX = "AttendanceAndTotalHours.xlsx";

    @Value("${spring.mail.verify.host}")
    @NonFinal
    String host;
    @Value("${spring.mail.username}")
    @NonFinal
    String formEmail;

    JavaMailSender emailSender;
    ExcelFileService excelFileService;


    public void sendSimpleMailMessage(String name, String to) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject("New User Account Verification");
            message.setFrom(formEmail);
            message.setTo(to);
            message.setText("Hello " + name + ",\n\n" +
                    "Please click the link below to verify your account:\n\n" +
                    host + "/verify?token=" + to + "dfasdfsdfaewrwefasfsdf\n\n" +
                    "Thank you,\n" +
                    "EIMS Team");
            emailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Detailed error message: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
            throw new CustomMessageException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email: " + e.getMessage());
        }
    }

    public void sendAttendanceAndHoursMailMessage(String to, int semesterId) {
        try {
            byte[] excelData = excelFileService.generateAttendanceAndTotalHoursExcelFileForSemester(semesterId);
            System.out.println(excelData.length);
            if (excelData == null || excelData.length == 0) {
                throw new CustomException(ErrorCode.EXCEL_FILE_GENERATION_ERROR);
            }
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(2);
            helper.setSubject(ATTENDANCE_AND_TOTAL_HOURS_REPORT);
            helper.setFrom(formEmail);
            helper.setTo(to);
            helper.setText("Hello,\n\n" +
                    "Please find the attached excel file for the attendance and total hours report for the semester.\n\n" +
                    "Thank you,\n" +
                    "EIMS Team");
            helper.addAttachment(ATTENDANCE_AND_TOTAL_HOURS_XLSX, new ByteArrayResource(excelData));
            emailSender.send(message);
            System.out.println("Email sent successfully");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new CustomMessageException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    private MimeMessage getMimeMessage() {
        return emailSender.createMimeMessage();
    }
}
