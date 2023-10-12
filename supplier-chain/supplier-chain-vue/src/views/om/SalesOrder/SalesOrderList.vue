<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="顾客">
              <j-search-select-tag placeholder="请选择顾客" v-model="queryParam.customerId"
                                   dict="cm_customer_profile where del_flag=0,company_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="是否已生成装车单">
              <j-dict-select-tag placeholder="请选择状态" v-model="queryParam.createStackStatus"
                                 dictCode="yes_no_status"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <!--          <a-col :xl="5" :lg="7" :md="8" :sm="24">-->
          <!--            <a-form-item label="旧系统单号">-->
          <!--              <j-input placeholder="请输入编号" v-model="queryParam.oldNo"></j-input>-->
          <!--            </a-form-item>-->
          <!--          </a-col>-->
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="创建日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开始时间" class="query-group-cust"
                      v-model="queryParam.createTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择结束时间" class="query-group-cust"
                      v-model="queryParam.createTime_end"></j-date>
            </a-form-item>
          </a-col>
          <!--          <a-col :xl="5" :lg="7" :md="8" :sm="24">-->
          <!--            <a-form-item label="材料号">-->
          <!--              <j-input placeholder="请输入材料号" v-model="queryParam.matNo"></j-input>-->
          <!--            </a-form-item>-->
          <!--          </a-col>-->
          <a-col v-has="'sm:createBy_dictText'" :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="创建人">
              <j-search-select-tag placeholder="请选择创建人" v-model="queryParam.createBy"
                                   :dict="createByDict"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="产品大类">
              <j-dict-select-tag placeholder="请选择产品大类" v-model="queryParam.prodClassCode1" dictCode="prod_code"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="目的地">
              <a-input placeholder="请输入目的地" v-model="queryParam.destination"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="提单编号">
              <j-input placeholder="请输入提单编号" v-model="queryParam.saleBillNo"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="车号">
              <j-input placeholder="请输入车牌号" v-model="queryParam.carNo"></j-input>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:salesMan_dictText'" :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="业务员">
              <j-search-select-tag placeholder="请选择业务员" v-model="queryParam.salesMan"
                                   dict="cm_salesman_info,name,id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="手机号码">
              <j-input placeholder="请输入手机号码" v-model="queryParam.phone"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="仓库">
              <j-dict-select-tag placeholder="请选择仓库" v-model="queryParam.stockHouseId" dictCode="sm_stock,name,id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="牌号">
              <a-input placeholder="请输入牌号" v-model="queryParam.sgSign"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="备注">
              <j-input placeholder="请输入备注" v-model="queryParam.remark"></j-input>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:notSalesMan'" :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="排除业务员">
              <j-search-select-tag placeholder="请选择业务员" v-model="queryParam.notSalesMan"
                                   dict="cm_salesman_info,name,id"/>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:notCustomerId'" :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="排除顾客">
              <j-search-select-tag placeholder="请选择顾客" v-model="queryParam.notCustomerId"
                                   dict="cm_customer_profile where del_flag=0,company_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="助记码">
              <j-input placeholder="请输入助记码" v-model="queryParam.alias"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料号">
              <a-input placeholder="请输入材料号" v-model="queryParam.matNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:fleetName'" a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="车队名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag placeholder="请选择车队" v-model="queryParam.fleetName"
                                   dict="sm_fleet where del_flag=0,fleet_name,fleet_name"/>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:pushLogistics'" :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="物流园推送状态">
              <j-dict-select-tag placeholder="请选择状态" v-model="queryParam.pushLogistics" dictCode="push_logistics"/>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:guangdong'" :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="导入装车">
              <j-dict-select-tag placeholder="是否导入装车" v-model="queryParam.gdImportStackDetail" dictCode="yes_no_status"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button v-has="'sm:add'" @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button v-has="'sm:add'" @click="openConfirmModel" type="primary" icon="plus">生成装车单</a-button>
      <a-button @click="modification()" type="primary" icon="edit">提单信息修改</a-button>
      <a-button v-if="tenantId !=='12'" @click="rpPrint(0)" type="primary" icon="printer">打印装车单</a-button>
      <a-button v-has="'sm:rpPrint_H'" @click="reportH" type="primary" icon="printer">打印装车单(中板)</a-button>
      <a-button v-if="tenantId !=='12'" @click="rpPrint(1)" type="primary" icon="printer">打印签收单</a-button>
      <a-button @click="rpPrint1()" type="primary" icon="printer">预览装车单</a-button>
      <a-button v-has="'sm:guangdong'" @click="rpPrintSignInOrderGD()" type="primary" icon="printer">打印签收单</a-button>
      <a-button v-has="'sm:guangdong'" @click="rpPrintGD()" type="primary" icon="printer">船运转货委托</a-button>
      <a-button v-has="'sm:guangdong'" @click="rpPrintGDCar()" type="primary" icon="printer">车运转货委托</a-button>
      <a-button v-has="'sm:guangdong'" @click="rpPrintGD_H()" type="primary" icon="printer">打印装车通知单（中板）</a-button>
      <a-button v-has="'sm:guangdong'" @click="rpPrintStackOrder()" type="primary" icon="printer">打印装车通知单</a-button>
      <a-button v-has="'sm:guangdong'" @click="stackRp" type="primary" icon="printer">打印钢材销售单</a-button>
      <a-button v-has="'sm:trustedThird'" @click="sendTrustedThird()" type="primary">推送签收单(可信数据)</a-button>
      <a-button v-has="'sm:trustedThird'" @click="viewTrustedThird()" type="primary">查看签收单(可信数据)</a-button>
      <a-button v-has="'sm:pushLogistics'" @click="pushLogistics" type="primary">推送物流园</a-button>
      <a-button v-has="'sm:pushLogistics'" @click="pushLogisticsNow" type="primary" :loading="loading1">立即推送</a-button>
      <a-button v-has="'sm:pushLogistics'" @click="cancelLogistics" type="primary" :loading="loading2">撤销推送</a-button>
<!--      <a-button v-has="'sm:pushLogistics'" @click="pushTransfer" type="primary" :loading="loading3">推送过户</a-button>-->
<!--      <a-button v-has="'sm:pushLogistics'" @click="cancelTransfer" type="primary" :loading="loading4">撤销过户</a-button>-->
      <a-button v-if="tenantId ===5||tenantId ===6||tenantId ===7||tenantId ===8||tenantId ===13"
                type="primary" @click="editCreateInvoiceStatus()">修改开票时间</a-button>
      <a-dropdown v-has="'sm:add'" v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete"/>
            删除
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作
          <a-icon type="down"/>
        </a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{
        selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
        <a style="margin-left: 10%"> 勾选重量：{{this.selectedParams.selectedWeight}}</a>
        <a style="margin-left: 10%"> 勾选金额：{{this.selectedParams.selectedAmount}}</a>
        <a style="margin-left: 10%"> 重量合计：{{this.totalParams.num}}</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        class="j-table-force-nowrap"
        :scroll="{x:true}"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :customRow="customRow"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="getImgView(text)" height="25px" alt=""
               style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            :ghost="true"
            type="primary"
            icon="download"
            size="small"
            @click="downloadFile(text)">
            下载
          </a-button>
        </template>

        <span slot="action" slot-scope="text, record">
          <a v-has="'sm:downLoadSalesOrderRp'" :href="chSalesOrderRp" @click="downLoadRp(record)">下载装车单</a>
          <a-divider v-has="'sm:downLoadSalesOrderRp'" type="vertical"/>
          <a @click="handleDetail(record)">详情</a>

          <a-divider type="vertical"/>
          <a v-has="'sm:add'" @click="handleCobyAdd(record)">复制新增</a>
        </span>
      </a-table>
    </div>

    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="提单明细表" key="1">
        <SalesOrderDetList ref="sodl" :mainId="selectedMainId" :parentRecord="selectionRows[0]"/>
      </a-tab-pane>
<!--      <a-tab-pane tab="费用明细表" key="2">-->
<!--        <CostBreakdownList ref="sodw" :mainId="selectedMainId" :parentRecord="selectionRows[0]"/>-->
<!--      </a-tab-pane>-->
    </a-tabs>

    <sales-order-modal ref="modalForm" @ok="modalFormOk"/>
    <consignee-and-car-no-modal ref="consigneeAndCarNo" :record="this.selectionRows[0]"
                                @ok="loadData"></consignee-and-car-no-modal>

    <j-modal
      :visible="rpVisible"
      fullscreen
      @ok="close"
      @cancel="close"
      cancelText="关闭">
      <iframe :src="chSalesOrderRp" style="width: 100%;height: 85vh" frameborder="0"></iframe>
    </j-modal>

    <j-modal
      :visible="editInvoiceVisible"
      title="开票时间"
      width="60%"
      @ok="handleOKInvoice"
      @cancel="handleCancel"
    >
      <a-row :gutter="24">
        <a-col :span="12">
          <a-form-item label="开票时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开票时间" class="query-group-cust"
                    v-model="createInvoiceTime"></j-date>
          </a-form-item>
        </a-col>
      </a-row>
    </j-modal>
    <!-- 装车单弹窗 -->
    <j-modal
      :title="title"
      :width="width"
      :visible="visible"
      :fullscreen=true
      @ok="handleOk"
      :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
      @cancel="handleCancel"
      cancelText="关闭">
      <div style="width: auto;height: 850px">
        <iframe :src="stackUrl" style="width:97%;height: 100%"></iframe>
      </div>
    </j-modal>

  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import SalesOrderModal from './modules/SalesOrderModal'
  import SalesOrderDetList from './SalesOrderDetList'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import JDate from '@/components/jeecg/JDate.vue'
  import '@/assets/less/TableExpand.less'
  import ACol from 'ant-design-vue/es/grid/Col'
  import reportConfig from '@/views/report_config.json'
  import { getAction, postAction } from '@api/manage'
  import ConsigneeAndCarNoModal from './modules/ConsigneeAndCarNoModal'
  import JInput from '@/components/jeecg/JInput'
  import JEllipsis from '@/components/jeecg/JEllipsis'
  import { colAuthFilter } from '@/utils/authFilter'
  import common from '@views/base/common/common'
  import CostBreakdownList from './CostBreakdownList'
  import { filterDictTextByCache } from '@/components/dict/JDictSelectUtil'

  export default {
    name: 'SalesOrderList',
    mixins: [JeecgListMixin],
    components: {
      CostBreakdownList,
      ACol,
      SalesOrderModal,
      SalesOrderDetList,
      JSearchSelectTag,
      JDate,
      JInput,
      ConsigneeAndCarNoModal,
      JEllipsis
    },
    data() {
      let ellipsis = (v,l = 15) =><j-ellipsis value={v} length={25}/>
      return {
        loading1: false,
        loading2: false,
        loading3: false,
        loading4: false,
        createByDict: '',
        rpVisible: false,
        editInvoiceVisible: false,
        labelCol: {
          xs: { span: 10 },
          sm: { span: 6 }
        },
        wrapperCol: {
          xs: { span: 15 },
          sm: { span: 10 }
        },
        visible: false,
        queryParam: {
          createStackStatus: '0'
        },
        rpName: '',
        disableMixinCreated: true,
        selectedMainId: '',
        createInvoiceTime: '',
        stackDetDataSource: [],
        description: '销售单管理页面',
        reportUrl: '',
        previewVisible: false,
        previewImage: '',
        //下载报表
        baseUrl: '',
        chSalesOrderRp: common.reportUrl + reportConfig.reportUrl.ch.salesOrderUrl,
        // 表头
        columns: [
          {
            title: '序号',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: 'center',
            customRender: function (t, r, index) {
              return parseInt(index) + 1
            }
          },
          {
            title: '创建日期',
            align: 'center',
            dataIndex: 'createTime',
            sorter: true
          },
          {
            title: '制单时间',
            align: 'center',
            dataIndex: 'orderTime',
            sorter: true
          },
          {
            title: '截止日期',
            align: 'center',
            dataIndex: 'closeTime'
          },
          {
            title: '提单号',
            align: 'center',
            dataIndex: 'saleBillNo'
          },
          {
            title: '客户名称',
            align: 'center',
            dataIndex: 'customerId_dictText'
          },
          {
            title: '来款可用金额',
            align: 'center',
            dataIndex: 'incomeAvailableAccountString'
          },
          {
            title: '提单总金额',
            align: 'center',
            dataIndex: 'totalAccountYRMString'
          },
          {
            title: '客户需打款金额',
            align: 'center',
            dataIndex: 'customerNeedToPayAmount'
          },
          {
            title: '业务员',
            align: 'center',
            dataIndex: 'salesMan_dictText',

          },
          {
            title: '创建人',
            align: 'center',
            dataIndex: 'createBy_dictText'
          },
          // {
          //   title: '制单人',
          //   align: 'center',
          //   dataIndex: 'orderBy'
          // },

          {
            title: '仓库',
            align: 'center',
            dataIndex: 'stockId_dictText'
          },
          // {
          //   title: '材料号',
          //   align: 'center',
          //   dataIndex: 'matNo'
          // },
          {
            title: '已装车',
            align: 'center',
            dataIndex: 'createStackStatus_dictText'
          },
          {
            title: '备注',
            align: 'center',
            dataIndex: 'remark',
            customRender: (text) => ellipsis(text)
          },
          {
            title: '是否生成发票',
            align: 'center',
            dataIndex: 'createInvoiceStatus_dictText'
          },
          {
            title: '开票时间',
            align: 'center',
            dataIndex: 'createInvoiceTime',
            sorter: true
          },
          {
            title: '提货人',
            align: 'center',
            dataIndex: 'consigneeName'
          },
          {
            title: '装货人',
            align: 'center',
            dataIndex: 'shipperName'
          },
          {
            title: '车号',
            align: 'center',
            dataIndex: 'carNo'
          },
          {
            title: '手机号码',
            align: 'center',
            dataIndex: 'phone'
          },
          {
            title: '重量',
            align: 'center',
            dataIndex: 'weight'
          },
          {
            title: '车队',
            align: 'center',
            dataIndex: 'fleetName'
          },
          {
            title: '目的地',
            align: 'center',
            dataIndex: 'destination'
          },
          {
            title: '收料人',
            align: 'center',
            dataIndex: 'receivingName'
          },
          /*{
            title: '到站',
            align: 'center',
            dataIndex: 'stationCode_dictText'
          },*/
          /*{
            title: '专用线',
            align: 'center',
            dataIndex: 'routeCode_dictText'
          },*/
          {
            title: '总金额',
            align: 'center',
            dataIndex: 'totalAmountString'
          },
          {
            title: '推送状态',
            align: 'center',
            dataIndex: 'pushLogistics',
            customRender: function (text) {
              return filterDictTextByCache('push_logistics', text)
            }
          },
          {
            title: '是否导入装车',
            align: 'center',
            dataIndex: 'gdImportStackDetail',
            customRender: function (text) {
              return filterDictTextByCache('yes_no_status', text)
            }
          },
          {
            title: '打印次数',
            align: 'center',
            dataIndex: 'printNum'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: 'center',
            fixed: 'right',
            scopedSlots: { customRender: 'action' },
          },
        ],
        url: {
          queryCompanyReportName: '/bd/tenantReport/queryCompanyReportName',
          list: '/om/salesOrder/list',
          delete: '/om/salesOrder/delete',
          deleteBatch: '/om/salesOrder/deleteBatch',
          exportXlsUrl: '/om/salesOrder/exportXls',
          importExcelUrl: 'om/salesOrder/importExcel',
          sendTrustedThird: 'trustedThird/trustedThird/send',
          viewTrustedThird: 'trustedThird/trustedThird/viewTrustedThird',
          printNum: '/om/salesOrder/printNum',
          pushLogistics: '/om/salesOrder/waitPush',
          pushLogisticsNow: '/om/salesOrder/pushLogisticsNow',
          pushTransfer: '/om/salesOrder/pushTransfer',
          cancelLogistics: '/om/salesOrder/cancelLogistics',
          cancelTransfer: '/om/salesOrder/cancelTransfer',
          editCreateInvoiceStatus: '/om/salesOrder/editCreateInvoiceStatus'
        },
        title: '',
        width: 1600,
        disableSubmit: false,
        stackUrl: {
          // stackUrl0:common.reportUrl + '/webroot/decision/view/report?viewlet=SMS_ZX%252FSM%252FsalesOrder.cpt&ref_t=design&ref_c=7730d52e-42a5-49c0-b1ca-2b8ed3f3381c',
          //ch
          stackUrl1: common.reportUrl + '/webroot/decision/view/report?viewlet=SMS_ZX%252FSM%252FCH%252FsalesOrder.cpt&ref_t=design&ref_c=b80f776f-7d57-41c1-9329-9e566421ef3b',
          //yrm
          stackUrl2: common.reportUrl + '/webroot/decision/view/report?viewlet=SMS_ZX%252FSM%252FYRM%252FsalesOrder_H.cpt&ref_t=design&ref_c=7730d52e-42a5-49c0-b1ca-2b8ed3f3381c',
        },
        totalParams: {
          num: 0,
        },
        selectedParams: {
          selectedWeight: 0,
          selectedAmount: 0
        },
        dictOptions: {}
      }
    },
    created() {
      this.disableMixinCreated = true
      this.columns = colAuthFilter(this.columns, 'sm:')
      this.loadData()
      this.initDictConfig()
      this.queryReportUrl()
      this.createByDict = 'sys_user where rel_tenant_ids =' + this.tenantId + 'and del_flag=0 order by create_time,realname,username'
    },
    mounted() {
      this.loadReportConfig()
    },
    watch: {
      'dataSource': function () {
        let totalNum = 0
        for (let i = 0; i < this.dataSource.length; i++) {

          let totalAmount = this.dataSource[i].totalAmount.toFixed(2) || 0
          this.dataSource[i].totalAmountString = ( totalAmount+ '').replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,');
          if (this.dataSource[i].incomeAvailableAccount != undefined){
            let incomeAvailable = this.dataSource[i].incomeAvailableAccount.toFixed(2) || 0
            this.dataSource[i].incomeAvailableAccountString = (incomeAvailable + '').replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,');
          }
          if (this.dataSource[i].incomeAvailableAccount != undefined){
            let totalAccountYRM = this.dataSource[i].totalAccountYRM.toFixed(2) || 0
            this.dataSource[i].totalAccountYRMString = (totalAccountYRM + '').replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,');
          }totalNum += this.dataSource[i].weight
        }
        this.totalParams.num = totalNum.toFixed(3)
      },
      'selectionRows': function (selectionRows) {
        if (selectionRows) {
          let rows = selectionRows
          let selectedWeight = 0
          let selectedAmount = 0
          for (let i = 0; i < rows.length; i++) {
            selectedWeight += rows[i].weight
            selectedAmount += rows[i].totalAmount
          }
          this.selectedParams.selectedWeight = selectedWeight.toFixed(3)
          this.selectedParams.selectedAmount = selectedAmount.toFixed(2).replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,');
        }
      }
    },
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      }
    },
    methods: {
      initDictConfig() {
      },
      queryReportUrl() {
        let that = this
        getAction(that.url.queryCompanyReportName, { tenantId: that.tenantId }).then((res) => {
          if (res.success) {
            that.rpName = res.message
          }
        })
      },
      judgeAction(val) {
        if (val === 0) {
          if (this.rpName !== 'empty') {
            this.reportUrl = reportConfig.reportUrl.sm.salesOrderUrl.replace('xxx', this.rpName)
          } else {
            this.reportUrl = reportConfig.reportUrl.sm.salesOrder
          }
        }
        if (val === 1) {
          if (this.rpName !== 'empty') {
            this.reportUrl = reportConfig.reportUrl.sm.stackUrl.replace('xxx', this.rpName)
          } else {
            this.reportUrl = reportConfig.reportUrl.sm.stack
          }
        }
      },
      rpPrint(val) {
        if (this.selectionRows == null || this.selectedRowKeys.length === 0) {
          return this.$message.warning('请勾选记录！')
        }
        this.judgeAction(val)
        let salesBillNo = []
        let salesBillNoH = []
        for (let i = 0; i < this.selectionRows.length; i++) {
          //如果租户为YRM
          salesBillNo.push(this.selectionRows[i].saleBillNo)
        }
        // if(val == 0) {
        //   window.open('http://localhost:8888/webroot/decision/view/report?viewlet=newSalesOrder.cpt' + '&tenant_id=' + this.tenantId + '&id='+this.selectionRows[0].saleBillNo);
        //
        // }
        // if(val == 1) {
        //   window.open('http://localhost:8888/webroot/decision/view/report?viewlet=newSignInOrder.cpt' + '&tenant_id=' + this.tenantId + '&id='+this.selectionRows[0].saleBillNo);
        //
        // }
        if (salesBillNo.length > 0) {
          this.reportPrint(salesBillNo, this.tenantId, this.reportUrl)
        }
        // //如果租户为YRM
        // if(salesBillNoH.length>0 && this.rpName==='YRM'){
        //   let reportUrlH = list[0] + '_H.' + list[1]
        //   this.reportPrint(salesBillNoH,this.tenantId,reportUrlH)
        // }
      },
      rpPrint2(){
        if (this.selectionRows == null || this.selectedRowKeys.length === 0 || this.selectedRowKeys.length > 1) {
          return this.$message.warning('请勾选一条记录！')
        }
        window.open("http://localhost:8888/webroot/decision/view/report?viewlet=goodEntrustmentLetter.cpt&id="+ this.selectionRows[0].id);
      },
      reportH() {
        let salesBillNoH = []
        for (let i = 0; i < this.selectionRows.length; i++) {
          salesBillNoH.push(this.selectionRows[i].saleBillNo)
        }
        this.reportPrint(salesBillNoH, this.tenantId, reportConfig.reportUrl.yrm.H)
      },
      //预览装车单
      rpPrint1() {
        if (this.selectionRows == null || this.selectedRowKeys.length === 0 || this.selectedRowKeys.length > 1) {
          return this.$message.warning('请勾选一条记录！')
        }

        this.visible = true
        for (let i = 0; i < this.selectionRows.length; i++) {
          this.saleBillNo = this.selectionRows[i].saleBillNo
          if (this.rpName === 'YRM') {
            this.stackUrl = this.stackUrl.stackUrl2 + '&id=' + this.saleBillNo + '&tenant_id=' + this.$ls.get('TENANT_ID')
          } else {
            this.stackUrl = this.stackUrl.stackUrl1 + '&id=' + this.saleBillNo + '&tenant_id=' + this.$ls.get('TENANT_ID')
          }
          // else if (this.rpName==='CH'){
          //   this.stackUrl = this.stackUrl.stackUrl +'&id=' + this.saleBillNo + '&tenant_id=' + this.$ls.get("TENANT_ID")
          // }
        }
      },
      rpPrintGD() {
        let ids = []
        for (let i = 0; i < this.selectionRows.length; i++) {
          ids.push(this.selectionRows[i].id)
        }
        this.reportPrint(ids, this.tenantId, reportConfig.reportUrl.gd.salesOrderUrl);
      },
      rpPrintGDCar() {
        let ids = []
        for (let i = 0; i < this.selectionRows.length; i++) {
          ids.push(this.selectionRows[i].id)
        }
        this.reportPrint(ids, this.tenantId, reportConfig.reportUrl.gd.salesOrderCarUrl);
      },
      rpPrintSignInOrderGD() {
        let saleBillNo = []
        for (let i = 0; i < this.selectionRows.length; i++) {
          saleBillNo.push(this.selectionRows[i].saleBillNo)
        }
        this.reportPrint(saleBillNo, this.tenantId, reportConfig.reportUrl.gd.signInOrder);
      },
      // rpPrintGD_H() {
      //   if (this.selectionRows == null || this.selectedRowKeys.length === 0 || this.selectedRowKeys.length > 1) {
      //     return this.$message.warning('请勾选一条记录！')
      //   }
      //   let saleBillNo = []
      //   let sgSign =this.$refs.sodl.dataSource[0].sgSign;
      //   saleBillNo.push(this.selectionRows[i].saleBillNo)
      //   if(sgSign == 'Q235WQ'|| sgSign=='Q355BEM' || sgSign=='SS400WQ') {
      //     this.reportPrint(saleBillNo, this.tenantId, reportConfig.reportUrl.gd.salesOrderUrl_H-1);
      //   } else if(sgSign == 'Q235B'|| sgSign=='Q355B') {
      //     this.reportPrint(saleBillNo, this.tenantId, reportConfig.reportUrl.gd.salesOrderUrl_H-2);
      //   }
      // },
      rpPrintGD_H() {
        let saleBillNo = []

        //广东打印装车通知单记录点击打印次数
        var ids = ''
        for (var a = 0; a < this.selectedRowKeys.length; a++) {
          ids += this.selectedRowKeys[a] + ','
        }
        getAction(this.url.printNum, { ids: ids }).then((res) => {
          Console.log("打印次数 + 1")
        }).finally(() => {
          this.loadData()
        })

        for (let i = 0; i < this.selectionRows.length; i++) {
          saleBillNo.push(this.selectionRows[i].saleBillNo)
        }
        this.reportPrint(saleBillNo, this.tenantId, reportConfig.reportUrl.gd.stackOrder_H);
      },
      rpPrintStackOrder() {
        let saleBillNo = []

        //广东打印装车通知单记录点击打印次数
        var ids = ''
        for (var a = 0; a < this.selectedRowKeys.length; a++) {
          ids += this.selectedRowKeys[a] + ','
        }
        getAction(this.url.printNum, { ids: ids }).then((res) => {
          Console.log("打印次数 + 1")
        }).finally(() => {
          this.loadData()
        })

        for (let i = 0; i < this.selectionRows.length; i++) {
          saleBillNo.push(this.selectionRows[i].saleBillNo)
        }
        this.reportPrint(saleBillNo, this.tenantId, reportConfig.reportUrl.gd.stackOrder);
      },
      stackRp() {
        if (this.selectionRows.length === 0 || this.selectedRowKeys.length === '') {
          return this.$message.warning('请勾选记录！')
        }
        let saleBillNo = []
        for (let i = 0; i < this.selectionRows.length; i++) {
          saleBillNo.push(this.selectionRows[i].saleBillNo)
        }
        this.reportPrint(saleBillNo, this.tenantId, reportConfig.reportUrl.gd.stack);

      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedMainId = selectedRowKeys[0]
        this.selectedRowKeys = selectedRowKeys
        this.selectionRows = selectionRows
      },
      openConfirmModel() {
        if (this.selectionRows == null || this.selectedRowKeys.length === 0 || this.selectedRowKeys.length > 1) {
          return this.$message.warning('请勾选一条记录！')
        }
        if (this.selectionRows[0].createStackStatus === '1') {
          return this.$message.warning('该提单已生成装车单！')
        }
        this.handleEdit(this.selectionRows[0])
      },
      pushLogistics() {
        var that = this
        if (that.selectionRows == null || that.selectedRowKeys.length === 0) {
          return this.$message.warning('请勾选记录！')
        }
        postAction(that.url.pushLogistics, that.selectedRowKeys).then((res) => {
            if (res.success) {
              this.$message.success('操作成功')
            } else {
              this.$message.warning(res.message)
            }
          }
        ).finally(() => {
          this.loadData()
        })
      },
      pushLogisticsNow() {

        var that = this
        that.loading1 = true
        var id = that.selectedRowKeys[0]
        if (that.selectedRowKeys.length < 1 || that.selectedRowKeys.length > 1) {
          return that.$message.warning('请勾选一条记录！')
        }
        getAction(that.url.pushLogisticsNow, {id:id}).then((res) => {
            if (res.success) {
              this.$message.success('操作成功')
            } else {
              that.$message.warning(res.message)
            }
          }
        ).finally(() => {
          that.loading1 = false
          that.loadData()
        })
      },
      pushTransfer() {

        var that = this
        that.loading3 = true
        var id = that.selectedRowKeys[0]
        if (that.selectedRowKeys.length < 1 || that.selectedRowKeys.length > 1) {
          return this.$message.warning('请勾选一条记录！')
        }
        getAction(that.url.pushTransfer, {id:id}).then((res) => {
            if (res.success) {
              this.$message.success('操作成功')
            } else {
              this.$message.warning(res.message)
            }
          }
        ).finally(() => {
          that.loading3 = false
          this.loadData()
        })
      },
      cancelLogistics() {

        var that = this
        that.loading2 = true
        if (that.selectionRows == null || that.selectedRowKeys.length === 0) {
          return that.$message.warning('请勾选记录！')
        }
        postAction(that.url.cancelLogistics, that.selectedRowKeys).then((res) => {
            if (res.success) {
              that.$message.success('操作成功')
            } else {
              that.$message.warning(res.message)
            }
          }
        ).finally(() => {
          that.loading2 = false
          that.loadData()
        })
      },
      cancelTransfer() {

        var that = this
        that.loading4 = false
        if (that.selectionRows == null || that.selectedRowKeys.length === 0) {
          return this.$message.warning('请勾选记录！')
        }
        postAction(that.url.cancelTransfer, that.selectedRowKeys).then((res) => {
            if (res.success) {
              this.$message.success('操作成功')
            } else {
              this.$message.warning(res.message)
            }
          }
        ).finally(() => {
          that.loading4 = false
          this.loadData()
        })
      },
      modification() {
        if (this.selectionRows == null || this.selectedRowKeys.length === 0 || this.selectedRowKeys.length > 1) {
          return this.$message.warning('请勾选一条记录！')
        }
        this.$refs.consigneeAndCarNo.getSalesOrderDetList()
        this.$refs.consigneeAndCarNo.visible = true
      },
      //发起签收单签署
      sendTrustedThird() {
        if (this.selectionRows.length !== 1) {
          return this.$message.warning('请勾选一条记录')
        } else {
          getAction(this.url.sendTrustedThird, { saleBillNo: this.selectionRows[0].saleBillNo }).then((res) => {
              if (res.success) {
                this.$message.success('推送成功')
              } else {
                this.$message.warning(res.message)
              }

            }
          )
        }
      },
      //查看作准文件
      viewTrustedThird() {
        if (this.selectionRows.length !== 1) {
          return this.$message.warning('请勾选一条记录')
        } else {
          getAction(this.url.viewTrustedThird, { saleBillNo: this.selectionRows[0].saleBillNo }).then((res) => {
              if (res.success) {
                window.open(common.outerNetReportUrl + res.result)
              } else {
                this.$message.warning(res.message)
              }

            }
          )
        }
      },
      handleEdit: function (record) {
        this.$refs.modalForm.edit(record)
        this.$refs.modalForm.title = '生成装车单'
        this.$refs.modalForm.disableSubmit = false
      },
      downLoadRp(record) {
        this.chSalesOrderRp = common.reportUrl + reportConfig.reportUrl.ch.salesOrderUrl + '&id=' + record.saleBillNo + '&tenant_id=' + record.tenantId + '&format=excel'
        // this.rpVisible = true
      },
      close() {
        this.baseUrl = ''
        this.rpVisible = false

      },
      handleOk() {
        this.$emit('close')
        this.visible = false
        this.$refs.realForm.submitForm()
      },
      handleCancel() {
        this.$emit('close')
        this.visible = false
        this.previewVisible = false
        this.editInvoiceVisible = false
      },
      editCreateInvoiceStatus(){
        this.editInvoiceVisible = true
      },
      handleOKInvoice(){
        var that = this
        if (that.selectionRows == null || that.selectedRowKeys.length < 1 ) {
          return that.$message.warning('请勾选一条记录！')
        }
        var ids = "";
        for (var a = 0; a < this.selectedRowKeys.length; a++) {
          ids += this.selectedRowKeys[a] + ",";
        }
        var createInvoiceTime =  this.createInvoiceTime
        getAction(this.url.editCreateInvoiceStatus,{ids,createInvoiceTime}).then((res) => {
          if (res.success) {
            this.$message.success('修改成功！')
          }
          if (res.code !== 200) {
            this.$message.warning(res.message)
          }
        }).finally(() => {
          this.handleCancel()
          this.loadData()
        })
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>