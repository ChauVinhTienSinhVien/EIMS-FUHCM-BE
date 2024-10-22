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
    private int mainTableStartAt = 8;
    private int sheetWidth;
    private final SemesterRepository semesterRepository;
    private final InvigilatorAttendanceRepository invigilatorAttendanceRepository;
    private final ConfigurationHolder configurationHolder;

    //Formula
    private String totalInvigilatorsFormula = "COUNTA(";
    private String totalRemunerationFormula = "SUM(";
    private String averageRemunerationFormula = "B" + (SUMARY_TABLE_START_ROW + 4) + "/ B" + (SUMARY_TABLE_START_ROW + 3);

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

        double totalRemuneration = 0;

        // Bắt đầu từ hàng 9 để dành chỗ cho bảng tổng hợp
        int rowNum = mainTableStartAt + 1;

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
        totalInvigilatorsFormula += ")";
        totalRemunerationFormula += ")";
        createSummaryTable(sheet, totalInvigilatorsFormula, totalRemunerationFormula);

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
            totalInvigilatorsFormula += "A" + (row.getRowNum() + 1) + ",";
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
    }

    private void createAMainTableRow(XSSFRow row, int cellNum, String value) {
        XSSFCell cell = row.createCell(cellNum);
        cell.setCellValue(value);
        XSSFWorkbook workbook = row.getSheet().getWorkbook();
        XSSFCellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(borderStyle(row.getSheet().getWorkbook()));
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
                    XSSFCellStyle style = sheet.getRow(startRow).getCell(i).getCellStyle();
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
        XSSFWorkbook workbook = row.getSheet().getWorkbook();

        createAMainTableRow(row, FU_ID_COLUMN_INDEX, "Total:");
        createAMainTableRowFormula(row, HOURS_COLUMN_INDEX, "SUM(H" + (startRow + 1) + ":H" + row.getRowNum() + ")");
        createAMainTableRowFormula(row, TOTAL_HOURS_COLUMN_INDEX, "H" + (row.getRowNum() + 1));
        createAMainTableRow(row, HOURLY_RATE_COLUMN_INDEX, hourRate);
        createAMainTableRowFormula(row, TOTAL_REMUNERATION_COLUMN_INDEX, "PRODUCT(I" + (row.getRowNum() + 1) + ",J" + (row.getRowNum() + 1) + ")");
        totalRemunerationFormula += "K" + (row.getRowNum() + 1) + ",";

        XSSFCellStyle style = getDefaultStyle(row.getSheet().getWorkbook());
        style.cloneStyleFrom(borderStyle(row.getSheet().getWorkbook()));
        System.out.println("Style: " + style.getFont().getFontName());
        style.setFillForegroundColor(new XSSFColor(new Color(194, 239, 236), null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        row.setRowStyle(style);

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, FU_ID_COLUMN_INDEX, END_TIME_COLUMN_INDEX));
    }

    private void createSummaryTable(XSSFSheet sheet, String totalInvigilatorsFormula, String totalRemunerationFormula) {

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

        XSSFCellStyle style = row.getSheet().getWorkbook().createCellStyle();
        style.cloneStyleFrom(borderStyle(row.getSheet().getWorkbook()));

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

    private void addNewRowToSummaryTable(XSSFSheet sheet, int rowNum, String metric, int value, String unit) {
        XSSFRow row = sheet.createRow(rowNum);
        row.createCell(METRIC_COLUMN_INDEX).setCellValue(metric);
        row.createCell(VALUE_COLUMN_INDEX).setCellValue(value);
        row.createCell(UNIT_COLUMN_INDEX).setCellValue(unit);
        XSSFCellStyle style = row.getSheet().getWorkbook().createCellStyle();
        style.cloneStyleFrom(borderStyle(row.getSheet().getWorkbook()));
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
        XSSFRow headerRow = sheet.getRow(mainTableStartAt);
        for (Cell cell : headerRow) {
            cell.setCellStyle(headerStyle);
        }
        sheet.getRow(SUMARY_TABLE_START_ROW).getCell(0).setCellStyle(headerStyle);
        XSSFRow summaryTitleRow = sheet.getRow(SUMARY_TABLE_START_ROW + 1);
        for (Cell cell : summaryTitleRow) {
            cell.setCellStyle(headerStyle);
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
        System.out.println("Style format title: " + style.getFont().getFontName());
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
