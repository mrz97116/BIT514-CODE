<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="12" :sm="24">
            <a-form-item label="顾客">
              <j-search-select-tag placeholder="请选择顾客" v-model="queryParam.customerId = customerId"
                                   dict="cm_customer_profile where  del_flag=0,company_name,id" disabled/>
            </a-form-item>
          </a-col>

          <a-col :xl="6" :lg="7" :md="12" :sm="24">
            <a-form-item label="类型">
              <j-search-select-tag placeholder="请选择类型" v-model="queryParam.type"
                                   dict="fund_detail_type"/>
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
<!--
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('资金明细')">导出</a-button>
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
-->

    <!-- table区域-begin -->
    <div>
<!--
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>
-->

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
        class="j-table-force-nowrap"
        @change="handleTableChange">
        <!--:rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"-->

<!--
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
-->

<!--
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
-->

      </a-table>
    </div>

  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JDictSelectTag from '@/components/dict/JDictSelectTag'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import {filterDictTextByCache} from '@/components/dict/JDictSelectUtil'

  export default {
    name: 'FundDetailForm',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      JDictSelectTag,
      JSearchSelectTag
    },
    data () {
      return {
        description: '资金明细查询页面',
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
            title:'顾客',
            align:"center",
            dataIndex: 'customerId_dictText'
          },
          {
            title:'金额',
            align:"center",
            dataIndex: 'amount'
          },
          {
            title:'类型',
            align:"center",
            dataIndex: 'type',
            customRender: (text) => {
              //字典值翻译通用方法
              return filterDictTextByCache('fund_detail_type', text);
            }
          },
          {
            title:'外部订单号',
            align:"center",
            dataIndex: 'outTradeNo'
          },
/*
          {
            title:'账户',
            align:"center",
            dataIndex: 'accountId'
          },
*/
/*
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }
*/
        ],
        url: {
          list: "/fm/fundDetail/list",
          delete: "/fm/fundDetail/delete",
          deleteBatch: "/fm/fundDetail/deleteBatch",
          exportXlsUrl: "/fm/fundDetail/exportXls",
          importExcelUrl: "fm/fundDetail/importExcel",

        },
        dictOptions:{},
        queryParam: {}
      }
    },
    props: {
      customerId: {
        type: String,
        require: false,
        default:'',
      }
    },
    watch:{
      customerId:{
        handler(){
          this.queryParam.customerId = this.customerId
          this.searchQuery();
        },
        immediate: true
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
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>