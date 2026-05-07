<template>
  <view class="law-detail" v-if="detail">
    <view class="header">
      <view class="tags">
        <text class="tag type">{{ detail.lawType }}</text>
        <text class="tag status" :class="detail.status">{{ detail.status }}</text>
      </view>
      <view class="law-number">{{ detail.lawNumber }}</view>
    </view>

    <view class="title">{{ detail.title }}</view>

    <view class="info-row">
      <view class="info-item">
        <text class="label">施行日期</text>
        <text class="value">{{ detail.effectiveDate }}</text>
      </view>
    </view>

    <view class="section" v-if="detail.chapters && detail.chapters.length">
      <view class="section-title">目录</view>
      <view class="toc">
        <view
          v-for="(chapter, index) in detail.chapters"
          :key="index"
          class="toc-item"
          @click="scrollToChapter(index)"
        >
          {{ chapter.title }}
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">正文</view>
      <view class="content">
        <view v-for="(chapter, cIndex) in detail.chapters" :key="cIndex" class="chapter">
          <view class="chapter-title">{{ chapter.title }}</view>
          <view
            v-for="(article, aIndex) in chapter.articles"
            :key="aIndex"
            class="article"
          >
            {{ article }}
          </view>
        </view>
      </view>
    </view>
  </view>
  <view v-else class="loading">加载中...</view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { lawApi } from '@/services/law'

const detail = ref<any>(null)

onMounted(async () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const id = currentPage.options?.id || ''

  if (id) {
    try {
      detail.value = await lawApi.getDetail(id)
    } catch (e) {
      console.error('加载法规详情失败', e)
    }
  }
})

const scrollToChapter = (index: number) => {
  uni.pageScrollTo({
    selector: `.chapter:nth-child(${index + 1})`,
    duration: 300
  })
}
</script>

<style lang="scss" scoped>
.law-detail {
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
  background: #fff7e6;
  color: #fa8c16;
}

.tag.status.effective {
  background: #f6ffed;
  color: #52c41a;
}

.tag.status.expired {
  background: #fff1f0;
  color: #ff4d4f;
}

.law-number {
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

.info-row {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  display: flex;
  gap: 40rpx;
}

.info-item {
  .label {
    color: #999;
    font-size: 24rpx;
    margin-right: 8rpx;
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

.toc {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.toc-item {
  padding: 12rpx 24rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
  font-size: 26rpx;
  color: #666;
}

.content {
  font-size: 28rpx;
  line-height: 1.8;
  color: #333;
}

.chapter {
  margin-bottom: 32rpx;
}

.chapter-title {
  font-size: 32rpx;
  font-weight: 500;
  margin-bottom: 16rpx;
  color: #1890ff;
}

.article {
  margin-bottom: 12rpx;
  text-indent: 2em;
}

.loading {
  text-align: center;
  padding: 100rpx;
  color: #999;
}
</style>