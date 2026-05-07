<template>
  <view class="case-detail" v-if="detail">
    <view class="header">
      <view class="tags">
        <text class="tag type">{{ detail.caseType }}</text>
        <text class="tag status">{{ detail.status }}</text>
      </view>
      <view class="case-number">{{ detail.caseNumber }}</view>
    </view>

    <view class="title">{{ detail.title }}</view>

    <view class="info-grid">
      <view class="info-item">
        <view class="label">审理法院</view>
        <view class="value">{{ detail.court }}</view>
      </view>
      <view class="info-item">
        <view class="label">案件日期</view>
        <view class="value">{{ detail.date }}</view>
      </view>
      <view class="info-item">
        <view class="label">当事人</view>
        <view class="value">{{ detail.parties }}</view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">案件内容</view>
      <view class="section-content">{{ detail.content }}</view>
    </view>

    <view class="section" v-if="detail.judgment">
      <view class="section-title">判决结果</view>
      <view class="section-content">{{ detail.judgment }}</view>
    </view>

    <view class="actions">
      <button class="action-btn" @click="toggleBookmark">
        {{ isBookmarked ? '取消收藏' : '收藏' }}
      </button>
      <button class="action-btn primary" @click="analyzeWithAI">AI分析</button>
    </view>
  </view>
  <view v-else class="loading">加载中...</view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { caseApi } from '@/services/case'

const detail = ref<any>(null)
const isBookmarked = ref(false)
const caseId = ref('')

onMounted(async () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  caseId.value = currentPage.options?.id || ''

  if (caseId.value) {
    await loadDetail()
  }
})

const loadDetail = async () => {
  try {
    detail.value = await caseApi.getDetail(caseId.value)
  } catch (e) {
    console.error('加载案例详情失败', e)
  }
}

const toggleBookmark = async () => {
  try {
    if (isBookmarked.value) {
      await caseApi.removeBookmark(caseId.value)
    } else {
      await caseApi.addBookmark(caseId.value)
    }
    isBookmarked.value = !isBookmarked.value
  } catch (e) {
    console.error('操作失败', e)
  }
}

const analyzeWithAI = () => {
  uni.navigateTo({
    url: `/pages/ai/chat?context=case&id=${caseId.value}`
  })
}
</script>

<style lang="scss" scoped>
.case-detail {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
}

.header {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.tags {
  display: flex;
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.tag {
  padding: 4rpx 16rpx;
  border-radius: 4rpx;
  font-size: 22rpx;
}

.tag.type {
  background: #e6f7ff;
  color: #1890ff;
}

.tag.status {
  background: #f6ffed;
  color: #52c41a;
}

.case-number {
  color: #999;
  font-size: 26rpx;
}

.title {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  font-size: 32rpx;
  font-weight: 500;
  line-height: 1.5;
  margin-bottom: 20rpx;
}

.info-grid {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24rpx;
}

.info-item {
  .label {
    color: #999;
    font-size: 24rpx;
    margin-bottom: 8rpx;
  }
  .value {
    color: #333;
    font-size: 28rpx;
  }
}

.section {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 500;
  margin-bottom: 16rpx;
  color: #333;
}

.section-content {
  font-size: 28rpx;
  line-height: 1.8;
  color: #666;
}

.actions {
  display: flex;
  gap: 20rpx;
  margin-top: 40rpx;
}

.action-btn {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
  background: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 44rpx;
  font-size: 30rpx;
}

.action-btn.primary {
  background: #1890ff;
  color: #fff;
  border: none;
}

.loading {
  text-align: center;
  padding: 100rpx;
  color: #999;
}
</style>