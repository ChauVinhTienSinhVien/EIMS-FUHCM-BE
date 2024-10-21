//
//
//package com.fullsnacke.eimsfuhcmbe.service;
//
//import com.fullsnacke.eimsfuhcmbe.configuration.ConfigurationHolder;
//import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
//import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
//import com.fullsnacke.eimsfuhcmbe.entity.Semester;
//import com.fullsnacke.eimsfuhcmbe.entity.User;
//import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
//import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
//import com.fullsnacke.eimsfuhcmbe.repository.ExamSlotRepository;
//import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAssignmentRepository;
//import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
//import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.xssf.usermodel.XSSFCell;
//import org.apache.poi.xssf.usermodel.XSSFRow;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.stereotype.Service;
//
//import java.io.ByteArrayOutputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.time.Duration;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Date;
//import java.util.List;
//
//@Service
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@RequiredArgsConstructor
//public class ExcelFileServiceImpl implements ExcelFileService {
//
//    public static final String TOTAL_HOURS = "Total Hours";
//    UserRepository userRepository;
//    InvigilatorAssignmentRepository invigilatorAssignmentRepository;
//    ExamSlotRepository examSlotRepository;
//    SemesterRepository semesterRepository;
//    ConfigurationHolder configurationHolder;
//
//    public byte[] generateAttendanceAndTotalHoursExcelFileForSemester(int semesterId) {
//        Semester semester = semesterRepository.findById(semesterId)
//                .orElseThrow(() -> new CustomException(ErrorCode.SEMESTER_NOT_FOUND));
//
//        List<User> invigilators = userRepository.findAllByRole_NameAndIsDeleted("invigilator", false);
//        System.out.println("Invigilators: " + invigilators.size());
//
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        try (workbook) {
//            XSSFSheet sheet = workbook.createSheet("Attendance and Total Hours");
//            createHeaderRow(sheet);
//            int rowNum = 1;
//
//            for (User invigilator : invigilators) {
//                List<InvigilatorAssignment> assignments = invigilatorAssignmentRepository
//                        .findByInvigilatorAndSemester(invigilator, semester);
//                if (assignments.isEmpty()) {
//                    System.out.printf("No assignments for invigilator: %s%n", invigilator.getFuId());
//                    continue;
//                }
//                double totalHours = 0;
//                int startRow = rowNum;
//
//                for (InvigilatorAssignment assignment : assignments) {
//                    ExamSlot examSlot = assignment.getInvigilatorRegistration().getExamSlot();
//                    System.out.println("ExamSlot: " + examSlot.getId());
//                    XSSFRow row = sheet.createRow(rowNum++);
//                    fillInvigilatorDate(row, invigilator, examSlot);
//
//                    double hours = calculateHours(examSlot);
//                    totalHours += hours;
//                    row.createCell(8).setCellValue(hours);
//                }
//                // Tổng kết cho mỗi giám thị
//                XSSFRow row = sheet.createRow(rowNum++);
//                double hourlyRate = configurationHolder.getHourlyRate();
//                fillSummaryRow(sheet, row, totalHours, hourlyRate);
//                sheet.createRow(rowNum++);
//            }
//            System.out.println("Excel file generated successfully. Size: " + getBytes(workbook).length + " bytes");
//            return getBytes(workbook);
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new CustomException(ErrorCode.EXCEL_FILE_GENERATION_ERROR);
//        }
//    }
//
//    private void createHeaderRow(XSSFSheet sheet) {
//        XSSFRow headerRow = sheet.createRow(0);
//        String[] columns = {"FUID", "First name", "Last name", "Department", "Date", "Start time", "End time", "Hours", "Total Hours", "Hourly Rate", "Total Remuneration"};
//        for (int i = 0; i < columns.length; i++) {
//            XSSFCell cell = headerRow.createCell(i);
//            cell.setCellValue(columns[i]);
//        }
//    }
//
//    private void fillInvigilatorDate(XSSFRow row, User invigilator, ExamSlot examSlot) {
//        row.createCell(0).setCellValue(invigilator.getFuId());
//        row.createCell(1).setCellValue(invigilator.getFirstName());
//        row.createCell(2).setCellValue(invigilator.getLastName());
//        row.createCell(3).setCellValue(invigilator.getDepartment());
//        row.createCell(4).setCellValue(invigilator.getEmail());
//        row.createCell(5).setCellValue(Date.from(examSlot.getStartAt().toInstant()));
//        row.createCell(6).setCellValue(formatTime(examSlot.getStartAt()));
//        row.createCell(7).setCellValue(formatTime(examSlot.getEndAt()));
//    }
//
//    private String formatTime(ZonedDateTime time) {
//        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
//    }
//
//    private double calculateHours(ExamSlot examSlot) {
//        return Duration.between(examSlot.getStartAt(), examSlot.getEndAt()).toHours();
//    }
//
//    private void fillSummaryRow(XSSFSheet sheet, XSSFRow row, double totalHours, double hourRate) {
//        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
//        row.createCell(0).setCellValue(TOTAL_HOURS);
//        row.createCell(8).setCellValue(totalHours);
//        row.createCell(9).setCellFormula("I" + (row.getRowNum() + 1));
//        row.createCell(10).setCellValue(hourRate);
//        row.createCell(11).setCellFormula("PRODUCT(J" + (row.getRowNum() + 1) + ",K" + (row.getRowNum() + 1) + ")");
//    }
//
//    private byte[] getBytes(XSSFWorkbook workbook) throws IOException {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        workbook.write(bos);
//        return bos.toByteArray();
//    }
//}

package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.configuration.ConfigurationHolder;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAssignment;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAssignmentRepository;
import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ExcelFileServiceImpl implements ExcelFileService {

    public static final String TOTAL_HOURS = "Total Hours";
    public static final int TOTAL_HOURS_COLUMN = 8;
    UserRepository userRepository;
    InvigilatorAssignmentRepository invigilatorAssignmentRepository;
    SemesterRepository semesterRepository;
    ConfigurationHolder configurationHolder;

    public byte[] generateAttendanceAndTotalHoursExcelFileForSemester(int semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEMESTER_NOT_FOUND));

        List<User> invigilators = userRepository.findAllByRole_NameAndIsDeleted("invigilator", false);

        XSSFWorkbook workbook = new XSSFWorkbook();
        try (workbook) {
            XSSFSheet sheet = workbook.createSheet("Attendance and Total Hours");
            createMainTitle(sheet);

            int totalInvigilators = invigilators.size();
            double totalRemuneration = 0;

            createHeaderRow(sheet);

            int rowNum = 9; // Bắt đầu từ hàng 8 để dành chỗ cho bảng tổng hợp
            for (User invigilator : invigilators) {
                List<InvigilatorAssignment> assignments = invigilatorAssignmentRepository
                        .findByInvigilatorAndSemester(invigilator, semester);
                if (assignments.isEmpty()) {
                    System.out.printf("No assignments for invigilator: %s%n", invigilator.getFuId());
                    continue;
                }

                int startRow = rowNum;
                double totalHours = 0;
                for (InvigilatorAssignment assignment : assignments) {
                    ExamSlot examSlot = assignment.getInvigilatorRegistration().getExamSlot();
                    XSSFRow row = sheet.createRow(rowNum++);
                    fillInvigilatorDate(row, invigilator, examSlot, startRow == rowNum - 1);
                    double hours = calculateHours(examSlot);
                    totalHours += hours;
                    XSSFColor lightGray = new XSSFColor(new Color(242, 242, 242), null);
                    XSSFCellStyle centerAlignStyle = getCenterAlignStyle(workbook);
                    centerAlignStyle.setFillForegroundColor(lightGray);
                    createCell(row, 7, hours, getCenterAlignStyle(workbook)).setCellStyle(centerAlignStyle);
                }

                mergeInvigilatorCells(sheet, startRow, rowNum - 1);

                XSSFRow row = sheet.createRow(rowNum++);
                double hourlyRate = configurationHolder.getHourlyRate();
                fillSummaryRow(sheet, row, totalHours, hourlyRate);
                totalRemuneration += totalHours * hourlyRate;
                sheet.createRow(rowNum++);
            }

            createSummaryTable(sheet, totalInvigilators, totalRemuneration);

            for (int i = 0; i < 11; i++) {
                sheet.autoSizeColumn(i);
            }

            System.out.println("Excel file generated successfully. Size: " + getBytes(workbook).length + " bytes");
            return getBytes(workbook);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.EXCEL_FILE_GENERATION_ERROR);
        }
    }

    private void createSummaryTable(XSSFSheet sheet, int totalInvigilators, double totalRemuneration) {
        XSSFRow titleRow = sheet.createRow(1);
        XSSFCell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Invigilator Summary");

        DecimalFormat decimalFormat = new DecimalFormat("#,##0");

        XSSFCellStyle rightAlignStyle = sheet.getWorkbook().createCellStyle();
        rightAlignStyle.cloneStyleFrom(getDefaultStyle(sheet.getWorkbook()));
        rightAlignStyle.setAlignment(HorizontalAlignment.RIGHT);

        XSSFCellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        setBorderForStyle(headerStyle);

        XSSFFont headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        XSSFRow headerRow = sheet.createRow(2);
        createCell(headerRow, 0, "Metric", headerStyle);
        createCell(headerRow, 1, "Value", headerStyle);

        getDefaultStyle(sheet.getWorkbook()).setAlignment(HorizontalAlignment.RIGHT);
        XSSFRow invigilatorsRow = sheet.createRow(3);
        createCell(invigilatorsRow, 0, "Total Invigilators", getDefaultStyle(sheet.getWorkbook()));
        createCell(invigilatorsRow, 1, totalInvigilators, rightAlignStyle);

        XSSFRow remunerationRow = sheet.createRow(4);
        createCell(remunerationRow, 0, "Total Remuneration", getDefaultStyle(sheet.getWorkbook()));
        createCell(remunerationRow, 1, decimalFormat.format(totalRemuneration), rightAlignStyle);

        XSSFRow averageRow = sheet.createRow(5);
        createCell(averageRow, 0, "Average Remuneration per Invigilator", getDefaultStyle(sheet.getWorkbook()));
        createCell(averageRow, 1, decimalFormat.format(totalRemuneration / totalInvigilators), rightAlignStyle);

        // Định dạng bảng
        XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        for (int i = 1; i <= 5; i++) {
            for (int j = 0; j < 2; j++) {
                XSSFCell cell = sheet.getRow(i).getCell(j);
                if (cell == null) {
                    cell = sheet.getRow(i).createCell(j);
                }
                cell.setCellStyle(style);
            }
        }

        // Tự động điều chỉnh độ rộng cột
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        // Apply title style
        XSSFCellStyle titleStyle = sheet.getWorkbook().createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        XSSFFont titleFont = sheet.getWorkbook().createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);

        // Merge title cells
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
    }

//    private void createSummaryTable(XSSFSheet sheet, int totalInvigilators, double totalRemuneration) {
//        XSSFRow titleRow = sheet.createRow(1);
//        XSSFCell titleCell = titleRow.createCell(0);
//        titleCell.setCellValue("Invigilator Summary");
//
//        XSSFRow headerRow = sheet.createRow(2);
//        headerRow.createCell(0).setCellValue("Metric");
//        headerRow.createCell(1).setCellValue("Value");
//
//        XSSFRow invigilatorsRow = sheet.createRow(3);
//        invigilatorsRow.createCell(0).setCellValue("Total Invigilators");
//        invigilatorsRow.createCell(1).setCellValue(totalInvigilators);
//
//        XSSFRow remunerationRow = sheet.createRow(4);
//        remunerationRow.createCell(0).setCellValue("Total Remuneration");
//
//        XSSFRow averageRow = sheet.createRow(5);
//        averageRow.createCell(0).setCellValue("Average Remuneration per Invigilator");
//
//        // Create number format for currency
//        XSSFDataFormat format = sheet.getWorkbook().createDataFormat();
//        short currencyFormatId = format.getFormat("$#,##0.00");
//
//        // Apply styles and number formats
//        XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
//        style.setBorderBottom(BorderStyle.THIN);
//        style.setBorderTop(BorderStyle.THIN);
//        style.setBorderLeft(BorderStyle.THIN);
//        style.setBorderRight(BorderStyle.THIN);
//
//        XSSFCellStyle currencyStyle = sheet.getWorkbook().createCellStyle();
//        currencyStyle.cloneStyleFrom(style);
//        currencyStyle.setDataFormat(currencyFormatId);
//
//        for (int i = 1; i <= 5; i++) {
//            for (int j = 0; j < 2; j++) {
//                XSSFCell cell = sheet.getRow(i).getCell(j);
//                if (cell == null) {
//                    cell = sheet.getRow(i).createCell(j);
//                }
//                if (i >= 4 && j == 1) {
//                    cell.setCellStyle(currencyStyle);
//                } else {
//                    cell.setCellStyle(style);
//                }
//            }
//        }
//
//        // Set values with proper formatting
//        remunerationRow.getCell(1).setCellValue(totalRemuneration);
//        averageRow.getCell(1).setCellValue(totalRemuneration / totalInvigilators);
//
//        // Auto-size columns
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

    private void createMainTitle(XSSFSheet sheet) {
        XSSFRow titleRow = sheet.createRow(0);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
        XSSFCell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("ATTENDANCE AND TOTAL HOURS REPORT");

        XSSFCellStyle titleStyle = sheet.getWorkbook().createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        XSSFFont titleFont = sheet.getWorkbook().createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);
    }

    private void createHeaderRow(XSSFSheet sheet) {
        XSSFRow headerRow = sheet.createRow(8);
        String[] columns = {"FUID", "First name", "Last name", "Department", "Date", "Start time", "End time", "Hours", "Total Hours", "Hourly Rate", "Total Remuneration"};

        XSSFCellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        headerStyle.setFillForegroundColor(new XSSFColor(new Color(219, 229, 241), null));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        setBorderForStyle(headerStyle);

        XSSFFont headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFont(headerFont);

        for (int i = 0; i < columns.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillInvigilatorDate(XSSFRow row, User invigilator, ExamSlot examSlot, boolean isFirstRow) {
        XSSFWorkbook workbook = row.getSheet().getWorkbook();
        XSSFCellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(workbook.createDataFormat().getFormat("dd-MM-yyyy"));
        setBorderForStyle(dateCellStyle);

        XSSFCellStyle centerAlignStyle = getCenterAlignStyle(workbook);
        XSSFCellStyle defaultStyle = getDefaultStyle(workbook);


        XSSFColor lightGray = new XSSFColor(new Color(242, 242, 242), null);
        defaultStyle.setFillForegroundColor(lightGray);
        defaultStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        defaultStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerAlignStyle.setFillForegroundColor(lightGray);
        centerAlignStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        centerAlignStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dateCellStyle.setFillForegroundColor(lightGray);
        dateCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        dateCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);


        if (isFirstRow) {
            createCell(row, 0, invigilator.getFuId(), centerAlignStyle);
            createCell(row, 1, invigilator.getLastName(), defaultStyle);
            createCell(row, 2, invigilator.getFirstName(), defaultStyle);
            createCell(row, 3, invigilator.getDepartment(), defaultStyle);
        } else {
            for (int i = 0; i < 4; i++) {
                createCell(row, i, "", defaultStyle);
            }
        }
        createCell(row, 4, Date.from(examSlot.getStartAt().toInstant()), dateCellStyle);
        createCell(row, 5, formatTime(examSlot.getStartAt()), centerAlignStyle);
        createCell(row, 6, formatTime(examSlot.getEndAt()), centerAlignStyle);
        createCell(row, 7, calculateHours(examSlot), centerAlignStyle);
        createCell(row, 8, "", centerAlignStyle);
        createCell(row, 9, "", centerAlignStyle);
        createCell(row, 10, "", centerAlignStyle);
    }

    private XSSFCell createCell(XSSFRow row, int column, Object value, XSSFCellStyle style) {
        XSSFCell cell = row.createCell(column);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        }
        cell.setCellStyle(style);
        return cell;
    }

    private String formatTime(ZonedDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }

    private double calculateHours(ExamSlot examSlot) {
        return Duration.between(examSlot.getStartAt(), examSlot.getEndAt()).toHours();
    }

    private void fillSummaryRow(XSSFSheet sheet, XSSFRow row, double totalHours, double hourRate) {
        XSSFCellStyle summaryStyle = sheet.getWorkbook().createCellStyle();
        summaryStyle.setFillForegroundColor(new XSSFColor(new Color(189, 215, 238), null));
        summaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        summaryStyle.setAlignment(HorizontalAlignment.CENTER);
        setBorderForStyle(summaryStyle);

        XSSFCellStyle totalStyle = summaryStyle;
        totalStyle.setAlignment(HorizontalAlignment.RIGHT);

        XSSFFont summaryFont = sheet.getWorkbook().createFont();
        summaryFont.setBold(true);
        summaryFont.setColor(IndexedColors.DARK_BLUE.getIndex());
        summaryStyle.setFont(summaryFont);

        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 6));
        XSSFCell totalCell = row.createCell(0);
        totalCell.setCellValue("Total:");
        totalCell.setCellStyle(totalStyle);


        DecimalFormat decimalFormat = new DecimalFormat("#,##0");
        XSSFCellStyle centerAlignStyle = getCenterAlignStyle(row.getSheet().getWorkbook());
        XSSFColor lightGray = new XSSFColor(new Color(242, 242, 242), null);
        centerAlignStyle.setFillForegroundColor(lightGray);
        centerAlignStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        centerAlignStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        createCell(row, 7, totalHours, centerAlignStyle).setCellStyle(summaryStyle);
        createCell(row, 8, totalHours, centerAlignStyle).setCellStyle(summaryStyle);
        createCell(row, 9, decimalFormat.format(hourRate), summaryStyle);
        createCell(row, 10, decimalFormat.format(totalHours * hourRate), summaryStyle);

        for (int i = 1; i <= 6; i++) {
            createCell(row, i, "", summaryStyle);
        }
    }

    private byte[] getBytes(XSSFWorkbook workbook) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        return bos.toByteArray();
    }

    private void setBorderForStyle(XSSFCellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
    }

    private XSSFCellStyle getCenterAlignStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        setBorderForStyle(style);
        return style;
    }

    private XSSFCellStyle getDefaultStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        setBorderForStyle(style);
        return style;
    }

    private void mergeInvigilatorCells(XSSFSheet sheet, int startRow, int endRow) {
        if (startRow < endRow) {
            for (int i = 0; i < 4; i++) {
                CellRangeAddress range = new CellRangeAddress(startRow, endRow, i, i);
                if (!isRangeAlreadyMerged(sheet, range)) {
                    sheet.addMergedRegion(range);
                }
                CellRangeAddress range2 = new CellRangeAddress(startRow, endRow, i + TOTAL_HOURS_COLUMN, i + TOTAL_HOURS_COLUMN);
                if (!isRangeAlreadyMerged(sheet, range2)) {
                    sheet.addMergedRegion(range2);
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
}