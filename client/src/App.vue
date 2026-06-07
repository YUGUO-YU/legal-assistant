<script setup lang="ts">
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
import { useAuthStore } from '@/stores/auth'

onLaunch(() => {
  console.log('App Launch')
  
  // 初始化用户登录状态
  const authStore = useAuthStore()
  authStore.initFromStorage()
  
  // #ifdef H5
  console.log('Running on H5 (Web)')
  // #endif
  
  // #ifdef MP-WEIXIN
  console.log('Running on WeChat Mini Program')
  // #endif
})

onShow(() => {
  console.log('App Show')
})

onHide(() => {
  console.log('App Hide')
})
</script>

<style lang="scss">
/* 全局样式 - 适用于所有平台 */
page {
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 28rpx;
  line-height: 1.5;
  color: #333333;
}

/* H5 特定样式 */
/* #ifdef H5 */
@media (min-width: 768px) {
  page {
    background-color: #f0f2f5;
  }
}
/* #endif */

/* 微信小程序特定样式 */
/* #ifdef MP-WEIXIN */
page {
  background-color: #f8f8f8;
}
/* #endif */

/* 重置默认样式 */
view, text, image, scroll-view, swiper, button, input {
  box-sizing: border-box;
}

button {
  &::after {
    border: none;
  }
}
</style>
