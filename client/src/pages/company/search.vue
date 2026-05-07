<template>
  <view class="company-search">
    <view class="search-bar">
      <input
        v-model="keyword"
        class="search-input"
        placeholder="搜索企业名称/统一社会信用代码"
        @confirm="handleSearch"
      />
      <button class="search-btn" @click="handleSearch">搜索</button>
    </view>

    <scroll-view
      class="company-list"
      scroll-y
      @scrolltolower="loadMore"
    >
      <view
        v-for="item in companies"
        :key="item.id"
        class="company-item"
        @click="goToDetail(item.id)"
      >
        <view class="company-header">
          <text class="company-name">{{ item.name }}</text>
          <text class="company-status" :class="item.status">{{ item.status }}</text>
        </view>
        <view class="company-info">
          <text class="info-item">法定代表人: {{ item.legalPerson }}</text>
          <text class="info-item">注册资本: {{ item.capital }}</text>
        </view>
        <view class="company-meta">
          <text class="credit-code">{{ item.creditCode }}</text>
          <text class="establish-date">成立日期: {{ item.establishDate }}</text>
        </view>
      </view>

      <view v-if="loading" class="loading">加载中...</view>
      <view v-if="noMore && companies.length > 0" class="no-more">没有更多了</view>
      <view v-if="!loading && companies.length === 0" class="empty">
        <text>暂无数据</text>
        <text class="empty-hint">试试搜索其他关键词</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { companyApi, type CompanyItem } from '@/services/company'

const keyword = ref('')
const companies = ref<CompanyItem[]>([])
const loading = ref(false)
const noMore = ref(false)
const page = ref(1)
const pageSize = ref(20)

onMounted(() => {
  loadCompanies()
})

const handleSearch = () => {
  companies.value = []
  page.value = 1
  noMore.value = false
  loadCompanies()
}

const loadCompanies = async () => {
  if (loading.value) return
  loading.value = true

  try {
    const res = await companyApi.search({
      keyword: keyword.value || undefined,
      page: page.value,
      pageSize: pageSize.value
    })

    if (page.value === 1) {
      companies.value = res.list || []
    } else {
      companies.value.push(...(res.list || []))
    }

    noMore.value = (res.list || []).length < pageSize.value
  } catch (e) {
    console.error('搜索企业失败', e)
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (noMore.value || loading.value) return
  page.value++
  loadCompanies()
}

const goToDetail = (id: string) => {
  uni.navigateTo({
    url: `/pages/company/detail?id=${id}`
  })
}
</script>

<style lang="scss" scoped>
.company-search {
  min-height: 100vh;
  background: #f5f5f5;
}

.search-bar {
  display: flex;
  padding: 20rpx;
  background: #fff;
  gap: 20rpx;
}

.search-input {
  flex: 1;
  height: 72rpx;
  padding: 0 24rpx;
  background: #f5f5f5;
  border-radius: 36rpx;
  font-size: 28rpx;
}

.search-btn {
  width: 140rpx;
  height: 72rpx;
  line-height: 72rpx;
  background: #1890ff;
  color: #fff;
  border-radius: 36rpx;
  font-size: 28rpx;
}

.company-list {
  height: calc(100vh - 180rpx);
  padding: 20rpx;
}

.company-item {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.company-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.company-name {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  flex: 1;
}

.company-status {
  padding: 4rpx 16rpx;
  border-radius: 4rpx;
  font-size: 22rpx;
}

.company-status.存续 {
  background: #f6ffed;
  color: #52c41a;
}

.company-status.吊销 {
  background: #fff1f0;
  color: #ff4d4f;
}

.company-status.注销 {
  background: #f5f5f5;
  color: #999;
}

.company-info {
  display: flex;
  gap: 32rpx;
  margin-bottom: 12rpx;
}

.info-item {
  font-size: 26rpx;
  color: #666;
}

.company-meta {
  display: flex;
  justify-content: space-between;
  font-size: 24rpx;
  color: #999;
}

.loading, .no-more, .empty {
  text-align: center;
  padding: 40rpx;
  color: #999;
  font-size: 26rpx;
}

.empty {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.empty-hint {
  font-size: 24rpx;
}
</style>