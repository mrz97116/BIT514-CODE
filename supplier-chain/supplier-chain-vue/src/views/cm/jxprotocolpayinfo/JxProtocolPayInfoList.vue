<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">



        <a-col :xl="6" :lg="7" :md="8" :sm="24">
          <a-form-item label="对方户名">
            <a-input placeholder="请输入对方户名" v-model="queryParam.accountName" ></a-input>
          </a-form-item>
        </a-col>

        <a-col :span="9">
          <a-form-item label="账户明细编号-交易流水号">
            <a-input placeholder="请输入账户明细编号-交易流水号" v-model="queryParam.serialNumber" ></a-input>
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
      <a-button @click="handleAdd" v-has="'cmustomer:add'" type="primary" icon="plus" >新增</a-button>
<!--      <a-button type="primary" icon="download" @click="handleExportXls('经销支付信息')">导出</a-button>-->
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">

        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button v-has="'cmustomer:add'" style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
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
          <a v-has="'cmustomer:add'" @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a v-has="'cmustomer:add'">删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <jx-protocol-pay-info-modal ref="modalForm" @ok="modalFormOk"></jx-protocol-pay-info-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JxProtocolPayInfoModal from './modules/JxProtocolPayInfoModal'

  export default {
    name: 'JxProtocolPayInfoList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      JxProtocolPayInfoModal
    },
    data () {
      return {
        description: '经销支付信息管理页面',
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
            title:'账号',
            align:"center",
            dataIndex: 'accountNumber'
          },
          {
            title:'账号名称',
            align:"center",
            dataIndex: 'numberName'
          },
          {
            title:'交易时间',
            align:"center",
            dataIndex: 'traTime',
          },
          {
            title:'借方发生额（支取）',
            align:"center",
            dataIndex: 'debitDraw'
          },
          {
            title:'贷方发生额（收入）',
            align:"center",
            dataIndex: 'creditIncome'
          },
          {
            title:'余额',
            align:"center",
            dataIndex: 'balance'
          },
          {
            title:'币种',
            align:"center",
            dataIndex: 'currency'
          },
          {
            title:'对方户名',
            align:"center",
            dataIndex: 'accountName'
          },
          {
            title:'对方账号',
            align:"center",
            dataIndex: 'oppositeNumber'
          },
          {
            title:'对方开户机构',
            align:"center",
            dataIndex: 'mechanism'
          },
          {
            title:'记账日期',
            align:"center",
            dataIndex: 'bookkeepingDate',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          },
          {
            title:'摘要',
            align:"center",
            dataIndex: 'abstractt'
          },
            {
                title:'备注',
                align:"center",
                dataIndex: 'remark'
            },
          {
            title:'账户明细编号-交易流水号',
            align:"center",
            dataIndex: 'serialNumber'
          },
          {
            title:'企业流水号',
            align:"center",
            dataIndex: 'enterpriseNumber'
          },
          {
            title:'凭证种类',
            align:"center",
            dataIndex: 'voucherType'
          },
          {
            title:'凭证号',
            align:"center",
            dataIndex: 'voucherNo'
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
          list: "/cm/jxProtocolPayInfo/list",
          delete: "/cm/jxProtocolPayInfo/delete",
          deleteBatch: "/cm/jxProtocolPayInfo/deleteBatch",
          exportXlsUrl: "/cm/jxProtocolPayInfo/exportXls",
          importExcelUrl: "cm/jxProtocolPayInfo/importExcel",

        },
        dictOptions:{},
      }
    },
    created() {
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig(){
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>