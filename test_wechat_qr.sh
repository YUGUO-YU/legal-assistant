#!/bin/bash

echo "======================================"
echo "  微信扫码登录功能测试"
echo "======================================"
echo ""

# 1. 测试后端接口
echo "1. 测试后端二维码生成接口..."
RESPONSE=$(curl -s http://localhost:8080/api/v1/auth/wechat/qr/generate)

if echo "$RESPONSE" | grep -q '"code":0'; then
    echo "   ✅ 接口调用成功"
    SCENE=$(echo "$RESPONSE" | grep -o '"scene":"[^"]*"' | cut -d'"' -f4)
    echo "   Scene: $SCENE"
    
    # 2. 测试状态查询
    echo ""
    echo "2. 测试二维码状态查询..."
    STATUS_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/auth/wechat/qr/status \
      -H "Content-Type: application/json" \
      -d "{\"scene\":\"$SCENE\"}")
    
    if echo "$STATUS_RESPONSE" | grep -q '"status":"waiting"'; then
        echo "   ✅ 状态查询成功：waiting（等待扫码）"
    else
        echo "   ⚠️  状态查询返回：$(echo "$STATUS_RESPONSE" | grep -o '"status":"[^"]*"')"
    fi
else
    echo "   ❌ 接口调用失败"
    echo "   Response: $RESPONSE"
    exit 1
fi

# 3. 检查前端服务
echo ""
echo "3. 检查前端服务..."
FRONTEND=$(curl -s http://localhost:5173/ | head -5)

if echo "$FRONTEND" | grep -q "DOCTYPE"; then
    echo "   ✅ 前端服务正常运行"
else
    echo "   ⚠️  前端服务可能未正常启动"
fi

echo ""
echo "======================================"
echo "  测试完成"
echo "======================================"
echo ""
echo "预览地址:"
echo "  - 本地：http://localhost:5173/"
echo "  - 预览：https://5173-0eabf9becb461963.monkeycode-ai.online"
echo ""
echo "微信扫码登录测试步骤:"
echo "  1. 打开登录页面"
echo "  2. 点击'扫码登录'或'微信扫码'按钮"
echo "  3. 观察二维码弹窗是否显示"
echo "  4. 查看浏览器控制台是否有接口请求"
echo ""
