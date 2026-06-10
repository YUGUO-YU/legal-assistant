package com.legal.assistant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mail")
public class MailConfig {
    private boolean enabled = false;
    private String host;
    private int port = 587;
    private String username;
    private String password;
    private String from;
}
