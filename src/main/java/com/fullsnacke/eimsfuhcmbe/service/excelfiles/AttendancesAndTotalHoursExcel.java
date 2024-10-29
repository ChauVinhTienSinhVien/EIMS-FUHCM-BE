package com.fullsnacke.eimsfuhcmbe.service.excelfiles;

import com.fullsnacke.eimsfuhcmbe.configuration.ConfigurationHolder;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAttendance;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAttendanceRepository;
import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AttendancesAndTotalHoursExcel {
    private final Color HEADER_COLOR = new Color(219, 229, 241);
    private final Color SUMMARY_ROW_COLOR = new Color(189, 215, 238);
    private final Color DARK_BLUE = new Color(28, 69, 135);
    //Summary Table
    private final int METRIC_COLUMN_INDEX = 0;
    private final int VALUE_COLUMN_INDEX = 1;
    private final int UNIT_COLUMN_INDEX = 2;
    private final String SUMMARY_TABLE_TITLE = "Invigilator Summary";

    //Main Table
    //index of column
    private final int NO_COLUMN_INDEX = 0;
    private final char NO_CHAR = 'A';
    private final int FU_ID_COLUMN_INDEX = 1;
    private final char FU_ID_CHAR = 'B';
    private final int FIRST_NAME_COLUMN_INDEX = 2;
    private final char FIRST_NAME_CHAR = 'C';
    private final int LAST_NAME_COLUMN_INDEX = 3;
    private final char LAST_NAME_CHAR = 'D';
    private final int DEPARTMENT_COLUMN_INDEX = 4;
    private final char DEPARTMENT_CHAR = 'E';
    private final int DATE_COLUMN_INDEX = 5;
    private final char DATE_CHAR = 'F';
    private final int START_TIME_COLUMN_INDEX = 6;
    private final char START_TIME_CHAR = 'G';
    private final int END_TIME_COLUMN_INDEX = 7;
    private final char END_TIME_CHAR = 'H';
    private final int HOURS_COLUMN_INDEX = 8;
    private final char HOURS_CHAR = 'I';
    private final int TOTAL_HOURS_COLUMN_INDEX = 9;
    private final char TOTAL_HOURS_CHAR = 'J';
    private final int HOURLY_RATE_COLUMN_INDEX = 10;
    private final char HOURLY_RATE_CHAR = 'K';
    private final int TOTAL_REMUNERATION_COLUMN_INDEX = 11;
    private final char TOTAL_REMUNERATION_CHAR = 'L';

    //field name
    private final String NO_FIELD = "No.";
    private final String FU_ID = "Fu ID";
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
    private int mainTableStartAt = 8;
    private int sheetWidth;

    private final SemesterRepository semesterRepository;
    private final InvigilatorAttendanceRepository invigilatorAttendanceRepository;
    private final ConfigurationHolder configurationHolder;

    //Formula
    private String totalInvigilatorsFormula = "COUNTA(";
    private String totalRemunerationFormula = "SUM(";
    private String averageRemunerationFormula = "B" + (SUMARY_TABLE_START_ROW + 4) + "/ B" + (SUMARY_TABLE_START_ROW + 3);

    public AttendancesAndTotalHoursExcel(SemesterRepository semesterRepository, InvigilatorAttendanceRepository invigilatorAttendanceRepository, ConfigurationHolder configurationHolder) {
        this.semesterRepository = semesterRepository;
        this.invigilatorAttendanceRepository = invigilatorAttendanceRepository;
        this.configurationHolder = configurationHolder;
    }

    public byte[] generateAttendanceAndTotalHoursExcelFileForSemester(int semesterId) {
        // Tìm semester theo ID, nếu không tìm thấy thì ném exception
        // Kiểm tra kì có tồn tại không
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEMESTER_NOT_FOUND));

        // Lấy danh sách các attendance đã hoàn thành (có cả check-in và check-out) cho semester này
        java.util.List<InvigilatorAttendance> completedAttendances = invigilatorAttendanceRepository.findCompletedAttendancesBySemesterId(semesterId);

        // Nhóm các attendance theo invigilator (sử dụng fuId làm key)
        Map<String, java.util.List<InvigilatorAttendance>> attendanceMap = completedAttendances.stream()
                .collect(Collectors.groupingBy(attendance -> attendance.getInvigilatorAssignment().getInvigilatorRegistration().getInvigilator().getFuId()));

        // Tạo workbook mới để viết dữ liệu và sheet "Attendance and Total Hours"
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(SHEET_NAME);

        // Tạo tiêu đề chính và hàng tiêu đề
        createMainTitle(sheet, semester);
        createHeaderRow(sheet);


        // Bắt đầu từ hàng 9 để dành chỗ cho bảng tổng hợp
        int rowNum = mainTableStartAt + 1;

        int number = 0;

        // Lặp qua từng invigilator và các attendance của họ
        for (Map.Entry<String, List<InvigilatorAttendance>> entry : attendanceMap.entrySet()) {
            // Vì tất cả các attendance trong entry này đều thuộc về cùng một invigilator nên lấy thông tin từ bất kỳ attendance nào cũng được
            User invigilator = entry.getValue().get(0).getInvigilatorAssignment().getInvigilatorRegistration().getInvigilator();

            // Lưu lại dòng bắt đầu của invigilator này
            // Dùng để merge các ô của invigilator này sau khi đã tạo xong
            int startRow = rowNum;

            double totalHours = 0;
            ++number;
            // Lặp qua từng attendance của invigilator
            for (InvigilatorAttendance attendance : entry.getValue()) {
                ExamSlot examSlot = attendance.getInvigilatorAssignment().getInvigilatorRegistration().getExamSlot();
                XSSFRow row = sheet.createRow(rowNum++);
                fillInvigilatorData(row, number, invigilator, examSlot, startRow == rowNum - 1);
            }
            mergeInvigilatorCells(sheet, startRow, rowNum - 1);

            fillSummaryRow(sheet, rowNum++, startRow, configurationHolder.getHourlyRate());

            sheet.addMergedRegion(new CellRangeAddress(startRow, rowNum - 1, NO_COLUMN_INDEX, NO_COLUMN_INDEX));
            sheet.createRow(rowNum++);
        }
        totalInvigilatorsFormula += ")";
        totalRemunerationFormula += ")";
        createSummaryTable(sheet);

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
        String[] columns = {NO_FIELD, FU_ID, FIRST_NAME, LAST_NAME, DEPARTMENT, DATE, START_TIME, END_TIME, HOURS, TOTAL_HOURS, HOURLY_RATE, TOTAL_REMUNERATION};
        sheetWidth = columns.length;
        for (int i = 0; i < columns.length; i++) {
            headerRow.createCell(i).setCellValue(columns[i]);
        }
    }

    private void fillInvigilatorData(XSSFRow row, int order, User invigilator, ExamSlot examSlot, boolean isFirstRow) {
        XSSFWorkbook workbook = row.getSheet().getWorkbook();
        XSSFCellStyle style = workbook.createCellStyle();
        borderStyle(style, workbook);
        getDefaultStyle(style, workbook);

        if (isFirstRow) {
            totalInvigilatorsFormula += " A" + (row.getRowNum() + 1) + ",";
            row.createCell(NO_COLUMN_INDEX).setCellValue(order);
            XSSFCellStyle noStyle = formatHeader(workbook).copy();
            getCenterAlignXYStyle(noStyle, workbook);
            setFormatForCellWithBorder(row.getCell(NO_COLUMN_INDEX), workbook, noStyle);
            row.getCell(NO_COLUMN_INDEX).setCellStyle(noStyle);
            row.createCell(FU_ID_COLUMN_INDEX).setCellValue(invigilator.getFuId());
            row.createCell(FIRST_NAME_COLUMN_INDEX).setCellValue(invigilator.getFirstName());
            row.createCell(LAST_NAME_COLUMN_INDEX).setCellValue(invigilator.getLastName());
            row.createCell(DEPARTMENT_COLUMN_INDEX).setCellValue(invigilator.getDepartment());
        } else {
            row.createCell(NO_COLUMN_INDEX).setCellValue("");
            row.createCell(FU_ID_COLUMN_INDEX).setCellValue("");
            row.createCell(FIRST_NAME_COLUMN_INDEX).setCellValue("");
            row.createCell(LAST_NAME_COLUMN_INDEX).setCellValue("");
            row.createCell(DEPARTMENT_COLUMN_INDEX).setCellValue("");
        }
        row.createCell(DATE_COLUMN_INDEX).setCellValue(examSlot.getStartAt().toLocalDate());
        row.createCell(START_TIME_COLUMN_INDEX).setCellValue(formatTime(examSlot.getStartAt()));
        row.createCell(END_TIME_COLUMN_INDEX).setCellValue(formatTime(examSlot.getEndAt()));
        row.createCell(HOURS_COLUMN_INDEX).setCellFormula("HOUR(" + END_TIME_CHAR + (row.getRowNum() + 1) + "-" + START_TIME_CHAR + (row.getRowNum() + 1) + ") + MINUTE(" + END_TIME_CHAR + (row.getRowNum() + 1) + "-" + START_TIME_CHAR + (row.getRowNum() + 1) + ")/60");
        row.createCell(TOTAL_HOURS_COLUMN_INDEX).setCellValue("");
        row.createCell(HOURLY_RATE_COLUMN_INDEX).setCellValue("");
        row.createCell(TOTAL_REMUNERATION_COLUMN_INDEX).setCellValue("");

        for (int i = FU_ID_COLUMN_INDEX; i <= TOTAL_REMUNERATION_COLUMN_INDEX; i++) {
            setFormatForCellWithBorder(row.getCell(i), workbook, style);
        }

        XSSFCellStyle timeCellStyle = row.getCell(DATE_COLUMN_INDEX).getCellStyle().copy();
        timeCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy"));
        row.getCell(DATE_COLUMN_INDEX).setCellStyle(timeCellStyle);
    }


    private void mergeInvigilatorCells(XSSFSheet sheet, int startRow, int endRow) {
        if (startRow < endRow) {
            for (int i = FU_ID_COLUMN_INDEX; i <= DEPARTMENT_COLUMN_INDEX; i++) {
                CellRangeAddress range = new CellRangeAddress(startRow, endRow, i, i);
                if (!isRangeAlreadyMerged(sheet, range)) {
                    sheet.addMergedRegion(range);
                    XSSFCellStyle style = sheet.getRow(startRow).getCell(i).getCellStyle().copy();
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    sheet.getRow(startRow).getCell(i).setCellStyle(style);
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
        XSSFWorkbook workbook = sheet.getWorkbook();
        XSSFCellStyle style = workbook.createCellStyle();
        getDefaultStyle(style, workbook);
        borderStyle(style, workbook);
        style.setFillForegroundColor(new XSSFColor(SUMMARY_ROW_COLOR, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.getFont().setBold(true);
        style.getFont().setColor(new XSSFColor(DARK_BLUE, null));

        row.createCell(FU_ID_COLUMN_INDEX).setCellValue("Total:");
        row.createCell(HOURS_COLUMN_INDEX).setCellFormula("SUM(" + HOURS_CHAR + (startRow + 1) + ":" + HOURS_CHAR + row.getRowNum() + ")");
        row.createCell(TOTAL_HOURS_COLUMN_INDEX).setCellFormula("" + HOURS_CHAR + (row.getRowNum() + 1));
        row.createCell(HOURLY_RATE_COLUMN_INDEX).setCellValue(hourRate);
        row.createCell(TOTAL_REMUNERATION_COLUMN_INDEX).setCellFormula("PRODUCT(" + TOTAL_HOURS_CHAR + (row.getRowNum() + 1) + "," + HOURLY_RATE_CHAR + (row.getRowNum() + 1) + ")");
        totalRemunerationFormula += TOTAL_REMUNERATION_CHAR + "" + (row.getRowNum() + 1) + ",";

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, FU_ID_COLUMN_INDEX, END_TIME_COLUMN_INDEX));

        setFormatForCellWithBorder(row.getCell(FU_ID_COLUMN_INDEX), workbook, style);

        for (int i = HOURS_COLUMN_INDEX; i <= TOTAL_REMUNERATION_COLUMN_INDEX; i++) {
            XSSFCell cell = row.getCell(i);
            setFormatForCellWithBorder(cell, workbook, style);
        }
    }

    private void setFormatForCellWithBorder(XSSFCell cell, XSSFWorkbook workbook, XSSFCellStyle style) {
        cell.setCellStyle(style);
    }

    private void createSummaryTable(XSSFSheet sheet) {

        int rowNum = SUMARY_TABLE_START_ROW;

        XSSFRow titleRow = sheet.createRow(rowNum);
        titleRow.createCell(0).setCellValue(SUMMARY_TABLE_TITLE.toUpperCase());

        addNewRowToSummaryTable(sheet, ++rowNum, "Metric", "Value", "Unit");
        int cellNum = sheet.getRow(rowNum).getLastCellNum();

        addNewRowToSummaryTableFormula(sheet, ++rowNum, "Total Invigilators", totalInvigilatorsFormula, "People");

        addNewRowToSummaryTableFormula(sheet, ++rowNum, "Total Remuneration", totalRemunerationFormula, "VND");

        addNewRowToSummaryTableFormula(sheet, ++rowNum, "Average Remuneration", averageRemunerationFormula, "VND");

        sheet.addMergedRegion(new CellRangeAddress(SUMARY_TABLE_START_ROW, SUMARY_TABLE_START_ROW, 0, cellNum - 1));
    }

    private void addNewRowToSummaryTableFormula(XSSFSheet sheet, int rowNum, String metric, String formula, String unit) {
        XSSFRow row = sheet.createRow(rowNum);

        XSSFWorkbook workbook = row.getSheet().getWorkbook();
        XSSFCellStyle style = workbook.createCellStyle();
        borderStyle(style, workbook);
        getDefaultStyle(style, workbook);

        row.createCell(METRIC_COLUMN_INDEX).setCellValue(metric);
        row.getCell(METRIC_COLUMN_INDEX).setCellStyle(style);

        row.createCell(VALUE_COLUMN_INDEX).setCellFormula(formula);
        row.getCell(VALUE_COLUMN_INDEX).setCellStyle(style);

        row.createCell(UNIT_COLUMN_INDEX).setCellValue(unit);
        row.getCell(UNIT_COLUMN_INDEX).setCellStyle(style);
    }

    private void addNewRowToSummaryTable(XSSFSheet sheet, int rowNum, String metric, String value, String unit) {
        XSSFRow row = sheet.createRow(rowNum);
        row.createCell(METRIC_COLUMN_INDEX).setCellValue(metric);
        row.createCell(VALUE_COLUMN_INDEX).setCellValue(value);
        row.createCell(UNIT_COLUMN_INDEX).setCellValue(unit);
    }

    private void formatWorkbook(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.getSheetAt(0);

        XSSFCellStyle titleStyle = formatTitle(workbook);
        sheet.getRow(0).getCell(0).setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, sheetWidth));

        // Format header row
        XSSFCellStyle headerStyle = formatHeader(workbook);
        XSSFRow headerRow = sheet.getRow(mainTableStartAt);
        for (Cell cell : headerRow) {
            cell.setCellStyle(headerStyle);
        }
        sheet.getRow(SUMARY_TABLE_START_ROW).getCell(0).setCellStyle(headerStyle.copy());
        sheet.getRow(SUMARY_TABLE_START_ROW).getCell(0).getCellStyle().setFillPattern(FillPatternType.NO_FILL);
        XSSFRow summaryTitleRow = sheet.getRow(SUMARY_TABLE_START_ROW + 1);
        for (Cell cell : summaryTitleRow) {
            cell.setCellStyle(headerStyle);
        }
        // Auto-size columns
        for (int i = NO_COLUMN_INDEX; i <= TOTAL_REMUNERATION_COLUMN_INDEX; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private XSSFCellStyle formatTitle(XSSFWorkbook workbook) {
        // Format font
        XSSFFont titleFont = getFont(workbook);
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 20);
        titleFont.setColor(IndexedColors.RED1.getIndex());
        // Format style
        XSSFCellStyle style = workbook.createCellStyle();
        getDefaultStyle(style, workbook);
        getCenterAlignXYStyle(style, workbook);
        style.setFont(titleFont);
        // null đơn giản có nghĩa là "không sử dụng bảng màu cụ thể trong IndexedColors map - tập hợp các màu mặc định được định nghĩa sẵn,"
        // mà thay vào đó bạn sử dụng mã màu RGB trực tiếp.
        style.setFillForegroundColor(new XSSFColor(new Color(194, 239, 236), null));
        style.setWrapText(true);// Thiết lập ô để chứa xuống dòng

        return style;
    }

    private XSSFCellStyle formatHeader(XSSFWorkbook workbook) {

        XSSFCellStyle headerStyle = workbook.createCellStyle();
        borderStyle(headerStyle, workbook);
        getDefaultStyle(headerStyle, workbook);
        getCenterAlignXYStyle(headerStyle, workbook);
        headerStyle.setFillForegroundColor(new XSSFColor(HEADER_COLOR, null));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.getFont().setBold(true);
        headerStyle.getFont().setColor(new XSSFColor(DARK_BLUE, null));

        return headerStyle;
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
