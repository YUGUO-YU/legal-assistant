<template>
  <view class="lead-create">
    <view class="form-item">
      <view class="label">案源标题 <text class="required">*</text></view>
      <input
        v-model="form.title"
        class="input"
        placeholder="请输入案源标题"
      />
    </view>

    <view class="form-item">
      <view class="label">案源描述</view>
      <textarea
        v-model="form.description"
        class="textarea"
        placeholder="请详细描述案源情况"
        :maxlength="-1"
      />
    </view>

    <view class="form-item">
      <view class="label">案源来源</view>
      <picker mode="selector" :range="sources" @change="onSourceChange">
        <view class="picker-value">
          {{ form.source || '请选择案源来源' }}
        </view>
      </picker>
    </view>

    <view class="form-item">
      <view class="label">标签</view>
      <view class="tags-input">
        <view class="tag" v-for="(tag, index) in form.tags" :key="index">
          {{ tag }}
          <text class="tag-close" @click="removeTag(index)">x</text>
        </view>
        <input
          v-model="tagInput"
          class="tag-input"
          placeholder="添加标签"
          @confirm="addTag"
        />
      </view>
      <view class="tag-hints">
        <text
          v-for="hint in tagHints"
          :key="hint"
          class="tag-hint"
          @click="addTagFromHint(hint)"
        >{{ hint }}</text>
      </view>
    </view>

    <view class="form-item">
      <view class="label">预计金额</view>
      <input
        v-model="form.expectedAmount"
        class="input"
        type="number"
        placeholder="请输入预计金额（元）"
      />
    </view>

    <view class="form-item">
      <view class="label">联系方式</view>
      <input
        v-model="form.contact"
        class="input"
        placeholder="请输入联系方式"
      />
    </view>

    <view class="form-actions">
      <button class="btn save-btn" @click="handleCreate" :disabled="creating">
        {{ creating ? '创建中...' : '创建案源' }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { leadApi } from '@/services/lead'

const creating = ref(false)
const tagInput = ref('')

const form = reactive({
  title: '',
  description: '',
  source: '',
  tags: [] as string[],
  expectedAmount: '',
  contact: ''
})

const sources = ['客户推荐', '线上推广', '线下活动', '同行介绍', '自主开发', '其他']
const tagHints = ['婚姻家庭', '劳动纠纷', '合同纠纷', '交通事故', '刑事辩护', '知识产权']

const onSourceChange = (e: any) => {
  form.source = sources[e.detail.value]
}

const addTag = () => {
  if (tagInput.value && !form.tags.includes(tagInput.value)) {
    form.tags.push(tagInput.value)
    tagInput.value = ''
  }
}

const addTagFromHint = (tag: string) => {
  if (!form.tags.includes(tag)) {
    form.tags.push(tag)
  }
}

const removeTag = (index: number) => {
  form.tags.splice(index, 1)
}

const handleCreate = async () => {
  if (!form.title) {
    uni.showToast({ title: '请输入案源标题', icon: 'none' })
    return
  }

  creating.value = true

  try {
    const res = await leadApi.create({
      title: form.title,
      description: form.description,
      source: form.source,
      tags: form.tags
    })

    uni.showToast({ title: '创建成功' })
    setTimeout(() => {
      uni.navigateTo({
        url: `/pages/lead/detail?id=${res.id}`
      })
    }, 1500)
  } catch (e) {
    console.error('创建案源失败', e)
  } finally {
    creating.value = false
  }
}
</script>

<style lang="scss" scoped>
.lead-create {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
}

.form-item {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.label {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 16rpx;
  font-weight: 500;
}

.required {
  color: #ff4d4f;
}

.input {
  height: 72rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
}

.textarea {
  width: 100%;
  min-height: 200rpx;
  padding: 20rpx;
  font-size: 28rpx;
  line-height: 1.6;
  background: #f5f5f5;
  border-radius: 8rpx;
  box-sizing: border-box;
}

.picker-value {
  height: 72rpx;
  padding: 0 20rpx;
  line-height: 72rpx;
  font-size: 28rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
  color: #666;
}

.tags-input {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  padding: 16rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
  min-height: 72rpx;
}

.tag {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 16rpx;
  background: #e6f7ff;
  border-radius: 4rpx;
  font-size: 24rpx;
  color: #1890ff;
}

.tag-close {
  font-size: 20rpx;
}

.tag-input {
  flex: 1;
  min-width: 120rpx;
  height: 48rpx;
  font-size: 26rpx;
}

.tag-hints {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 16rpx;
}

.tag-hint {
  padding: 8rpx 20rpx;
  background: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 4rpx;
  font-size: 24rpx;
  color: #666;
}

.form-actions {
  margin-top: 40rpx;
}

.btn {
  height: 88rpx;
  line-height: 88rpx;
  background: #1890ff;
  color: #fff;
  border-radius: 44rpx;
  font-size: 30rpx;
}

.btn[disabled] {
  opacity: 0.6;
}
</style>