<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="顾客">
              <j-search-select-tag placeholder="请选择顾客" v-model="queryParam.customerId" dict="cm_customer_profile where  del_flag=0,company_name,id"/>
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
<!--      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>-->
      <a-button @click="hSends" type="primary" v-has="'fundAccount'">退款</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('资金账户表')">导出</a-button>
<!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
<!--        <a-button type="primary" icon="import">导入</a-button>-->
<!--      </a-upload>-->
<!--      <a-dropdown v-if="selectedRowKeys.length > 0">-->
<!--        <a-menu slot="overlay">-->
<!--          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>-->
<!--        </a-menu>-->
<!--        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>-->
<!--      </a-dropdown>-->
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        :scroll="{x:true}"
        bordered
        rowKey="customerId"
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
          <img v-else :src="getImgView(text)" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>
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
<!--
          <a @click="handleEdit(record)">编辑</a>
          <a @click="handleDetail(record)">详情</a>
-->
<!--

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
-->
<!--           <a @click="handleAddSettle(record)" v-has="'fundAccount'">资金流水详情</a>-->
           <a @click="financeDetail(record)" v-has="'fundAccount'">财务明细详情</a>
        </span>

      </a-table>
    </div>


    <j-modal
      :visible.sync="modal.visible"
      :width="500"
      :title="modal.title"
      :fullscreen.sync="modal.fullscreen"
      :switchFullscreen="modal.switchFullscreen"
      :okClose = "modal.okClose"
      @ok = "handleOk"
      @cancel="handleCancel"

    >

      <div>
        <template>
          <div>
             退款客户: <a> {{customer}}</a>
          </div>
          <div style="height: 50px"></div>
          <div>
            <a-form layout="inline">
              <a-row :gutter="24">
                <a-col :span="24">
                  <a-form-item label="退款">
                    <a-input :disabled="amount == 0" placeholder="请输入退款金额" v-model="selectValue"></a-input>
                  </a-form-item>
                  <a-form-item v-has="'fm:receiptCode'" label="收据编号">
                    <a-input :disabled="amount == 0" placeholder="请输入收据编号" v-model="receiptCode"></a-input>
                  </a-form-item>
                  <a-form-item label="备注">
                    <a-input :disabled="amount == 0" placeholder="请输入备注" v-model="remarks"></a-input>
                  </a-form-item>
                </a-col>
              </a-row>
            </a-form>
          </div>
          <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
            <i class="anticon anticon-info-circle ant-alert-icon"></i> 可退金额：<a style="font-weight: 600">{{ amount }}</a> 元
          </div>
        </template>
      </div>
    </j-modal>

    <fund-account-modal ref="modalForm" @ok="modalFormOk"></fund-account-modal>
    <fund-detail-modal ref="modalFormDetail" @ok="modalFormOk"></fund-detail-modal>
    <FinanceDetailModal ref="modalFinanceDetail" @ok="modalFormOK"></FinanceDetailModal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import FundAccountModal from './modules/FundAccountModal'
  import {getAction, postAction} from "../../../api/manage";
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import JDate from '@/components/jeecg/JDate.vue'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import FundDetailModal from "./modules/FundDetailModal"
  import FinanceDetailModal from "./modules/FinanceDetailModal"
  import { colAuthFilter } from "@/utils/authFilter"

  export default {
    name: 'FundAccountList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      FundAccountModal,
      JDictSelectTag,
      JDate,
      JSearchSelectTag,
      FundDetailModal,
      FinanceDetailModal
    },
    data () {
      return {
        dataSource: [],
        modal: {
          title: '退款信息',
          visible: false,
          fullscreen: false,
          switchFullscreen: false,
          okClose: false,
        },
        selectedRowKeys:[],
        selectValue: '',
        receiptCode: '',
        remarks: '',
        customer: '',
        amount:0,
        description: '资金账户表管理页面',
        // 表头
        columns: [
          {
            title: '序号',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title:'顾客名称',
            align:"center",
            dataIndex: 'customerName'
          },
          {
            title:'总余额',
            align:"center",
            dataIndex: 'totalAmount'
          },
          {
            title:'来款可用余额',
            align:"center",
            dataIndex: 'incomeAccount'
          },
          {
            title:'来款可用余额',
            align:"center",
            dataIndex: 'incomeAccountAndFrozenAccount'
          },
          {
            title:'授信可用余额',
            align:"center",
            dataIndex: 'creditAccount'
          },
          {
            title:'冻结金额',
            align:"center",
            dataIndex: 'frozenAccount'
          },
          {
            title:'预用金额',
            align:"center",
            dataIndex: 'preUseAccount'
          },
          {
            title:'客户实际总金额',
            align:"center",
            dataIndex: 'availableAndPreUseAccount'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/fm/fundAccount/list",
          delete: "/fm/fundAccount/delete",
          deleteBatch: "/fm/fundAccount/deleteBatch",
          exportXlsUrl: "/fm/fundAccount/exportXls",
          importExcelUrl: "fm/fundAccount/importExcel",
          refundUpdate: "fm/fundAccount/refundUpdate",
          queryList: "fm/fundAccount/queryList",

        },
        dictOptions:{},
        /* 分页参数 */
        ipagination:{
          current: 1,
          pageSize: 300,
          pageSizeOptions: ['300','600'],
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
      this.columns = colAuthFilter(this.columns,'fm:');
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig(){
      },
      handleAddSettle(record) {
        this.$refs['modalFormDetail'].detailOpen(record);
      },
      financeDetail(record)  {
        this.$refs['modalFinanceDetail'].detailOpen(record);
      },
      handleOk() {
        if(this.selectValue == undefined){
          this.$message.warning("请输入退款金额！")
        }else {
          console.log("aaaa---",this.selectionRows[0].customerId)
          let params = {
            id : this.selectionRows[0].customerId,
            refundAmount : this.selectValue,
            receiptCode : this.receiptCode,
            remarks : this.remarks
          };
          getAction(this.url.refundUpdate,params).then(res=>{
            if(res.success){
              this.$message.success("冻结成功")
              this.loadData();
            }else{
              this.$message.warning(res.message)
            }
          });
        }
        this.selectedRowKeys = [];
        this.selectValue = '';
        this.remarks = '';
        this.receiptCode = '';
        this.modal.visible=false;
      },
      handleCancel(){
        this.selectValue = '';
        this.remarks = '';
        this.receiptCode = '';
        this.selectedRowKeys = [];
        this.modal.visible=false;
      },
      hSends(){
        if(this.selectedRowKeys.length == 0 ){
          this.$message.warning("请勾选要退款的顾客！")
        }else if (this.selectedRowKeys.length > 1){
          this.$message.warning("只能勾选一条退款信息！")
        }else {
          this.modal.visible=true;
        }
        console.log("111",this.selectionRows);
        this.amount = this.selectionRows[0].incomeAccount;
        this.customer = this.selectionRows[0].customerName;
      },
      searchQuery() {
        this.loadData1(1);
      },
      loadData1(arg) {
        if(!this.url.list){
          this.$message.error("请设置url.list属性!")
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        var params = this.getQueryParams();//查询条件.
        console.log('*-------params',params)
        this.loading = true;
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            console.log('res.result.records---',res.result.records)
            this.dataSource = res.result.records;
            this.ipagination.total = res.result.total;
          }
          if(res.code!=200){
            this.$message.warning(res.message)
          }
          this.loading = false;
        })
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>