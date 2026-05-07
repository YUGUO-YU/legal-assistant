package com.legal.assistant.module.law.service.impl;

import com.legal.assistant.module.law.dto.*;
import com.legal.assistant.module.law.service.LawService;
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
public class LawServiceImpl implements LawService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LAW_CACHE_PREFIX = "law:search:";
    private static final long CACHE_EXPIRE = 86400L;

    @Override
    public LawSearchResponse search(LawSearchRequest request) {
        String cacheKey = LAW_CACHE_PREFIX + request.hashCode();

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return (LawSearchResponse) cached;
        }

        List<LawSearchResponse.LawItem> mockLaws = buildMockLaws(request.getKeyword());

        LawSearchResponse response = LawSearchResponse.builder()
            .list(mockLaws)
            .total((long) mockLaws.size())
            .page(request.getPage() != null ? request.getPage() : 1)
            .pageSize(request.getPageSize() != null ? request.getPageSize() : 20)
            .build();

        redisTemplate.opsForValue().set(cacheKey, response, CACHE_EXPIRE, TimeUnit.SECONDS);

        return response;
    }

    @Override
    public LawDetailResponse getDetail(String id) {
        return LawDetailResponse.builder()
            .id(id)
            .title("《中华人民共和国民法典》")
            .level("law")
            .organ("全国人民代表大会")
            .issueDate("2020-05-28")
            .effectiveDate("2021-01-01")
            .status("effective")
            .content("《中华人民共和国民法典》共七编、一千二百六十条...")
            .chapters(List.of(
                LawDetailResponse.Chapter.builder()
                    .title("第一编 总则")
                    .articles("第1条至第204条")
                    .build(),
                LawDetailResponse.Chapter.builder()
                    .title("第二编 物权")
                    .articles("第205条至第462条")
                    .build()
            ))
            .relatedLaws(List.of(
                LawDetailResponse.RelatedLaw.builder()
                    .id("related-1")
                    .title("《最高人民法院关于适用〈中华人民共和国民法典〉合同编通则若干问题的解释》")
                    .build()
            ))
            .build();
    }

    @Override
    public LawSearchResponse getRelated(String id) {
        return LawSearchResponse.builder()
            .list(buildMockLaws(null))
            .total(5L)
            .page(1)
            .pageSize(5)
            .build();
    }

    private List<LawSearchResponse.LawItem> buildMockLaws(String keyword) {
        List<LawSearchResponse.LawItem> laws = new ArrayList<>();
        String[] titles = {
            "《中华人民共和国民法典》",
            "《中华人民共和国刑法》",
            "《中华人民共和国公司法》",
            "《中华人民共和国劳动法》",
            "《中华人民共和国合同法》"
        };

        for (int i = 0; i < titles.length; i++) {
            String title = keyword != null && !keyword.isEmpty()
                ? titles[i].replace("法", keyword + "法")
                : titles[i];
            laws.add(LawSearchResponse.LawItem.builder()
                .id("law-" + (i + 1))
                .title(title)
                .level(i < 2 ? "law" : (i < 4 ? "regulation" : "local"))
                .organ(i < 2 ? "全国人民代表大会" : "国务院")
                .issueDate("202" + (i % 4) + "-0" + (i % 9 + 1) + "-28")
                .effectiveDate("202" + (i % 4 + 1) + "-01-01")
                .status("effective")
                .build());
        }
        return laws;
    }
}