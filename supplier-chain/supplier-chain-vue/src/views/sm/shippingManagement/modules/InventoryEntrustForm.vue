<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="12">
            <a-form-item label="提货点" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag :disabled="editType" v-decorator="['pickupSpot',validatorRules.pickupSpot]"
                                   @change="selectChange"
                                   dict="sm_stock where del_flag=0,name,name"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="目的地" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag :disabled="editType" v-decorator="['departureSpot',validatorRules.departureSpot]"
                                   @change="selectChange"
                                   dict="sm_stock where del_flag=0,name,name"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="车队" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag :disabled="editType" v-decorator="['entrustedToPick',validatorRules.entrustedToPick]"
                                   @change="selectChange"
                                   dict="sm_fleet where del_flag=0,fleet_name,fleet_name"/>
            </a-form-item>
          </a-col>
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
          <a-col :span="12">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input style="type: text" :disabled="newEditType" v-decorator="['remark' ]" placeholder="请输入备注"/>
            </a-form-item>
          </a-col>
          <a-col v-if="showFlowSubmitButton" :span="24" style="text-align: center">
            <a-button @click="submitForm">提 交</a-button>
          </a-col>
        </a-row>
      </a-form>
    </j-form-container>
    <div>
      <consignee-modal ref="consignee" @ok="submit" :disabled="disableSubmit"></consignee-modal>
    </div>
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="物料明细表" :key="refKeys[0]" :forceRender="true" key="1">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="shippingManagementTable.loading"
          :columns="shippingManagementTable.columns"
          :dataSource="shippingManagementTable.dataSource"
          :maxHeight="480"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :addShow="false"
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
import ConsigneeModal from "../../goodEntrustmentLetter/modules/ConsigneelistModal";
import JSearchSelectTag from '@comp/dict/JSearchSelectTag'

export default {
  name: 'InventoryEntrustForm',
  mixins: [JEditableTableMixin],
  components: {
    JFormContainer,
    JDate,
    JDictSelectTag,
    ConsigneeModal,
    JSearchSelectTag
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
      validatorRules: {
        pickupSpot: {
          rules: [
            {required: true, message: '请选择仓库!'}
          ]
        },
        departureSpot: {
          rules: [
            {required: true, message: '请选择目的地!'}
          ]
        },
        entrustedToPick: {
          rules: [
            {required: true, message: '请选择车队!'}
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
      refKeys: ['shippingManagement'],
      tableKeys: ['shippingManagement'],
      activeKey: 'shippingManagement',
      shippingManagementTable: {
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '创建时间',
            key: 'createTime',
            type: FormTypes.date,
            width: '180px',
            placeholder: '请输入${title}',
            validateRules: [{required: true, message: '${title}不能为空'}],
            disabled:true,
            defaultValue: ''
          },
          {
            title: '出厂时间',
            key: 'deliveryTime',
            type: FormTypes.date,
            width: '150px',
            placeholder: '请输入${title}',
            disabled:true,
            defaultValue: ''
          },
          {
            title: '产品大类',
            key: 'prodClassCode',
            type: FormTypes.select,
            dictCode: 'prod_code',
            width: '150px',
            placeholder: '请输入${title}',
            disabled:true,
            defaultValue: ''
          },
          {
            title: '产品名',
            key: 'productName',
            type: FormTypes.input,
            width: '150px',
            placeholder: '请输入${title}',
            disabled:true,
            defaultValue: ''
          },
          {
            title: '物料状态',
            key: 'matStatus',
            type: FormTypes.select,
            dictCode: 'shipping_mat_status',
            width: '100px',
            placeholder: '请输入${title}',
            disabled:true,
            defaultValue: ''
          },
          {
            title: '规格',
            key: 'matSpecs',
            type: FormTypes.input,
            width: '100px',
            placeholder: '请输入${title}',
            disabled:true,
            defaultValue: ''
          },
          {
            title: '厚',
            key: 'matThick',
            type: FormTypes.inputNumber,
            width: '100px',
            placeholder: '请输入${title}',
            disabled:true,
            defaultValue: '0'
          },
          {
            title: '宽',
            key: 'matWidth',
            type: FormTypes.inputNumber,
            width: '100px',
            placeholder: '请输入${title}',
            disabled:true,
            defaultValue: '0'
          },
          {
            title: '长',
            key: 'matLen',
            type: FormTypes.inputNumber,
            width: '100px',
            placeholder: '请输入${title}',
            disabled:true,
            defaultValue: '0'
          },
          {
            title: '牌号',
            key: 'steelNo',
            type: FormTypes.input,
            width: '100px',
            placeholder: '请输入${title}',
            disabled:true,
            defaultValue: ''
          },
          {
            title: '尺寸',
            key: 'matSize',
            type: FormTypes.input,
            width: '100px',
            placeholder: '请输入${title}',
            disabled:true,
            defaultValue: ''
          },
          {
            title: '材料号',
            key: 'matNo',
            type: FormTypes.input,
            width: '100px',
            placeholder: '请输入${title}',
            disabled:true,
            defaultValue: ''
          },
          {
            title: '车牌号',
            key: 'carNo',
            type: FormTypes.input,
            width: '100px',
            placeholder: '请输入${title}',
            disabled:true,
            defaultValue: ''
          },
          {
            title: '数量',
            key: 'quantity',
            type: FormTypes.inputNumber,
            width: '100px',
            placeholder: '请输入${title}',
            disabled:true,
            defaultValue: '0'
          },
          {
            title: '重量',
            key: 'weight',
            type: FormTypes.inputNumber,
            width: '100px',
            placeholder: '请输入${title}',
            disabled:true,
            defaultValue: '0'
          },
          {
            title: '备注',
            key: 'remark',
            type: FormTypes.input,
            width: '100px',
            placeholder: '请输入${title}',
            disabled:true,
            defaultValue: ''
          },
        ]
      },
      url: {
        add: "/sm/goodEntrustmentLetter/addGoodEntrustmentLetter",
        edit: "/sm/goodEntrustmentLetter/addGoodEntrustmentLetter",
        queryById: "/sm/goodEntrustmentLetter/queryById",
        inventoryEntrustDet: {
          list: '/sm/goodEntrustmentLetter/getDetails'
        },
      },
      tenantId: '',
      selectedKeys: []
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
    this.tenantId = this.$ls.get("TENANT_ID")
  },
  methods: {
    popupCallback(row) {
      this.form.setFieldsValue(pick(row, 'pickupSpot', 'clientUnit', 'entrustedToPick', 'departureSpot', 'departureSpotAddress', 'departureSpotPhone', 'consignee', 'consigneePhone', 'consigneeIdentity', 'transportMode', 'shipNo', 'dispatchShipBillNo', 'consignationDate', 'wholeCarNo'))
    },
    //渲染流程表单数据
    addBefore() {
      this.form.resetFields()
      this.shippingManagementTable.dataSource = []
    },
    editAfter(){
      if (this.model.id) {
        let params = { id: this.model.id }
        this.requestSubTableData(this.url.inventoryEntrustDet.list, params, this.shippingManagementTable)
      }
    },
    showShippingManagement(shippingManagements){
      this.shippingManagementTable.dataSource = shippingManagements
    },
    getAllTable() {
      let values = this.tableKeys.map(key => getRefPromise(this, key))
      return Promise.all(values)
    },
    /** 整理成formData */
    classifyIntoFormData(allValues) {
      let main = Object.assign(this.model, allValues.formValue)
      let ids = [];
      this.shippingManagementTable.dataSource.forEach(item=>{
        ids.push(item.id);
      })
      main['shippingManagementIds'] = ids;
      return {
        ...main, // 展开
        shippingManagementList: allValues.tablesValue[0].values
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