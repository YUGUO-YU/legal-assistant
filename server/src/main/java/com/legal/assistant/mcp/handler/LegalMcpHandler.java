package com.legal.assistant.mcp.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.legal.assistant.module.cases.service.CaseService;
import com.legal.assistant.module.company.service.CompanyService;
import com.legal.assistant.module.law.service.LawService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class LegalMcpHandler extends TextWebSocketHandler {

    private final CaseService caseService;
    private final LawService lawService;
    private final CompanyService companyService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, JsonNode> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("MCP client connected: {}", session.getId());
        sessions.put(session.getId(), objectMapper.createObjectNode());
        sendResponse(session, createServerInfo());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            JsonNode request = objectMapper.readTree(message.getPayload());
            JsonNode result = processRequest(request);
            sendResponse(session, result);
        } catch (Exception e) {
            log.error("Error processing MCP request", e);
            sendError(session, e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("MCP client disconnected: {}", session.getId());
        sessions.remove(session.getId());
    }

    private JsonNode processRequest(JsonNode request) {
        String method = request.path("method").asText();
        JsonNode params = request.path("params");
        String id = request.path("id").asText(null);

        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.put("id", id);

        switch (method) {
            case "initialize":
                response.set("result", handleInitialize(params));
                break;
            case "tools/list":
                response.set("result", handleToolsList());
                break;
            case "tools/call":
                response.set("result", handleToolsCall(params));
                break;
            default:
                response.set("error", createError(-32601, "Method not found", method));
                return response;
        }

        return response;
    }

    private JsonNode handleInitialize(JsonNode params) {
        ObjectNode result = objectMapper.createObjectNode();
        result.put("protocolVersion", "2024-11-05");
        result.set("serverInfo", createServerInfo().get("result").get("serverInfo"));
        result.set("capabilities", createCapabilities());
        return result;
    }

    private ObjectNode createServerInfo() {
        ObjectNode serverInfo = objectMapper.createObjectNode();
        serverInfo.put("name", "legal-assistant-mcp");
        serverInfo.put("version", "1.0.0");
        return serverInfo;
    }

    private ObjectNode createCapabilities() {
        ObjectNode capabilities = objectMapper.createObjectNode();
        ObjectNode tools = objectMapper.createObjectNode();
        tools.put("listChanged", true);
        capabilities.set("tools", tools);
        return capabilities;
    }

    private JsonNode handleToolsList() {
        ObjectNode result = objectMapper.createObjectNode();
        result.putArray("tools");

        ObjectNode caseSearch = createTool("case_search", "搜索案例",
            "keyword", "string", "搜索关键词"
        );
        ObjectNode caseDetail = createTool("case_detail", "获取案例详情",
            "id", "string", "案例ID"
        );
        ObjectNode lawSearch = createTool("law_search", "搜索法规",
            "keyword", "string", "搜索关键词"
        );
        ObjectNode lawDetail = createTool("law_detail", "获取法规详情",
            "id", "string", "法规ID"
        );
        ObjectNode companySearch = createTool("company_search", "搜索企业",
            "keyword", "string", "搜索关键词"
        );
        ObjectNode companyDetail = createTool("company_detail", "获取企业详情",
            "id", "string", "企业ID"
        );
        ObjectNode companyRisk = createTool("company_risk", "获取企业风险信息",
            "id", "string", "企业ID"
        );

        result.withArray("tools").addAll(
            java.util.List.of(caseSearch, caseDetail, lawSearch, lawDetail,
                            companySearch, companyDetail, companyRisk)
        );

        return result;
    }

    private ObjectNode createTool(String name, String description, String... params) {
        ObjectNode tool = objectMapper.createObjectNode();
        tool.put("name", name);
        tool.put("description", description);
        tool.putArray("inputSchema");
        return tool;
    }

    private JsonNode handleToolsCall(JsonNode params) {
        String toolName = params.path("name").asText();
        JsonNode arguments = params.path("arguments");

        ObjectNode result = objectMapper.createObjectNode();

        try {
            switch (toolName) {
                case "case_search":
                    var caseReq = new com.legal.assistant.module.cases.dto.CaseSearchRequest();
                    caseReq.setKeyword(arguments.path("keyword").asText());
                    caseReq.setPage(1);
                    caseReq.setPageSize(10);
                    var caseResp = caseService.search(caseReq);
                    result.put("content", objectMapper.writeValueAsString(caseResp));
                    break;
                case "case_detail":
                    var caseDetailResp = caseService.getDetail(arguments.path("id").asText());
                    result.put("content", objectMapper.writeValueAsString(caseDetailResp));
                    break;
                case "law_search":
                    var lawReq = new com.legal.assistant.module.law.dto.LawSearchRequest();
                    lawReq.setKeyword(arguments.path("keyword").asText());
                    lawReq.setPage(1);
                    lawReq.setPageSize(10);
                    var lawResp = lawService.search(lawReq);
                    result.put("content", objectMapper.writeValueAsString(lawResp));
                    break;
                case "law_detail":
                    var lawDetailResp = lawService.getDetail(arguments.path("id").asText());
                    result.put("content", objectMapper.writeValueAsString(lawDetailResp));
                    break;
                case "company_search":
                    var companyReq = new com.legal.assistant.module.company.dto.CompanySearchRequest();
                    companyReq.setKeyword(arguments.path("keyword").asText());
                    companyReq.setPage(1);
                    companyReq.setPageSize(10);
                    var companyResp = companyService.search(companyReq);
                    result.put("content", objectMapper.writeValueAsString(companyResp));
                    break;
                case "company_detail":
                    var companyDetailResp = companyService.getDetail(arguments.path("id").asText());
                    result.put("content", objectMapper.writeValueAsString(companyDetailResp));
                    break;
                case "company_risk":
                    var companyRiskResp = companyService.getRiskInfo(arguments.path("id").asText());
                    result.put("content", objectMapper.writeValueAsString(companyRiskResp));
                    break;
                default:
                    result.put("error", "Unknown tool: " + toolName);
            }
        } catch (Exception e) {
            log.error("Error calling tool: " + toolName, e);
            result.put("error", e.getMessage());
        }

        ObjectNode output = objectMapper.createObjectNode();
        output.set("content", objectMapper.createArrayNode().add(result));
        return output;
    }

    private ObjectNode createError(int code, String message, String data) {
        ObjectNode error = objectMapper.createObjectNode();
        error.put("code", code);
        error.put("message", message);
        error.put("data", data);
        return error;
    }

    private void sendResponse(WebSocketSession session, JsonNode response) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        } catch (Exception e) {
            log.error("Error sending response", e);
        }
    }

    private void sendError(WebSocketSession session, String message) {
        ObjectNode error = objectMapper.createObjectNode();
        error.put("jsonrpc", "2.0");
        error.putNull("id");
        error.set("error", createError(-32603, "Internal error", message));
        sendResponse(session, error);
    }
}