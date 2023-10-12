<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <!-- 主表单区域 -->
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="12" >
            <a-form-item label="订单编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['orderNo']" disabled placeholder="请输入订单编号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="顾客名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag v-decorator="['customerId', validatorRules.customerId]" dict="cm_customer_profile,company_name,id" />
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="订单性质" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['orderType', validatorRules.orderType]" :trigger-change="true" dictCode="order_type" placeholder="请选择订单性质"/>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="目的地" :labelCol="labelCol" :wrapperCol="wrapperCol">
		          <j-area-linkage type="cascader" v-decorator="['desCity', validatorRules.desCity]" placeholder="请输入省市区"/>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="产品大类" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['productCode', validatorRules.productCode]" :trigger-change="true" dictCode="prod_code" placeholder="请选择产品大类"/>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="订单总重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['orderWt']" disabled placeholder="通过订单明细自动计算总重量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="总价" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['totalPrice']" disabled placeholder="通过订单明细自动计算总价" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['remark']" placeholder="请输入备注"></a-input>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </j-form-container>
      <!-- 子表单区域 -->
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="订单明细表" :key="refKeys[0]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="orderDetTable.loading"
          :columns="orderDetTable.columns"
          :dataSource="orderDetTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true"/>
      </a-tab-pane>
      <a-tab-pane tab="商务条款" :key="refKeys[1]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[1]"
          :loading="provisionTable.loading"
          :columns="provisionTable.columns"
          :dataSource="provisionTable.dataSource"
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
  import JDictSelectTag from "@/components/dict/JDictSelectTag"
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import JAreaLinkage from '@comp/jeecg/JAreaLinkage'

  export default {
    name: 'OrderForm',
    mixins: [JEditableTableMixin],
    components: {
      JFormContainer,
      JDictSelectTag,
      JSearchSelectTag,
      JAreaLinkage,
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
          orderType: {
            rules: [
              { required: true, message: '请输入订单性质!'},
            ]
          },
          desCity: {
            rules: [
              { required: true, message: '请输入目的地!'},
            ]
          },
          productCode: {
            rules: [
              { required: true, message: '请输入产品大类!'},
            ]
          },
          customerId: {
            rules: [
              { required: true, message: '请选择顾客!'},
            ]
          }
        },
        refKeys: ['orderDet', 'provision', ],
        tableKeys:['orderDet', 'provision', ],
        activeKey: 'orderDet',
        // 订单明细表
        orderDetTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '品名中文',
              key: 'prodCname',
              type: FormTypes.popup,
              popupCode:"sm_mat",
              orgFields:"mat_kind,prod_cname,prod_cname_other,wt_method_code,sg_sign,mat_len,mat_width,mat_thick,technincal_standard,prod_class_code,prod_code,cost_price",
              destFields:"matKind,prodCname,prodCnameOther,wtMethodCode,sgSign,orderLen,orderWidth,orderThick,sgStd,prodClassCode,prodCode,costPrice",
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '订货重量',
              key: 'weight',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '单价',
              key: 'price',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '成本价',
              key: 'costPrice',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '标准',
              key: 'sgStd',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '订货长度',
              key: 'orderLen',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '订货宽度',
              key: 'orderWidth',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '订货厚度',
              key: 'orderThick',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '品名中文别名',
              key: 'prodCnameOther',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '牌号',
              key: 'sgSign',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '物料种类',
              key: 'matKind',
              type: FormTypes.select,
              dictCode:"mat_kind",
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '加价',
              key: 'addPrice',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '过磅重量',
              key: 'weightingWeight',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '过磅价格',
              key: 'weightingPrice',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '计重方式',
              key: 'weightType',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '产品大类',
              key: 'prodClassCode',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '品名代码',
              key: 'prodCode',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },

            {
              title: 'mat_kind',
              key: 'mat_kind',
              type:"hidden"
            },

            {
              title: 'prod_cname',
              key: 'prod_cname',
              type:"hidden"
            },

            {
              title: 'prod_cname_other',
              key: 'prod_cname_other',
              type:"hidden"
            },

            {
              title: 'wt_method_code',
              key: 'wt_method_code',
              type:"hidden"
            },

            {
              title: 'sg_sign',
              key: 'sg_sign',
              type:"hidden"
            },

            {
              title: 'order_len',
              key: 'order_len',
              type:"hidden"
            },

            {
              title: 'order_width',
              key: 'order_width',
              type:"hidden"
            },

            {
              title: 'order_thick',
              key: 'order_thick',
              type:"hidden"
            },

            {
              title: 'sg_std',
              key: 'sg_std',
              type:"hidden"
            },
          ]
        },
        // 商务条款
        provisionTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '条款内容',
              key: 'provisionContent',
              type: FormTypes.popup,
              popupCode:"om_provision_base",
              destFields:"provisionType,provisionContent,validityStatus",
              orgFields:"provision_type,provision_content,validity_status",
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '条款类型',
              key: 'provisionType',
              type: FormTypes.select,
              dictCode:"provision_type",
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '生效标识',
              key: 'validityStatus',
              type: FormTypes.select,
              dictCode:"provision_status",
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },

            {
              title: 'provision_type',
              key: 'provision_type',
              type:"hidden"
            },

            {
              title: 'provision_content',
              key: 'provision_content',
              type:"hidden"
            },

            {
              title: 'validity_status',
              key: 'validity_status',
              type:"hidden"
            },
          ]
        },
        url: {
          add: "/om/order/add",
          edit: "/om/order/edit",
          queryById: "/om/order/queryById",
          orderDet: {
            list: '/om/order/queryOrderDetByMainId'
          },
          provision: {
            list: '/om/order/queryProvisionByMainId'
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
        this.orderDetTable.dataSource=[]
        this.provisionTable.dataSource=[]
      },
      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },
      /** 调用完edit()方法之后会自动调用此方法 */
      editAfter() {
        let fieldval = pick(this.model,'orderNo','orderType','desCity','productCode','orderWt','totalPrice','remark','customerId','customerText')
        this.$nextTick(() => {
          this.form.setFieldsValue(fieldval)
        })
        // 加载子表数据
        if (this.model.id) {
          let params = { id: this.model.id }
          this.requestSubTableData(this.url.orderDet.list, params, this.orderDetTable)
          this.requestSubTableData(this.url.provision.list, params, this.provisionTable)
        }
      },
      /** 整理成formData */
      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          orderDetList: allValues.tablesValue[0].values,
          provisionList: allValues.tablesValue[1].values,
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
       this.form.setFieldsValue(pick(row,'orderNo','orderType','desCity','productCode','orderWt','totalPrice','remark','customerId','customerText'))
     },

    }
  }
</script>

<style scoped>
</style>