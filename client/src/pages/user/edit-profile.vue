<template>
  <view class="edit-profile">
    <view class="form-item">
      <view class="label">用户名</view>
      <input
        v-model="form.username"
        class="input"
        placeholder="请输入用户名"
        disabled
      />
    </view>

    <view class="form-item">
      <view class="label">邮箱</view>
      <input
        v-model="form.email"
        class="input"
        type="text"
        placeholder="请输入邮箱"
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
      <view class="label">律所/机构</view>
      <input
        v-model="form.firmName"
        class="input"
        placeholder="请输入律所或机构名称"
      />
    </view>

    <view class="form-item">
      <view class="label">执业领域</view>
      <view class="tags-input">
        <view
          v-for="(tag, index) in form.practiceAreas"
          :key="index"
          class="tag"
        >
          {{ tag }}
          <text class="tag-close" @click="removeArea(index)">x</text>
        </view>
        <picker mode="selector" :range="areas" @change="addArea">
          <view class="add-area">+ 添加领域</view>
        </picker>
      </view>
    </view>

    <view class="form-item">
      <view class="label">个人简介</view>
      <textarea
        v-model="form.bio"
        class="textarea"
        placeholder="请输入个人简介"
        :maxlength="500"
      />
      <view class="char-count">{{ form.bio?.length || 0 }}/500</view>
    </view>

    <view class="form-actions">
      <button class="btn save-btn" @click="handleSave" :disabled="saving">
        {{ saving ? '保存中...' : '保存' }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { userApi, type UserProfile } from '@/services/user'

const saving = ref(false)

const form = reactive({
  username: '',
  email: '',
  phone: '',
  firmName: '',
  practiceAreas: [] as string[],
  bio: ''
})

const areas = [
  '民商事诉讼',
  '刑事辩护',
  '行政诉讼',
  '公司法务',
  '知识产权',
  '劳动纠纷',
  '婚姻家庭',
  '交通事故',
  '房产纠纷',
  '建设工程',
  '金融证券',
  '国际贸易'
]

onMounted(async () => {
  await loadProfile()
})

const loadProfile = async () => {
  try {
    const res = await userApi.getProfile()
    const profile = res.data
    form.username = profile.username
    form.email = profile.email || ''
    form.phone = profile.phone || ''
    form.firmName = profile.firmName || ''
    form.practiceAreas = profile.practiceAreas || []
    form.bio = profile.bio || ''
  } catch (e) {
    console.error('加载用户信息失败', e)
  }
}

const addArea = (e: any) => {
  const area = areas[e.detail.value]
  if (area && !form.practiceAreas.includes(area)) {
    form.practiceAreas.push(area)
  }
}

const removeArea = (index: number) => {
  form.practiceAreas.splice(index, 1)
}

const handleSave = async () => {
  saving.value = true
  try {
    await userApi.updateProfile({
      email: form.email,
      phone: form.phone,
      firmName: form.firmName,
      practiceAreas: form.practiceAreas,
      bio: form.bio
    })
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
</script>

<style lang="scss" scoped>
.edit-profile {
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

.input[disabled] {
  color: #999;
}

.tags-input {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  padding: 16rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
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

.add-area {
  padding: 8rpx 20rpx;
  background: #fff;
  border: 1px dashed #d9d9d9;
  border-radius: 4rpx;
  font-size: 24rpx;
  color: #666;
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

.char-count {
  text-align: right;
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
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