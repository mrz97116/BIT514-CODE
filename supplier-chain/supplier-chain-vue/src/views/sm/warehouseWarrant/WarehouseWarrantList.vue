<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="仓库">
              <j-dict-select-tag placeholder="请选择仓库" v-model="queryParam.stockId" dictCode="sm_stock,name,id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="创建人">
              <j-search-select-tag placeholder="请选择创建人" v-model="queryParam.createBy" :dict="createByDict"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="入库单号">
              <a-input placeholder="请输入入库单号" v-model="queryParam.stockNo"></a-input>
            </a-form-item>
          </a-col>

          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="入库时间">
              <j-date placeholder="请选择开始日期" class="query-group-cust" v-model="queryParam.stockTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date placeholder="请选择结束日期" class="query-group-cust" v-model="queryParam.stockTime_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="备注">
              <a-input placeholder="请输入备注" v-model="queryParam.remark"></a-input>
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
      <a-button type="primary" icon="download" @click="handleExportXls('入库单')">导出</a-button>
      <a-button v-has="'sm:editWarehouseWarrant'" @click="editWarehouseWarrant" type="primary" icon="edit">编辑</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
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

          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a @click="handleDetail(record)">详情</a>
          <a-divider type="vertical"/>
          <a @click="delete1(record.id)">删除</a>
        </span>

      </a-table>
    </div>

    <warehouse-warrant-modal ref="modalForm" @ok="modalFormOk"></warehouse-warrant-modal>

    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="明细表" key="1">
        <WarehouseWarrantDetList ref="wwd" :mainId="selectedMainId" :parentRecord="selectionRows[0]" @primaryTableLoadData="loadData"/>
      </a-tab-pane>
    </a-tabs>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import WarehouseWarrantModal from './modules/WarehouseWarrantModal'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import JDate from '@/components/jeecg/JDate.vue'
  import WarehouseWarrantDetList from './WarehouseWarrantDetList'
  import {getAction} from "../../../api/manage";

  export default {
    name: 'WarehouseWarrantList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      JDictSelectTag,
      JDate,
      WarehouseWarrantModal,
      WarehouseWarrantDetList,
      JSearchSelectTag
    },
    data () {
      return {
        description: '入库单管理页面',
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
            title: '创建人',
            align: 'center',
            dataIndex: 'createBy_dictText'
          },
          {
            title:'仓库',
            align:"center",
            dataIndex: 'stockId_dictText'
          },
          {
            title:'入库单号',
            align:"center",
            dataIndex: 'stockNo'
          },
          {
            title:'数量合计',
            align:"center",
            dataIndex: 'qty'
          },
          {
            title:'重量合计',
            align:"center",
            dataIndex: 'weight'
          },
          {
            title:'入库时间',
            align:"center",
            dataIndex: 'stockTime',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
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
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        selectedMainId:'',
        createByDict: '',
        url: {
          list: "/sm/warehouseWarrant/list",
          delete: "/sm/warehouseWarrant/delete",
          deleteBatch: "/sm/warehouseWarrant/deleteBatch",
          exportXlsUrl: "/sm/warehouseWarrant/exportXls",
          importExcelUrl: "sm/warehouseWarrant/importExcel",
          removeAll: "sm/warehouseWarrant/removeAll"
        },
        dictOptions:{},
      }
    },
    created() {
      this.disableMixinCreated=true;
      this.loadData();
      this.initDictConfig();
      this.createByDict = "sys_user where del_flag = 0 and rel_tenant_ids ="+ this.tenantId +" order by create_time,realname,username"
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig(){
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedMainId = selectedRowKeys[0]
        this.selectedRowKeys = selectedRowKeys
        this.selectionRows = selectionRows
      },

      delete1(id) {
        var that = this;
        this.$confirm({
          title: "确认删除",
          content: "是否删除选中数据?",
          onOk: function () {
            getAction(that.url.removeAll, {id: id}).then((res)=>{
              if (res.success){
                that.$message.success("删除成功");
              } else {
                that.$message.error(res.message);
              }
            }).finally(() => {
              that.loadData();
              that.selectedMainId ="";
            });
          }
        })
      },
      editWarehouseWarrant(){
        if (this.selectionRows == null || this.selectedRowKeys.length === 0 || this.selectedRowKeys.length > 1) {
          return this.$message.warning('请勾选一条记录！')
        }
        this.handleEdit(this.selectionRows[0])
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>