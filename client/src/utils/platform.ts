/**
 * 平台类型枚举
 */
export enum PlatformType {
  H5 = 'H5',
  WECHAT_MP = 'MP-WEIXIN',
  APP = 'APP',
  ALIPAY_MP = 'MP-ALIPAY',
  UNKNOWN = 'UNKNOWN'
}

/**
 * 获取当前运行平台
 */
export function getPlatform(): PlatformType {
  // #ifdef H5
  return PlatformType.H5
  // #endif
  
  // #ifdef MP-WEIXIN
  return PlatformType.WECHAT_MP
  // #endif
  
  // #ifdef APP-PLUS
  return PlatformType.APP
  // #endif
  
  // #ifdef MP-ALIPAY
  return PlatformType.ALIPAY_MP
  // #endif
  
  return PlatformType.UNKNOWN
}

/**
 * 是否在微信小程序环境
 */
export function isWeChatMp(): boolean {
  // #ifdef MP-WEIXIN
  return true
  // #endif
  
  return false
}

/**
 * 是否在 H5 环境
 */
export function isH5(): boolean {
  // #ifdef H5
  return true
  // #endif
  
  return false
}

/**
 * 是否在 App 环境
 */
export function isApp(): boolean {
  // #ifdef APP-PLUS
  return true
  // #endif
  
  return false
}

/**
 * 获取平台特定的配置
 */
export function getPlatformConfig() {
  return {
    platform: getPlatform(),
    isWeChatMp: isWeChatMp(),
    isH5: isH5(),
    isApp: isApp()
  }
}
