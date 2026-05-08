package com.legal.assistant.mcp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.legal.assistant.mcp.handler.LegalMcpHandler;
import com.legal.assistant.module.cases.service.CaseService;
import com.legal.assistant.module.company.service.CompanyService;
import com.legal.assistant.module.law.service.LawService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final CaseService caseService;
    private final LawService lawService;
    private final CompanyService companyService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(mcpHandler(), "/mcp/legal")
            .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler mcpHandler() {
        return new LegalMcpHandler(caseService, lawService, companyService);
    }
}