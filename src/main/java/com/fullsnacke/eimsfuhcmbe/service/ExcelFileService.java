package com.fullsnacke.eimsfuhcmbe.service;

import java.io.IOException;
import java.util.List;

public interface ExcelFileService {
    byte[] generateAttendanceAndTotalHoursExcelFileForSemester(int semesterId);
    byte[] generateAttendanceAndTotalHoursExcelFileForSemester(int semesterId, String fuId);
}
