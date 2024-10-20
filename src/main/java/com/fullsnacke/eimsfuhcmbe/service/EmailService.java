package com.fullsnacke.eimsfuhcmbe.service;

public interface EmailService {
    void sendSimpleMailMessage(String name, String to, String token);
}
