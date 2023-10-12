<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="顾客">
              <j-search-select-tag placeholder="请选择顾客" v-model="queryParam.customer" dict="cm_customer_profile where  del_flag=0,company_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="结算单编号">
              <j-input placeholder="请输入结算单编号" v-model="queryParam.settleNo"></j-input>
            </a-form-item>
          </a-col>
          <a-col v-has="'fm:saleBillNo'" :xl="6" :lg="7" :md="8" :sm="24" >
            <a-form-item label="提单编号">
              <j-input placeholder="请输入结算单编号" v-model="queryParam.saleBillNo"></j-input>
            </a-form-item>
          </a-col>
          <a-col v-has="'fm:stackingNo'" :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="装车单编号">
              <j-input placeholder="请输入结算单编号" v-model="queryParam.stackingNo"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="创建日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开始时间" class="query-group-cust"
                      v-model="queryParam.createTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择结束时间" class="query-group-cust"
                      v-model="queryParam.createTime_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col v-has="'fm:shipDate'" :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="装车日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开始时间" class="query-group-cust"
                      v-model="queryParam.shipDate_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择结束时间" class="query-group-cust"
                      v-model="queryParam.shipDate_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24" >
            <a-form-item label="产品大类">
              <j-dict-select-tag placeholder="请选择产品大类" v-model="queryParam.prodCode1" dictCode="prod_code"/>
            </a-form-item>
          </a-col>
          <a-col v-has="'fm:createBy'" :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="开单人">
              <j-search-select-tag placeholder="请选择创建人" v-model="queryParam.createBy"
                                   :dict="createByDict"></j-search-select-tag>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="发票状态">
              <j-dict-select-tag placeholder="请选择发票状态" v-model="queryParam.invoiceStatus"
                                 dictCode="invoice_status"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="结算单状态">
              <j-dict-select-tag placeholder="请选择结算状态" v-model="queryParam.statementState"
                                 dictCode="statement_state"></j-dict-select-tag>
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
      <a-button @click="handleAddSettle" type="primary" icon="plus" v-has="'settleInfo'">新增结算单</a-button>
      <a-button @click="rpPrint" type="primary" v-has="'fm:printingSettleInfo'">打印结算单</a-button>
      <a-button @click="invoiceApply('invoiceApply')" type="primary" icon="plus" v-has="'settleInfo'" :loading="loading1">生成发票申请</a-button>
      <a-button v-has="'invoiceMerge'" @click="invoiceApply('invoiceMerge')" type="primary" icon="plus" :loading="loading1">生成发票申请(合并)</a-button>
      <a-button @click="invoiceApplyForOne" type="primary" icon="plus" v-has="'settleInfo'" :loading="loading2">一键生成发票</a-button>
      <a-button @click="deleteSettle" type="primary" icon="minus" v-has="'deleteSettleInfo'">删除结算单</a-button>
<!--      <a-button @click="open" type="primary" v-has="'writeOff'">红冲</a-button>-->
      <a-button type="primary" icon="download" @click="handleExportXls('结算单信息')" v-has="'settleInfo'">导出</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <!--        <a-menu slot="overlay">-->
        <!--          <a-menu-item key="1" @click="batchDel" v-has="'settleInfo'"><a-icon type="delete"/>删除</a-menu-item>-->
        <!--        </a-menu>-->
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
        <a style="margin-left: 60%" > 结算金额合计：{{this.settleAccount}}</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        :scroll="{x:true}"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        class="j-table-force-nowrap"
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
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"/>
          <a @click="handleDetail(record)">详情</a>

        </span>

      </a-table>
    </div>


    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="结算单明细" key="1">
        <StackDetList :mainId="selectedMainId" :parentRecord="selectionRows[0]"/>
      </a-tab-pane>
    </a-tabs>

    <div>
      <j-modal :visible=this.modal.switch @ok="close(0)"
               width="80%"
               @cancel="close">
        <a-tabs defaultActiveKey="1">
          <a-tab-pane tab="结算单明细" key="1">
            <StackDetList :mainId="selectedMainId" :parentRecord="selectionRows[0]"/>
          </a-tab-pane>
        </a-tabs>
      </j-modal>
    </div>

    <settle-info-modal ref="modalForm" @ok="modalFormOk"></settle-info-modal>
    <settle-stack-info-modal ref="modalFormStack" @ok="modalFormOk"></settle-stack-info-modal>

  </a-card>
</template>

<script>

import '@/assets/less/TableExpand.less'
import {mixinDevice} from '@/utils/mixin'
import {JeecgListMixin} from '@/mixins/JeecgListMixin'
import SettleInfoModal from './modules/SettleInfoModal'
import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
import JDate from '@/components/jeecg/JDate.vue'
import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
import {filterDictTextByCache} from '@/components/dict/JDictSelectUtil'
import SettleStackInfoModal from "./modules/SettleStackInfoModal";
import reportConfig from '@/views/report_config.json'
import {getAction, postAction} from '../../../api/manage'
import StackDetList from '@/views/sm/stack/StackDetList'
import JInput from '@/components/jeecg/JInput'
import {colAuthFilter} from "@/utils/authFilter"


export default {
  name: 'SettleInfoList',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
    SettleStackInfoModal,
    JDictSelectTag,
    JDate,
    JInput,
    JSearchSelectTag,
    SettleInfoModal,
    StackDetList
  },
  data() {
    return {
      loading1: false,
      loading2: false,
      rpName: '',
      reportUrl: '',
      createByDict: '',
      description: '结算单信息管理页面',
      settleAccount: 0,
      //弹窗
      modal: {
        title: '装车单信息',
        visible: false,
        fullscreen: true,
        switchFullscreen: true,
        okClose: false,
        switch: false
      },
      stackDataSource: [],
      selectedMainId: '',
      // 表头
      columns: [
        {
          title: '序号',
          dataIndex: '',
          key: 'rowIndex',
          width: 60,
          align: "center",
          customRender: function (t, r, index) {
            return parseInt(index) + 1;
          }
        },
        {
          title: '开单人',
          align: "center",
          dataIndex: 'createBy'
        },
        {
          title: '创建日期',
          align: "center",
          dataIndex: 'createTime'
        },
        {
          title: '装车日期',
          align: "center",
          dataIndex: 'shipDate'
        },
        {
          title: '单据状态',
          align: "center",
          dataIndex: 'status',
          customRender: function (text) {
            return filterDictTextByCache('settle_status', text)
          }
        },
        {
          title: '发票状态',
          align: "center",
          dataIndex: 'invoiceStatus_dictText'
        },
        {
          title: '结算单编号',
          align: "center",
          dataIndex: 'settleNo'
        },
        {
          title: '提单编号',
          align: "center",
          dataIndex: 'saleBillNo'
        },
        {
          title: '装车单编号',
          align: "center",
          dataIndex: 'stackingNo'
        },
        {
          title: '顾客',
          align: "center",
          dataIndex: 'customer_dictText'
        },
        {
          title:'产品大类',
          align:"center",
          dataIndex: 'prodCode_dictText',
        },
        {
          title: '每吨折扣',
          align: "center",
          dataIndex: 'discountString'
        },
        {
          title: '结算金额',
          align: "center",
          dataIndex: 'settleAmountString'
        },
        // {
        //   title:'结算单总金额',
        //   align:"center",
        //   dataIndex: 'rowTotal'
        // },
        {
          title: '结算单状态',
          align: "center",
          dataIndex: 'statementState'
        },
        {
          title: '备注',
          align: 'center',
          dataIndex: 'remark'
        },
        {
          title: '红冲编号',
          align: "center",
          dataIndex: 'writeOffNo'
        },
        {
          title: '红冲时间',
          align: "center",
          dataIndex: 'writeOffTime'
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: "center",
          fixed: "right",
          width: 147,
          scopedSlots: {customRender: 'action'}
        }
      ],

      url: {
        queryCompanyReportName: '/bd/tenantReport/queryCompanyReportName',

        list: "/fm/settleInfo/list",
        delete: "/fm/settleInfo/delete",
        deleteBatch: "/fm/settleInfo/deleteBatch",
        exportXlsUrl: "/fm/settleInfo/exportXls",
        importExcelUrl: "fm/settleInfo/importExcel",
        listStackDetail: "/sm/stack/listStackDetail",
        invoiceApply: "/fm/invoice/invoiceApply",
        invoiceMerge: "/fm/invoice/invoiceMerge",
        invoiceApplyForOne: "/fm/invoice/invoiceApplyForOne",
        deleteSettle: "/fm/fundAccount/deleteSettle",

      },
      dictOptions: {},
      ipagination: {
        current: 1,
        pageSize: 10,
        pageSizeOptions: ['10', '20', '100', '300', '500'],
        showTotal: (total, range) => {
          return range[0] + "-" + range[1] + " 共" + total + "条"
        },
        showQuickJumper: true,
        showSizeChanger: true,
        total: 0
      },
    }
  },
  created() {
    this.queryReportUrl();
    this.createByDict = "sys_user where del_flag = 0 and rel_tenant_ids =" + this.tenantId + ",realname,username";
    this.columns = colAuthFilter(this.columns, 'fm:');
    // this.selectStatementState();
    this.initDictConfig();
  },
  mounted() {
    this.loadReportConfig()
  },
  watch: {
    'dataSource':function() {
      let settleAccount = 0;
      for(let i =0;i<this.dataSource.length;i++) {
        this.dataSource[i].settleAmountString = this.dataSource[i].settleAmount.toFixed(2).replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,');
        this.dataSource[i].discountString = this.dataSource[i].discount.toFixed(2).replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,');
        settleAccount += this.dataSource[i].settleAmount;
      }
      this.settleAccount = (settleAccount.toFixed(2) + '').replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,');
    },
  },
  computed: {
    importExcelUrl: function () {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    },
  },
  methods: {

    open() {
      this.modal.switch = true
    },
    close(num) {
      this.modal.switch = false
      if (num == 0) {
        this.writeOff()
      }
    },

    onSelectChange(selectedRowKeys, selectionRows) {
      if (selectionRows.length > 0) {
        this.selectedMainId = selectionRows[0].stackId
      }
      this.selectedRowKeys = selectedRowKeys;
      this.selectionRows = selectionRows;
    },
    initDictConfig() {
    },
    handleAddSettle() {
      this.$refs['modalFormStack'].hOpen();
    },

    queryReportUrl() {
      let that = this
      getAction(that.url.queryCompanyReportName, {tenantId: that.tenantId}).then((res) => {
        if (res.success) {
          that.rpName = res.message
        }
      })
    },
    rpPrint() {
      if (this.selectionRows == null || this.selectedRowKeys.length === 0) {
        return this.$message.warning('请勾选记录！')
      }
      if (this.rpName !== 'empty') {
        //根据租户不同进入不同的报表
        this.reportUrl = reportConfig.reportUrl.fm.settleUrl.replace('xxx', this.rpName)
      } else {
        this.reportUrl = reportConfig.reportUrl.fm.settle
      }
      // let reportUrl = reportConfig.reportUrl.fm.settle
      this.reportPrint(this.selectedRowKeys, this.tenantId, this.reportUrl)
    },
    invoiceApply(parm) {
      let stackIds = [];
      if (this.selectionRows == null || this.selectedRowKeys.length === 0) {
        return this.$message.warning('请勾选记录！')
      }
      console.log("selectionRows:", this.selectionRows)
      this.selectionRows.forEach(function (val, index, arr) {
          stackIds.push(val.stackId);
        }
      );
      let url = parm == 'invoiceApply'?this.url.invoiceApply: this.url.invoiceMerge;
      var that = this
      that.loading1 = true;
      postAction(url, stackIds).then((res) => {
          if (res.success) {
            console.log('发票数据已生成', res)
            that.$message.success(res.result)
          }
          if (res.code === 500) {
            this.$message.warning(res.message)
          }

        }).finally(() => {
        that.loading1 = false
      });
    },
    invoiceApplyForOne() {
      var that = this;
      that.loading2 = true
      this.$confirm({
        title: "确认生成",
        content: "是否一键生成发票数据?",
        onOk: function () {
          getAction(that.url.invoiceApplyForOne).then((res) => {
            if (res.success) {
              that.$message.success("发票信息已生成");
            } else {
              that.$message.error(res.message);
            }
          }).finally(() => {
            that.loading2 = false;
            that.loadData();
          });
        }

      })
    },

    deleteSettle() {
      if (this.selectionRows == null || this.selectedRowKeys.length === 0) {
        return this.$message.warning('请勾选结算单！')
      }
      // if(this.selectedRowKeys.length > 1){
      //   return this.$message.warning('只能勾选一条结算单')
      // }
      postAction(this.url.deleteSettle, this.selectedRowKeys).then((res) => {
        if (res.success) {
          this.$message.success("删除成功！")
          this.loadData()
        }
        if (res.code === 500) {
          this.$message.warning(res.message)
        }

      })
    },
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>