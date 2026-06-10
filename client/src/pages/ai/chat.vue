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
            <view class="text" v-if="msg.content">{{ msg.content }}</view>
            <view class="loading" v-else>思考中...</view>
            <view class="sources" v-if="msg.sources && msg.sources.length > 0">
              <view class="sources-title">📚 参考资料：</view>
              <view
                v-for="(source, idx) in msg.sources"
                :key="idx"
                class="source-item"
              >
                <text class="source-icon">{{ source.type === 'case' ? '📋' : '📚' }}</text>
                <text class="source-text">{{ source.title }}</text>
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
        {{ sending ? '...' : '发送' }}
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
  sources?: Source[]
}

const messages = ref<Message[]>([])
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
  // 获取页面参数
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const query = currentPage.options?.query || ''
  
  // 如果有传入query参数，自动发送
  if (query) {
    inputText.value = decodeURIComponent(query)
    setTimeout(() => {
      sendMessage()
    }, 300)
  }
  
  const context = currentPage.options?.context || ''
  const id = currentPage.options?.id || ''

  if (context === 'case') {
    messages.value.push({
      role: 'assistant',
      content: '您好，我已加载该案例信息。请问您想了解案例的哪些方面？比如案件分析、判决预测、法律建议等。'
    })
  } else if (context === 'company') {
    messages.value.push({
      role: 'assistant',
      content: '您好，我已加载该企业信息。请问您想了解企业的哪些方面？比如企业风险分析、合作建议、法律尽职调查等。'
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
    content: question
  })

  await scrollToBottom()

  try {
    const { content, sources } = await callAI(question)
    messages.value.push({
      role: 'assistant',
      content: content,
      sources: sources
    })
  } catch (e) {
    messages.value.push({
      role: 'assistant',
      content: '抱歉，AI 服务暂时不可用，请稍后再试。'
    })
  } finally {
    sending.value = false
    await scrollToBottom()
  }
}

const callAI = async (question: string): Promise<{ content: string, sources: Source[] }> => {
  try {
    // 并行调用AI和法律数据API
    const [aiRes, caseRes, lawRes] = await Promise.all([
      uni.request({
        url: 'http://localhost:8080/api/v1/ai/chat',
        method: 'POST',
        data: { message: question },
        header: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + uni.getStorageSync('token')
        }
      }),
      uni.request({
        url: 'http://localhost:8080/api/v1/legal/cases/search?keyword=' + encodeURIComponent(question),
      }),
      uni.request({
        url: 'http://localhost:8080/api/v1/legal/laws/search?keyword=' + encodeURIComponent(question),
      })
    ])

    let content = '抱歉，AI 服务暂时不可用，请稍后再试。'
    const sources: Source[] = []

    // 处理AI响应
    if (aiRes.data.code === 0) {
      content = aiRes.data.data.content
    }

    // 处理案例数据
    if (caseRes.data.code === 0 && caseRes.data.data.cases) {
      const cases = caseRes.data.data.cases.slice(0, 3)
      cases.forEach((c: any) => {
        sources.push({
          title: c.title || c.caseNumber,
          type: 'case',
          id: c.id
        })
      })
    }

    // 处理法规数据
    if (lawRes.data.code === 0 && lawRes.data.data.laws) {
      const laws = lawRes.data.data.laws.slice(0, 3)
      laws.forEach((l: any) => {
        sources.push({
          title: l.name,
          type: 'law',
          id: l.id
        })
      })
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

const scrollToBottom = async () => {
  await nextTick()
  scrollIntoView.value = 'msg-' + (messages.value.length - 1)
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
  padding: 28rpx 32rpx;
  background: $background-white;
  border-radius: $radius-lg $radius-lg $radius-lg 4rpx;
  font-size: 30rpx;
  line-height: 1.8;
  color: $text-primary;
  box-shadow: $shadow-md;
  white-space: pre-wrap;
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
  margin-top: 20rpx;
  padding: 20rpx 24rpx;
  background: #f0f9ff;
  border-radius: $radius-md;
  border-left: 4rpx solid #1890ff;
}

.sources-title {
  font-size: 24rpx;
  color: #1890ff;
  font-weight: 600;
  margin-bottom: 12rpx;
}

.source-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 0;
  font-size: 24rpx;
  color: $text-secondary;
}

.source-icon {
  font-size: 24rpx;
}

.source-text {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
</style>