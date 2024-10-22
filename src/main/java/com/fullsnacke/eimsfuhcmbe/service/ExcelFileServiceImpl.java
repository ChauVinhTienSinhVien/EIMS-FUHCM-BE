////
////
////package com.fullsnacke.eimsfuhcmbe.service;
////
////import com.fullsnacke.eimsfuhcmbe.configuration.ConfigurationHolder;
////import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
////import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
////import com.fullsnacke.eimsfuhcmbe.entity.Semester;
////import com.fullsnacke.eimsfuhcmbe.entity.User;
////import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
////import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
////import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotRepository;
////import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAssignmentRepository;
////import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
////import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
////import lombok.AccessLevel;
////import lombok.RequiredArgsConstructor;
////import lombok.experimental.FieldDefaults;
////import org.apache.poi.ss.util.CellRangeAddress;
////import org.apache.poi.xssf.usermodel.XSSFCell;
////import org.apache.poi.xssf.usermodel.XSSFRow;
////import org.apache.poi.xssf.usermodel.XSSFSheet;
////import org.apache.poi.xssf.usermodel.XSSFWorkbook;
////import org.springframework.stereotype.Service;
////
////import java.io.ByteArrayOutputStream;
////import java.io.FileOutputStream;
////import java.io.IOException;
////import java.time.Duration;
////import java.time.ZonedDateTime;
////import java.time.format.DateTimeFormatter;
////import java.util.Date;
////import java.util.List;
////
////@Service
////@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
////@RequiredArgsConstructor
////public class ExcelFileServiceImpl implements ExcelFileService {
////
////    public static final String TOTAL_HOURS = "Total Hours";
////    UserRepository userRepository;
////    InvigilatorAssignmentRepository invigilatorAssignmentRepository;
////    ExamSlotRepository examSlotRepository;
////    SemesterRepository semesterRepository;
////    ConfigurationHolder configurationHolder;
////
////    public byte[] generateAttendanceAndTotalHoursExcelFileForSemester(int semesterId) {
////        Semester semester = semesterRepository.findById(semesterId)
////                .orElseThrow(() -> new CustomException(ErrorCode.SEMESTER_NOT_FOUND));
////
////        List<User> invigilators = userRepository.findAllByRole_NameAndIsDeleted("invigilator", false);
////        System.out.println("Invigilators: " + invigilators.size());
////
////        XSSFWorkbook workbook = new XSSFWorkbook();
////        try (workbook) {
////            XSSFSheet sheet = workbook.createSheet("Attendance and Total Hours");
////            createHeaderRow(sheet);
////            int rowNum = 1;
////
////            for (User invigilator : invigilators) {
////                List<InvigilatorAssignment> assignments = invigilatorAssignmentRepository
////                        .findByInvigilatorAndSemester(invigilator, semester);
////                if (assignments.isEmpty()) {
////                    System.out.printf("No assignments for invigilator: %s%n", invigilator.getFuId());
////                    continue;
////                }
////                double totalHours = 0;
////                int startRow = rowNum;
////
////                for (InvigilatorAssignment assignment : assignments) {
////                    ExamSlot examSlot = assignment.getInvigilatorRegistration().getExamSlot();
////                    System.out.println("ExamSlot: " + examSlot.getId());
////                    XSSFRow row = sheet.createRow(rowNum++);
////                    fillInvigilatorDate(row, invigilator, examSlot);
////
////                    double hours = calculateHours(examSlot);
////                    totalHours += hours;
////                    row.createCell(8).setCellValue(hours);
////                }
////                // Tổng kết cho mỗi giám thị
////                XSSFRow row = sheet.createRow(rowNum++);
////                double hourlyRate = configurationHolder.getHourlyRate();
////                fillSummaryRow(sheet, row, totalHours, hourlyRate);
////                sheet.createRow(rowNum++);
////            }
////            System.out.println("Excel file generated successfully. Size: " + getBytes(workbook).length + " bytes");
////            return getBytes(workbook);
////        } catch (IOException e) {
////            e.printStackTrace();
////            throw new CustomException(ErrorCode.EXCEL_FILE_GENERATION_ERROR);
////        }
////    }
////
////    private void createHeaderRow(XSSFSheet sheet) {
////        XSSFRow headerRow = sheet.createRow(0);
////        String[] columns = {"FUID", "First name", "Last name", "Department", "Date", "Start time", "End time", "Hours", "Total Hours", "Hourly Rate", "Total Remuneration"};
////        for (int i = 0; i < columns.length; i++) {
////            XSSFCell cell = headerRow.createCell(i);
////            cell.setCellValue(columns[i]);
////        }
////    }
////
////    private void fillInvigilatorDate(XSSFRow row, User invigilator, ExamSlot examSlot) {
////        row.createCell(0).setCellValue(invigilator.getFuId());
////        row.createCell(1).setCellValue(invigilator.getFirstName());
////        row.createCell(2).setCellValue(invigilator.getLastName());
////        row.createCell(3).setCellValue(invigilator.getDepartment());
////        row.createCell(4).setCellValue(invigilator.getEmail());
////        row.createCell(5).setCellValue(Date.from(examSlot.getStartAt().toInstant()));
////        row.createCell(6).setCellValue(formatTime(examSlot.getStartAt()));
////        row.createCell(7).setCellValue(formatTime(examSlot.getEndAt()));
////    }
////
////    private String formatTime(ZonedDateTime time) {
////        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
////    }
////
////    private double calculateHours(ExamSlot examSlot) {
////        return Duration.between(examSlot.getStartAt(), examSlot.getEndAt()).toHours();
////    }
////
////    private void fillSummaryRow(XSSFSheet sheet, XSSFRow row, double totalHours, double hourRate) {
////        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
////        row.createCell(0).setCellValue(TOTAL_HOURS);
////        row.createCell(8).setCellValue(totalHours);
////        row.createCell(9).setCellFormula("I" + (row.getRowNum() + 1));
////        row.createCell(10).setCellValue(hourRate);
////        row.createCell(11).setCellFormula("PRODUCT(J" + (row.getRowNum() + 1) + ",K" + (row.getRowNum() + 1) + ")");
////    }
////
////    private byte[] getBytes(XSSFWorkbook workbook) throws IOException {
////        ByteArrayOutputStream bos = new ByteArrayOutputStream();
////        workbook.write(bos);
////        return bos.toByteArray();
////    }
////}
//
//package com.fullsnacke.eimsfuhcmbe.service;
//
//import com.fullsnacke.eimsfuhcmbe.configuration.ConfigurationHolder;
//import com.fullsnacke.eimsfuhcmbe.entity.*;
//import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
//import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
//import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAssignmentRepository;
//import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAttendanceRepository;
//import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
//import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.xssf.usermodel.*;
//import org.springframework.stereotype.Service;
//
//import java.awt.Color;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.text.DecimalFormat;
//import java.time.Duration;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Service
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@RequiredArgsConstructor
//public class ExcelFileServiceImpl implements ExcelFileService {
//
//    public static final String TOTAL_HOURS = "Total Hours";
//    public static final int TOTAL_HOURS_COLUMN = 8;
//    UserRepository userRepository;
//    InvigilatorAssignmentRepository invigilatorAssignmentRepository;
//    SemesterRepository semesterRepository;
//    InvigilatorAttendanceRepository invigilatorAttendanceRepository;
//    ConfigurationHolder configurationHolder;
//
//    public byte[] generateAttendanceAndTotalHoursExcelFileForSemester(int semesterId) {
//        //Kiểm tra kì có tồn tại không
//        Semester semester = semesterRepository.findById(semesterId)
//                .orElseThrow(() -> new CustomException(ErrorCode.SEMESTER_NOT_FOUND));
//
//        // Lấy danh sách attendances đã hoàn thành theo semester. Nghĩa là checkin và checkout đều đã được thực hiện
//        List<InvigilatorAttendance> completedAttendances = invigilatorAttendanceRepository.findCompletedAttendancesBySemesterId(semesterId);
//
//        // Group các attendances theo invigilator. Key là fuId của invigilator
//        Map<String, List<InvigilatorAttendance>> attendanceMap = completedAttendances
//                .stream()
//                .collect(Collectors.groupingBy(attendance -> attendance.getInvigilatorAssignment().getInvigilatorRegistration().getInvigilator().getFuId()));
//
//        // Tạo workbook để viết dữ liệu
//        XSSFWorkbook workbook = new XSSFWorkbook();
//
//        try (workbook) {
//            // Tạo sheet mới với tên "Attendance and Total Hours"
//            XSSFSheet sheet = workbook.createSheet("Attendance and Total Hours");
//
//            createMainTitle(sheet);
//
//            int totalInvigilators = attendanceMap.keySet().size();
//            double totalRemuneration = 0;
//
//            createHeaderRow(sheet);
//
//            int rowNum = 9; // Bắt đầu từ hàng 8 để dành chỗ cho bảng tổng hợp
//            for (Map.Entry<String, List<InvigilatorAttendance>> entry : attendanceMap.entrySet()) {
//                User invigilator = entry.getValue().get(0).getInvigilatorAssignment().getInvigilatorRegistration().getInvigilator();
//                int startRow = rowNum;
//                double totalHours = 0;
//                for (InvigilatorAttendance attendance : entry.getValue()) {
//                    ExamSlot examSlot = attendance.getInvigilatorAssignment().getInvigilatorRegistration().getExamSlot();
//                    XSSFRow row = sheet.createRow(rowNum++);
//                    fillInvigilatorDate(row, invigilator, examSlot, startRow == rowNum - 1);
//                    double hours = calculateHours(examSlot);
//                    totalHours += hours;
//                    XSSFColor lightGray = new XSSFColor(new Color(242, 242, 242), null);
//                    XSSFCellStyle centerAlignStyle = getCenterAlignStyle(workbook);
//                    centerAlignStyle.setFillForegroundColor(lightGray);
//                    createCell(row, 7, hours, getCenterAlignStyle(workbook)).setCellStyle(centerAlignStyle);
//                }
//
//                mergeInvigilatorCells(sheet, startRow, rowNum - 1);
//
//                XSSFRow row = sheet.createRow(rowNum++);
//                double hourlyRate = configurationHolder.getHourlyRate();
//                fillSummaryRow(sheet, row, totalHours, hourlyRate);
//                totalRemuneration += totalHours * hourlyRate;
//                sheet.createRow(rowNum++);
//            }
//
//            createSummaryTable(sheet, totalInvigilators, totalRemuneration);
//
//            for (int i = 0; i < 11; i++) {
//                sheet.autoSizeColumn(i);
//            }
//
//            System.out.println("Excel file generated successfully. Size: " + getBytes(workbook).length + " bytes");
//            return getBytes(workbook);
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new CustomException(ErrorCode.EXCEL_FILE_GENERATION_ERROR);
//        }
//    }
//
//    private void createSummaryTable(XSSFSheet sheet, int totalInvigilators, double totalRemuneration) {
//        XSSFRow titleRow = sheet.createRow(1);
//        XSSFCell titleCell = titleRow.createCell(0);
//        titleCell.setCellValue("Invigilator Summary");
//
//        DecimalFormat decimalFormat = new DecimalFormat("#,##0");
//
//        XSSFCellStyle rightAlignStyle = sheet.getWorkbook().createCellStyle();
//        rightAlignStyle.cloneStyleFrom(getDefaultStyle(sheet.getWorkbook()));
//        rightAlignStyle.setAlignment(HorizontalAlignment.RIGHT);
//
//        XSSFCellStyle headerStyle = sheet.getWorkbook().createCellStyle();
//        headerStyle.setAlignment(HorizontalAlignment.CENTER);
//        setBorderForStyle(headerStyle);
//
//        XSSFFont headerFont = sheet.getWorkbook().createFont();
//        headerFont.setBold(true);
//        headerStyle.setFont(headerFont);
//
//        XSSFRow headerRow = sheet.createRow(2);
//        createCell(headerRow, 0, "Metric", headerStyle);
//        createCell(headerRow, 1, "Value", headerStyle);
//
//        getDefaultStyle(sheet.getWorkbook()).setAlignment(HorizontalAlignment.RIGHT);
//        XSSFRow invigilatorsRow = sheet.createRow(3);
//        createCell(invigilatorsRow, 0, "Total Invigilators", getDefaultStyle(sheet.getWorkbook()));
//        createCell(invigilatorsRow, 1, totalInvigilators, rightAlignStyle);
//
//        XSSFRow remunerationRow = sheet.createRow(4);
//        createCell(remunerationRow, 0, "Total Remuneration", getDefaultStyle(sheet.getWorkbook()));
//        createCell(remunerationRow, 1, decimalFormat.format(totalRemuneration), rightAlignStyle);
//
//        XSSFRow averageRow = sheet.createRow(5);
//        createCell(averageRow, 0, "Average Remuneration per Invigilator", getDefaultStyle(sheet.getWorkbook()));
//        createCell(averageRow, 1, decimalFormat.format(totalRemuneration / totalInvigilators), rightAlignStyle);
//
//        // Định dạng bảng
//        XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
//        style.setBorderBottom(BorderStyle.THIN);
//        style.setBorderTop(BorderStyle.THIN);
//        style.setBorderLeft(BorderStyle.THIN);
//        style.setBorderRight(BorderStyle.THIN);
//
//        for (int i = 1; i <= 5; i++) {
//            for (int j = 0; j < 2; j++) {
//                XSSFCell cell = sheet.getRow(i).getCell(j);
//                if (cell == null) {
//                    cell = sheet.getRow(i).createCell(j);
//                }
//                cell.setCellStyle(style);
//            }
//        }
//
//        // Tự động điều chỉnh độ rộng cột
//        sheet.autoSizeColumn(0);
//        sheet.autoSizeColumn(1);
//
//        // Apply title style
//        XSSFCellStyle titleStyle = sheet.getWorkbook().createCellStyle();
//        titleStyle.setAlignment(HorizontalAlignment.CENTER);
//        XSSFFont titleFont = sheet.getWorkbook().createFont();
//        titleFont.setBold(true);
//        titleFont.setFontHeightInPoints((short) 14);
//        titleStyle.setFont(titleFont);
//        titleCell.setCellStyle(titleStyle);
//
//        // Merge title cells
//        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
//    }
//
//    //Một row có nhiều cell và để lưu value thì ta cần tạo cell và set value cho cell đó
//    private void createMainTitle(XSSFSheet sheet) {
//        // Tạo dòng title và merge các ô để tạo title. Merge các ô từ 0 đến 10
//        XSSFRow titleRow = sheet.createRow(0);
//        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
//
//        // Chỉ ra vị trí của cell trong row và tạo cell
//        XSSFCell titleCell = titleRow.createCell(0);
//        // Tạo cell title và set giá trị cho cell
//        titleCell.setCellValue("ATTENDANCE AND TOTAL HOURS REPORT");
//
//        XSSFCellStyle titleStyle = sheet.getWorkbook().createCellStyle();
//        titleStyle.setAlignment(HorizontalAlignment.CENTER);
//        XSSFFont titleFont = sheet.getWorkbook().createFont();
//        titleFont.setBold(true);
//        titleFont.setFontHeightInPoints((short) 16);
//        titleStyle.setFont(titleFont);
//        titleCell.setCellStyle(titleStyle);
//    }
//
//    private void createHeaderRow(XSSFSheet sheet) {
//        XSSFRow headerRow = sheet.createRow(8);
//        String[] columns = {"FUID", "First name", "Last name", "Department", "Date", "Start time", "End time", "Hours", "Total Hours", "Hourly Rate", "Total Remuneration"};
//
//        XSSFCellStyle headerStyle = sheet.getWorkbook().createCellStyle();
//        headerStyle.setFillForegroundColor(new XSSFColor(new Color(219, 229, 241), null));
//        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        headerStyle.setAlignment(HorizontalAlignment.CENTER);
//        setBorderForStyle(headerStyle);
//
//        XSSFFont headerFont = sheet.getWorkbook().createFont();
//        headerFont.setBold(true);
//        headerFont.setColor(IndexedColors.DARK_BLUE.getIndex());
//        headerStyle.setFont(headerFont);
//
//        for (int i = 0; i < columns.length; i++) {
//            XSSFCell cell = headerRow.createCell(i);
//            cell.setCellValue(columns[i]);
//            cell.setCellStyle(headerStyle);
//        }
//    }
//
//    private void fillInvigilatorDate(XSSFRow row, User invigilator, ExamSlot examSlot, boolean isFirstRow) {
//        XSSFWorkbook workbook = row.getSheet().getWorkbook();
//        XSSFCellStyle dateCellStyle = workbook.createCellStyle();
//        dateCellStyle.setDataFormat(workbook.createDataFormat().getFormat("dd-MM-yyyy"));
//        setBorderForStyle(dateCellStyle);
//
//        XSSFCellStyle centerAlignStyle = getCenterAlignStyle(workbook);
//        XSSFCellStyle defaultStyle = getDefaultStyle(workbook);
//
//
//        XSSFColor lightGray = new XSSFColor(new Color(242, 242, 242), null);
//        defaultStyle.setFillForegroundColor(lightGray);
//        defaultStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        defaultStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        centerAlignStyle.setFillForegroundColor(lightGray);
//        centerAlignStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        centerAlignStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        dateCellStyle.setFillForegroundColor(lightGray);
//        dateCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        dateCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//
//
//        if (isFirstRow) {
//            createCell(row, 0, invigilator.getFuId(), centerAlignStyle);
//            createCell(row, 1, invigilator.getLastName(), defaultStyle);
//            createCell(row, 2, invigilator.getFirstName(), defaultStyle);
//            createCell(row, 3, invigilator.getDepartment(), defaultStyle);
//        } else {
//            for (int i = 0; i < 4; i++) {
//                createCell(row, i, "", defaultStyle);
//            }
//        }
//        createCell(row, 4, Date.from(examSlot.getStartAt().toInstant()), dateCellStyle);
//        createCell(row, 5, formatTime(examSlot.getStartAt()), centerAlignStyle);
//        createCell(row, 6, formatTime(examSlot.getEndAt()), centerAlignStyle);
//        createCell(row, 7, calculateHours(examSlot), centerAlignStyle);
//        createCell(row, 8, "", centerAlignStyle);
//        createCell(row, 9, "", centerAlignStyle);
//        createCell(row, 10, "", centerAlignStyle);
//    }
//
//    private XSSFCell createCell(XSSFRow row, int column, Object value, XSSFCellStyle style) {
//        XSSFCell cell = row.createCell(column);
//        if (value instanceof String) {
//            cell.setCellValue((String) value);
//        } else if (value instanceof Date) {
//            cell.setCellValue((Date) value);
//        } else if (value instanceof Number) {
//            cell.setCellValue(((Number) value).doubleValue());
//        }
//        cell.setCellStyle(style);
//        return cell;
//    }
//
//    private String formatTime(ZonedDateTime time) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
//        return time.format(formatter);
//    }
//
//    private double calculateHours(ExamSlot examSlot) {
//        return Duration.between(examSlot.getStartAt(), examSlot.getEndAt()).toHours();
//    }
//
//    private void fillSummaryRow(XSSFSheet sheet, XSSFRow row, double totalHours, double hourRate) {
//        XSSFCellStyle summaryStyle = sheet.getWorkbook().createCellStyle();
//        summaryStyle.setFillForegroundColor(new XSSFColor(new Color(189, 215, 238), null));
//        summaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        summaryStyle.setAlignment(HorizontalAlignment.CENTER);
//        setBorderForStyle(summaryStyle);
//
//        XSSFCellStyle totalStyle = summaryStyle;
//        totalStyle.setAlignment(HorizontalAlignment.RIGHT);
//
//        XSSFFont summaryFont = sheet.getWorkbook().createFont();
//        summaryFont.setBold(true);
//        summaryFont.setColor(IndexedColors.DARK_BLUE.getIndex());
//        summaryStyle.setFont(summaryFont);
//
//        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 6));
//        XSSFCell totalCell = row.createCell(0);
//        totalCell.setCellValue("Total:");
//        totalCell.setCellStyle(totalStyle);
//
//
//        DecimalFormat decimalFormat = new DecimalFormat("#,##0");
//        XSSFCellStyle centerAlignStyle = getCenterAlignStyle(row.getSheet().getWorkbook());
//        XSSFColor lightGray = new XSSFColor(new Color(242, 242, 242), null);
//        centerAlignStyle.setFillForegroundColor(lightGray);
//        centerAlignStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        centerAlignStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//
//        createCell(row, 7, totalHours, centerAlignStyle).setCellStyle(summaryStyle);
//        createCell(row, 8, totalHours, centerAlignStyle).setCellStyle(summaryStyle);
//        createCell(row, 9, decimalFormat.format(hourRate), summaryStyle);
//        createCell(row, 10, decimalFormat.format(totalHours * hourRate), summaryStyle);
//
//        for (int i = 1; i <= 6; i++) {
//            createCell(row, i, "", summaryStyle);
//        }
//    }
//
//    private byte[] getBytes(XSSFWorkbook workbook) throws IOException {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        workbook.write(bos);
//        return bos.toByteArray();
//    }
//
//    private void setBorderForStyle(XSSFCellStyle style) {
//        style.setBorderBottom(BorderStyle.THIN);
//        style.setBorderTop(BorderStyle.THIN);
//        style.setBorderLeft(BorderStyle.THIN);
//        style.setBorderRight(BorderStyle.THIN);
//        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
//        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
//    }
//
//    private XSSFCellStyle getCenterAlignStyle(XSSFWorkbook workbook) {
//        XSSFCellStyle style = workbook.createCellStyle();
//        style.setAlignment(HorizontalAlignment.CENTER);
//        setBorderForStyle(style);
//        return style;
//    }
//
//    private XSSFCellStyle getDefaultStyle(XSSFWorkbook workbook) {
//        XSSFCellStyle style = workbook.createCellStyle();
//        setBorderForStyle(style);
//        return style;
//    }
//
//    private void mergeInvigilatorCells(XSSFSheet sheet, int startRow, int endRow) {
//        if (startRow < endRow) {
//            for (int i = 0; i < 4; i++) {
//                CellRangeAddress range = new CellRangeAddress(startRow, endRow, i, i);
//                if (!isRangeAlreadyMerged(sheet, range)) {
//                    sheet.addMergedRegion(range);
//                }
//                CellRangeAddress range2 = new CellRangeAddress(startRow, endRow, i + TOTAL_HOURS_COLUMN, i + TOTAL_HOURS_COLUMN);
//                if (!isRangeAlreadyMerged(sheet, range2)) {
//                    sheet.addMergedRegion(range2);
//                }
//            }
//
//        }
//    }
//
//    private boolean isRangeAlreadyMerged(XSSFSheet sheet, CellRangeAddress rangeToCheck) {
//        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
//            CellRangeAddress range = sheet.getMergedRegion(i);
//            if (range.intersects(rangeToCheck)) {
//                return true;
//            }
//        }
//        return false;
//    }
//}
package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.configuration.ConfigurationHolder;
import com.fullsnacke.eimsfuhcmbe.entity.*;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAttendanceRepository;
import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExcelFileServiceImpl implements ExcelFileService {

    //Summary Table
    private final int METRIC_COLUMN_INDEX = 0;
    private final int VALUE_COLUMN_INDEX = 1;
    private final int UNIT_COLUMN_INDEX = 2;
    private final String SUMMARY_TABLE_TITLE = "Invigilator Summary";

    //Main Table
    //index of columns
    private int FU_ID_COLUMN_INDEX = 0;
    private int FIRST_NAME_COLUMN_INDEX = 1;
    private int LAST_NAME_COLUMN_INDEX = 2;
    private int DEPARTMENT_COLUMN_INDEX = 3;
    private int DATE_COLUMN_INDEX = 4;
    private int START_TIME_COLUMN_INDEX = 5;
    private int END_TIME_COLUMN_INDEX = 6;
    private int HOURS_COLUMN_INDEX = 7;
    private int TOTAL_HOURS_COLUMN_INDEX = 8;
    private int HOURLY_RATE_COLUMN_INDEX = 9;
    private int TOTAL_REMUNERATION_COLUMN_INDEX = 10;

    //field name
    private final String FU_ID = "FUID";
    private final String FIRST_NAME = "First name";
    private final String LAST_NAME = "Last name";
    private final String DEPARTMENT = "Department";
    private final String DATE = "Date";
    private final String START_TIME = "Start time";
    private final String END_TIME = "End time";
    private final String HOURS = "Hours";
    private final String TOTAL_HOURS = "Total Hours";
    private final String HOURLY_RATE = "Hourly Rate";
    private final String TOTAL_REMUNERATION = "Total Remuneration";


    //Common Configurations
    private final int FONT_SIZE = 10;
    private final String FONT_FAMILY = "Tahoma";

    //Main Title
    private final String SHEET_NAME = "Attendance and Total Hours";

    //Summary Table
    private final int MAIN_TITLE_ROW = 0;
    private final int SUMARY_TABLE_START_ROW = 2;
    private int mainTableStartAt = 9;
    private int sheetWidth;
    private final SemesterRepository semesterRepository;
    private final InvigilatorAttendanceRepository invigilatorAttendanceRepository;
    private final ConfigurationHolder configurationHolder;

    @Override
    public byte[] generateAttendanceAndTotalHoursExcelFileForSemester(int semesterId) {
        // Tìm semester theo ID, nếu không tìm thấy thì ném exception
        // Kiểm tra kì có tồn tại không
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEMESTER_NOT_FOUND));

        // Lấy danh sách các attendance đã hoàn thành (có cả check-in và check-out) cho semester này
        List<InvigilatorAttendance> completedAttendances = invigilatorAttendanceRepository.findCompletedAttendancesBySemesterId(semesterId);

        // Nhóm các attendance theo invigilator (sử dụng fuId làm key)
        Map<String, List<InvigilatorAttendance>> attendanceMap = completedAttendances.stream()
                .collect(Collectors.groupingBy(attendance -> attendance.getInvigilatorAssignment().getInvigilatorRegistration().getInvigilator().getFuId()));

        // Tạo workbook mới để viết dữ liệu và sheet "Attendance and Total Hours"
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(SHEET_NAME);

        // Tạo tiêu đề chính và hàng tiêu đề
        createMainTitle(sheet, semester);
        createHeaderRow(sheet);

        int totalInvigilators = attendanceMap.keySet().size();
        double totalRemuneration = 0;

        // Bắt đầu từ hàng 9 để dành chỗ cho bảng tổng hợp
        int rowNum = mainTableStartAt;

        // Lặp qua từng invigilator và các attendance của họ
        for (Map.Entry<String, List<InvigilatorAttendance>> entry : attendanceMap.entrySet()) {
            // Lấy thông tin của invigilator
            // Vì tất cả các attendance trong entry này đều thuộc về cùng một invigilator nên lấy thông tin từ bất kỳ attendance nào cũng được
            User invigilator = entry.getValue().get(0).getInvigilatorAssignment().getInvigilatorRegistration().getInvigilator();

            // Lưu lại dòng bắt đầu của invigilator này
            // Dùng để merge các ô của invigilator này sau khi đã tạo xong
            int startRow = rowNum;

            double totalHours = 0;

            // Lặp qua từng attendance của invigilator
            for (InvigilatorAttendance attendance : entry.getValue()) {
                ExamSlot examSlot = attendance.getInvigilatorAssignment().getInvigilatorRegistration().getExamSlot();
                XSSFRow row = sheet.createRow(rowNum++);
                fillInvigilatorData(row, invigilator, examSlot, startRow == rowNum - 1);

//                totalHours += hours;
                // Tạo ô cho số giờ làm việc
                // Vì hours nằm ở cột 7 nên setCellValue cho ô ở cột 7
//                row.createCell(HOURS_COLUMN_INDEX).setCellFormula("SUM(H" + startRow + ":H" + (rowNum - 1) + ")");
            }
            mergeInvigilatorCells(sheet, startRow, rowNum - 1);
            mergeInvigilatorCells(sheet, startRow, rowNum - 1);
            // Tạo hàng tổng kết cho mỗi invigilator
            double hourlyRate = configurationHolder.getHourlyRate();
            fillSummaryRow(sheet, rowNum++, startRow, hourlyRate);
            totalRemuneration += totalHours * hourlyRate;
            sheet.createRow(rowNum++);
        }

        createSummaryTable(sheet, totalInvigilators, totalRemuneration);

        formatWorkbook(workbook);

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new CustomException(ErrorCode.EXCEL_FILE_GENERATION_ERROR);
        }
    }

    private void createMainTitle(XSSFSheet sheet, Semester semester) {
        XSSFRow titleRow = sheet.createRow(MAIN_TITLE_ROW);
        XSSFCell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("ATTENDANCE AND TOTAL HOURS REPORT\nFPTUHCM - " + semester.getName().toUpperCase());
    }

    private void createHeaderRow(XSSFSheet sheet) {
        XSSFRow headerRow = sheet.createRow(8);
        String[] columns = {FU_ID, FIRST_NAME, LAST_NAME, DEPARTMENT, DATE, START_TIME, END_TIME, HOURS, TOTAL_HOURS, HOURLY_RATE, TOTAL_REMUNERATION};
        int headerLength = columns.length;
        sheetWidth = columns.length;
        for (int i = 0; i < columns.length; i++) {
            headerRow.createCell(i).setCellValue(columns[i]);
        }
    }

    private void fillInvigilatorData(XSSFRow row, User invigilator, ExamSlot examSlot, boolean isFirstRow) {
        XSSFWorkbook workbook = row.getSheet().getWorkbook();
        XSSFCellStyle style = getDefaultStyle(workbook);
        style.cloneStyleFrom(borderStyle(workbook));
        if (isFirstRow) {
            createAMainTableRow(row, FU_ID_COLUMN_INDEX, invigilator.getFuId());
            createAMainTableRow(row, FIRST_NAME_COLUMN_INDEX, invigilator.getFirstName());
            createAMainTableRow(row, LAST_NAME_COLUMN_INDEX, invigilator.getLastName());
            createAMainTableRow(row, DEPARTMENT_COLUMN_INDEX, invigilator.getDepartment());
        }
        createAMainTableRow(row, DATE_COLUMN_INDEX, examSlot.getStartAt().toLocalDate());
        XSSFCellStyle timeCellStyle = workbook.createCellStyle();
        timeCellStyle.cloneStyleFrom(style);
        timeCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy"));
        row.getCell(DATE_COLUMN_INDEX).setCellStyle(timeCellStyle);

        createAMainTableRow(row, START_TIME_COLUMN_INDEX, formatTime(examSlot.getStartAt()));
        createAMainTableRow(row, END_TIME_COLUMN_INDEX, formatTime(examSlot.getEndAt()));
        createAMainTableRowFormula(row, HOURS_COLUMN_INDEX, "HOUR(G" + (row.getRowNum() + 1) + "-F" + (row.getRowNum() + 1) + ") + MINUTE(G" + (row.getRowNum() + 1) + "-F" + (row.getRowNum() + 1) + ")/60");
        createAMainTableRow(row, TOTAL_HOURS_COLUMN_INDEX, "");
        createAMainTableRow(row, HOURLY_RATE_COLUMN_INDEX, "");
        createAMainTableRow(row, TOTAL_REMUNERATION_COLUMN_INDEX, "");

    }

    private void createAMainTableRowFormula(XSSFRow row, int cellNum, String value) {
        XSSFCell cell = row.createCell(cellNum);
        cell.setCellFormula(value);
        XSSFCellStyle style = getDefaultStyle(row.getSheet().getWorkbook());
        style.cloneStyleFrom(borderStyle(row.getSheet().getWorkbook()));
        cell.setCellStyle(style);
    }

    private void createAMainTableRow(XSSFRow row, int cellNum, String value) {
        XSSFCell cell = row.createCell(cellNum);
        cell.setCellValue(value);
        XSSFCellStyle style = getDefaultStyle(row.getSheet().getWorkbook());
        style.cloneStyleFrom(borderStyle(row.getSheet().getWorkbook()));
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        cell.setCellStyle(style);
    }

    private void createAMainTableRow(XSSFRow row, int cellNum, double value) {
        XSSFCell cell = row.createCell(cellNum);
        cell.setCellValue(value);
        XSSFCellStyle style = getDefaultStyle(row.getSheet().getWorkbook());
        style.cloneStyleFrom(borderStyle(row.getSheet().getWorkbook()));
        cell.setCellStyle(style);
    }

    private void createAMainTableRow(XSSFRow row, int cellNum, LocalDate value) {
        XSSFCell cell = row.createCell(cellNum);
        cell.setCellValue(value);
        XSSFCellStyle style = getDefaultStyle(row.getSheet().getWorkbook());
        style.cloneStyleFrom(borderStyle(row.getSheet().getWorkbook()));
        cell.setCellStyle(style);
    }


    private void mergeInvigilatorCells(XSSFSheet sheet, int startRow, int endRow) {
        if (startRow < endRow) {
            for (int i = FU_ID_COLUMN_INDEX; i <= DEPARTMENT_COLUMN_INDEX; i++) {
                CellRangeAddress range = new CellRangeAddress(startRow, endRow, i, i);
                if (!isRangeAlreadyMerged(sheet, range)) {
                    sheet.addMergedRegion(range);
                }
            }
            for (int i = TOTAL_HOURS_COLUMN_INDEX; i <= TOTAL_REMUNERATION_COLUMN_INDEX; i++) {
                CellRangeAddress range = new CellRangeAddress(startRow, endRow, i, i);
                if (!isRangeAlreadyMerged(sheet, range)) {
                    sheet.addMergedRegion(range);
                }
            }
        }
    }

    private boolean isRangeAlreadyMerged(XSSFSheet sheet, CellRangeAddress rangeToCheck) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            if (range.intersects(rangeToCheck)) {
                return true;
            }
        }
        return false;
    }

    private String formatTime(ZonedDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private void fillSummaryRow(XSSFSheet sheet, int rowNum, int startRow, double hourRate) {
        XSSFRow row = sheet.createRow(rowNum);

        createAMainTableRow(row, FU_ID_COLUMN_INDEX, "Total:");
        XSSFCellStyle totalStyle = getRightAlignStyle(sheet.getWorkbook());
        totalStyle.getFont().setBold(true);
        createAMainTableRowFormula(row, HOURS_COLUMN_INDEX, "SUM(H" + (startRow + 1) + ":H" + row.getRowNum() + ")");
        createAMainTableRowFormula(row, TOTAL_HOURS_COLUMN_INDEX, "H" + (row.getRowNum() + 1));
        createAMainTableRow(row, HOURLY_RATE_COLUMN_INDEX, hourRate);
        createAMainTableRowFormula(row, TOTAL_REMUNERATION_COLUMN_INDEX, "PRODUCT(I" + (row.getRowNum() + 1) + ",J" + (row.getRowNum() + 1) + ")");
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, FU_ID_COLUMN_INDEX, END_TIME_COLUMN_INDEX));
    }

    private void createSummaryTable(XSSFSheet sheet, int totalInvigilators, double totalRemuneration) {

        int rowNum = SUMARY_TABLE_START_ROW;

        XSSFRow titleRow = sheet.createRow(rowNum);
        titleRow.createCell(0).setCellValue(SUMMARY_TABLE_TITLE.toUpperCase());

        addNewRowToSummaryTable(sheet, ++rowNum, "Metric", "Value", "Unit");
        int cellNum = sheet.getRow(rowNum).getLastCellNum();

        addNewRowToSummaryTable(sheet, ++rowNum, "Total Invigilators", totalInvigilators, "people");

        addNewRowToSummaryTable(sheet, ++rowNum, "Total Remuneration", totalRemuneration, "VND");

        addNewRowToSummaryTable(sheet, ++rowNum, "Average Remuneration", (totalInvigilators <= 0) ? 0 : totalRemuneration / totalInvigilators, "VND");

        sheet.addMergedRegion(new CellRangeAddress(SUMARY_TABLE_START_ROW, SUMARY_TABLE_START_ROW, 0, cellNum - 1));
    }

    private void addNewRowToSummaryTable(XSSFSheet sheet, int rowNum, String metric, double value, String unit) {
        XSSFRow row = sheet.createRow(rowNum);
        row.createCell(METRIC_COLUMN_INDEX).setCellValue(metric);
        row.createCell(VALUE_COLUMN_INDEX).setCellValue(value);
        row.createCell(UNIT_COLUMN_INDEX).setCellValue(unit);
    }

    private void addNewRowToSummaryTable(XSSFSheet sheet, int rowNum, String metric, String value, String unit) {
        XSSFRow row = sheet.createRow(rowNum);
        row.createCell(METRIC_COLUMN_INDEX).setCellValue(metric);
        row.createCell(VALUE_COLUMN_INDEX).setCellValue(value);
        row.createCell(UNIT_COLUMN_INDEX).setCellValue(unit);
    }

    private void addNewRowToSummaryTable(XSSFSheet sheet, int rowNum, String metric, int value, String unit) {
        XSSFRow row = sheet.createRow(rowNum);
        row.createCell(METRIC_COLUMN_INDEX).setCellValue(metric);
        row.createCell(VALUE_COLUMN_INDEX).setCellValue(value);
        row.createCell(UNIT_COLUMN_INDEX).setCellValue(unit);
    }

    /*****************************************************
     *  Complete input data                               *
     *  Start formatting the sheet to make it look nice   *
     *****************************************************/


    private void formatWorkbook(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.getSheetAt(0);

        XSSFCellStyle titleStyle = formatTitle(workbook);
        // Áp dụng style cho cell được chọn
        // Vì Title nằm ở hàng 0 và cột 0 nên setCellValue cho ô ở hàng 0 và cột 0
        sheet.getRow(0).getCell(0).setCellStyle(titleStyle);
        // Merge title cells
        // Merge các ô từ hàng 0 đến hàng 0 và từ cột 0 đến cột 11
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, sheetWidth));

        // Format header row
        XSSFCellStyle headerStyle = formatHeader(workbook);
        XSSFRow headerRow = sheet.getRow(8);
        for (Cell cell : headerRow) {
            cell.setCellStyle(headerStyle);
        }

        // Format data rows
//        XSSFCellStyle dataCellStyle = getDefaultStyle(workbook);
//        dataCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        dataCellStyle.cloneStyleFrom(borderStyle(workbook));
//        for (int i = 9; i < sheet.getLastRowNum(); i++) {
//            XSSFRow row = sheet.getRow(i);
//            if (row != null) {
//                for (Cell cell : row) {
//                    cell.setCellStyle(dataCellStyle);
//                }
//            }
//        }

        // Format summary rows
        XSSFCellStyle summaryStyle = workbook.createCellStyle();
        summaryStyle.setFillForegroundColor(new XSSFColor(new Color(189, 215, 238), null));
        summaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        summaryStyle.setAlignment(HorizontalAlignment.CENTER);
        XSSFFont summaryFont = workbook.createFont();
        summaryFont.setBold(true);
        summaryFont.setColor(IndexedColors.DARK_BLUE.getIndex());
        summaryStyle.setFont(summaryFont);
        for (int i = 1; i <= 4; i++) {
            XSSFRow row = sheet.getRow(i);
            if (row != null) {
                for (Cell cell : row) {
                    cell.setCellStyle(summaryStyle);
                }
            }
        }

        // Auto-size columns
        for (int i = 0; i < 11; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private XSSFCellStyle formatTitle(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.getSheetAt(0);
        // Format font
        XSSFFont titleFont = getFont(workbook);
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 20);
        titleFont.setColor(IndexedColors.RED1.getIndex());

        // Format style
        XSSFCellStyle style = getCenterAlignStyle(workbook);
        style.setFont(titleFont);
        // null đơn giản có nghĩa là "không sử dụng bảng màu cụ thể trong IndexedColors map - tập hợp các màu mặc định được định nghĩa sẵn,"
        // mà thay vào đó bạn sử dụng mã màu RGB trực tiếp.
        style.setFillForegroundColor(new XSSFColor(new Color(194, 239, 236), null));
        style.setWrapText(true);// Thiết lập ô để chứa xuống dòng

        return style;
    }

    private XSSFCellStyle formatHeader(XSSFWorkbook workbook) {

        XSSFCellStyle headerStyle = borderStyle(workbook);

        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.getFont().setBold(true);

        return headerStyle;
    }

    private XSSFCellStyle getCenterAlignStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(getDefaultStyle(workbook));
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private XSSFCellStyle getDefaultStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFillPattern(FillPatternType.NO_FILL);
        style.setFont(getFont(workbook));
        return style;
    }

    private XSSFCellStyle getRightAlignStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(getDefaultStyle(workbook));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private XSSFCellStyle getLeftAlignStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(getDefaultStyle(workbook));
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }

    private XSSFCellStyle borderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = getDefaultStyle(workbook);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private XSSFFont getFont(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setFontName(FONT_FAMILY);
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setFontHeightInPoints((short) FONT_SIZE);
        return font;
    }

}