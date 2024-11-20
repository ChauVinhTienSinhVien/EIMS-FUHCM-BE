package com.fullsnacke.eimsfuhcmbe.service;

import java.util.List;

public interface EmailService {
    List<String> sendAttendanceAndHoursMailMessageInListEmails(int semesterId, List<String> toEmails);
}
