package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.configuration.ConfigurationHolder;
import com.fullsnacke.eimsfuhcmbe.entity.ExamSlot;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAttendance;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAttendanceRepository;
import com.fullsnacke.eimsfuhcmbe.repository.SemesterRepository;
import com.fullsnacke.eimsfuhcmbe.service.excelfiles.AttendancesAndToTalHoursOfAnInvigilatorExcel;
import com.fullsnacke.eimsfuhcmbe.service.excelfiles.AttendancesAndTotalHoursExcel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ExcelFileServiceImpl implements ExcelFileService {

    InvigilatorAttendanceRepository invigilatorAttendanceRepository;
    ConfigurationHolder configurationHolder;

    @Override
    public byte[] generateAttendanceAndTotalHoursExcelFileForSemester(Semester semester, String toEmail) {
        AttendancesAndToTalHoursOfAnInvigilatorExcel excel = new AttendancesAndToTalHoursOfAnInvigilatorExcel(invigilatorAttendanceRepository, configurationHolder);
        return excel.generateAttendanceAndTotalHoursExcelFileBySemesterIdAndFuId(semester, toEmail);
    }
}
