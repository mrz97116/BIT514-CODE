<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="装车单号">
              <j-input placeholder="请输入装车单号" v-model="queryParam.stackingNo"></j-input>
            </a-form-item>
          </a-col>
          <!--          <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
          <!--            <a-form-item label="旧系统单号">-->
          <!--              <j-input placeholder="请输入单号" v-model="queryParam.oldStackNo"></j-input>-->
          <!--            </a-form-item>-->
          <!--          </a-col>-->
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="提单号">
              <j-input placeholder="请输入提单号" v-model="queryParam.saleBillNo"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="车号">
              <j-input placeholder="请输入车号" v-model="queryParam.carNo"></j-input>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:shipNo'" :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="船号">
              <a-input placeholder="请输入船号" v-model="queryParam.shipNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:createTime'" :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="创建日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开始时间" class="query-group-cust"
                      v-model="queryParam.createTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择结束时间" class="query-group-cust"
                      v-model="queryParam.createTime_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:orderTime'" :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="制单时间">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开始时间" class="query-group-cust"
                      v-model="queryParam.orderTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择结束时间" class="query-group-cust"
                      v-model="queryParam.orderTime_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:shipDate'" :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="装车时间">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开始时间" class="query-group-cust"
                      v-model="queryParam.shipDate_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择结束时间" class="query-group-cust"
                      v-model="queryParam.shipDate_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="顾客">
              <j-search-select-tag placeholder="请选择顾客" v-model="queryParam.customerId"
                                   dict="cm_customer_profile where  del_flag=0,company_name,id"/>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:salesMan_dictText'" :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="业务员">
              <j-search-select-tag placeholder="请选择业务员" v-model="queryParam.salesMan"
                                   dict="cm_salesman_info,name,id"/>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:createBy_dictText'" :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="创建人">
              <j-search-select-tag placeholder="请选择创建人" v-model="queryParam.createBy"
                                   :dict="createByDict"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="仓储">
              <j-search-select-tag placeholder="请选择仓储" v-model="queryParam.stockNoClass"
                                   dict="sm_stock where del_flag=0,name,id"></j-search-select-tag>
            </a-form-item>
          </a-col>
          <!--addBy liujiazhi 2021.04.27-->
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="牌号">
              <a-input placeholder="请输入牌号" v-model="queryParam.sgSign"></a-input>
            </a-form-item>
          </a-col>
          <!--addBy liujiazhi 2021.04.27-->
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="品名">
              <a-input placeholder="请输入品名" v-model="queryParam.prodCname"></a-input>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:status_dictText'" :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="冲红状态">
              <j-dict-select-tag placeholder="请选择冲红状态" v-model="queryParam.status" dictCode="stack_status"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="产品大类">
              <j-dict-select-tag placeholder="请选择产品大类" v-model="queryParam.prodClassCode1" dictCode="prod_code"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="是否已生成结算单">
              <j-dict-select-tag placeholder="请选择状态" v-model="queryParam.settled" dictCode="settled_state"/>
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
      <a-button v-if="tenantId !=='12'" @click="rpPrint(0)" type="primary" icon="plus">打印装车单</a-button>
      <a-button v-if="tenantId !=='12'" @click="rpPrint(1)" type="primary" icon="plus">打印签收单</a-button>
      <a-button v-if="tenantId !=='12'" @click="rpPrint1()" type="primary" icon="plus">预览装车单</a-button>
      <a-button v-has="'sm:checkListRp'" @click="checkListRp" type="primary" icon="plus">打印装车清单</a-button>
      <a-button v-has="'sm:checkListRp'" @click="checkListRp_H" type="primary" icon="plus">打印装车清单(中板)</a-button>
      <a-button v-has="'sm:stackWriteOff'" @click="writeOff" type="primary" icon="plus">冲红</a-button>
      <a-button v-has="'sm:stackReviewer'" @click="writeOffReviewer" type="primary" icon="plus">冲红复核</a-button>
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
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a
        style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
        <a style="margin-left: 13%"> 勾选重量：{{this.selectedParams.selectedWeight}}</a>
        <a style="margin-left: 2%"> 勾选总价：{{this.selectedParams.selectedAmount}}</a>
        <a style="margin-left: 13%"> 重量合计：{{this.totalParams.totalWeight}}</a>
        <a style="margin-left: 2%"> 总价合计：{{this.totalParams.totalAmount}}</a>
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
          <!--<a @click="handleEdit(record)">编辑</a>-->
          <!--<a @click="handleDetail(record)">详情</a>
          <a-divider type="vertical"/>
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a v-has="'sm:add'">删除</a>
          </a-popconfirm>-->
          <!--<a @click="rpPrint1()">装车单</a>-->
          <a-divider type="vertical"/>
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"/>
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a v-has="'sm:add'">删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

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

    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="装车单明细表" key="1">
        <StackDetList :mainId="selectedMainId" :parentRecord="selectionRows[0]"/>
      </a-tab-pane>
    </a-tabs>

    <stack-modal ref="modalForm" @ok="modalFormOk"/>
    <write-off-modal ref="WriteOffModal"></write-off-modal>
    <write-off-reviewer-modal ref="WriteOffReviewerModal"></write-off-reviewer-modal>

  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import StackModal from './modules/StackModal'
  import StackDetList from './StackDetList'
  import '@/assets/less/TableExpand.less'
  import JSearchSelectTag from '@comp/dict/JSearchSelectTag'
  import JDate from '@/components/jeecg/JDate.vue'
  import reportConfig from '@views/report_config.json'
  import { getAction } from '@api/manage'
  import JInput from '@/components/jeecg/JInput'
  import { colAuthFilter } from '@/utils/authFilter'
  import common from '@views/base/common/common.js'
  import WriteOffModal from './modules/WriteOffModal'
  import WriteOffReviewerModal from './modules/WriteOffReviewerModal'
  import { filterDictTextByCache } from '@/components/dict/JDictSelectUtil'

  export default {
    name: 'StackList',
    mixins: [JeecgListMixin],
    components: {
      JSearchSelectTag,
      StackModal,
      StackDetList,
      JInput,
      JDate,
      WriteOffModal,
      WriteOffReviewerModal
    },
    data() {
      return {
        createByDict: '',
        statusDict: '',
        rpName: '',
        reportUrl: '',
        selectedMainId: '',
        saleBillNo: '',
        queryParam: {
          settled: '',
        },
        description: '装车单主表管理页面',
        // 表头
        columns: [
          {
            title: '#',
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
            dataIndex: 'createTime'
          },
          {
            title: '制单时间',
            align: 'center',
            dataIndex: 'orderTime'
          },
          {
            title: '装车时间',
            align: 'center',
            dataIndex: 'shipDate'
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
          {
            title: '装车单号',
            align: 'center',
            dataIndex: 'stackingNo'
          },
          // {
          //   title: '旧系统单号',
          //   align: 'center',
          //   dataIndex:  'oldStackNo'
          // },
          {
            title: '提单号',
            align: 'center',
            dataIndex: 'saleBillNo'
          },
          {
            title: '重量',
            align: 'center',
            dataIndex: 'weight'
          },
          // {
          //   title: '数量',
          //   align: 'center',
          //   dataIndex: 'qty'
          // },
          {
            title: '总价',
            align: 'center',
            dataIndex: 'totalAmountString'
          },
          {
            title: '每吨优惠',
            align: 'center',
            dataIndex: 'discountString'
          },
          {
            title: '结算金额',
            align: 'center',
            dataIndex: 'settledTotalPriceString'
          },
          {
            title: '顾客',
            align: 'center',
            dataIndex: 'customerId_dictText'
          },
          {
            title: '仓储',
            align: 'center',
            dataIndex: 'stockNoClass_dictText'
          },
          {
            title: '车号',
            align: 'center',
            dataIndex: 'carNo'
          },
          {
            title: '电话号码',
            align: 'center',
            dataIndex: 'phone'
          },
          {
            title: '红冲状态',
            align: 'center',
            dataIndex: 'status',
            customRender: function (text) {
              return filterDictTextByCache('stack_status', text)
            }
          },
          {
            title: '船号',
            align: 'center',
            dataIndex: 'shipNo'
          },
          {
            title: '装货人',
            align: 'center',
            dataIndex: 'shipperName'
          },
          {
            title: '备注',
            align: 'center',
            dataIndex: 'remark'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: 'center',
            fixed: 'right',
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          queryCompanyReportName: '/bd/tenantReport/queryCompanyReportName',
          list: '/sm/stack/list',
          delete: '/sm/stack/delete',
          deleteBatch: '/sm/stack/deleteBatch',
          exportXlsUrl: '/sm/stack/exportXls',
          importExcelUrl: 'sm/stack/importExcel',
          selectStackDet: 'sm/stack/selectStackDet',
        },
        title: '',
        width: 1600,
        visible: false,
        disableSubmit: false,
        stackUrl: {
          // stackUrl0:common.reportUrl + '/webroot/decision/view/report?viewlet=SMS_ZX%252FSM%252FsalesOrder.cpt&ref_t=design&ref_c=7730d52e-42a5-49c0-b1ca-2b8ed3f3381c',
          //ch
          stackUrl1: common.reportUrl + '/webroot/decision/view/report?viewlet=SMS_ZX%252FSM%252FCH%252FsalesOrder.cpt&ref_t=design&ref_c=b80f776f-7d57-41c1-9329-9e566421ef3b',
          //yrm
          stackUrl2: common.reportUrl + '/webroot/decision/view/report?viewlet=SMS_ZX%252FSM%252FYRM%252FsalesOrder_H.cpt&ref_t=design&ref_c=7730d52e-42a5-49c0-b1ca-2b8ed3f3381c',
        },
        /* 分页参数 */
        ipagination: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '50', '100','300'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        totalParams: {
          totalWeight: 0,
          totalAmount: 0,
        },
        selectedParams: {
          selectedWeight: 0,
          selectedAmount: 0
        },
        dictOptions: {},
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
        let totalWeight = 0
        let totalAmount = 0
        for (let i = 0; i < this.dataSource.length; i++) {
          this.dataSource[i].totalAmount = this.dataSource[i].totalAmount || 0;
          this.dataSource[i].discount = this.dataSource[i].discount || 0;
          this.dataSource[i].settledTotalPrice = this.dataSource[i].settledTotalPrice || 0;

          this.dataSource[i].totalAmountString = (this.dataSource[i].totalAmount.toFixed(2) + '').replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,');
          this.dataSource[i].discountString = (this.dataSource[i].discount.toFixed(2) + '').replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,');
          this.dataSource[i].settledTotalPriceString = (this.dataSource[i].settledTotalPrice.toFixed(2) + '').replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,');

          totalWeight += this.dataSource[i].weight
          totalAmount += this.dataSource[i].totalAmount
        }
        this.totalParams.totalWeight = totalWeight.toFixed(3)
        this.totalParams.totalAmount = (totalAmount.toFixed(2) + '').replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,');
      },
      'selectionRows': function (selectionRows) {
        if (selectionRows) {
          let rows = selectionRows
          let selectedAmount = 0
          let selectedWeight = 0
          for (let i = 0; i < rows.length; i++) {
            selectedAmount += rows[i].totalAmount
            selectedWeight += rows[i].weight
          }
          this.selectedParams.selectedAmount = (selectedAmount.toFixed(2) + '').replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,');
          this.selectedParams.selectedWeight = selectedWeight.toFixed(3)
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
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedMainId = selectedRowKeys[0]
        this.selectedRowKeys = selectedRowKeys
        this.selectionRows = selectionRows
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
          }
          else {
            this.reportUrl = reportConfig.reportUrl.sm.salesOrder
          }
        }
        if (val === 1) {
          if (this.rpName !== 'empty') {
            this.reportUrl = reportConfig.reportUrl.sm.stackUrl.replace('xxx', this.rpName)
          }
          else {
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
          if (this.selectionRows[i].prodClassCode === 'H' && this.rpName === 'YRM') {
            salesBillNoH.push(this.selectionRows[i].saleBillNo)
          } else {
            salesBillNo.push(this.selectionRows[i].saleBillNo)
          }
        }
        if (salesBillNo.length > 0) {
          this.reportPrint(salesBillNo, this.tenantId, this.reportUrl)
        }
        //如果租户为YRM
        if (salesBillNoH.length > 0 && this.rpName === 'YRM') {
          let list = this.reportUrl.split('.')
          let reportUrlH = list[0] + '_H.' + list[1]
          this.reportPrint(salesBillNoH, this.tenantId, reportUrlH)
        }
      },
      //预览装车单
      rpPrint1() {
        if (this.selectionRows == null || this.selectedRowKeys.length === 0 || this.selectedRowKeys.length > 1) {
          return this.$message.warning('请勾选一条记录！')
        }
        debugger
        this.visible = true
        for (let i = 0; i < this.selectionRows.length; i++) {
          this.saleBillNo = this.selectionRows[i].saleBillNo
          if (this.rpName === 'YRM') {
            this.stackUrl = this.stackUrl.stackUrl2 + '&id=' + this.saleBillNo + '&tenant_id=' + this.$ls.get('TENANT_ID')
          } else {
            this.stackUrl = this.stackUrl.stackUrl1 + '&id=' + this.saleBillNo + '&tenant_id=' + this.$ls.get('TENANT_ID')
          }
          // else if (this.rpName==='CH'){
          // this.stackUrl = this.stackUrl.stackUrl1 +'&id=' + this.saleBillNo + '&tenant_id=' + this.$ls.get("TENANT_ID")
          // }
        }
      },
      checkListRp() {
        if (this.rpName !== 'empty') {
          let rp = reportConfig.reportUrl.sm.checkListUrl.replace('xxx', this.rpName)
          this.reportPrint(this.selectedRowKeys, -1, rp)
        }
      },
      checkListRp_H() {
        if (this.rpName !== 'empty') {
          let rp = reportConfig.reportUrl.sm.checkListUrl_H.replace('xxx', this.rpName)
          this.reportPrint(this.selectedRowKeys, -1, rp)
        }
      },
      stackRp() {
        if (this.selectionRows.length > 1 || this.selectedRowKeys.length > 1) {
          return this.$message.warning('请勾选一条记录！')
        }
        let saleBillNo = []
        for (let i = 0; i < this.selectionRows.length; i++) {
          saleBillNo.push(this.selectionRows[i].saleBillNo)
        }
        this.reportPrint(saleBillNo, this.tenantId, reportConfig.reportUrl.gd.stack);

      },
      handleOk() {
        this.$emit('close')
        this.visible = false
        this.$refs.realForm.submitForm()
      },
      close() {
        this.$emit('close')
        this.visible = false
      },
      handleCancel() {
        this.close()

      },
      writeOff() {
        debugger
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('请勾选一条记录')
        } else if (this.selectedRowKeys.length > 1) {
          this.$message.warning('只可以勾选一条记录')
        } else {
          let id = this.selectedRowKeys[0]
          this.$refs.WriteOffModal.stackId = id
          getAction(this.url.selectStackDet, { id: id }).then((res) => {
            this.$refs.WriteOffModal.selectionRows = res.result
          })
          this.$refs.WriteOffModal.writeOffVisible = true
        }

      },
      writeOffReviewer() {
        debugger
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('请勾选一条记录')
        } else if (this.selectedRowKeys.length > 1) {
          this.$message.warning('只可以勾选一条记录')
        } else {
          let id = this.selectedRowKeys[0]
          this.$refs.WriteOffReviewerModal.stackId = id
          getAction(this.url.selectStackDet, { id: id }).then((res) => {
            this.$refs.WriteOffReviewerModal.selectionRows = res.result
          })
          this.$refs.WriteOffReviewerModal.writeOffReviewerVisible = true
        }

      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>