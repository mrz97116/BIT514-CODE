<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    fullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row>
          <a-col :span="8">
            <a-form-item label="品名中文" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-popup
                v-decorator="['prodCname']"
                :trigger-change="true"
                org-fields="mat_kind,prod_cname,prod_cname_other,wt_method_code,sg_sign,mat_len,mat_width,mat_thick,technincal_standard,prod_class_code,prod_code,cost_price"
                dest-fields="matKind,prodCname,prodCnameOther,wtMethodCode,sgSign,orderLen,orderWidth,orderThick,sgStd,prodClassCode,prodCode,costPrice"
                code="sm_mat"
                :initQueryParam="prodClassCode()"
                @callback="popupCallback"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="标准" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['sgStd']" placeholder="请输入标准"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="订货长度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['orderLen',validatorRules.orderLen]" placeholder="请输入订货长度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="牌号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['sgSign',validatorRules.sgSign]" placeholder="请输入牌号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="品名代码" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prodCode',validatorRules.prodCode]" placeholder="请输入品名代码"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="订货宽度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['orderWidth',validatorRules.orderWidth]" placeholder="请输入订货宽度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="订货厚度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['orderThick',validatorRules.orderThick]" placeholder="请输入订货厚度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="品名中文别名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prodCnameOther',validatorRules.prodCnameOther]" placeholder="请输入品名中文别名"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="物料种类" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['matKind']" :trigger-change="true" dictCode="mat_kind"
                                 placeholder="请选择物料种类"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="物料重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['weight',validatorRules.weight]" placeholder="请输入订货重量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="单价" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['price',validatorRules.price]" placeholder="请输入单价" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="成本价" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['costPrice',validatorRules.costPrice]" placeholder="请输入成本价" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="加价" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['addPrice']" placeholder="请输入加价" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <!--<a-col :span="24">-->
            <!--<a-form-item label="过磅重量" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
              <!--<a-input-number v-decorator="['weightingWeight']" placeholder="请输入过磅重量" style="width: 100%"/>-->
            <!--</a-form-item>-->
          <!--</a-col>-->
          <!--<a-col :span="24">-->
            <!--<a-form-item label="过磅价格" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
              <!--<a-input-number v-decorator="['weightingPrice']" placeholder="请输入过磅价格" style="width: 100%"/>-->
            <!--</a-form-item>-->
          <!--</a-col>-->
          <a-col :span="8">
            <a-form-item label="计重方式" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag v-decorator="['weightType']" placeholder="请选择计重方式" :trigger-change="true"
                                 dictCode="wt_method_code"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="产品大类" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['prodClassCode', validatorRules.prodClassCode]" :trigger-change="true" dictCode="prod_code" disabled placeholder="请输入产品大类"/>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>

import { httpAction } from '@/api/manage'
import pick from 'lodash.pick'
import { validateDuplicateValue } from '@/utils/util'
import JDictSelectTag from '@/components/dict/JDictSelectTag'

export default {
  name: 'OrderDetModal',
  components: {
    JDictSelectTag,
  },
  props: {
    mainId: {
      type: String,
      required: false,
      default: ''
    },
    parentRecord: {
      type: Object,
      default: function () {
        return {}
      },
      required: true
    }
  },
  data() {
    return {
      form: this.$form.createForm(this),
      title: '操作',
      width: 800,
      visible: false,
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
        price: {
          rules: [
            { required: true, message: '请输入单价!' },
          ]
        },
        costPrice: {
          rules: [
            { required: true, message: '请输入成本价!' },
          ]
        },
        sgSign: {
          rules: [
            { required: true, message: '请输入牌号!' },
          ]
        },
        prodCode: {
          rules: [
            { required: true, message: '请输入品名代码!' },
          ]
        },
        orderWidth: {
          rules: [
            { required: true, message: '请输入订货宽度!' },
          ]
        },
        prodCnameOther: {
          rules: [
            { required: true, message: '请输入品名中文别名!' },
          ]
        },
        orderThick: {
          rules: [
            { required: true, message: '请输入订货厚度度!' },
          ]
        },
        orderLen: {
          rules: [
            { required: true, message: '请输入订货长度!' },
          ]
        }
      },
      url: {
        add: '/om/order/addOrderDet',
        edit: '/om/order/editOrderDet',
      }

    }
  },
  created() {

  },
  methods: {
    add() {
      this.edit({})
    },
    prodClassCode() {
      return this.parentRecord.productCode
    },
    edit(record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue(pick(this.model, 'sgStd', 'orderLen', 'sgSign', 'orderWidth', 'orderThick',
          'prodCname', 'prodCnameOther', 'matKind', 'weight', 'price', 'addPrice', 'weightingWeight', 'weightingPrice',
          'weightType', 'prodClassCode','prodCode','costPrice'))
      })
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    handleOk() {
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
          formData['orderMainId'] = this.mainId
          formData['orderNo'] = this.parentRecord.orderNo
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
            that.close()
            //刷新主表数据
            that.$emit('primaryTableLoadData')
          })
        }

      })
    },
    handleCancel() {
      this.close()
    },
    popupCallback(row) {
      this.form.setFieldsValue(pick(row, 'orderMainId', 'sgStd', 'orderLen', 'sgSign', 'orderWidth', 'orderThick',
        'prodCname', 'prodCnameOther', 'matKind', 'weight', 'price', 'addPrice', 'weightingWeight', 'weightingPrice',
        'weightType', 'prodClassCode','prodCode','costPrice'))
    },

  }
}
</script>
