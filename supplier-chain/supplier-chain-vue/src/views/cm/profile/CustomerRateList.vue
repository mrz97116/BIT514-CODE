<template>
  <a-card :bordered="false" :class="'cust-erp-sub-tab'">
    <!-- 操作按钮区域 -->
    <div class="table-operator" v-if="mainId">
      <a-button v-has="'cmustomer:add'" @click="handleAdd" type="primary" icon="plus">新增</a-button>
<!--      <a-button type="primary" icon="download" @click="handleExportXls('顾客银行卡信息')">导出</a-button>-->
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
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button v-has="'cmustomer:add'" style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
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
        <template slot="rateSlot" slot-scope="text">
          <a-rate v-model="text" disabled></a-rate>
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
            <a v-has="'cmustomer:add'">删除</a>
          </a-popconfirm>
        </span>

      </a-table>
    </div>

    <customerRate-modal ref="modalForm" @ok="modalFormOk" :mainId="mainId"></customerRate-modal>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import CustomerRateModal from './modules/CustomerRateModal'
  import { getAction } from '@/api/manage'



  export default {
    name: "CustomerRateList",
    mixins:[JeecgListMixin],
    components: { CustomerRateModal },
    props:{
      mainId:{
        type:String,
        default:'',
        required:false
      }
    },
    watch:{
      mainId:{
        immediate: true,
        handler(val) {
          if(!this.mainId){
            this.clearList()
          }else{
            this.queryParam['customerId'] = val
            this.loadData(1);
          }
        }
      }
    },
    data () {
      return {
        description: '客户基础信息管理页面',
        disableMixinCreated:true,
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
            title:'评分时间',
            align:"center",
            dataIndex: 'rateTime',
          },
          {
            title:'星级评分',
            align:"center",
            dataIndex: 'rate',
            scopedSlots: { customRender: 'rateSlot' },
          },
          {
            title:'备注',
            align:"center",
            dataIndex: 'remarks',
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/cm/customerProfile/listCustomerRateByMainId",
          delete: "/cm/customerProfile/deleteCustomerRate",
          deleteBatch: "/cm/customerProfile/deleteBatchCustomerRate",
          exportXlsUrl: "/cm/customerProfile/exportCustomerRate",
          importUrl: "/cm/customerProfile/importCustomerRate",
        },
        dictOptions:{
         status:[],
         type:[],
        },
      }
    },
    computed: {
      importExcelUrl(){
        return `${window._CONFIG['domianURL']}/${this.url.importUrl}/${this.mainId}`;
      }
    },
    methods: {
      clearList(){
        this.dataSource=[]
        this.selectedRowKeys=[]
        this.ipagination.current = 1
      },

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>
