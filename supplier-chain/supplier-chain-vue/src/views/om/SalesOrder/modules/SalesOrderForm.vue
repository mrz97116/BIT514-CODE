<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <!-- 主表单区域 -->
      <a-form :form="form" slot="detail" title="客户信息">
        <a-row>
          <a-col :span="8">
            <a-form-item label="客户名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag :disabled="editType" v-decorator="['customerId',validatorRules.customerId]"
                                   @change="selectChange"
                                   dict="cm_customer_profile where del_flag=0,company_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="提货人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-col :span="10">
                <a-input disabled v-decorator="['consigneeName']"
                         placeholder="选择提货人后自动填写"/>
              </a-col>
              <a-button @click="Open()" type="primary">添加提货人信息</a-button>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="收料人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-col :span="10">
                <a-input ref="receivingName" disabled v-decorator="['receivingName',validatorRules.receivingName]"
                         placeholder="选择收料人后自动填写"/>
              </a-col>
              <a-button @click="Receiving()" type="primary">添加收料人信息</a-button>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="运输方式" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag :disabled="editType" type="list"
                                 v-decorator="['trnpModeCode',validatorRules.trnpModeCode ]" :trigger-change="true"
                                 dictCode="trnp_mode_code" placeholder="请选择运输方式编码"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="身份证" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['idCard' ]" placeholder="选择提货人后自动填写"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="收料人手机号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['receivingPhone' ]" placeholder="选择收料人后自动填写"/>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:destination'" :span="8">
            <a-form-item label="目的地" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input :disabled="newEditType" v-decorator="['destination']" placeholder="请输入目的地"/>
            </a-form-item>
          </a-col>
          <a-col :span="8" v-show="false">
            <a-form-item label="物流园目的地" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag placeholder="请选择物流园目的地" :disabled="newEditType"
                                   v-decorator="['pushDestination']"
                                   :dictOptions="this.selectPushDestination"/>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:salesMan_dictText'" :span="8">
            <a-form-item label="业务员" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag :disabled="editType" v-decorator="['salesMan']" dict="cm_salesman_info,name,id"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="手机号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['phone' ]" placeholder="选择提货人后自动填写"/>
            </a-form-item>
          </a-col>

          <a-col :span="8">
            <a-form-item label="收料地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['receivingAddress' ]" placeholder="选择收料人后自动填写"/>
            </a-form-item>
          </a-col>

          <a-col v-has="'sm:equipmentMatFlag'" :span="8">
            <a-form-item label="设备物资标识" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list"
                                 v-decorator="['equipmentSuppliesFlag',validatorRules.equipmentSuppliesFlag ]"
                                 :trigger-change="true"
                                 dictCode="yes_no_status" placeholder="请选择设备物资标识"/>
            </a-form-item>
          </a-col>

          <a-col v-has="'sm:fleetName_dictText'" :span="8">
            <a-form-item label="车队名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag ref="fleetName" :disabled="editType" v-decorator="['fleetName']"
                                   dict="sm_fleet,fleet_name,fleet_name"/>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:fleetDeliverExpense_dictText'" :span="8">
            <a-form-item label="车队运费" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['fleetDeliverExpense' ]" placeholder="选择收料人后自动填写"/>
            </a-form-item>
          </a-col>

          <!--          <a-col v-has="'sm:pushDestination'" :span="8">-->
          <!--            <a-form-item label="物流园目的地" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
          <!--              <j-search-select-tag placeholder="请选择物流园目的地" :disabled="newEditType"-->
          <!--                                   v-decorator="['pushDestination']"-->
          <!--                                   :dictOptions="this.selectPushDestination"/>-->
          <!--            </a-form-item>-->
          <!--          </a-col>-->

          <!--          <a-col v-has="'sm:pushCarNo'" :span="8">-->
          <!--            <a-form-item label="物流园车号" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
          <!--              <j-search-select-tag placeholder="请选择物流园车号" :disabled="newEditType"-->
          <!--                                   v-decorator="['pushCarNo']"-->
          <!--                                   :dictOptions="this.selectPushCarNo"/>-->
          <!--            </a-form-item>-->
          <!--          </a-col>-->

          <a-col :span="8" v-show="show()">
            <a-form-item label="到站" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag :disabled="editType" v-decorator="['stationCode']"
                                   dict="bd_train_station,station_name,station_code"/>
            </a-form-item>
          </a-col>
          <a-col :span="8" v-show="show()">
            <a-form-item label="专用线" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag :disabled="editType" v-decorator="['routeCode']"
                                   dict="bd_private_route_name,route_name,route_code"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="车号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input :disabled="tenantId != '12' && editType" v-decorator="['carNo',validatorRules.carNo ]"
                       placeholder="请选择车号"/>
            </a-form-item>
          </a-col>
          <a-col :span="8" v-has="'sm:salesOrderTime'">
            <a-form-item label="制单时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择制单时间" class="query-group-cust"
                      v-decorator="['orderTime']"/>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:deliverExpense_dictText'" :span="8">
            <a-form-item label="运费" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['deliverExpense' ]" placeholder="选择收料人后自动填写"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input style="type: text" :disabled="newEditType" v-decorator="['remark' ]" placeholder="请输入备注"/>
            </a-form-item>
          </a-col>
          <a-col :span="8" v-if="editType">
            <a-form-item label="发货时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择发货时间" class="query-group-cust"
                      v-decorator="['shipDate']"/>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:failureCause'" :span="8" v-show="editType">
            <a-form-item label="推送失败原因" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-textarea v-decorator="['failureCause' ]"/>
            </a-form-item>
          </a-col>
          <a-col :span="8" v-has="'sm:shipperName'">
            <a-form-item label="装货人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-col :span="6">
                <a-input disabled v-decorator="['shipperName']"
                         placeholder="选择装货人后自动填写"/>
              </a-col>
              <a-button @click="ShipperInfoName()" type="primary">添加装货人信息</a-button>
            </a-form-item>
          </a-col>
          <a-col :span="8" v-has="'sm:closeTime'">
            <a-form-item label="截止日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择截止日期" class="query-group-cust"
                      v-decorator="['closeTime']" :useCurrentTime="true"/>
            </a-form-item>
          </a-col>
          <a-col :span="8" v-show="false">
            <a-form-item label="来款id" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['fundIds']"/>
            </a-form-item>
          </a-col>
          <a-col :span="8" v-show="false">
            <a-form-item label="预开单id" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prepareOrderId']"/>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </j-form-container>
    <div>
      <consignee-modal ref="consignee" @ok="submit" :disabled="disableSubmit"></consignee-modal>
    </div>
    <div>
      <receiving-modal ref="receiving" @ok="receiving" :disabled="disableSubmit"></receiving-modal>
    </div>
    <div>
      <ShipperInfo-modal ref="shipperInfo" @ok="shipperInfo" :disabled="disableSubmit"></ShipperInfo-modal>
    </div>
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 可用金额：<a
        style="font-weight: 600">{{ orderAvailableAmount }}</a>
        元，
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 可开单金额：<a
        style="font-weight: 600">{{ totalAvailableAmount }}</a>
        元，
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 授信可用金额：<a
        style="font-weight: 600">{{ creditAvailAbleAmount }}</a>
        元,
        <a
          style="font-weight: 600">{{ carNo }}</a>
      </div>
    </div>
    <a-button v-has="'sm:oneClickAcquire'" type="primary" icon="star" @click="oneClickAcquire"
              :disabled="editType">一键获取运费、车队运费和车队名称
    </a-button>
    <span v-if="tenantId == '12'" style="width: 100px">厚度：</span>
    <a-input v-if="tenantId == '12'" v-model="batchThick" style="width: 100px" type="primary"/>
    <span v-if="tenantId == '12'" style="width: 100px">价格：</span>
    <a-input v-if="tenantId == '12'" v-model="batchPrice" style="width: 100px" type="primary"/>
    <a-button v-if="tenantId == '12'" @click="priceCheck">确定</a-button>
    <!-- 子表单区域 -->
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="提单明细表" :key="refKeys[0]" :forceRender="true" key="1">
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
          @valueChange="onDataChange"
          :addShow="addShow"/>
      </a-tab-pane>
      <a-tab-pane tab="选款" key="2"
                  v-if="tenantId === '1' || tenantId === '12' || tenantId === '9' || tenantId==='10' || tenantId==='2'">
        <a-card :bordered="false">
          <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
              <a-row :gutter="24">

                <a-col :xl="6" :lg="7" :md="8" :sm="24">
                  <a-form-item label="来款方式">
                    <j-dict-select-tag placeholder="来款方式" v-model="salesOrderDetTable.queryParamPaymentMethod"
                                       dictCode="payment_method"></j-dict-select-tag>
                  </a-form-item>
                </a-col>

                <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="selectFundPool(customerPropId)" icon="search">查询</a-button>
            </span>
                </a-col>
              </a-row>
            </a-form>
          </div>


          <div>
            <a-table
              rowKey="id"
              bordered
              :columns="salesOrderDetTable.fundPoolColumns"
              :dataSource="salesOrderDetTable.fundPoolSource"
              :rowNumber="true"
              :actionButton="true"
              :scroll="{x:true}"
              :rowSelection="{selectedRowKeys: salesOrderDetTable.selectedStackRowKeys,onChange: onSelectChange}"
              class="j-matTable-force-nowrap">
              >


            </a-table>
          </div>
        </a-card>
        <!--        <OptionFundPoolModal ref="OptionFundPoolModal"/>-->


      </a-tab-pane>
    </a-tabs>
  </a-spin>
</template>

<script>

  import pick from 'lodash.pick'
  import { getAction } from '@api/manage'
  import { FormTypes, getRefPromise } from '@/utils/JEditableTableUtil'
  import { JEditableTableMixin } from '@/mixins/JEditableTableMixin'
  import JFormContainer from '@comp/jeecg/JFormContainer'
  import JDictSelectTag from '@comp/dict/JDictSelectTag'
  import JSearchSelectTag from '@comp/dict/JSearchSelectTag'
  import ConsigneeModal from './ConsigneelistModal'
  import JInput from '../../../../components/jeecg/JInput'
  import ReceivingModal from './ReceivingList'
  import { math } from '@/views/utils/math.js'
  import { filterDictTextByCache } from '@/components/dict/JDictSelectUtil'
  import JDate from '@comp/jeecg/JDate'
  import ShipperInfoModal from './../ShipperInfoList'

  export default {
    name: 'SalesOrderForm',
    mixins: [JEditableTableMixin],
    components: {
      JDate,
      JInput,
      JFormContainer,
      JDictSelectTag,
      JSearchSelectTag,
      ConsigneeModal,
      ReceivingModal,
      ShipperInfoModal
    },
    data() {
      return {
        currentTime: null,
        disableSubmit: false,
        selectValue1: 0,
        totalAvailableAmount: '0',
        creditAvailAbleAmount: '0',
        orderAvailableAmount: '0',
        carNo: '',
        customerPropId: '',
        tenantId: '',
        selectPushDestination: [],
        selectPushCarNo: [],
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },
        labelCol2: {
          xs: { span: 24 },
          sm: { span: 3 }
        },
        wrapperCol2: {
          xs: { span: 24 },
          sm: { span: 20 }
        },
        // getPushLogistics: [],
        // 新增时子表默认添加几行空数据
        addDefaultRowNum: 1,
        validatorRules: {
          customerId: {
            rules: [
              { required: true, message: '请选择客户!' }
            ]
          },
          stockHouseId: {
            rules: [
              { required: true, message: '请选择仓库!' }
            ]
          },
          stockId: {
            rules: [
              { required: true, message: '请选择仓库!' }
            ]
          },
          trnpModeCode: {
            initialValue: 'automobile'
          },
          equipmentSuppliesFlag: {
            initialValue: '0'
          },
          consigneeName: {
            rules: [
              { required: false, message: '请选择提货人!' }
            ]
          },
          receivingName: {
            rules: [
              { required: false, message: '请输入收料人姓名!' }
            ]
          },
          receivingName: {
            rules: [
              { required: false, message: '请输入收料人姓名!' }
            ]
          },
          carNo: {
            rules: [
              { required: false, message: '请输入车号!' }
            ]
          },
          salesMan: {
            rules: [
              { required: true, message: '请选择业务员!' }
            ]
          },
          fleetName: {
            rules: [
              { required: true, message: '请选择车队名称!' }
            ]
          },
          pushLogistics: {
            initialValue: 'logistics'
          }
        },
        refKeys: ['salesOrderDet'],
        tableKeys: ['salesOrderDet'],
        activeKey: 'salesOrderDet',
        fundPoolIds: '',
        batchPrice: 0,
        batchThick: 0,
        pushLogistics: {
          initialValue: 'logistics'
        },
        // 销售单明细表
        salesOrderDetTable: {
          loading: false,
          dataSource: [],
          fundPoolSource: [],
          selectedStackRowKeys: [],
          queryParamPaymentMethod: '',
          selectionFundPoolDevelopment: false,
          columns: [
            {
              title: '品名中文',
              key: 'prodCname',
              type: FormTypes.hidden,
              width: '180px',
              placeholder: '请输入${title}',
              validateRules: [{ required: true, message: '${title}不能为空' }],
              defaultValue: '',
              initQueryParam: ''
            },
            {
              title: '合同明细号',
              key: 'contractListNo',
              type: FormTypes.hidden,
              popupCode: 'om_contract',
              orgFields: 'contract_list_no',
              destFields: 'contractListNo',
              width: '180px',
              placeholder: '请输入${title}',
              defaultValue: '',
              initQueryParam: ''
            },
            {
              title: '品名中文别名',
              key: 'prodCnameOther',
              width: '180px',
              placeholder: '请输入${title}',
              type: FormTypes.hidden,
              defaultValue: ''
            },
            {
              title: '产品名称',
              key: 'oldProdCname',
              type: FormTypes.popup,
              popupCode: 's_om_mat',
              orgFields: 'mat_kind,prod_cname,old_prod_cname,prod_cname_other,sg_sign,cust_mat_specs,mat_len,mat_width,mat_thick,prod_class_code,mat_no,piece_weight,available_qty,available_weight,stock_house_id,name,wt_mode,id,unit,surface_treatment,plating_weight,mat_thick,mat_width,mat_len',
              destFields: 'matKind,prodCname,oldProdCname,prodCnameOther,sgSign,custMatSpecs,orderLen,orderWidth,orderThick,prodClassCode,matNo,matTheoryWt,qty,weight,stockId,stockName,wtMode,inventoryId,unit,surfaceTreatment,platingWeight,orderThick,orderWidth,orderLen',
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
              title: '规格',
              key: 'custMatSpecs',
              type: FormTypes.hidden,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: '',
              disabled: true
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
              width: '150px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '数量',
              key: 'qty',
              type: FormTypes.inputNumber,
              width: '75px',
              placeholder: '请输入${title}',
              defaultValue: '',
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
            },
            {
              title: '仓库名称',
              key: 'stockName',
              type: FormTypes.input,
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
              defaultValue: '0'
            },
            {
              title: '单价',
              key: 'price',
              type: FormTypes.inputNumber,
              width: '100px',
              placeholder: '请输入${title}',
              validateRules: [{ required: true, message: '${title}不能为空' }],
              defaultValue: '0'
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
              defaultValue: '0',
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
              type: 'hidden',
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '过磅价格',
              key: 'weightingPrice',
              type: 'hidden',
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '物料种类',
              key: 'matKind',
              type: 'hidden',
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
              type: 'hidden',
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
              type: 'hidden',
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '单位',
              key: 'unit',
              type: 'hidden',
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '锌层重量',
              key: 'platingWeight',
              type: FormTypes.input,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            },
            {
              title: '钝化方式',
              key: 'surfaceTreatment',
              type: FormTypes.input,
              width: '100px',
              placeholder: '请输入${title}',
              defaultValue: ''
            }
          ],
          fundPoolColumns: [
            {
              title: '来款方式',
              align: 'center',
              dataIndex: 'paymentMethod',
              customRender: function (text) {
                return filterDictTextByCache('payment_method', text)
              }
            },
            {
              title: '来款金额',
              align: 'center',
              dataIndex: 'incomingAmount'
            },
            {
              title: '可用金额',
              align: 'center',
              dataIndex: 'availAmount'
            },
            {
              title: '汇票到期天数',
              align: 'center',
              dataIndex: 'gapDays'
            },
            {
              title: '期限',
              align: 'center',
              dataIndex: 'term'
            },
            {
              title: '承兑银行',
              align: 'center',
              dataIndex: 'acceptanceBankName'
            },
            {
              title: '承兑汇票号',
              align: 'center',
              dataIndex: 'acceptanceTicketNo'
            },
            {
              title: '来款日期',
              align: 'center',
              dataIndex: 'incomingDate'
            },
            {
              title: '收据编号',
              align: 'center',
              dataIndex: 'receiptCode'
            },
          ]
        },
        url: {
          add: '/om/salesOrder/add',
          edit: '/om/salesOrder/edit',
          queryById: '/om/salesOrder/queryById',
          salesOrderDet: {
            list: '/om/salesOrder/querySalesOrderDetByMainId'
          },
          selectAvailableAmount: '/fm/fundAccount/selectAvailableAmount',
          selectCustomerByAlias: '/cm/customerProfile/selectCustomerByAlias',
          selectCanLadingBill: '/cm/customerProfile/selectCanLadingBill',
          queryFleet: '/fleet/smFleet/fleetList',
          checkCarNo: '/om/salesOrder/checkCarNo',
          selectFundPool: '/fm/fundPool/selectFundPool',
          selectPushDestination: '/om/salesOrder/selectPushDestination',
          selectPushCarNo: '/om/salesOrder/selectPushCarNo',
          queryParamsFromBackEnd: '/om/salesOrder/queryParamsFromBackEnd',
          querySalesOrderDetByPrepareId: '/sm/prepareOrder/querySalesOrderDetByPrepareId'

          // getPushLogistics: '/om/salesOrder/getPushLogistics'
        },
        receivingDetail: {}
      }
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
      },
      editType: {
        type: Boolean,
        default: false,
        required: false
      },
      addShow: {
        type: Boolean,
        default: false,
        required: false
      },
      newEditType: {
        type: Boolean,
        default: false,
        required: false
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
      this.msgValidator()
      this.currentTime = new Date()
      // this.form.setFieldsValue({shipDate: this.currentTime.toString()})
      // this.form.setFieldsValue({shipDate: this.orderTime.toString()})
      var that = this
      getAction(this.url.queryParamsFromBackEnd).then((res) => {
        if (res.success) {
          for (var key in res.result) {
            that.salesOrderDetTable.columns.forEach(item => {
              // 是否显示基础单价
              if (key === 'basicPrice') {
                if (item.key === 'basicPrice' && !res.result[key]) {
                  item.type = FormTypes.hidden
                }
                if (item.key === 'price' && res.result[key]) {
                  item.disabled = true
                }
              }
              //是否显示运费
              if (key === 'deliverExpense') {
                if ((item.key === 'deliverExpense' ||
                  item.key === 'deliverTotalPrice' ||
                  item.key === 'fleetName' ||
                  item.key === 'fleetDeliverExpense' ||
                  item.key === 'fleetTotalPrice') && !res.result[key]) {
                  item.type = FormTypes.hidden
                }
                if (item.key === 'deliverTotalPrice' && res.result[key]) {
                  item.disabled = true
                }
              }
              //是否显示加价
              if (key === 'addPrice' && item.key === 'addPrice' && !res.result[key]) {
                item.type = FormTypes.hidden
              }
              //是否显示理论重量
              if (key === 'theoryWeight' && item.key === 'theoryWeight' && !res.result[key]) {
                item.type = FormTypes.hidden
              }
              //是否显示理论单价
              if (key === 'theoryPrice' && item.key === 'theoryPrice' && !res.result[key]) {
                item.type = FormTypes.hidden
              }
            })
          }
        }
      })
      getAction(this.url.queryFleet).then((res) => {
          if (res.success) {
            that.salesOrderDetTable.columns.forEach(item => {
              if (item.key === 'fleetName') {
                item.options = res.result
              }
            })
          }
        }
      )
      this.tenantId = this.$ls.get('TENANT_ID')
      /**推送物流园暂时未使用代码
       // this.SelectPushDestination()
       // this.SelectPushCarNo()
       // this.GetPushLogistics()
       */
    },
    methods: {
      Open() {
        this.$refs['consignee'].hOpend()
      },
      Receiving() {
        this.$refs['receiving'].hOpend()
      },
      ShipperInfoName() {
        this.$refs['shipperInfo'].hOpend()
      },
      addBefore() {
        this.form.resetFields()
        this.salesOrderDetTable.dataSource = []
        this.salesOrderDetTable.fundPoolSource = []
        this.$refs.salesOrderDet.columns[1].initQueryParam = ''
        this.selectedRowKeys = []
        this.salesOrderDetTable.selectedStackRowKeys = []
        this.fundPoolIds = ''
      },
      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },
      submit(row) {
        this.form.setFieldsValue({ consigneeName: row.consigneeName, idCard: row.idCard, phone: row.phone })
      },
      receiving(rec) {
        this.form.setFieldsValue({
          receivingName: rec.receivingName,
          receivingPhone: rec.phone,
          receivingAddress: rec.address,
          fleetDeliverExpense: rec.fleetDeliverExpense,
          deliverExpense: rec.deliverExpense
        })
        this.receivingDetail = rec
      },
      shipperInfo(ref) {
        this.form.setFieldsValue({
          shipperName: ref.shipperName,
        })
      },
      show() {
        return this.form.getFieldValue('trnpModeCode') === 'train'
      },
      /** 调用完edit()方法之后会自动调用此方法 */
      editAfter() {
        let fieldval = pick(this.model, 'customerId', 'customerName', 'trnpModeCode', 'destination', 'carNo', 'salesMan', 'consigneeName'
          , 'idCard', 'phone', 'stationCode', 'routeCode', 'remark', 'receivingName', 'receivingPhone', 'receivingAddress', 'shipDate', 'equipmentSuppliesFlag', 'shipperName', 'orderTime', 'closeTime', 'fleetName', 'fundIds', 'pushDestination', 'pushCarNo', 'logisticsDestination', 'failureCause')
        this.$nextTick(() => {
          this.form.setFieldsValue(fieldval)
        })
        // 加载子表数据
        if (this.model.id) {
          let params = { id: this.model.id }
          this.requestSubTableData(this.url.salesOrderDet.list, params, this.salesOrderDetTable)
        }
      },
      //跟随顾客变化可用余额、可用授信
      selectChange(customerId) {
        if (customerId) {
          var that = this
          //阳蕊明选择客户后判断开单状态
          if (that.tenantId + '' === '5' || that.tenantId + '' === '6' || that.tenantId + '' === '7' || that.tenantId + '' === '8' || that.tenantId + '' === '13') {
            getAction(this.url.selectCanLadingBill, { id: customerId }).then((res) => {
              if (res.success) {

              } else {
                that.$message.warning(res.message)
              }
            })
          }
          getAction(this.url.selectAvailableAmount, { id: customerId }).then((res) => {
            if (res.message) {
              that.totalAvailableAmount = res.result.totalAvailableAmount
              that.creditAvailAbleAmount = res.result.creditAvailAbleAmount
              that.orderAvailableAmount = res.result.orderAvailableAmount
              // cmId = res.result.creditAccount.customerId
              // that.form.setFieldsValue({ alias: res.result.alias })
            } else {
              that.$message.warning(res.message)
            }
          })
          this.$refs.salesOrderDet.columns[1].initQueryParam = customerId + '-customerId'
          //记录当前主表客户id
          this.customerPropId = customerId
          this.selectedRowKeys = []
          this.salesOrderDetTable.selectedStackRowKeys = []
          this.selectFundPool(customerId)
        }
      },
      selectStatusChange(val) {
        this.$refs.salesOrderDet.columns[0].initQueryParam = val + '-salesOrderStatus'
      },
      cobyAddAfter() {
        let fieldval = pick(this.model, 'customerId', 'customerName', 'trnpModeCode', 'destination', 'carNo', 'salesMan', 'consigneeName'
          , 'idCard', 'phone', 'stationCode', 'routeCode', 'remark', 'receivingName', 'receivingPhone', 'receivingAddress', 'equipmentSuppliesFlag', 'shipperName', 'orderTime', 'closeTime', 'fleetName', 'pushDestination', 'pushCarNo', 'pushLogistics', 'logisticsDestination', 'failureCause', 'prepareOrderId')
        this.$nextTick(() => {
          this.form.setFieldsValue(fieldval)
        })
        // 加载子表数据
        if (this.model.id) {
          let params = { id: this.model.id }

          if (this.model.saleBillNo) {
            this.model['prepareOrderId'] = null
          }
          this.requestSubTableData(this.model.prepareOrderId ? this.url.querySalesOrderDetByPrepareId : this.url.salesOrderDet.list, params, this.salesOrderDetTable)
        }
        delete this.model.id
        this.selectChange(fieldval.customerId)
      }
      ,
      /** 整理成formData */
      classifyIntoFormData(allValues) {

        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          salesOrderDetList: allValues.tablesValue[0].values
        }
      },
      validateError(msg) {
        this.$message.error(msg)
      }
      ,
      //渲染流程表单数据
      showFlowData() {
        if (this.formBpm === true) {
          let params = { id: this.formData.dataId }
          getAction(this.url.queryById, params).then((res) => {
            if (res.success) {
              this.edit(res.result)
            }
          })
        }
      },
      popupCallback(row) {
        this.form.setFieldsValue(pick(row, 'customerId', 'customerName', 'trnpModeCode', 'destination', 'carNo', 'salesMan', 'consigneeName'
          , 'idCard', 'phone', 'stationCode', 'routeCode', 'remark', 'receivingName', 'receivingPhone', 'receivingAddress', 'equipmentSuppliesFlag', 'shipperName', 'orderTime', 'closeTime', 'fleetName', 'fundIds', 'pushDestination', 'pushCarNo', 'pushLogistics', 'surfaceTreatment', 'platingWeight', 'logisticsDestination'))
      },
      onDataChange(props) {
        // console.log(type,row,column,value,target);
        //修改单价 重量 件数 运费 基础单价 加价 车队运费 才会进下面方法
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

          debugger
          //含税单价，五菱  暂时写死
          if (this.tenantId + '' === '2' && this.customerPropId === '1486166902624890882'){
            debugger
            price = math.multiply(props.row.basicPrice || 0 , 1.13 )
          }


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
          // if (props.row.wtMode === '0') {
          //   theoryPrice = price
          //   theoryWeight = weight
          // }
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
          //重新计算列合并
          this.$refs['salesOrderDet'].recalcOneStatisticsColumn('qty')
          this.$refs['salesOrderDet'].recalcOneStatisticsColumn('weight')
          this.$refs['salesOrderDet'].recalcOneStatisticsColumn('price')
          this.$refs['salesOrderDet'].recalcOneStatisticsColumn('totalPrice')
          this.$refs['salesOrderDet'].recalcOneStatisticsColumn('deliverTotalPrice')
          this.$refs['salesOrderDet'].recalcOneStatisticsColumn('fleetTotalPrice')
          // this.$refs['salesOrderDet'].recalcOneStatisticsColumn('theoryPrice')
          // this.$refs['salesOrderDet'].recalcOneStatisticsColumn('theoryWeight')
        }
        /**
         * queryData中的数据拼接顺序不可顺便修改，
         * 如修改需将JPopupOnlReport.vue文件中的initQueryParam监听属性一并修改
         */
        let queryData = props.row.prodCnameOther + '-' + props.row.oldProdCname + '-' + props.row.sgSign + '-' + props.row.orderLen + '-' + props.row.orderWidth
          + '-' + props.row.orderThick + '-' + props.row.prodClassCode + '-' + props.row.prodCname + '-' + this.customerPropId
        this.$refs.salesOrderDet.columns[1].initQueryParam = queryData + '-salesOrderContract'
      },
      //租户为岑海时，收料人 车号必填
      msgValidator() {
        if (this.tenantId + '' === '2') {
          this.validatorRules.carNo.rules[0].required = true
          this.validatorRules.consigneeName.rules[0].required = true
        }
      },
      popupCallbackCustomer(row) {
        this.selectChange(row.customerName)
        this.form.setFieldsValue(pick(row, 'customerName'))
      },
      oneClickAcquire() {
        for (let i = 0; i < this.$refs.salesOrderDet.rows.length; i++) {
          this.$refs.salesOrderDet.getValues((error, values) => {
            this.$refs.salesOrderDet.setValues([{
              rowKey: this.$refs.salesOrderDet.rows[i].id,
              values: {
                'fleetDeliverExpense': this.receivingDetail.fleetDeliverExpense,
                'deliverExpense': this.receivingDetail.deliverExpense,
                'price': math.add(this.receivingDetail.deliverExpense, values[i].basicPrice, values[i].addPrice),
                'fleetTotalPrice': math.toFixed(math.multiply(this.receivingDetail.fleetDeliverExpense, values[i].weight), 2),
                'deliverTotalPrice': math.toFixed(math.multiply(this.receivingDetail.deliverExpense, values[i].weight), 2),
                'fleetName': this.$refs.fleetName.value,
                'totalPrice': math.toFixed(math.multiply(math.add(this.receivingDetail.deliverExpense, values[i].basicPrice, values[i].addPrice), values[i].weight), 2)
              }
            }])
          })
        }
      },

      checkCarNo(record) {
        this.tenantId = record.tenantId
        let id = record.id
        getAction(this.url.checkCarNo, { id: id }).then((res) => {
          if (res.result.booleanValue) {
            this.$message.warning('车号不相同！')
            this.carNo = '导入装车实际车号:' + res.result.stringValue
          }
        })
      },
      onSelectChange(selectedRowKeys, selectionRows) {

        console.log('selectionRows--', selectionRows)
        // this.salesOrderDetTable.fundPoolSource = selectionRows;
        this.salesOrderDetTable.selectedStackRowKeys = selectedRowKeys
        this.selectedRowKeys = selectedRowKeys
        this.fundPoolIds
        this.selectedRowKeys.forEach((i, index) => {
          if (index === 0) {
            this.fundPoolIds = i
          }
          if (index > 0) {
            this.fundPoolIds = this.fundPoolIds + ',' + i
          }
        })
        this.form.setFieldsValue({ fundIds: this.fundPoolIds })
      },
      selectFundPool(customerId) {
        let paymentMethod = this.salesOrderDetTable.queryParamPaymentMethod
        getAction(this.url.selectFundPool, { id: customerId, paymentMethod: paymentMethod }).then((res) => {
          if (res.code === 200) {
            this.salesOrderDetTable.fundPoolSource = res.result
          }
        })
      },
      selectionFundPool(fundIds) {
        let fundPoolIds = fundIds.split(',')
        this.onSelectChange(fundPoolIds)
      },
      SelectPushDestination() {
        getAction(this.url.selectPushDestination).then((res) => {
          if (res.code = 200) {
            this.selectPushDestination = res.result
          }
        })
      },
      SelectPushCarNo() {
        getAction(this.url.selectPushCarNo).then((res) => {
          if (res.code === 200) {
            this.selectPushCarNo = res.result
          }
        })
      },
      handleCheckPrice() {
        let priceDesc = ''
        this.salesOrderDetTable.dataSource.forEach((item, index) => {
          let { values } = this.$refs.salesOrderDet.getValuesSync({ validate: false, rowIds: [item.id] })
          if (values.length > 0) {
            let num = Math.abs(values[0].price - values[0].theoryPrice)
            if (0.06 < (num / values[0].price) && (values[0].prodClassCode == 'B' || values[0].prodClassCode == 'D')) {
              priceDesc = priceDesc + '第 ' + (index + 1) + ' 条明细 ：' + (num / values[0].price).toFixed(4) + ';\n'
            }
          }

        })
        return priceDesc
      },
      priceCheck() {
        for (let i = 0; i < this.$refs.salesOrderDet.rows.length; i++) {
          this.$refs.salesOrderDet.getValues((error, values) => {
            if (values[i].orderThick == this.batchThick) {
              this.$refs.salesOrderDet.setValues([{
                rowKey: this.$refs.salesOrderDet.rows[i].id,
                values: {
                  'price': this.batchPrice,
                  'totalPrice': math.toFixed(math.multiply(this.batchPrice, values[i].weight), 2)
                }
              }])
            }
            this.$refs['salesOrderDet'].recalcOneStatisticsColumn('totalPrice')
          })
        }
      }
      // GetPushLogistics() {
      //   getAction(this.url.getPushLogistics).then((res) => {
      //     if (res.code = 200) {
      //       this.getPushLogistics = res.result;
      //     }
      //   })
      // },
    }
  }
</script>

<style scoped>
</style>