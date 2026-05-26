# OpenClaw Agent Configuration

agent: legal-assistant
model: openai/gpt-4
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
tools:
  browser:
    enabled: true
  sessions:
    enabled: true