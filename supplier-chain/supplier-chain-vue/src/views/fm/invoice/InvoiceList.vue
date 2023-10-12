<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="账户名称">
              <a-input placeholder="请输入账户名称" v-model="queryParam.accountType"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="结算单编号">
              <a-input placeholder="请输入结算单编号" v-model="queryParam.notes"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="顾客">
              <j-search-select-tag placeholder="请选择顾客" v-model="queryParam.customerId" dict="cm_customer_profile where  del_flag=0,company_name,id"/>
            </a-form-item>
          </a-col>

          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="单据日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开始时间" class="query-group-cust"
                      v-model="queryParam.createTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择结束时间" class="query-group-cust"
                      v-model="queryParam.createTime_end"></j-date>
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
      <a-button type="primary" icon="download" @click="handleExportXls('发票信息')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
        <span style="margin-left: 5%">合计金额：{{this.selectParams.invoiceMakeMoney}}</span>
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
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <invoice-modal ref="modalForm" @ok="modalFormOk"/>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import InvoiceModal from './modules/InvoiceModal'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import '@/assets/less/TableExpand.less'
  import JDate from '@/components/jeecg/JDate.vue'
  import ACol from 'ant-design-vue/es/grid/Col'

  export default {
    name: "InvoiceList",
    mixins:[JeecgListMixin],
    components: {
      JSearchSelectTag,
      InvoiceModal,
      JDate,
      ACol
    },
    data () {
      return {
        description: '发票信息管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title:'账户名称',
            align:"center",
            dataIndex: 'accountType'
          },
          {
            title:'顾客',
            align:"center",
            dataIndex: 'customerId_dictText'
          },
          {
            title:'审核状态',
            align:"center",
            dataIndex: 'status_dictText'
          },
          {
            title:'总金额',
            align:"center",
            dataIndex: 'amount'
          },
/*
          {
            title:'租户id',
            align:"center",
            dataIndex: 'tenantId'
          },
*/
          {
            title:'单号',
            align:"center",
            dataIndex: 'hdorderno'
          },
          {
            title:'单据日期',
            align:"center",
            dataIndex: 'lasttime',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          },
          {
            title:'购买方',
            align:"center",
            dataIndex: 'receiverName'
          },
          {
            title:'税号',
            align:"center",
            dataIndex: 'taxnum'
          },
          {
            title:'地址电话',
            align:"center",
            dataIndex: 'addresstel'
          },
          {
            title:'银行账户',
            align:"center",
            dataIndex: 'bankinfo'
          },
          {
            title:'合计金额',
            align:"center",
            dataIndex: 'invoiceMakeMoneyString'
          },
          {
            title:'操作员',
            align:"center",
            dataIndex: 'checker1'
          },
          {
            title:'备注',
            align:"center",
            dataIndex: 'notes'
          },
          {
            title:'发票号',
            align:"center",
            dataIndex: 'invoiceno'
          },
          {
            title:'专票与普票识别参数 =s',
            align:"center",
            dataIndex: 'billtype'
          },
/*
          {
            title:'结算id',
            align:"center",
            dataIndex: 'settleid'
          },
          {
            title:'删除标志',
            align:"center",
            dataIndex: 'delFlag'
          },
*/
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/fm/invoice/list",
          delete: "/fm/invoice/delete",
          deleteBatch: "/fm/invoice/deleteBatch",
          exportXlsUrl: "/fm/invoice/exportXls",
          importExcelUrl: "fm/invoice/importExcel",

        },
        selectParams:{
          invoiceMakeMoney:0
        },
        dictOptions:{},
      }
    },
    created() {
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
      initDictConfig(){
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedMainId = selectedRowKeys[0]
        this.selectedRowKeys = selectedRowKeys
        this.selectionRows = selectionRows
      }
    },
    watch: {
      'dataSource':function() {
        for(let i =0;i<this.dataSource.length;i++) {
          debugger
          this.dataSource[i].invoiceMakeMoneyString = (this.dataSource[i].invoiceMakeMoney.toFixed(2) + '').replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,');
        }
      },
      'selectionRows': function (selectionRows) {
        if (selectionRows) {
          let rows = selectionRows
          let invoiceMakeMoney = 0
          for (let i = 0; i < rows.length; i++) {
            invoiceMakeMoney += rows[i].invoiceMakeMoney;
          }
          this.selectParams.invoiceMakeMoney = invoiceMakeMoney.toFixed(2).replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,');
        }
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>