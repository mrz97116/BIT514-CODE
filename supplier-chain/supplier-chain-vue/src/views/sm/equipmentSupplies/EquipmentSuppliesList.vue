<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="单号">
              <a-input placeholder="请输入单号" v-model="queryParam.orderNo"></a-input>
            </a-form-item>
          </a-col>

          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="顾客名称">
              <a-input placeholder="请输入顾客名称" v-model="queryParam.customer"></a-input>
            </a-form-item>
          </a-col>

          <template v-if="toggleSearchStatus">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="状态">
                <j-dict-select-tag placeholder="请选择状态" v-model="queryParam.status" dictCode="settle_status"/>
              </a-form-item>
            </a-col>
          </template>

          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <!--<a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>-->
      <a-button @click="initEs" type="primary" icon="">一键结算</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('设备物资网主表')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tenantTokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-button @click="revocation" type="primary" >红冲</a-button>
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

<!--      <template slot="pcaSlot" slot-scope="text">-->
<!--        <div>{{ getPcaText(text) }}</div>-->
<!--      </template>-->

    </div>
    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="设备物资明细表" key="1">
        <EquipmentSuppliesDetList :mainId="selectedMainId"/>
      </a-tab-pane>
    </a-tabs>

    <equipment-supplies-modal ref="modalForm" @ok="modalFormOk"></equipment-supplies-modal>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { mixinDevice } from '@/utils/mixin'
  import EquipmentSuppliesModal from './modules/EquipmentSuppliesModal'
  import EquipmentSuppliesDetList from '@/views/sm/equipmentSuppliesDet/EquipmentSuppliesDetList'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import Area from '@/components/_util/Area'
  import { getAction, postAction } from '@/api/manage'
  import '@/assets/less/TableExpand.less'
  import {filterDictTextByCache} from '@/components/dict/JDictSelectUtil'

  export default {
    name: 'EquipmentSuppliesList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      JDictSelectTag,
      EquipmentSuppliesModal,
      EquipmentSuppliesDetList
    },
    data () {
      return {
        description: '设备物资网主表管理页面',
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
            title:'单号',
            align:"center",
            dataIndex: 'orderNo'
          },
          {
            title:'总价',
            align:"center",
            dataIndex: 'totalPrice'
          },
          {
            title:'顾客名称',
            align:"center",
            dataIndex: 'customer'
          },
          {
            title:'状态',
            align:"center",
            dataIndex: 'status_dictText'
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
          list: "/sm/equipmentSupplies/list",
          delete: "/sm/equipmentSupplies/delete",
          deleteBatch: "/sm/equipmentSupplies/deleteBatch",
          exportXlsUrl: "/sm/equipmentSupplies/exportXls",
          importExcelUrl: "/sm/equipmentSuppliesDet/importExcel",
          subduction: "/sm/equipmentSupplies/subduction",
          revocation: "/sm/equipmentSupplies/revocation"
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
      },
    },
    methods: {
      initDictConfig(){
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedMainId = selectedRowKeys[0]
        this.selectedRowKeys = selectedRowKeys
        this.selectionRows = selectionRows
        console.log('selectionRows', this.selectionRows)
      },
      initEs() {
        var that = this;
        this.$confirm({
          title: "确认结算",
          content: "是否结算?",
          onOk: function () {
            that.loading = true;
            getAction(that.url.subduction).then((res) => {
              if (res.success) {
                that.$message.success(res.message);
                that.loadData();
                that.onClearSelected();
              } else {
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.loading = false;
            });
          }
        });
      },
      revocation() {
        if(this.selectionRows.length=== 0) {
          return this.$message.warning("请勾选需要红冲的单号");
        }
        postAction(this.url.revocation,this.selectedRowKeys).then((res) => {
          if(res.success) {
            this.$message.success("已红冲")
            this.loadData();
          } else {
            this.$message.warning(res.message)
          }

        }).finally(() => {
          this.loading = false;
        });
      }

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>