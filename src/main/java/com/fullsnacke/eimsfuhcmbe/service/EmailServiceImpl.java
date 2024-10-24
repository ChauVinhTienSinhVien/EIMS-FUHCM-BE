package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.assignment.CustomMessageException;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAssignmentRepository;
import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final SemesterRepository semesterRepository;
    private final UserRepository userRepository;
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String ATTENDANCE_AND_TOTAL_AMOUNT_REPORT = "Attendance and Total Amount Report";
    public static final String ATTENDANCE_AND_TOTAL_HOURS_XLSX = "AttendanceAndTotalHours.xlsx";
    public static final String EMAIL_TEMPLATE = "AttendenceAndTotalAmountReportTemplate";
    private final InvigilatorAssignmentRepository invigilatorAssignmentRepository;

    @Value("${spring.mail.verify.host}")
    @NonFinal
    String host;
    @Value("${spring.mail.username}")
    @NonFinal
    String fromEmail;
    @NonFinal
    String emailSupport;

    JavaMailSender emailSender;
    ExcelFileService excelFileService;
    SpringTemplateEngine templateEngine;
    @NonFinal
    int count = 0;
    public List<String> sendAttendanceAndHoursMailMessageInListEmails(int semesterId, List<String> toEmails) {
        try {
            List<String> failedEmails = new ArrayList<>();
            for(String email: toEmails){
                if(sendAttendanceAndHoursMailMessageForInvigilator(semesterId, email) != null){
                    failedEmails.add(email);
                }
            }
            return failedEmails;
        } catch (Exception e) {
            throw new CustomMessageException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private String sendAttendanceAndHoursMailMessageForInvigilator(int semesterId, String toEmail) {
        emailSupport = fromEmail;
        try {
            byte[] excelData = excelFileService.generateAttendanceAndTotalHoursExcelFileForSemester(semesterId, toEmail);
            System.out.println("#" + ++count + "length: " + excelData.length);
            if (excelData == null || excelData.length == 0) {
                return toEmail;
            }
            User invigilator = userRepository.findUserByEmail(toEmail);

            String fullName = invigilator.getLastName() + " " + invigilator.getFirstName();
            System.out.println("fullname: " + fullName);

            Semester semester = semesterRepository.findSemesterById(semesterId);
            System.out.println(semester.getName());

            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("semesterName", semester.getName());
            context.setVariable("emailSupport", emailSupport);

            System.out.println(context.getVariableNames());
            System.out.println(context.getVariable("emailSupport"));
            String text = templateEngine.process(EMAIL_TEMPLATE, context);

            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(2);
            helper.setSubject(ATTENDANCE_AND_TOTAL_AMOUNT_REPORT);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setText(text, true);

            helper.addAttachment(ATTENDANCE_AND_TOTAL_HOURS_XLSX, new ByteArrayResource(excelData));

            emailSender.send(message);
            System.out.println("Email sent successfully");
            return null;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new CustomMessageException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private MimeMessage getMimeMessage() {
        return emailSender.createMimeMessage();
    }



}
