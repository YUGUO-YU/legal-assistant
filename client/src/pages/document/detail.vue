<template>
  <view class="document-detail" v-if="doc">
    <view class="header">
      <view class="doc-title">{{ doc.title }}</view>
      <view class="doc-meta">
        <text class="doc-status" :class="doc.status">{{ getStatusText(doc.status) }}</text>
        <text class="doc-type">{{ doc.docType }}</text>
        <text class="doc-words">{{ doc.wordCount }}字</text>
      </view>
    </view>

    <view class="section">
      <view class="section-title">正文</view>
      <view class="doc-content">{{ doc.content }}</view>
    </view>

    <view class="section" v-if="doc.tags && doc.tags.length">
      <view class="section-title">标签</view>
      <view class="tags">
        <text v-for="tag in doc.tags" :key="tag" class="tag">{{ tag }}</text>
      </view>
    </view>

    <view class="section">
      <view class="section-title">历史版本</view>
      <view class="version-list">
        <view
          v-for="v in versions"
          :key="v.id"
          class="version-item"
          @click="restoreVersion(v.id)"
        >
          <view class="v-info">
            <text class="v-date">{{ v.createdAt }}</text>
            <text class="v-action">恢复此版本</text>
          </view>
        </view>
        <view v-if="versions.length === 0" class="empty-versions">暂无版本记录</view>
      </view>
    </view>

    <view class="info-row">
      <view class="info-item">
        <text class="label">创建时间</text>
        <text class="value">{{ doc.createdAt }}</text>
      </view>
      <view class="info-item">
        <text class="label">更新时间</text>
        <text class="value">{{ doc.updatedAt }}</text>
      </view>
    </view>

    <view class="actions">
      <button class="action-btn" @click="goToEdit">编辑</button>
      <button class="action-btn" @click="duplicateDoc">复制</button>
      <button class="action-btn danger" @click="handleDelete">删除</button>
    </view>
  </view>
  <view v-else class="loading">加载中...</view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { documentApi, type DocumentItem } from '@/services/document'

const doc = ref<DocumentItem | null>(null)
const versions = ref<any[]>([])
const docId = ref('')

onMounted(async () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  docId.value = currentPage.options?.id || ''

  if (docId.value) {
    await Promise.all([loadDoc(), loadVersions()])
  }
})

const loadDoc = async () => {
  try {
    const res = await documentApi.getById(docId.value)
    doc.value = res.data
  } catch (e) {
    console.error('加载文书详情失败', e)
  }
}

const loadVersions = async () => {
  try {
    const res = await documentApi.getVersions(docId.value)
    versions.value = res.data || []
  } catch (e) {
    console.error('加载版本失败', e)
  }
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

const goToEdit = () => {
  uni.navigateTo({
    url: `/pages/document/edit?id=${docId.value}`
  })
}

const duplicateDoc = async () => {
  if (!doc.value) return
  try {
    const res = await documentApi.create({
      title: doc.value.title + ' (副本)',
      content: doc.value.content,
      docType: doc.value.docType,
      tags: doc.value.tags
    })
    uni.showToast({ title: '已创建副本' })
    setTimeout(() => {
      uni.navigateTo({
        url: `/pages/document/detail?id=${res.data.id}`
      })
    }, 1000)
  } catch (e) {
    console.error('复制失败', e)
  }
}

const restoreVersion = async (versionId: string) => {
  uni.showModal({
    title: '确认恢复',
    content: '确定要恢复到此版本吗？当前内容将被覆盖',
    success: async (res) => {
      if (res.confirm) {
        try {
          await documentApi.restoreVersion(docId.value, versionId)
          uni.showToast({ title: '已恢复' })
          await loadDoc()
        } catch (e) {
          console.error('恢复失败', e)
        }
      }
    }
  })
}

const handleDelete = async () => {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这个文书吗？删除后不可恢复',
    success: async (res) => {
      if (res.confirm) {
        try {
          await documentApi.delete(docId.value)
          uni.navigateBack()
        } catch (e) {
          console.error('删除失败', e)
        }
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.document-detail {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
  padding-bottom: 120rpx;
}

.header {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.doc-title {
  font-size: 32rpx;
  font-weight: 500;
  margin-bottom: 16rpx;
}

.doc-meta {
  display: flex;
  gap: 24rpx;
  align-items: center;
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

.doc-type, .doc-words {
  font-size: 24rpx;
  color: #999;
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

.doc-content {
  font-size: 28rpx;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
}

.tags {
  display: flex;
  gap: 12rpx;
  flex-wrap: wrap;
}

.tag {
  padding: 8rpx 20rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
  font-size: 26rpx;
  color: #666;
}

.version-list {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.version-item {
  padding: 16rpx;
  background: #fafafa;
  border-radius: 8rpx;
}

.v-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.v-date {
  font-size: 26rpx;
  color: #666;
}

.v-action {
  font-size: 24rpx;
  color: #1890ff;
}

.empty-versions {
  text-align: center;
  padding: 20rpx;
  color: #999;
  font-size: 26rpx;
}

.info-row {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.info-item {
  display: flex;
  justify-content: space-between;
  font-size: 26rpx;
}

.info-item .label {
  color: #999;
}

.info-item .value {
  color: #666;
}

.actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 20rpx;
  padding: 20rpx;
  background: #fff;
  box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.05);
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

.action-btn.danger {
  color: #ff4d4f;
  border-color: #ff4d4f;
}

.loading {
  text-align: center;
  padding: 100rpx;
  color: #999;
}
</style>