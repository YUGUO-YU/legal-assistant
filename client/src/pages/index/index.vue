<template>
  <view class="container">
    <view class="header">
      <view class="logo">
        <text class="logo-text">法律助手</text>
      </view>
      <view class="subtitle">专业的法律工作效率工具</view>
    </view>

    <view class="quick-actions">
      <view class="section-title">快捷功能</view>
      <view class="action-grid">
        <view class="action-item" @click="navigateTo('/pages/case/search')">
          <view class="action-icon">📋</view>
          <text class="action-text">案例查询</text>
        </view>
        <view class="action-item" @click="navigateTo('/pages/law/search')">
          <view class="action-icon">📚</view>
          <text class="action-text">法规检索</text>
        </view>
        <view class="action-item" @click="navigateTo('/pages/company/search')">
          <view class="action-icon">🏢</view>
          <text class="action-text">企业查询</text>
        </view>
        <view class="action-item" @click="navigateTo('/pages/document/create')">
          <view class="action-icon">📝</view>
          <text class="action-text">文书起草</text>
        </view>
      </view>
    </view>

    <view class="ai-section">
      <view class="section-title">AI 助手</view>
      <view class="ai-card" @click="navigateTo('/pages/ai/chat')">
        <view class="ai-icon">🤖</view>
        <view class="ai-info">
          <text class="ai-title">法律智能助手</text>
          <text class="ai-desc">随时为您解答法律问题</text>
        </view>
        <view class="ai-arrow">›</view>
      </view>
    </view>

    <view class="tools-section">
      <view class="section-title">常用工具</view>
      <view class="tool-list">
        <view class="tool-item" @click="navigateTo('/pages/lead/list')">
          <text class="tool-text">案源管理</text>
          <text class="tool-arrow">›</text>
        </view>
        <view class="tool-item" @click="navigateTo('/pages/document/list')">
          <text class="tool-text">我的文书</text>
          <text class="tool-arrow">›</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMount } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

onMount(() => {
  authStore.initFromStorage()
})

function navigateTo(url: string) {
  if (!authStore.isLoggedIn && url !== '/pages/case/search' && url !== '/pages/law/search') {
    uni.navigateTo({ url: '/pages/auth/login' })
    return
  }
  uni.navigateTo({ url })
}
</script>

<style lang="scss" scoped>
.container {
  padding: 32rpx;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.header {
  text-align: center;
  padding: 60rpx 0;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border-radius: 24rpx;
  margin-bottom: 32rpx;
}

.logo-text {
  font-size: 48rpx;
  font-weight: bold;
  color: #ffffff;
}

.subtitle {
  margin-top: 16rpx;
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  margin-bottom: 24rpx;
  color: #333;
}

.quick-actions {
  margin-bottom: 32rpx;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24rpx;
}

.action-item {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 32rpx 16rpx;
  text-align: center;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.action-icon {
  font-size: 48rpx;
  margin-bottom: 12rpx;
}

.action-text {
  font-size: 24rpx;
  color: #666;
}

.ai-section {
  margin-bottom: 32rpx;
}

.ai-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 32rpx;
  display: flex;
  align-items: center;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.ai-icon {
  font-size: 56rpx;
  margin-right: 24rpx;
}

.ai-info {
  flex: 1;
}

.ai-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  display: block;
}

.ai-desc {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
  display: block;
}

.ai-arrow {
  font-size: 40rpx;
  color: #ccc;
}

.tools-section {
  margin-bottom: 32rpx;
}

.tool-list {
  background: #ffffff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.tool-item {
  padding: 32rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1rpx solid #f0f0f0;
}

.tool-item:last-child {
  border-bottom: none;
}

.tool-text {
  font-size: 28rpx;
  color: #333;
}

.tool-arrow {
  font-size: 32rpx;
  color: #ccc;
}
</style>