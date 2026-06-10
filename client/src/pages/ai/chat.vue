<template>
  <view class="ai-chat">
    <scroll-view
      class="chat-scroll"
      scroll-y
      :scroll-top="scrollTop"
      :scroll-into-view="scrollIntoView"
    >
      <view class="messages">
        <view
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message', msg.role]"
          :id="'msg-' + index"
        >
          <view class="avatar">
            {{ msg.role === 'user' ? '我' : 'AI' }}
          </view>
          <view class="content">
            <view class="text" v-if="msg.displayedContent !== undefined">
              <rich-text :nodes="formatContent(msg.displayedContent)"></rich-text>
              <view class="typing-cursor" v-if="msg.isTyping"></view>
            </view>
            <view class="loading" v-else-if="msg.isLoading">
              <view class="loading-dots">
                <text class="dot">●</text>
                <text class="dot">●</text>
                <text class="dot">●</text>
              </view>
              <text class="loading-text">AI 思考中...</text>
            </view>
            <view class="sources" v-if="msg.sources && msg.sources.length > 0">
              <view class="sources-title">📚 参考资料</view>
              <view class="sources-note">以下为相关法律法规及案例摘要，仅供参考</view>
              <view
                v-for="(source, idx) in msg.sources"
                :key="idx"
                class="source-item"
              >
                <text class="source-icon">{{ source.type === 'case' ? '📋' : '📜' }}</text>
                <text class="source-text">{{ source.title }}</text>
                <text class="source-type">{{ source.type === 'case' ? '案例' : '法规' }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <view class="input-area">
      <input
        v-model="inputText"
        class="input"
        placeholder="输入法律问题..."
        @confirm="sendMessage"
        :disabled="sending"
      />
      <button class="send-btn" @click="sendMessage" :disabled="sending || !inputText">
        <text v-if="sending" class="btn-loading">● ●</text>
        <text v-else>发送</text>
      </button>
    </view>

    <view class="quick-questions" v-if="messages.length === 0">
      <view class="quick-title">试试问这些问题:</view>
      <view class="quick-list">
        <view
          v-for="q in quickQuestions"
          :key="q"
          class="quick-item"
          @click="askQuestion(q)"
        >
          {{ q }}
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'

interface Source {
  title: string
  type: 'case' | 'law' | 'company'
  id?: string
}

interface Message {
  role: 'user' | 'assistant'
  content: string
  displayedContent?: string
  isTyping?: boolean
  isLoading?: boolean
  sources?: Source[]
}

const messages = ref<Message[]>([] as Message[])
const inputText = ref('')
const sending = ref(false)
const scrollTop = ref(0)
const scrollIntoView = ref('')

const quickQuestions = [
  '劳动纠纷怎么处理？',
  '民间借贷利息怎么计算？',
  '房屋租赁合同要注意什么？',
  '交通事故责任如何划分？'
]

onMounted(() => {
  const token = uni.getStorageSync('token')
  if (!token) {
    uni.navigateTo({ url: '/pages/auth/login' })
    return
  }

  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const query = currentPage.options?.query || ''

  if (query) {
    inputText.value = decodeURIComponent(query)
    setTimeout(() => {
      sendMessage()
    }, 300)
  }

  const context = currentPage.options?.context || ''

  if (context === 'case') {
    messages.value.push({
      role: 'assistant',
      content: '您好，我已加载该案例信息。请问您想了解案例的哪些方面？比如案件分析、判决预测、法律建议等。',
      displayedContent: '您好，我已加载该案例信息。请问您想了解案例的哪些方面？比如案件分析、判决预测、法律建议等。'
    })
  } else if (context === 'company') {
    messages.value.push({
      role: 'assistant',
      content: '您好，我已加载该企业信息。请问您想了解企业的哪些方面？比如企业风险分析，合作建议、法律尽职调查等。',
      displayedContent: '您好，我已加载该企业信息。请问您想了解企业的哪些方面？比如企业风险分析，合作建议、法律尽职调查等。'
    })
  }
})

const askQuestion = (question: string) => {
  inputText.value = question
  sendMessage()
}

const sendMessage = async () => {
  if (!inputText.value || sending.value) return

  const question = inputText.value
  inputText.value = ''
  sending.value = true

  messages.value.push({
    role: 'user',
    content: question,
    displayedContent: question
  })

  await scrollToBottom()

  messages.value.push({
    role: 'assistant',
    content: '',
    isLoading: true,
    sources: []
  })

  try {
    const { content, sources } = await callAI(question)
    const lastIndex = messages.value.length - 1
    messages.value[lastIndex].isLoading = false
    messages.value[lastIndex].content = content

    await typeText(lastIndex, content, sources)
  } catch (e) {
    const lastIndex = messages.value.length - 1
    messages.value[lastIndex].isLoading = false
    messages.value[lastIndex].displayedContent = '抱歉，AI 服务暂时不可用，请稍后再试。'
  } finally {
    sending.value = false
    await scrollToBottom()
  }
}

const typeText = async (msgIndex: number, fullText: string, sources: Source[]) => {
  const chars = fullText.split('')
  const typeSpeed = 15
  let currentContent = ''

  for (let i = 0; i < chars.length; i++) {
    currentContent += chars[i]
    messages.value[msgIndex].displayedContent = currentContent
    messages.value[msgIndex].isTyping = true

    if (i % 10 === 0) {
      await scrollToBottom()
      await new Promise(resolve => setTimeout(resolve, typeSpeed))
    }
  }

  messages.value[msgIndex].isTyping = false
  messages.value[msgIndex].sources = sources
}

const formatContent = (text: string): string => {
  if (!text) return ''

  let html = text
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.+?)\*/g, '<em>$1</em>')
    .replace(/^### (.+)$/gm, '<view class="h3">$1</view>')
    .replace(/^## (.+)$/gm, '<view class="h2">$1</view>')
    .replace(/^# (.+)$/gm, '<view class="h1">$1</view>')
    .replace(/^- (.+)$/gm, '<view class="list-item">$1</view>')
    .replace(/^(\d+)\. (.+)$/gm, '<view class="list-item-num">$1. $2</view>')
    .replace(/>(.+)$/gm, '<view class="quote">$1</view>')
    .replace(/\n{3,}/g, '\n\n')
    .replace(/\n\n/g, '</p><p>')
    .replace(/\n/g, '<br/>')

  html = '<p>' + html + '</p>'
  html = html.replace(/<p><br\/>/g, '<p>')
  html = html.replace(/<br\/><\/p>/g, '</p>')
  html = html.replace(/<p>(<view class="h[123]">)/g, '$1')
  html = html.replace(/(<\/view>)<\/p>/g, '$1')
  html = html.replace(/<p>(<strong>)/g, '$1')
  html = html.replace(/(<\/strong>)<\/p>/g, '$1')
  html = html.replace(/<p>(<em>)/g, '$1')
  html = html.replace(/(<\/em>)<\/p>/g, '$1')

  return html
}

const scrollToBottom = async () => {
  await nextTick()
  scrollIntoView.value = 'msg-' + (messages.value.length - 1)
}

const callAI = async (question: string): Promise<{ content: string, sources: Source[] }> => {
  try {
    const baseUrl = uni.getStorageSync('baseUrl') || 'http://192.168.2.5:8080'

    const [caseRes, lawRes] = await Promise.all([
      uni.request({
        url: baseUrl + '/api/v1/legal/cases/search?keyword=' + encodeURIComponent(question),
      }),
      uni.request({
        url: baseUrl + '/api/v1/legal/laws/search?keyword=' + encodeURIComponent(question),
      })
    ])

    const lawSources: { name: string; content?: string }[] = []
    const caseSources: { title: string; content?: string }[] = []
    const sources: Source[] = []

    if (lawRes.data.code === 0 && lawRes.data.data.laws) {
      lawRes.data.data.laws.slice(0, 3).forEach((l: any) => {
        lawSources.push({
          name: l.name,
          content: l.content || l.description
        })
        sources.push({
          title: l.name,
          type: 'law',
          id: l.id
        })
      })
    }

    if (caseRes.data.code === 0 && caseRes.data.data.cases) {
      caseRes.data.data.cases.slice(0, 3).forEach((c: any) => {
        caseSources.push({
          title: c.title || c.caseNumber,
          content: c.brief || c.description
        })
        sources.push({
          title: c.title || c.caseNumber,
          type: 'case',
          id: c.id
        })
      })
    }

    const aiRes = await uni.request({
      url: baseUrl + '/api/v1/ai/chat',
      method: 'POST',
      data: {
        message: question,
        lawSources,
        caseSources
      },
      header: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + uni.getStorageSync('token')
      }
    })

    if (aiRes.statusCode === 401) {
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
      uni.navigateTo({ url: '/pages/auth/login' })
      return { content: '请先登录', sources: [] }
    }

    let content = '抱歉，AI 服务暂时不可用，请稍后再试。'

    if (aiRes.data.code === 0) {
      content = aiRes.data.data.content
    }

    return { content, sources }
  } catch (e) {
    console.error('AI调用失败', e)
    return {
      content: '抱歉，AI 服务暂时不可用，请稍后再试。',
      sources: []
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/style/variables.scss';

.ai-chat {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: linear-gradient(180deg, #f0f9ff 0%, #ffffff 100%);
}

.chat-scroll {
  flex: 1;
  height: 0;
}

.messages {
  padding: 32rpx;
  padding-bottom: 160rpx;
}

.message {
  display: flex;
  margin-bottom: 40rpx;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.user {
  flex-direction: row-reverse;
}

.avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: $radius-round;
  background: $primary-gradient;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  font-weight: 600;
  flex-shrink: 0;
  box-shadow: 0 4rpx 12rpx rgba(24, 144, 255, 0.3);
}

.message.assistant .avatar {
  background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
  box-shadow: 0 4rpx 12rpx rgba(82, 196, 26, 0.3);
}

.content {
  max-width: 70%;
  margin: 0 24rpx;
}

.text {
  padding: 24rpx 28rpx;
  background: $background-white;
  border-radius: $radius-lg $radius-lg $radius-lg 4rpx;
  font-size: 28rpx;
  line-height: 1.8;
  color: $text-primary;
  box-shadow: $shadow-sm;
  white-space: normal;
  word-break: break-all;

  :deep(p) {
    margin: 12rpx 0;
    line-height: 1.8;
  }

  :deep(.h1) {
    font-size: 36rpx;
    font-weight: 700;
    color: #1a1a1a;
    margin: 20rpx 0 16rpx;
    padding-bottom: 8rpx;
    border-bottom: 2rpx solid #e5e7eb;
  }

  :deep(.h2) {
    font-size: 32rpx;
    font-weight: 600;
    color: #333;
    margin: 16rpx 0 12rpx;
  }

  :deep(.h3) {
    font-size: 30rpx;
    font-weight: 600;
    color: #444;
    margin: 12rpx 0 8rpx;
  }

  :deep(strong) {
    font-weight: 700;
    color: #1890ff;
  }

  :deep(em) {
    font-style: italic;
    color: $text-secondary;
  }

  :deep(.list-item) {
    padding: 6rpx 0 6rpx 24rpx;
    margin: 4rpx 0;
  }

  :deep(.list-item-num) {
    padding: 6rpx 0 6rpx 24rpx;
    margin: 4rpx 0;
  }

  :deep(.quote) {
    padding: 12rpx 20rpx;
    margin: 12rpx 0;
    background: #f8f9fa;
    border-left: 4rpx solid $primary-color;
    border-radius: 0 8rpx 8rpx 0;
    color: $text-secondary;
  }
}

.message.user .text {
  background: $primary-gradient;
  color: #fff;
  border-radius: $radius-lg $radius-lg 4rpx $radius-lg;
  box-shadow: 0 4rpx 16rpx rgba(24, 144, 255, 0.25);
}

.loading {
  padding: 28rpx 32rpx;
  background: $background-white;
  border-radius: $radius-lg;
  font-size: 28rpx;
  color: $text-secondary;
  box-shadow: $shadow-sm;
  display: flex;
  align-items: center;
  gap: 12rpx;

  &::before {
    content: '';
    width: 24rpx;
    height: 24rpx;
    border: 3rpx solid $border-color;
    border-top-color: $primary-color;
    border-radius: $radius-round;
    animation: spin 1s linear infinite;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.sources {
  margin-top: 24rpx;
  padding: 20rpx 24rpx;
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f4ff 100%);
  border-radius: $radius-md;
  border: 1rpx solid #91caff;
}

.sources-title {
  font-size: 24rpx;
  color: #1890ff;
  font-weight: 600;
  margin-bottom: 12rpx;
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.sources-note {
  font-size: 20rpx;
  color: #8c8c8c;
  margin-bottom: 12rpx;
  font-style: italic;
}

.source-item {
  display: flex;
  align-items: flex-start;
  gap: 8rpx;
  padding: 10rpx 0;
  font-size: 24rpx;
  color: $text-regular;
  border-bottom: 1rpx solid #e5e7eb;

  &:last-child {
    border-bottom: none;
  }
}

.source-icon {
  font-size: 24rpx;
  flex-shrink: 0;
  margin-top: 2rpx;
}

.source-text {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.source-type {
  font-size: 20rpx;
  color: #8c8c8c;
  flex-shrink: 0;
}

.input-area {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 20rpx;
  padding: 24rpx 32rpx;
  background: $background-white;
  box-shadow: 0 -8rpx 24rpx rgba(0, 0, 0, 0.06);
  border-radius: 48rpx 48rpx 0 0;
  backdrop-filter: blur(10rpx);
}

.input {
  flex: 1;
  height: 88rpx;
  padding: 0 32rpx;
  background: $background-light;
  border-radius: $radius-round;
  font-size: 30rpx;
  color: $text-primary;
  border: 2rpx solid transparent;
  transition: all $transition-fast;

  &:focus {
    background: $background-white;
    border-color: $primary-color;
  }
}

.send-btn {
  width: 160rpx;
  height: 88rpx;
  line-height: 88rpx;
  background: $primary-gradient;
  color: #fff;
  border-radius: $radius-round;
  font-size: 30rpx;
  font-weight: 600;
  box-shadow: 0 8rpx 20rpx rgba(24, 144, 255, 0.3);
  transition: all $transition-fast;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;

  &:active:not([disabled]) {
    transform: scale(0.95);
  }

  &[disabled] {
    background: linear-gradient(135deg, #d1d5db 0%, #9ca3af 100%);
    box-shadow: none;
  }
}

.quick-questions {
  position: fixed;
  bottom: 160rpx;
  left: 32rpx;
  right: 32rpx;
  background: rgba(255, 255, 255, 0.95);
  border-radius: $radius-lg;
  padding: 32rpx;
  box-shadow: $shadow-lg;
  backdrop-filter: blur(10rpx);
}

.quick-title {
  font-size: 26rpx;
  color: $text-secondary;
  margin-bottom: 20rpx;
  font-weight: 500;
}

.quick-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.quick-item {
  padding: 16rpx 28rpx;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-radius: $radius-round;
  font-size: 26rpx;
  color: $primary-dark;
  font-weight: 500;
  box-shadow: 0 2rpx 8rpx rgba(24, 144, 255, 0.15);
  transition: all $transition-fast;

  &:active {
    transform: scale(0.95);
    box-shadow: 0 4rpx 12rpx rgba(24, 144, 255, 0.25);
  }
}

.typing-cursor {
  display: inline-block;
  width: 4rpx;
  height: 32rpx;
  background: $primary-color;
  margin-left: 4rpx;
  animation: blink 0.8s infinite;
  vertical-align: middle;
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

.loading-dots {
  display: flex;
  gap: 6rpx;

  .dot {
    font-size: 20rpx;
    animation: bounce 1.4s infinite ease-in-out both;

    &:nth-child(1) { animation-delay: -0.32s; }
    &:nth-child(2) { animation-delay: -0.16s; }
  }
}

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.8); opacity: 0.5; }
  40% { transform: scale(1.2); opacity: 1; }
}

.loading-text {
  margin-left: 12rpx;
  color: $text-secondary;
  font-size: 26rpx;
}

.btn-loading {
  display: flex;
  gap: 4rpx;
  animation: pulse 1s infinite;

  &:nth-child(1) { animation-delay: 0s; }
  &:nth-child(2) { animation-delay: 0.2s; }
}

@keyframes pulse {
  0%, 100% { opacity: 0.4; }
  50% { opacity: 1; }
}

:deep(.formatted-content) {
  font-size: 30rpx;
  line-height: 1.6;
  color: $text-primary;

  .h1 {
    font-size: 40rpx;
    font-weight: 700;
    color: #1a1a1a;
    margin: 32rpx 0 20rpx;
    padding-left: 16rpx;
    border-left: 6rpx solid $primary-color;
  }

  .h2 {
    font-size: 34rpx;
    font-weight: 600;
    color: #333;
    margin: 28rpx 0 16rpx;
    padding-left: 12rpx;
    border-left: 4rpx solid $primary-color;
  }

  .h3 {
    font-size: 30rpx;
    font-weight: 600;
    color: #444;
    margin: 24rpx 0 12rpx;
  }

  .bold {
    font-weight: 700;
    color: #1890ff;
  }

  .italic {
    font-style: italic;
    color: $text-secondary;
  }

  .paragraph {
    margin: 16rpx 0;
  }

  .list-item {
    padding: 8rpx 0 8rpx 32rpx;
    position: relative;

    &::before {
      content: '';
      position: absolute;
      left: 12rpx;
      top: 50%;
      transform: translateY(-50%);
      width: 8rpx;
      height: 8rpx;
      background: $primary-color;
      border-radius: 50%;
    }
  }

  .table-row {
    display: flex;
    padding: 12rpx 0;
    border-bottom: 1rpx solid $border-color;

    .table-cell {
      flex: 1;
      font-size: 26rpx;
      color: $text-secondary;
      text-align: center;
    }
  }
}
</style>