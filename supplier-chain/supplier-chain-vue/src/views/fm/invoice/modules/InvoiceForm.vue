<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <!-- 主表单区域 -->
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="12">
            <a-form-item label="账户名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['accountType']" placeholder="请输入账户名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="顾客" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag v-decorator="['customerId']" dict="cm_customer_profile where  del_flag=0,company_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="审核状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['status']" :trigger-change="true"
                                 dictCode="common_check_status" placeholder="请选择审核状态"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="总金额" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['amount']" placeholder="请输入总金额"
                              :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                              :parser="value => value.replace(/\$\s?|(,*)/g, '')"style="width: 100%"/>
            </a-form-item>
          </a-col>
          <!--
                    <a-col :span="12" >
                      <a-form-item label="租户id" :labelCol="labelCol" :wrapperCol="wrapperCol">
                        <a-input v-decorator="['tenantId']" placeholder="请输入租户id"></a-input>
                      </a-form-item>
                    </a-col>
          -->
          <a-col :span="12">
            <a-form-item label="单号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['hdorderno']" placeholder="请输入单号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="单据日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择单据日期" v-decorator="['lasttime']" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="购买方" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['receiverName']" placeholder="请输入购买方"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="税号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['taxnum']" placeholder="请输入税号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="地址电话" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['addresstel']" placeholder="请输入地址电话"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="银行账户" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['bankinfo']" placeholder="请输入银行账户"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="合计金额" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['invoiceMakeMoney']" placeholder="请输入合计金额"
                              :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                              :parser="value => value.replace(/\$\s?|(,*)/g, '')"style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="操作员" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['checker1']" placeholder="请输入操作员"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['notes']" placeholder="请输入备注"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="发票号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['invoiceno']" placeholder="请输入发票号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="专票与普票识别参数 =s" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['billtype']" placeholder="请输入专票与普票识别参数 =s"></a-input>
            </a-form-item>
          </a-col>
          <!--
                   <a-col :span="12" >
                 <a-form-item label="结算id" :labelCol="labelCol" :wrapperCol="wrapperCol">
                       <a-input-number v-decorator="['settleid']" placeholder="请输入结算id" style="width: 100%"/>
                     </a-form-item>
                   </a-col>
                   <a-col :span="12" >
                     <a-form-item label="删除标志" :labelCol="labelCol" :wrapperCol="wrapperCol">
                       <a-input-number v-decorator="['delFlag']" placeholder="请输入删除标志" style="width: 100%"/>
                     </a-form-item>
                   </a-col>
          -->
        </a-row>
      </a-form>
    </j-form-container>
    <!-- 子表单区域 -->
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="发票明细" :key="refKeys[0]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="invoiceDetailTable.loading"
          :columns="invoiceDetailTable.columns"
          :dataSource="invoiceDetailTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true"/>
      </a-tab-pane>
    </a-tabs>
  </a-spin>
</template>

<script>

  import pick from 'lodash.pick'
  import { getAction } from '@/api/manage'
  import { FormTypes, getRefPromise } from '@/utils/JEditableTableUtil'
  import { JEditableTableMixin } from '@/mixins/JEditableTableMixin'
  import { validateDuplicateValue } from '@/utils/util'
  import JFormContainer from '@/components/jeecg/JFormContainer'
  import JDate from '@/components/jeecg/JDate'
  import JDictSelectTag from '@/components/dict/JDictSelectTag'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'

  export default {
    name: 'InvoiceForm',
    mixins: [JEditableTableMixin],
    components: {
      JFormContainer,
      JDate,
      JDictSelectTag,
      JSearchSelectTag
    },
    data() {
      return {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },
        labelCol2: {
          xs: { span: 24 },
          sm: { span: 3 }
        },
        wrapperCol2: {
          xs: { span: 24 },
          sm: { span: 20 }
        },
        // 新增时子表默认添加几行空数据
        addDefaultRowNum: 1,
        validatorRules: {},
        refKeys: ['invoiceDetail'],
        tableKeys: ['invoiceDetail'],
        activeKey: 'invoiceDetail',
        // 发票明细
        invoiceDetailTable: {
          loading: false,
          dataSource: [],
          columns: [
/*
            {
              title: '主表ID',
              key: 'mainid',
              type: FormTypes.input,
              width: '200px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
*/
            {
              title: '产品编码',
              key: 'productcode',
              type: FormTypes.input,
              width: '200px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '商品名称',
              key: 'warename',
              type: FormTypes.input,
              width: '200px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '规格型号',
              key: 'warespec',
              type: FormTypes.input,
              width: '200px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '计量单位',
              key: 'wareunit',
              type: FormTypes.input,
              width: '200px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '重量',
              key: 'num',
              type: FormTypes.inputNumber,
              width: '200px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '单价不含税',
              key: 'unitpricewithouttax',
              type: FormTypes.inputNumber,
              width: '200px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '单价(含税)',
              key: 'unitpricewithtax',
              type: FormTypes.inputNumber,
              width: '200px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '不含税金额',
              key: 'amountnotincludedintax',
              type: FormTypes.inputNumber,
              width: '200px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '金额(含税)',
              key: 'payment',
              type: FormTypes.inputNumber,
              width: '200px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '税率',
              key: 'saletax',
              type: FormTypes.inputNumber,
              width: '200px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '折扣',
              key: 'discount',
              type: FormTypes.inputNumber,
              width: '200px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '税额math.round(item.小计.value/1.13*0.13,2)',
              key: 'tax',
              type: FormTypes.inputNumber,
              width: '200px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '序号',
              key: 'mxnum',
              type: FormTypes.inputNumber,
              width: '200px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '税收分类编码',
              key: 'taxsortcode',
              type: FormTypes.input,
              width: '200px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            /*
                        {
                          title: '结算单号',
                          key: 'settleid',
                          type: FormTypes.inputNumber,
                          width:"200px",
                          placeholder: '请输入${title}',
                          defaultValue: '',
                        },
            */
            {
              title: '结算明细号',
              key: 'settlemxid',
              type: FormTypes.inputNumber,
              width: '200px',
              placeholder: '请输入${title}',
              defaultValue: ''
            }
            /*
                        {
                          title: '租户id',
                          key: 'tenantId',
                          type: FormTypes.inputNumber,
                          width:"200px",
                          placeholder: '请输入${title}',
                          defaultValue: '',
                        },
                        {
                          title: '删除标志',
                          key: 'delFlag',
                          type: FormTypes.inputNumber,
                          width:"200px",
                          placeholder: '请输入${title}',
                          defaultValue: '',
                        },
            */
          ]
        },
        url: {
          add: '/fm/invoice/add',
          edit: '/fm/invoice/edit',
          queryById: '/fm/invoice/queryById',
          invoiceDetail: {
            list: '/fm/invoice/queryInvoiceDetailByMainId'
          }
        }
      }
    },
    props: {
      //流程表单data
      formData: {
        type: Object,
        default: () => {
        },
        required: false
      },
      //表单模式：false流程表单 true普通表单
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
      addBefore() {
        this.form.resetFields()
        this.invoiceDetailTable.dataSource = []
      },
      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },
      /** 调用完edit()方法之后会自动调用此方法 */
      editAfter() {
        let fieldval = pick(this.model, 'accountType', 'customerId', 'status', 'amount', 'hdorderno', 'lasttime', 'receiverName', 'taxnum', 'addresstel', 'bankinfo', 'invoiceMakeMoney', 'checker1', 'notes', 'invoiceno', 'billtype')
        this.$nextTick(() => {
          this.form.setFieldsValue(fieldval)
        })
        // 加载子表数据
        if (this.model.id) {
          let params = { id: this.model.id }
          this.requestSubTableData(this.url.invoiceDetail.list, params, this.invoiceDetailTable)
        }
      },
      /** 整理成formData */
      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          invoiceDetailList: allValues.tablesValue[0].values
        }
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
      validateError(msg) {
        this.$message.error(msg)
      },
      popupCallback(row) {
        this.form.setFieldsValue(pick(row, 'accountType', 'customerId', 'status', 'amount', 'hdorderno', 'lasttime', 'receiverName', 'taxnum', 'addresstel', 'bankinfo', 'invoiceMakeMoney', 'checker1', 'notes', 'invoiceno', 'billtype', 'delFlag'))
      }

    }
  }
</script>

<style scoped>
</style>