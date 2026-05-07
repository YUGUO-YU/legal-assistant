package com.legal.assistant.module.company.service.impl;

import com.legal.assistant.module.company.dto.*;
import com.legal.assistant.module.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String COMPANY_CACHE_PREFIX = "company:";
    private static final long CACHE_EXPIRE = 43200L;

    @Override
    public CompanySearchResponse search(CompanySearchRequest request) {
        String cacheKey = COMPANY_CACHE_PREFIX + "search:" + request.hashCode();

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return (CompanySearchResponse) cached;
        }

        List<CompanySearchResponse.CompanyItem> mockCompanies = buildMockCompanies(request.getKeyword());

        CompanySearchResponse response = CompanySearchResponse.builder()
            .list(mockCompanies)
            .total((long) mockCompanies.size())
            .page(request.getPage() != null ? request.getPage() : 1)
            .pageSize(request.getPageSize() != null ? request.getPageSize() : 20)
            .build();

        redisTemplate.opsForValue().set(cacheKey, response, CACHE_EXPIRE, TimeUnit.SECONDS);

        return response;
    }

    @Override
    public CompanyDetailResponse getDetail(String id) {
        return buildMockDetail(id);
    }

    @Override
    public CompanyDetailResponse getShareholders(String id) {
        return buildMockDetail(id);
    }

    @Override
    public CompanyDetailResponse getRiskInfo(String id) {
        return buildMockDetail(id);
    }

    @Override
    public Object getGraph(String id) {
        return List.of(
            List.of("company", "invest", "sub1"),
            List.of("company", "legal", "person1"),
            List.of("sub1", "invest", "sub2")
        );
    }

    private CompanyDetailResponse buildMockDetail(String id) {
        return CompanyDetailResponse.builder()
            .id(id)
            .name("示例科技有限公司")
            .creditCode("91110000XXXXXXXX")
            .legalPerson("张三")
            .capital("1000万元")
            .establishDate("2010-01-01")
            .status("存续")
            .businessScope("技术开发、技术咨询、技术服务；软件开发；计算机系统服务...")
            .shareholders(List.of(
                CompanyDetailResponse.Shareholder.builder()
                    .name("张三")
                    .sharePercent(60.0)
                    .capital("600万元")
                    .build(),
                CompanyDetailResponse.Shareholder.builder()
                    .name("李四")
                    .sharePercent(40.0)
                    .capital("400万元")
                    .build()
            ))
            .risks(List.of(
                CompanyDetailResponse.RiskInfo.builder()
                    .type("行政处罚")
                    .description("违反广告法规定")
                    .date("2024-03-15")
                    .build()
            ))
            .lawsuits(List.of(
                CompanyDetailResponse.lawsuit.builder()
                    .caseNumber("(2024)京01民初1234号")
                    .title("合同纠纷案")
                    .date("2024-06-01")
                    .build()
            ))
            .build();
    }

    private List<CompanySearchResponse.CompanyItem> buildMockCompanies(String keyword) {
        List<CompanySearchResponse.CompanyItem> companies = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            companies.add(CompanySearchResponse.CompanyItem.builder()
                .id("company-" + i)
                .name(keyword != null ? keyword + "科技公司" : "示例科技有限公司" + i)
                .creditCode("91110000" + String.format("%06d", i))
                .legalPerson("张三" + i)
                .capital((1000 + i * 100) + "万元")
                .establishDate("201" + (i % 5) + "-0" + (i % 9 + 1) + "-01")
                .status("存续")
                .build());
        }
        return companies;
    }
}