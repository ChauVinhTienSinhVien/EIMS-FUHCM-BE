package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.entity.Semester;

public interface ExcelFileService {
    byte[] generateAttendanceAndTotalHoursExcelFileForSemester(Semester semester, String email);
}
