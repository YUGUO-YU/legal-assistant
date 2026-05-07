#!/bin/bash

# OpenClaw Skills 安装脚本
# 用于安装法律助手所需的 OpenClaw Skills

set -e

OPENCLAW_CLI="${OPENCLAW_CLI:-openclaw}"
SKILLS_DIR="${SKILLS_DIR:-./skills}"

echo "========================================="
echo "OpenClaw Skills 安装脚本"
echo "========================================="

# 需要安装的 Skills 列表
SKILLS=(
  "china-legal-query"
  "china-contract-review"
  "china-legal-analysis"
  "mova-contract-generation"
  "regulation-monitor"
  "china-company-search"
  "caseclaw"
  "web-search"
  "internet-search"
  "document-pro"
)

echo ""
echo "将要安装以下 Skills:"
for skill in "${SKILLS[@]}"; do
  echo "  - $skill"
done
echo ""

read -p "确认安装? (y/n) " -n 1 -r
echo ""
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
  echo "安装已取消"
  exit 0
fi

# 创建 skills 目录
mkdir -p "$SKILLS_DIR"

# 安装每个 skill
for skill in "${SKILLS[@]}"; do
  echo ""
  echo ">>> 安装 $skill ..."
  $OPENCLAW_CLI skill install "$skill" --dir "$SKILLS_DIR/$skill" || {
    echo "警告: $skill 安装失败，继续安装其他 skills"
  }
done

echo ""
echo "========================================="
echo "Skills 安装完成!"
echo "========================================="
echo ""
echo "已安装的 Skills:"
ls -la "$SKILLS_DIR"
echo ""
echo "下一步:"
echo "1. 配置 OpenClaw Gateway"
echo "2. 启动服务: openclaw gateway start"
