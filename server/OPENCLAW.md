# OpenClaw AI Configuration

## Overview
OpenClaw 作为独立 AI 服务，通过 MCP (Model Context Protocol) 协议与后端集成，提供法律智能服务。

## MCP Server Endpoint
- WebSocket: `ws://localhost:8080/mcp/legal`
- HTTP: `http://localhost:8080/mcp/legal`

## Available Skills

### China Legal Query
- **Skill ID**: china-legal-query
- **Purpose**: 检索中国法律法规、司法解释
- **Tools**: law_search, law_detail

### China Contract Review
- **Skill ID**: china-contract-review
- **Purpose**: 合同审查与风险提示
- **Tools**: contract_review

### China Legal Analysis
- **Skill ID**: china-legal-analysis
- **Purpose**: 法律问题分析与建议
- **Tools**: legal_analysis

### MOVa Contract Generation
- **Skill ID**: mova-contract-generation
- **Purpose**: 合同智能生成
- **Tools**: generate_contract

### Regulation Monitor
- **Skill ID**: regulation-monitor
- **Purpose**: 法规动态监控
- **Tools**: monitor_regulations

### China Company Search
- **Skill ID**: china-company-search
- **Purpose**: 企业信息查询
- **Tools**: company_search, company_detail, company_risk

### CaseClaw
- **Skill ID**: caseclaw
- **Purpose**: 案例检索与分析
- **Tools**: case_search, case_detail

### Web Search
- **Skill ID**: web-search
- **Purpose**: 互联网搜索
- **Tools**: web_search

### Internet Search
- **Skill ID**: internet-search
- **Purpose**: 实时互联网搜索
- **Tools**: internet_search

### Document Pro
- **Skill ID**: document-pro
- **Purpose**: 法律文书处理
- **Tools**: document_parse, document_summary

## Configuration Example

### Gateway Config (gateway.yml)
```yaml
mcp:
  servers:
    legal:
      type: websocket
      url: ws://localhost:8080/mcp/legal
      skills:
        - china-legal-query
        - china-contract-review
        - china-legal-analysis
        - mova-contract-generation
        - regulation-monitor
        - china-company-search
        - caseclaw
        - web-search
        - document-pro
```

### Client Integration
```javascript
import { OpenClawClient } from '@openclaw/client';

const client = new OpenClawClient({
  gateway: 'http://localhost:8090',
  apiKey: process.env.OPENCLAW_API_KEY
});

const legalTools = client.getTools(['china-legal-query', 'caseclaw']);
const result = await legalTools.case_search({ keyword: '合同纠纷' });
```

## Environment Variables
- `OPENCLAW_API_KEY`: OpenClaw API 密钥
- `OPENCLAW_GATEWAY_URL`: OpenClaw Gateway 地址 (默认: http://localhost:8090)
