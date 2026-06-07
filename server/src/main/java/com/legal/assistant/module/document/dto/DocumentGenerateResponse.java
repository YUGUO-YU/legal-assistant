package com.legal.assistant.module.document.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 生成文书响应
 */
@Data
@AllArgsConstructor
public class DocumentGenerateResponse {
    
    private String downloadUrl;
    
    private String filePath;
    
    private Integer expiresIn;
}
