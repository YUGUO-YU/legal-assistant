<template>
  <view class="login-container">
    <view class="login-wrapper">
      <view class="login-header">
        <view class="logo-wrapper">
          <view class="logo-icon">⚖️</view>
          <text class="title">欢迎回来</text>
          <text class="subtitle">登录法律助手</text>
        </view>
      </view>

      <view class="login-form">
        <view class="form-title">
          <text class="title-icon">{{ loginMode === 'phone' ? '📱' : '📧' }}</text>
          {{ loginMode === 'phone' ? '手机号登录' : '邮箱验证码登录' }}
        </view>
        
        <template v-if="loginMode === 'phone'">
          <view class="form-item">
            <view class="input-wrapper">
              <text class="input-icon">📱</text>
              <input
                v-model="phone"
                type="tel"
                maxlength="11"
                placeholder="请输入手机号"
                class="input"
              />
            </view>
          </view>

          <view class="form-item">
            <view class="input-wrapper">
              <text class="input-icon">🔐</text>
              <input
                v-model="code"
                type="tel"
                maxlength="6"
                placeholder="请输入验证码"
                class="input"
              />
              <button 
                class="send-btn" 
                :class="{ disabled: countdown > 0 }" 
                @click="sendCode"
              >
                {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
              </button>
            </view>
          </view>

          <button class="login-btn" :disabled="loading" @click="handleLogin">
            <text class="btn-text">{{ loading ? '登录中...' : '立即登录' }}</text>
          </button>
        </template>
        
        <template v-else>
          <view class="form-item">
            <view class="input-wrapper">
              <text class="input-icon">📧</text>
              <input
                v-model="email"
                type="email"
                placeholder="请输入邮箱地址"
                class="input"
              />
            </view>
          </view>

          <view class="form-item">
            <view class="input-wrapper">
              <text class="input-icon">🔐</text>
              <input
                v-model="emailCode"
                type="tel"
                maxlength="6"
                placeholder="请输入验证码"
                class="input"
              />
              <button 
                class="send-btn" 
                :class="{ disabled: emailCountdown > 0 }" 
                @click="sendEmailCode"
              >
                {{ emailCountdown > 0 ? `${emailCountdown}s` : '发送验证码' }}
              </button>
            </view>
          </view>

          <button class="login-btn" :disabled="emailLoading" @click="handleEmailLogin">
            <text class="btn-text">{{ emailLoading ? '登录中...' : '立即登录' }}</text>
          </button>
        </template>

        <view class="switch-mode">
          <text class="switch-text" @click="switchMode">
            {{ loginMode === 'phone' ? '使用邮箱验证码登录' : '使用手机号登录' }}
          </text>
          <text class="switch-text divider">|</text>
          <text class="switch-text" @click="goToMiniLogin">小程序登录</text>
        </view>
        
        <view class="divider">
          <view class="divider-line"></view>
          <text class="divider-text">其他登录方式</text>
          <view class="divider-line"></view>
        </view>
        
        <view class="other-login">
          <view class="other-item" @click="goToMiniLogin">
            <view class="other-icon wx">💚</view>
            <text class="other-text">微信小程序</text>
          </view>
          <view class="other-item" @click="switchMode">
            <view class="other-icon email">📧</view>
            <text class="other-text">邮箱验证码</text>
          </view>
        </view>
      </view>

      <view class="login-footer">
        <text>登录即表示同意</text>
        <text class="link">《用户协议》</text>
        <text>和</text>
        <text class="link">《隐私政策》</text>
      </view>
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
const email = ref('')
const emailCode = ref('')
const emailLoading = ref(false)
const emailCountdown = ref(0)
const loginMode = ref<'phone' | 'email'>('phone')

let countdownTimer: ReturnType<typeof setInterval> | null = null
let emailCountdownTimer: ReturnType<typeof setInterval> | null = null

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

function startEmailCountdown() {
  emailCountdown.value = 60
  emailCountdownTimer = setInterval(() => {
    emailCountdown.value--
    if (emailCountdown.value <= 0) {
      emailCountdown.value = 0
      if (emailCountdownTimer) {
        clearInterval(emailCountdownTimer)
        emailCountdownTimer = null
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
  } catch (error: any) {
    uni.showToast({ title: error.message || '发送失败', icon: 'none' })
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
      uni.navigateTo({ url: '/pages/index/index' })
    }, 1000)
  } catch (error: any) {
    uni.showToast({ title: error.message || '登录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

async function sendEmailCode() {
  if (emailCountdown.value > 0 || !email.value) return

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(email.value)) {
    uni.showToast({ title: '请输入正确的邮箱格式', icon: 'none' })
    return
  }

  try {
    const api = await import('@/services/api')
    await api.default.post('/api/v1/auth/email/code/send', {
      email: email.value,
      type: 'login'
    })
    uni.showToast({ title: '验证码已发送到邮箱', icon: 'success' })
    startEmailCountdown()
  } catch (error: any) {
    uni.showToast({ title: error.message || '发送失败', icon: 'none' })
  }
}

async function handleEmailLogin() {
  if (!email.value || !emailCode.value) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' })
    return
  }

  emailLoading.value = true

  try {
    const api = await import('@/services/api')
    const res = await api.default.post('/api/v1/auth/email/code/login', {
      email: email.value,
      code: emailCode.value
    })
    
    uni.setStorageSync('token', res.data.token)
    uni.setStorageSync('refreshToken', res.data.refreshToken)
    uni.setStorageSync('userInfo', res.data.user)
    
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateTo({ url: '/pages/index/index' })
    }, 1000)
  } catch (error: any) {
    uni.showToast({ title: error.message || '登录失败', icon: 'none' })
  } finally {
    emailLoading.value = false
  }
}

function switchMode() {
  loginMode.value = loginMode.value === 'phone' ? 'email' : 'phone'
}

function goToMiniLogin() {
  uni.navigateTo({ url: '/pages/auth/mini-login' })
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #f0f9ff 0%, #ffffff 40%, #ffffff 100%);
  padding: 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-wrapper {
  width: 100%;
  max-width: 480px;
  padding: 0 20px;
}

.login-header {
  padding: 80rpx 0 50rpx;
  text-align: center;
}

.logo-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-icon {
  font-size: 80px;
  margin-bottom: 20px;
  filter: drop-shadow(0 8rpx 20rpx rgba(24, 144, 255, 0.2));
  animation: logoPulse 3s ease-in-out infinite;
}

@keyframes logoPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.02); }
}

.title {
  font-size: 36px;
  font-weight: 700;
  color: #1f2937;
  letter-spacing: 2px;
  margin: 0 0 10rpx 0;
}

.subtitle {
  margin: 0;
  font-size: 16px;
  color: #6b7280;
}

.login-form {
  background: #ffffff;
  border-radius: 16px;
  padding: 40rpx;
  box-shadow: 0 10rpx 40rpx rgba(0, 0, 0, 0.08);
}

.form-title {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 30rpx;
  padding-left: 12rpx;
  position: relative;
  display: flex;
  align-items: center;
  gap: 12rpx;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 24rpx;
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    border-radius: 2px;
  }
  
  .title-icon {
    font-size: 28px;
  }
}

.form-item {
  margin-bottom: 24rpx;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
  border: 2px solid transparent;
  transition: all 0.3s;

  &:focus-within {
    border-color: #1890ff;
    box-shadow: 0 4rpx 20rpx rgba(24, 144, 255, 0.2);
    transform: translateY(-2rpx);
  }
}

.input-wrapper {
  display: flex;
  align-items: center;
  padding: 6px 20rpx;
}

.input-icon {
  font-size: 24px;
  margin-right: 12rpx;
  flex-shrink: 0;
}

.input {
  flex: 1;
  height: 56px;
  font-size: 16px;
  color: #1f2937;
  border: none;
  outline: none;
  background: transparent;
  caret-color: #1890ff;

  &::placeholder {
    color: #9ca3af;
  }
}

.send-btn {
  flex-shrink: 0;
  padding: 10rpx 24rpx;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  color: #1890ff;
  font-size: 14px;
  font-weight: 600;
  border: none;
  border-radius: 9999px;
  transition: all 0.2s;
  cursor: pointer;

  &.disabled {
    background: #f3f4f6;
    color: #9ca3af;
    cursor: not-allowed;
  }
}

.login-btn {
  width: 100%;
  height: 56px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border-radius: 9999px;
  margin-top: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(24, 144, 255, 0.35);
  transition: all 0.3s;
  border: none;
  cursor: pointer;

  &:hover:not([disabled]) {
    transform: translateY(-3rpx);
    box-shadow: 0 12rpx 32rpx rgba(24, 144, 255, 0.45);
  }

  &[disabled] {
    background: linear-gradient(135deg, #d1d5db 0%, #9ca3af 100%);
    box-shadow: none;
    cursor: not-allowed;
  }

  .btn-text {
    font-size: 16px;
    font-weight: 700;
    color: #ffffff;
  }

  .btn-icon {
    font-size: 24px;
    color: #ffffff;
    margin-left: 8rpx;
  }
}

.switch-mode {
  text-align: center;
  margin-top: 30rpx;
  padding: 16rpx 0;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16rpx;
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%);
  border-radius: 12px;
}

.switch-text {
  font-size: 14px;
  color: #1890ff;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  padding: 8rpx 16rpx;
  border-radius: 8px;
  background: rgba(24, 144, 255, 0.08);

  &:hover {
    background: rgba(24, 144, 255, 0.15);
  }

  &.divider {
    color: #9ca3af;
    background: transparent;
    cursor: default;
  }
}

.divider {
  display: flex;
  align-items: center;
  margin: 30rpx 0 20rpx;

  .divider-line {
    flex: 1;
    height: 1px;
    background: linear-gradient(90deg, transparent 0%, #e5e7eb 20%, #d1d5db 50%, #e5e7eb 80%, transparent 100%);
  }

  .divider-text {
    padding: 0 20rpx;
    font-size: 13px;
    color: #9ca3af;
    font-weight: 500;
    background: #ffffff;
  }
}

.other-login {
  display: flex;
  justify-content: center;
  gap: 60px;
  margin-top: 20rpx;
}

.other-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    transform: translateY(-4rpx);
  }
}

.other-icon {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  border-radius: 50%;
  margin-bottom: 8rpx;
  transition: all 0.3s;
  background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
  
  &.wx {
    background: linear-gradient(135deg, #07c160 0%, #05a850 100%);
    box-shadow: 0 6rpx 16rpx rgba(7, 193, 96, 0.3);
  }
  
  &.email {
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    box-shadow: 0 6rpx 16rpx rgba(24, 144, 255, 0.3);
  }

  &:hover {
    transform: scale(1.1) rotate(5deg);
  }
}

.other-text {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.login-footer {
  position: fixed;
  bottom: 30rpx;
  left: 0;
  right: 0;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  color: #9ca3af;

  .link {
    color: #1890ff;
    text-decoration: none;
    font-weight: 500;

    &:hover {
      text-decoration: underline;
    }
  }
}

.qr-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.qr-modal {
  background: #ffffff;
  border-radius: 20px;
  padding: 30rpx;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.3);
}

.qr-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
  padding-bottom: 16rpx;
  border-bottom: 2px solid #f3f4f6;
}

.modal-title {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
}

.modal-close {
  font-size: 32px;
  color: #6b7280;
  cursor: pointer;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  border-radius: 50%;

  &:hover {
    background: #f3f4f6;
    color: #1f2937;
  }
}

@media (max-width: 768px) {
  .login-header {
    padding: 60rpx 0 40rpx;
  }

  .logo-icon {
    font-size: 70px;
  }

  .title {
    font-size: 32px;
  }

  .login-form {
    padding: 30rpx 24rpx;
  }

  .other-login {
    gap: 40px;
  }
}

@media (max-width: 480px) {
  .login-header {
    padding: 40rpx 0 30rpx;
  }

  .logo-icon {
    font-size: 60px;
  }

  .title {
    font-size: 28px;
  }

  .subtitle {
    font-size: 14px;
  }

  .login-form {
    padding: 24rpx 20rpx;
    border-radius: 12px;
  }

  .form-title {
    font-size: 20px;
  }

  .other-login {
    gap: 30px;
  }

  .other-icon {
    width: 56px;
    height: 56px;
    font-size: 28px;
  }

  .other-text {
    font-size: 12px;
  }
}
</style>
