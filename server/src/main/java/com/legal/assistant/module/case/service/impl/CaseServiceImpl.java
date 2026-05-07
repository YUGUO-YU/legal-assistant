package com.legal.assistant.module.case.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.legal.assistant.common.exception.BusinessException;
import com.legal.assistant.common.result.ResultCode;
import com.legal.assistant.module.case.dto.*;
import com.legal.assistant.module.case.entity.CaseBookmark;
import com.legal.assistant.module.case.mapper.CaseBookmarkMapper;
import com.legal.assistant.module.case.service.CaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseServiceImpl implements CaseService {

    private final CaseBookmarkMapper caseBookmarkMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CASE_CACHE_PREFIX = "case:search:";
    private static final long CACHE_EXPIRE = 3600L;

    @Override
    public CaseSearchResponse search(CaseSearchRequest request) {
        String cacheKey = CASE_CACHE_PREFIX + request.hashCode();

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return (CaseSearchResponse) cached;
        }

        List<CaseSearchResponse.CaseItem> mockCases = buildMockCases(request.getKeyword());

        CaseSearchResponse response = CaseSearchResponse.builder()
            .list(mockCases)
            .total((long) mockCases.size())
            .page(request.getPage() != null ? request.getPage() : 1)
            .pageSize(request.getPageSize() != null ? request.getPageSize() : 20)
            .build();

        redisTemplate.opsForValue().set(cacheKey, response, CACHE_EXPIRE, TimeUnit.SECONDS);

        return response;
    }

    @Override
    public CaseDetailResponse getDetail(String id) {
        return CaseDetailResponse.builder()
            .id(id)
            .caseNumber("(2024)京01民终12345号")
            .title("原告张三与被告李四合同纠纷案")
            .court("北京市第一中级人民法院")
            .caseType("民事")
            .procedure("二审")
            .judgmentDate("2024-06-15")
            .plaintiff("张三")
            .defendant("李四")
            .judge("王法官")
            .lawyer("赵律师")
            .content("本案现已审理终结...")
            .relatedLaws(List.of("《中华人民共和国民法典》第三编合同", "《中华人民共和国民事诉讼法》"))
            .similarCases(buildMockCases(null))
            .bookmarked(false)
            .build();
    }

    @Override
    public CaseSearchResponse getSimilarCases(String id) {
        return CaseSearchResponse.builder()
            .list(buildMockCases(null))
            .total(10L)
            .page(1)
            .pageSize(10)
            .build();
    }

    @Override
    public void bookmark(String userId, BookmarkCaseRequest request) {
        CaseBookmark bookmark = new CaseBookmark();
        bookmark.setUserId(userId);
        bookmark.setExternalId(request.getExternalId());
        bookmark.setSource(request.getSource());
        bookmark.setTitle(request.getTitle());
        bookmark.setNote(request.getNote());

        caseBookmarkMapper.insert(bookmark);
    }

    @Override
    public List<CaseSearchResponse.CaseItem> getBookmarks(String userId) {
        LambdaQueryWrapper<CaseBookmark> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CaseBookmark::getUserId, userId)
            .orderByDesc(CaseBookmark::getCreatedAt);

        return caseBookmarkMapper.selectList(wrapper).stream()
            .map(b -> CaseSearchResponse.CaseItem.builder()
                .id(b.getId())
                .externalId(b.getExternalId())
                .title(b.getTitle())
                .source(b.getSource())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public void removeBookmark(String userId, String id) {
        LambdaQueryWrapper<CaseBookmark> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CaseBookmark::getId, id)
            .eq(CaseBookmark::getUserId, userId);

        caseBookmarkMapper.delete(wrapper);
    }

    private List<CaseSearchResponse.CaseItem> buildMockCases(String keyword) {
        List<CaseSearchResponse.CaseItem> cases = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            cases.add(CaseSearchResponse.CaseItem.builder()
                .id("case-" + i)
                .caseNumber("(2024)京01民终" + (10000 + i) + "号")
                .title(keyword != null ? "涉及" + keyword + "的案件" : "合同纠纷案")
                .court("北京市第" + (i % 3 + 1) + "中级人民法院")
                .caseType("民事")
                .procedure(i % 2 == 0 ? "一审" : "二审")
                .judgmentDate("2024-0" + (i % 9 + 1) + "-15")
                .plaintiff("原告" + i)
                .defendant("被告" + i)
                .build());
        }
        return cases;
    }
}