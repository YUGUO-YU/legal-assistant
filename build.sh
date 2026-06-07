#!/bin/bash

# Web 和小程序快速构建脚本

echo "======================================"
echo "  法律助手 - 多平台构建脚本"
echo "======================================"
echo ""

cd /workspace/client

# 检查 Node.js
if ! command -v node &> /dev/null; then
    echo "❌ 错误：未安装 Node.js"
    exit 1
fi

echo "Node.js 版本：$(node -v)"
echo "npm 版本：$(npm -v)"
echo ""

# 选择构建类型
echo "请选择构建类型:"
echo "1. H5 Web 开发版"
echo "2. H5 Web 生产版"
echo "3. 微信小程序"
echo "4. 全部构建"
echo ""
read -p "请输入选项 (1-4): " choice

case $choice in
    1)
        echo ""
        echo "🚀 开始构建 H5 Web 开发版..."
        npm run build:h5
        echo ""
        echo "✅ 构建完成！"
        echo "📁 输出目录：dist/build/h5/"
        ;;
    2)
        echo ""
        echo "🚀 开始构建 H5 Web 生产版..."
        npm run build:h5:prod
        echo ""
        echo "✅ 构建完成！"
        echo "📁 输出目录：dist/build/h5/"
        ;;
    3)
        echo ""
        echo "🚀 开始构建微信小程序..."
        npm run build:mp-weixin
        echo ""
        echo "✅ 构建完成！"
        echo "📁 输出目录：dist/build/mp-weixin/"
        echo ""
        echo "📱 下一步："
        echo "1. 打开微信开发者工具"
        echo "2. 导入 dist/build/mp-weixin/ 目录"
        echo "3. 上传代码并提交审核"
        ;;
    4)
        echo ""
        echo "🚀 开始构建所有平台..."
        echo ""
        echo "步骤 1/2: 构建 H5 Web 版"
        npm run build:h5:prod
        echo ""
        echo "步骤 2/2: 构建微信小程序"
        npm run build:mp-weixin
        echo ""
        echo "✅ 全部构建完成！"
        echo ""
        echo "📁 H5 Web: dist/build/h5/"
        echo "📱 小程序：dist/build/mp-weixin/"
        ;;
    *)
        echo "❌ 无效的选项"
        exit 1
        ;;
esac

echo ""
echo "======================================"
echo "  构建完成"
echo "======================================"
