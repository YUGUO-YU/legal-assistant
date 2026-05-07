<template>
  <view class="company-detail" v-if="detail">
    <view class="header">
      <view class="company-name">{{ detail.name }}</view>
      <view class="company-status" :class="detail.status">{{ detail.status }}</view>
    </view>

    <view class="basic-info">
      <view class="info-grid">
        <view class="info-item">
          <text class="label">统一社会信用代码</text>
          <text class="value">{{ detail.creditCode }}</text>
        </view>
        <view class="info-item">
          <text class="label">法定代表人</text>
          <text class="value">{{ detail.legalPerson }}</text>
        </view>
        <view class="info-item">
          <text class="label">注册资本</text>
          <text class="value">{{ detail.capital }}</text>
        </view>
        <view class="info-item">
          <text class="label">成立日期</text>
          <text class="value">{{ detail.establishDate }}</text>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">经营范围</view>
      <view class="section-content">{{ detail.businessScope }}</view>
    </view>

    <view class="section" v-if="detail.shareholders && detail.shareholders.length">
      <view class="section-title">股东信息</view>
      <view class="shareholder-list">
        <view
          v-for="(sh, index) in detail.shareholders"
          :key="index"
          class="shareholder-item"
        >
          <view class="sh-name">{{ sh.name }}</view>
          <view class="sh-info">
            <text>持股比例: {{ sh.sharePercent }}%</text>
            <text>认缴出资: {{ sh.capital }}</text>
          </view>
        </view>
      </view>
    </view>

    <view class="section" v-if="detail.risks && detail.risks.length">
      <view class="section-title">风险信息</view>
      <view class="risk-list">
        <view
          v-for="(risk, index) in detail.risks"
          :key="index"
          class="risk-item"
        >
          <view class="risk-header">
            <text class="risk-type">{{ risk.type }}</text>
            <text class="risk-date">{{ risk.date }}</text>
          </view>
          <view class="risk-desc">{{ risk.description }}</view>
        </view>
      </view>
    </view>

    <view class="section" v-if="detail.lawsuits && detail.lawsuits.length">
      <view class="section-title">法律诉讼</view>
      <view class="lawsuit-list">
        <view
          v-for="(lawsuit, index) in detail.lawsuits"
          :key="index"
          class="lawsuit-item"
        >
          <view class="lawsuit-title">{{ lawsuit.title }}</view>
          <view class="lawsuit-info">
            <text>{{ lawsuit.caseNumber }}</text>
            <text>{{ lawsuit.date }}</text>
          </view>
        </view>
      </view>
    </view>

    <view class="actions">
      <button class="action-btn" @click="viewGraph">查看企业图谱</button>
      <button class="action-btn primary" @click="analyzeWithAI">AI分析</button>
    </view>
  </view>
  <view v-else class="loading">加载中...</view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { companyApi } from '@/services/company'

const detail = ref<any>(null)
const companyId = ref('')

onMounted(async () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  companyId.value = currentPage.options?.id || ''

  if (companyId.value) {
    await loadDetail()
  }
})

const loadDetail = async () => {
  try {
    detail.value = await companyApi.getDetail(companyId.value)
  } catch (e) {
    console.error('加载企业详情失败', e)
  }
}

const viewGraph = () => {
  uni.navigateTo({
    url: `/pages/company/graph?id=${companyId.value}`
  })
}

const analyzeWithAI = () => {
  uni.navigateTo({
    url: `/pages/ai/chat?context=company&id=${companyId.value}`
  })
}
</script>

<style lang="scss" scoped>
.company-detail {
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

.company-name {
  font-size: 34rpx;
  font-weight: 500;
  margin-bottom: 16rpx;
}

.company-status {
  display: inline-block;
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

.basic-info {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24rpx;
}

.info-item {
  .label {
    display: block;
    color: #999;
    font-size: 24rpx;
    margin-bottom: 8rpx;
  }
  .value {
    display: block;
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
  line-height: 1.6;
  color: #666;
}

.shareholder-list, .risk-list, .lawsuit-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.shareholder-item {
  padding: 16rpx;
  background: #fafafa;
  border-radius: 8rpx;
}

.sh-name {
  font-size: 28rpx;
  font-weight: 500;
  margin-bottom: 8rpx;
}

.sh-info {
  display: flex;
  gap: 24rpx;
  font-size: 24rpx;
  color: #666;
}

.risk-item {
  padding: 16rpx;
  background: #fff1f0;
  border-radius: 8rpx;
}

.risk-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8rpx;
}

.risk-type {
  color: #ff4d4f;
  font-size: 26rpx;
  font-weight: 500;
}

.risk-date {
  color: #999;
  font-size: 24rpx;
}

.risk-desc {
  font-size: 26rpx;
  color: #666;
  line-height: 1.5;
}

.lawsuit-item {
  padding: 16rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
}

.lawsuit-title {
  font-size: 28rpx;
  margin-bottom: 8rpx;
}

.lawsuit-info {
  display: flex;
  justify-content: space-between;
  font-size: 24rpx;
  color: #999;
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