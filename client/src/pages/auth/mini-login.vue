<template>
  <view class="mini-login">
    <view class="login-header">
      <view class="logo-icon">⚖️</view>
      <text class="app-name">法律助手</text>
    </view>

    <view class="login-content">
      <view class="welcome-text">
        <text class="title">欢迎使用</text>
        <text class="subtitle">一键登录，开启法律服务</text>
      </view>

      <view class="login-btn-wrapper">
        <button 
          class="login-btn" 
          type="primary" 
          open-type="getPhoneNumber" 
          @getphonenumber="onGetPhoneNumber"
          :loading="loading"
          :disabled="loading"
        >
          <text class="btn-icon">📱</text>
          <text class="btn-text">{{ loading ? '登录中...' : '微信一键登录' }}</text>
        </button>
      </view>

      <view class="agreement">
        <checkbox-group @change="onAgreementChange">
          <label class="agreement-label">
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

    <view class="login-footer">
      <view class="other-methods">
        <text class="divider-text">其他登录方式</text>
        <view class="methods">
          <view class="method-item" @click="navigateToPhoneLogin">
            <text class="method-icon">📱</text>
            <text class="method-text">手机登录</text>
          </view>
          <view class="method-item" @click="navigateToEmailLogin">
            <text class="method-icon">📧</text>
            <text class="method-text">邮箱登录</text>
          </view>
        </view>
      </view>
    </view>

    <view class="tips" v-if="showTips">
      <text class="tips-title">温馨提示</text>
      <text class="tips-text">微信一键登录仅需点击一次即可完成</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const loading = ref(false)
const agreed = ref(false)
const showTips = ref(true)

function onAgreementChange(e: any) {
  agreed.value = e.detail.value.length > 0
}

async function onGetPhoneNumber(e: any) {
  if (!agreed.value) {
    uni.showToast({ title: '请先同意用户协议', icon: 'none' })
    return
  }

  if (e.detail.errMsg !== 'getPhoneNumber:ok') {
    console.error('获取手机号失败', e.detail)
    uni.showToast({ title: '请允许获取手机号', icon: 'none' })
    return
  }

  loading.value = true

  try {
    // 通过 code 获取 session 和手机号
    const codeResponse = await wx.login()
    if (!codeResponse.code) {
      throw new Error('获取 code 失败')
    }

    // 调用后端接口
    const api = await import('@/services/api')
    const res = await api.default.post('/api/v1/auth/wechat/mini/login', {
      code: codeResponse.code,
      encryptedData: e.detail.encryptedData,
      iv: e.detail.iv
    })

    // 保存登录信息
    uni.setStorageSync('token', res.data.token)
    uni.setStorageSync('refreshToken', res.data.refreshToken)
    uni.setStorageSync('userInfo', res.data.user)

    uni.showToast({ title: '登录成功', icon: 'success' })

    // 跳转到首页
    setTimeout(() => {
      uni.switchTab({ url: '/pages/index/index' })
    }, 1000)

  } catch (error: any) {
    console.error('微信登录失败', error)
    uni.showToast({ 
      title: error.message || '登录失败，请重试', 
      icon: 'none' 
    })
  } finally {
    loading.value = false
  }
}

function navigateToPhoneLogin() {
  uni.navigateTo({ url: '/pages/auth/phone-login' })
}

function navigateToEmailLogin() {
  uni.navigateTo({ url: '/pages/auth/login' })
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
.mini-login {
  min-height: 100vh;
  background: linear-gradient(180deg, #f0f9ff 0%, #ffffff 50%, #ffffff 100%);
  display: flex;
  flex-direction: column;
  padding: 0 40rpx;
}

.login-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 160rpx;
  
  .logo-icon {
    font-size: 120rpx;
    margin-bottom: 24rpx;
    filter: drop-shadow(0 8rpx 20rpx rgba(24, 144, 255, 0.2));
  }
  
  .app-name {
    font-size: 40rpx;
    font-weight: 700;
    color: #1f2937;
    letter-spacing: 4rpx;
  }
}

.login-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 80rpx 0;
}

.welcome-text {
  text-align: center;
  margin-bottom: 80rpx;
  
  .title {
    display: block;
    font-size: 48rpx;
    font-weight: 700;
    color: #1f2937;
    margin-bottom: 16rpx;
  }
  
  .subtitle {
    display: block;
    font-size: 28rpx;
    color: #6b7280;
  }
}

.login-btn-wrapper {
  margin-bottom: 40rpx;
}

.login-btn {
  width: 100%;
  height: 96rpx;
  background: linear-gradient(135deg, #07c160 0%, #06ad56 100%);
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.35);
  
  .btn-icon {
    font-size: 40rpx;
    margin-right: 16rpx;
  }
  
  .btn-text {
    font-size: 32rpx;
    font-weight: 600;
    color: #ffffff;
  }
  
  &[disabled] {
    background: linear-gradient(135deg, #9ca3af 0%, #6b7280 100%);
    box-shadow: none;
  }
}

.agreement {
  padding: 0 20rpx;
  
  .agreement-label {
    display: flex;
    align-items: flex-start;
  }
  
  .agreement-text {
    font-size: 24rpx;
    color: #6b7280;
    line-height: 1.6;
    margin-left: 8rpx;
  }
  
  .link {
    color: #1890ff;
  }
}

.login-footer {
  padding: 40rpx 0 60rpx;
}

.other-methods {
  .divider-text {
    display: block;
    text-align: center;
    font-size: 24rpx;
    color: #9ca3af;
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
      font-size: 48rpx;
      margin-bottom: 12rpx;
    }
    
    .method-text {
      font-size: 24rpx;
      color: #6b7280;
    }
  }
}

.tips {
  position: fixed;
  bottom: 40rpx;
  left: 40rpx;
  right: 40rpx;
  background: rgba(24, 144, 255, 0.1);
  border-radius: 16rpx;
  padding: 24rpx;
  
  .tips-title {
    display: block;
    font-size: 26rpx;
    font-weight: 600;
    color: #1890ff;
    margin-bottom: 8rpx;
  }
  
  .tips-text {
    font-size: 24rpx;
    color: #666;
  }
}
</style>
