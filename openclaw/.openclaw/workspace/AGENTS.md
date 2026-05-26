# OpenClaw Agent Configuration

agent: legal-assistant
model: minimax/minimax2.7
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

# LLM Configuration
# Using Minimax2.7 API
env:
  MINIMAX_API_KEY: sk-cp-ekoYcsSAxZvJrF7fvTa6Ysotr5tUdq0tuDip1T288mxFO2VoH6oguSDnUvHUyyrpY1BzzQeifVkOTrSZMZ_gnr1NrvimwFs7IWccNtjjqMyLq1wOEBuagUs
  MINIMAX_BASE_URL: https://api.minimax.chat/v2