<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <!-- 主表单区域 -->
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="12" >
            <a-form-item label="客户名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag disabled v-decorator="['customerId']" dict="cm_customer_profile where  del_flag=0,company_name,id" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="助记码" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['alias' ]" placeholder="请输入助记码"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="仓储" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag disabled v-decorator="['stockNoClass']" dict="sm_stock  where del_flag=0,name,id" />
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="运输方式" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag disabled type="list" v-decorator="['trnpModeCode' ]" :trigger-change="true" dictCode="trnp_mode_code" placeholder="请选择运输方式编码"/>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="目的地" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['destination' ]" placeholder="请输入目的地"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="车号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['carNo']" placeholder="请输入车号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" v-show="false">
            <a-form-item label="船号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['shipNo']" placeholder="请输入船号"></a-input>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:salesMan_dictText'" :span="12" >
            <a-form-item label="业务员" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag disabled v-decorator="['salesMan']" dict="cm_salesman_info,name,id" placeholder="请选择业务员"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['remark' ]" placeholder="请输入备注"/>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="提货人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['consigneeName' ]" placeholder="选择提货人后自动填写"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="身份证" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['idCard' ]"  placeholder="选择提货人后自动填写"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="手机号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['phone' ]"  placeholder="选择提货人后自动填写"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" v-show="false">
            <a-form-item label="到站" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag disabled v-decorator="['stationCode']" :dict-options="[]" dict="bd_train_station,station_name,station_code" />
            </a-form-item>
          </a-col>
          <a-col :span="12" v-show="false">
            <a-form-item label="专用线" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag disabled v-decorator="['routeCode']" dict="bd_private_route_name,route_name,route_code" />
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <div style="margin-left: 200px">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-button disabled @click="Open()" type="primary">添加提货人信息</a-button>
              </a-form-item>
            </div>
          </a-col>
          <a-col :span="12">
            <a-form-item label="发货时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date :show-time="true" disabled date-format="YYYY-MM-DD" placeholder="请选择发货时间" class="query-group-cust"
                      v-decorator="['shipDate']"/>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </j-form-container>
    <div>
      <consignee-modal ref="consignee"  @ok="submit" :disabled="disableSubmit"></consignee-modal>
    </div>
      <!-- 子表单区域 -->
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="销售单明细表" :key="refKeys[0]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="stackDetTable.loading"
          :columns="stackDetTable.columns"
          :dataSource="stackDetTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :addShow="false"
          :actionButton="true"
          @valueChange="onDataChange"/>
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
  import ConsigneeModal from '../../../om/SalesOrder/modules/ConsigneelistModal'
  import JInput from "../../../../components/jeecg/JInput";
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import { filterDictTextByCache } from '@comp/dict/JDictSelectUtil'
  import { math } from '@/views/utils/math.js'
  import JDate from '@/components/jeecg/JDate.vue'

  export default {
    name: 'StackForm',
    mixins: [JEditableTableMixin],
    components: {
      JFormContainer,
      JDate,
      ConsigneeModal,
      JInput,
      JSearchSelectTag
    },
    data() {
      return {
        disableSubmit: false,
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
              { required: true, message: '请选择客户!' },
            ]
          },
          stockNoClass: {
            rules: [
              { required: true, message: '请选择仓储!' },
            ]
          },
          destination: {
            rules: [
              { required: true, message: '请输入目的地!' },
            ]
          },
          salesMan: {
            rules: [
              { required: true, message: '请选择业务员!' },
            ]
          },
          consigneeName: {
            rules: [
              { required: true, message: '请选择提货人!' },
            ]
          },
          status: {
            rules: [
              {required: true, message: '请选择状态！'}
            ]
          }
        },
        refKeys: ['stackDet', ],
        tableKeys:['stackDet', ],
        activeKey: 'stackDet',
        // 装车单明细表
        stackDetTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '品名中文',
              key: 'prodCname',
              type: FormTypes.popup,
              popupCode:"s_om_mat",
              destFields: 'matKind,prodCname,prodCnameOther,sgSign,orderLen,orderWidth,orderThick,prodClassCode,matNo',
              orgFields: 'mat_kind,prod_cname,prod_cname_other,sg_sign,mat_len,mat_width,mat_thick,prod_class_code,mat_no',
              width:"150px",
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true,
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '品名中文别名',
              key: 'prodCnameOther',
              type: FormTypes.input,
              width:"150px",
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true,
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '产品名称',
              key: 'oldProdCname',
              type: FormTypes.input,
              width: '150px',
              placeholder: '请输入${title}',
              disabled: true,
              defaultValue: ''
            },
            {
              title: '数量',
              key: 'qty',
              model: "qty",
              type: FormTypes.inputNumber,
              width:"100px",
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true,
              validateRules: [//此处进行验证
                {
                  required: true,//在前端设置此字段必填
                  message: '${title}不能为空',//在前端设置此字段不能为null，提示文本
                  // 自定义函数校验 handler,表单column验证
                  handler(type, value, row, column, callback, target) {
                    let val = value + ''
                    if (val.indexOf('.') !== -1) {
                      target.setValues([
                        {
                          rowKey: row.id, // 行的id
                          values: { // 在这里 values 中的 name 是你 columns 中配置的 key
                            'qty': parseInt(val)
                          }
                        }])
                      return
                    }
                    callback(true) // true = 通过验证
                  }
                }
              ],
              statistics: true
            },
            {
              title: '单重',
              key: 'matTheoryWt',
              type: FormTypes.inputNumber,
              width:"100px",
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true
            },
            {
              title: '重量',
              key: 'weight',
              type: FormTypes.inputNumber,
              width:"100px",
              rowSelection:true,
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true,
              statistics: true,
              validateRules: [
                {
                  required: true,
                  message: '${title}不能为空',
                  handler(type,value,row,column,callback,target){
                    let val = value + ''
                    if(val.indexOf('.') !== -1){
                      let start = val.split('.')[0]
                      let end = val.split('.')[1]
                      if(end.length >3){
                        target.setValues([
                          {
                            rowKey: row.id,
                            values:{
                              'weight': start + '.' + end.substr(0,3)
                            }
                          }
                        ])
                        return
                      }
                    }
                    callback(true)
                  }
                },
                // { required: false, message: '小数点后的数字不能大于四位', pattern: /^([0-9]+)[.]?([\d]{0,4})$/},

              ],
            },
            {
              title: '单价',
              key: 'price',
              type: FormTypes.inputNumber,
              width:"100px",
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true,
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '总价',
              key: 'totalAmount',
              type: FormTypes.inputNumber,
              width:"100px",
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true,
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '材料长度',
              key: 'matLen',
              type: FormTypes.inputNumber,
              width:"100px",
              placeholder: '请输入${title}',
              disabled: true,
              defaultValue: '',
            },
            {
              title: '材料宽度',
              key: 'matWidth',
              type: FormTypes.inputNumber,
              width:"100px",
              placeholder: '请输入${title}',
              disabled: true,
              defaultValue: '',
            },
            {
              title: '材料厚度',
              key: 'matThick',
              type: FormTypes.inputNumber,
              width:"100px",
              placeholder: '请输入${title}',
              disabled: true,
              defaultValue: '',
            },
            {
              title: '牌号',
              key: 'sgSign',
              type: FormTypes.input,
              width:"100px",
              placeholder: '请输入${title}',
              disabled: true,
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '计重方式',
              key: 'wtMode',
              type: FormTypes.radio,
              dictCode:"wt_method_code",
              options:[{value:"0",text:"实重"},{value:"1",text:"理重"}],
              width:"170px",
              placeholder: '请输入${title}',
              disabled: true,
              defaultValue: '0',
            },
            {
              title: '加价',
              key: 'addPrice',
              type: FormTypes.inputNumber,
              width:"100px",
              placeholder: '请输入${title}',
              disabled: true,
              defaultValue: '',
            },
            {
              title: '过磅重量',
              key: 'weightingWeight',
              type: FormTypes.inputNumber,
              width:"100px",
              placeholder: '请输入${title}',
              disabled: true,
              defaultValue: '',
            },
            {
              title: '过磅价格',
              key: 'weightingPrice',
              type: FormTypes.inputNumber,
              width:"100px",
              placeholder: '请输入${title}',
              disabled: true,
              defaultValue: '',
            },
            {
              title: '材料号',
              key: 'matNo',
              type: FormTypes.input,
              width:"100px",
              placeholder: '请输入${title}',
              disabled: true,
              defaultValue: '',
            },
            {
              title: '物料种类',
              key: 'matKind',
              type: FormTypes.input,
              width:"100px",
              placeholder: '请输入${title}',
              disabled: true,
              defaultValue: '',
            },
            {
              title: '产品大类',
              key: 'prodClassCode',
              type: FormTypes.input,
              width:"100px",
              placeholder: '请输入${title}',
              disabled: true,
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '标准',
              key: 'sgStd',
              type: FormTypes.input,
              width:"100px",
              disabled: true,
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '品名代码',
              key: 'prodCode',
              type: FormTypes.input,
              width:"100px",
              placeholder: '请输入${title}',
              disabled: true,
              defaultValue: '',
            },
            {
              title: '装车单明细id',
              key: 'salesOrderDetId',
              type: 'hidden',
              width:"100px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '优惠后的单价',
              key: 'discountPrice',
              type: FormTypes.inputNumber,
              width:"100px",
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true
            },
            {
              title: '优惠后的总价',
              key: 'discountTotalAmount',
              type: FormTypes.inputNumber,
              width:"100px",
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true
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
              title: 'mat_theory_wt',
              key: 'mat_theory_wt',
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
        url: {
          add: "/sm/stack/add",
          edit: "/sm/stack/edit",
          queryById: "/sm/stack/queryById",
          stackDet: {
            list: '/sm/stack/queryStackDetByMainId'
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
      Open() {
        this.$refs['consignee'].hOpend();
      },
      addBefore(){
        this.form.resetFields()
        this.stackDetTable.dataSource=[]
      },
      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },
      submit(row){
        console.log('row----',row)
        this.form.setFieldsValue(row);
      },
      /** 调用完edit()方法之后会自动调用此方法 */
      editAfter() {
        let fieldval = pick(this.model,'customerId','alias','stockNoClass','trnpModeCode','destination','carNo','shipNo'
          ,'salesMan','consigneeName','remark','idCard','phone','stationCode','routeCode','shipDate')
        this.$nextTick(() => {
          this.form.setFieldsValue(fieldval)
        })
        // 加载子表数据
        if (this.model.id) {
          let params = { id: this.model.id }
          this.requestSubTableData(this.url.stackDet.list, params, this.stackDetTable)
        }
      },
      /** 整理成formData */
      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          stackDetList: allValues.tablesValue[0].values,
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
       this.form.setFieldsValue(pick(row,'customerId','alias','stockNoClass','trnpModeCode','destination','carNo','shipNo'
         ,'salesMan','consigneeName','remark','idCard','phone','stationCode','routeCode'))
     },
      onDataChange(props) {
        // console.log(type,row,column,value,target);
        if (props.column.key == 'price' || props.column.key == 'weight' || props.column.key == 'qty') {
          let value = 0;
          let weight = props.row.weight ||0;
          if(props.row.matTheoryWt && props.row.qty){
            weight = math.multiply(props.row.matTheoryWt,props.row.qty)
          }
          if(props.column.key =='weight' || props.column.key == 'price'){
            weight = props.row.weight;
          }
          if (props.row.price > 0 && weight > 0) {
            value = math.multiply(props.row.price,weight)
          }
          this.$refs.stackDet.setValues([{
            rowKey: props.row.id,
            values: { // 在这里 values 中的 name 是你 columns 中配置的 key
              'totalAmount': value,
              'weight' : weight
            }
          }])
          this.$refs['stackDet'].recalcOneStatisticsColumn('qty');
          this.$refs['stackDet'].recalcOneStatisticsColumn('weight');
          this.$refs['salesOrderDet'].recalcOneStatisticsColumn('totalAmount');
        }
      }

    }
  }
</script>

<style scoped>
</style>