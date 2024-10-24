package com.fullsnacke.eimsfuhcmbe.service;

public interface EmailService {
    void sendSimpleMailMessage(String name, String to);
//    void sendAttendanceAndHoursMailMessage(String name, String to, int semesterId);
    void sendAttendanceAndHoursMailMessage(String to, int semesterId);
}
