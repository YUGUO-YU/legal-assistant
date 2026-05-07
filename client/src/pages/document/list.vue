<template>
  <view class="document-list">
    <view class="search-bar">
      <input
        v-model="keyword"
        class="search-input"
        placeholder="搜索文书标题"
        @confirm="handleSearch"
      />
    </view>

    <view class="filter-bar">
      <view
        v-for="tab in tabs"
        :key="tab.value"
        class="tab"
        :class="{ active: currentCategory === tab.value }"
        @click="onTabChange(tab.value)"
      >
        {{ tab.label }}
      </view>
    </view>

    <scroll-view
      class="doc-scroll"
      scroll-y
      @scrolltolower="loadMore"
    >
      <view
        v-for="item in documents"
        :key="item.id"
        class="doc-item"
        @click="goToDetail(item.id)"
      >
        <view class="doc-header">
          <text class="doc-title">{{ item.title }}</text>
          <text class="doc-status" :class="item.status">{{ getStatusText(item.status) }}</text>
        </view>
        <view class="doc-preview" v-if="item.content">
          {{ item.content.substring(0, 100) }}...
        </view>
        <view class="doc-meta">
          <text class="doc-type">{{ item.docType }}</text>
          <text class="doc-words">{{ item.wordCount }}字</text>
          <text class="doc-date">{{ item.updatedAt }}</text>
        </view>
        <view class="doc-tags" v-if="item.tags && item.tags.length">
          <text v-for="tag in item.tags" :key="tag" class="tag">{{ tag }}</text>
        </view>
      </view>

      <view v-if="loading" class="loading">加载中...</view>
      <view v-if="noMore && documents.length > 0" class="no-more">没有更多了</view>
      <view v-if="!loading && documents.length === 0" class="empty">
        <text>暂无文书</text>
        <text class="empty-hint">点击右下角按钮创建新文书</text>
      </view>
    </scroll-view>

    <view class="add-btn" @click="goToCreate">+</view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { documentApi, type DocumentItem } from '@/services/document'

const tabs = [
  { label: '全部', value: '' },
  { label: '合同', value: 'contract' },
  { label: '协议', value: 'agreement' },
  { label: '函件', value: 'letter' },
  { label: '其他', value: 'other' }
]

const keyword = ref('')
const currentCategory = ref('')
const documents = ref<DocumentItem[]>([])
const loading = ref(false)
const noMore = ref(false)
const page = ref(1)
const pageSize = ref(20)

onMounted(() => {
  loadDocuments()
})

const handleSearch = () => {
  documents.value = []
  page.value = 1
  noMore.value = false
  loadDocuments()
}

const loadDocuments = async () => {
  if (loading.value) return
  loading.value = true

  try {
    const res = await documentApi.getList(
      currentCategory.value || undefined,
      page.value,
      pageSize.value
    )

    if (page.value === 1) {
      documents.value = res.list || []
    } else {
      documents.value.push(...(res.list || []))
    }

    noMore.value = (res.list || []).length < pageSize.value
  } catch (e) {
    console.error('加载文书失败', e)
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (noMore.value || loading.value) return
  page.value++
  loadDocuments()
}

const onTabChange = (category: string) => {
  currentCategory.value = category
  documents.value = []
  page.value = 1
  noMore.value = false
  loadDocuments()
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    draft: '草稿',
    review: '审核中',
    approved: '已定稿',
    archived: '已归档'
  }
  return map[status] || status
}

const goToDetail = (id: string) => {
  uni.navigateTo({
    url: `/pages/document/detail?id=${id}`
  })
}

const goToCreate = () => {
  uni.navigateTo({
    url: '/pages/document/edit'
  })
}
</script>

<style lang="scss" scoped>
.document-list {
  min-height: 100vh;
  background: #f5f5f5;
}

.search-bar {
  padding: 20rpx;
  background: #fff;
}

.search-input {
  height: 72rpx;
  padding: 0 24rpx;
  background: #f5f5f5;
  border-radius: 36rpx;
  font-size: 28rpx;
}

.filter-bar {
  display: flex;
  padding: 0 20rpx 20rpx;
  background: #fff;
  gap: 16rpx;
}

.tab {
  height: 64rpx;
  line-height: 64rpx;
  padding: 0 24rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
  font-size: 26rpx;
  color: #666;
}

.tab.active {
  background: #1890ff;
  color: #fff;
}

.doc-scroll {
  height: calc(100vh - 260rpx);
  padding: 20rpx;
}

.doc-item {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.doc-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12rpx;
}

.doc-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  flex: 1;
}

.doc-status {
  padding: 4rpx 16rpx;
  border-radius: 4rpx;
  font-size: 22rpx;
}

.doc-status.draft {
  background: #f5f5f5;
  color: #999;
}

.doc-status.review {
  background: #fff7e6;
  color: #fa8c16;
}

.doc-status.approved {
  background: #f6ffed;
  color: #52c41a;
}

.doc-status.archived {
  background: #e6f7ff;
  color: #1890ff;
}

.doc-preview {
  font-size: 26rpx;
  color: #666;
  line-height: 1.5;
  margin-bottom: 12rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.doc-meta {
  display: flex;
  gap: 24rpx;
  font-size: 24rpx;
  color: #999;
  margin-bottom: 12rpx;
}

.doc-tags {
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

.empty {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.empty-hint {
  font-size: 24rpx;
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