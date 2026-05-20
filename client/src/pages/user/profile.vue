<template>
  <view class="profile" v-if="user">
    <view class="header-section">
      <view class="avatar-section" @click="changeAvatar">
        <image class="avatar" :src="user.avatar || defaultAvatar" mode="aspectFill" />
        <view class="avatar-edit">修改</view>
      </view>
      <view class="user-info">
        <view class="username">{{ user.username }}</view>
        <view class="role">{{ user.role === 'lawyer' ? '律师' : '用户' }}</view>
        <view class="firm" v-if="user.firmName">{{ user.firmName }}</view>
      </view>
    </view>

    <view class="stats-section">
      <view class="stat-item">
        <text class="stat-value">{{ stats.docCount }}</text>
        <text class="stat-label">文书</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ stats.caseCount }}</text>
        <text class="stat-label">案例</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ stats.leadCount }}</text>
        <text class="stat-label">案源</text>
      </view>
    </view>

    <view class="menu-section">
      <view class="menu-item" @click="goToPage('/pages/user/edit-profile')">
        <text class="menu-icon">&#xe61d;</text>
        <text class="menu-text">编辑资料</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="goToPage('/pages/document/list')">
        <text class="menu-icon">&#xe60f;</text>
        <text class="menu-text">我的文书</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="goToPage('/pages/case/search')">
        <text class="menu-icon">&#xe61c;</text>
        <text class="menu-text">收藏的案例</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="goToPage('/pages/lead/list')">
        <text class="menu-icon">&#xe620;</text>
        <text class="menu-text">案源管理</text>
        <text class="menu-arrow">></text>
      </view>
    </view>

    <view class="menu-section">
      <view class="menu-item">
        <text class="menu-icon">&#xe618;</text>
        <text class="menu-text">消息通知</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item">
        <text class="menu-icon">&#xe617;</text>
        <text class="menu-text">帮助中心</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item">
        <text class="menu-icon">&#xe619;</text>
        <text class="menu-text">关于我们</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="goToSettings">
        <text class="menu-icon">&#xe61a;</text>
        <text class="menu-text">设置</text>
        <text class="menu-arrow">></text>
      </view>
    </view>

    <view class="logout-section">
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </view>
  </view>
  <view v-else class="loading">加载中...</view>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { userApi, type UserProfile } from '@/services/user'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const user = ref<UserProfile | null>(null)

const defaultAvatar = '/static/images/default-avatar.png'

const stats = reactive({
  docCount: 0,
  caseCount: 0,
  leadCount: 0
})

onMounted(async () => {
  await loadProfile()
})

const loadProfile = async () => {
  try {
    const res = await userApi.getProfile()
    user.value = res.data
  } catch (e) {
    console.error('加载用户信息失败', e)
  }
}

const changeAvatar = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const tempFile = res.tempFilePaths[0]
      try {
        await userApi.updateAvatar(tempFile)
        if (user.value) {
          user.value.avatar = tempFile
        }
        uni.showToast({ title: '头像已更新' })
      } catch (e) {
        console.error('更新头像失败', e)
      }
    }
  })
}

const goToPage = (url: string) => {
  uni.navigateTo({ url })
}

const goToSettings = () => {
  uni.navigateTo({
    url: '/pages/user/settings'
  })
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
.profile {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;
}

.header-section {
  display: flex;
  align-items: center;
  padding: 60rpx 40rpx;
  background: linear-gradient(135deg, #1890ff 0%, #4d9fff 100%);
  color: #fff;
}

.avatar-section {
  position: relative;
  margin-right: 32rpx;
}

.avatar {
  width: 140rpx;
  height: 140rpx;
  border-radius: 70rpx;
  background: #fff;
}

.avatar-edit {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  padding: 4rpx 16rpx;
  background: rgba(0,0,0,0.5);
  border-radius: 20rpx;
  font-size: 20rpx;
}

.user-info {
  flex: 1;
}

.username {
  font-size: 36rpx;
  font-weight: 500;
  margin-bottom: 8rpx;
}

.role {
  display: inline-block;
  padding: 4rpx 16rpx;
  background: rgba(255,255,255,0.2);
  border-radius: 4rpx;
  font-size: 22rpx;
  margin-bottom: 8rpx;
}

.firm {
  font-size: 26rpx;
  opacity: 0.9;
}

.stats-section {
  display: flex;
  padding: 40rpx 20rpx;
  background: #fff;
  margin-bottom: 20rpx;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 40rpx;
  font-weight: 500;
  color: #1890ff;
  margin-bottom: 8rpx;
}

.stat-label {
  font-size: 24rpx;
  color: #999;
}

.menu-section {
  background: #fff;
  margin-bottom: 20rpx;
  padding-left: 40rpx;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 32rpx 40rpx 32rpx 0;
  border-bottom: 1px solid #f0f0f0;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  width: 48rpx;
  font-size: 36rpx;
  color: #1890ff;
  margin-right: 24rpx;
}

.menu-text {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.menu-arrow {
  color: #ccc;
  font-size: 28rpx;
}

.logout-section {
  padding: 40rpx;
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

.loading {
  text-align: center;
  padding: 100rpx;
  color: #999;
}
</style>