package com.legal.assistant.module.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.legal.assistant.common.exception.BusinessException;
import com.legal.assistant.common.result.ResultCode;
import com.legal.assistant.module.document.dto.*;
import com.legal.assistant.module.document.entity.Document;
import com.legal.assistant.module.document.entity.DocumentVersion;
import com.legal.assistant.module.document.mapper.DocumentMapper;
import com.legal.assistant.module.document.mapper.DocumentVersionMapper;
import com.legal.assistant.module.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentMapper documentMapper;
    private final DocumentVersionMapper documentVersionMapper;

    @Override
    public DocumentListResponse getList(String userId, String type, String status, String keyword, Integer page, Integer pageSize) {
        page = page == null ? 1 : page;
        pageSize = pageSize == null ? 20 : pageSize;

        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Document::getUserId, userId);

        if (type != null && !type.isEmpty()) {
            wrapper.eq(Document::getDocType, type);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Document::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Document::getTitle, keyword);
        }

        wrapper.orderByDesc(Document::getUpdatedAt);

        Page<Document> pageResult = documentMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<DocumentResponse> list = pageResult.getRecords().stream()
            .map(DocumentResponse::fromEntity)
            .collect(Collectors.toList());

        return DocumentListResponse.builder()
            .list(list)
            .total(pageResult.getTotal())
            .page(page)
            .pageSize(pageSize)
            .build();
    }

    @Override
    public DocumentResponse getById(String userId, String id) {
        Document doc = getDocument(userId, id);
        return DocumentResponse.fromEntity(doc);
    }

    @Override
    @Transactional
    public DocumentResponse create(String userId, CreateDocumentRequest request) {
        Document doc = new Document();
        doc.setUserId(userId);
        doc.setTitle(request.getTitle());
        doc.setDocType(request.getDocType());
        doc.setContent(request.getContent() != null ? request.getContent() : "");
        doc.setCaseId(request.getCaseId());
        doc.setStatus("draft");
        doc.setVersion(1);

        documentMapper.insert(doc);

        saveVersion(doc.getId(), doc.getContent(), 1);

        return DocumentResponse.fromEntity(doc);
    }

    @Override
    @Transactional
    public DocumentResponse update(String userId, String id, UpdateDocumentRequest request) {
        Document doc = getDocument(userId, id);

        if (request.getTitle() != null) {
            doc.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            int newVersion = doc.getVersion() + 1;
            doc.setVersion(newVersion);
            saveVersion(doc.getId(), request.getContent(), newVersion);
            doc.setContent(request.getContent());
        }
        if (request.getStatus() != null) {
            doc.setStatus(request.getStatus());
        }

        documentMapper.updateById(doc);

        return DocumentResponse.fromEntity(doc);
    }

    @Override
    @Transactional
    public void delete(String userId, String id) {
        Document doc = getDocument(userId, id);
        documentMapper.deleteById(doc);
    }

    @Override
    public List<DocumentResponse> getVersions(String userId, String id) {
        getDocument(userId, id);

        LambdaQueryWrapper<DocumentVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentVersion::getDocumentId, id)
            .orderByDesc(DocumentVersion::getVersion);

        return documentVersionMapper.selectList(wrapper).stream()
            .map(v -> DocumentResponse.builder()
                .id(v.getId())
                .content(v.getContent())
                .version(v.getVersion())
                .createdAt(v.getCreatedAt() != null ? v.getCreatedAt().toString() : null)
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public String exportDocument(String userId, String id, String format) {
        Document doc = getDocument(userId, id);

        if (!"pdf".equals(format) && !"word".equals(format)) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }

        return doc.getContent();
    }

    private Document getDocument(String userId, String id) {
        Document doc = documentMapper.selectOne(
            new LambdaQueryWrapper<Document>()
                .eq(Document::getId, id)
                .eq(Document::getUserId, userId)
        );

        if (doc == null) {
            throw new BusinessException(ResultCode.DOCUMENT_NOT_FOUND);
        }

        return doc;
    }

    private void saveVersion(String documentId, String content, int version) {
        DocumentVersion versionRecord = new DocumentVersion();
        versionRecord.setDocumentId(documentId);
        versionRecord.setContent(content);
        versionRecord.setVersion(version);
        documentVersionMapper.insert(versionRecord);
    }
}