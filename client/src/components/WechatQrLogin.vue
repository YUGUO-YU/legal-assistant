<template>
  <div class="wechat-qr-login">
    <div class="qr-header">
      <div class="qr-icon">💚</div>
      <h2 class="qr-title">微信扫码登录</h2>
      <p class="qr-subtitle">使用微信扫描二维码登录</p>
    </div>
    
    <div class="qr-container" v-if="qrCodeUrl">
      <!-- 二维码图片 -->
      <div class="qr-code-wrapper">
        <img 
          :src="qrCodeUrl" 
          class="qr-image"
          @error="handleQrError"
          alt="微信登录二维码"
        />
      </div>
      
      <!-- 状态提示 -->
      <div class="qr-status" :class="status">
        <span v-if="status === 'waiting'">📱 等待扫码</span>
        <span v-else-if="status === 'scanned'">✓ 已扫码，请确认登录</span>
        <span v-else-if="status === 'confirmed'">✓✓ 登录成功</span>
        <span v-else-if="status === 'expired'">⚠ 二维码已过期</span>
      </div>
      
      <!-- 刷新按钮 -->
      <button class="refresh-btn" @click="refreshQrCode" v-if="status === 'expired'">
        <span class="refresh-icon">🔄</span>
        <span>点击刷新二维码</span>
      </button>
    </div>
    
    <div class="qr-loading" v-else>
      <div class="loading-spinner"></div>
      <p>正在加载二维码...</p>
    </div>
    
    <div class="qr-tips">
      <p class="tip-title">💡 小贴士</p>
      <p class="tip-text">请使用微信扫描二维码完成登录</p>
      <p class="tip-text-small">二维码有效期 5 分钟，过期请刷新</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import api from '@/services/api'

const qrCodeUrl = ref('')
const scene = ref('')
const status = ref<'waiting' | 'scanned' | 'confirmed' | 'expired'>('waiting')
let pollTimer: ReturnType<typeof setInterval> | null = null
const POLL_INTERVAL = 2000 // 2 秒轮询一次
const isH5 = computed(() => {
  // #ifdef H5
  return true
  // #endif
  return false
})

// 生成二维码
async function generateQrCode() {
  try {
    const res = await api.get<{
      qrCodeUrl: string
      scene: string
      expiresIn: number
    }>('/api/v1/auth/wechat/qr/generate')
    
    qrCodeUrl.value = res.data.qrCodeUrl
    scene.value = res.data.scene
    status.value = 'waiting'
    
    // 开始轮询状态
    startPolling()
  } catch (error: any) {
    console.error('生成二维码失败', error)
    alert('加载二维码失败：' + (error.message || '未知错误'))
  }
}

// 轮询二维码状态
async function checkQrStatus() {
  if (!scene.value) return
  
  try {
    const res = await api.post<{
      status: string
      message: string
      user?: any
    }>('/api/v1/auth/wechat/qr/status', {
      scene: scene.value
    })
    
    const newStatus = res.data.status as typeof status.value
    status.value = newStatus
    
    if (newStatus === 'confirmed' && res.data.user) {
      // 登录成功
      stopPolling()
      
      // 保存 token
      localStorage.setItem('token', res.data.user.token)
      localStorage.setItem('refreshToken', res.data.user.refreshToken)
      localStorage.setItem('userInfo', JSON.stringify(res.data.user.user))
      
      alert('✓ 登录成功！')
      
      // 跳转到首页
      setTimeout(() => {
        window.location.href = '/'
      }, 1000)
    } else if (newStatus === 'expired') {
      // 二维码过期
      stopPolling()
    }
  } catch (error) {
    console.error('查询二维码状态失败', error)
  }
}

// 开始轮询
function startPolling() {
  stopPolling()
  pollTimer = setInterval(checkQrStatus, POLL_INTERVAL)
}

// 停止轮询
function stopPolling() {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

// 刷新二维码
function refreshQrCode() {
  status.value = 'waiting'
  generateQrCode()
}

// 二维码加载失败
function handleQrError(e: any) {
  console.error('二维码加载失败', e)
  // 二维码 URL 是微信的 OAuth URL，不能直接作为图片显示
  // 我们需要使用二维码生成服务或者显示提示
}

onMounted(() => {
  generateQrCode()
})

onUnmounted(() => {
  stopPolling()
})

// 暴露刷新方法
defineExpose({
  refreshQrCode
})
</script>

<style lang="scss" scoped>
.wechat-qr-login {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  max-width: 500px;
  margin: 0 auto;
}

.qr-header {
  text-align: center;
  margin-bottom: 40px;
}

.qr-icon {
  font-size: 60px;
  margin-bottom: 12px;
  display: block;
}

.qr-title {
  display: block;
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 8px;
  margin-top: 0;
}

.qr-subtitle {
  display: block;
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.qr-container {
  background: #ffffff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  box-sizing: border-box;
}

.qr-code-wrapper {
  width: 280px;
  height: 280px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #bae7ff;
}

.qr-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.qr-status {
  font-size: 16px;
  margin-bottom: 20px;
  padding: 12px 24px;
  border-radius: 9999px;
  text-align: center;
  width: 100%;
  box-sizing: border-box;
  
  &.waiting {
    color: #1890ff;
    background: #e6f7ff;
  }
  
  &.scanned {
    color: #fa8c16;
    background: #fff7e6;
  }
  
  &.confirmed {
    color: #52c41a;
    background: #f6ffed;
  }
  
  &.expired {
    color: #ff4d4f;
    background: #fff1f0;
  }
}

.refresh-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: #ffffff;
  border: none;
  border-radius: 9999px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(24, 144, 255, 0.4);
  }
  
  &:active {
    transform: translateY(0);
  }
}

.refresh-icon {
  font-size: 18px;
}

.qr-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px 40px;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #e5e7eb;
  border-top-color: #1890ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.qr-tips {
  margin-top: 40px;
  text-align: center;
  
  .tip-title {
    font-size: 14px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 8px 0;
  }
  
  .tip-text {
    font-size: 13px;
    color: #6b7280;
    margin: 4px 0;
  }
  
  .tip-text-small {
    font-size: 12px;
    color: #9ca3af;
    margin: 4px 0 0 0;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .wechat-qr-login {
    padding: 20px;
  }
  
  .qr-icon {
    font-size: 48px;
  }
  
  .qr-title {
    font-size: 24px;
  }
  
  .qr-container {
    padding: 24px;
  }
  
  .qr-code-wrapper {
    width: 240px;
    height: 240px;
  }
}

@media (max-width: 480px) {
  .qr-code-wrapper {
    width: 200px;
    height: 200px;
  }
  
  .qr-status {
    font-size: 14px;
    padding: 10px 20px;
  }
  
  .refresh-btn {
    padding: 10px 20px;
    font-size: 13px;
  }
}
</style>
