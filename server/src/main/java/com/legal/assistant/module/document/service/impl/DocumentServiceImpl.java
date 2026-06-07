package com.legal.assistant.module.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepoove.poi.XWPFTemplate;
import com.legal.assistant.common.exception.BusinessException;
import com.legal.assistant.module.document.dto.DocumentGenerateRequest;
import com.legal.assistant.module.document.entity.DocumentHistory;
import com.legal.assistant.module.document.entity.DocumentTemplate;
import com.legal.assistant.module.document.mapper.DocumentHistoryMapper;
import com.legal.assistant.module.document.mapper.DocumentTemplateMapper;
import com.legal.assistant.module.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    
    private final DocumentTemplateMapper templateMapper;
    private final DocumentHistoryMapper historyMapper;
    
    @Value("${legal.assistant.document.template-dir:/workspace/server/templates}")
    private String templateDir;
    
    @Value("${legal.assistant.document.output-dir:/workspace/server/output}")
    private String outputDir;
    
    @Value("${legal.assistant.document.file-retention-days:1}")
    private Integer fileRetentionDays = 1;
    
    @Override
    public List<DocumentTemplate> getPublicTemplates() {
        LambdaQueryWrapper<DocumentTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentTemplate::getIsPublic, true)
               .eq(DocumentTemplate::getDeleted, false)
               .orderByAsc(DocumentTemplate::getCategory);
        return templateMapper.selectList(wrapper);
    }
    
    @Override
    public List<DocumentTemplate> getTemplatesByCategory(String category) {
        LambdaQueryWrapper<DocumentTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentTemplate::getCategory, category)
               .eq(DocumentTemplate::getIsPublic, true)
               .eq(DocumentTemplate::getDeleted, false);
        return templateMapper.selectList(wrapper);
    }
    
    @Override
    public DocumentTemplate getTemplateById(Long templateId) {
        DocumentTemplate template = templateMapper.selectById(templateId);
        if (template == null || template.getDeleted()) {
            throw new BusinessException(1001, "模板不存在或已被删除");
        }
        return template;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String generateDocument(Long userId, DocumentGenerateRequest request) throws Exception {
        log.info("开始生成文书：userId={}, templateId={}", userId, request.getTemplateId());
        
        // 1. 查询模板
        DocumentTemplate template = getTemplateById(request.getTemplateId());
        
        // 2. 拼接完整模板路径
        String templateFilePath = templateDir + template.getFilePath();
        log.info("模板文件路径：{}", templateFilePath);
        
        // 3. 检查模板文件是否存在
        Path templatePath = Paths.get(templateFilePath);
        if (!Files.exists(templatePath)) {
            throw new BusinessException(1002, "模板文件不存在：" + templateFilePath);
        }
        
        // 4. 准备模板数据
        Map<String, Object> templateData = prepareTemplateData(request.getData());
        
        // 5. 生成文件名
        String fileName = generateFileName(template.getName());
        String outputPath = outputDir + "/" + fileName;
        
        // 6. 确保输出目录存在
        Path outputDirPath = Paths.get(outputDir);
        if (!Files.exists(outputDirPath)) {
            Files.createDirectories(outputDirPath);
        }
        
        // 7. 使用 poi-tl 渲染模板
        try {
            XWPFTemplate.compile(templateFilePath)
                .render(templateData)
                .writeToFile(outputPath);
            
            log.info("文书生成成功：{}", outputPath);
        } catch (Exception e) {
            log.error("文书生成失败：templateId={}", request.getTemplateId(), e);
            throw new BusinessException(1004, "文档生成失败：" + e.getMessage());
        }
        
        // 8. 保存历史记录
        saveHistory(userId, template.getId(), fileName, outputPath, request.getData());
        
        // 9. 返回文件路径
        return outputPath;
    }
    
    @Override
    public byte[] downloadFile(String filePath) throws Exception {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new BusinessException(1005, "文件不存在或已过期");
        }
        return Files.readAllBytes(path);
    }
    
    @Override
    public List<Map<String, Object>> getUserHistory(Long userId) {
        List<DocumentHistory> histories = historyMapper.selectByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (DocumentHistory history : histories) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", history.getId());
            item.put("templateId", history.getTemplateId());
            item.put("fileName", history.getFileName());
            item.put("createdAt", history.getCreatedAt());
            
            // 判断文件是否还存在
            Path path = Paths.get(history.getFilePath());
            item.put("fileExists", Files.exists(path));
            
            result.add(item);
        }
        
        return result;
    }
    
    /**
     * 准备模板数据
     */
    private Map<String, Object> prepareTemplateData(Map<String, Object> inputData) {
        Map<String, Object> data = new HashMap<>(inputData);
        
        // 格式化日期
        data.forEach((key, value) -> {
            if (value instanceof Date) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
                data.put(key, sdf.format(value));
            } else if (value instanceof LocalDateTime) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy 年 MM 月 dd 日");
                data.put(key, ((LocalDateTime) value).format(formatter));
            }
        });
        
        return data;
    }
    
    /**
     * 生成文件名
     */
    private String generateFileName(String templateName) {
        String timestamp = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        );
        String random = UUID.randomUUID().toString().substring(0, 8);
        return String.format("%s_%s_%s.docx", templateName, timestamp, random);
    }
    
    /**
     * 保存历史记录
     */
    private void saveHistory(Long userId, Long templateId, String fileName, 
                            String filePath, Map<String, Object> data) {
        DocumentHistory history = new DocumentHistory();
        history.setUserId(userId);
        history.setTemplateId(templateId);
        history.setFileName(fileName);
        history.setFilePath(filePath);
        history.setTemplateData(data);
        historyMapper.insert(history);
    }
}
