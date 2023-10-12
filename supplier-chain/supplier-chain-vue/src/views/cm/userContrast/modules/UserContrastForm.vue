<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-item label="租户代码" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['tenantCode']" placeholder="请输入租户代码"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="公司名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag v-decorator="['companyName']" dict="sys_tenant,name,name"/>
            </a-form-item>
          </a-col>
          <!--在公司名称对照编辑页面添加配置下拉框 addBy liujiazhi 2021.3.25-->
          <a-col :span="24">
            <a-form-item label="控款配置" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['configuration']" :trigger-change="true"
                                 dictCode="tenant_configuration"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <!-- 编辑页面添加删除入库单配置下拉框 addBy liujiazhi 2021.5.20 -->
          <a-col :span="24">
            <a-form-item label="删除入库单配置" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['stockConfiguration']" :trigger-change="true"
                                 dictCode="stock_configuration"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="中板合并配置" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['cutdealConfiguration']" :trigger-change="true"
                                 dictCode="cutdeal_configuration"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="销售单仓库配置" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['orderStockConfiguration']" :trigger-change="true"
                                 dictCode="order_stock_configuration"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="自动结算" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['autoSettle']" :trigger-change="true"
                                 dictCode="yes_no_status"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="异客户批量结算" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['diffCusBatSettle']" :trigger-change="true"
                                 dictCode="yes_no_status"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="提单明细显示基础单价" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['salesDetBasicPrice']" :trigger-change="true"
                                 dictCode="yes_no_status"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="提单明细显示运费" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['salesDetDeliverExpense']" :trigger-change="true"
                                 dictCode="yes_no_status"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="提单明细显示加价" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['salesDetAddPrice']" :trigger-change="true"
                                 dictCode="yes_no_status"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="入库收货单位校验" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['storageConsigneeUnitCheck']" :trigger-change="true"
                                 dictCode="yes_no_status"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="库存定时任务配置" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['inventoryTimingConfiguration']" :trigger-change="true"
                                 dictCode="yes_no_status"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="船运单管理配置" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['shippingManagementConfiguration']" :trigger-change="true"
                                 dictCode="yes_no_status"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="提单明细显示理论重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag v-decorator="['salesDetTheoryWeight']"
                                   :dictOptions="this.salesDetTheoryWeight"></j-search-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="提单明细显示理论单重" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag v-decorator="['salesDetTheoryPrice']"
                                   :dictOptions="this.salesDetTheoryWeight"></j-search-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="开单校验单价" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['averageCostCheck']" :trigger-change="true"
                                 dictCode="yes_no_status"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="产品大类单据编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['prodClassSerialNo']" :trigger-change="true"
                                 dictCode="yes_no_status"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="装车定时任务配置" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['stackTimingConfiguration']" :trigger-change="true"
                                 dictCode="yes_no_status"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="红冲复核" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['writeOffReviewer']" :trigger-change="true"
                                 dictCode="yes_no_status"></j-dict-select-tag>
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
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'

  export default {
    name: 'UserContrastForm',
    components: {
      JFormContainer,
      JSearchSelectTag,
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
        salesDetTheoryWeight: [],
        queryParam: {
          tenant_configuration: '',
          stock_configuration: '',
          cutdeal_configuration: '',
          yes_no_status: ''
        },
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
          add: '/bd/companyTenant/add',
          edit: '/bd/companyTenant/edit',
          queryById: '/cm/userContrast/queryById',
          selectSalesDetTheoryWeight: '/bd/companyTenant/selectSalesDetTheoryWeight'
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
      this.selectSalesDetTheoryWeight()
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
          this.form.setFieldsValue(pick(this.model, 'tenantCode', 'companyName', 'configuration', 'stockConfiguration', 'cutdealConfiguration', 'orderStockConfiguration', 'autoSettle', 'diffCusBatSettle', 'salesDetDeliverExpense', 'salesDetAddPrice', 'storageConsigneeUnitCheck', 'inventoryTimingConfiguration', 'shippingManagementConfiguration', 'salesDetTheoryWeight', 'salesDetTheoryPrice', 'shippingManagementConfiguration', 'salesDetBasicPrice', 'averageCostCheck', 'prodClassSerialNo', 'stackTimingConfiguration', 'writeOffReviewer'))
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
        this.form.setFieldsValue(pick(row, 'tenantCode', 'companyName', 'configuration', 'stockConfiguration', 'cutdealConfiguration', 'orderStockConfiguration', 'autoSettle', 'diffCusBatSettle', 'salesDetDeliverExpense', 'salesDetAddPrice', 'storageConsigneeUnitCheck', 'inventoryTimingConfiguration', 'shippingManagementConfiguration', 'salesDetTheoryWeight', 'salesDetTheoryPrice', 'shippingManagementConfiguration', 'salesDetBasicPrice', 'averageCostCheck', 'prodClassSerialNo', 'stackTimingConfiguration', 'writeOffReviewer'))
      },
      selectSalesDetTheoryWeight() {
        getAction(this.url.selectSalesDetTheoryWeight).then((res) => {
          if (res.code = 200) {
            this.salesDetTheoryWeight = res.result
          }
        })
      }
    }
  }
</script>