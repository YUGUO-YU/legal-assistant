package com.legal.assistant.module.search.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WebSearchService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${search.provider:duckduckgo}")
    private String provider;

    @Value("${search.api-key:}")
    private String apiKey;

    @Value("${search.fallback.enabled:true}")
    private boolean fallbackEnabled;

    public WebSearchService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public List<SearchResult> search(String query, int count) {
        List<SearchResult> results = new ArrayList<>();

        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            String url;
            if ("bing".equals(provider) && !apiKey.isEmpty()) {
                url = "https://api.bing.microsoft.com/v7.0/search?q=" + encodedQuery + "&count=" + count;
            } else {
                url = buildDuckDuckGoUrl(encodedQuery, count);
            }

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(20));

            if ("bing".equals(provider) && !apiKey.isEmpty()) {
                requestBuilder.header("Ocp-Apim-Subscription-Key", apiKey);
            }

            HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                results = parseResponse(response.body(), count);
            } else {
                log.warn("Search API returned status: {}", response.statusCode());
            }
        } catch (Exception e) {
            log.error("Search error for query: {}", query, e);
        }

        if (results.isEmpty() && fallbackEnabled) {
            results = getFallbackResults(query, count);
        }

        return results;
    }

    private String buildDuckDuckGoUrl(String encodedQuery, int count) {
        return "https://api.duckduckgo.com/?q=" + encodedQuery + "&format=json&no_html=1&skip_disambiguation=1&cnt=" + count;
    }

    private List<SearchResult> parseResponse(String responseBody, int count) {
        List<SearchResult> results = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(responseBody);

            if ("bing".equals(provider)) {
                JsonNode webPages = root.path("webPages").path("value");
                if (webPages.isArray()) {
                    for (JsonNode node : webPages) {
                        if (results.size() >= count) break;
                        SearchResult result = new SearchResult();
                        result.setTitle(node.path("name").asText(""));
                        result.setUrl(node.path("url").asText(""));
                        result.setDescription(node.path("snippet").asText(""));
                        results.add(result);
                    }
                }
            } else {
                JsonNode abstractNode = root.path("Abstract");
                if (!abstractNode.isMissingNode() && !abstractNode.asText().isEmpty()) {
                    SearchResult result = new SearchResult();
                    result.setTitle(root.path("Heading").asText("搜索结果"));
                    result.setUrl(root.path("AbstractURL").asText(""));
                    result.setDescription(abstractNode.asText(""));
                    results.add(result);
                }

                JsonNode relatedTopics = root.path("RelatedTopics");
                if (relatedTopics.isArray()) {
                    for (JsonNode node : relatedTopics) {
                        if (results.size() >= count) break;
                        String text = node.path("Text").asText("");
                        String url = node.path("FirstURL").asText("");
                        if (!text.isEmpty() && !url.isEmpty()) {
                            SearchResult result = new SearchResult();
                            result.setTitle(text.length() > 50 ? text.substring(0, 47) + "..." : text);
                            result.setUrl(url);
                            result.setDescription(text);
                            results.add(result);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse search response", e);
        }

        return results;
    }

    private List<SearchResult> getFallbackResults(String query, int count) {
        List<SearchResult> results = new ArrayList<>();
        String[][]fallbackData = {
            {"劳动纠纷处理", "https://china.findlaw.cn/baike/ldjl.html", "劳动纠纷可以通过协商、调解、仲裁、诉讼等方式处理。建议优先协商，如协商不成可申请劳动仲裁。"},
            {"法律咨询", "https://www.12348.gov.cn/", "遇到法律问题可拨打12348法律服务热线，或前往当地法律援助中心咨询。"},
            {"劳动法规定", "https://www.mohrss.gov.cn/", "中华人民共和国劳动法和劳动合同法规定了劳动者的权利义务，具体可查询人社部官网。"}
        };

        for (int i = 0; i < Math.min(count, fallbackData.length); i++) {
            SearchResult result = new SearchResult();
            result.setTitle(fallbackData[i][0]);
            result.setUrl(fallbackData[i][1]);
            result.setDescription(fallbackData[i][2]);
            results.add(result);
        }

        return results;
    }

    public static class SearchResult {
        private String title;
        private String url;
        private String description;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}