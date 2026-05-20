<template>
  <view class="lead-list">
    <view class="filter-bar">
      <view
        v-for="tab in tabs"
        :key="tab.value"
        class="tab"
        :class="{ active: currentStatus === tab.value }"
        @click="onTabChange(tab.value)"
      >
        {{ tab.label }}
      </view>
    </view>

    <scroll-view
      class="lead-scroll"
      scroll-y
      @scrolltolower="loadMore"
    >
      <view
        v-for="item in leads"
        :key="item.id"
        class="lead-item"
        @click="goToDetail(item.id)"
      >
        <view class="lead-header">
          <text class="lead-title">{{ item.title }}</text>
          <text class="lead-status" :class="item.status">{{ getStatusText(item.status) }}</text>
        </view>
        <view class="lead-desc" v-if="item.description">{{ item.description }}</view>
        <view class="lead-meta">
          <text class="lead-source" v-if="item.source">{{ item.source }}</text>
          <text class="lead-date">{{ item.updatedAt }}</text>
        </view>
        <view class="lead-tags" v-if="item.tags && item.tags.length">
          <text v-for="tag in item.tags" :key="tag" class="tag">{{ tag }}</text>
        </view>
      </view>

      <view v-if="loading" class="loading">加载中...</view>
      <view v-if="noMore && leads.length > 0" class="no-more">没有更多了</view>
      <view v-if="!loading && leads.length === 0" class="empty">暂无案源</view>
    </scroll-view>

    <view class="add-btn" @click="goToCreate">+</view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { leadApi, type LeadItem } from '@/services/lead'

const tabs = [
  { label: '全部', value: '' },
  { label: '新建', value: 'new' },
  { label: '跟进中', value: 'following' },
  { label: '已成交', value: 'closed' }
]

const currentStatus = ref('')
const leads = ref<LeadItem[]>([])
const loading = ref(false)
const noMore = ref(false)
const page = ref(1)
const pageSize = ref(20)

onMounted(() => {
  loadLeads()
})

const loadLeads = async () => {
  if (loading.value) return
  loading.value = true

  try {
    const res = await leadApi.getList(
      currentStatus.value || undefined,
      page.value,
      pageSize.value
    )

    const data = res.data
    if (page.value === 1) {
      leads.value = data.list || []
    } else {
      leads.value.push(...(data.list || []))
    }

    noMore.value = (data.list || []).length < pageSize.value
  } catch (e) {
    console.error('加载案源失败', e)
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (noMore.value || loading.value) return
  page.value++
  loadLeads()
}

const onTabChange = (status: string) => {
  currentStatus.value = status
  leads.value = []
  page.value = 1
  noMore.value = false
  loadLeads()
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    new: '新建',
    following: '跟进中',
    closed: '已成交'
  }
  return map[status] || status
}

const goToDetail = (id: string) => {
  uni.navigateTo({
    url: `/pages/lead/detail?id=${id}`
  })
}

const goToCreate = () => {
  uni.navigateTo({
    url: '/pages/lead/create'
  })
}
</script>

<style lang="scss" scoped>
.lead-list {
  min-height: 100vh;
  background: #f5f5f5;
}

.filter-bar {
  display: flex;
  padding: 20rpx;
  background: #fff;
  gap: 16rpx;
}

.tab {
  flex: 1;
  height: 64rpx;
  line-height: 64rpx;
  text-align: center;
  background: #f5f5f5;
  border-radius: 8rpx;
  font-size: 26rpx;
  color: #666;
}

.tab.active {
  background: #1890ff;
  color: #fff;
}

.lead-scroll {
  height: calc(100vh - 180rpx);
  padding: 20rpx;
}

.lead-item {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.lead-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12rpx;
}

.lead-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  flex: 1;
}

.lead-status {
  padding: 4rpx 16rpx;
  border-radius: 4rpx;
  font-size: 22rpx;
}

.lead-status.new {
  background: #e6f7ff;
  color: #1890ff;
}

.lead-status.following {
  background: #fff7e6;
  color: #fa8c16;
}

.lead-status.closed {
  background: #f6ffed;
  color: #52c41a;
}

.lead-desc {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 12rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.lead-meta {
  display: flex;
  justify-content: space-between;
  font-size: 24rpx;
  color: #999;
  margin-bottom: 12rpx;
}

.lead-tags {
  display: flex;
  gap: 8rpx;
  flex-wrap: wrap;
}

.tag {
  padding: 4rpx 12rpx;
  background: #f5f5f5;
  border-radius: 4rpx;
  font-size: 22rpx;
  color: #666;
}

.loading, .no-more, .empty {
  text-align: center;
  padding: 40rpx;
  color: #999;
  font-size: 26rpx;
}

.add-btn {
  position: fixed;
  right: 40rpx;
  bottom: 120rpx;
  width: 100rpx;
  height: 100rpx;
  line-height: 100rpx;
  text-align: center;
  background: #1890ff;
  color: #fff;
  border-radius: 50%;
  font-size: 60rpx;
  box-shadow: 0 4rpx 20rpx rgba(24, 144, 255, 0.4);
}
</style>