<template>
  <j-modal
    title="详情"
    width="70%"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <j-form-container :disabled="formDisabled">
        <a-form :form="form">
          <a-row>
            <a-col :span="8">
              <a-form-item label="牌号" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input v-decorator="['sgSign']" placeholder="请输入牌号"></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="品名代码" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input v-decorator="['prodCode']" placeholder="请输入品名代码"></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="品名中文" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input v-decorator="['prodCname']" placeholder="请输入品名中文"></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="产品大类" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <j-dict-select-tag type="list" v-decorator="['prodClassCode']" :trigger-change="true"
                                   dictCode="prod_code"
                                   placeholder="请选择产品大类"/>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="品名中文别名" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input v-decorator="['prodCnameOther']" placeholder="请输入品名中文别名"></a-input>
              </a-form-item>
            </a-col>
<!--            <a-col :span="8">-->
<!--              <a-form-item label="仓库" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--                <j-dict-select-tag type="list" v-decorator="['stockHouseId']" :trigger-change="true"-->
<!--                                   dictCode="sm_stock,name,id" placeholder="请选择仓库"/>-->
<!--              </a-form-item>-->
<!--            </a-col>-->
            <a-col :span="8">
              <a-form-item label="库存类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <j-dict-select-tag type="list" v-decorator="['stockType']" :trigger-change="true"
                                   dictCode="stock_type"
                                   placeholder="请选择库存类型"/>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="材料号" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input v-decorator="['matNo']" placeholder="请输入材料号"></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="材料实际重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input v-decorator="['matActWt']" placeholder="请输入材料实际重量"></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="材料净重" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input-number v-decorator="['matNetWt']" placeholder="请输入材料净重"
                                style="width: 100%"/>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="材料磅差重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input-number v-decorator="['matDiscrepWt']" placeholder="请输入材料磅差重量" style="width: 100%"/>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="材料件数" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input-number v-decorator="['matNum']" placeholder="请输入材料件数（根数）" style="width: 100%"/>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="材料种类" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <j-dict-select-tag type="list" v-decorator="['matKind']" :trigger-change="true" dictCode="mat_kind"
                                   placeholder="请选择材料种类"/>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="材料长度" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input-number v-decorator="['matLen']" placeholder="请输入材料长度" style="width: 100%"/>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="材料宽度" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input-number v-decorator="['matWidth']" placeholder="请输入材料宽度"
                                style="width: 100%"/>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="材料厚度" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input-number v-decorator="['matThick']" placeholder="请输入材料厚度"
                                style="width: 100%"/>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="规格" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input v-decorator="['custMatSpecs']" placeholder="请输入规格"></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="采购价" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input-number v-decorator="['purchasePrice']" placeholder="请输入采购价" style="width: 100%"/>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="成本价" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input-number v-decorator="['costPrice']" placeholder="请输入成本价" style="width: 100%"/>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="技术标准" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input v-decorator="['technicalStandard']" placeholder="请输入技术标准"></a-input>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </j-form-container>
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
    formDisabled: {
      default: true
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
        sm: { span: 10 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 13 },
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
    edit(record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue(pick(this.model, 'sgSign', 'prodCode', 'prodCname', 'prodClassCode', 'prodCnameOther', 'matStatus', 'stockType', 'matNo', 'matActWt', 'matNetWt', 'matDiscrepWt', 'matNum','surfaceTreatment', 'matKind', 'availableWeight', 'availableQty', 'preuseQty', 'preuseWeight', 'matLen', 'matWidth', 'matThick', 'matActLen', 'matActWidth', 'matActThick', 'custMatSpecs', 'dealerId', 'purchasePrice', 'technicalStandard', 'addType', 'rawMaterialNo'))
      })
    },
    handleOk() {
      this.visible = false
    },
    handleCancel() {
      this.visible = false
    }
  }
}
</script>
