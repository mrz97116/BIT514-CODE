<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="客户名称">
              <j-search-select-tag placeholder="请选择客户名称" v-model="queryParam.customerId" dict="cm_customer_profile where  del_flag=0,company_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="创建日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开始时间" class="query-group-cust" v-model="queryParam.createTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择结束时间" class="query-group-cust" v-model="queryParam.createTime_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="合同状态">
              <j-search-select-tag placeholder="请输入合同状态" v-model="queryParam.contractFlag" dict="contract_flag"></j-search-select-tag>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="目的地">
              <a-input placeholder="请输入目的地" v-model="queryParam.destination"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="合同编号">
              <a-input placeholder="请输入合同编号" v-model="queryParam.contractNo"></a-input>
            </a-form-item>
          </a-col>
<!--
          <template v-if="toggleSearchStatus">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="目的地">
                <a-input placeholder="请输入目的地" v-model="queryParam.destination"></a-input>
              </a-form-item>
            </a-col>
          </template>
-->
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
      <a-button @click="handleAdd" type="primary" icon="plus">新增合同</a-button>
      <a-button @click="rpPrint()" type="primary" icon="plus">打印合同</a-button>
<!--      <a-button type="primary" icon="download" @click="handleExportXls('合同主档表')">导出</a-button>-->
      <!--<a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
        <!--<a-button type="primary" icon="import">导入</a-button>-->
      <!--</a-upload>-->
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
<!--          <a @click="handleEdit(record)">编辑</a>-->
<!--          <a-divider type="vertical" />-->
<!--          <a @click="reportPrint()">打印</a>-->
<!--          <a-divider type="vertical" />-->
            <a @click="handleDetail(record)">详情</a>
<!--          <a-dropdown>-->
<!--            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>-->
<!--            <a-menu slot="overlay">-->
<!--              <a-menu-item>-->
<!--                <a @click="handleDetail(record)">详情</a>-->
<!--              </a-menu-item>-->
<!--              <a-menu-item>-->
<!--                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">-->
<!--                  <a>删除</a>-->
<!--                </a-popconfirm>-->
<!--              </a-menu-item>-->
<!--            </a-menu>-->
<!--          </a-dropdown>-->
        </span>

      </a-table>
    </div>
    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="合同明细" key="1">
        <ContractDetList ref="cdl" :mainId="selectedMainId" :parentRecord="selectionRows[0]"/>
      </a-tab-pane>
    </a-tabs>

    <contract-modal ref="modalForm" @ok="modalFormOk"/>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import ContractModal from './modules/ContractModal'
  import JDate from '@/components/jeecg/JDate.vue'
  import '@/assets/less/TableExpand.less'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import common from '@views/base/common/common.js'
  import reportConfig from '@/views/report_config.json'
  import ContractDetList from './ContractDetList'

  export default {
    name: "ContractList",
    mixins:[JeecgListMixin],
    components: {
      JDate,
      ContractModal,
      JSearchSelectTag,
      ContractDetList
    },
    data () {
      return {
        description: '合同主档表管理页面',
        selectedMainId: '',
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
            title:'创建日期',
            align:"center",
            dataIndex: 'createTime'
          },
          {
            title:'客户名称',
            align:"center",
            dataIndex: 'customerId_dictText'
          },
          {
            title:'合同编号',
            align:"center",
            dataIndex: 'contractNo'
          },
          {
            title:'订货总重量',
            align:"center",
            dataIndex: 'orderWt'
          },
          {
            title:'总价',
            align:"center",
            dataIndex: 'totalPrice'
          },
          {
            title:'合同比例',
            align:"center",
            dataIndex: 'rate'
          },
          {
            title:'目的地',
            align:"center",
            dataIndex: 'destination'
          },
          {
            title:'合同状态',
            align:"center",
            dataIndex: 'contractFlag_dictText'
          },
          {
            title:'备注',
            align:"center",
            dataIndex: 'remark'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/om/contract/list",
          delete: "/om/contract/delete",
          deleteBatch: "/om/contract/deleteBatch",
          exportXlsUrl: "/om/contract/exportXls",
          importExcelUrl: "om/contract/importExcel",

        },
        dictOptions:{},
      }
    },
    created() {
    },
    mounted() {
      this.loadReportConfig()
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
      initDictConfig(){
      },
      rpPrint(){
        if (this.selectionRows == null || this.selectedRowKeys.length === 0) {
          return this.$message.warning('请勾选记录！')
        }
        let reportUrl = reportConfig.reportUrl.om.contract
        this.reportPrint(this.selectedRowKeys,-1,reportUrl)
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedMainId = selectedRowKeys[0]
        this.selectedRowKeys = selectedRowKeys
        this.selectionRows = selectionRows
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>