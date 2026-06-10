<template>
  <view class="law-search">
    <view class="search-bar">
      <input
        v-model="keyword"
        class="search-input"
        placeholder="搜索法规关键词"
        @confirm="handleSearch"
      />
      <button class="search-btn" @click="handleSearch">搜索</button>
    </view>

    <view class="filter-bar">
      <picker mode="selector" :range="lawTypes" @change="onTypeChange">
        <view class="filter-item">
          <text>{{ selectedType || '法规类型' }}</text>
          <text class="arrow">▼</text>
        </view>
      </picker>
    </view>

    <scroll-view
      class="law-list"
      scroll-y
      @scrolltolower="loadMore"
    >
      <view
        v-for="item in laws"
        :key="item.id"
        class="law-item"
        @click="goToDetail(item.id)"
      >
        <view class="law-header">
          <text class="law-type">{{ item.lawType }}</text>
          <text class="law-status" :class="item.status">{{ item.status }}</text>
        </view>
        <view class="law-title">{{ item.title }}</view>
        <view class="law-info">
          <text class="law-number">{{ item.lawNumber }}</text>
          <text class="law-date">施行日期: {{ item.effectiveDate }}</text>
        </view>
        <view class="law-source" v-if="item.source">
          <text class="source-tag">📚 {{ item.source }}</text>
        </view>
      </view>

      <view v-if="loading" class="loading">
        <text class="loading-text">加载中...</text>
      </view>
      <view v-if="noMore && laws.length > 0" class="no-more">没有更多了</view>
      <view v-if="!loading && laws.length === 0" class="empty">
        <text class="empty-icon">📚</text>
        <text class="empty-text">暂无数据</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { lawApi, type LawItem } from '@/services/law'

const keyword = ref('')
const selectedType = ref('')
const laws = ref<LawItem[]>([])
const loading = ref(false)
const noMore = ref(false)
const page = ref(1)
const pageSize = ref(20)

const lawTypes = ['全部', '法律', '行政法规', '司法解释', '部门规章', '地方性法规']

onMounted(() => {
  // 获取页面参数
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const urlKeyword = currentPage.options?.keyword || ''
  
  if (urlKeyword) {
    keyword.value = decodeURIComponent(urlKeyword)
  }
  
  loadLaws()
})

const handleSearch = () => {
  laws.value = []
  page.value = 1
  noMore.value = false
  loadLaws()
}

const loadLaws = async () => {
  if (loading.value) return
  loading.value = true

  try {
    const res = await lawApi.search({
      keyword: keyword.value || undefined,
      lawType: selectedType.value !== '全部' ? selectedType.value : undefined,
      page: page.value,
      pageSize: pageSize.value
    })

    const data = res.data
    if (page.value === 1) {
      laws.value = data.list || []
    } else {
      laws.value.push(...(data.list || []))
    }

    noMore.value = (data.list || []).length < pageSize.value
  } catch (e) {
    console.error('搜索法规失败', e)
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (noMore.value || loading.value) return
  page.value++
  loadLaws()
}

const onTypeChange = (e: any) => {
  selectedType.value = lawTypes[e.detail.value]
  handleSearch()
}

const goToDetail = (id: string) => {
  uni.navigateTo({
    url: `/pages/law/detail?id=${id}`
  })
}
</script>

<style lang="scss" scoped>
@import '@/style/variables.scss';

.law-search {
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

  &::placeholder {
    color: $text-placeholder;
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
  display: flex;
  align-items: center;
  justify-content: center;

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

.law-list {
  height: calc(100vh - 320rpx);
  padding: 0 32rpx;
}

.law-item {
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

.law-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.law-type {
  background: linear-gradient(135deg, #fff7e6 0%, #ffe7ba 100%);
  color: #fa8c16;
  padding: 8rpx 20rpx;
  border-radius: $radius-round;
  font-size: 24rpx;
  font-weight: 600;
  box-shadow: 0 2rpx 8rpx rgba(250, 140, 22, 0.15);
}

.law-status {
  padding: 8rpx 20rpx;
  border-radius: $radius-round;
  font-size: 24rpx;
  font-weight: 500;
}

.law-status.effective {
  background: linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%);
  color: #52c41a;
}

.law-status.expired {
  background: linear-gradient(135deg, #fff1f0 0%, #ffccc7 100%);
  color: #ff4d4f;
}

.law-title {
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

.law-info {
  display: flex;
  justify-content: space-between;
  font-size: 26rpx;
  color: $text-secondary;
  padding-top: 20rpx;
  border-top: 1rpx solid $border-color;
}

.law-number {
  font-weight: 500;
}

.law-source {
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
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60rpx;
  color: $text-secondary;
  font-size: 28rpx;
  font-weight: 500;
}

.empty-icon {
  font-size: 100rpx;
  margin-bottom: 24rpx;
}

.loading-text {
  display: flex;
  align-items: center;
  
  &::before {
    content: '';
    width: 32rpx;
    height: 32rpx;
    border: 4rpx solid $border-color;
    border-top-color: $primary-color;
    border-radius: 50%;
    margin-right: 16rpx;
    animation: spin 1s linear infinite;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>