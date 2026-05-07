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
      </view>

      <view v-if="loading" class="loading">加载中...</view>
      <view v-if="noMore && laws.length > 0" class="no-more">没有更多了</view>
      <view v-if="!loading && laws.length === 0" class="empty">暂无数据</view>
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

    if (page.value === 1) {
      laws.value = res.list || []
    } else {
      laws.value.push(...(res.list || []))
    }

    noMore.value = (res.list || []).length < pageSize.value
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
.law-search {
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

.law-list {
  height: calc(100vh - 300rpx);
  padding: 0 20rpx;
}

.law-item {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.law-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16rpx;
}

.law-type {
  background: #fff7e6;
  color: #fa8c16;
  padding: 4rpx 16rpx;
  border-radius: 4rpx;
  font-size: 22rpx;
}

.law-status {
  padding: 4rpx 16rpx;
  border-radius: 4rpx;
  font-size: 22rpx;
}

.law-status.effective {
  background: #f6ffed;
  color: #52c41a;
}

.law-status.expired {
  background: #fff1f0;
  color: #ff4d4f;
}

.law-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 16rpx;
  line-height: 1.4;
}

.law-info {
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