<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="订单编号">
              <a-input placeholder="请输入订单编号" v-model="queryParam.orderNo"></a-input>
            </a-form-item>
          </a-col>

          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="订单性质">
              <j-dict-select-tag placeholder="请选择订单性质" v-model="queryParam.orderType" dictCode="order_type"/>
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
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button @click="contractAdd" type="primary">生成合同</a-button>
      <!--<a-button type="primary" icon="download" @click="handleExportXls('订单主表')">导出</a-button>-->
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
        <template slot="pcaSlot" slot-scope="text">
          <div>{{ getPcaText(text) }}</div>
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

    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="订单明细表" key="1">
        <OrderDetList :mainId="selectedMainId" :parentRecord="selectionRows[0]"/>
      </a-tab-pane>
      <a-tab-pane tab="商务条款" key="2" forceRender>
        <ProvisionList :mainId="selectedMainId" :parentRecord="selectionRows[0]"/>
      </a-tab-pane>
    </a-tabs>

    <order-modal ref="modalForm" @ok="modalFormOk"/>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import OrderModal from './modules/OrderModal'
  import OrderDetList from './OrderDetList'
  import ProvisionList from '@/views/om/ProvisionBase/ProvisionList'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import Area from '@/components/_util/Area'
  import { getAction, postAction } from '@/api/manage'
  import '@/assets/less/TableExpand.less'
  import {filterDictTextByCache} from '@/components/dict/JDictSelectUtil'

  export default {
    name: "OrderList",
    mixins:[JeecgListMixin],
    components: {
      JDictSelectTag,
      OrderModal,
      OrderDetList,
      ProvisionList
    },
    data () {
      return {
        description: '订单主表管理页面',
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
            title:'订单编号',
            align:"center",
            dataIndex: 'orderNo'
          },
          {
            title:'订单性质',
            align:"center",
            dataIndex: 'orderType_dictText'
          },
          {
            title:'目的地',
            align:"center",
            dataIndex: 'desCity',
            scopedSlots: {customRender: 'pcaSlot'}
          },
          {
            title:'产品大类',
            align:"center",
            dataIndex: 'productCode',
            customRender: function (text){
              return filterDictTextByCache('prod_code', text)
            }
          },
          {
            title:'订单总重量',
            align:"center",
            dataIndex: 'orderWt'
          },
          {
            title:'总价',
            align:"center",
            dataIndex: 'totalPrice'
          },
          {
            title:'备注',
            align:"center",
            dataIndex: 'remark'
          },
          {
            title:'顾客名称',
            align:"center",
            dataIndex: 'customerId'
          },
          // {
          //   title:'顾客名称',
          //   align:"center",
          //   dataIndex: 'customerText'
          // },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/om/order/list",
          delete: "/om/order/delete",
          deleteBatch: "/om/order/deleteBatch",
          exportXlsUrl: "/om/order/exportXls",
          importExcelUrl: "om/order/importExcel",
          contractAdd: '/om/contract/addContract'
        },
        selectedMainId: '',
        dictOptions:{},
      }
    },
    created() {
      this.pcaData = new Area()
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
      getPcaText(code){
        return this.pcaData.getText(code);
      },
      initDictConfig(){
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedMainId = selectedRowKeys[0]
        this.selectedRowKeys = selectedRowKeys
        this.selectionRows = selectionRows
        console.log('selectionRows', this.selectionRows)
      },
      contractAdd() {
        getAction(this.url.contractAdd, { orderNo: this.selectionRows[0].orderNo }).then((res) => {
          if (res.success) {
            this.$message.success('订单生成合同成功')
          }
          if (res.code === 510) {
            this.$message.warning(res.message)
          }
          this.loading = false
        })

      },

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>