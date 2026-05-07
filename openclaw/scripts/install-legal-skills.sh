#!/bin/bash

# 法律助手 OpenClaw Skills 安装脚本
# 安装法律领域相关的 Skills

set -e

echo "=========================================="
echo "法律助手 OpenClaw Skills 安装脚本"
echo "=========================================="

# 定义 Skills 列表
SKILLS=(
    "clawhub/china-legal-query"
    "clawhub/china-contract-review"
    "clawhub/china-legal-analysis"
    "clawhub/mova-contract-generation"
    "clawhub/regulation-monitor"
    "clawhub/china-company-search"
    "clawhub/caseclaw"
    "clawhub/web-search"
    "clawhub/internet-search"
    "clawhub/document-pro"
)

# 法律领域 Skills
echo ""
echo ">>> 安装法律领域 Skills..."
for skill in "${SKILLS[@]}"; do
    echo "安装: $skill"
    openclaw skills add "$skill" || echo "警告: $skill 安装失败"
done

echo ""
echo "=========================================="
echo "Skills 安装完成！"
echo "=========================================="
echo ""
echo "已安装的 Skills："
for skill in "${SKILLS[@]}"; do
    echo "  - $skill"
done
echo ""
echo "下一步："
echo "  1. 运行 openclaw setup 完成配置"
echo "  2. 运行 openclaw gateway 启动服务"
echo "  3. 配置渠道：微信/Telegram/WebChat"
echo ""