<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="8">
            <a-form-item label="牌号" :labelCol="labelCol" :wrapperCol="wrapperCol" :disabled="inputDisabled">
              <j-popup
                v-decorator="['sgSign',validatorRules.sgSign]"
                :trigger-change="true"
                org-fields="prod_cname,prod_cname_other,mat_kind,sg_sign,mat_thick,mat_width,mat_len,prod_class_code,price"
                dest-fields="prodCname,prodCnameOther,matKind,sgSign,mat_thick,mat_width,mat_len,prodClassCode,purchasePrice"
                code="om_order_mat"
                @callback="popupCallback" :disabled="inputDisabled"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="品名中文" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prodCname',validatorRules.prodCname]" placeholder="请输入品名中文" :disabled="inputDisabled"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="产品大类代码" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['prodClassCode',validatorRules.prodClassCode]" :trigger-change="true" dictCode="prod_code"
                                 placeholder="请选择产品大类代码" :disabled="inputDisabled"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="品名中文别名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prodCnameOther',validatorRules.prodCnameOther]" placeholder="请输入品名中文别名" :disabled="inputDisabled"></a-input>
            </a-form-item>
          </a-col>

            <a-col :span="8">
              <a-form-item label="仓库" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <j-dict-select-tag type="list" v-decorator="['stockHouseId']" :trigger-change="true"
                                   dictCode="sm_stock,name,id" placeholder="请选择仓库" :disabled="inputDisabled"/>
              </a-form-item>
            </a-col>
          <a-col :span="8">
            <a-form-item label="库存类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['stockType']" :trigger-change="true" dictCode="stock_type"
                                 placeholder="请选择库存类型" :disabled="inputDisabled"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="材料号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['matNo']" placeholder="请输入材料号" :disabled="inputDisabled"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="材料净重" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matNetWt',validatorRules.matNetWt]" placeholder="请输入材料净重"
                              style="width: 100%" :disabled="inputDisabled"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="材料磅差重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matDiscrepWt']" placeholder="请输入材料磅差重量" style="width: 100%" :disabled="inputDisabled"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="材料件数" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matNum',validatorRules.matNum]" placeholder="请输入材料件数" style="width: 100%" :disabled="inputDisabled"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="材料种类" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['matKind']" :trigger-change="true" dictCode="mat_kind"
                                 placeholder="请选择材料种类" :disabled="inputDisabled"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="材料厚度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matThick',validatorRules.matThick]" placeholder="请输入材料厚度"
                              style="width: 100%" :disabled="inputDisabled"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="材料宽度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matWidth',validatorRules.matWidth]" placeholder="请输入材料宽度"
                              style="width: 100%" :disabled="inputDisabled"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="材料长度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matLen',validatorRules.matLen]" placeholder="请输入材料长度" style="width: 100%" :disabled="inputDisabled"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="规格" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['custMatSpecs']" placeholder="请输入规格" :disabled="inputDisabled"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="采购价" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['purchasePrice']" placeholder="请输入采购价" style="width: 100%" :disabled="inputDisabled"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="成本价" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['costPrice']" placeholder="请输入成本价" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="技术标准" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['technicalStandard']" placeholder="请输入技术标准" :disabled="inputDisabled"></a-input>
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
  name: 'MatForm',
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
    },
    inputDisabled: {
      typeL: Boolean,
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
        sgSign: {
          rules: [
            { required: true, message: '请选择牌号!' }
          ]
        },
        prodCname: {
          rules: [
            { required: true, message: '请输入品名中文!' }
          ]
        },
        prodCnameOther: {
          rules: [
            { required: true, message: '请输入品名中文别名!' }
          ]
        },
        matLen: {
          rules: [
            { required: true, message: '请输入材料长度!' }
          ]
        },
        matWidth: {
          rules: [
            { required: true, message: '请输入材料宽度!' }
          ]
        },
        matThick: {
          rules: [
            { required: true, message: '请输入材料厚度!' }
          ]
        },
        matNetWt: {
          rules: [
            { required: true, message: '请输入材料净重!' }
          ]
        },
        prodClassCode: {
          rules: [
            { required: true, message: '请选择产品大类!' }
          ]
        },
        matNum: {
          rules: [
            { required: true, message: '请输入材料件数!' }
          ]
        },
        // addType: {
        //   rules: [
        //     { required: true, message: '请选择新增类型'}
        //   ]
        // },
        // rawMaterialNo: {
        //   rules: [
        //     { required: true, message: '请输入原材料号'}
        //   ]
        // }
      },
      url: {
        add: '/sm/mat/add',
        edit: '/sm/mat/edit',
        queryById: '/sm/mat/queryById'
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
      this.inputDisabled
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue(pick(this.model, 'sgSign', 'prodCode', 'prodCname', 'prodClassCode', 'prodCnameOther', 'matStatus', 'stockHouseId', 'stockType', 'matNo', 'matActWt', 'matNetWt', 'matDiscrepWt', 'matNum', 'matKind', 'availableWeight', 'availableQty', 'preuseQty', 'preuseWeight', 'matLen', 'matWidth', 'matThick', 'matActLen', 'matActWidth', 'matActThick', 'custMatSpecs', 'dealerId', 'purchasePrice', 'technicalStandard', 'addType', 'rawMaterialNo'))
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
      // debugger;
      const that = this
      // 触发表单验证
      if (that.form.getFieldValue('addType') == 'machining_add' && that.form.getFieldValue('rawMaterialNo') == null) {
        that.$message.warning('请输入原材料号！')
      } else {
        that.$confirm({
          title: '确认提交',
          content: '是否提交数据?',
          onOk: function () {
            that.form.validateFields((err, values) => {
              if (!err) {
                that.confirmLoading = true
                let httpurl = ''
                let method = ''
                if (!that.model.id) {
                  httpurl += that.url.add
                  method = 'post'
                } else {
                  httpurl += that.url.edit
                  method = 'put'
                }
                let formData = Object.assign(that.model, values)
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

          }
        })
      }
    },
    popupCallback(row) {
      this.form.setFieldsValue(pick(row, 'sgSign', 'prodCode', 'prodCname', 'prodClassCode', 'prodCnameOther', 'matStatus', 'stockHouseId', 'stockType', 'matNo', 'matActWt', 'matNetWt', 'matDiscrepWt', 'matNum', 'matKind', 'availableWeight', 'availableQty', 'preuseQty', 'preuseWeight', 'matLen', 'matWidth', 'matThick', 'matActLen', 'matActWidth', 'matActThick', 'custMatSpecs', 'dealerId', 'purchasePrice', 'technicalStandard', 'rawMaterialNo', 'addType'))
    },
  }
}
</script>