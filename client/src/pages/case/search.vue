<template>
  <view class="case-search">
    <view class="search-bar">
      <view class="search-mode-indicator" v-if="isAIMode">
        <text class="mode-icon">🤖</text>
        <text class="mode-text">AI 智能分析</text>
      </view>
      <input
        v-model="keyword"
        class="search-input"
        :placeholder="isAIMode ? '输入案情描述，AI 将分析类案...' : '搜索案例关键词'"
        @confirm="handleSearch"
      />
      <button class="search-btn" @click="handleSearch">
        {{ isAIMode ? 'AI 分析' : '搜索' }}
      </button>
    </view>

    <view class="filter-bar" v-if="!isAIMode">
      <picker mode="selector" :range="caseTypes" @change="onTypeChange">
        <view class="filter-item">
          <text>{{ selectedType || '案件类型' }}</text>
          <text class="arrow">▼</text>
        </view>
      </picker>
      <picker mode="selector" :range="courts" @change="onCourtChange">
        <view class="filter-item">
          <text>{{ selectedCourt || '法院' }}</text>
          <text class="arrow">▼</text>
        </view>
      </picker>
    </view>

    <!-- AI 分析结果展示 -->
    <view class="ai-analysis" v-if="isAIMode && aiAnalysis">
      <view class="analysis-header">
        <text class="analysis-title">🤖 AI 智能分析结果</text>
      </view>
      <view class="analysis-content">
        <view class="analysis-section">
          <text class="section-label">案件类型预测</text>
          <text class="section-value">{{ aiAnalysis.caseType || '民事' }}</text>
        </view>
        <view class="analysis-section">
          <text class="section-label">诉讼金额区间</text>
          <text class="section-value">{{ aiAnalysis.amountRange || '待定' }}</text>
        </view>
        <view class="analysis-section">
          <text class="section-label">相似案例数</text>
          <text class="section-value highlight">{{ aiAnalysis.similarCount || 0 }} 个</text>
        </view>
        <view class="analysis-section full">
          <text class="section-label">法律建议</text>
          <text class="section-text">{{ aiAnalysis.suggestion || '根据您的案情描述，请参考以下相关案例...' }}</text>
        </view>
      </view>
    </view>

    <scroll-view
      class="case-list"
      scroll-y
      @scrolltolower="loadMore"
    >
      <view
        v-for="item in cases"
        :key="item.id"
        class="case-item"
        @click="goToDetail(item.id)"
      >
        <view class="case-header">
          <text class="case-type">{{ item.caseType }}</text>
          <text class="case-date">{{ item.date }}</text>
        </view>
        <view class="case-title">{{ item.title }}</view>
        <view class="case-info">
          <text class="case-number">{{ item.caseNumber }}</text>
          <text class="case-court">{{ item.court }}</text>
        </view>
        <view class="case-source" v-if="item.source">
          <text class="source-tag">📋 {{ item.source }}</text>
        </view>
      </view>

      <view v-if="loading" class="loading">
        <text class="loading-text">加载中...</text>
      </view>
      <view v-if="noMore && cases.length > 0" class="no-more">没有更多了</view>
      <view v-if="!loading && cases.length === 0 && !aiAnalysis" class="empty">
        <text class="empty-icon">📭</text>
        <text class="empty-text">{{ isAIMode ? '请输入案情描述获取 AI 分析' : '暂无数据' }}</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { caseApi, type CaseItem } from '@/services/case'

const keyword = ref('')
const selectedType = ref('')
const selectedCourt = ref('')
const cases = ref<CaseItem[]>([])
const loading = ref(false)
const noMore = ref(false)
const page = ref(1)
const pageSize = ref(20)
const isAIMode = ref(false)
const aiAnalysis = ref<any>(null)

const caseTypes = ['全部', '民事', '刑事', '行政', '执行']
const courts = ['全部', '北京市第一中级人民法院', '上海市第一中级人民法院', '广东省高级人民法院']

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const urlKeyword = currentPage.options?.keyword || ''
  const mode = currentPage.options?.mode || ''
  
  if (urlKeyword) {
    keyword.value = decodeURIComponent(urlKeyword)
    if (mode === 'ai') {
      isAIMode.value = true
      // 模拟 AI 分析
      setTimeout(() => {
        aiAnalysis.value = {
          caseType: '民事合同纠纷',
          amountRange: '5万 - 20万',
          similarCount: Math.floor(Math.random() * 20) + 5,
          suggestion: '根据您的案情描述，建议优先协商解决，如协商不成可考虑诉讼。建议收集相关合同、付款凭证等证据。'
        }
      }, 1000)
    }
  } else if (mode === 'ai') {
    isAIMode.value = true
    uni.showToast({
      title: 'AI 模式：输入案情描述搜索类案',
      icon: 'none',
      duration: 2000
    })
  }
  
  loadCases()
})

const handleSearch = () => {
  cases.value = []
  page.value = 1
  noMore.value = false
  
  if (isAIMode.value && keyword.value.trim()) {
    // 模拟 AI 分析
    uni.showLoading({ title: 'AI 分析中...' })
    setTimeout(() => {
      uni.hideLoading()
      aiAnalysis.value = {
        caseType: '民事合同纠纷',
        amountRange: '5万 - 20万',
        similarCount: Math.floor(Math.random() * 20) + 5,
        suggestion: '根据您的案情描述，建议优先协商解决，如协商不成可考虑诉讼。建议收集相关合同、付款凭证等证据。'
      }
      loadCases()
    }, 1500)
  } else {
    aiAnalysis.value = null
    loadCases()
  }
}

const loadCases = async () => {
  if (loading.value) return
  loading.value = true

  try {
    const res = await caseApi.search({
      keyword: keyword.value || undefined,
      caseType: selectedType.value !== '全部' ? selectedType.value : undefined,
      court: selectedCourt.value !== '全部' ? selectedCourt.value : undefined,
      page: page.value,
      pageSize: pageSize.value
    })

    const data = res.data
    if (page.value === 1) {
      cases.value = data.list || []
    } else {
      cases.value.push(...(data.list || []))
    }

    noMore.value = (data.list || []).length < pageSize.value
  } catch (e) {
    console.error('搜索案例失败', e)
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (noMore.value || loading.value) return
  page.value++
  loadCases()
}

const onTypeChange = (e: any) => {
  selectedType.value = caseTypes[e.detail.value]
  handleSearch()
}

const onCourtChange = (e: any) => {
  selectedCourt.value = courts[e.detail.value]
  handleSearch()
}

const goToDetail = (id: string) => {
  uni.navigateTo({
    url: `/pages/case/detail?id=${id}`
  })
}
</script>

<style lang="scss" scoped>
@import '@/style/variables.scss';

.case-search {
  min-height: 100vh;
  background: linear-gradient(180deg, #f0f9ff 0%, #ffffff 100%);
}

.search-bar {
  display: flex;
  padding: 24rpx 32rpx;
  background: $background-white;
  gap: 20rpx;
  box-shadow: $shadow-sm;
  position: sticky;
  top: 0;
  z-index: 10;
  align-items: center;
}

.search-mode-indicator {
  display: flex;
  align-items: center;
  padding: 8rpx 20rpx;
  background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
  border-radius: 20rpx;
  margin-right: 16rpx;
  
  .mode-icon {
    font-size: 24rpx;
    margin-right: 8rpx;
  }
  
  .mode-text {
    font-size: 22rpx;
    color: #fff;
    font-weight: 600;
  }
}

.search-input {
  flex: 1;
  height: 88rpx;
  padding: 0 32rpx;
  background: $background-light;
  border-radius: $radius-round;
  font-size: 28rpx;
  color: $text-primary;
  border: 2rpx solid transparent;
  transition: all $transition-fast;

  &:focus {
    background: $background-white;
    border-color: $primary-color;
  }
}

.search-btn {
  width: 150rpx;
  height: 88rpx;
  background: $primary-gradient;
  color: #fff;
  border-radius: $radius-round;
  font-size: 30rpx;
  font-weight: 600;
  box-shadow: 0 8rpx 20rpx rgba(24, 144, 255, 0.3);
  transition: all $transition-fast;
  border: none;

  &:active {
    transform: scale(0.95);
  }
}

.filter-bar {
  display: flex;
  padding: 24rpx 32rpx;
  background: $background-white;
  margin-bottom: 0;
  gap: 20rpx;
  box-shadow: $shadow-sm;
}

.filter-item {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 72rpx;
  background: $background-light;
  border-radius: $radius-round;
  font-size: 28rpx;
  color: $text-regular;
  font-weight: 500;
  transition: all $transition-fast;

  &:active {
    background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
    color: $primary-color;
  }
}

.arrow {
  margin-left: 8rpx;
  font-size: 24rpx;
  color: $text-placeholder;
}

.ai-analysis {
  margin: 24rpx 32rpx;
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%);
  border-radius: $radius-lg;
  padding: 32rpx;
  border: 2rpx solid rgba(24, 144, 255, 0.2);

  .analysis-header {
    margin-bottom: 24rpx;
  }

  .analysis-title {
    font-size: 30rpx;
    font-weight: 700;
    color: $primary-color;
  }

  .analysis-content {
    display: flex;
    flex-wrap: wrap;
    gap: 20rpx;
  }

  .analysis-section {
    width: calc(50% - 10rpx);
    background: #ffffff;
    border-radius: $radius-md;
    padding: 20rpx;

    &.full {
      width: 100%;
    }

    .section-label {
      display: block;
      font-size: 24rpx;
      color: $text-secondary;
      margin-bottom: 8rpx;
    }

    .section-value {
      font-size: 28rpx;
      font-weight: 600;
      color: $text-primary;

      &.highlight {
        color: $primary-color;
        font-size: 32rpx;
      }
    }

    .section-text {
      font-size: 26rpx;
      color: $text-regular;
      line-height: 1.6;
    }
  }
}

.case-list {
  height: calc(100vh - 320rpx);
  padding: 0 32rpx;
}

.case-item {
  background: $background-white;
  border-radius: $radius-lg;
  padding: 32rpx;
  margin-bottom: 24rpx;
  box-shadow: $shadow-md;
  transition: all $transition-fast;

  &:active {
    transform: translateY(-4rpx);
    box-shadow: $shadow-lg;
  }
}

.case-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.case-type {
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  color: $primary-dark;
  padding: 8rpx 20rpx;
  border-radius: $radius-round;
  font-size: 24rpx;
  font-weight: 600;
  box-shadow: 0 2rpx 8rpx rgba(24, 144, 255, 0.15);
}

.case-date {
  color: $text-secondary;
  font-size: 26rpx;
  font-weight: 500;
}

.case-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 20rpx;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.case-info {
  display: flex;
  justify-content: space-between;
  font-size: 26rpx;
  color: $text-secondary;
  padding-top: 20rpx;
  border-top: 1rpx solid $border-color;
}

.case-number {
  font-weight: 500;
}

.case-court {
  max-width: 400rpx;
  @include ellipsis;
}

.case-source {
  margin-top: 12rpx;
  padding-top: 12rpx;
  border-top: 1rpx dashed $border-color;
}

.source-tag {
  display: inline-block;
  padding: 4rpx 12rpx;
  background: #f0f9ff;
  color: #1890ff;
  font-size: 22rpx;
  border-radius: $radius-sm;
}

.loading, .no-more, .empty {
  text-align: center;
  padding: 60rpx;
  color: $text-secondary;
  font-size: 28rpx;
  font-weight: 500;
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  
  &::before {
    content: '📭';
    font-size: 100rpx;
    margin-bottom: 24rpx;
    display: block;
  }
}
</style>