package com.legal.assistant.module.mail.service.impl;

import com.legal.assistant.config.MailConfig;
import com.legal.assistant.module.mail.service.MailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final MailConfig mailConfig;

    @Override
    public void sendVerificationCode(String to, String code) {
        if (!mailConfig.isEnabled()) {
            log.info("邮件发送未启用，验证码 {} 将发送到 {} 但不实际发送", code, to);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailConfig.getFrom());
            helper.setTo(to);
            helper.setSubject("法律助手 - 验证码");

            String htmlContent = buildEmailContent(code);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("验证码邮件已发送至: {}", to);
        } catch (Exception e) {
            log.error("发送验证码邮件失败: {}", e.getMessage());
        }
    }

    private String buildEmailContent(String code) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f5f5f5; }
                    .container { max-width: 400px; margin: 50px auto; padding: 30px; background: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { text-align: center; margin-bottom: 30px; }
                    .logo { font-size: 32px; margin-bottom: 10px; }
                    .title { color: #1890ff; font-size: 24px; font-weight: bold; }
                    .code-box { background: #f0f9ff; border: 2px dashed #1890ff; border-radius: 8px; padding: 20px; text-align: center; margin: 20px 0; }
                    .code { font-size: 36px; font-weight: bold; color: #1890ff; letter-spacing: 8px; }
                    .tip { color: #666; font-size: 14px; text-align: center; }
                    .footer { text-align: center; margin-top: 30px; color: #999; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">⚖️</div>
                        <div class="title">法律助手</div>
                    </div>
                    <div class="code-box">
                        <div class="code">%s</div>
                    </div>
                    <div class="tip">验证码有效期为 5 分钟，请勿泄露给他人</div>
                    <div class="footer">
                        <p>此邮件由系统自动发送，请勿回复</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(code);
    }
}
