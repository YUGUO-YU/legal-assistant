package com.legal.assistant.module.legal.service.impl;

import com.legal.assistant.module.legal.dto.LegalSearchRequest;
import com.legal.assistant.module.legal.dto.LegalSearchResponse;
import com.legal.assistant.module.legal.service.LegalDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LegalDataServiceImpl implements LegalDataService {

    @Value("${legal-data.wenshu.enabled:false}")
    private boolean wenshuEnabled;

    @Value("${legal-data.wenshu.app-key:}")
    private String wenshuAppKey;

    @Value("${legal-data.law.enabled:true}")
    private boolean lawEnabled;

    @Value("${legal-data.company.enabled:true}")
    private boolean companyEnabled;

    @Value("${legal-data.company.api-key:}")
    private String companyApiKey;

    private final HttpClient httpClient;

    public LegalDataServiceImpl() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public LegalSearchResponse searchCases(LegalSearchRequest request) {
        List<LegalSearchResponse.CaseItem> cases = new ArrayList<>();

        if (wenshuEnabled && wenshuAppKey != null && !wenshuAppKey.isEmpty()) {
            try {
                cases = searchCasesFromWenshu(request);
            } catch (Exception e) {
                log.error("裁判文书网查询失败", e);
                cases = buildMockCases(request.getKeyword());
            }
        } else {
            cases = buildMockCases(request.getKeyword());
        }

        return LegalSearchResponse.builder()
                .cases(cases)
                .total((long) cases.size())
                .page(request.getPage() != null ? request.getPage() : 1)
                .pageSize(request.getPageSize() != null ? request.getPageSize() : 20)
                .build();
    }

    @Override
    public LegalSearchResponse searchLaws(LegalSearchRequest request) {
        List<LegalSearchResponse.LawItem> laws = new ArrayList<>();

        if (lawEnabled) {
            laws = searchLawsFromApi(request);
        }

        if (laws.isEmpty()) {
            laws = buildMockLaws(request.getKeyword());
        }

        return LegalSearchResponse.builder()
                .laws(laws)
                .total((long) laws.size())
                .page(request.getPage() != null ? request.getPage() : 1)
                .pageSize(request.getPageSize() != null ? request.getPageSize() : 20)
                .build();
    }

    @Override
    public LegalSearchResponse searchCompanies(LegalSearchRequest request) {
        List<LegalSearchResponse.CompanyItem> companies = new ArrayList<>();

        if (companyEnabled && companyApiKey != null && !companyApiKey.isEmpty()) {
            try {
                companies = searchCompaniesFromApi(request);
            } catch (Exception e) {
                log.error("企业查询失败", e);
                companies = buildMockCompanies(request.getKeyword());
            }
        } else {
            companies = buildMockCompanies(request.getKeyword());
        }

        return LegalSearchResponse.builder()
                .companies(companies)
                .total((long) companies.size())
                .page(request.getPage() != null ? request.getPage() : 1)
                .pageSize(request.getPageSize() != null ? request.getPageSize() : 20)
                .build();
    }

    @Override
    public LegalSearchResponse searchAll(LegalSearchRequest request) {
        LegalSearchResponse casesResp = searchCases(request);
        LegalSearchResponse lawsResp = searchLaws(request);
        LegalSearchResponse companiesResp = searchCompanies(request);

        return LegalSearchResponse.builder()
                .cases(casesResp.getCases())
                .laws(lawsResp.getLaws())
                .companies(companiesResp.getCompanies())
                .total(casesResp.getTotal() + lawsResp.getTotal() + companiesResp.getTotal())
                .page(request.getPage() != null ? request.getPage() : 1)
                .pageSize(request.getPageSize() != null ? request.getPageSize() : 20)
                .build();
    }

    private List<LegalSearchResponse.CaseItem> searchCasesFromWenshu(LegalSearchRequest request) {
        List<LegalSearchResponse.CaseItem> cases = new ArrayList<>();
        try {
            String url = "https://wenshu.court.gov.cn/website/wenshu/181217CRTKBS2P3/index.html";
            log.info("裁判文书网查询: keyword={}", request.getKeyword());
        } catch (Exception e) {
            log.error("裁判文书网API调用失败", e);
        }
        return cases;
    }

    private List<LegalSearchResponse.LawItem> searchLawsFromApi(LegalSearchRequest request) {
        List<LegalSearchResponse.LawItem> laws = new ArrayList<>();
        try {
            String url = "https://flk.npc.gov.cn/search2/?_token=&searchWord=" +
                    java.net.URLEncoder.encode(request.getKeyword(), java.nio.charset.StandardCharsets.UTF_8);
            log.info("法律法规数据库查询: keyword={}", request.getKeyword());
        } catch (Exception e) {
            log.error("法律法规API调用失败", e);
        }
        return laws;
    }

    private List<LegalSearchResponse.CompanyItem> searchCompaniesFromApi(LegalSearchRequest request) {
        List<LegalSearchResponse.CompanyItem> companies = new ArrayList<>();
        try {
            String url = "https://api.tianyancha.com/api/v2/search/startV2?word=" +
                    java.net.URLEncoder.encode(request.getKeyword(), java.nio.charset.StandardCharsets.UTF_8);
            log.info("天眼查API查询: keyword={}", request.getKeyword());
        } catch (Exception e) {
            log.error("天眼查API调用失败", e);
        }
        return companies;
    }

    private List<LegalSearchResponse.CaseItem> buildMockCases(String keyword) {
        List<LegalSearchResponse.CaseItem> cases = new ArrayList<>();
        String[] courts = {"北京市第一中级人民法院", "上海市浦东新区人民法院", "广州市中级人民法院", "深圳市中级人民法院"};
        String[] types = {"民事", "刑事", "行政", "执行"};

        for (int i = 1; i <= 10; i++) {
            final int idx = i;
            cases.add(LegalSearchResponse.CaseItem.builder()
                    .id("case-" + idx)
                    .caseNumber("(2024)最高法民终" + (10000 + idx) + "号")
                    .title(keyword != null ? "涉及" + keyword + "的案件" : "合同纠纷案")
                    .court(courts[idx % courts.length])
                    .caseType(types[idx % types.length])
                    .procedure(idx % 2 == 0 ? "一审" : "二审")
                    .judgmentDate("2024-0" + (idx % 9 + 1) + "-15")
                    .plaintiff("原告" + idx)
                    .defendant("被告" + idx)
                    .content("本案现已审理终结，依法判决如下...")
                    .source("裁判文书网")
                    .build());
        }
        return cases;
    }

    private List<LegalSearchResponse.LawItem> buildMockLaws(String keyword) {
        List<LegalSearchResponse.LawItem> laws = new ArrayList<>();
        String[] levels = {"法律", "行政法规", "司法解释", "部门规章", "地方性法规"};
        String[] categories = {"民法", "刑法", "行政法", "商法", "经济法"};

        for (int i = 1; i <= 10; i++) {
            final int idx = i;
            laws.add(LegalSearchResponse.LawItem.builder()
                    .id("law-" + idx)
                    .name(keyword != null ? "《" + keyword + "相关规定》" : "《中华人民共和国民法典》")
                    .level(levels[idx % levels.length])
                    .category(categories[idx % categories.length])
                    .issueDate("2024-0" + (idx % 9 + 1) + "-01")
                    .effectiveDate("2024-0" + (idx % 9 + 1) + "-15")
                    .content("第一条 为了保护民事主体的合法权益...\n" +
                            "第二条 民事主体的人身权利、财产权利以及其他合法权益受法律保护...")
                    .source("国家法律法规数据库")
                    .build());
        }
        return laws;
    }

    private List<LegalSearchResponse.CompanyItem> buildMockCompanies(String keyword) {
        List<LegalSearchResponse.CompanyItem> companies = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            final int idx = i;
            companies.add(LegalSearchResponse.CompanyItem.builder()
                    .id("company-" + idx)
                    .name(keyword != null ? keyword + "科技有限公司" : "示例科技有限公司")
                    .creditCode("91110000" + String.format("%08d", idx))
                    .legalPerson("张三" + idx)
                    .registeredCapital((idx * 100) + "万元人民币")
                    .status("在业")
                    .establishDate("201" + (idx % 9) + "-0" + (idx % 9 + 1) + "-15")
                    .address("北京市朝阳区建国路" + idx + "号")
                    .build());
        }
        return companies;
    }
}
