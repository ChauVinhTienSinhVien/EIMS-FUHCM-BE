package com.fullsnacke.eimsfuhcmbe.service;

import java.io.IOException;
import java.util.List;

public interface ExcelFileService {
    byte[] generateAttendanceAndTotalHoursExcelFileForSemester(int semesterId);
}
