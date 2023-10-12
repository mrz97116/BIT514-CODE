<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="仓库名称">
              <j-input placeholder="请输入仓库名称" v-model="queryParam.name"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="仓库地址">
              <a-input placeholder="请输入仓库地址" v-model="queryParam.address"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="是否可用">
              <j-dict-select-tag placeholder="请选择是否可用" v-model="queryParam.active" dictCode="sm_status"/>
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
      <a-button v-has="'sm:add'" @click="handleAdd" type="primary" icon="plus">新增</a-button>

      <!--<a-button type="primary" icon="download" @click="handleExportXls('仓库信息表')">导出</a-button>-->
      <a-space v-has="'sm:add'" v-if="selectedRowKeys.length > 0">
        <a-button @click="batchSelect('enable')" type="primary" icon="check">
          启用
        </a-button>

        <a-button @click="batchSelect('disable')" type="primary" icon="close">
          禁用
        </a-button>
      </a-space>
      <a-dropdown v-has="'sm:add'" v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete"/>
            删除
          </a-menu-item>
          <a-menu-item key="2" @click="batchSelect('enable')" >
            <a-icon type="check"/>
            启用
          </a-menu-item>
          <a-menu-item key="3" @click="batchSelect('disable')">
            <a-icon type="close"/>
            禁用
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

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"/>
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a v-has="'sm:add'">删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <stock-modal ref="modalForm" @ok="modalFormOk"></stock-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import {mixinDevice} from '@/utils/mixin'
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import StockModal from './modules/StockModal'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import {filterMultiDictText, filterDictTextByCache} from '@/components/dict/JDictSelectUtil'
  import { getAction,postAction } from '@/api/manage'
  import JInput from '@/components/jeecg/JInput'
  import { colAuthFilter } from '@/utils/authFilter'

  export default {
    name: 'StockList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      JDictSelectTag,
      JInput,
      StockModal
    },
    data() {
      return {
        description: '仓库信息表管理页面',
        // 表头
        columns: [
          {
            title: '序号',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: "center",
            customRender: function (t, r, index) {
              return parseInt(index) + 1;
            }
          },
          {
            title: '仓库名称',
            align: "center",
            dataIndex: 'name'
          },
          {
            title: '仓库地址',
            align: "center",
            dataIndex: 'address'
          },
          {
            title:'是否是物流园仓库',
            align:"center",
            dataIndex: 'logisticsParkWarehouse',
            customRender: (text) => {
              return filterDictTextByCache('yn',text)
            }
          },
          {
            title:'是否为虚拟仓',
            align:"center",
            dataIndex: 'virtualStock',
            customRender: (text) => {
              return filterDictTextByCache('yn',text)
            }
          },
          {
            title: '是否可用',
            align: "center",
            dataIndex: 'active',
            customRender: (text) => {
              //字典值翻译通用方法
              return filterDictTextByCache('yn', text);
            }
          },
          {
            title: '备注',
            align: "center",
            dataIndex: 'remark'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: "center",
            fixed: "right",
            width: 147,
            scopedSlots: {customRender: 'action'}
          }
        ],
        url: {
          list: "/sm/stock/list",
          delete: "/sm/stock/delete",
          deleteBatch: "/sm/stock/deleteBatch",
          exportXlsUrl: "/sm/stock/exportXls",
          importExcelUrl: "sm/stock/importExcel",
          enable: "sm/stock/enable",
          disable: "sm/stock/disable",
        },
        dictOptions: {},
      }
    },
    created() {
      this.disableMixinCreated = true
      this.columns = colAuthFilter(this.columns, 'sm:');
      this.loadData();
      this.initDictConfig();
    },
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig() {
      },
      batchSelect(parameter){
/*
        if(parameter=="enable"){
          var params ={ids : this.selectedRowKeys}
          postAction(this.url.enable,params);
        };
        if(parameter=="disable"){
          var params ={ids : this.selectedRowKeys}
          postAction(this.url.disable,params);
        };
        this.loadData();
        this.searchQuery();
*/
        var actionUrl = '';
        if(parameter=="enable"){
          actionUrl = this.url.enable
        };
        if(parameter=="disable"){
          actionUrl = this.url.disable
        };
        var params ={ids : this.selectedRowKeys}
        postAction(actionUrl, params).then((res) => {
          if (res.success) {
            this.$message.success(res.message)
          } else {
            this.$message.warning(res.message)
          }
          this.loadData();
          this.searchQuery();
        })
      }

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>