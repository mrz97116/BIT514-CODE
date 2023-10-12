<template>
  <a-card :bordered="false" :class="'cust-erp-sub-tab'">
    <!-- 操作按钮区域 -->
    <div class="table-operator" v-if="mainId">
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

    <getActualDeliveryDet-modal ref="modalForm" @ok="modalFormOk" :mainId="mainId"></getActualDeliveryDet-modal>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import {filterDictTextByCache} from '@/components/dict/JDictSelectUtil'
  import GetActualDeliveryDetModal from './modules/GetActualDeliveryDetModal'



  export default {
    name: "GetActualDeliveryDetList",
    mixins:[JeecgListMixin],
    components: { GetActualDeliveryDetModal },
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
            this.queryParam['getActualDeliveryId'] = val
            this.loadData(1);
          }
        }
      }
    },
    data () {
      return {
        description: '获取装车实际管理页面',
        disableMixinCreated:true,
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
            title:'材料号',
            align:"center",
            dataIndex: 'materialNo'
          },
          {
            title:'品名',
            align:"center",
            dataIndex: 'productName'
          },
          {
            title:'材质',
            align:"center",
            dataIndex: 'steelGradeName'
          },
          {
            title:'产地',
            align:"center",
            dataIndex: 'steelMillsName'
          },
          {
            title:'生产标准',
            align:"center",
            dataIndex: 'standardName'
          },
          {
            title:'长度',
            align:"center",
            dataIndex: 'length'
          },
          {
            title:'宽度',
            align:"center",
            dataIndex: 'width'
          },
          {
            title:'厚度',
            align:"center",
            dataIndex: 'thick'
          },
          {
            title:'包装',
            align:"center",
            dataIndex: 'packageCount'
          },
          {
            title:'计重方式',
            align:"center",
            dataIndex: 'weightMode',
            customRender: (text) =>{
              return filterDictTextByCache('wt_method_code',text);
            }
          },
          {
            title:'数量单位',
            align:"center",
            dataIndex: 'numberUnit'
          },
          {
            title:'重量单位',
            align:"center",
            dataIndex: 'weightUnit'
          },
          {
            title:'数量',
            align:"center",
            dataIndex: 'quantity'
          },
          {
            title:'重量',
            align:"center",
            dataIndex: 'weight'
          },

        ],
        url: {
          list: "/sm/getActualDelivery/listGetActualDeliveryDetByMainId",
          delete: "/sm/getActualDelivery/deleteGetActualDeliveryDet",
          deleteBatch: "/sm/getActualDelivery/deleteBatchGetActualDeliveryDet",
          exportXlsUrl: "/sm/getActualDelivery/exportGetActualDeliveryDet",
          importUrl: "/sm/getActualDelivery/importGetActualDeliveryDet",
        },
        dictOptions:{
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
