#!/bin/bash

echo "======================================"
echo "  法律助手 - 启动所有服务"
echo "======================================"
echo ""

# 设置 Redis 目录
REDIS_PASSWORD=${REDIS_PASSWORD:-123456}

# 1. 启动 Redis
echo "1/2 检查 Redis 服务..."

if redis-cli ping > /dev/null 2>&1; then
    echo "    ✅ Redis 已运行"
else
    echo "    ⚠️  Redis 未运行，正在启动..."
    
    if command -v redis-server &> /dev/null; then
        redis-server --daemonize yes --requirepass "$REDIS_PASSWORD" --protected-mode no
        sleep 2
        
        if redis-cli -a "$REDIS_PASSWORD" ping > /dev/null 2>&1; then
            echo "    ✅ Redis 启动成功"
        else
            echo "    ❌ Redis 启动失败"
            exit 1
        fi
    else
        echo "    ❌ Redis 未安装，正在安装..."
        apt-get update
        apt-get install -y redis-server
        redis-server --daemonize yes --requirepass "$REDIS_PASSWORD" --protected-mode no
        sleep 2
        echo "    ✅ Redis 安装并启动成功"
    fi
fi

echo ""

# 2. 检查后端
echo "2/2 检查后端服务..."

if curl -s http://localhost:8080 > /dev/null 2>&1; then
    echo "    ✅ 后端已运行"
else
    echo "    ⚠️  后端未运行，正在启动..."
    
    if [ ! -f "/workspace/server/target/legal-assistant-1.0.0.jar" ]; then
        echo "    ❌ JAR 文件不存在，请先编译项目"
        echo "    执行：cd /workspace/server && mvn clean package -DskipTests"
        exit 1
    fi
    
    # 停止现有的 Java 进程
    pkill -9 java 2>/dev/null
    sleep 2
    
    # 启动后端
    cd /workspace/server
    nohup java -jar target/legal-assistant-1.0.0.jar > /tmp/backend.log 2>&1 &
    
    echo "    启动中，请等待 10 秒..."
    sleep 10
    
    # 验证启动
    if curl -s http://localhost:8080 > /dev/null 2>&1; then
        echo "    ✅ 后端启动成功（端口 8080）"
    else
        echo "    ❌ 后端启动失败，查看日志：/tmp/backend.log"
        tail -20 /tmp/backend.log
        exit 1
    fi
fi

echo ""
echo "======================================"
echo "  ✅ 所有服务启动完成"
echo "======================================"
echo ""

# 显示服务状态
echo "服务状态："
echo ""

# Redis 状态
if redis-cli ping > /dev/null 2>&1; then
    echo "  Redis:      ✅ 运行中"
else
    echo "  Redis:      ❌ 未运行"
fi

# 后端状态  
if curl -s http://localhost:8080 > /dev/null 2>&1; then
    echo "  后端服务：  ✅ 运行中 (端口 8080)"
else
    echo "  后端服务：  ❌ 未运行"
fi

echo ""
echo "测试接口："
echo "  短信发送：POST http://localhost:8080/api/v1/auth/sms/send"
echo "  手机登录：POST http://localhost:8080/api/v1/auth/phone/login"
echo ""
echo "查看日志：tail -f /tmp/backend.log"
echo ""
echo "停止服务：./stop.sh"
echo ""
