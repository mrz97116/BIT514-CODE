<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row>
          <a-col :span="24">
            <a-form-item label="订货长度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['orderLen']" placeholder="请输入订货长度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="订货宽度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['orderWidth']" placeholder="请输入订货宽度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="订货厚度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['orderThick']" placeholder="请输入订货厚度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="预交重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['preWeight']" placeholder="请输入预交重量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="预交件数" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['preQty']" placeholder="请输入预交件数" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="已交重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['delivyWeight']" placeholder="请输入已交重量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="已交件数" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['delivyQty']" placeholder="请输入已交件数" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="合同明细号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['contractListNo']" placeholder="请输入合同明细号" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="品名中文" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prodCname']" placeholder="请输入品名中文"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="品名中文别名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prodCnameOther']" placeholder="请输入品名中文别名"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="标准" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['sgStd']" placeholder="请输入标准"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="物料种类" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['matKind']" placeholder="请输入物料种类"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="过磅价格" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['weightingPrice']" placeholder="请输入过磅价格" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="产品大类" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prodClassCode']" placeholder="请输入产品大类"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="加价" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['addPrice']" placeholder="请输入加价" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="物料重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['weight']" placeholder="请输入物料重量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="单价" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['price']" placeholder="请输入单价" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="过磅重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['weightingWeight']" placeholder="请输入过磅重量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="计重方式" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag v-decorator="['weightType']" placeholder="请选择计重方式" :trigger-change="true"
                                 dictCode="wt_method_code"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <!--          <a-col :span="24">-->
          <!--            <a-form-item label="合同编号" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
          <!--              <a-input v-decorator="['contractNo']" placeholder="请输入合同编号"></a-input>-->
          <!--            </a-form-item>-->
          <!--          </a-col>-->
        </a-row>
      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>

import { httpAction } from '@/api/manage'
import pick from 'lodash.pick'
import { validateDuplicateValue } from '@/utils/util'

export default {
  name: 'ContractDetModal',
  components: {},
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
      validatorRules: {},
      url: {
        add: '/om/contract/addContractDet',
        edit: '/om/contract/editContractDet',
      }

    }
  },
  created() {
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
        this.form.setFieldsValue(pick(this.model, 'orderLen', 'orderWidth', 'orderThick', 'prodCname',
          'prodCnameOther', 'sgStd', 'matKind', 'weightingPrice', 'prodClassCode', 'addPrice', 'weight', 'price',
          'weightingWeight', 'weightType','preWeight','preQty','delivyWeight','delivyQty','contractListNo'))
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
          formData['parentId'] = this.mainId
          formData['contractNo'] = this.parentRecord.contractNo
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
          })
        }

      })
    },
    handleCancel() {
      this.close()
    },
    popupCallback(row) {
      this.form.setFieldsValue(pick(row, 'orderLen', 'orderWidth', 'orderThick', 'prodCname',
        'prodCnameOther', 'sgStd', 'matKind', 'weightingPrice', 'prodClassCode', 'addPrice', 'weight', 'price',
        'weightingWeight', 'weightType','preWeight','preQty','delivyWeight','delivyQty','contractListNo'))
    },

  }
}
</script>
