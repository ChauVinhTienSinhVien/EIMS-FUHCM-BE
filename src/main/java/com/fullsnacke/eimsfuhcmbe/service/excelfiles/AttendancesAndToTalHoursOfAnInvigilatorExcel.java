package com.fullsnacke.eimsfuhcmbe.service.excelfiles;

import com.fullsnacke.eimsfuhcmbe.configuration.ConfigurationHolder;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAttendance;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.assignment.CustomMessageException;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAttendanceRepository;
import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.http.HttpStatus;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AttendancesAndToTalHoursOfAnInvigilatorExcel {

    private final SemesterRepository semesterRepository;
    private final InvigilatorAttendanceRepository invigilatorAttendanceRepository;
    private final ConfigurationHolder configurationHolder;

    private final String SHEET_NAME = "Attendance and Total Hours";
    private final int MAIN_TITLE_ROW = 0;
    private int INVIGILATOR_INFO_START_ROW;
    private int SUMMARY_TABLE_START_ROW;
    private int MAIN_TABLE_START_ROW;

    private int sheetMaxWidth = 0;

    //Common Configurations
    private final int FONT_SIZE = 10;
    private final String FONT_FAMILY = "Tahoma";
    private final java.awt.Color HEADER_COLOR = new java.awt.Color(219, 229, 241);
    private final java.awt.Color SUMMARY_ROW_COLOR = new java.awt.Color(189, 215, 238);
    private final java.awt.Color DARK_BLUE = new Color(28, 69, 135);

    public AttendancesAndToTalHoursOfAnInvigilatorExcel(SemesterRepository semesterRepository, InvigilatorAttendanceRepository invigilatorAttendanceRepository, ConfigurationHolder configurationHolder) {
        this.semesterRepository = semesterRepository;
        this.invigilatorAttendanceRepository = invigilatorAttendanceRepository;
        this.configurationHolder = configurationHolder;
    }

    public  byte[] generateAttendanceAndTotalHoursExcelFileBySemesterIdAndFuId(int semesterId, String fuId) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEMESTER_NOT_FOUND));

        List<InvigilatorAttendance> completedAttendances = invigilatorAttendanceRepository.findAttendancesBySemesterIdAndFuId(semesterId, fuId);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(SHEET_NAME);

        createMainTitle(sheet, semester);
        createInvigiLatorInfo(sheet, completedAttendances.get(0).getInvigilatorAssignment().getInvigilatorRegistration().getInvigilator());
        createSummaryTable(sheet);
        //Main Table
        createTableHeader(sheet);

        formatAutoFitColumn(sheet);

        sheet.addMergedRegion(new CellRangeAddress(MAIN_TITLE_ROW, MAIN_TITLE_ROW, 0, sheetMaxWidth - 1));

        String path = "src/main/resources/datafiles/books.xlsx";
        try {
            FileOutputStream fos = new FileOutputStream(path);
            workbook.write(fos);
            fos.close();
            workbook.close();
            System.out.println("Excel file created successfully");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw new CustomMessageException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        }

        return new byte[0];
    }
    private void createMainTitle(XSSFSheet sheet, Semester semester) {
        XSSFRow titleRow = sheet.createRow(MAIN_TITLE_ROW);
        XSSFCell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("ATTENDANCE AND TOTAL HOURS REPORT\nFPTUHCM - " + semester.getName().toUpperCase());
        XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
        formatTitle(style, sheet.getWorkbook());
        titleCell.setCellStyle(style);
    }

    private void createInvigiLatorInfo(XSSFSheet sheet, User invigilator) {
        INVIGILATOR_INFO_START_ROW = MAIN_TITLE_ROW + 2;
        int endRow = INVIGILATOR_INFO_START_ROW;
        XSSFRow row = sheet.createRow(INVIGILATOR_INFO_START_ROW);
        //Row 2
        XSSFCell invigilatorCell = row.createCell(0);
        invigilatorCell.setCellValue("Invigilator information".toUpperCase());
        endRow++;

        //Styles
        XSSFCellStyle titleStyle = sheet.getWorkbook().createCellStyle();
        formatHeader(titleStyle, sheet.getWorkbook());
        titleStyle.setAlignment(HorizontalAlignment.LEFT);

        XSSFCellStyle valueStyle = sheet.getWorkbook().createCellStyle();
        getDefaultStyle(valueStyle, sheet.getWorkbook());
        borderStyle(valueStyle, sheet.getWorkbook());


        //Row 3
        createInvigilatorInfoRow(sheet, endRow++, "FU ID", invigilator.getFuId(), titleStyle, valueStyle);
        sheet.addMergedRegion(new CellRangeAddress(INVIGILATOR_INFO_START_ROW, INVIGILATOR_INFO_START_ROW, 0, sheet.getRow(endRow - 1).getLastCellNum() - 1));
        //Row 4
        createInvigilatorInfoRow(sheet, endRow++, "First name", invigilator.getFirstName(), titleStyle, valueStyle);
        //Row 5
        createInvigilatorInfoRow(sheet, endRow++, "Last name", invigilator.getLastName(), titleStyle, valueStyle);
        //Row 6
        createInvigilatorInfoRow(sheet, endRow++, "Department", invigilator.getDepartment(), titleStyle, valueStyle);
        //Row 7
        createInvigilatorInfoRow(sheet, endRow++, "Email", invigilator.getEmail(), titleStyle, valueStyle);

        invigilatorCell.setCellStyle(titleStyle.copy());
        invigilatorCell.getCellStyle().setFillPattern(FillPatternType.NO_FILL);
        getCenterAlignXYStyle(invigilatorCell.getCellStyle(), sheet.getWorkbook());
        borderStyle(invigilatorCell.getCellStyle(), sheet.getWorkbook());

        SUMMARY_TABLE_START_ROW = endRow + 2;
    }

    private void createInvigilatorInfoRow(XSSFSheet sheet, int rowNum, String title, String value, XSSFCellStyle titleStyle, XSSFCellStyle valueStyle) {
        XSSFRow row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(title);
        row.getCell(0).setCellStyle(titleStyle);
        row.createCell(1).setCellValue(value);
        row.getCell(1).setCellStyle(valueStyle);
    }

    private void createSummaryTable(XSSFSheet sheet) {
        int endRow = SUMMARY_TABLE_START_ROW;

        XSSFCellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        formatHeader(headerStyle, sheet.getWorkbook());
        XSSFRow headerRow = sheet.createRow(SUMMARY_TABLE_START_ROW);
        String[] header = {"Metric", "Value", "Unit"};
        for (int i = 0; i < header.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(headerStyle);
        }
        endRow++;

        XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
        getDefaultStyle(style, sheet.getWorkbook());
        borderStyle(style, sheet.getWorkbook());

        createSummaryTableRow(sheet, endRow++, "Total slots", "slot", style);
        createSummaryTableRow(sheet, endRow++, "Total hours", "hour", style);
        createSummaryTableRow(sheet, endRow++, "Total Remuneration", "VND", style);

        MAIN_TABLE_START_ROW = endRow + 2;

    }

    private void createSummaryTableRow(XSSFSheet sheet, int rowNum, String metric, String unit, XSSFCellStyle style) {
        XSSFRow row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(metric);
        row.createCell(1);
        row.createCell(2).setCellValue(unit);

        for (int i = 0; i < 3; i++) {
            row.getCell(i).setCellStyle(style);
        }
    }


    private void createTableHeader(XSSFSheet sheet) {
        XSSFRow headerRow = sheet.createRow(MAIN_TABLE_START_ROW);
        String[] headers = {"No.", "Exam slot ID", "Date", "Start time", "End time", "Check in", "Check out", "Hours", "Total Hours", "Total Remuneration"};

        XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
        formatHeader(style, sheet.getWorkbook());

        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }

        sheetMaxWidth = headers.length;
    }

    private void formatTitle(XSSFCellStyle style, XSSFWorkbook workbook) {
        XSSFFont titleFont = getFont(workbook);
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 20);
        titleFont.setColor(IndexedColors.RED1.getIndex());

        getDefaultStyle(style, workbook);
        getCenterAlignXYStyle(style, workbook);
        style.setFont(titleFont);

        style.setFillForegroundColor(new XSSFColor(new Color(194, 239, 236), null));
        style.setWrapText(true);
    }

    private void formatHeader(XSSFCellStyle style, XSSFWorkbook workbook) {
        borderStyle(style, workbook);
        getDefaultStyle(style, workbook);
        getCenterAlignXYStyle(style, workbook);
        style.setFillForegroundColor(new XSSFColor(HEADER_COLOR, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.getFont().setBold(true);
        style.getFont().setColor(new XSSFColor(DARK_BLUE, null));
    }

    private String formatTime(ZonedDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private void formatAutoFitColumn(XSSFSheet sheet) {
        for (int i = 0; i < sheetMaxWidth ; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void getCenterAlignXYStyle(XSSFCellStyle style, XSSFWorkbook workbook) {
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    private void getDefaultStyle(XSSFCellStyle style, XSSFWorkbook workbook) {
        style.setFillPattern(FillPatternType.NO_FILL);
        style.setFont(getFont(workbook));
        style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#,##0"));
    }

    private void borderStyle(XSSFCellStyle style, XSSFWorkbook workbook) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private XSSFFont getFont(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setFontName(FONT_FAMILY);
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setFontHeightInPoints((short) FONT_SIZE);
        return font;
    }
}
