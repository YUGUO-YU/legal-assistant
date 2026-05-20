<template>
  <view class="lead-detail" v-if="lead">
    <view class="header">
      <view class="lead-title">{{ lead.title }}</view>
      <view class="lead-status" :class="lead.status">{{ getStatusText(lead.status) }}</view>
    </view>

    <view class="section">
      <view class="section-title">基本信息</view>
      <view class="info-list">
        <view class="info-item" v-if="lead.description">
          <text class="label">描述</text>
          <text class="value">{{ lead.description }}</text>
        </view>
        <view class="info-item" v-if="lead.source">
          <text class="label">来源</text>
          <text class="value">{{ lead.source }}</text>
        </view>
        <view class="info-item">
          <text class="label">创建时间</text>
          <text class="value">{{ lead.createdAt }}</text>
        </view>
        <view class="info-item">
          <text class="label">更新时间</text>
          <text class="value">{{ lead.updatedAt }}</text>
        </view>
      </view>
    </view>

    <view class="section" v-if="lead.tags && lead.tags.length">
      <view class="section-title">标签</view>
      <view class="tags">
        <text v-for="tag in lead.tags" :key="tag" class="tag">{{ tag }}</text>
      </view>
    </view>

    <view class="actions">
      <button class="action-btn" @click="showStatusPicker = true">修改状态</button>
      <button class="action-btn" @click="goToEdit">编辑</button>
      <button class="action-btn danger" @click="handleDelete">删除</button>
    </view>

    <uni-popup ref="statusPicker" type="bottom">
      <view class="status-picker">
        <view class="picker-header">
          <text>选择状态</text>
          <text @click="showStatusPicker = false">取消</text>
        </view>
        <view class="picker-options">
          <view
            v-for="status in statuses"
            :key="status.value"
            class="picker-item"
            @click="changeStatus(status.value)"
          >
            {{ status.label }}
          </view>
        </view>
      </view>
    </uni-popup>
  </view>
  <view v-else class="loading">加载中...</view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { leadApi, type LeadItem } from '@/services/lead'

const lead = ref<LeadItem | null>(null)
const showStatusPicker = ref(false)
const leadId = ref('')

const statuses = [
  { label: '新建', value: 'new' },
  { label: '跟进中', value: 'following' },
  { label: '已成交', value: 'closed' }
]

onMounted(async () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  leadId.value = currentPage.options?.id || ''

  if (leadId.value) {
    await loadLead()
  }
})

const loadLead = async () => {
  try {
    const res = await leadApi.getById(leadId.value)
    lead.value = res.data
  } catch (e) {
    console.error('加载案源详情失败', e)
  }
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    new: '新建',
    following: '跟进中',
    closed: '已成交'
  }
  return map[status] || status
}

const changeStatus = async (status: string) => {
  try {
    await leadApi.updateStatus(leadId.value, status)
    if (lead.value) {
      lead.value.status = status
    }
    showStatusPicker.value = false
  } catch (e) {
    console.error('更新状态失败', e)
  }
}

const goToEdit = () => {
  uni.navigateTo({
    url: `/pages/lead/edit?id=${leadId.value}`
  })
}

const handleDelete = async () => {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这个案源吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await leadApi.delete(leadId.value)
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
.lead-detail {
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

.lead-title {
  font-size: 32rpx;
  font-weight: 500;
  margin-bottom: 16rpx;
}

.lead-status {
  display: inline-block;
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

.info-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.info-item {
  display: flex;
  gap: 16rpx;
  font-size: 28rpx;
}

.info-item .label {
  color: #999;
  min-width: 120rpx;
}

.info-item .value {
  color: #333;
  flex: 1;
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

.status-picker {
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  padding-bottom: env(safe-area-inset-bottom);
}

.picker-header {
  display: flex;
  justify-content: space-between;
  padding: 24rpx;
  border-bottom: 1px solid #f0f0f0;
  font-size: 28rpx;
}

.picker-options {
  padding: 20rpx;
}

.picker-item {
  padding: 24rpx;
  text-align: center;
  font-size: 28rpx;
  border-bottom: 1px solid #f0f0f0;
}
</style>