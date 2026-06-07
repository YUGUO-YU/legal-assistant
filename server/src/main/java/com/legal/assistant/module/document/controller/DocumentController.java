package com.legal.assistant.module.document.controller;

import com.legal.assistant.common.result.Result;
import com.legal.assistant.module.document.dto.DocumentGenerateRequest;
import com.legal.assistant.module.document.entity.DocumentTemplate;
import com.legal.assistant.module.document.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文书生成控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {
    
    private final DocumentService documentService;
    
    /**
     * 获取模板列表
     */
    @GetMapping("/templates")
    public Result<List<DocumentTemplate>> getTemplates() {
        List<DocumentTemplate> templates = documentService.getPublicTemplates();
        return Result.success(templates);
    }
    
    /**
     * 按分类获取模板
     */
    @GetMapping("/templates/category/{category}")
    public Result<List<DocumentTemplate>> getTemplatesByCategory(@PathVariable String category) {
        List<DocumentTemplate> templates = documentService.getTemplatesByCategory(category);
        return Result.success(templates);
    }
    
    /**
     * 获取模板详情
     */
    @GetMapping("/templates/{id}")
    public Result<DocumentTemplate> getTemplate(@PathVariable Long id) {
        DocumentTemplate template = documentService.getTemplateById(id);
        return Result.success(template);
    }
    
    /**
     * 生成文书
     */
    @PostMapping("/generate")
    public Result<Map<String, String>> generate(
            @Valid @RequestBody DocumentGenerateRequest request,
            @RequestAttribute("userId") Long userId) {
        try {
            String filePath = documentService.generateDocument(userId, request);
            
            Map<String, String> response = new HashMap<>();
            response.put("downloadUrl", "/api/v1/documents/download?file=" + 
                URLEncoder.encode(filePath, StandardCharsets.UTF_8.toString()));
            response.put("filePath", filePath);
            response.put("expiresIn", String.valueOf(24 * 60 * 60));
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("生成文书失败", e);
            return Result.error(1004, "生成失败：" + e.getMessage());
        }
    }
    
    /**
     * 下载文书
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam String file) {
        try {
            byte[] fileContent = documentService.downloadFile(file);
            
            String fileName = file.substring(file.lastIndexOf("/") + 1);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", 
                URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()));
            
            return new ResponseEntity<>(fileContent, headers, org.springframework.http.HttpStatus.OK);
        } catch (Exception e) {
            log.error("下载文件失败：file={}", file, e);
            return new ResponseEntity<>(org.springframework.http.HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * 获取用户生成历史
     */
    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getHistory(@RequestAttribute("userId") Long userId) {
        List<Map<String, Object>> history = documentService.getUserHistory(userId);
        return Result.success(history);
    }
}
