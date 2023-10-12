<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="12">
            <a-form-item label="提货点" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['pickupSpot',validatorRules.pickupSpot]" placeholder="请输入提货点"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="委托单位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['clientUnit',validatorRules.clientUnit]" placeholder="请输入委托单位"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="委托提货单位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['entrustedToPick']" placeholder="请输入委托提货单位"></a-input>
            </a-form-item>
          </a-col>
<!--          <a-col :span="12">-->
<!--            <a-form-item label="出发地" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--              <a-input v-decorator="['departureSpot']" placeholder="请输入出发地"></a-input>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
<!--          <a-col :span="12">-->
<!--            <a-form-item label="出发地地址" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--              <a-input v-decorator="['departureSpotAddress']" placeholder="请输入出发地地址"></a-input>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
<!--          <a-col :span="12">-->
<!--            <a-form-item label="出发地电话" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--              <a-input v-decorator="['departureSpotPhone']" placeholder="请输入出发地电话"></a-input>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
          <a-col :span="12">
            <a-form-item label="提货人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-col :span="16">
                <a-input disabled v-decorator="['consignee',validatorRules.consignee]" placeholder="选择提货人后自动填写"></a-input>
              </a-col>
              <a-button @click="Open()" type="primary">添加提货人信息</a-button>
            </a-form-item>

          </a-col>
          <a-col :span="12">
            <a-form-item label="提货人电话" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['consigneePhone',validatorRules.consigneePhone]" placeholder="选择提货人后自动填写"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="提货人身份证号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['consigneeIdentity',validatorRules.consigneeIdentity]"
                       placeholder="选择提货人后自动填写"></a-input>
            </a-form-item>
          </a-col>
<!--          <a-col :span="12">-->
<!--            <a-form-item label="运输方式" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--              <j-dict-select-tag type="list" v-decorator="['transportMode']" :trigger-change="true"-->
<!--                                 dictCode="trnp_mode_code" placeholder="请输入运输方式"/>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
<!--          <a-col :span="12">-->
<!--            <a-form-item label="船号" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--              <a-input v-decorator="['shipNo']" placeholder="请输入船号"></a-input>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
<!--          <a-col :span="12">-->
<!--            <a-form-item label="派船分单号" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--              <a-input v-decorator="['dispatchShipBillNo']" placeholder="请输入派船分单号"></a-input>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
          <a-col :span="12">
            <a-form-item label="委托日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择委托日期" v-decorator="['consignationDate']" :trigger-change="true"
                      style="width: 100%"/>
            </a-form-item>
          </a-col>
<!--          <a-col :span="12">-->
<!--            <a-form-item label="整车车号" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--              <a-input v-decorator="['wholeCarNo']" placeholder="请输入整车车号"></a-input>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
          <a-col v-if="showFlowSubmitButton" :span="24" style="text-align: center">
            <a-button @click="submitForm">提 交</a-button>
          </a-col>
        </a-row>
      </a-form>
    </j-form-container>
    <div>
      <consignee-modal ref="consignee" @ok="submit" :disabled="ture"></consignee-modal>
    </div>
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="材料明细表" :key="refKeys[0]" :forceRender="true" key="1">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="salesOrderDetTable.loading"
          :columns="salesOrderDetTable.columns"
          :dataSource="salesOrderDetTable.dataSource"
          :maxHeight="480"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true"
          />
      </a-tab-pane>
    </a-tabs>
  </a-spin>
</template>

<script>

import {httpAction, getAction} from '@/api/manage'
import pick from 'lodash.pick'
import JFormContainer from '@/components/jeecg/JFormContainer'
import JDate from '@/components/jeecg/JDate'
import JDictSelectTag from '@comp/dict/JDictSelectTag'
import {FormTypes, getRefPromise} from '@/utils/JEditableTableUtil'
import {JEditableTableMixin} from '@/mixins/JEditableTableMixin'
import ConsigneeModal from './ConsigneelistModal'

export default {
  name: 'GoodEntrustmentLetterForm',
  mixins: [JEditableTableMixin],
  components: {
    JFormContainer,
    JDate,
    JDictSelectTag,
    ConsigneeModal
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
    },
  },
  data() {
    return {
      labelCol: {
        xs: {span: 24},
        sm: {span: 5},
      },
      wrapperCol: {
        xs: {span: 24},
        sm: {span: 16},
      },
      confirmLoading: false,
      validatorRules: {
        pickupSpot: {
          rules: [
            {required: true, message: '请填写提货点!'}
          ]
        },
        clientUnit: {
          rules: [
            {required: true, message: '请填写委托单位!'}
          ]
        },
        consignee: {
          rules: [
            {required: true, message: '请填写提货人!'},
          ]
        },
        consigneePhone: {
          rules: [
            {required: true, message: '请填写提货人电话!'}
          ]
        },
        consigneeIdentity: {
          rules: [
            {required: true, message: '请填写提货人身份证!'}
          ]
        },
      },
      refKeys: ['salesOrderDet'],
      tableKeys: ['salesOrderDet'],
      activeKey: 'salesOrderDet',
      salesOrderDetTable: {
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '品名中文别名',
            key: 'prodCnameOther',
            type: FormTypes.popup,
            popupCode: 'good_entrustment_letter_det',
            orgFields: 'prod_cname_other,sg_sign,prod_class_code,mat_no,weight,order_len,order_width,order_thick,surface_treatment,plating_weight,stock_id,wt_mode',
            destFields: 'prodCnameOther,sgSign,prodClassCode,matNo,weight,orderLen,orderWidth,orderThick,surfaceTreatment,platingWeight,stockId,wtMode',
            width: '180px',
            placeholder: '请输入${title}',
            validateRules: [{required: true, message: '${title}不能为空'}],
            defaultValue: '',
            initQueryParam: ''
          },
          {
            title: '牌号',
            key: 'sgSign',
            type: FormTypes.input,
            width: '150px',
            placeholder: '请输入${title}',
            validateRules: [{required: true, message: '${title}不能为空'}],
            defaultValue: '',
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
            title: '产品大类',
            key: 'prodClassCode',
            type: FormTypes.select,
            dictCode: 'prod_code',
            width: '150px',
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '重量',
            key: 'weight',
            type: FormTypes.inputNumber,
            width: '100px',
            placeholder: '请输入${title}',
            defaultValue: '0',
          },
          {
            title: '材料长度',
            key: 'orderLen',
            type: FormTypes.inputNumber,
            width: '100px',
            placeholder: '请输入${title}',
            defaultValue: '0',
          },
          {
            title: '材料宽度',
            key: 'orderWidth',
            type: FormTypes.inputNumber,
            width: '100px',
            placeholder: '请输入${title}',
            defaultValue: '0',
          },
          {
            title: '材料厚度',
            key: 'orderThick',
            type: FormTypes.inputNumber,
            width: '100px',
            placeholder: '请输入${title}',
            defaultValue: '0',
          },
          {
            title: '钝化方式',
            key: 'surfaceTreatment',
            type: FormTypes.input,
            width: '100px',
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '锌层重量',
            key: 'platingWeight',
            type: FormTypes.input,
            width: '100px',
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '计重方式',
            key: 'wtMode',
            type: FormTypes.select,
            dictCode: 'wt_method_code',
            width: '100px',
            placeholder: '请输入${title}',
            defaultValue: '',
          }
        ]
      },
      url: {
        add: "/sm/goodEntrustmentLetter/addGoodEntrustmentLetter",
        edit: "/sm/goodEntrustmentLetter/editGoodEntrustmentLetter",
        queryById: "/sm/goodEntrustmentLetter/queryById",
        salesOrderDet: {
          list: '/sm/goodEntrustmentLetter/getDetails'
        },
      },
      tenantId: ''
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
    this.tenantId = this.$ls.get("TENANT_ID")
  },
  methods: {
    popupCallback(row) {
      this.form.setFieldsValue(pick(row, 'pickupSpot', 'clientUnit', 'entrustedToPick', 'departureSpot', 'departureSpotAddress', 'departureSpotPhone', 'consignee', 'consigneePhone', 'consigneeIdentity', 'transportMode', 'shipNo', 'dispatchShipBillNo', 'consignationDate', 'wholeCarNo'))
    },
    //渲染流程表单数据
    showFlowData() {
      if (this.formBpm === true) {
        let params = {id: this.formData.dataId}
        getAction(this.url.queryById, params).then((res) => {
          if (res.success) {
            this.edit(res.result)
          }
        })
      }
    },
    addBefore() {
      this.form.resetFields()
      this.salesOrderDetTable.dataSource = []
      this.$refs.salesOrderDet.columns[1].initQueryParam = ''
      this.selectedRowKeys = []
    },
    /** 调用完edit()方法之后会自动调用此方法 */
    editAfter() {
      debugger
      let fieldval = pick(this.model, 'pickupSpot', 'clientUnit', 'entrustedToPick', 'departureSpot', 'departureSpotAddress', 'departureSpotPhone', 'consignee', 'consigneePhone', 'consigneeIdentity', 'transportMode', 'shipNo', 'dispatchShipBillNo', 'consignationDate', 'wholeCarNo')
      this.$nextTick(() => {
        this.form.setFieldsValue(fieldval)
      })
      // 加载子表数据
      if (this.model.id) {
        let params = {id: this.model.id}
        this.requestSubTableData(this.url.salesOrderDet.list, params, this.salesOrderDetTable)
      }
    },
    getAllTable() {
      let values = this.tableKeys.map(key => getRefPromise(this, key))
      return Promise.all(values)
    },
    /** 整理成formData */
    classifyIntoFormData(allValues) {
      let main = Object.assign(this.model, allValues.formValue)
      return {
        ...main, // 展开
        salesOrderDetList: allValues.tablesValue[0].values
      }
    },
    Open() {
      this.$refs['consignee'].hOpend()
    },
    submit(row) {
      this.form.setFieldsValue({consignee: row.consigneeName, consigneeIdentity: row.idCard, consigneePhone: row.phone})
    }
  }
}
</script>