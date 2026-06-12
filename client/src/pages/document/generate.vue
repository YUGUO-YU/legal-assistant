<template>
  <view class="document-generate">
    <view class="step-bar">
      <view
        v-for="(step, index) in steps"
        :key="index"
        class="step"
        :class="{ active: currentStep >= index, current: currentStep === index }"
      >
        <view class="step-num">{{ index + 1 }}</view>
        <view class="step-text">{{ step }}</view>
      </view>
    </view>

    <view class="step-content">
      <view v-show="currentStep === 0" class="step-panel">
        <view class="section-title">选择文书类型</view>
        <view class="category-grid">
          <view
            v-for="cat in categories"
            :key="cat.value"
            class="category-card"
            :class="{ selected: selectedCategory === cat.value }"
            @click="selectCategory(cat.value)"
          >
            <view class="cat-icon">{{ cat.icon }}</view>
            <view class="cat-name">{{ cat.label }}</view>
          </view>
        </view>
      </view>

      <view v-show="currentStep === 1" class="step-panel">
        <view class="section-title">选择模板</view>
        <view class="template-list" v-if="templates.length">
          <view
            v-for="tpl in templates"
            :key="tpl.id"
            class="template-card"
            :class="{ selected: selectedTemplate?.id === tpl.id }"
            @click="selectTemplate(tpl)"
          >
            <view class="tpl-name">{{ tpl.name }}</view>
            <view class="tpl-desc">{{ tpl.description }}</view>
            <view class="tpl-vars">变量: {{ tpl.variables?.join(', ') }}</view>
          </view>
        </view>
        <view v-else class="empty">该分类暂无模板</view>
      </view>

      <view v-show="currentStep === 2" class="step-panel">
        <view class="section-title">填写信息</view>
        <view class="section-subtitle" v-if="selectedTemplate">
          {{ selectedTemplate.name }}
        </view>
        <view class="form-list">
          <view
            v-for="variable in templateVariables"
            :key="variable.name"
            class="form-item"
          >
            <view class="form-label">{{ variable.label }}</view>
            <input
              v-if="variable.type === 'text'"
              v-model="formData[variable.name]"
              class="form-input"
              :placeholder="variable.placeholder"
            />
            <textarea
              v-else-if="variable.type === 'textarea'"
              v-model="formData[variable.name]"
              class="form-textarea"
              :placeholder="variable.placeholder"
            />
          </view>
        </view>
      </view>

      <view v-show="currentStep === 3" class="step-panel">
        <view class="section-title">生成结果</view>
        <view class="result-card" v-if="generated">
          <view class="result-icon">&#10003;</view>
          <view class="result-title">文书生成成功</view>
          <view class="result-tip">点击下方按钮下载生成的 Word 文档</view>
          <button class="download-btn" @click="downloadDocument">
            &#8595; 下载 Word 文档
          </button>
        </view>
        <view class="generating" v-else>
          <view class="spinner"></view>
          <view class="generating-text">正在生成文书...</view>
        </view>
      </view>
    </view>

    <view class="bottom-actions">
      <button
        class="btn prev-btn"
        v-if="currentStep > 0"
        @click="prevStep"
      >
        上一步
      </button>
      <button
        class="btn next-btn"
        v-if="currentStep < 3"
        :disabled="!canNext"
        @click="nextStep"
      >
        下一步
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { documentApi, type DocumentTemplate } from '@/services/document'

const steps = ['选择类型', '选择模板', '填写信息', '生成完成']
const currentStep = ref(0)

const categories = [
  { label: '律师函', value: 'lawyer_letter', icon: '&#128203;', desc: '函件类' },
  { label: '催告函', value: 'dunning_letter', icon: '&#9998;', desc: '函件类' },
  { label: '民间借贷起诉状', value: 'civil_loan_complaint', icon: '&#128196;', desc: '民商事' },
  { label: '买卖合同', value: 'sales_contract', icon: '&#128203;', desc: '合同类' },
  { label: '劳动合同', value: 'labor', icon: '&#128188;', desc: '合同类' }
]

const selectedCategory = ref('')
const templates = ref<DocumentTemplate[]>([])
const selectedTemplate = ref<DocumentTemplate | null>(null)
const formData = ref<Record<string, string>>({})
const generated = ref(false)
const downloadPath = ref('')

const templateVariables = computed(() => {
  if (!selectedTemplate.value?.variables) return []
  return selectedTemplate.value.variables.map((v: any) => {
    const name = typeof v === 'string' ? v : v.name
    const labels: Record<string, { label: string; type: string; placeholder: string }> = {
      plaintiff_name: { label: '原告姓名', type: 'text', placeholder: '请输入原告姓名' },
      plaintiff_id: { label: '原告身份证号', type: 'text', placeholder: '请输入身份证号' },
      plaintiff_address: { label: '原告住所地', type: 'text', placeholder: '请输入住所地' },
      defendant_name: { label: '被告姓名', type: 'text', placeholder: '请输入被告姓名' },
      defendant_id: { label: '被告身份证号', type: 'text', placeholder: '请输入身份证号' },
      defendant_address: { label: '被告住所地', type: 'text', placeholder: '请输入住所地' },
      court_name: { label: '管辖法院', type: 'text', placeholder: '如：北京市朝阳区人民法院' },
      principal_amount: { label: '本金金额', type: 'text', placeholder: '请输入本金金额' },
      interest_rate: { label: '利率', type: 'text', placeholder: '如：年利率4.35%' },
      loan_date: { label: '借款日期', type: 'text', placeholder: '如：2024年1月1日' },
      claim_amount: { label: '诉讼请求金额', type: 'text', placeholder: '请输入金额' },
      facts_desc: { label: '事实与理由', type: 'textarea', placeholder: '请详细描述事实与理由' },
      evidence_list: { label: '证据清单', type: 'textarea', placeholder: '请列出证据清单' },
      sign_date: { label: '签署日期', type: 'text', placeholder: '如：2024年6月7日' },
      recipient_name: { label: '收件人姓名', type: 'text', placeholder: '请输入收件人姓名' },
      letter_type: { label: '函件类型', type: 'text', placeholder: '如：催告/警告/通知' },
      facts_summary: { label: '事实摘要', type: 'textarea', placeholder: '请简要描述相关事实' },
      legal_basis: { label: '法律依据', type: 'textarea', placeholder: '请列出相关法律条文' },
      demands: { label: '诉求事项', type: 'textarea', placeholder: '请列出具体诉求' },
      deadline_days: { label: '履行期限（天）', type: 'text', placeholder: '如：15' },
      consequences: { label: '法律后果', type: 'textarea', placeholder: '请说明逾期不履行的后果' },
      creditor_name: { label: '债权人姓名', type: 'text', placeholder: '请输入债权人姓名' },
      debtor_name: { label: '债务人姓名', type: 'text', placeholder: '请输入债务人姓名' },
      debt_amount: { label: '债务金额', type: 'text', placeholder: '请输入债务金额' },
      debt_type: { label: '债务类型', type: 'text', placeholder: '如：借款/货款/服务费' },
      debt_start_date: { label: '债务起始日', type: 'text', placeholder: '如：2024年1月1日' },
      debt_due_date: { label: '到期日', type: 'text', placeholder: '如：2024年6月30日' },
      claim_content: { label: '催告内容', type: 'textarea', placeholder: '请详细说明催告内容' },
      lawyer_name: { label: '律师姓名', type: 'text', placeholder: '请输入律师姓名' },
      law_firm_name: { label: '律所名称', type: 'text', placeholder: '请输入律所名称' },
      seller_name: { label: '卖方名称', type: 'text', placeholder: '请输入卖方全称' },
      seller_uscc: { label: '卖方统一社会信用代码', type: 'text', placeholder: '请输入统一社会信用代码' },
      buyer_name: { label: '买方名称', type: 'text', placeholder: '请输入买方全称' },
      buyer_uscc: { label: '买方统一社会信用代码', type: 'text', placeholder: '请输入统一社会信用代码' },
      product_name: { label: '产品名称', type: 'text', placeholder: '请输入产品名称' },
      product_spec: { label: '规格型号', type: 'text', placeholder: '请输入规格型号' },
      product_quantity: { label: '数量', type: 'text', placeholder: '请输入数量' },
      product_price: { label: '单价', type: 'text', placeholder: '请输入单价' },
      total_amount: { label: '合同总金额', type: 'text', placeholder: '请输入总金额' },
      delivery_date: { label: '交付日期', type: 'text', placeholder: '如：2024年6月30日' },
      delivery_place: { label: '交付地点', type: 'text', placeholder: '请输入交付地点' },
      payment_terms: { label: '付款方式', type: 'textarea', placeholder: '如：合同签订后30日内支付50%，交货后支付50%' },
      quality_standard: { label: '质量标准', type: 'text', placeholder: '如：符合国家标准' },
      warranty_period: { label: '质保期', type: 'text', placeholder: '如：12个月' },
      employer_name: { label: '用人单位', type: 'text', placeholder: '请输入用人单位名称' },
      employee_name: { label: '劳动者姓名', type: 'text', placeholder: '请输入劳动者姓名' },
      id_card: { label: '身份证号', type: 'text', placeholder: '请输入身份证号' },
      position: { label: '工作岗位', type: 'text', placeholder: '如：软件工程师' },
      contract_type: { label: '合同类型', type: 'text', placeholder: '如：固定期限/无固定期限' },
      contract_start: { label: '合同起始日期', type: 'text', placeholder: '如：2024年1月1日' },
      contract_end: { label: '合同结束日期', type: 'text', placeholder: '如：2027年12月31日' },
      probation_period: { label: '试用期', type: 'text', placeholder: '如：3个月' },
      work_location: { label: '工作地点', type: 'text', placeholder: '请输入工作地点' },
      salary: { label: '月工资', type: 'text', placeholder: '如：15000元' },
      pay_day: { label: '发薪日', type: 'text', placeholder: '如：每月25日' },
      salary_structure: { label: '薪酬构成', type: 'text', placeholder: '如：基本工资+岗位工资+绩效' },
      working_hours: { label: '工时制度', type: 'text', placeholder: '如：标准工时制' },
      confidentiality_terms: { label: '保密与竞业限制条款', type: 'textarea', placeholder: '请输入相关条款' },
      termination_terms: { label: '合同解除条款', type: 'textarea', placeholder: '请输入解除条件' }
    }
    return {
      name: name,
      ...(labels[name] || { label: name, type: 'text', placeholder: `请输入${name}` })
    }
  })
})

const canNext = computed(() => {
  switch (currentStep.value) {
    case 0:
      return !!selectedCategory.value
    case 1:
      return !!selectedTemplate.value
    case 2:
      return templateVariables.value.every(v => formData.value[v.name])
    default:
      return true
  }
})

const selectCategory = async (value: string) => {
  selectedCategory.value = value
  try {
    const res = await documentApi.getTemplatesByCategory(value)
    templates.value = res.data || []
  } catch (e) {
    templates.value = []
  }
}

const selectTemplate = (tpl: DocumentTemplate) => {
  selectedTemplate.value = tpl
  formData.value = {}
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const nextStep = async () => {
  if (currentStep.value === 2) {
    await generateDocument()
  }
  if (currentStep.value < 3) {
    currentStep.value++
  }
}

const generateDocument = async () => {
  if (!selectedTemplate.value) return

  try {
    const res = await documentApi.generateDocument({
      templateId: selectedTemplate.value.id,
      data: formData.value
    })
    downloadPath.value = res.data.filePath
    generated.value = true
  } catch (e: any) {
    uni.showToast({
      title: e.message || '生成失败',
      icon: 'none'
    })
  }
}

const downloadDocument = () => {
  if (!downloadPath.value) return
  const url = documentApi.getDownloadUrl(downloadPath.value)
  window.open(url, '_blank')
}
</script>

<style lang="scss" scoped>
.document-generate {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.step-bar {
  display: flex;
  justify-content: space-between;
  padding: 30rpx 40rpx;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 10;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  flex: 1;

  .step-num {
    width: 48rpx;
    height: 48rpx;
    line-height: 48rpx;
    text-align: center;
    border-radius: 50%;
    background: #e6e6e6;
    color: #999;
    font-size: 24rpx;
  }

  .step-text {
    font-size: 22rpx;
    color: #999;
  }

  &.active .step-num {
    background: #1890ff;
    color: #fff;
  }

  &.active .step-text {
    color: #1890ff;
  }

  &.current .step-num {
    background: #1890ff;
    color: #fff;
    box-shadow: 0 4rpx 12rpx rgba(24, 144, 255, 0.4);
  }
}

.step-content {
  padding: 20rpx;
}

.step-panel {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20rpx); }
  to { opacity: 1; transform: translateY(0); }
}

.section-title {
  font-size: 32rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 20rpx;
}

.section-subtitle {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 20rpx;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20rpx;
}

.category-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx 20rpx;
  text-align: center;
  border: 2rpx solid transparent;
  transition: all 0.2s;

  &.selected {
    border-color: #1890ff;
    background: #e6f7ff;
  }

  .cat-icon {
    font-size: 48rpx;
    margin-bottom: 12rpx;
  }

  .cat-name {
    font-size: 26rpx;
    color: #333;
  }
}

.template-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.template-card {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  border: 2rpx solid transparent;

  &.selected {
    border-color: #1890ff;
    background: #e6f7ff;
  }

  .tpl-name {
    font-size: 28rpx;
    font-weight: 500;
    color: #333;
    margin-bottom: 8rpx;
  }

  .tpl-desc {
    font-size: 24rpx;
    color: #666;
    margin-bottom: 8rpx;
  }

  .tpl-vars {
    font-size: 22rpx;
    color: #999;
  }
}

.empty {
  text-align: center;
  padding: 60rpx;
  color: #999;
  font-size: 26rpx;
}

.form-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.form-item {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
}

.form-label {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 12rpx;
  font-weight: 500;
}

.form-input {
  height: 72rpx;
  padding: 0 20rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
  font-size: 28rpx;
}

.form-textarea {
  width: 100%;
  min-height: 160rpx;
  padding: 20rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.result-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 60rpx 40rpx;
  text-align: center;

  .result-icon {
    width: 120rpx;
    height: 120rpx;
    line-height: 120rpx;
    background: #52c41a;
    color: #fff;
    border-radius: 50%;
    font-size: 60rpx;
    margin: 0 auto 24rpx;
  }

  .result-title {
    font-size: 36rpx;
    font-weight: 500;
    color: #333;
    margin-bottom: 12rpx;
  }

  .result-tip {
    font-size: 26rpx;
    color: #666;
    margin-bottom: 40rpx;
  }

  .download-btn {
    width: 400rpx;
    height: 88rpx;
    line-height: 88rpx;
    background: #1890ff;
    color: #fff;
    border-radius: 44rpx;
    font-size: 30rpx;
  }
}

.generating {
  background: #fff;
  border-radius: 16rpx;
  padding: 80rpx 40rpx;
  text-align: center;

  .spinner {
    width: 80rpx;
    height: 80rpx;
    border: 4rpx solid #e6e6e6;
    border-top-color: #1890ff;
    border-radius: 50%;
    margin: 0 auto 24rpx;
    animation: spin 1s linear infinite;
  }

  .generating-text {
    font-size: 28rpx;
    color: #666;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 20rpx;
  padding: 20rpx 40rpx;
  background: #fff;
  box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.05);
}

.btn {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  font-size: 30rpx;
  text-align: center;
}

.prev-btn {
  background: #fff;
  border: 1px solid #d9d9d9;
  color: #666;
}

.next-btn {
  background: #1890ff;
  color: #fff;

  &[disabled] {
    opacity: 0.5;
  }
}
</style>
