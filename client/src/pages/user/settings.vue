<template>
  <view class="settings">
    <view class="section">
      <view class="section-title">账号设置</view>
      <view class="menu-list">
        <view class="menu-item" @click="changePassword">
          <text class="menu-text">修改密码</text>
          <text class="menu-arrow">></text>
        </view>
        <view class="menu-item" @click="bindPhone">
          <text class="menu-text">绑定手机</text>
          <text class="menu-value">{{ phone || '未绑定' }}</text>
        </view>
        <view class="menu-item" @click="bindEmail">
          <text class="menu-text">绑定邮箱</text>
          <text class="menu-value">{{ email || '未绑定' }}</text>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">通知设置</view>
      <view class="menu-list">
        <view class="menu-item">
          <text class="menu-text">消息推送</text>
          <switch :checked="notifications.message" @change="onMessageChange" />
        </view>
        <view class="menu-item">
          <text class="menu-text">案件提醒</text>
          <switch :checked="notifications.case" @change="onCaseChange" />
        </view>
        <view class="menu-item">
          <text class="menu-text">系统通知</text>
          <switch :checked="notifications.system" @change="onSystemChange" />
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">隐私设置</view>
      <view class="menu-list">
        <view class="menu-item">
          <text class="menu-text">允许他人查看我的案例</text>
          <switch :checked="privacy.allowViewCase" @change="onViewCaseChange" />
        </view>
        <view class="menu-item">
          <text class="menu-text">允许他人查看我的文书</text>
          <switch :checked="privacy.allowViewDoc" @change="onViewDocChange" />
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">其他</view>
      <view class="menu-list">
        <view class="menu-item" @click="clearCache">
          <text class="menu-text">清除缓存</text>
          <text class="menu-value">{{ cacheSize }}</text>
        </view>
        <view class="menu-item" @click="checkUpdate">
          <text class="menu-text">检查更新</text>
          <text class="menu-value">v{{ version }}</text>
        </view>
        <view class="menu-item">
          <text class="menu-text">关于我们</text>
          <text class="menu-arrow">></text>
        </view>
      </view>
    </view>

    <view class="logout-section">
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const phone = ref('')
const email = ref('')
const cacheSize = ref('0 KB')
const version = ref('1.0.0')

const notifications = reactive({
  message: true,
  case: true,
  system: true
})

const privacy = reactive({
  allowViewCase: false,
  allowViewDoc: false
})

onMounted(() => {
  loadSettings()
})

const loadSettings = () => {
  try {
    const userInfo = uni.getStorageInfoSync()
    const size = userInfo.size
    if (size < 1024) {
      cacheSize.value = size + ' B'
    } else if (size < 1024 * 1024) {
      cacheSize.value = Math.round(size / 1024) + ' KB'
    } else {
      cacheSize.value = Math.round(size / (1024 * 1024)) + ' MB'
    }
  } catch (e) {
    console.error('获取缓存大小失败', e)
  }
}

const changePassword = () => {
  uni.navigateTo({ url: '/pages/user/change-password' })
}

const bindPhone = () => {
  uni.navigateTo({ url: '/pages/user/bind-phone' })
}

const bindEmail = () => {
  uni.navigateTo({ url: '/pages/user/bind-email' })
}

const onMessageChange = (e: any) => {
  notifications.message = e.detail.value
}

const onCaseChange = (e: any) => {
  notifications.case = e.detail.value
}

const onSystemChange = (e: any) => {
  notifications.system = e.detail.value
}

const onViewCaseChange = (e: any) => {
  privacy.allowViewCase = e.detail.value
}

const onViewDocChange = (e: any) => {
  privacy.allowViewDoc = e.detail.value
}

const clearCache = () => {
  uni.showModal({
    title: '确认清除',
    content: '确定要清除缓存吗？',
    success: (res) => {
      if (res.confirm) {
        uni.clearStorageSync()
        cacheSize.value = '0 B'
        uni.showToast({ title: '缓存已清除' })
      }
    }
  })
}

const checkUpdate = () => {
  uni.showToast({ title: '已是最新版本', icon: 'none' })
}

const handleLogout = () => {
  uni.showModal({
    title: '确认退出',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        authStore.logout()
        uni.reLaunch({ url: '/pages/auth/login' })
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.settings {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;
}

.section {
  margin-bottom: 20rpx;
}

.section-title {
  padding: 24rpx 32rpx 16rpx;
  font-size: 26rpx;
  color: #999;
}

.menu-list {
  background: #fff;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1px solid #f0f0f0;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-text {
  font-size: 28rpx;
  color: #333;
}

.menu-value {
  font-size: 28rpx;
  color: #999;
}

.menu-arrow {
  font-size: 28rpx;
  color: #ccc;
}

.logout-section {
  padding: 40rpx 32rpx;
}

.logout-btn {
  height: 88rpx;
  line-height: 88rpx;
  background: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 44rpx;
  font-size: 30rpx;
  color: #ff4d4f;
}
</style>