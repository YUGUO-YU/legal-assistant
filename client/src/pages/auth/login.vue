<template>
  <div class="login-container">
    <!-- H5 端微信扫码弹窗 -->
    <div class="qr-modal-overlay" v-if="showQrLogin" @click="closeQrLogin">
      <div class="qr-modal" @click.stop>
        <div class="qr-modal-header">
          <h3 class="modal-title">微信扫码登录</h3>
          <button class="modal-close" @click="closeQrLogin">×</button>
        </div>
        <WechatQrLogin ref="qrLoginRef" />
      </div>
    </div>

    <div class="login-wrapper">
      <div class="login-header">
        <div class="logo-wrapper">
          <div class="logo-icon">⚖️</div>
          <h1 class="title">欢迎回来</h1>
          <p class="subtitle">登录法律助手</p>
        </div>
      </div>

      <div class="login-form">
        <h2 class="form-title">
          <span class="title-icon">{{ loginMode === 'phone' ? '📱' : '📧' }}</span>
          {{ loginMode === 'phone' ? '手机号登录' : '邮箱验证码登录' }}
        </h2>
        
        <!-- 手机号登录表单 -->
        <template v-if="loginMode === 'phone'">
          <div class="form-item">
            <div class="input-wrapper">
              <div class="input-icon">📱</div>
              <input
                v-model="phone"
                type="tel"
                maxlength="11"
                placeholder="请输入手机号"
                class="input"
              />
            </div>
          </div>

          <div class="form-item">
            <div class="input-wrapper">
              <div class="input-icon">🔐</div>
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
                {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
              </button>
            </div>
          </div>

          <button class="login-btn" :disabled="loading" @click="handleLogin">
            <span class="btn-text">{{ loading ? '登录中...' : '立即登录' }}</span>
            <span class="btn-icon">→</span>
          </button>
        </template>
        
        <!-- 邮箱验证码登录表单 -->
        <template v-else>
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
                :disabled="emailCountdown > 0"
              >
                {{ emailCountdown > 0 ? `${emailCountdown}s` : '发送验证码' }}
              </button>
            </div>
          </div>

          <button class="login-btn" :disabled="emailLoading" @click="handleEmailLogin">
            <span class="btn-text">{{ emailLoading ? '登录中...' : '立即登录' }}</span>
            <span class="btn-icon">→</span>
          </button>
        </template>

        <div class="switch-mode">
          <span class="switch-text" @click="switchMode">
            {{ loginMode === 'phone' ? '使用邮箱验证码登录' : '使用手机号登录' }}
          </span>
          <span class="switch-text divider" @click="showQrLogin = true">
            扫码登录
          </span>
        </div>
        
        <div class="divider">
          <div class="divider-line"></div>
          <span class="divider-text">其他登录方式</span>
          <div class="divider-line"></div>
        </div>
        
        <div class="other-login">
          <div class="other-item" @click="showQrLogin = true">
            <div class="other-icon wx">💚</div>
            <span class="other-text">微信扫码</span>
          </div>
          <div class="other-item" @click="emailCodeLogin">
            <div class="other-icon email">📧</div>
            <span class="other-text">邮箱验证码</span>
          </div>
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
import { useAuthStore } from '@/stores/auth'
import WechatQrLogin from '@/components/WechatQrLogin.vue'

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
const showQrLogin = ref(false)
const qrLoginRef = ref()

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
    alert('验证码已发送')
    startCountdown()
  } catch (error: any) {
    console.error(error)
    alert(error.message || '发送失败')
  }
}

async function handleLogin() {
  if (!phone.value || !code.value) {
    alert('请填写完整信息')
    return
  }

  loading.value = true

  try {
    await authStore.loginByPhone(phone.value, code.value)
    alert('登录成功')
    window.location.href = '/'
  } catch (error: any) {
    console.error(error)
    alert(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

async function sendEmailCode() {
  if (emailCountdown.value > 0 || !email.value) return

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(email.value)) {
    alert('请输入正确的邮箱格式')
    return
  }

  try {
    const api = await import('@/services/api')
    await api.default.post('/api/v1/auth/email/code/send', {
      email: email.value,
      type: 'login'
    })
    alert('验证码已发送到邮箱')
    startEmailCountdown()
  } catch (error: any) {
    console.error(error)
    alert(error.message || '发送失败')
  }
}

async function handleEmailLogin() {
  if (!email.value || !emailCode.value) {
    alert('请填写完整信息')
    return
  }

  emailLoading.value = true

  try {
    const api = await import('@/services/api')
    const res = await api.default.post('/api/v1/auth/email/code/login', {
      email: email.value,
      code: emailCode.value
    })
    
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('refreshToken', res.data.refreshToken)
    localStorage.setItem('userInfo', JSON.stringify(res.data.user))
    
    alert('登录成功')
    window.location.href = '/'
  } catch (error: any) {
    console.error(error)
    alert(error.message || '登录失败')
  } finally {
    emailLoading.value = false
  }
}

function phoneLogin() {
  loginMode.value = 'phone'
}

function switchMode() {
  loginMode.value = loginMode.value === 'phone' ? 'email' : 'phone'
}

function emailCodeLogin() {
  loginMode.value = 'email'
}

function closeQrLogin() {
  showQrLogin.value = false
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
  padding: 80px 0 50px;
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
  filter: drop-shadow(0 8px 20px rgba(24, 144, 255, 0.2));
  animation: logoPulse 3s ease-in-out infinite;
}

@keyframes logoPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.02);
  }
}

.title {
  font-size: 36px;
  font-weight: 700;
  color: #1f2937;
  letter-spacing: 2px;
  margin: 0 0 10px 0;
  animation: titleFloat 3s ease-in-out infinite;
}

@keyframes titleFloat {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

.subtitle {
  margin: 0;
  font-size: 16px;
  color: #6b7280;
}

.login-form {
  background: #ffffff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

// 表单切换动画
.form-transition {
  &-enter-active,
  &-leave-active {
    transition: all 0.3s ease;
  }

  &-enter-from {
    opacity: 0;
    transform: translateX(-20px);
  }

  &-leave-to {
    opacity: 0;
    transform: translateX(20px);
  }
}

.form-title {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 30px;
  padding-left: 12px;
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 24px;
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    border-radius: 2px;
  }
  
  .title-icon {
    font-size: 28px;
  }
}

.form-item {
  margin-bottom: 24px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 2px solid transparent;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  animation: fadeInUp 0.4s ease-out backwards;

  &:nth-child(2) {
    animation-delay: 0.1s;
  }

  &:nth-child(3) {
    animation-delay: 0.2s;
  }

  &:focus-within {
    border-color: #1890ff;
    box-shadow: 0 4px 20px rgba(24, 144, 255, 0.2);
    transform: translateY(-2px);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.input-wrapper {
  display: flex;
  align-items: center;
  padding: 6px 20px;
}

.input-icon {
  font-size: 24px;
  margin-right: 12px;
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
  padding: 10px 24px;
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

  &:hover:not(.disabled) {
    transform: scale(1.05);
  }

  &:active:not(.disabled) {
    transform: scale(0.98);
  }
}

.login-btn {
  width: 100%;
  height: 56px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border-radius: 9999px;
  margin-top: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(24, 144, 255, 0.35);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  cursor: pointer;
  position: relative;
  overflow: hidden;

  &:hover:not([disabled]) {
    transform: translateY(-3px);
    box-shadow: 0 12px 32px rgba(24, 144, 255, 0.45);
  }

  &:active:not([disabled]) {
    transform: translateY(-1px);
    box-shadow: 0 6px 20px rgba(24, 144, 255, 0.3);
  }

  &[disabled] {
    background: linear-gradient(135deg, #d1d5db 0%, #9ca3af 100%);
    box-shadow: none;
    cursor: not-allowed;
    opacity: 0.7;
  }

  .btn-text {
    font-size: 16px;
    font-weight: 700;
    color: #ffffff;
    transition: all 0.2s;
  }

  .btn-icon {
    font-size: 24px;
    color: #ffffff;
    margin-left: 8px;
    transition: all 0.3s;
  }

  &:hover:not([disabled]) .btn-icon {
    transform: translateX(4px);
  }

  &[loading] {
    &::before {
      content: '';
      position: absolute;
      width: 20px;
      height: 20px;
      border: 3px solid rgba(255, 255, 255, 0.3);
      border-top-color: #ffffff;
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    }

    .btn-text, .btn-icon {
      opacity: 0;
    }
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
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
    font-size: 18px;
    font-weight: 700;
    color: #ffffff;
  }

  .btn-icon {
    font-size: 24px;
    color: #ffffff;
    margin-left: 8px;
    animation: pulse-arrow 2s ease-in-out infinite;
  }
}

@keyframes pulse-arrow {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(4px); }
}

.switch-mode {
  text-align: center;
  margin-top: 30px;
  padding: 16px 0;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0;
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%);
  border-radius: 12px;
}

.switch-text {
  font-size: 14px;
  color: #1890ff;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  padding: 8px 16px;
  border-radius: 8px;
  background: rgba(24, 144, 255, 0.08);

  &:hover {
    background: rgba(24, 144, 255, 0.15);
    transform: translateY(-1px);
  }

  &:active {
    transform: translateY(0);
  }

  &.divider {
    color: #9ca3af;
    background: transparent;
    cursor: default;
    pointer-events: none;

    &:hover {
      background: transparent;
      transform: none;
    }
  }

  &::after {
    content: '|';
    margin-left: 16px;
    color: #d1d5db;
  }

  &:last-child::after {
    content: '';
  }
}

.divider {
  display: flex;
  align-items: center;
  margin: 30px 0 20px;
  position: relative;

  .divider-line {
    flex: 1;
    height: 1px;
    background: linear-gradient(90deg, transparent 0%, #e5e7eb 20%, #d1d5db 50%, #e5e7eb 80%, transparent 100%);
  }

  .divider-text {
    padding: 0 20px;
    font-size: 13px;
    color: #9ca3af;
    font-weight: 500;
    background: #ffffff;
    position: relative;
    z-index: 1;
  }
}

.other-login {
  display: flex;
  justify-content: center;
  gap: 60px;
  margin-top: 20px;
}

.other-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    transform: translateY(-4px);
  }

  &:active {
    transform: translateY(-2px);
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
  margin-bottom: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  
  &.wx {
    background: linear-gradient(135deg, #07c160 0%, #05a850 100%);
    color: #ffffff;
    box-shadow: 0 6px 16px rgba(7, 193, 96, 0.3);
  }
  
  &.email {
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    color: #ffffff;
    box-shadow: 0 6px 16px rgba(24, 144, 255, 0.3);
  }

  &:hover {
    transform: scale(1.1) rotate(5deg);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  }
}

.other-text {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
  transition: all 0.2s;
}

.other-item:hover .other-text {
  color: #1f2937;
}

  .other-icon {
    width: 64px;
    height: 64px;
    border-radius: 50%;
    background: #f3f4f6;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32px;
    margin-bottom: 10px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    transition: all 0.2s;

    &.wx {
      background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
    }

    &.phone {
      background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
    }
  }

  .other-text {
    font-size: 13px;
    color: #6b7280;
  }
}

.login-footer {
  position: fixed;
  bottom: 30px;
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

/* 扫码登录弹窗 */
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
  padding: 30px;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: modalSlideIn 0.3s ease-out;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.qr-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 16px;
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
  transition: all 0.2s;

  &:hover {
    background: #f3f4f6;
    color: #1f2937;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-header {
    padding: 60px 0 40px;
  }

  .logo-icon {
    font-size: 70px;
  }

  .title {
    font-size: 32px;
  }

  .login-form {
    padding: 30px 24px;
  }

  .other-login {
    gap: 40px;
  }

  .input {
    height: 50px;
    font-size: 15px;
  }

  .send-btn {
    padding: 8px 18px;
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .login-header {
    padding: 40px 0 30px;
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
    padding: 24px 20px;
    border-radius: 12px;
  }

  .form-title {
    font-size: 20px;
  }

  .form-item {
    border-radius: 8px;
  }

  .input {
    height: 48px;
    font-size: 14px;
  }

  .input-icon {
    font-size: 20px;
    margin-right: 10px;
  }

  .send-btn {
    padding: 6px 14px;
    font-size: 12px;
  }

  .login-btn {
    height: 48px;
    margin-top: 30px;
    font-size: 15px;
  }

  .switch-mode {
    flex-direction: column;
    gap: 8px;
    padding: 12px;

    .switch-text {
      width: 100%;
      text-align: center;

      &::after {
        content: '';
      }
    }
  }

  .other-login {
    gap: 30px;
  }

  .other-icon {
    width: 56px !important;
    height: 56px !important;
    font-size: 28px !important;
  }

  .other-text {
    font-size: 12px !important;
  }

  .qr-modal {
    max-width: 95%;
    padding: 24px;
  }
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
    padding: 24px 20px;
    border-radius: 12px;
  }

  .form-item {
    border-radius: 8px;
  }

  .login-btn {
    height: 50px;
    margin-top: 30px;
  }

  .btn-text {
    font-size: 16px !important;
  }

  .switch-mode {
    flex-direction: column;
    gap: 8px;
  }

  .switch-text::after {
    content: '';
  }

  .other-login {
    gap: 30px;
  }

  .other-icon {
    width: 48px !important;
    height: 48px !important;
    font-size: 24px !important;
  }

  .other-text {
    font-size: 12px !important;
  }

  .login-footer {
    bottom: 20px;
    flex-wrap: wrap;
  }
}
</style>
