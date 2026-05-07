package com.legal.assistant.module.lead.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.legal.assistant.common.exception.BusinessException;
import com.legal.assistant.common.result.ResultCode;
import com.legal.assistant.module.lead.dto.*;
import com.legal.assistant.module.lead.entity.Lead;
import com.legal.assistant.module.lead.mapper.LeadMapper;
import com.legal.assistant.module.lead.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final LeadMapper leadMapper;

    @Override
    public LeadListResponse getList(String userId, String status, Integer page, Integer pageSize) {
        page = page == null ? 1 : page;
        pageSize = pageSize == null ? 20 : pageSize;

        LambdaQueryWrapper<Lead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Lead::getUserId, userId);

        if (StringUtils.hasText(status)) {
            wrapper.eq(Lead::getStatus, status);
        }

        wrapper.orderByDesc(Lead::getUpdatedAt);

        Page<Lead> pageResult = leadMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<LeadResponse> list = pageResult.getRecords().stream()
            .map(LeadResponse::fromEntity)
            .collect(Collectors.toList());

        return LeadListResponse.builder()
            .list(list)
            .total(pageResult.getTotal())
            .page(page)
            .pageSize(pageSize)
            .build();
    }

    @Override
    public LeadResponse getById(String userId, String id) {
        Lead lead = getLead(userId, id);
        return LeadResponse.fromEntity(lead);
    }

    @Override
    @Transactional
    public LeadResponse create(String userId, CreateLeadRequest request) {
        Lead lead = new Lead();
        lead.setUserId(userId);
        lead.setTitle(request.getTitle());
        lead.setDescription(request.getDescription());
        lead.setSource(request.getSource());
        lead.setTags(request.getTags() != null ? String.join(",", request.getTags()) : null);
        lead.setStatus("new");

        leadMapper.insert(lead);

        return LeadResponse.fromEntity(lead);
    }

    @Override
    @Transactional
    public LeadResponse update(String userId, String id, UpdateLeadRequest request) {
        Lead lead = getLead(userId, id);

        if (request.getTitle() != null) {
            lead.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            lead.setDescription(request.getDescription());
        }
        if (request.getSource() != null) {
            lead.setSource(request.getSource());
        }
        if (request.getTags() != null) {
            lead.setTags(String.join(",", request.getTags()));
        }
        if (request.getStatus() != null) {
            lead.setStatus(request.getStatus());
        }

        leadMapper.updateById(lead);

        return LeadResponse.fromEntity(lead);
    }

    @Override
    @Transactional
    public void updateStatus(String userId, String id, String status) {
        Lead lead = getLead(userId, id);
        lead.setStatus(status);
        leadMapper.updateById(lead);
    }

    @Override
    @Transactional
    public void delete(String userId, String id) {
        Lead lead = getLead(userId, id);
        leadMapper.deleteById(lead);
    }

    private Lead getLead(String userId, String id) {
        Lead lead = leadMapper.selectOne(
            new LambdaQueryWrapper<Lead>()
                .eq(Lead::getId, id)
                .eq(Lead::getUserId, userId)
        );

        if (lead == null) {
            throw new BusinessException(ResultCode.LEAD_NOT_FOUND);
        }

        return lead;
    }
}