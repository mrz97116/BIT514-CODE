<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <!-- 主表单区域 -->
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="12">
            <a-form-item label="客户名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag v-decorator="['customerId',validatorRules.customerId]"
                                   dict="cm_customer_profile,company_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="合同比例(%)" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['rate']" placeholder="请输入合同比例" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="合同份数" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['copies',validatorRules.copies]" placeholder="请输入合同份数"
                              style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="目的地" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['destination']" placeholder="请输入目的地"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="合同状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag v-decorator="['contractFlag',validatorRules.contractFlag]" :trigger-change="true" dictCode="contract_flag" placeholder="请输入合同状态"/>
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
      <a-tab-pane tab="合同明细表" :key="refKeys[0]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="contractDetTable.loading"
          :columns="contractDetTable.columns"
          :dataSource="contractDetTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          @valueChange="onDataChange"
          :actionButton="true"/>
      </a-tab-pane>
      <a-tab-pane tab="合同商务条款" :key="refKeys[1]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[1]"
          :loading="contractProvisionTable.loading"
          :columns="contractProvisionTable.columns"
          :dataSource="contractProvisionTable.dataSource"
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
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import JDictSelectTag from "@/components/dict/JDictSelectTag"

  export default {
    name: 'ContractForm',
    mixins: [JEditableTableMixin],
    components: {
      JFormContainer,
      JSearchSelectTag,
      JDictSelectTag
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
          customerId: {
            rules: [
              {required: true, message: '请选择顾客!'}
            ]
          },
          copies:{
            rules: [
              { required: true, message: '请输入合同份数!'},
            ],
            initialValue: '2'
          },
          contractFlag:{
            rules: [
              { required: true, message: '不能为空!'},
            ],
            initialValue: 'normal'
          },
        },
        refKeys: ['contractDet', 'contractProvision', ],
        tableKeys:['contractDet', 'contractProvision', ],
        activeKey: 'contractDet',
        // 合同明细表
        contractDetTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '品名中文',
              key: 'prodCname',
              type: FormTypes.popup,
              popupCode:"s_om_mat",
              orgFields:"prod_cname,prod_cname_other,sg_sign,prod_class_code,mat_kind,weight,mat_len,mat_width,mat_thick,old_prod_cname",
              destFields:"prodCname,prodCnameOther,sgSign,prodClassCode,matKind,pieceWeight,orderLen,orderWidth,orderThick,oldProdCname",
              width:"130px",
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
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
              title: '产品名称',
              key: 'oldProdCname',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '合同类型',
              key: 'contractType',
              type: FormTypes.select,
              dictCode: 'contract_type',
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              fontColour: '#ff0000'
            },
            {
              title: '牌号',
              key: 'sgSign',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '件数',
              key: 'qty',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '物料重量',
              key: 'weight',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              statistics: true
            },
            // {
            //   title: '剩余重量',
            //   key: 'residualWeight',
            //   type: FormTypes.inputNumber,
            //   width:"200px",
            //   placeholder: '请输入${title}',
            //   defaultValue: '',
            // },
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
              title: '总价',
              key: 'totalPrice',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
              disable: true
            },
            {
              title: '订货长度',
              key: 'orderLen',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
              disabled: true
            },
            {
              title: '订货宽度',
              key: 'orderWidth',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
              disabled: true
            },
            {
              title: '订货厚度',
              key: 'orderThick',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
              disabled: true
            },
            // {
            //   title: '预交件数',
            //   key: 'preQty',
            //   type: FormTypes.inputNumber,
            //   width:"200px",
            //   placeholder: '请输入${title}',
            //   defaultValue: '',
            //   disabled: true
            // },
            // {
            //   title: '预交重量',
            //   key: 'preWeight',
            //   type: FormTypes.inputNumber,
            //   width:"200px",
            //   placeholder: '请输入${title}',
            //   defaultValue: '',
            //   disabled: true
            // },
            {
              title: '已交件数',
              key: 'delivyQty',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true
            },
            {
              title: '已交重量',
              key: 'delivyWeight',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true
            },
            // {
            //   title: '合同明细号',
            //   key: 'contractListNo',
            //   type: FormTypes.input,
            //   width:"200px",
            //   placeholder: '请输入${title}',
            //   defaultValue: '',
            //   disabled: true
            // },
            {
              title: '标准',
              key: 'sgStd',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '物料种类',
              type: FormTypes.select,
              dictCode: 'mat_kind',
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
              title: '产品大类',
              key: 'prodClassCode',
              type: FormTypes.select,
              dictCode: 'prod_code',
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
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
              title: '计重方式',
              key: 'weightType',
              type: FormTypes.radio,
              dictCode: 'wt_method_code',
              options: [{ value: '0', text: '实重' }, { value: '1', text: '理重' }],
              width: '170px',
              placeholder: '请输入${title}',
              defaultValue: '0'
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
              title: '交货期',
              key: 'delivyDate',
              type: FormTypes.select,
              dictCode: 'delivy_date',
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '现货',
            },
            {
              title: '合同偏差量',
              key: 'contractDeviation',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '订货重量单件目标值',
              key: 'goodWeightPieceTarget',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true
            },
            {
              title: '订货重量单件最大值',
              key: 'goodWeightPieceMaximum',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true
            },
            {
              title: '订货重量单件最小值',
              key: 'goodWeightPieceMinimum',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true
            },
            {
              title: '订货最小厚度（直径）',
              key: 'goodMinimumThickness',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true
            },
            {
              title: '订货最大厚度（直径）',
              key: 'goodMaximumThickness',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true
            },
            {
              title: '订货最小宽度',
              key: 'goodMinimumWidth',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true
            },
            {
              title: '订货最大宽度',
              key: 'goodMaximumWidth',
              type: FormTypes.inputNumber,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true
            },
          ]
        },
        // 合同商务条款
        contractProvisionTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '条款内容',
              key: 'provisionContent',
              type: FormTypes.popup,
              popupCode: 'om_provision_base',
              orgFields: 'provision_type,provision_content,validity_status',
              destFields: 'provisionType,provisionContent,validityStatus',
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '条款类型',
              key: 'provisionType',
              type: FormTypes.sel_search,
              width:"200px",
              dictCode: 'provision_type',
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '生效标识',
              key: 'validityStatus',
              type: FormTypes.sel_search,
              width:"200px",
              dictCode: 'provision_status',
              placeholder: '请输入${title}',
              defaultValue: '',
            },
          ]
        },
        url: {
          add: "/om/contract/add",
          edit: "/om/contract/edit",
          queryById: "/om/contract/queryById",
          contractDet: {
            list: '/om/contract/queryContractDetByMainId'
          },
          contractProvision: {
            list: '/om/contract/queryContractProvisionByMainId'
          },
          queryWhichContractControl: 'cm/customerConfiguration/queryWhichContractControl'
        },
        tenantId: this.$ls.get("TENANT_ID")
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
      var that = this;
      //若为装车单合同控量，则不需要出现预交信息
      getAction(this.url.queryWhichContractControl,{tenantId: that.tenantId}).then((res) => {
          if (res.result[1]==='1') {
            that.contractDetTable.columns.forEach(item => {
              if (item.key === 'preQty' || item.key ==='preWeight') {
                item.type = FormTypes.hidden;
              }
            })
          }
        }
      )
    },
    methods: {
      addBefore(){
        this.form.resetFields()
        this.contractDetTable.dataSource=[]
        this.contractProvisionTable.dataSource=[]
      },
      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },
      /** 调用完edit()方法之后会自动调用此方法 */
      editAfter() {
        let fieldval = pick(this.model,'customerId','rate','copies','destination','contractFlag','remark')
        this.$nextTick(() => {
          this.form.setFieldsValue(fieldval)
        })
        // 加载子表数据
        if (this.model.id) {
          let params = { id: this.model.id }
          this.requestSubTableData(this.url.contractDet.list, params, this.contractDetTable)
          this.requestSubTableData(this.url.contractProvision.list, params, this.contractProvisionTable)
        }
      },
      /** 整理成formData */
      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          contractDetList: allValues.tablesValue[0].values,
          contractProvisionList: allValues.tablesValue[1].values,
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
       this.form.setFieldsValue(pick(row,'customerId','rate','copies','destination','contractFlag','remark'))
     },
      onDataChange(props) {
        // console.log(type,row,column,value,target);
        if (props.column.key === 'price' || props.column.key === 'weight') {
          let value = '0';
          if (props.row.price>0 && props.row.weight>0) {
            value = props.row.price * props.row.weight
          }
          this.$refs.contractDet.setValues([{
            rowKey: props.row.id,
            values: { // 在这里 values 中的 name 是你 columns 中配置的 key
              'totalPrice': value
            }
          }])
          this.$refs['contractDet'].recalcOneStatisticsColumn('weight');
        }
        if(props.column.key ==='contractType' && props.value ==='0'){
          for (var item of this.$refs.contractDet.columns){
            if (item.key === 'goodWeightPieceTarget'
              || item.key === 'goodWeightPieceMaximum'
              || item.key ==='goodWeightPieceMinimum'
              || item.key === 'goodMaximumThickness'
              || item.key === 'goodMinimumThickness'
              || item.key === 'goodMaximumWidth'
              || item.key === 'goodMinimumWidth') {
              item.disabled = true
              continue
            }
            item.disabled = false
          }
        }
        if(props.column.key ==='contractType' && props.value ==='1'){
          for (var item of this.$refs.contractDet.columns){
            if (item.key === 'orderLen' || item.key === 'orderWidth' || item.key ==='orderThick') {
              item.disabled = true
              continue
            }
            item.disabled = false
          }
        }
      },

    }
  }
</script>

<style scoped>
</style>