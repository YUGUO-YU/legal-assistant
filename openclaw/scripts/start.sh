#!/bin/bash

# OpenClaw Gateway 启动脚本

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OPENCLAW_DIR="$(dirname "$SCRIPT_DIR")"

echo "启动 OpenClaw Gateway..."

cd "$OPENCLAW_DIR"

# 检查 Node.js
if ! command -v node &> /dev/null; then
    echo "错误: 需要 Node.js 环境"
    echo "请访问 https://nodejs.org/ 安装 Node.js 18+"
    exit 1
fi

# 检查 OpenClaw CLI
if ! command -v openclaw &> /dev/null; then
    echo "安装 OpenClaw CLI..."
    npm install -g openclaw@latest
fi

# 启动 Gateway
echo "启动 Gateway (端口 8090)..."
openclaw gateway --port 8090 --config "$OPENCLAW_DIR/.openclaw/config/openclaw.json"