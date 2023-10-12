<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="仓库">
              <j-dict-select-tag placeholder="请输入仓库" v-model="queryParam.stockId" dictCode="sm_stock,name,id"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="提单编号">
              <a-input placeholder="请输入提单编号" v-model="queryParam.saleBillNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="品名中文">
              <j-input placeholder="请输入品名中文" v-model="queryParam.prodCname"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="车号">
              <j-input placeholder="请输入车号" v-model="queryParam.carNo"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="产地">
              <a-input placeholder="请输入产地" v-model="queryParam.placeOfOrigin"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="牌号">
              <a-input placeholder="请输入牌号" v-model="queryParam.sgSign"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="出库类型">
              <a-input placeholder="请输入出库类型" v-model="queryParam.outType"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="装车单编号">
              <a-input placeholder="请输入装车单编号" v-model="queryParam.stockNo"></a-input>
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
      <!--<a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>-->
      <a-button type="primary" icon="download" @click="handleExportXls('结算单导入')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tenantTokenHeader" :action="importExcelUrl"
                @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-dropdown v-if="selectedRowKeys.length > 0">
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
        :customCell="changeCellProps"
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

      </a-table>
    </div>

    <settle-import-modal ref="modalForm" @ok="modalFormOk"></settle-import-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import SettleImportModal from './modules/SettleImportModal'
  import JInput from '@/components/jeecg/JInput'
  import JDate from '@/components/jeecg/JDate.vue'


  export default {
    name: 'SettleImportList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      SettleImportModal,
      JDate,
      JInput
    },
    data() {
      return {
        description: '结算单导入管理页面',
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
            title: '仓库',
            align: 'center',
            dataIndex: 'stockId'
          },
          {
            title: '创建日期',
            align: 'center',
            dataIndex: 'createTime',
            sorter: true
          },
          {
            title: '提单编号',
            align: 'center',
            dataIndex: 'saleBillNo'
          },
          {
            title: '品名中文',
            align: 'center',
            dataIndex: 'prodCname'
          },
          {
            title: '车号',
            align: 'center',
            dataIndex: 'carNo'
          },
          {
            title: '规格',
            align: 'center',
            dataIndex: 'custMatSpecs'
          },
          {
            title: '产地',
            align: 'center',
            dataIndex: 'placeOfOrigin'
          },
          {
            title: '数量',
            align: 'center',
            dataIndex: 'qty'
          },
          {
            title: '重量',
            align: 'center',
            dataIndex: 'weight'
          },
          {
            title: '牌号',
            align: 'center',
            dataIndex: 'sgSign'
          },
          {
            title: '出库类型',
            align: 'center',
            dataIndex: 'outType'
          },
          // {
          //   title: '磅计重量',
          //   align: 'center',
          //   dataIndex: 'realityWeight'
          // },
          // {
          //   title: '价格',
          //   align: 'center',
          //   dataIndex: 'price',
          //   customCell:this.changeCellProps
          // },
          // {
          //   title: '理计价格',
          //   align: 'center',
          //   dataIndex: 'realityPrice'
          // },
          // {
          //   title: '装车单编号',
          //   align: 'center',
          //   dataIndex: 'stockNo'
          // },
        ],
        url: {
          list: '/fm/settleImport/list',
          delete: '/fm/settleImport/delete',
          deleteBatch: '/fm/settleImport/deleteBatch',
          exportXlsUrl: '/fm/settleImport/exportXls',
          importExcelUrl: 'fm/settleImport/settleImportExcel',

        },
        dictOptions: {},
      }
    },
    created() {
    },
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      },
    },
    methods: {
      initDictConfig() {
      },
      changeCellProps(record){
        if(record.price > (record.realityPrice + record.realityPrice*0.07)){
          return this.changeCellFontColor("价格",record)
        }
      },
      changeCellFontColor(name,record){
        return{
          style:{'color':'red'}
        }
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>