<template>
  <view class="container">
    <view class="header">
      <view class="logo">
        <text class="logo-text">⚖️ 法律助手</text>
      </view>
      <view class="subtitle">专业的法律工作效率工具</view>
    </view>

    <view class="main-content">
      <view class="section quick-actions">
        <view class="section-header">
          <text class="section-title">快捷功能</text>
        </view>
        <view class="search-tabs">
          <view 
            class="tab-item" 
            :class="{ active: searchMode === 'ai_search' }"
            @click="switchSearchMode('ai_search')"
          >
            <text class="tab-icon">🤖</text>
            <text class="tab-text">AI 搜法</text>
          </view>
          <view 
            class="tab-item" 
            :class="{ active: searchMode === 'ai_case' }"
            @click="switchSearchMode('ai_case')"
          >
            <text class="tab-icon">📊</text>
            <text class="tab-text">AI 类案</text>
          </view>
          <view 
            class="tab-item" 
            :class="{ active: searchMode === 'case' }"
            @click="switchSearchMode('case')"
          >
            <text class="tab-icon">📋</text>
            <text class="tab-text">案例查询</text>
          </view>
          <view 
            class="tab-item" 
            :class="{ active: searchMode === 'law' }"
            @click="switchSearchMode('law')"
          >
            <text class="tab-icon">📚</text>
            <text class="tab-text">法规检索</text>
          </view>
        </view>
        <view class="search-box">
          <view class="search-input-wrapper">
            <text class="search-icon">🔍</text>
            <input 
              v-model="searchKeyword"
              class="search-input"
              :placeholder="searchPlaceholder"
              @confirm="handleSearch"
            />
            <view class="search-btn" @click="handleSearch">搜索</view>
          </view>
        </view>
      </view>

      <view class="section ai-section" v-if="searchMode === 'ai_search' || searchMode === 'ai_case'">
        <view class="section-header">
          <text class="section-title">{{ searchMode === 'ai_search' ? 'AI 搜法' : 'AI 类案' }}</text>
        </view>
        <view class="ai-card" @click="navigateTo('/pages/ai/chat')">
          <view class="ai-icon-wrapper">
            <view class="ai-icon">🤖</view>
          </view>
          <view class="ai-info">
            <text class="ai-title">法律智能助手</text>
            <text class="ai-desc">专业法律咨询，7×24 小时在线</text>
          </view>
          <view class="ai-arrow">›</view>
        </view>
      </view>

      <view class="section tools-section">
        <view class="section-header">
          <text class="section-title">常用工具</text>
        </view>
        <view class="tool-list">
          <view class="tool-item" @click="navigateTo('/pages/lead/list')">
            <view class="tool-content">
              <view class="tool-icon">💼</view>
              <text class="tool-text">案源管理</text>
              <text class="tool-badge" v-if="unreadLeads > 0">{{ unreadLeads }} 新</text>
            </view>
            <text class="tool-arrow">›</text>
          </view>
          <view class="tool-item" @click="navigateTo('/pages/document/list')">
            <view class="tool-content">
              <view class="tool-icon">📄</view>
              <text class="tool-text">我的文书</text>
            </view>
            <text class="tool-arrow">›</text>
          </view>
          <view class="tool-item" @click="navigateTo('/pages/user/profile')">
            <view class="tool-content">
              <view class="tool-icon">👤</view>
              <text class="tool-text">个人中心</text>
            </view>
            <text class="tool-arrow">›</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const unreadLeads = ref(0)
const searchKeyword = ref('')
const searchMode = ref('ai_search')

const searchPlaceholder = computed(() => {
  switch (searchMode.value) {
    case 'ai_search': return '输入法律问题，AI 为您解答'
    case 'ai_case': return '输入案情描述，AI 分析类案'
    case 'case': return '搜索案例名称、案号、当事人'
    case 'law': return '搜索法规名称、条款内容'
    default: return '请输入搜索关键词'
  }
})

onMounted(() => {
  authStore.initFromStorage()
  loadUnreadCount()
})

function loadUnreadCount() {
  setTimeout(() => {
    unreadLeads.value = Math.floor(Math.random() * 5)
  }, 1000)
}

function switchSearchMode(mode: string) {
  searchMode.value = mode
  searchKeyword.value = ''
}

function handleSearch() {
  if (!searchKeyword.value.trim()) {
    uni.showToast({ title: '请输入搜索关键词', icon: 'none' })
    return
  }
  
  switch (searchMode.value) {
    case 'ai_search':
      uni.navigateTo({ url: `/pages/ai/chat?query=${encodeURIComponent(searchKeyword.value)}` })
      break
    case 'ai_case':
      uni.navigateTo({ url: `/pages/case/search?mode=ai&keyword=${encodeURIComponent(searchKeyword.value)}` })
      break
    case 'case':
      uni.navigateTo({ url: `/pages/case/search?keyword=${encodeURIComponent(searchKeyword.value)}` })
      break
    case 'law':
      uni.navigateTo({ url: `/pages/law/search?keyword=${encodeURIComponent(searchKeyword.value)}` })
      break
  }
}

function navigateTo(url: string) {
  if (!authStore.isLoggedIn && url !== '/pages/case/search' && url !== '/pages/law/search') {
    uni.navigateTo({ url: '/pages/auth/login' })
    return
  }
  uni.navigateTo({ url })
}
</script>

<style lang="scss" scoped>
@import '@/style/variables.scss';

.container {
  min-height: 100vh;
  background: linear-gradient(180deg, #f0f9ff 0%, #e0f2fe 30%, #f0f9ff 100%);
  padding: 0;
}

.header {
  position: relative;
  overflow: hidden;
  padding: 80rpx 32rpx 100rpx;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border-radius: 0 0 48rpx 48rpx;
  box-shadow: 0 12rpx 32rpx rgba(24, 144, 255, 0.25);

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -20%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
    animation: shimmer 3s ease-in-out infinite;
  }
}

@keyframes shimmer {
  0%, 100% { transform: scale(1) rotate(0deg); }
  50% { transform: scale(1.05) rotate(5deg); }
}

.logo {
  position: relative;
  z-index: 1;
}

.logo-text {
  font-size: 52rpx;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 4rpx;
  text-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
}

.subtitle {
  position: relative;
  z-index: 1;
  margin-top: 20rpx;
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.92);
  font-weight: 300;
  letter-spacing: 2rpx;
}

.main-content {
  padding: 0 32rpx;
  margin-top: -60rpx;
}

.section {
  margin-bottom: 40rpx;
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 28rpx;
  padding-left: 12rpx;
}

.section-title {
  font-size: 34rpx;
  font-weight: 700;
  color: $text-primary;
  position: relative;
  padding-left: 20rpx;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 8rpx;
    height: 28rpx;
    background: $primary-gradient;
    border-radius: 4rpx;
  }
}

.quick-actions {
  .search-tabs {
    display: flex;
    gap: 16rpx;
    margin-bottom: 24rpx;
  }

  .tab-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20rpx 12rpx;
    background: #ffffff;
    border-radius: 16rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
    transition: all 0.3s ease;

    &.active {
      background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
      box-shadow: 0 8rpx 24rpx rgba(24, 144, 255, 0.3);
      
      .tab-icon, .tab-text {
        color: #ffffff;
      }
    }

    .tab-icon {
      font-size: 40rpx;
      margin-bottom: 8rpx;
    }

    .tab-text {
      font-size: 24rpx;
      color: $text-secondary;
      font-weight: 500;
    }
  }

  .search-box {
    background: #ffffff;
    border-radius: 24rpx;
    padding: 16rpx;
    box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
  }

  .search-input-wrapper {
    display: flex;
    align-items: center;
    background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%);
    border-radius: 16rpx;
    padding: 16rpx 20rpx;
  }

  .search-icon {
    font-size: 36rpx;
    margin-right: 16rpx;
  }

  .search-input {
    flex: 1;
    height: 48rpx;
    font-size: 28rpx;
    color: $text-primary;
    background: transparent;
    border: none;
    outline: none;

    &::placeholder {
      color: $text-placeholder;
    }
  }

  .search-btn {
    padding: 12rpx 32rpx;
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    color: #ffffff;
    font-size: 26rpx;
    font-weight: 600;
    border-radius: 12rpx;
    margin-left: 16rpx;
  }
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24rpx;
}

.action-item {
  position: relative;
  overflow: hidden;
  background: $background-white;
  border-radius: $radius-lg;
  padding: 40rpx 20rpx 32rpx;
  text-align: center;
  box-shadow: $shadow-md;
  transition: all $transition-base;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 6rpx;
    background: $primary-gradient;
    opacity: 0;
    transition: opacity $transition-base;
  }

  &:active {
    transform: translateY(-8rpx);
    box-shadow: $shadow-lg;

    &::before {
      opacity: 1;
    }
  }
}

.action-icon {
  position: relative;
  width: 96rpx;
  height: 96rpx;
  margin: 0 auto 20rpx;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-radius: $radius-md;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 56rpx;
  box-shadow: inset 0 4rpx 12rpx rgba(24, 144, 255, 0.1);
}

.action-text {
  font-size: 26rpx;
  color: $text-regular;
  font-weight: 500;
}
}

.ai-section {
  .ai-card {
    position: relative;
    overflow: hidden;
    background: linear-gradient(135deg, #ffffff 0%, #fafafa 100%);
    border-radius: $radius-lg;
    padding: 40rpx 32rpx;
    display: flex;
    align-items: center;
    box-shadow: $shadow-lg;
    transition: all $transition-base;

    &::before {
      content: '';
      position: absolute;
      top: -50%;
      right: -50%;
      width: 200%;
      height: 200%;
      background: radial-gradient(circle, rgba(82, 196, 26, 0.05) 0%, transparent 70%);
      animation: pulse 4s ease-in-out infinite;
    }

    &:active {
      transform: scale(0.98);
    }

    .ai-icon-wrapper {
      position: relative;
      z-index: 1;
      width: 100rpx;
      height: 100rpx;
      border-radius: $radius-round;
      background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 28rpx;
      box-shadow: 0 8rpx 20rpx rgba(82, 196, 26, 0.3);

      .ai-icon {
        font-size: 60rpx;
      }
    }

    .ai-info {
      flex: 1;
      position: relative;
      z-index: 1;
    }

    .ai-title {
      font-size: 34rpx;
      font-weight: 700;
      color: $text-primary;
      display: block;
      margin-bottom: 8rpx;
    }

    .ai-desc {
      font-size: 26rpx;
      color: $text-secondary;
      display: block;
    }

    .ai-arrow {
      position: relative;
      z-index: 1;
      font-size: 44rpx;
      color: $primary-color;
      font-weight: bold;
      transition: transform $transition-base;
    }

    &:active .ai-arrow {
      transform: translateX(8rpx);
    }
  }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.1); opacity: 0.8; }
}

.tools-section {
  .tool-list {
    @include card;
    overflow: hidden;
    padding: 8rpx;
  }

  .tool-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 36rpx 28rpx;
    border-radius: $radius-md;
    transition: background $transition-fast;

    &:active {
      background: $background-light;
    }

    &:not(:last-child)::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 120rpx;
      right: 0;
      height: 1rpx;
      background: linear-gradient(90deg, transparent 0%, $border-color 20%, $border-color 100%);
    }

    .tool-content {
      display: flex;
      align-items: center;
      flex: 1;
    }

    .tool-icon {
      width: 64rpx;
      height: 64rpx;
      border-radius: $radius-md;
      background: linear-gradient(135deg, #fff1eb 0%, #ace0f9 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 36rpx;
      margin-right: 24rpx;
    }

    .tool-text {
      font-size: 30rpx;
      color: $text-primary;
      font-weight: 500;
    }

    .tool-badge {
      margin-left: 16rpx;
      padding: 4rpx 12rpx;
      background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
      color: #fff;
      font-size: 22rpx;
      border-radius: $radius-round;
      font-weight: 500;
    }

    .tool-arrow {
      font-size: 36rpx;
      color: $text-placeholder;
      transition: transform $transition-fast;
    }

    &:active .tool-arrow {
      transform: translateX(8rpx);
      color: $primary-color;
    }
  }
}

.status-bar {
  padding: 24rpx 32rpx;
  background: $background-white;
  border-radius: $radius-lg $radius-lg 0 0;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.04);

  .status-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12rpx 0;

    .status-label {
      font-size: 26rpx;
      color: $text-secondary;
    }

    .status-value {
      font-size: 26rpx;
      color: $text-primary;
      font-weight: 600;
    }
  }
}
</style>