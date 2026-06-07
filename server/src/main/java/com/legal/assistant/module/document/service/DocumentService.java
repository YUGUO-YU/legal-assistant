package com.legal.assistant.module.document.service;

import com.legal.assistant.module.document.dto.DocumentGenerateRequest;
import com.legal.assistant.module.document.entity.DocumentTemplate;

import java.util.List;
import java.util.Map;

/**
 * 文书服务接口
 */
public interface DocumentService {
    
    /**
     * 获取所有公开模板
     */
    List<DocumentTemplate> getPublicTemplates();
    
    /**
     * 按分类获取模板
     */
    List<DocumentTemplate> getTemplatesByCategory(String category);
    
    /**
     * 获取模板详情
     */
    DocumentTemplate getTemplateById(Long templateId);
    
    /**
     * 生成文书
     */
    String generateDocument(Long userId, DocumentGenerateRequest request) throws Exception;
    
    /**
     * 下载文件
     */
    byte[] downloadFile(String filePath) throws Exception;
    
    /**
     * 获取用户生成历史
     */
    List<Map<String, Object>> getUserHistory(Long userId);
}
