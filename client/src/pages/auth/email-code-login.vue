<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="login-header">
        <div class="logo-wrapper">
          <div class="logo-icon">⚖️</div>
          <h1 class="title">邮箱验证码登录</h1>
          <p class="subtitle">使用邮箱和验证码快速登录</p>
        </div>
      </div>

      <div class="login-form">
        <h2 class="form-title">登录账号</h2>
        
        <div class="form-item">
          <div class="input-wrapper">
            <div class="input-icon">📧</div>
            <input
              v-model="email"
              type="email"
              placeholder="请输入邮箱地址"
              class="input"
            />
          </div>
        </div>

        <div class="form-item">
          <div class="input-wrapper">
            <div class="input-icon">🔐</div>
            <input
              v-model="code"
              type="text"
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
              {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
            </button>
          </div>
        </div>

        <button class="login-btn" :disabled="loading" @click="handleLogin">
          <span class="btn-text">{{ loading ? '登录中...' : '立即登录' }}</span>
          <span class="btn-icon">→</span>
        </button>

        <div class="switch-mode">
          <span class="switch-text" @click="switchToPhone">
            使用手机验证码登录
          </span>
          <span class="switch-text divider" @click="switchToEmailPassword">
            使用邮箱密码登录
          </span>
        </div>

        <div class="tips">
          <p class="tip-text">💡 提示</p>
          <p class="tip-text-small">验证码已发送到您的邮箱，有效期 5 分钟</p>
          <p class="tip-text-small">如果是首次登录，将自动创建账号</p>
        </div>
      </div>

      <div class="login-footer">
        <span>登录即表示同意</span>
        <a href="#" class="link">《用户协议》</a>
        <span>和</span>
        <a href="#" class="link">《隐私政策》</a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import api from '@/services/api'

const email = ref('')
const code = ref('')
const loading = ref(false)
const countdown = ref(0)

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
  if (countdown.value > 0 || !email.value) return

  // 简单的邮箱格式验证
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(email.value)) {
    alert('请输入正确的邮箱格式')
    return
  }

  try {
    await api.post('/api/v1/auth/email/code/send', {
      email: email.value,
      type: 'login'
    })
    alert('验证码已发送到邮箱')
    startCountdown()
  } catch (error: any) {
    console.error(error)
    alert(error.message || '发送失败')
  }
}

async function handleLogin() {
  if (!email.value || !code.value) {
    alert('请填写完整信息')
    return
  }

  loading.value = true

  try {
    const res = await api.post('/api/v1/auth/email/code/login', {
      email: email.value,
      code: code.value
    })
    
    // 保存 token
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('refreshToken', res.data.refreshToken)
    localStorage.setItem('userInfo', JSON.stringify(res.data.user))
    
    alert('✓ 登录成功')
    
    // 跳转到首页
    window.location.href = '/'
  } catch (error: any) {
    console.error(error)
    alert(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

function switchToPhone() {
  window.location.href = '/pages/auth/login'
}

function switchToEmailPassword() {
  window.location.href = '/pages/auth/email-login'
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
  padding: 20px;
}

.login-header {
  padding: 60px 0 40px;
  text-align: center;
}

.logo-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-icon {
  font-size: 70px;
  margin-bottom: 16px;
  filter: drop-shadow(0 8px 20px rgba(24, 144, 255, 0.2));
}

.title {
  font-size: 32px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  color: #6b7280;
}

.login-form {
  background: #ffffff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.form-title {
  font-size: 22px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 24px;
  padding-left: 12px;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 20px;
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    border-radius: 2px;
  }
}

.form-item {
  margin-bottom: 20px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 2px solid transparent;
  transition: all 0.2s;

  &:focus-within {
    border-color: #1890ff;
    box-shadow: 0 2px 16px rgba(24, 144, 255, 0.15);
  }
}

.input-wrapper {
  display: flex;
  align-items: center;
  padding: 6px 16px;
}

.input-icon {
  font-size: 20px;
  margin-right: 12px;
  flex-shrink: 0;
}

.input {
  flex: 1;
  height: 48px;
  font-size: 14px;
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
  padding: 8px 20px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  color: #1890ff;
  font-size: 13px;
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

  &:hover:not(.disabled) {
    transform: scale(1.05);
  }

  &:active:not(.disabled) {
    transform: scale(0.98);
  }
}

.login-btn {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border-radius: 9999px;
  margin-top: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 20px rgba(24, 144, 255, 0.3);
  transition: all 0.2s;
  border: none;
  cursor: pointer;

  &:hover:not([disabled]) {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(24, 144, 255, 0.4);
  }

  &:active:not([disabled]) {
    transform: translateY(0);
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
    font-size: 20px;
    color: #ffffff;
    margin-left: 6px;
    animation: pulse-arrow 2s ease-in-out infinite;
  }
}

@keyframes pulse-arrow {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(3px); }
}

.switch-mode {
  text-align: center;
  margin-top: 24px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
}

.switch-text {
  font-size: 13px;
  color: #1890ff;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    opacity: 0.8;
  }

  &.divider {
    color: #6b7280;
  }

  &::after {
    content: '|';
    margin-left: 12px;
    color: #d1d5db;
  }

  &:last-child::after {
    content: '';
  }
}

.tips {
  margin-top: 20px;
  padding: 16px;
  background: #f0f9ff;
  border-radius: 8px;
  
  .tip-text {
    font-size: 13px;
    color: #1890ff;
    margin: 4px 0;
  }
  
  .tip-text-small {
    font-size: 12px;
    color: #6b7280;
    margin: 4px 0 0 0;
  }
}

.login-footer {
  position: fixed;
  bottom: 24px;
  left: 0;
  right: 0;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 12px;
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

@media (max-width: 768px) {
  .logo-icon {
    font-size: 60px;
  }

  .title {
    font-size: 28px;
  }

  .login-form {
    padding: 24px 20px;
  }
}
</style>
