<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <!-- 主表单区域 -->
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="24" >
            <a-form-item label="税号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['taxNo']" placeholder="请输入税号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24" >
            <a-form-item label="地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['address']" placeholder="请输入地址"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24" >
            <a-form-item label="手机号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['mobile']" placeholder="请输入手机号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24" >
            <a-form-item label="传真" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['fax']" placeholder="请输入传真"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24" >
            <a-form-item label="单位名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['companyName']" placeholder="请输入单位名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24" >
            <a-form-item label="删除标志" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['delFlag']" placeholder="请输入删除标志" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24" >
            <a-form-item label="租户ID" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['tenantId']" placeholder="请输入租户ID" style="width: 100%"/>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </j-form-container>
      <!-- 子表单区域 -->
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="公司银行卡信息" :key="refKeys[0]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="companyInfoBankTable.loading"
          :columns="companyInfoBankTable.columns"
          :dataSource="companyInfoBankTable.dataSource"
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
  import { FormTypes,getRefPromise } from '@/utils/JEditableTableUtil'
  import { JEditableTableMixin } from '@/mixins/JEditableTableMixin'
  import { validateDuplicateValue } from '@/utils/util'
  import JFormContainer from '@/components/jeecg/JFormContainer'

  export default {
    name: 'CompanyInfoForm',
    mixins: [JEditableTableMixin],
    components: {
      JFormContainer,
    },
    data() {
      return {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        labelCol2: {
          xs: { span: 24 },
          sm: { span: 3 },
        },
        wrapperCol2: {
          xs: { span: 24 },
          sm: { span: 20 },
        },
        // 新增时子表默认添加几行空数据
        addDefaultRowNum: 1,
        validatorRules: {
        },
        refKeys: ['companyInfoBank', ],
        tableKeys:['companyInfoBank', ],
        activeKey: 'companyInfoBank',
        // 公司银行卡信息
        companyInfoBankTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '开户人姓名',
              key: 'accountHolderName',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '开户手机号',
              key: 'mobile',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '银行卡号',
              key: 'bankCardNo',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '开户行',
              key: 'bank',
              type: FormTypes.sel_search,
              dictCode:"fm_bank,bank_name,id",
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
          ]
        },
        url: {
          add: "/om/companyInfo/add",
          edit: "/om/companyInfo/edit",
          queryById: "/om/companyInfo/queryById",
          companyInfoBank: {
            list: '/om/companyInfo/queryCompanyInfoBankByMainId'
          },
        }
      }
    },
    props: {
      //流程表单data
      formData: {
        type: Object,
        default: ()=>{},
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
      formDisabled(){
        if(this.formBpm===true){
          if(this.formData.disabled===false){
            return false
          }
          return true
        }
        return this.disabled
      },
      showFlowSubmitButton(){
        if(this.formBpm===true){
          if(this.formData.disabled===false){
            return true
          }
        }
        return false
      }
    },
    created () {
      //如果是流程中表单，则需要加载流程表单data
      this.showFlowData();
    },
    methods: {
      addBefore(){
        this.form.resetFields()
        this.companyInfoBankTable.dataSource=[]
      },
      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },
      /** 调用完edit()方法之后会自动调用此方法 */
      editAfter() {
        let fieldval = pick(this.model,'taxNo','address','mobile','fax','companyName','delFlag','tenantId')
        this.$nextTick(() => {
          this.form.setFieldsValue(fieldval)
        })
        // 加载子表数据
        if (this.model.id) {
          let params = { id: this.model.id }
          this.requestSubTableData(this.url.companyInfoBank.list, params, this.companyInfoBankTable)
        }
      },
      /** 整理成formData */
      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          companyInfoBankList: allValues.tablesValue[0].values,
        }
      },
      //渲染流程表单数据
      showFlowData(){
        if(this.formBpm === true){
          let params = {id:this.formData.dataId};
          getAction(this.url.queryById,params).then((res)=>{
            if(res.success){
              this.edit (res.result);
            }
          })
        }
      },
      validateError(msg){
        this.$message.error(msg)
      },
     popupCallback(row){
       this.form.setFieldsValue(pick(row,'taxNo','address','mobile','fax','companyName','delFlag','tenantId'))
     },

    }
  }
</script>

<style scoped>
</style>