<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail" title="客户信息">
        <a-row>
          <a-col :span="6">
            <a-form-item label="仓库" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['stockId']" :trigger-change="true"
                                 dictCode="sm_stock,name,id" placeholder="请选择仓库"/>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="来源地" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['areaSource']" placeholder="请输入来源地"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="数量合计" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number disabled v-decorator="['qty']" placeholder="请输入数量合计" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="重量合计" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number disabled v-decorator="['weight']" placeholder="请输入重量合计" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="入库时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date disabled placeholder="请选择入库时间" v-decorator="['stockTime']" :trigger-change="true"
                      style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['remark']" placeholder="请输入备注"></a-input>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </j-form-container>
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="入库明细" :key="refKeys[0]" :forceRender="true" key="1">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="matInWarehouseWarrantTable.loading"
          :columns="matInWarehouseWarrantTable.columns"
          :dataSource="matInWarehouseWarrantTable.dataSource"
          :maxHeight="480"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true"
          :addShow="addShow"/>
      </a-tab-pane>
    </a-tabs>
  </a-spin>
</template>

<script>

import {JEditableTableMixin} from '@/mixins/JEditableTableMixin'
import {httpAction, getAction} from '@/api/manage'
import pick from 'lodash.pick'
import JFormContainer from '@/components/jeecg/JFormContainer'
import JDate from '@/components/jeecg/JDate'
import JDictSelectTag from "@/components/dict/JDictSelectTag"
import {FormTypes, getRefPromise} from '@/utils/JEditableTableUtil'
import JSearchSelectTag from '@/components/dict/JSearchSelectTag'

export default {
  name: 'WarehouseWarrantForm',
  mixins: [JEditableTableMixin],
  components: {
    JFormContainer,
    JDate,
    JDictSelectTag,
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
    addShow: {
      type: Boolean,
      default: false,
      required: false
    }
  },
  data() {
    return {
      labelCol: {
        xs: {span: 24},
        sm: {span: 6},
      },
      wrapperCol: {
        xs: {span: 24},
        sm: {span: 12},
      },
      validatorRules: {},
      refKeys: ['matInWarehouseWarrant'],
      tableKeys: ['matInWarehouseWarrant'],
      activeKey: 'matInWarehouseWarrant',
      // 销售单明细表
      matInWarehouseWarrantTable: {
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '品名中文别名',
            key: 'prodCnameOther',
            type: FormTypes.input,
            width: '180px',
            defaultValue: '',
            disabled: true
          },
          {
            title: '库位',
            key: 'stockLocation',
            type: FormTypes.input,
            width: '100px',
          },
          {
            title: '成本价',
            key: 'costPrice',
            type: FormTypes.input,
            width: '100px',
          },
          {
            title: '采购价',
            key: 'purchasePrice',
            type: FormTypes.input,
            width: '100px',
          },
          {
            title: '车号',
            key: 'carNo',
            type: FormTypes.input,
            width: '100px'
          },
          {
            title: '牌号',
            key: 'sgSign',
            type: FormTypes.input,
            width: '100px',
            defaultValue: '',
            disabled: true
          },
          {
            title: '规格',
            key: 'custMatSpecs',
            type: FormTypes.input,
            width: '100px',
            defaultValue: '',
            disabled: true
          },
          {
            title: '材料厚度',
            key: 'matThick',
            type: FormTypes.inputNumber,
            width: '100px',
            defaultValue: '0',
            disabled: true
          },
          {
            title: '材料宽度',
            key: 'matWidth',
            type: FormTypes.inputNumber,
            width: '100px',
            defaultValue: '0',
            disabled: true
          },
          {
            title: '材料长度',
            key: 'matLen',
            type: FormTypes.inputNumber,
            width: '100px',
            defaultValue: '0',
            disabled: true
          },
          {
            title: '材料号',
            key: 'matNo',
            type: FormTypes.input,
            width: '150px',
            defaultValue: '0',
            disabled: true
          },
          {
            title: '材料净重',
            key: 'matNetWt',
            type: FormTypes.inputNumber,
            width: '100px',
            defaultValue: '0',
            disabled: true
          },
          {
            title: '材料件数',
            key: 'matNum',
            type: FormTypes.inputNumber,
            width: '100px',
            defaultValue: '0',
            disabled: true
          },
        ]
      },
      url: {
        add: "/sm/warehouseWarrant/add",
        edit: "/sm/warehouseWarrant/editWarehouseWarrant",
        queryById: "/sm/warehouseWarrant/queryById",
        matInWarehouseWarrant: {
          list: '/sm/warehouseWarrant/queryMatByWarehouseWarrantId'
        },
      }
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
    this.showFlowData();
  },
  methods: {
    //渲染流程表单数据
    showFlowData() {
      if (this.formBpm === true) {
        let params = {id: this.formData.dataId};
        getAction(this.url.queryById, params).then((res) => {
          if (res.success) {
            this.edit(res.result);
          }
        });
      }
    },
    popupCallback(row) {
      this.form.setFieldsValue(pick(row, 'stockId', 'areaSource', 'qty', 'weight', 'stockTime', 'remark'))
    },
    /** 调用完edit()方法之后会自动调用此方法 */
    editAfter() {
      let fieldval = pick(this.model, 'stockId', 'areaSource', 'qty', 'weight', 'stockTime', 'remark')
      this.$nextTick(() => {
        this.form.setFieldsValue(fieldval)
      })
      // 加载子表数据
      if (this.model.id) {
        let params = {id: this.model.id}
        this.requestSubTableData(this.url.matInWarehouseWarrant.list, params, this.matInWarehouseWarrantTable)
      }
    },
    /** 整理成formData */
    classifyIntoFormData(allValues) {
      debugger
      let main = Object.assign(this.model, allValues.formValue)
      return {
        ...main, // 展开
        matList: allValues.tablesValue[0].values
      }
    },
    validateError(msg) {
      this.$message.error(msg)
    },
    getAllTable() {
      let values = this.tableKeys.map(key => getRefPromise(this, key))
      return Promise.all(values)
    },
  }
}
</script>