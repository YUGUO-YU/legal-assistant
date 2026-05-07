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

interface Message {
  role: 'user' | 'assistant'
  content: string
}

const messages = ref<Message[]>([])
const inputText = ref('')
const sending = ref(false)
const scrollTop = ref(0)
const scrollIntoView = ref('')

const quickQuestions = [
  '帮我起草一份房屋租赁合同',
  '合同违约怎么处理？',
  '如何查询企业工商信息？',
  '民间借贷利息怎么计算？'
]

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
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
    const response = await callAI(question)
    messages.value.push({
      role: 'assistant',
      content: response
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

const callAI = async (question: string): Promise<string> => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(`您的问题是："${question}"\n\n根据我的分析，这是一个涉及民事法律关系的问题。建议您：\n1. 收集相关证据材料\n2. 明确诉讼请求\n3. 必要时咨询专业律师\n\n您还想了解更多信息吗？`)
    }, 1500)
  })
}

const scrollToBottom = async () => {
  await nextTick()
  scrollIntoView.value = 'msg-' + (messages.value.length - 1)
}
</script>

<style lang="scss" scoped>
.ai-chat {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f5f5;
}

.chat-scroll {
  flex: 1;
  height: 0;
}

.messages {
  padding: 20rpx;
  padding-bottom: 120rpx;
}

.message {
  display: flex;
  margin-bottom: 32rpx;
}

.message.user {
  flex-direction: row-reverse;
}

.avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 36rpx;
  background: #1890ff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  flex-shrink: 0;
}

.message.assistant .avatar {
  background: #52c41a;
}

.content {
  max-width: 70%;
  margin: 0 20rpx;
}

.text {
  padding: 20rpx 24rpx;
  background: #fff;
  border-radius: 12rpx;
  font-size: 28rpx;
  line-height: 1.6;
  white-space: pre-wrap;
}

.message.user .text {
  background: #1890ff;
  color: #fff;
}

.loading {
  padding: 20rpx 24rpx;
  background: #fff;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #999;
}

.input-area {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 20rpx;
  padding: 20rpx;
  background: #fff;
  box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.05);
}

.input {
  flex: 1;
  height: 80rpx;
  padding: 0 24rpx;
  background: #f5f5f5;
  border-radius: 40rpx;
  font-size: 28rpx;
}

.send-btn {
  width: 140rpx;
  height: 80rpx;
  line-height: 80rpx;
  background: #1890ff;
  color: #fff;
  border-radius: 40rpx;
  font-size: 28rpx;
}

.send-btn[disabled] {
  background: #ccc;
}

.quick-questions {
  position: fixed;
  bottom: 120rpx;
  left: 20rpx;
  right: 20rpx;
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
}

.quick-title {
  font-size: 26rpx;
  color: #999;
  margin-bottom: 16rpx;
}

.quick-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.quick-item {
  padding: 12rpx 24rpx;
  background: #e6f7ff;
  border-radius: 8rpx;
  font-size: 26rpx;
  color: #1890ff;
}
</style>