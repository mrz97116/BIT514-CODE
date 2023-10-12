<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <!-- 主表单区域 -->
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="12" >
            <a-form-item label="单位名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['companyName', validatorRules.companyName]" placeholder="请输入单位名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="税号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['taxNo']" placeholder="请输入税号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="开票名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['billingName']" placeholder="请输入开票名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['address']" placeholder="请输入地址"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="区域" :labelCol="labelCol" :wrapperCol="wrapperCol">
		      <j-area-linkage type="cascader" v-decorator="['area']" placeholder="请输入省市区"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="座机号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['landLineNo']" placeholder="请输入座机"></a-input>
            </a-form-item>
          </a-col>

          <a-col :span="12" >
            <a-form-item label="营业执照" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-image-upload isMultiple v-decorator="['businessLicense']"></j-image-upload>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </j-form-container>
      <!-- 子表单区域 -->
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="顾客银行卡信息" :key="refKeys[0]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="customerBankTable.loading"
          :columns="customerBankTable.columns"
          :dataSource="customerBankTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true"/>
      </a-tab-pane>
      <a-tab-pane tab="顾客收货地址信息" :key="refKeys[1]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[1]"
          :loading="customerAddressTable.loading"
          :columns="customerAddressTable.columns"
          :dataSource="customerAddressTable.dataSource"
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
  import JFormContainer from '@/components/jeecg/JFormContainer'
  import JDictSelectTag from "@/components/dict/JDictSelectTag"
  import JAreaLinkage from '@comp/jeecg/JAreaLinkage'
  import JImageUpload from '@/components/jeecg/JImageUpload'

  export default {
    name: 'CustomerProfileForm',
    mixins: [JEditableTableMixin],
    components: {
      JFormContainer,
      JDictSelectTag,
      JAreaLinkage,
      JImageUpload,
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
          companyName: {
            rules: [
              { required: true, message: '请输入单位名称!'},
            ]
          },
        },
        refKeys: ['customerBank', 'customerAddress', ],
        tableKeys:['customerBank', 'customerAddress', ],
        activeKey: 'customerBank',
        // 顾客银行卡信息
        customerBankTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '开户行',
              key: 'bank',
              type: FormTypes.sel_search,
              dictCode:"fm_bank,bank_name,id",
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
          ]
        },
        // 顾客收货地址信息
        customerAddressTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '联系人姓名',
              key: 'contactName',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '联系电话',
              key: 'contactPhone',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [{ pattern: "m", message: "${title}格式不正确" }],
            },
            {
              title: '身份证号',
              key: 'idCardNo',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '地址',
              key: 'address',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
          ]
        },
        url: {
          add: "/cm/customerProfile/add",
          edit: "/cm/customerProfile/edit",
          queryById: "/cm/customerProfile/queryById",
          customerBank: {
            list: '/cm/customerProfile/queryCustomerBankByMainId'
          },
          customerAddress: {
            list: '/cm/customerProfile/queryCustomerAddressByMainId'
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
        this.customerBankTable.dataSource=[]
        this.customerAddressTable.dataSource=[]
      },
      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },
      /** 调用完edit()方法之后会自动调用此方法 */
      editAfter() {
        let fieldval = pick(this.model,'companyName','alias','taxNo','address','mobile','fax','area','status','type','businessLicense','landLineNo','billingName')
        this.$nextTick(() => {
          this.form.setFieldsValue(fieldval)
        })
        // 加载子表数据
        if (this.model.id) {
          let params = { id: this.model.id }
          this.requestSubTableData(this.url.customerBank.list, params, this.customerBankTable)
          this.requestSubTableData(this.url.customerAddress.list, params, this.customerAddressTable)
        }
      },
      /** 整理成formData */
      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          customerBankList: allValues.tablesValue[0].values,
          customerAddressList: allValues.tablesValue[1].values,
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
       this.form.setFieldsValue(pick(row,'companyName','alias','taxNo','address','mobile','fax','area','status','type','businessLicense','landLineNo','billingName'))
     },

    }
  }
</script>

<style scoped>
</style>