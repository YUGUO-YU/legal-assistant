package com.legal.assistant.module.document.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * 生成文书请求
 */
@Data
public class DocumentGenerateRequest {
    
    @NotNull(message = "模板 ID 不能为空")
    private Long templateId;
    
    @NotNull(message = "模板数据不能为空")
    private Map<String, Object> data;
}
