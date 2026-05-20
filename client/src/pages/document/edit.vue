<template>
  <view class="document-edit">
    <view class="form-item">
      <view class="label">标题</view>
      <input
        v-model="form.title"
        class="input"
        placeholder="请输入文书标题"
      />
    </view>

    <view class="form-item">
      <view class="label">类型</view>
      <picker mode="selector" :range="docTypes" @change="onTypeChange">
        <view class="picker-value">
          {{ form.docType || '请选择文书类型' }}
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
    </view>

    <view class="form-item content-item">
      <view class="label">正文</view>
      <textarea
        v-model="form.content"
        class="textarea"
        placeholder="请输入文书内容"
        :maxlength="-1"
      />
      <view class="word-count">{{ form.content?.length || 0 }}字</view>
    </view>

    <view class="form-actions">
      <button class="btn save-btn" @click="handleSave" :disabled="saving">
        {{ saving ? '保存中...' : '保存' }}
      </button>
      <button class="btn draft-btn" @click="handleSaveDraft" :disabled="saving">
        保存草稿
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { documentApi } from '@/services/document'

const docId = ref('')
const isEdit = ref(false)
const saving = ref(false)
const tagInput = ref('')

const form = reactive({
  title: '',
  content: '',
  docType: '',
  tags: [] as string[]
})

const docTypes = ['合同', '协议', '函件', '法律意见书', '申请书', '答辩状', '其他']

onMounted(async () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  docId.value = currentPage.options?.id || ''

  if (docId.value) {
    isEdit.value = true
    await loadDoc()
  }
})

const loadDoc = async () => {
  try {
    const res = await documentApi.getById(docId.value)
    const doc = res.data
    form.title = doc.title
    form.content = doc.content
    form.docType = doc.docType
    form.tags = doc.tags || []
  } catch (e) {
    console.error('加载文书失败', e)
  }
}

const onTypeChange = (e: any) => {
  form.docType = docTypes[e.detail.value]
}

const addTag = () => {
  if (tagInput.value && !form.tags.includes(tagInput.value)) {
    form.tags.push(tagInput.value)
    tagInput.value = ''
  }
}

const removeTag = (index: number) => {
  form.tags.splice(index, 1)
}

const handleSave = async () => {
  if (!form.title) {
    uni.showToast({ title: '请输入标题', icon: 'none' })
    return
  }

  saving.value = true
  try {
    if (isEdit.value) {
      await documentApi.update(docId.value, {
        title: form.title,
        content: form.content,
        docType: form.docType,
        tags: form.tags,
        status: 'review'
      })
    } else {
      const res = await documentApi.create({
        title: form.title,
        content: form.content,
        docType: form.docType,
        tags: form.tags
      })
      docId.value = res.data.id
      isEdit.value = true
    }
    uni.showToast({ title: '保存成功' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1000)
  } catch (e) {
    console.error('保存失败', e)
  } finally {
    saving.value = false
  }
}

const handleSaveDraft = async () => {
  if (!form.title) {
    uni.showToast({ title: '请输入标题', icon: 'none' })
    return
  }

  saving.value = true
  try {
    if (isEdit.value) {
      await documentApi.update(docId.value, {
        title: form.title,
        content: form.content,
        docType: form.docType,
        tags: form.tags,
        status: 'draft'
      })
    } else {
      const res = await documentApi.create({
        title: form.title,
        content: form.content,
        docType: form.docType,
        tags: form.tags
      })
      docId.value = res.data.id
      isEdit.value = true
    }
    uni.showToast({ title: '已保存草稿' })
  } catch (e) {
    console.error('保存失败', e)
  } finally {
    saving.value = false
  }
}
</script>

<style lang="scss" scoped>
.document-edit {
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

.input {
  height: 72rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
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
  cursor: pointer;
}

.tag-input {
  flex: 1;
  min-width: 120rpx;
  height: 48rpx;
  font-size: 26rpx;
}

.content-item {
  position: relative;
}

.textarea {
  width: 100%;
  min-height: 400rpx;
  padding: 20rpx;
  font-size: 28rpx;
  line-height: 1.8;
  background: #f5f5f5;
  border-radius: 8rpx;
  box-sizing: border-box;
}

.word-count {
  position: absolute;
  right: 40rpx;
  bottom: 40rpx;
  font-size: 24rpx;
  color: #999;
}

.form-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 40rpx;
}

.btn {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  font-size: 30rpx;
}

.save-btn {
  background: #1890ff;
  color: #fff;
}

.draft-btn {
  background: #fff;
  border: 1px solid #d9d9d9;
  color: #666;
}

.btn[disabled] {
  opacity: 0.6;
}
</style>