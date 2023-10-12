<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <!-- <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button> -->
      <a-button type="primary" icon="download" @click="handleExportXls('货物调拨表')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <!-- <a-button type="primary" icon="import">导入</a-button> -->
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

          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a v-has="'sm:add'">删除</a>
          </a-popconfirm>

        </span>

      </a-table>
    </div>

    <inventory-allot-modal ref="modalForm" @ok="modalFormOk"></inventory-allot-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import InventoryAllotModal from './modules/InventoryAllotModal'

  export default {
    name: 'InventoryAllotList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      InventoryAllotModal,
    },
    data () {
      return {
        description: '货物调拨表管理页面',
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
            title:'创建人',
            align:"center",
            dataIndex: 'createBy_dictText'
          },
          {
            title:'创建日期',
            align:"center",
            dataIndex: 'createTime'
          },
          {
            title:'调拨单号',
            align:"center",
            dataIndex: 'allotNum'
          },
          {
            title:'发货仓',
            align:"center",
            dataIndex: 'allotStockHouseId_dictText',

          },
          {
            title:'接收仓',
            align:"center",
            dataIndex: 'receiveStockHouseId_dictText'
          },
          {
            title:'库位',
            align:"center",
            dataIndex: 'stockLocation'
          },
          {
            title:'调拨数量合计',
            align:"center",
            dataIndex: 'allotNumberSum'
          },
          {
            title:'调拨重量合计',
            align:"center",
            dataIndex: 'allotWeightSum'
          },
          {
            title:'调拨时间',
            align:"center",
            dataIndex: 'allotCreateTime',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          },
          {
            title:'牌号',
            align:"center",
            dataIndex: 'sgSign'
          },
          {
            title:'材料号',
            align:"center",
            dataIndex: 'matNo'
          },
          {
            title:'材料长',
            align:"center",
            dataIndex: 'matLen'
          },
          {
            title:'材料宽',
            align:"center",
            dataIndex: 'matWidth'
          },
          {
            title:'材料厚',
            align:"center",
            dataIndex: 'matChick'
          },
          {
            title:'品名中文',
            align:"center",
            dataIndex: 'prodCname'
          },
          {
            title:'卸货人名字',
            align:"center",
            dataIndex: 'dischargerName'
          },
          {
            title:'备注',
            align:"center",
            dataIndex: 'remarks'
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
          list: "/sm/inventoryAllot/list",
          delete: "/sm/inventoryAllot/delete",
          deleteBatch: "/sm/inventoryAllot/deleteBatch",
          exportXlsUrl: "/sm/inventoryAllot/exportXls",
          importExcelUrl: "sm/inventoryAllot/importExcel",
          
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