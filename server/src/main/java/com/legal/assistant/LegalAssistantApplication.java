package com.legal.assistant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.legal.assistant.module.*.mapper")
public class LegalAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(LegalAssistantApplication.class, args);
    }
}