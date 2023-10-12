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
            <a-form-item label="条款内容" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-popup
                v-decorator="['provisionContent', validatorRules.provisionContent]"
                :trigger-change="true"
                org-fields="provision_type,provision_content,validity_status"
                dest-fields="provisionType,provisionContent,validityStatus"
                code="om_provision_base"
                @callback="popupCallback"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="条款类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['provisionType', validatorRules.provisionType]"
                                 :trigger-change="true" dictCode="provision_type" placeholder="请选择条款类型"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="生效标识" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['validityStatus']" :trigger-change="true"
                                 dictCode="provision_status" placeholder="请选择生效标识"/>
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
  name: 'ContractProvisionModal',
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
      required: false
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
        provisionType: {
          rules: [
            { required: true, message: '请输入条款类型!' },
          ]
        },
        provisionContent: {
          rules: [
            { required: true, message: '请输入条款内容!' },
          ]
        },
      },
      url: {
        add: '/om/contract/addContractProvision',
        edit: '/om/contract/editContractProvision',
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
        this.form.setFieldsValue(pick(this.model, 'provisionType', 'provisionContent', 'validityStatus', 'contractNo'))
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
      this.form.setFieldsValue(pick(row, 'provisionType', 'provisionContent', 'validityStatus', 'contractNo'))
    },

  }
}
</script>
