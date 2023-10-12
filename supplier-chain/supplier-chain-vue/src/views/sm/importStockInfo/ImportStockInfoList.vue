<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="产品大类">
              <j-dict-select-tag placeholder="请选择产品大类" v-model="queryParam.prodClassCode" dictCode="prod_code"/>
            </a-form-item>
          </a-col>
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="创建日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择开始时间" class="query-group-cust"
                      v-model="queryParam.createTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择结束时间" class="query-group-cust"
                      v-model="queryParam.createTime_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="仓库">
              <j-dict-select-tag placeholder="请选择仓库" v-model="queryParam.stockId" dictCode="sm_stock,name,id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="厂编号">
              <j-input placeholder="请输入厂编号" v-model="queryParam.goodsNo"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="备注">
              <j-input placeholder="请输入备注" v-model="queryParam.remarks"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料号">
              <j-input placeholder="请输入材料号" v-model="queryParam.matNo"></j-input>
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
<!--      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>-->
<!--      <a-button @click="warehousing" type="primary" icon="plus">入库</a-button>-->
      <a-button type="primary" icon="download" @click="handleExportXls('导入入库信息')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tenantTokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
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
        <a style="margin-left: 10%"> 勾选重量：{{this.selectedParams.selectedWeight}}</a>
        <a style="margin-left: 10%"> 重量合计：{{this.totalParams.num}}</a>
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
        </span>

      </a-table>
    </div>

    <import-stock-info-modal ref="modalForm" @ok="modalFormOk"></import-stock-info-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import ImportStockInfoModal from './modules/ImportStockInfoModal'
  import { getAction, postAction } from '../../../api/manage'
  import JDictSelectTag from '@/components/dict/JDictSelectTag'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import JDate from '@/components/jeecg/JDate.vue'
  import JInput from "../../../components/jeecg/JInput"

  export default {
    name: 'ImportStockInfoList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      ImportStockInfoModal,
      JDictSelectTag,
      JSearchSelectTag,
      JDate,
      JInput
    },
    data () {
      return {
        description: '导入入库信息管理页面',
        storageStatus: [],
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
            title: '创建日期',
            align: 'center',
            dataIndex: 'createTime',
            sorter: true
          },
          {
            title:'品名',
            align:"center",
            dataIndex: 'prodCnameOther'
          },
          {
            title:'厂编号',
            align:"center",
            dataIndex: 'goodsNo'
          },
          {
            title:'材料号',
            align:"center",
            dataIndex: 'matNo'
          },
          {
            title: '产品大类',
            align: 'center',
            dataIndex: 'prodClassCode_dictText'
          },
          {
            title: '材料厚度',
            align: 'center',
            dataIndex: 'matThick',
            sorter: (a, b) => a.matThick - b.matThick
          },
          {
            title: '材料宽度',
            align: 'center',
            dataIndex: 'matWidth',
            sorter: (a, b) => a.stayDays - b.stayDays
          },
          {
            title: '材料长度',
            align: 'center',
            dataIndex: 'matLen'
          },
          {
            title:'材质',
            align:"center",
            dataIndex: 'surfaces'
          },
          {
            title:'抄码净重',
            align:"center",
            dataIndex: 'copyCodeNetWeight'
          },
          {
            title:'数量',
            align:"center",
            dataIndex: 'qty'
          },
          {
            title:'仓库',
            align:"center",
            dataIndex: 'stockId',
          },
          {
            title:'产地',
            align:"center",
            dataIndex: 'sources'
          },
          {
            title:'船号',
            align:"center",
            dataIndex: 'shipNo'
          },
          {
            title:'备注',
            align:"center",
            dataIndex: 'remarks'
          },
          {
            title:'制单时间',
            align:"center",
            dataIndex: 'preparationTime',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
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
          list: "/sm/importStockInfo/list",
          delete: "/sm/importStockInfo/delete",
          deleteBatch: "/sm/importStockInfo/deleteBatch",
          exportXlsUrl: "/sm/importStockInfo/exportXls",
          importExcelUrl: "sm/importStockInfo/importExcel",
          warehousing: "/sm/importStockInfo/warehousing",
          storageStatus: "/sm/importStockInfo/storageStatus",

        },
        totalParams: {
          num: 0,
        },
        selectedParams: {
          selectedWeight: 0
        },
        dictOptions:{},
      }
    },
    created() {
      this.selectStorageStatus();
    },
    watch: {
      'dataSource': function () {
        debugger
        let totalNum = 0
        for (let i = 0; i < this.dataSource.length; i++) {
          totalNum += this.dataSource[i].copyCodeNetWeight
        }
        this.totalParams.num = totalNum.toFixed(3)
      },
      'selectionRows': function (selectionRows) {
        debugger
        if (selectionRows) {
          let rows = selectionRows
          let selectedWeight = 0
          for (let i = 0; i < rows.length; i++) {
            selectedWeight += rows[i].copyCodeNetWeight
          }
          this.selectedParams.selectedWeight = selectedWeight.toFixed(3)
        }
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig(){
      },
      warehousing(){
        let that = this
        if (that.selectedRowKeys.length <= 0) {
          that.$message.warning('请选择一条记录！');
          return;
        }
        this.$confirm({
          title: '入库',
          content: '是否入库选中数据?',
          onOk: function () {
            postAction(that.url.warehousing, that.selectedRowKeys).then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.loadData();
              } else {
                that.$message.warning(res.message)
              }
            }).finally(() => {
              that.loadData();
            });
          }
        })
      },
      selectStorageStatus(){
        getAction(this.url.storageStatus).then((res) => {
          if (res.code=200) {
            this.storageStatus = res.result;
          }
        })
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>