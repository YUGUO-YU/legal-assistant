#!/bin/bash

echo "======================================"
echo "  法律助手 - 停止所有服务"
echo "======================================"
echo ""

# 1. 停止后端
echo "1/2 停止后端服务..."

if pkill -9 java 2>/dev/null; then
    echo "    ✅ 后端已停止"
else
    echo "    ⚠️  后端未运行"
fi

# 2. 停止 Redis
echo "2/2 停止 Redis 服务..."

if redis-cli shutdown 2>/dev/null || pkill -9 redis-server 2>/dev/null; then
    echo "    ✅ Redis 已停止"
else
    echo "    ⚠️  Redis 未运行"
fi

echo ""
echo "======================================"
echo "  ✅ 所有服务已停止"
echo "======================================"
echo ""
