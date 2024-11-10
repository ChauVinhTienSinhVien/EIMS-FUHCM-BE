package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.configuration.ConfigurationHolder;
import com.fullsnacke.eimsfuhcmbe.entity.Semester;
import com.fullsnacke.eimsfuhcmbe.repository.InvigilatorAttendanceRepository;
import com.fullsnacke.eimsfuhcmbe.service.excelfiles.AttendancesAndToTalHoursOfAnInvigilatorExcel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

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
