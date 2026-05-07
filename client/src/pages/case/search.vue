<template>
  <view class="case-search">
    <view class="search-bar">
      <input
        v-model="keyword"
        class="search-input"
        placeholder="搜索案例关键词"
        @confirm="handleSearch"
      />
      <button class="search-btn" @click="handleSearch">搜索</button>
    </view>

    <view class="filter-bar">
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
      </view>

      <view v-if="loading" class="loading">加载中...</view>
      <view v-if="noMore && cases.length > 0" class="no-more">没有更多了</view>
      <view v-if="!loading && cases.length === 0" class="empty">暂无数据</view>
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

const caseTypes = ['全部', '民事', '刑事', '行政', '执行']
const courts = ['全部', '北京市第一中级人民法院', '上海市第一中级人民法院', '广东省高级人民法院']

onMounted(() => {
  loadCases()
})

const handleSearch = () => {
  cases.value = []
  page.value = 1
  noMore.value = false
  loadCases()
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

    if (page.value === 1) {
      cases.value = res.list || []
    } else {
      cases.value.push(...(res.list || []))
    }

    noMore.value = (res.list || []).length < pageSize.value
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
.case-search {
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

.filter-bar {
  display: flex;
  padding: 20rpx;
  background: #fff;
  margin-bottom: 20rpx;
  gap: 20rpx;
}

.filter-item {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 64rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
  font-size: 26rpx;
  color: #666;
}

.arrow {
  margin-left: 8rpx;
  font-size: 20rpx;
}

.case-list {
  height: calc(100vh - 300rpx);
  padding: 0 20rpx;
}

.case-item {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.case-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16rpx;
}

.case-type {
  background: #e6f7ff;
  color: #1890ff;
  padding: 4rpx 16rpx;
  border-radius: 4rpx;
  font-size: 22rpx;
}

.case-date {
  color: #999;
  font-size: 24rpx;
}

.case-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 16rpx;
  line-height: 1.4;
}

.case-info {
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
</style>