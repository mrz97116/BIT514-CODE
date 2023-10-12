<template>
  <a-card :bordered="false" :class="'cust-erp-sub-tab'">
    <!-- 操作按钮区域 -->
    <div class="table-operator" v-if="mainId">
<!--      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>-->
      <!--      <a-button type="primary" icon="download" @click="handleExportXls('商务条款')">导出</a-button>-->
      <!--      <a-upload-->
      <!--        name="file"-->
      <!--        :showUploadList="false"-->
      <!--        :multiple="false"-->
      <!--        :headers="tokenHeader"-->
      <!--        :action="importExcelUrl"-->
      <!--        @change="handleImportExcel">-->
      <!--          <a-button type="primary" icon="import">导入</a-button>-->
      <!--      </a-upload>-->
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
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a
        style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
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
<!--          <a @click="handleEdit(record)">编辑</a>-->
          <!--          <a-divider type="vertical" />-->
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a>删除</a>
          </a-popconfirm>
        </span>

      </a-table>
    </div>

    <provision-modal ref="modalForm" @ok="modalFormOk" :mainId="mainId" :parentRecord="parentRecord"></provision-modal>
  </a-card>
</template>

<script>

import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import ProvisionModal from './modules/ProvisionModal'
import { filterDictTextByCache } from '@/components/dict/JDictSelectUtil'

export default {
  name: 'ProvisionList',
  mixins: [JeecgListMixin],
  components: { ProvisionModal },
  props: {
    mainId: {
      type: String,
      default: '',
      required: false
    },
    parentRecord: {
      type: Object,
      default: function () {
        return {}
      },
      required: false
    }
  },
  watch: {
    mainId: {
      immediate: true,
      handler(val) {
        if (!this.mainId) {
          this.clearList()
        } else {
          this.url.list ='/om/order/queryProvisionByMainId?id='+ val
          this.loadData(1)
        }
      }
    }
  },
  data() {
    return {
      description: '订单主表管理页面',
      disableMixinCreated: true,
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
          title: '条款类型',
          align: 'center',
          dataIndex: 'provisionType',
          customRender: (text) => {
            return filterDictTextByCache('provision_type', text)
          }
        },
        {
          title: '条款内容',
          align: 'center',
          dataIndex: 'provisionContent'
        },
        {
          title: '生效标识',
          align: 'center',
          dataIndex: 'validityStatus_dictText',
        },
        // {
        //   title:'租户id',
        //   align:"center",
        //   dataIndex: 'tenantId'
        // },
        // {
        //   title:'逻辑删除',
        //   align:"center",
        //   dataIndex: 'delFlag'
        // },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          fixed: 'right',
          width: 147,
          scopedSlots: { customRender: 'action' },
        }
      ],
      url: {
        list: '/om/order/queryProvisionByMainId',
        delete: '/om/order/deleteProvision',
        deleteBatch: '/om/order/deleteBatchProvision',
        exportXlsUrl: '/om/order/exportProvision',
        importUrl: '/om/order/importProvision',
      },
      dictOptions: {
        orderType: [],
        productCode: [],
      },

    }
  },
  computed: {
    importExcelUrl() {
      return `${window._CONFIG['domianURL']}/${this.url.importUrl}/${this.mainId}`
    }
  },
  methods: {
    clearList() {
      this.dataSource = []
      this.selectedRowKeys = []
      this.ipagination.current = 1
    },

  }
}
</script>
<style scoped>
@import '~@assets/less/common.less'
</style>
