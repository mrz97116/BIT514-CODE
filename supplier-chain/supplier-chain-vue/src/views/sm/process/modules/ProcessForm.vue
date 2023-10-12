<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="8">
            <a-form-item label="原材料号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['rawMaterialNo']"
                       placeholder="无(原材料号为空)"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8" v-show="false">
            <a-form-item label="材料id" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['matId']"></a-input>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </j-form-container>
    <!-- 子表单区域 -->
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="加工明细表" :key="refKeys[0]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="processDetTable.loading"
          :columns="processDetTable.columns"
          :dataSource="processDetTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true"
          @valueChange="onDataChange"
        />
      </a-tab-pane>
    </a-tabs>
  </a-spin>
</template>

<script>

import pick from 'lodash.pick'
import { getAction } from '@/api/manage'
import { FormTypes, getRefPromise, VALIDATE_NO_PASSED, validateFormAndTables } from '@/utils/JEditableTableUtil'
import { JEditableTableMixin } from '@/mixins/JEditableTableMixin'
import { validateDuplicateValue } from '@/utils/util'
import JFormContainer from '@/components/jeecg/JFormContainer'
import JDictSelectTag from "@/components/dict/JDictSelectTag"
import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
import JAreaLinkage from '@comp/jeecg/JAreaLinkage'

  export default {
    name: 'ProcessForm',
    mixins: [JEditableTableMixin],
    components: {
      JFormContainer,
      JDictSelectTag,
      JSearchSelectTag,
      JAreaLinkage,
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
      },
      selectionRecord: {
        type: Object,
        default: function () {
          return {}
        },
        required: false
      }
    },
    data () {
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
        refKeys: ['processDetTable'],
        tableKeys:['processDetTable'],
        activeKey: 'processDetTable',
        // 加工明细表
        processDetTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '品名中文',
              key: 'prodCname',
              type: FormTypes.popup,
              popupCode:"sm_mat",
              orgFields:"prod_cname,prod_cname_other,prod_code,sg_sign,prod_class_code,mat_kind,mat_len,mat_width,mat_thick",
              destFields:"prodCname,prodCnameOther,prodCode,sgSign,prodClassCode,matKind,matLen,matWidth,matThick",
              initQueryParam:'',
              width:"180px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '品名中文别名',
              key: 'prodCnameOther',
              type: FormTypes.input,
              width:"180px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '材料净重',
              key: 'matNetWt',
              type: FormTypes.inputNumber,
              width:"180px",
              placeholder: '请输入${title}',
              defaultValue: '',
              statistics:true,
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '牌号',
              key: 'sgSign',
              type: FormTypes.input,
              width:"180px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '材料长度',
              key: 'matLen',
              type: FormTypes.inputNumber,
              width:"180px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '材料宽度',
              key: 'matWidth',
              type: FormTypes.inputNumber,
              width:"180px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '材料厚度',
              key: 'matThick',
              type: FormTypes.inputNumber,
              width:"180px",
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [
                { required: true, message: '请输入材料厚度!'},
                { required: false, message: '输入的值必须大于0!',pattern:/^(?!(0[0-9]{0,}$))[0-9]{1,}[.]{0,}[0-9]{0,}$/}
              ]
            },
            {
              title: '材料规格',
              key: 'custMatSpecs',
              type: FormTypes.input,
              width:"180px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '品名代码',
              key: 'prodCode',
              type: FormTypes.input,
              width:"180px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '产品大类',
              key: 'prodClassCode',
              type: FormTypes.input,
              width:"180px",
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '物料种类',
              key: 'matKind',
              type: FormTypes.select,
              dictCode:"mat_kind",
              width:"180px",
              placeholder: '请输入${title}',
              defaultValue: '',
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
              title: 'sg_sign',
              key: 'sg_sign',
              type:"hidden"
            },
            {
              title: 'mat_kind',
              key: 'mat_kind',
              type:"hidden"
            },
            {
              title: 'mat_len',
              key: 'mat_len',
              type:"hidden"
            },
            {
              title: 'mat_width',
              key: 'mat_width',
              type:"hidden"
            },
            {
              title: 'mat_thick',
              key: 'mat_thick',
              type:"hidden"
            }
          ]
        },
        url: {
          add: "/sm/process/add",
          edit: "/sm/process/edit",
          queryById: "/sm/process/queryById",
          processDet: {
            list: '/om/process/queryOrderDetByMainId'
          }
        }
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
        debugger
        this.form.resetFields()
        this.processDetTable.dataSource=[]
      },
      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },
      /** 调用完edit()方法之后会自动调用此方法 */
      editAfter() {
        this.form.setFieldsValue({rawMaterialNo: this.selectionRecord.rawMaterialNo}) //原材料号
        this.form.setFieldsValue({matId: this.selectionRecord.id})      //需要加工的材料的id
        let fieldval = pick(this.model,'rawMaterialNo')
        this.$nextTick(() => {
          this.form.setFieldsValue(fieldval)
        })
        // 加载子表数据
        // if (this.model.id) {
        //   let params = { id: this.model.id }
        //   this.requestSubTableData(this.url.orderDet.list, params, this.orderDetTable)
        // }
      },
      /** 整理成formData */
      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          matDetList: allValues.tablesValue[0].values
        }
      },
      /** 渲染流程表单数据 */
      showFlowData(){
        if(this.formBpm === true){
          let params = {id:this.formData.dataId};
          getAction(this.url.queryById,params).then((res)=>{
            if(res.success){
              this.edit (res.result);
            }
          });
        }
      },
      validateError(msg){
        this.$message.error(msg)
      },
      popupCallback(row){
        this.form.setFieldsValue(pick(row,'rawMaterialNo'))
      },
      onDataChange(props) {
        let custMatSpecs = 0
        if (props.column.key === 'matLen' || props.column.key === 'matWidth' || props.column.key === 'matThick') {
          if (props.row.matLen || props.row.matWidth || props.row.matThick) {
            if(props.row.matLen === '') props.row.matLen = 0
            if(props.row.matWidth === '') props.row.matWidth = 0
            if(props.row.matThick === '') props.row.matThick = 0
            custMatSpecs = props.row.matLen + '*' + props.row.matWidth + '*' + props.row.matThick
          }
        }
        this.$refs.processDetTable.setValues([{
          rowKey: props.row.id,
          values: { // 在这里 values 中的 name 是你 columns 中配置的 key
            'custMatSpecs': custMatSpecs
          }
        }])
      },
    }
  }
</script>