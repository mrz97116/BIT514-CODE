<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="3">
            <a-form-item label="仓库" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['stockHouseId',validatorRules.stockHouseId]"
                                 :trigger-change="true"
                                 dictCode="sm_stock,name,id" placeholder="请选择仓库"/>
            </a-form-item>
          </a-col>
          <a-col :span="3">
            <a-form-item label="库位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['stockLocation']" placeholder="请输入库位"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['remarks']" placeholder="请输入备注"></a-input>
            </a-form-item>
          </a-col>
          <!--          <a-col :span="12">-->
          <!--            <a-form-item label="库存类型" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
          <!--              <j-dict-select-tag type="list" v-decorator="['stockType']" :trigger-change="true" dictCode="stock_type"-->
          <!--                                 placeholder="请选择库存类型"/>-->
          <!--            </a-form-item>-->
          <!--          </a-col>-->
          <!--<a-col :span="12">-->
          <!--<a-form-item label="车号" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
          <!--<a-input placeholder="请输入车号" v-decorator="['carNo']"></a-input>-->
          <!--</a-form-item>-->
          <!--</a-col>-->
          <!--<a-col :span="12">-->
            <!--<a-form-item label="明细行数" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
              <!--<a-input-number placeholder="明细行数" v-decorator="['rowNum']" @change="change"></a-input-number>-->
            <!--</a-form-item>-->
          <!--</a-col>-->
          <a-col :span="6" v-has="'sm:dischargerName'">
            <a-form-item label="卸货人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-col :span="16">
                <a-input v-decorator="['dischargerName']"
                         placeholder="选择卸货人后自动填写"/>
              </a-col>
              <a-button @click="Discharger()" type="primary">添加卸货人信息</a-button>
            </a-form-item>
          </a-col>
          <a-col :span="6" v-has="'sm:stockTime'">
            <a-form-item label="入库时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择入库时间" class="query-group-cust"
                      v-decorator="['stockTime']" :useCurrentTime="true"/>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>

    </j-form-container>
    <div>
      <discharger-info-modal ref="discharger" @ok="discharger" :disabled="disableSubmit"></discharger-info-modal>
    </div>
    <!-- 子表单区域 -->
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="入库明细表" :key="refKeys[0]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="stockDetTable.loading"
          :columns="stockDetTable.columns"
          :dataSource="stockDetTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true"
          @valueChange="onDataChange"
          @input="onDataChange"/>
      </a-tab-pane>
    </a-tabs>
  </a-spin>
</template>

<script>
import JDate from '@comp/jeecg/JDate'
import pick from 'lodash.pick'
import {getAction} from '@/api/manage'
import {FormTypes, getRefPromise, VALIDATE_NO_PASSED, validateFormAndTables} from '@/utils/JEditableTableUtil'
import {JEditableTableMixin} from '@/mixins/JEditableTableMixin'
import {validateDuplicateValue} from '@/utils/util'
import JFormContainer from '@/components/jeecg/JFormContainer'
import JDictSelectTag from '@/components/dict/JDictSelectTag'
import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
import JAreaLinkage from '@comp/jeecg/JAreaLinkage'
import {math} from '@/views/utils/math.js'
import DischargerInfoModal from '../DischargerInfoList'
import Vue from 'vue'

export default {
  name: 'stockForm',
  mixins: [JEditableTableMixin],
  components: {
    JDate,
    JFormContainer,
    JDictSelectTag,
    JSearchSelectTag,
    JAreaLinkage,
    DischargerInfoModal,
    Vue
  },
  props: {
    //流程表单data
    formData: {
      type: Object,
      default: () => {
      },
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
  data() {
    return {
      disableSubmit: false,
      labelCol: {
        xs: {span: 24},
        sm: {span: 6},
      },
      wrapperCol: {
        xs: {span: 24},
        sm: {span: 16},
      },
      labelCol2: {
        xs: {span: 24},
        sm: {span: 3},
      },
      wrapperCol2: {
        xs: {span: 24},
        sm: {span: 20},
      },
      // 新增时子表默认添加几行空数据
      addDefaultRowNum: 1,
      validatorRules: {
        stockHouseId: {
          rules: [
            {required: true, message: '请选择仓库!'}
          ]
        }
      },
      refKeys: ['stockDetTable'],
      tableKeys: ['stockDetTable'],
      activeKey: 'stockDetTable',
      // 入库明细表
      stockDetTable: {
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '品名中文',
            key: 'prodCname',
            type: FormTypes.hidden,
            initQueryParam: '',
            width: '130px',
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{required: true, message: '${title}不能为空'}],
          },
          {
            title: '品名中文别名',
            key: 'prodCnameOther',
            type: FormTypes.popup,
            popupCode: 'om_order_mat',
            orgFields: 'prod_cname,prod_cname_other,sg_sign,prod_class_code,mat_kind,piece_weight,mat_len,mat_width,mat_thick,wt_method_code,piece_weight',
            destFields: 'prodCname,prodCnameOther,sgSign,prodClassCode,matKind,pieceWeight,matLen,matWidth,matThick,wtMode,pieceWeight',
            initQueryParam: '',
            width: '130px',
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{required: true, message: '${title}不能为空'}],
          },
          {
            title: '厚度',
            key: 'matThick',
            type: FormTypes.inputNumber,
            width: '70px',
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [
              {required: true, message: '请输入材料厚度!'},
              {required: false, message: '输入的值必须大于0!', pattern: /^(?!(0[0-9]{0,}$))[0-9]{1,}[.]{0,}[0-9]{0,}|0$/}
            ]
          },
          {
            title: '宽度',
            key: 'matWidth',
            type: FormTypes.inputNumber,
            width: '80px',
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '长度',
            key: 'matLen',
            type: FormTypes.inputNumber,
            width: '90px',
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '计重方式',
            key: 'wtMode',
            type: FormTypes.radio,
            dictCode: 'wt_method_code',
            options: [{value: '0', text: '实重'}, {value: '1', text: '理重'}],
            width: '170px',
            placeholder: '请输入${title}',
            defaultValue: '0'
          },
          {
            title: '材料规格',
            key: 'custMatSpecs',
            type: FormTypes.hidden,
            width: '130px',
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '件数',
            key: 'matNum',
            type: FormTypes.inputNumber,
            width: '70px',
            placeholder: '请输入${title}',
            defaultValue: '',
            statistics: true,
            fontColour: '#ff0000',
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
                          'matNum': parseInt(val)
                        }
                      }])
                    return
                  }
                  callback(true) // true = 通过验证
                }
              }
            ],
          },
          {
            title: '单位',
            key: 'unit',
            type: FormTypes.select,
            dictCode: 'unit',
            width: '100px',
            placeholder: '请输入${title}',
            defaultValue: '件',
            validateRules: [{required: true, message: '${title}不能为空'}]
          },
          {
            title: '单重',
            key: 'pieceWeight',
            type: FormTypes.inputNumber,
            width: '130px',
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [
              {
                require: true,
                message: '${title}不能为空',
                handler(type, value, row, column, callback, target) {
                  let val = value + ''
                  if (val.indexOf('.') !== -1) {
                    let head = val.split('.')[0]
                    let end = val.split('.')[1]
                    if (end.length > 3) {
                      target.setValues([
                        {
                          rowKey: row.id, // 行的id
                          values: { // 在这里 values 中的 name 是你 columns 中配置的 key
                            'pieceWeight': head + '.' + end.substr(0, 3)
                          }
                        }])
                      return
                    }
                  }
                  callback(true) // true = 通过验证
                }
              }
            ]
          },
          {
            title: '重量',
            key: 'matNetWt',
            type: FormTypes.inputNumber,
            width: '130px',
            placeholder: '请输入${title}',
            defaultValue: '',
            statistics: true,
            validateRules: [
              {
                require: true,
                message: '${title}不能为空',
                handler(type, value, row, column, callback, target) {
                  let val = value + ''
                  if (val.indexOf('.') !== -1) {
                    let head = val.split('.')[0]
                    let end = val.split('.')[1]
                    if (end.length > 3) {
                      target.setValues([
                        {
                          rowKey: row.id, // 行的id
                          values: { // 在这里 values 中的 name 是你 columns 中配置的 key
                            'matNetWt': head + '.' + end.substr(0, 3)
                          }
                        }])
                      return
                    }
                  }
                  callback(true) // true = 通过验证
                }
              }
            ]
          },
          {
            title: '柳钢出厂时刻',
            key: 'carLoadingTime',
            type: FormTypes.date,
            width: '130px',
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '车号',
            key: 'carNo',
            type: FormTypes.input,
            width: '130px',
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '牌号',
            key: 'sgSign',
            type: FormTypes.input,
            width: '130px',
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '材料磅差重量',
            key: 'matDiscrepWt',
            type: FormTypes.inputNumber,
            width: '130px',
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '采购价',
            key: 'purchasePrice',
            type: FormTypes.inputNumber,
            width: '130px',
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '成本价',
            key: 'costPrice',
            type: FormTypes.inputNumber,
            width: '130px',
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '产品大类',
            key: 'prodClassCode',
            type: FormTypes.hidden,
            dictCode: 'prod_code',
            width: '130px',
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{required: true, message: '${title}不能为空'}],
          },
          {
            title: '材料种类',
            key: 'matKind',
            type: FormTypes.hidden,
            dictCode: 'mat_kind',
            width: '130px',
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '材料号',
            key: 'matNo',
            type: FormTypes.input,
            width: '130px',
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '技术标准',
            key: 'technicalStandard',
            type: FormTypes.input,
            width: '130px',
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '块号',
            key: 'cakeNo',
            type: FormTypes.input,
            width: '130px',
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '钝化方式',
            key: 'surfaceTreatment',
            type: FormTypes.input,
            width: '130px',
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '锌层重量',
            key: 'platingWeight',
            type: FormTypes.input,
            width: '130px',
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: 'prod_cname',
            key: 'prod_cname',
            type: 'hidden'
          },
          {
            title: 'prod_cname_other',
            key: 'prod_cname_other',
            type: 'hidden'
          },
          {
            title: 'sg_sign',
            key: 'sg_sign',
            type: 'hidden'
          },
          {
            title: 'mat_kind',
            key: 'mat_kind',
            type: 'hidden'
          },
          {
            title: 'mat_len',
            key: 'mat_len',
            type: 'hidden'
          },
          {
            title: 'mat_width',
            key: 'mat_width',
            type: 'hidden'
          },
          {
            title: 'mat_thick',
            key: 'mat_thick',
            type: 'hidden'
          }
        ]
      },
      url: {
        add: '/sm/mat/addMat',
        // edit: '/sm/mat/edit',
        // queryById: '/sm/mat/queryById',
        // stockDet: {
        //   list: '/sm/mat/queryStockDetByMainId'
        // }
      },
      //租户id
      tenantId: Vue.ls.get("TENANT_ID")
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
  },
  methods: {
    addBefore() {
      this.form.resetFields()
      this.stockDetTable.dataSource = []
    },
    Discharger() {
      this.$refs['discharger'].hOpend()
    },
    discharger(ref) {
      // debugger
      this.form.setFieldsValue({dischargerName: ref.dischargerName})
    },
    getAllTable() {
      let values = this.tableKeys.map(key => getRefPromise(this, key))
      return Promise.all(values)
    },
    /** 调用完edit()方法之后会自动调用此方法 */
    editAfter() {
      let fieldval = pick(this.model, 'stockHouseId', 'stockLocation', 'stockType', 'carNo', 'remarks', 'dischargerName')
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
    validateError(msg) {
      this.$message.error(msg)
    },
    popupCallback(row) {
      this.form.setFieldsValue(pick(row, 'stockHouseId', 'stockLocation', 'stockType', 'carNo', 'remarks', 'dischargerName'))
    },
    onDataChange(props) {
      if (props.column.key === 'prodCname'
        || props.column.key === 'matLen'
        || props.column.key === 'matWidth'
        || props.column.key === 'matThick') {
        let pieceWeight = props.row.pieceWeight
        let custMatSpecs = 0
        let wt = props.row.matNetWt
        let density = 7.85
        let matNum = props.row.matNum
        if (props.row.matLen || props.row.matWidth || props.row.matThick) {
          if (props.row.matThick === '') props.row.matThick = 0
          if (props.row.matWidth === '') props.row.matWidth = 0
          if (props.row.matLen === '') props.row.matLen = 0
          custMatSpecs = props.row.matThick + '*' + props.row.matWidth + '*' + props.row.matLen
        }
        if (props.row.prodClassCode === 'F'
          && (this.tenantId === 5
            || this.tenantId === 6
            || this.tenantId === 7
            || this.tenantId === 8
            || this.tenantId === 13)) {
          debugger
          pieceWeight = math.toFixed(props.row.matLen * props.row.matWidth * props.row.matThick * density / 1000 / 1000 / 1000, 3)
          wt = math.toFixed(matNum * props.row.matLen * props.row.matWidth * props.row.matThick * density / 1000 / 1000 / 1000, 3)
        }

        this.$refs.stockDetTable.setValues([{
          rowKey: props.row.id,
          values: { // 在这里 values 中的 name 是你 columns 中配置的 key
            'custMatSpecs': custMatSpecs,
            'matNetWt': wt,
            'pieceWeight': pieceWeight
          }
        }])
      }
      if (props.column.key === 'matNum' || props.column.key === 'pieceWeight') {
        let wt = props.row.matNetWt
        if (props.row.pieceWeight && props.row.matNum) {
          wt = math.multiply(props.row.pieceWeight, props.row.matNum)
        }
        this.$refs.stockDetTable.setValues([{
          rowKey: props.row.id,
          values: { // 在这里 values 中的 name 是你 columns 中配置的 key
            'matNetWt': wt.toFixed(3)
          }
        }])
      }
      this.$refs['stockDetTable'].recalcOneStatisticsColumn('matNetWt')
      this.$refs['stockDetTable'].recalcOneStatisticsColumn('matNum')
    },
    change(e) {
      console.log(this.$refs.stockDetTable.rows)
      // this .$refs.stockDetTable.initialize();
      this.$refs.stockDetTable.add(e)
    }
  }
}
</script>