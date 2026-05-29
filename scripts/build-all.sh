#!/bin/bash

# 法律助手 - 一键打包脚本
# 用法: ./scripts/build-all.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "=========================================="
echo "法律助手 - 一键打包"
echo "=========================================="

cd "$PROJECT_DIR"

# 1. 构建前端
echo ""
echo "[1/3] 构建前端..."
cd client
npm install
npm run build:h5

# 复制构建产物到后端 static 目录
mkdir -p ../server/src/main/resources/static
rm -rf ../server/src/main/resources/static/*
cp -r dist/build/h5/* ../server/src/main/resources/static/
echo "前端构建完成"

# 2. 构建后端
echo ""
echo "[2/3] 构建后端 Spring Boot JAR..."
cd ../server
chmod +x mvnw 2>/dev/null || true

if command -v mvn &> /dev/null; then
    mvn clean package -DskipTests
else
    ./mvnw clean package -DskipTests
fi

echo "后端构建完成"

# 3. 输出结果
echo ""
echo "=========================================="
echo "构建完成！"
echo "=========================================="
echo ""
echo "JAR 文件位置: server/target/legal-assistant-1.0.0.jar"
echo ""
echo "启动方式:"
echo "  java -jar server/target/legal-assistant-1.0.0.jar"
echo ""
echo "访问地址: http://localhost:8080"
echo "=========================================="