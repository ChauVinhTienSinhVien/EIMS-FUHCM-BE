package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.configuration.ConfigurationHolder;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotRepository;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAssignmentRepository;
import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ExcelFileServiceImpl implements ExcelFileService{

    public static final String TOTAL_HOURS = "Total Hours";
    UserRepository userRepository;
    InvigilatorAssignmentRepository invigilatorAssignmentRepository;
    ExamSlotRepository examSlotRepository;
    SemesterRepository semesterRepository;
    ConfigurationHolder configurationHolder;

    public byte[] generateAttendanceAndTotalHoursExcelFileForSemester(int semesterId){
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEMESTER_NOT_FOUND));

        List<User> invigilators = userRepository.findAllByRole_NameAndIsDeleted("invigilator", false);
        System.out.println("Invigilators: " + invigilators.size());

        XSSFWorkbook workbook = new XSSFWorkbook();
        try (workbook) {
            XSSFSheet sheet = workbook.createSheet("Attendance and Total Hours");
            createHeaderRow(sheet);
            int rowNum = 1;

            for (User invigilator : invigilators) {
                List<InvigilatorAssignment> assignments = invigilatorAssignmentRepository
                        .findByInvigilatorAndSemester(invigilator, semester);
                if(assignments.isEmpty()) {
                    System.out.printf("No assignments for invigilator: %s%n", invigilator.getFuId());
                    continue;
                }
                double totalHours = 0;
                int startRow = rowNum;

                for (InvigilatorAssignment assignment : assignments) {
                    ExamSlot examSlot = assignment.getInvigilatorRegistration().getExamSlot();
                    System.out.println("ExamSlot: " + examSlot.getId());
                    XSSFRow row = sheet.createRow(rowNum++);
                    fillInvigilatorDate(row, invigilator, examSlot);

                    double hours = calculateHours(examSlot);
                    totalHours += hours;
                    row.createCell(8).setCellValue(hours);
                }
                // Tổng kết cho mỗi giám thị
                XSSFRow row = sheet.createRow(rowNum++);
                double hourlyRate = configurationHolder.getHourlyRate();
                fillSummaryRow(sheet, row, totalHours, hourlyRate);
                sheet.createRow(rowNum++);
            }
            System.out.println("Excel file generated successfully. Size: " + getBytes(workbook).length + " bytes");
            return getBytes(workbook);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.EXCEL_FILE_GENERATION_ERROR);
        }
    }

    private void createHeaderRow(XSSFSheet sheet) {
        XSSFRow headerRow = sheet.createRow(0);
        String[] columns = {"FuId", "Firstname", "Lastname", "Department", "Email", "Date", "Start Time", "End Time", "Total Hours", "Total Hours In Semester", "Hourly Rate", "Total Amount"};
        for (int i = 0; i < columns.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }
    }

    private void fillInvigilatorDate(XSSFRow row, User invigilator, ExamSlot examSlot) {
        row.createCell(0).setCellValue(invigilator.getFuId());
        System.out.println(row.getCell(0).getStringCellValue());
        row.createCell(1).setCellValue(invigilator.getFirstName());
        System.out.println(row.getCell(1).getStringCellValue());
        row.createCell(2).setCellValue(invigilator.getLastName());
        System.out.println(row.getCell(2).getStringCellValue());
        row.createCell(3).setCellValue(invigilator.getDepartment());
        System.out.println(row.getCell(3).getStringCellValue());
        row.createCell(4).setCellValue(invigilator.getEmail());
        System.out.println(row.getCell(4).getStringCellValue());
        row.createCell(5).setCellValue(Date.from(examSlot.getStartAt().toInstant()));
        row.createCell(6).setCellValue(formatTime(examSlot.getStartAt()));
        row.createCell(7).setCellValue(formatTime(examSlot.getEndAt()));
    }

    private String formatTime(ZonedDateTime time) {
        return time.getHour() + ":" + time.getMinute();
    }

    private double calculateHours(ExamSlot examSlot) {
//        return examSlot.getStartAt().until(examSlot.getEndAt(), MINUTES) / 60.0;
        return Duration.between(examSlot.getStartAt(), examSlot.getEndAt()).toHours();
    }

    private void fillSummaryRow(XSSFSheet sheet, XSSFRow row, double totalHours, double hourRate) {
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
        row.createCell(0).setCellValue(TOTAL_HOURS);
        row.createCell(8).setCellValue(totalHours);
        row.createCell(9).setCellFormula("I" + (row.getRowNum() + 1));
        row.createCell(10).setCellValue(hourRate);
        row.createCell(11).setCellFormula("PRODUCT(J" + (row.getRowNum() + 1) + ",K" + (row.getRowNum() + 1) + ")");
    }

    private byte[] getBytes(XSSFWorkbook workbook) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        return bos.toByteArray();
    }
}
