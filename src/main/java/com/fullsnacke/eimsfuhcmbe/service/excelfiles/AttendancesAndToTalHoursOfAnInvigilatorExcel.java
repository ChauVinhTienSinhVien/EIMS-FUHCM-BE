package com.fullsnacke.eimsfuhcmbe.service.excelfiles;

import com.fullsnacke.eimsfuhcmbe.configuration.ConfigurationHolder;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAttendance;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAttendanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class AttendancesAndToTalHoursOfAnInvigilatorExcel {

    private final InvigilatorAttendanceRepository invigilatorAttendanceRepository;
    private final ConfigurationHolder configurationHolder;

    private final String SHEET_NAME = "Attendance and Total Hours";
    private final int MAIN_TITLE_ROW = 0;
    private int INVIGILATOR_INFO_START_ROW;
    private int SUMMARY_TABLE_START_ROW;
    private int MAIN_TABLE_START_ROW;

    //Main Table
    private final int NO_COLUMN = 0;
    private final char NO_COLUMN_CHAR = 'A';
    private final int EXAM_SLOT_ID_COLUMN = 1;
    private final char EXAM_SLOT_ID_COLUMN_CHAR = 'B';
    private final int DATE_COLUMN = 2;
    private final char DATE_COLUMN_CHAR = 'C';
    private final int START_TIME_COLUMN = 3;
    private final char START_TIME_COLUMN_CHAR = 'D';
    private final int END_TIME_COLUMN = 4;
    private final char END_TIME_COLUMN_CHAR = 'E';
    private final int CHECK_IN_COLUMN = 5;
    private final char CHECK_IN_COLUMN_CHAR = 'F';
    private final int CHECK_OUT_COLUMN = 6;
    private final char CHECK_OUT_COLUMN_CHAR = 'G';
    private final int HOURS_COLUMN = 7;
    private final char HOURS_COLUMN_CHAR = 'H';
    private final int TOTAL_HOURS_COLUMN = 8;
    private final char TOTAL_HOURS_COLUMN_CHAR = 'I';
    private final int HOURLY_RATE_COLUMN = 9;
    private final char HOURLY_RATE_COLUMN_CHAR = 'J';
    private final int TOTAL_REMUNERATION_COLUMN = 10;
    private final char TOTAL_REMUNERATION_COLUMN_CHAR = 'K';

    private int sheetMaxWidth = 0;
    private String totalSlotFormula = "COUNTA(";
    private String totalHoursFormula = "SUM(";
    private String totalRemunerationFormula;
    private String hourlyRateFormula;

    //Common Configurations
    private final int FONT_SIZE = 10;
    //private final String FONT_FAMILY = "freetype";
    private final java.awt.Color HEADER_COLOR = new java.awt.Color(219, 229, 241);
    private final java.awt.Color SUMMARY_ROW_COLOR = new java.awt.Color(189, 215, 238);
    private final java.awt.Color DARK_BLUE = new Color(28, 69, 135);

    public AttendancesAndToTalHoursOfAnInvigilatorExcel(InvigilatorAttendanceRepository invigilatorAttendanceRepository, ConfigurationHolder configurationHolder) {
        this.invigilatorAttendanceRepository = invigilatorAttendanceRepository;
        this.configurationHolder = configurationHolder;
    }

    public byte[] generateAttendanceAndTotalHoursExcelFileBySemesterIdAndFuId(Semester semester, String toEmail) {
        try {

            List<InvigilatorAttendance> completedAttendances = invigilatorAttendanceRepository.findAttendancesBySemesterIdAndEmail(semester, toEmail, Instant.now().atZone(ZoneId.systemDefault()));
//            List<InvigilatorAttendance> completedAttendances = invigilatorAttendanceRepository.findAttendancesBySemesterIdAndEmail(semester, toEmail);

            System.out.println("Completed attendances: " + completedAttendances.size());
            completedAttendances.stream().forEach(invigilatorAttendance -> {
                System.out.println("Exam slot ID: " + invigilatorAttendance.getInvigilatorAssignment().getInvigilatorRegistration().getExamSlot().getId());
                System.out.println("StartAt: " + invigilatorAttendance.getInvigilatorAssignment().getInvigilatorRegistration().getExamSlot().getStartAt());
            });
            if (completedAttendances.isEmpty()) {
                return new byte[0];
            }

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(SHEET_NAME);

            createMainTitle(sheet, semester);
            createInvigiLatorInfo(sheet, completedAttendances.get(0).getInvigilatorAssignment().getInvigilatorRegistration().getInvigilator());
            createSummaryTable(sheet);

            //Main Table
            createTableHeader(sheet);

            int endRow = MAIN_TABLE_START_ROW + 1;
            for (InvigilatorAttendance attendance : completedAttendances)
                createARowAttendance(sheet, endRow++, attendance);

            if ((MAIN_TABLE_START_ROW + 1) < (endRow - 1)) {
                for (int i = TOTAL_HOURS_COLUMN; i <= TOTAL_REMUNERATION_COLUMN; i++) {
                    sheet.addMergedRegion(new CellRangeAddress(MAIN_TABLE_START_ROW + 1, endRow - 1, i, i));
                }
            }

            trueFalseStyle(sheet, CHECK_IN_COLUMN_CHAR, MAIN_TABLE_START_ROW + 1, endRow);
            trueFalseStyle(sheet, CHECK_OUT_COLUMN_CHAR, MAIN_TABLE_START_ROW + 1, endRow);
            setupHoursColumnStatus(sheet, MAIN_TABLE_START_ROW + 1, endRow);

            sheet.addMergedRegion(new CellRangeAddress(MAIN_TITLE_ROW, MAIN_TITLE_ROW, 0, sheetMaxWidth - 1));

            formatAutoFitColumn(sheet);

            createSummaryRow(sheet, endRow++);

            updateSummaryTable(sheet);

            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                workbook.write(bos);
                byte[] bytes = bos.toByteArray();
                System.out.println("Excel file generated successfully! Size: " + bytes.length);
                return bos.toByteArray();
            } catch (IOException e) {
                log.error("Error while writing excel file: " + e.getMessage());
                return new byte[0];
            }
        } catch (Exception e) {
            log.error("Error while generating excel file: " + e.getMessage());
            return new byte[0];
        }
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
        //Row 2
        XSSFCellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        formatHeader(headerStyle, sheet.getWorkbook());
        headerStyle.setFillPattern(FillPatternType.NO_FILL);

        createInvigilatorInfoRow(sheet, endRow++, "Invigilator information".toUpperCase(), "", headerStyle, headerStyle);

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

        //Header Style
        XSSFCellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        formatHeader(headerStyle, sheet.getWorkbook());

        //Title Style
        XSSFCellStyle titleStyle = headerStyle.copy();
        titleStyle.setFillPattern(FillPatternType.NO_FILL);

        //Styles
        XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
        getDefaultStyle(style, sheet.getWorkbook());
        borderStyle(style, sheet.getWorkbook());

        //Title
        createSummaryTableRow(sheet, endRow++, "Summary".toUpperCase(), "Summary".toUpperCase(), titleStyle);

        //Header Row
        XSSFRow headerRow = sheet.createRow(SUMMARY_TABLE_START_ROW + 1);

        String[] header = {"Metric", "Value", "Unit"};
        for (int i = 0; i < header.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(headerStyle);
        }
        endRow++;
        sheet.addMergedRegion(new CellRangeAddress(SUMMARY_TABLE_START_ROW, SUMMARY_TABLE_START_ROW, 0, header.length - 1));

        createSummaryTableRow(sheet, endRow++, "Total slots", "slot", style);
        createSummaryTableRow(sheet, endRow++, "Total hours", "hour", style);
        createSummaryTableRow(sheet, endRow++, "Hourly rate", "VND", style);
        createSummaryTableRow(sheet, endRow++, "Total Remuneration", "VND", style);

        MAIN_TABLE_START_ROW = endRow + 2;

    }

    private void updateSummaryTable(XSSFSheet sheet) {
        int count = 2;
        XSSFRow row = sheet.getRow(SUMMARY_TABLE_START_ROW + count++);
        row.getCell(1).setCellFormula(totalSlotFormula);
        row = sheet.getRow(SUMMARY_TABLE_START_ROW + count++);
        row.getCell(1).setCellFormula(totalHoursFormula);
        row = sheet.getRow(SUMMARY_TABLE_START_ROW + count++);
        row.getCell(1).setCellFormula(hourlyRateFormula);
        row = sheet.getRow(SUMMARY_TABLE_START_ROW + count++);
        row.getCell(1).setCellFormula(totalRemunerationFormula);
    }

    private void createSummaryTableRow(XSSFSheet sheet, int rowNum, String metric, String unit, XSSFCellStyle style) {
        XSSFRow row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(metric);
        row.createCell(1);
        row.createCell(2).setCellValue(unit.toUpperCase().charAt(0) + unit.substring(1));

        for (int i = 0; i < 3; i++) {
            row.getCell(i).setCellStyle(style);
        }
    }

    private void createTableHeader(XSSFSheet sheet) {
        XSSFRow headerRow = sheet.createRow(MAIN_TABLE_START_ROW);
        String[] headers = {"No.", "Exam slot ID", "Date", "Start time", "End time", "Check in", "Check out", "Hours", "Total Hours", "Hourly Rate", "Total Remuneration"};

        XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
        formatHeader(style, sheet.getWorkbook());

        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }

        sheetMaxWidth = headers.length;
    }

    private void setupHoursColumnStatus(XSSFSheet sheet, int startRow, int endRow) {
        for (int i = startRow + 1; i <= endRow; i++) {
            SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

            String trueCondition = "AND(" + CHECK_IN_COLUMN_CHAR + i + "=TRUE," + CHECK_OUT_COLUMN_CHAR + i + "=TRUE)";
            ConditionalFormattingRule greenRule = sheetCF.createConditionalFormattingRule(trueCondition);
            PatternFormatting fillGreen = greenRule.createPatternFormatting();
            fillGreen.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.index);
            fillGreen.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

            String falseCondition = "OR(" + CHECK_IN_COLUMN_CHAR + i + "=FALSE," + CHECK_OUT_COLUMN_CHAR + i + "=FALSE)";
            ConditionalFormattingRule redRule = sheetCF.createConditionalFormattingRule(falseCondition);
            PatternFormatting fillRed = redRule.createPatternFormatting();
            fillRed.setFillBackgroundColor(new XSSFColor(new Color(250, 128, 114), null));
            fillRed.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

            CellRangeAddress[] range = {new CellRangeAddress(i - 1, i - 1, HOURS_COLUMN, HOURS_COLUMN)};
            sheetCF.addConditionalFormatting(range, greenRule, redRule);
        }
    }

    private void createSummaryRow(XSSFSheet sheet, int row) {
        XSSFWorkbook workbook = sheet.getWorkbook();
        XSSFRow totalRow = sheet.createRow(row);
        sheet.addMergedRegion(new CellRangeAddress(row, row, NO_COLUMN, CHECK_OUT_COLUMN));
        totalRow.createCell(NO_COLUMN).setCellValue("Total:");
        for (int i = EXAM_SLOT_ID_COLUMN; i <= CHECK_OUT_COLUMN; i++) {
            totalRow.createCell(i);
        }
        totalRow.createCell(HOURS_COLUMN).setCellFormula("SUM(" + HOURS_COLUMN_CHAR + (MAIN_TABLE_START_ROW + 2) + ":" + HOURS_COLUMN_CHAR + row + ")");
        totalRow.createCell(TOTAL_HOURS_COLUMN).setCellFormula(HOURS_COLUMN_CHAR + "" + (row + 1));
        totalRow.createCell(HOURLY_RATE_COLUMN).setCellValue(configurationHolder.getHourlyRate());
        totalRow.createCell(TOTAL_REMUNERATION_COLUMN).setCellFormula(TOTAL_HOURS_COLUMN_CHAR + "" + (row + 1) + "*" + HOURLY_RATE_COLUMN_CHAR + "" + (row + 1));
        totalRemunerationFormula = TOTAL_REMUNERATION_COLUMN_CHAR + "" + (row + 1);
        totalHoursFormula = TOTAL_HOURS_COLUMN_CHAR + "" + (row + 1);
        totalSlotFormula = totalSlotFormula.substring(0, totalSlotFormula.length() - 1) + ")";
        hourlyRateFormula = HOURLY_RATE_COLUMN_CHAR + "" + (row + 1);

        XSSFCellStyle style = workbook.createCellStyle();
        getDefaultStyle(style, workbook);
        borderStyle(style, workbook);
        style.setFillForegroundColor(new XSSFColor(SUMMARY_ROW_COLOR, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.getFont().setBold(true);
        style.getFont().setColor(new XSSFColor(DARK_BLUE, null));
        for (int i = NO_COLUMN; i <= TOTAL_REMUNERATION_COLUMN; i++) {
            totalRow.getCell(i).setCellStyle(style);
        }
    }

    private void createARowAttendance(XSSFSheet sheet, int rowNum, InvigilatorAttendance attendance) {
        XSSFWorkbook workbook = sheet.getWorkbook();
        XSSFCellStyle style = workbook.createCellStyle();
        getDefaultStyle(style, workbook);
        borderStyle(style, workbook);

        ExamSlot examSlot = attendance.getInvigilatorAssignment().getInvigilatorRegistration().getExamSlot();
        XSSFRow row = sheet.createRow(rowNum++);
        row.createCell(NO_COLUMN).setCellValue(rowNum - 1 - MAIN_TABLE_START_ROW);
        row.createCell(EXAM_SLOT_ID_COLUMN).setCellValue(examSlot.getId());
        row.createCell(DATE_COLUMN).setCellValue(examSlot.getStartAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        row.createCell(START_TIME_COLUMN).setCellValue(formatTime(examSlot.getStartAt()));
        row.createCell(END_TIME_COLUMN).setCellValue(formatTime(examSlot.getEndAt()));
        row.createCell(CHECK_IN_COLUMN).setCellValue(attendance.getCheckIn() != null);
        row.createCell(CHECK_OUT_COLUMN).setCellValue(attendance.getCheckOut() != null);

        String trueFormula = "HOUR(" + END_TIME_COLUMN_CHAR + (row.getRowNum() + 1) + "-" + START_TIME_COLUMN_CHAR + (row.getRowNum() + 1) + ") + MINUTE(" + END_TIME_COLUMN_CHAR + (row.getRowNum() + 1) + "-" + START_TIME_COLUMN_CHAR + (row.getRowNum() + 1) + ")/60";
        String hoursFormula = "IF(AND(" + CHECK_IN_COLUMN_CHAR + (row.getRowNum() + 1) + "=TRUE," + CHECK_OUT_COLUMN_CHAR + (row.getRowNum() + 1) + "=TRUE)," + trueFormula + ", 0)";
        row.createCell(HOURS_COLUMN).setCellFormula(hoursFormula);

        row.createCell(TOTAL_HOURS_COLUMN);
        row.createCell(HOURLY_RATE_COLUMN);
        row.createCell(TOTAL_REMUNERATION_COLUMN);

        for (int i = NO_COLUMN; i <= TOTAL_REMUNERATION_COLUMN; i++) {
            row.getCell(i).setCellStyle(style);
        }

        totalSlotFormula += EXAM_SLOT_ID_COLUMN_CHAR + "" + (row.getRowNum() + 1) + ",";
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
        for (int i = 0; i < sheetMaxWidth; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void trueFalseStyle(XSSFSheet sheet, char column, int startRow, int endRow) {
        SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

        ConditionalFormattingRule trueRule = sheetCF.createConditionalFormattingRule(ComparisonOperator.EQUAL, "TRUE");
        PatternFormatting fillGreen = trueRule.createPatternFormatting();
        fillGreen.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.index);
        fillGreen.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        ConditionalFormattingRule falseRule = sheetCF.createConditionalFormattingRule(ComparisonOperator.EQUAL, "FALSE");
        PatternFormatting fillRed = falseRule.createPatternFormatting();
        fillRed.setFillBackgroundColor(new XSSFColor(new Color(250, 128, 114), null));
        fillRed.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
        CellRangeAddress[] range = {CellRangeAddress.valueOf(column + "" + startRow + ":" + column + endRow)};
        sheetCF.addConditionalFormatting(range, trueRule, falseRule);
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
        //font.setFontName(FONT_FAMILY);
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setFontHeightInPoints((short) FONT_SIZE);
        return font;
    }
}
