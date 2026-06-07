<template>
  <view class="phone-login">
    <view class="login-header">
      <view class="back-btn" @click="goBack">
        <text class="back-icon">←</text>
      </view>
      <text class="title">手机号登录</text>
    </view>

    <view class="login-form">
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
            :disabled="countdown > 0"
          >
            {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
          </button>
        </view>
      </view>

      <button class="login-btn" :disabled="loading" @click="handleLogin">
        {{ loading ? '登录中...' : '立即登录' }}
      </button>

      <view class="agreement">
        <checkbox-group @change="onAgreementChange">
          <label>
            <checkbox :checked="agreed" color="#1890ff" style="transform:scale(0.8)"/>
            <text class="agreement-text">
              登录即表示同意
              <text class="link" @tap.stop="openAgreement('user')">《用户协议》</text>
              和
              <text class="link" @tap.stop="openAgreement('privacy')">《隐私政策》</text>
            </text>
          </label>
        </checkbox-group>
      </view>
    </view>

    <view class="other-login">
      <text class="divider-text">其他登录方式</text>
      <view class="methods">
        <view class="method-item" @click="navigateToMiniLogin">
          <text class="method-icon">💚</text>
          <text class="method-text">微信登录</text>
        </view>
        <view class="method-item" @click="navigateToEmailLogin">
          <text class="method-icon">📧</text>
          <text class="method-text">邮箱登录</text>
        </view>
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
const agreed = ref(false)

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
  } catch (error: any) {
    uni.showToast({ title: error.message || '发送失败', icon: 'none' })
  }
}

async function handleLogin() {
  if (!agreed.value) {
    uni.showToast({ title: '请先同意用户协议', icon: 'none' })
    return
  }

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
    }, 1000)
  } catch (error: any) {
    uni.showToast({ title: error.message || '登录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function onAgreementChange(e: any) {
  agreed.value = e.detail.value.length > 0
}

function goBack() {
  uni.navigateBack()
}

function navigateToMiniLogin() {
  uni.redirectTo({ url: '/pages/auth/mini-login' })
}

function navigateToEmailLogin() {
  uni.redirectTo({ url: '/pages/auth/login' })
}

function openAgreement(type: string) {
  if (type === 'user') {
    uni.showModal({
      title: '用户协议',
      content: '法律助手用户协议内容...',
      showCancel: false
    })
  } else {
    uni.showModal({
      title: '隐私政策',
      content: '法律助手隐私政策内容...',
      showCancel: false
    })
  }
}
</script>

<style lang="scss" scoped>
.phone-login {
  min-height: 100vh;
  background: #ffffff;
  padding: 0 40rpx;
}

.login-header {
  display: flex;
  align-items: center;
  padding: 40rpx 0;
  
  .back-btn {
    width: 64rpx;
    height: 64rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .back-icon {
      font-size: 40rpx;
      color: #333;
    }
  }
  
  .title {
    font-size: 36rpx;
    font-weight: 600;
    color: #1f2937;
    margin-left: 20rpx;
  }
}

.login-form {
  padding: 40rpx 0;
}

.form-item {
  margin-bottom: 32rpx;
  background: #f5f5f5;
  border-radius: 16rpx;
  
  .input-wrapper {
    display: flex;
    align-items: center;
    padding: 24rpx;
  }
  
  .input-icon {
    font-size: 36rpx;
    margin-right: 16rpx;
  }
  
  .input {
    flex: 1;
    height: 48rpx;
    font-size: 28rpx;
    background: transparent;
    border: none;
    outline: none;
  }
  
  .send-btn {
    flex-shrink: 0;
    padding: 12rpx 24rpx;
    background: #1890ff;
    color: #fff;
    font-size: 24rpx;
    border-radius: 8rpx;
    border: none;
    
    &.disabled {
      background: #ccc;
    }
  }
}

.login-btn {
  width: 100%;
  height: 96rpx;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: #fff;
  font-size: 32rpx;
  font-weight: 600;
  border-radius: 48rpx;
  border: none;
  margin-top: 40rpx;
  
  &[disabled] {
    background: #ccc;
  }
}

.agreement {
  margin-top: 32rpx;
  padding: 0 8rpx;
  
  .agreement-text {
    font-size: 24rpx;
    color: #666;
    margin-left: 8rpx;
  }
  
  .link {
    color: #1890ff;
  }
}

.other-login {
  padding: 60rpx 0;
  text-align: center;
  
  .divider-text {
    font-size: 24rpx;
    color: #999;
    display: block;
    margin-bottom: 40rpx;
  }
  
  .methods {
    display: flex;
    justify-content: center;
    gap: 80rpx;
  }
  
  .method-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .method-icon {
      font-size: 56rpx;
      margin-bottom: 12rpx;
    }
    
    .method-text {
      font-size: 24rpx;
      color: #666;
    }
  }
}
</style>
