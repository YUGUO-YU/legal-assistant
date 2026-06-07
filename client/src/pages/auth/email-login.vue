<template>
  <view class="container">
    <view class="header">
      <view class="logo-wrapper">
        <view class="logo-icon">⚖️</view>
        <text class="title">邮箱登录</text>
        <text class="subtitle">使用邮箱和密码登录法律助手</text>
      </view>
    </view>

    <view class="form">
      <view class="form-item">
        <view class="input-wrapper">
          <view class="input-icon">📧</view>
          <input
            v-model="email"
            type="text"
            placeholder="请输入邮箱地址"
            class="input"
          />
        </view>
      </view>

      <view class="form-item">
        <view class="input-wrapper">
          <view class="input-icon">🔐</view>
          <input
            v-model="password"
            type="password"
            placeholder="请输入密码"
            class="input"
          />
        </view>
      </view>

      <view class="forgot-password">
        <text class="link" @click="handleForgotPassword">忘记密码？</text>
      </view>

      <button class="login-btn" :disabled="loading" @click="handleLogin">
        <text class="btn-text">{{ loading ? '登录中...' : '立即登录' }}</text>
        <text class="btn-icon">→</text>
      </button>

      <view class="switch-mode">
        <text class="switch-text" @click="switchMode">
          使用手机验证码登录
        </text>
      </view>
    </view>

    <view class="footer">
      <text class="footer-text">还没有账号？</text>
      <text class="link" @click="goToRegister">立即注册</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const loading = ref(false)

async function handleLogin() {
  if (!email.value || !password.value) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' })
    return
  }

  // 简单的邮箱格式验证
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(email.value)) {
    uni.showToast({ title: '请输入正确的邮箱格式', icon: 'none' })
    return
  }

  loading.value = true

  try {
    await authStore.loginByEmail(email.value, password.value)
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      uni.switchTab({ url: '/pages/index/index' })
    }, 1500)
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

function switchMode() {
  uni.navigateBack()
}

function handleForgotPassword() {
  uni.showToast({ title: '请联系客服重置密码', icon: 'none' })
}

function goToRegister() {
  uni.navigateTo({ url: '/pages/auth/register' })
}
</script>

<style lang="scss" scoped>
@import '@/style/variables.scss';

.container {
  min-height: 100vh;
  background: linear-gradient(180deg, #f0f9ff 0%, #ffffff 40%, #ffffff 100%);
  padding: 0;
}

.header {
  padding: 120rpx 48rpx 80rpx;
  text-align: center;
}

.logo-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-icon {
  font-size: 100rpx;
  margin-bottom: 32rpx;
  filter: drop-shadow(0 8rpx 20rpx rgba(24, 144, 255, 0.2));
}

.title {
  font-size: 52rpx;
  font-weight: 700;
  color: $text-primary;
  letter-spacing: 2rpx;
}

.subtitle {
  margin-top: 16rpx;
  font-size: 28rpx;
  color: $text-secondary;
}

.form {
  padding: 0 48rpx;
  margin-bottom: 60rpx;
}

.form-item {
  margin-bottom: 32rpx;
  background: $background-white;
  border-radius: $radius-lg;
  box-shadow: $shadow-md;
  border: 2rpx solid transparent;
  transition: all $transition-fast;

  &:focus-within {
    border-color: $primary-color;
    box-shadow: $shadow-primary;
  }
}

.input-wrapper {
  display: flex;
  align-items: center;
  padding: 8rpx 24rpx;
}

.input-icon {
  font-size: 40rpx;
  margin-right: 20rpx;
  flex-shrink: 0;
}

.input {
  flex: 1;
  height: 96rpx;
  font-size: 32rpx;
  color: $text-primary;
}

.forgot-password {
  text-align: right;
  padding: 0 16rpx;
  margin-bottom: 32rpx;
}

.link {
  font-size: 28rpx;
  color: $primary-color;
  font-weight: 500;
}

.login-btn {
  width: 100%;
  height: 108rpx;
  background: $primary-gradient;
  border-radius: $radius-round;
  margin-top: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 12rpx 28rpx rgba(24, 144, 255, 0.35);
  transition: all $transition-base;
  border: none;

  &:active:not([disabled]) {
    transform: scale(0.98);
    box-shadow: 0 8rpx 20rpx rgba(24, 144, 255, 0.25);
  }

  &[disabled] {
    background: linear-gradient(135deg, #d1d5db 0%, #9ca3af 100%);
    box-shadow: none;
  }

  .btn-text {
    font-size: 34rpx;
    font-weight: 700;
    color: #ffffff;
  }

  .btn-icon {
    font-size: 40rpx;
    color: #ffffff;
    margin-left: 12rpx;
    animation: pulse-arrow 2s ease-in-out infinite;
  }
}

@keyframes pulse-arrow {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(8rpx); }
}

.switch-mode {
  text-align: center;
  margin-top: 40rpx;
}

.switch-text {
  font-size: 28rpx;
  color: $primary-color;
  font-weight: 500;
}

.footer {
  position: fixed;
  bottom: 60rpx;
  left: 0;
  right: 0;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
}

.footer-text {
  font-size: 28rpx;
  color: $text-placeholder;
}

.link {
  font-size: 28rpx;
  color: $primary-color;
  font-weight: 500;
}
</style>
