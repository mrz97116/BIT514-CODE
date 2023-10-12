<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="12">
            <a-form-item label="顾客名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag v-decorator="['custName', validatorRules.custName]" placeholder="请选择顾客"
                                 :trigger-change="true" dictCode="cm_customer_profile,company_name,company_name"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="产品大类" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-popup
                v-decorator="['prodClassCode', validatorRules.prodClassCode]"
                :trigger-change="true"
                org-fields="prod_class_code,mat_kind,mat_len,sg_sign,prod_cname,mat_width,mat_thick,preuse_qty"
                dest-fields="prodClassCode,matKind,matLen,sgSign,prodCname,matWidth,matThick,qty"
                code="sm_mat"
                @callback="popupCallback"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="物料种类" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag disabled type="list" v-decorator="['matKind']" :trigger-change="true" dictCode="mat_kind" placeholder="请选择物料种类"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="长度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matLen']" disabled placeholder="请输入长度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="牌号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['sgSign']" disabled placeholder="请输入牌号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="品名中文" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prodCname']" disabled placeholder="请输入品名中文"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="宽度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matWidth']" disabled placeholder="请输入宽度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="厚度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matThick']" disabled placeholder="请输入厚度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="数量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['qty']" disabled placeholder="请输入数量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['weight', validatorRules.weight]" placeholder="请输入重量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="合同编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['contractNo', validatorRules.contractNo]" placeholder="请输入合同编号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="采购状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['status', validatorRules.status]" disabled
                                 :trigger-change="true" dictCode="purchaseApply_status" placeholder="请选择采购状态"/>
            </a-form-item>
          </a-col>
          <a-col v-if="showFlowSubmitButton" :span="24" style="text-align: center">
            <a-button @click="submitForm">提 交</a-button>
          </a-col>
        </a-row>
      </a-form>
    </j-form-container>
  </a-spin>
</template>

<script>

import { httpAction, getAction } from '@/api/manage'
import pick from 'lodash.pick'
import { validateDuplicateValue } from '@/utils/util'
import JFormContainer from '@/components/jeecg/JFormContainer'
import JDictSelectTag from '@/components/dict/JDictSelectTag'

export default {
  name: 'PurchaseApplyForm',
  components: {
    JFormContainer,
    JDictSelectTag,
  },
  props: {
    //流程表单data
    formData: {
      type: Object,
      default: () => {
      },
      required: false
    },
    //表单模式：true流程表单 false普通表单
    formBpm: {
      type: Boolean,
      default: false,
      required: false
    },
    //表单禁用
    disabled: {
      type: Boolean,
      default: false,
      required: false
    }
  },
  data() {
    return {
      form: this.$form.createForm(this),
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      confirmLoading: false,
      validatorRules: {
        custName: {
          rules: [
            { required: true, message: '请输入顾客名称!' },
          ]
        },
        prodClassCode: {
          rules: [
            { required: true, message: '请输入产品大类!' },
          ]
        },
        weight: {
          rules: [
            { required: true, message: '请输入重量!' },
          ]
        },
        contractNo: {
          rules: [
            { required: true, message: '请输入合同编号!' },
          ]
        },
        status: {
          rules: [
            { required: true, message: '请输入采购状态!' },
          ]
        },
      },
      url: {
        add: '/sm/purchaseApply/add',
        edit: '/sm/purchaseApply/edit',
        queryById: '/sm/purchaseApply/queryById'
      }
    }
  },
  computed: {
    formDisabled() {
      if (this.formBpm === true) {
        if (this.formData.disabled === false) {
          return false
        }
        return true
      }
      return this.disabled
    },
    showFlowSubmitButton() {
      if (this.formBpm === true) {
        if (this.formData.disabled === false) {
          return true
        }
      }
      return false
    }
  },
  created() {
    //如果是流程中表单，则需要加载流程表单data
    this.showFlowData()
  },
  methods: {
    add() {
      this.edit({})
    },
    edit(record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue(pick(this.model, 'custName', 'prodClassCode', 'matKind', 'matLen', 'sgSign', 'prodCname',
          'matWidth', 'matThick', 'qty', 'weight', 'contractNo', 'status'))
      })
    },
    //渲染流程表单数据
    showFlowData() {
      if (this.formBpm === true) {
        let params = { id: this.formData.dataId }
        getAction(this.url.queryById, params).then((res) => {
          if (res.success) {
            this.edit(res.result)
          }
        })
      }
    },
    submitForm() {
      const that = this
      // 触发表单验证
      this.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let httpurl = ''
          let method = ''
          if (!this.model.id) {
            httpurl += this.url.add
            method = 'post'
          } else {
            httpurl += this.url.edit
            method = 'put'
          }
          let formData = Object.assign(this.model, values)
          console.log('表单提交数据', formData)
          httpAction(httpurl, formData, method).then((res) => {
            if (res.success) {
              that.$message.success(res.message)
              that.$emit('ok')
            } else {
              that.$message.warning(res.message)
            }
          }).finally(() => {
            that.confirmLoading = false
          })
        }

      })
    },
    popupCallback(row) {
      var status = this.form.getFieldValue('status')
      this.form.setFieldsValue({ status: 'init' })
      this.form.setFieldsValue(pick(row, 'custName', 'prodClassCode', 'matKind', 'matLen', 'sgSign', 'prodCname', 'matWidth',
        'matThick', 'qty', 'weight', 'contractNo', 'status'))
    },
  }
}
</script>