package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.Semester;

import java.io.IOException;
import java.util.List;

public interface ExcelFileService {
    byte[] generateAttendanceAndTotalHoursExcelFileForSemester(Semester semester, String email);
}
