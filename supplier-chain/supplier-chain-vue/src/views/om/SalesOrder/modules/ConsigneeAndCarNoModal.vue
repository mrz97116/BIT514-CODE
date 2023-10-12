<template>
  <j-modal
    title="提单信息修改"
    width=70%
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel">
    <a-form :form="form" title="客户信息">
      <a-row>
        <a-col :span="12">
          <a-form-item  label="客户名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <j-search-select-tag :disabled='record.paymentFlag ==="paid" || record.createStackStatus ==="1"' :v-decorator="['customerId']" dict="cm_customer_profile where  del_flag=0,company_name,id" v-model="customerId"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="车号" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-input v-model="carNo" placeholder="请输入车号"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="仓库" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <j-search-select-tag :v-decorator="['stockId']" dict="sm_stock,name,id" v-model="stockId"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="目的地" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-input v-model="destination" placeholder="请输入目的地"/>
          </a-form-item>
        </a-col>
        <!-- addBy liujiazhi 2021.3.22-->
        <a-col v-has="'sm:salesMan_dictText'" :span="12">
          <a-form-item label="业务员" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <j-search-select-tag :v-decorator="['salesMan']" dict="cm_salesman_info,name,id" v-model="salesMan"/>
          </a-form-item>
        </a-col>

        <a-col :span="12">
          <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-input v-model="remark" placeholder="请输入备注"/>
          </a-form-item>
        </a-col>

        <a-col :span="12">
          <a-form-item label="款项描述" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-input v-model="fundDesc" placeholder="款项描述"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="车队信息" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <j-search-select-tag dict="sm_fleet,fleet_name,fleet_name" v-model="fleetName"/>

          </a-form-item>
        </a-col>

        <a-col :span="12">
          <a-form-item label="提货人" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-col :span="16">
              <a-input disabled v-model="consigneeName"
                       placeholder="选择提货人后自动填写"/>
            </a-col>
            <a-button @click="Open()" type="primary">添加提货人信息</a-button>
          </a-form-item>
        </a-col>

        <a-col :span="12">
          <a-form-item label="收料人" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-col :span="16">
              <a-input disabled v-model="receivingName"
                       placeholder="选择收料人后自动填写"/>
            </a-col>
            <a-button @click="Receiving()" type="primary">添加收料人信息</a-button>
          </a-form-item>
        </a-col>

        <a-col :span="12">
          <a-form-item label="身份证" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-input disabled v-model="idCard" placeholder="选择提货人后自动填写"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="收料地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-input disabled v-model="receivingAddress" placeholder="选择收料人后自动填写"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="手机号" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-input disabled v-model="phone" placeholder="选择提货人后自动填写"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="收料人手机号" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-input disabled v-model="receivingPhone" placeholder="选择收料人后自动填写"/>
          </a-form-item>
        </a-col>
        <a-col :span="12" v-if="createStackStatus === '1'">
          <a-form-item label="发货时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <j-date  :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择发货时间" class="query-group-cust"
                     v-model = "shipDate"/>
          </a-form-item>
        </a-col>
        <a-col :span="12" v-has="'sm:salesOrderTime'">
          <a-form-item label="制单时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择制单时间" class="query-group-cust"
                    v-model="orderTime"></j-date>
          </a-form-item>
        </a-col>

        <a-col :span="12" v-has="'sm:shipperName'">
          <a-form-item label="装货人" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-col :span="16">
              <a-input v-model="shipperName"
                       placeholder="选择装货人后自动填写"/>
            </a-col>
            <a-button @click="ShipperInfo()" type="primary">添加装货人信息</a-button>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="附件" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <j-upload :trigger-change="true" v-model="attach"></j-upload>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>

    <consignee-modal ref="consignee" @ok="submit" :disabled="disableSubmit"></consignee-modal>

    <receiving-modal ref="receiving" @ok="receiving" :disabled="disableSubmit"></receiving-modal>

    <div>
      <shipper-info-modal ref="shipperInfo" @ok="shipperInfo" :disabled="disableSubmit"></shipper-info-modal>
    </div>
    <!--zhuangj 2021.5.7   ===========================================================================================================-->

    <!-- 子表单区域 -->
    <a-tabs v-show="record.paymentFlag !== 'paid'" v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="提单明细表" :key="refKeys[0]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="salesOrderDetTable.loading"
          :columns="salesOrderDetTable.columns"
          :dataSource="salesOrderDetTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true"
          @valueChange="onDataChange"
          :addShow="addShow"/>
      </a-tab-pane>
    </a-tabs>



  </j-modal>
</template>

<script>

  import ConsigneeModal from './ConsigneelistModal'
  import { getAction, postAction } from '@api/manage'
  import ReceivingModal from './ReceivingList'
  import JDate from '@comp/jeecg/JDate'
  import JSearchSelectTag from '@comp/dict/JSearchSelectTag'
  import pick from 'lodash.pick'
  import { JEditableTableMixin } from '@/mixins/JEditableTableMixin'
  import JUpload from '@/components/jeecg/JUpload'
  import { FormTypes, getRefPromise,validateFormAndTables } from '@/utils/JEditableTableUtil'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import { math } from '@/views/utils/math.js'
  import ShipperInfoModal from "../ShipperInfoList";


  export default {
    name: 'ConsigneeAndCarNoModal',
    mixins: [JEditableTableMixin],
    components: {
      ShipperInfoModal,
      ConsigneeModal,
      ReceivingModal,
      JDate,
      JSearchSelectTag,
      JUpload
    },
    props: {
      record: {
        type: Object,
        default: function() {
          return {}
        }
      },
      editType: {
        type: Boolean,
        default: false,
        required: false
      },
      addShow: {
        type: Boolean,
        default: true,
        required: false
      }
    },
    data() {
      return {
        disableSubmit: false,
        visible: false,
        consigneeName: '',
        stockId: '',
        customerId: '',
        orderTime: '',
        idCard: '',
        phone: '',
        carNo: '',
        remark: '',
        fundDesc: '',
        receivingName: '',
        receivingPhone: '',
        receivingAddress: '',
        attach: '',
        shipDate: '',
        fleetName: '',
        createStackStatus: '',
        destination: '',
        salesMan: '',
        shipperName: '',
        labelCol: {
          xs: { span: 24 },
          sm: { span: 3 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 20 }
        },
        // addby liujiazhi 2021.3.22
        validatorRules: {
          salesMan: {
            rules: [
              { required: true, message: '请选择业务员!' }
            ]
          }
        },

        // 提单修改明细表======================================================================================
        //zhuangj 2021.5.7
        refKeys: ['salesOrderDet'],
        tableKeys: ['salesOrderDet'],
        activeKey: 'salesOrderDet',
        salesOrderDetTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '品名中文',
              key: 'prodCname',
              type: FormTypes.popup,
              popupCode: 's_om_mat',
              orgFields: 'mat_kind,prod_cname,prod_cname_other,old_prod_cname,sg_sign,mat_len,mat_width,mat_thick,prod_class_code,mat_no,piece_weight,available_qty,available_weight,stock_house_id,name,wt_mode,id',
              destFields: 'matKind,prodCname,prodCnameOther,oldProdCname,sgSign,orderLen,orderWidth,orderThick,prodClassCode,matNo,matTheoryWt,qty,weight,stockId,stockName,wtMode,inventoryId',
              width: '150px',
              placeholder: '请输入${title}',
              validateRules: [{ required: true, message: '${title}不能为空' }],
              defaultValue: '',
              initQueryParam: ''
            },
            {
              title: '合同明细号',
              key: 'contractListNo',
              type: FormTypes.popup,
              popupCode: 'om_contract',
              orgFields: 'contract_list_no',
              destFields: 'contractListNo',
              width: '150px',
              placeholder: '请输入${title}',
              defaultValue: '',
              initQueryParam: ''
            },
            {
              title: '品名中文别名',
              key: 'prodCnameOther',
              type: FormTypes.input,
              width: '150px',
              placeholder: '请输入${title}',
              validateRules: [{ required: true, message: '${title}不能为空' }],
              defaultValue: ''
            },
            {
              title: '产品名称',
              key: 'oldProdCname',
              type: FormTypes.input,
              width: '150px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '牌号',
              key: 'sgSign',
              type: FormTypes.input,
              width: '100px',
              placeholder: '请输入${title}',
              validateRules: [{ required: true, message: '${title}不能为空' }],
              defaultValue: ''
            },
            {
              title: '订货厚度',
              key: 'orderThick',
              type: FormTypes.inputNumber,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: '0',
              // validateRules: [
              //   { required: true, message: '请输入订货厚度!' },
              //   { required: false, message: '输入的值必须大于0!', pattern: /^(?!(0[0-9]{0,}$))[0-9]{1,}[.]{0,}[0-9]{0,}$/ }
              // ]
            },
            {
              title: '订货宽度',
              key: 'orderWidth',
              type: FormTypes.inputNumber,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: '0'
            },
            {
              title: '订货长度',
              key: 'orderLen',
              type: FormTypes.inputNumber,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: '0'
            },

            {
              title: '材料号',
              key: 'matNo',
              type: FormTypes.input,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },

            {
              title: '仓库名称',
              key: 'stockName',
              type: FormTypes.input,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: '0',
              disabled: true
            },
            {
              title: '数量',
              key: 'qty',
              type: FormTypes.inputNumber,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: '',
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
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true
            },
            {
              title: '重量',
              key: 'weight',
              type: FormTypes.inputNumber,
              width: '100px',
              rowSelection: true,
              placeholder: '请输入${title}',
              validateRules: [
                {
                  required: true,
                  message: '${title}不能为空',
                  handler(type, value, row, column, callback, target) {
                    let val = value + ''
                    if (val.indexOf('.') !== -1) {
                      let start = val.split('.')[0]
                      let end = val.split('.')[1]
                      if (end.length > 3) {
                        target.setValues([
                          {
                            rowKey: row.id,
                            values: {
                              'weight': start + '.' + end.substr(0, 3)
                            }
                          }
                        ])
                        return
                      }
                    }
                    callback(true)
                  }
                }
              ],
              defaultValue: '',
              statistics: true
            },
            {
              title: '理论重量',
              key: 'theoryWeight',
              type: FormTypes.inputNumber,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '基础单价',
              key: 'basicPrice',
              type: FormTypes.inputNumber,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: '0'
            },
            {
              title: '运费',
              key: 'deliverExpense',
              type: FormTypes.inputNumber,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: '0'
            },
            {
              title: '加价',
              key: 'addPrice',
              type: FormTypes.inputNumber,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '单价',
              key: 'price',
              type: FormTypes.inputNumber,
              width: '100px',
              placeholder: '请输入${title}',
              validateRules: [{ required: true, message: '${title}不能为空' }],
              defaultValue: ''
            },
            {
              title: '理论单价',
              key: 'theoryPrice',
              type: FormTypes.inputNumber,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '运费总价',
              key: 'deliverTotalPrice',
              type: FormTypes.inputNumber,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: '0',
              disabled: true,
              statistics: true
            },
            {
              title: '总价',
              key: 'totalPrice',
              type: FormTypes.inputNumber,
              width: '100px',
              placeholder: '请输入${title}',
              validateRules: [{ required: true, message: '${title}不能为空' }],
              defaultValue: '',
              disabled: true,
              statistics: true
            },
            {
              title: '车队名称',
              key: 'fleetName',
              type: FormTypes.sel_search,
              options: [],
              width: '150px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '车队运费',
              key: 'fleetDeliverExpense',
              type: FormTypes.inputNumber,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: '0'
            },
            {
              title: '车队总价',
              key: 'fleetTotalPrice',
              type: FormTypes.inputNumber,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '计重方式',
              key: 'wtMode',
              type: FormTypes.radio,
              dictCode: 'wt_method_code',
              options: [{ value: '0', text: '实重' }, { value: '1', text: '理重' }],
              width: '170px',
              placeholder: '请输入${title}',
              defaultValue: '1'
            },
            {
              title: '过磅重量',
              key: 'weightingWeight',
              type: FormTypes.inputNumber,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '过磅价格',
              key: 'weightingPrice',
              type: FormTypes.inputNumber,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },

            {
              title: '物料种类',
              key: 'matKind',
              type: FormTypes.select,
              dictCode: 'mat_kind',
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '产品大类',
              key: 'prodClassCode',
              type: FormTypes.select,
              dictCode: 'prod_code',
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }]

            },
            {
              title: '标准',
              key: 'sgStd',
              type: FormTypes.input,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '品名代码',
              key: 'prodCode',
              type: FormTypes.input,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '仓库',
              key: 'stockId',
              type: 'hidden',
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '库存id',
              key: 'inventoryId',
              type: "hidden",
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '单位',
              key: 'unit',
              type: "hidden",
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            }
          ]
        },
        url: {
          showSalesOrderDetList:'/om/salesOrder/showSalesOrderDetById',
          editSalesOrder: '/om/salesOrder/editSalesOrder',
          queryFleet: '/fleet/smFleet/fleetList',
          queryIfShowDeliverExpense: '/om/salesOrder/queryIfShowDeliverExpense',
          queryIfShowAddPrice: '/om/salesOrder/queryIfShowAddPrice',
          queryIfShowTheoryWeight: '/om/salesOrder/queryIfShowTheoryWeight',
          queryIfShowTheoryPrice: '/om/salesOrder/queryIfShowTheoryPrice',
          queryIfShowBasicPrice: '/om/salesOrder/queryIfShowBasicPrice',
          queryParamsFromBackEnd: '/om/salesOrder/queryParamsFromBackEnd'
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
      this.showFlowData()
      this.currentTime = new Date()
      this.form.setFieldsValue({shipDate:this.currentTime.toString()})
      var that = this
      getAction(this.url.queryFleet).then((res) => {
          if (res.success) {
            that.salesOrderDetTable.columns.forEach(item => {
              if (item.key === 'fleetName') {
                item.options = res.result;
              }
            })
          }
        }
      )
      getAction(this.url.queryParamsFromBackEnd).then((res) => {
        if (res.success) {
          that.salesOrderDetTable.columns.forEach(item => {
            for (var key in res.result) {
              // 是否显示基础单价
              if (key === 'basicPrice') {
                if (item.key === 'basicPrice' && !res.result[key]) {
                  item.type = FormTypes.hidden;
                }
                if (item.key === 'price' && res.result[key]) {
                  item.disabled.true
                }
              }
              //是否显示运费
              if (key === 'deliverExpense') {
                if ((item.key === 'deliverExpense' ||
                  item.key === 'deliverTotalPrice' ||
                  item.key === 'fleetName' ||
                  item.key === 'fleetDeliverExpense' ||
                  item.key === 'fleetTotalPrice') && !res.result[key]) {
                  item.type = FormTypes.hidden;
                }
                if (item.key === 'deliverTotalPrice' && res.result[key]) {
                  item.disabled = true
                }
              }
              //是否显示加价
              if (key === 'addPrice' && item.key === 'addPrice' && !res.result[key]) {
                item.type = FormTypes.hidden;
              }
              //是否显示理论重量
              if (key === 'theoryWeight' && item.key === 'theoryWeight' && !res.result[key]) {
                item.type = FormTypes.hidden;
              }
              //是否显示理论单价
              if (key === 'theoryPrice' && item.key === 'theoryPrice' && !res.result[key]) {
                item.type = FormTypes.hidden;
              }
              if(key === 'currentDate'){
                this.form.setFieldsValue({
                  orderTime: res.result[key]
                })
              }
            }
          })
        }
      })
    },
    watch: {
      record: {
        handler() {
          if(this.record.id!==undefined){
            this.consigneeName = this.record.consigneeName
            this.idCard = this.record.idCard
            this.stockId = this.record.stockId
            this.customerId = this.record.customerId
            this.orderTime = this.record.orderTime
            this.phone = this.record.phone
            this.carNo = this.record.carNo
            this.remark = this.record.remark
            this.fleetName = this.record.fleetName
            this.fundDesc = this.record.fundDesc
            this.receivingName = this.record.receivingName
            this.receivingPhone = this.record.receivingPhone
            this.receivingAddress = this.record.receivingAddress
            this.shipDate = this.record.shipDate
            this.createStackStatus = this.record.createStackStatus
            this.destination = this.record.destination
            this.salesMan = this.record.salesMan
            this.shipperName = this.record.shipperName
            this.attach = this.record.attach

            this.getSalesOrderDetList();
          }

        },
        immediate: true
      },
    },

    methods: {
      Open() {
        this.$refs['consignee'].hOpend()
      },
      Receiving() {
        this.$refs['receiving'].hOpend()
      },
      ShipperInfo(){
        this.$refs['shipperInfo'].hOpend()
      },
      shipperInfo(ref){
        this.shipperName = ref.shipperName
      },
      submit(row) {
        this.consigneeName = row.consigneeName
        this.idCard = row.idCard
        this.phone = row.phone
      },
      receiving(row) {
        this.receivingName = row.receivingName
        this.receivingPhone = row.phone
        this.receivingAddress = row.address
      },

      handleOk() {
        this.getAllTable().then(tables => {
          /** 一次性验证主表和所有的次表 */
          return validateFormAndTables(this.form, tables)
        }).then(allValues => {
          if (typeof this.classifyIntoFormData !== 'function') {
            throw this.throwNotFunction('classifyIntoFormData')
          }
          let formData = this.classifyIntoFormData(allValues)
          return this.editSalesOrder(formData)
        }).catch(e => {
          console.error(e)
        })
      },

      editSalesOrder(formData)  {
        let data = {
          id: this.record.id,
          consigneeName: this.consigneeName,
          stockId: this.stockId,
          customerId: this.customerId,
          orderTime: this.orderTime,
          idCard: this.idCard,
          phone: this.phone,
          carNo: this.carNo,
          remark: this.remark,
          receivingPhone: this.receivingPhone,
          receivingName: this.receivingName,
          receivingAddress: this.receivingAddress,
          shipDate : this.shipDate,
          destination: this.destination,
          salesMan: this.salesMan,
          paymentFlag:this.record.paymentFlag,
          fleetName: this.fleetName,
          fundDesc: this.fundDesc,
          salesOrderDetList:formData.salesOrderDetList,
          shipperName: this.shipperName,
          attach:this.attach
        }
        postAction(this.url.editSalesOrder,data).then((res) => {
          if (res.success) {
            this.$emit('ok')
            this.$message.success(res.message)
            this.visible = false
          }else{
            this.$message.warning(res.message)
            this.visible = true
          }
        })
      },
      /** 整理成formData */
      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          salesOrderDetList: allValues.tablesValue[0].values,
        }
      },
      //渲染流程表单数据
      showFlowData(){
        if(this.formBpm === true){
          let params = {id:this.formData.dataId};
          getAction(this.url.salesOrderDet,params).then((res)=>{
            if(res.success){
              this.edit (res.result);
            }
          })
        }
      },
      getSalesOrderDetList(){
        getAction(this.url.showSalesOrderDetList,{id:this.record.id}).then((res) => {
          if(res.success) {
            this.salesOrderDetTable.dataSource = res.result;
          }
        })
      },
      handleCancel() {
        this.$emit('close');
        this.visible = false
      },
      validateError(msg) {
        this.$message.error(msg)
      },
      popupCallback(row){
        this.form.setFieldsValue(pick(row,'customerId','customerName', 'trnpModeCode', 'destination', 'carNo', 'salesMan', 'consigneeName'
          , 'idCard', 'phone', 'stationCode', 'routeCode', 'remark', 'receivingName', 'receivingPhone', 'receivingAddress','shipDate','equipmentSuppliesFlag','shipperName'))
      },
      onDataChange(props) {
        // console.log(type,row,column,value,target);
        if (props.column.key === 'price' || props.column.key === 'weight'
          || props.column.key === 'qty' || props.column.key === 'deliverExpense'
          || props.column.key === 'basicPrice' || props.column.key === 'addPrice'
          || props.column.key === 'fleetDeliverExpense') {
          let value = 0
          let weight = props.row.weight || 0
          let dvalue = 0
          let price = 0
          let fleetTotalPrice = 0
          let theoryPrice = props.row.theoryPrice || 0
          let theoryWeight = props.row.theoryWeight || 0
          //先计算 总单价
          price = math.add(props.row.deliverExpense || 0, props.row.basicPrice || 0, props.row.addPrice || 0)
          // 修改总单价时取输入的单价值
          if (price == 0) {
            price = props.row.price
          }
          if (props.column.key === 'price') {
            price = props.row.price
          }
          //修改件数时 重量变化为 件数*单重
          if (props.column.key === 'qty') {
            weight = math.multiply(props.row.matTheoryWt || 0, props.row.qty)
            if (props.row.wtMode === '1') {
              theoryWeight = math.multiply(props.row.qty || 0, props.row.matTheoryWt || 0)
            }
          }
          //修改重量时取输入的重量值
          if (props.column.key === 'weight') {
            weight = props.row.weight
          }
          //计算总价
          value = math.multiply(price || 0, weight || 0)
          //修改运费时 计算运费总价 公式：运费*重量
          dvalue = math.multiply(props.row.deliverExpense || 0, weight || 0)
          //计算车队总价
          fleetTotalPrice = math.multiply(props.row.fleetDeliverExpense || 0, weight || 0)
          if (props.row.wtMode === '1') {
            //计算理论单价
            theoryPrice = math.divide(value, theoryWeight)
          }
          if (props.row.wtMode === '0') {
            theoryPrice = price
            theoryWeight = weight
          }
          //重新赋值
          this.$refs.salesOrderDet.setValues([{
            rowKey: props.row.id,
            values: { // 在这里 values 中的 name 是你 columns 中配置的 key
              'totalPrice': math.toFixed(value, 2),
              'weight': weight,
              'price': price,
              'deliverTotalPrice': math.toFixed(dvalue, 2),
              'fleetTotalPrice': math.toFixed(fleetTotalPrice, 2),
              'theoryPrice': math.toFixed(theoryPrice, 2),
              'theoryWeight': math.toFixed(theoryWeight, 3)
            }
          }])
          this.$refs['salesOrderDet'].recalcOneStatisticsColumn('qty')
          this.$refs['salesOrderDet'].recalcOneStatisticsColumn('weight')
          this.$refs['salesOrderDet'].recalcOneStatisticsColumn('totalPrice')
        }
        /**
         * queryData中的数据拼接顺序不可顺便修改，
         * 如修改需将JPopupOnlReport.vue文件中的initQueryParam监听属性一并修改
         */
        let queryData = props.row.prodCnameOther + '-' + props.row.oldProdCname + '-' + props.row.sgSign + '-' + props.row.orderLen + '-' + props.row.orderWidth
          + '-' + props.row.orderThick + '-' + props.row.prodClassCode + '-' + props.row.prodCname + '-' + this.customerPropId
        this.$refs.salesOrderDet.columns[1].initQueryParam = queryData + '-salesOrderContract'
      },

      //=========================================================================

    }
  }

</script>

<style scoped>

</style>