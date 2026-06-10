package com.legal.assistant.module.mail.service;

public interface MailService {
    void sendVerificationCode(String to, String code);
}
