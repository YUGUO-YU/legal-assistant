<template>
  <view class="container">
    <view class="header">
      <text class="title">欢迎回来</text>
      <text class="subtitle">登录法律助手</text>
    </view>

    <view class="form">
      <view class="form-item">
        <input
          v-model="phone"
          type="number"
          maxlength="11"
          placeholder="请输入手机号"
          class="input"
        />
      </view>

      <view class="form-item">
        <input
          v-model="code"
          type="number"
          maxlength="6"
          placeholder="请输入验证码"
          class="input"
        />
        <view class="send-btn" :class="{ disabled: countdown > 0 }" @click="sendCode">
          {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
        </view>
      </view>

      <button class="login-btn" :disabled="loading" @click="handleLogin">
        {{ loading ? '登录中...' : '登录' }}
      </button>

      <view class="switch-mode">
        <text class="switch-text" @click="switchMode">
          {{ loginMode === 'phone' ? '使用邮箱密码登录' : '使用手机验证码登录' }}
        </text>
      </view>
    </view>

    <view class="footer">
      <text class="footer-text">登录即表示同意</text>
      <text class="link">《用户协议》</text>
      <text class="footer-text">和</text>
      <text class="link">《隐私政策》</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const phone = ref('')
const code = ref('')
const loading = ref(false)
const countdown = ref(0)
const loginMode = ref<'phone' | 'email'>('phone')

let countdownTimer: ReturnType<typeof setInterval> | null = null

function startCountdown() {
  countdown.value = 60
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      countdown.value = 0
      if (countdownTimer) {
        clearInterval(countdownTimer)
        countdownTimer = null
      }
    }
  }, 1000)
}

async function sendCode() {
  if (countdown.value > 0 || !phone.value) return

  try {
    await authStore.sendSms(phone.value)
    uni.showToast({ title: '验证码已发送', icon: 'success' })
    startCountdown()
  } catch (error) {
    console.error(error)
  }
}

async function handleLogin() {
  if (!phone.value || !code.value) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' })
    return
  }

  loading.value = true

  try {
    await authStore.loginByPhone(phone.value, code.value)
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
  loginMode.value = loginMode.value === 'phone' ? 'email' : 'phone'
  if (loginMode.value === 'email') {
    uni.navigateTo({ url: '/pages/auth/email-login' })
  }
}
</script>

<style lang="scss" scoped>
.container {
  padding: 80rpx 48rpx;
  min-height: 100vh;
  background-color: #ffffff;
}

.header {
  margin-bottom: 80rpx;
}

.title {
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  color: #333;
}

.subtitle {
  display: block;
  margin-top: 16rpx;
  font-size: 28rpx;
  color: #999;
}

.form {
  margin-bottom: 60rpx;
}

.form-item {
  display: flex;
  align-items: center;
  border-bottom: 1rpx solid #e5e5e5;
  padding: 24rpx 0;
  margin-bottom: 24rpx;
}

.input {
  flex: 1;
  font-size: 32rpx;
  color: #333;
}

.send-btn {
  font-size: 28rpx;
  color: #1890ff;
  white-space: nowrap;
}

.send-btn.disabled {
  color: #cccccc;
}

.login-btn {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  background: #1890ff;
  color: #ffffff;
  font-size: 32rpx;
  border-radius: 48rpx;
  margin-top: 48rpx;
}

.login-btn[disabled] {
  background: #cccccc;
}

.switch-mode {
  text-align: center;
  margin-top: 32rpx;
}

.switch-text {
  font-size: 26rpx;
  color: #1890ff;
}

.footer {
  text-align: center;
  position: fixed;
  bottom: 60rpx;
  left: 0;
  right: 0;
}

.footer-text {
  font-size: 24rpx;
  color: #999999;
}

.link {
  font-size: 24rpx;
  color: #1890ff;
}
</style>