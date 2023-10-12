<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary" icon="download" @click="handleExportXls('设备物资明细表')">导出</a-button>
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

      </a-table>
    </div>

    <equipment-supplies-det-modal ref="modalForm" @ok="modalFormOk"></equipment-supplies-det-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { getAction } from '@api/manage'
  import JInput from '@/components/jeecg/JInput'
  import EquipmentSuppliesDetModal from './modules/EquipmentSuppliesDetModal'
  export default {
    name: 'EquipmentSuppliesDetList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
        EquipmentSuppliesDetModal,
       JInput
    },

    props:{
      mainId:{
        type:String,
        default:'',
        required:false
      },

    },
    watch:{
      mainId:{
        immediate: true,
          handler(val) {
          if(!this.mainId){
            this.clearList()
          }else{
            this.url.list='/sm/equipmentSuppliesDet/queryEquipmentSuppliesDetByMainId?id='+ val
            this.loadData(1);
          }
        }
      }
    },

    data () {
      return {
        description: '设备物资明细表管理页面',
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
            title:'领料单位',
            align:"center",
            dataIndex: 'customerId'
          },
          {
            title:'材料名称',
            align:"center",
            dataIndex: 'prodCname'
          },
          {
            title:'规格型号',
            align:"center",
            dataIndex: 'custMatSpecs'
          },
          {
            title:'出库数量',
            align:"center",
            dataIndex: 'deliveryQty'
          },
          {
            title:'销售金额',
            align:"center",
            dataIndex: 'salesAmount'
          },
        ],
        url: {
          list: "/sm/equipmentSuppliesDet/queryEquipmentSuppliesDetByMainId?id=",
          delete: "/sm/equipmentSuppliesDet/deleteEquipmentSuppliesDet",
          deleteBatch: "/sm/equipmentSuppliesDet/deleteBatchEquipmentSuppliesDet",
          exportXlsUrl: "/sm/equipmentSuppliesDet/exportEquipmentSuppliesDet",
          importExcelUrl: "/sm/equipmentSuppliesDet/importEquipmentSuppliesDet",
          subduction: "/sm/equipmentSuppliesDet/subductionEquipmentSuppliesDet"
        },
        dictOptions:{
          orderType:[],
          productCode:[],
        },
      }
    },
    created() {

    },
    computed: {
       importExcelUrl(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}/${this.mainId}`;
      },
    },
    methods: {
      clearList(){
        this.dataSource=[]
        this.selectedRowKeys=[]
        this.ipagination.current = 1
      },
      primaryTableLoadData(){
        this.$emit('primaryTableLoadData')
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>