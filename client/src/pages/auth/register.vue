<template>
  <view class="register">
    <view class="header">
      <text class="title">注册账号</text>
      <text class="subtitle">创建您的法律助手账号</text>
    </view>

    <view class="form">
      <view class="form-item">
        <view class="label">用户名</view>
        <input
          v-model="form.username"
          class="input"
          placeholder="请输入用户名"
        />
      </view>

      <view class="form-item">
        <view class="label">手机号</view>
        <input
          v-model="form.phone"
          class="input"
          type="number"
          placeholder="请输入手机号"
        />
      </view>

      <view class="form-item">
        <view class="label">验证码</view>
        <view class="code-input">
          <input
            v-model="form.code"
            class="input"
            type="number"
            placeholder="请输入验证码"
          />
          <button class="code-btn" @click="sendCode" :disabled="countdown > 0">
            {{ countdown > 0 ? '${countdown}s' : '获取验证码' }}
          </button>
        </view>
      </view>

      <view class="form-item">
        <view class="label">密码</view>
        <input
          v-model="form.password"
          class="input"
          password
          placeholder="请输入密码"
        />
      </view>

      <view class="form-item">
        <view class="label">确认密码</view>
        <input
          v-model="form.confirmPassword"
          class="input"
          password
          placeholder="请再次输入密码"
        />
      </view>

      <view class="form-item">
        <view class="label">身份</view>
        <picker mode="selector" :range="roles" @change="onRoleChange">
          <view class="picker-value">
            {{ form.role || '请选择身份' }}
          </view>
        </picker>
      </view>

      <view class="agreement">
        <checkbox-group @change="onAgreeChange">
          <label>
            <checkbox value="agree" :checked="agreed" />
            <text class="agreement-text">
              我已阅读并同意 <text class="link">《用户协议》</text> 和 <text class="link">《隐私政策》</text>
            </text>
          </label>
        </checkbox-group>
      </view>

      <button class="register-btn" @click="handleRegister" :disabled="registering || !canRegister">
        {{ registering ? '注册中...' : '注册' }}
      </button>

      <view class="login-link">
        已有账号？<text @click="goToLogin">立即登录</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { request } from '@/services/api'

const form = reactive({
  username: '',
  phone: '',
  code: '',
  password: '',
  confirmPassword: '',
  role: ''
})

const agreed = ref(false)
const registering = ref(false)
const countdown = ref(0)

const roles = ['律师', '企业用户', '个人用户']

const canRegister = computed(() => {
  return (
    form.username &&
    form.phone &&
    form.code &&
    form.password &&
    form.confirmPassword &&
    form.role &&
    agreed.value
  )
})

const onRoleChange = (e: any) => {
  form.role = roles[e.detail.value]
}

const onAgreeChange = (e: any) => {
  agreed.value = e.detail.value.includes('agree')
}

const sendCode = async () => {
  if (!form.phone) {
    uni.showToast({ title: '请输入手机号', icon: 'none' })
    return
  }

  try {
    await request({
      url: '/api/v1/auth/sms/send',
      method: 'POST',
      data: { phone: form.phone }
    })
    uni.showToast({ title: '验证码已发送' })
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (e) {
    console.error('发送验证码失败', e)
  }
}

const handleRegister = async () => {
  if (form.password !== form.confirmPassword) {
    uni.showToast({ title: '两次密码输入不一致', icon: 'none' })
    return
  }

  if (form.password.length < 6) {
    uni.showToast({ title: '密码长度不能少于6位', icon: 'none' })
    return
  }

  registering.value = true

  try {
    await request({
      url: '/api/v1/auth/register',
      method: 'POST',
      data: {
        username: form.username,
        phone: form.phone,
        code: form.code,
        password: form.password,
        role: form.role === '律师' ? 'lawyer' : 'user'
      }
    })

    uni.showToast({ title: '注册成功' })
    setTimeout(() => {
      uni.navigateTo({ url: '/pages/auth/login' })
    }, 1500)
  } catch (e) {
    console.error('注册失败', e)
  } finally {
    registering.value = false
  }
}

const goToLogin = () => {
  uni.navigateTo({ url: '/pages/auth/login' })
}
</script>

<style lang="scss" scoped>
.register {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 40rpx;
}

.header {
  padding: 60rpx 0;
  text-align: center;
}

.title {
  display: block;
  font-size: 48rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 16rpx;
}

.subtitle {
  font-size: 28rpx;
  color: #999;
}

.form {
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
}

.form-item {
  margin-bottom: 32rpx;
}

.label {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 16rpx;
  font-weight: 500;
}

.input {
  height: 88rpx;
  padding: 0 24rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  font-size: 28rpx;
}

.code-input {
  display: flex;
  gap: 20rpx;
}

.code-input .input {
  flex: 1;
}

.code-btn {
  width: 240rpx;
  height: 88rpx;
  line-height: 88rpx;
  background: #1890ff;
  color: #fff;
  border-radius: 12rpx;
  font-size: 26rpx;
}

.code-btn[disabled] {
  background: #ccc;
}

.picker-value {
  height: 88rpx;
  padding: 0 24rpx;
  line-height: 88rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #666;
}

.agreement {
  margin-bottom: 40rpx;
}

.agreement-text {
  font-size: 26rpx;
  color: #666;
  margin-left: 12rpx;
}

.link {
  color: #1890ff;
}

.register-btn {
  height: 88rpx;
  line-height: 88rpx;
  background: #1890ff;
  color: #fff;
  border-radius: 44rpx;
  font-size: 32rpx;
  margin-bottom: 32rpx;
}

.register-btn[disabled] {
  background: #ccc;
}

.login-link {
  text-align: center;
  font-size: 26rpx;
  color: #999;
}

.login-link text {
  color: #1890ff;
}
</style>