package com.legal.assistant.module.rag.service;

import com.legal.assistant.module.legal.dto.LegalSearchRequest;
import com.legal.assistant.module.legal.dto.LegalSearchResponse;
import com.legal.assistant.module.legal.service.LegalDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LegalRagService {

    private final LegalDataService legalDataService;

    public Map<String, Object> retrieveContext(String query, int maxLaws, int maxCases) {
        Map<String, Object> context = new HashMap<>();
        List<Map<String, String>> lawSources = new ArrayList<>();
        List<Map<String, String>> caseSources = new ArrayList<>();

        try {
            LegalSearchRequest searchRequest = LegalSearchRequest.builder()
                    .keyword(extractLegalKeywords(query))
                    .pageSize(20)
                    .build();

            LegalSearchResponse lawsResp = legalDataService.searchLaws(searchRequest);
            if (lawsResp != null && lawsResp.getLaws() != null) {
                lawSources = lawsResp.getLaws().stream()
                        .limit(maxLaws)
                        .map(law -> {
                            Map<String, String> map = new HashMap<>();
                            map.put("id", law.getId());
                            map.put("name", law.getName());
                            map.put("content", truncateContent(law.getContent(), 500));
                            map.put("source", law.getSource());
                            return map;
                        })
                        .collect(Collectors.toList());
            }

            LegalSearchResponse casesResp = legalDataService.searchCases(searchRequest);
            if (casesResp != null && casesResp.getCases() != null) {
                caseSources = casesResp.getCases().stream()
                        .limit(maxCases)
                        .map(c -> {
                            Map<String, String> map = new HashMap<>();
                            map.put("id", c.getId());
                            map.put("title", c.getTitle());
                            map.put("caseNumber", c.getCaseNumber());
                            map.put("content", truncateContent(c.getContent(), 500));
                            map.put("source", c.getSource());
                            return map;
                        })
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("RAG retrieval error", e);
        }

        context.put("lawSources", lawSources);
        context.put("caseSources", caseSources);
        return context;
    }

    public void enrichChatRequest(Map<String, Object> context, int maxLaws, int maxCases) {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> lawSources = (List<Map<String, String>>) context.get("lawSources");
        @SuppressWarnings("unchecked")
        List<Map<String, String>> caseSources = (List<Map<String, String>>) context.get("caseSources");

        if (lawSources != null && lawSources.size() > maxLaws) {
            context.put("lawSources", lawSources.subList(0, maxLaws));
        }
        if (caseSources != null && caseSources.size() > maxCases) {
            context.put("caseSources", caseSources.subList(0, maxCases));
        }
    }

    private String extractLegalKeywords(String query) {
        if (query == null || query.isEmpty()) {
            return "";
        }

        String[] legalKeywords = {
                "合同", "借贷", "借款", "买卖", "租赁", "劳动", "劳动合同", "解除合同",
                "违约金", "赔偿", "损失", "侵权", "离婚", "抚养", "财产", "分割",
                "交通事故", "保险", "医疗", "工伤", "民事", "刑事", "行政", "诉讼",
                "上诉", "申诉", "执行", "查封", "冻结", "抵押", "担保", "质押"
        };

        for (String keyword : legalKeywords) {
            if (query.contains(keyword)) {
                return keyword;
            }
        }

        return query.length() > 10 ? query.substring(0, 10) : query;
    }

    private String truncateContent(String content, int maxLength) {
        if (content == null) {
            return "";
        }
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
}
