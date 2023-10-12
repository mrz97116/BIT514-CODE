<template>
  <a-card :bordered="false" :class="'cust-erp-sub-tab'">
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
        <span style="margin-left: 35%">数量：{{this.totalParams.matNum}}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;重量：{{this.totalParams.weight}}</span>
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
        @change="handleTableChange">
        <!--        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"-->

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

  </a-card>
</template>

<script>

import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import {filterDictTextByCache} from '@/components/dict/JDictSelectUtil'
import { deleteAction } from '@api/manage'
import { FormTypes } from '@/utils/JEditableTableUtil'



export default {
  name: "SalesOrderDetList",
  mixins:[JeecgListMixin],
  components: {},
  props:{
    mainId:{
      type:String,
      default:'',
      required:false
    },
    parentRecord: {
      type: Object,
      default: function() {
        return {}
      },
      required: false
    }
  },
  watch:{
    mainId:{
      immediate: true,
      handler(val) {
        if(!this.mainId){
          this.clearList()
        }else{
          this.url.list='/om/salesOrder/querySalesOrderDetById?id='+ val
          this.loadData(1);
        }
      }
    },
    'dataSource': function () {
      let totalMatNum = 0 //库存数
      let totalWeight = 0 //库存量
      for (let i = 0; i < this.dataSource.length; i++) {
        totalMatNum += this.dataSource[i].qty
        totalWeight += this.dataSource[i].weight
      }
      //可售量、库存量保留三位小数
      this.totalParams.matNum = totalMatNum
      this.totalParams.weight = totalWeight.toFixed(3)
    },

  },
  data () {
    return {
      description: '提单主表管理页面',
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
          title:'产品名称',
          align:"center",
          dataIndex: 'oldProdCname'
        },
        {
          title:'牌号',
          align:"center",
          dataIndex: 'sgSign'
        },
        {
          title:'材料号',
          align:"center",
          dataIndex: 'matNo',
        },
        {
          title:'订货厚度',
          align:"center",
          dataIndex: 'orderThick',
          sorter: (a, b) => a.orderThick - b.orderThick
        },
        {
          title:'订货宽度',
          align:"center",
          dataIndex: 'orderWidth',
          sorter: (a, b) => a.orderWidth - b.orderWidth
        },
        {
          title:'订货长度',
          align:"center",
          dataIndex: 'orderLen',
        },
        {
          title:'单价',
          align:"center",
          dataIndex: 'price',
          sorter: (a, b) => a.price - b.price
        },
        {
          title:'加价',
          align: 'center',
          dataIndex: 'addPrice'
        },
        {
          title:'总价',
          align:"center",
          dataIndex: 'totalPrice'
        },
        {
          title:'数量',
          align:"center",
          dataIndex: 'qty',
          sorter: (a, b) => a.qty - b.qty
        },
        {
          title:'单重',
          align:"center",
          dataIndex: 'matTheoryWt'
        },
        {
          title:'重量',
          align:"center",
          dataIndex: 'weight',
          sorter: (a, b) => a.weight - b.weight
        },
        {
          title:'计重方式',
          align:"center",
          dataIndex: 'wtMode',
          customRender: (text) =>{
            return filterDictTextByCache('wt_method_code',text);
          }
        },

        {
          title:'合同明细号',
          align:"center",
          dataIndex: 'contractListNo'
        },
        {
          title:'过磅重量',
          align:"center",
          dataIndex: 'weightingWeight'
        },
        {
          title:'过磅价格',
          align:"center",
          dataIndex: 'weightingPrice'
        },

        // {
        //   title:'物料种类',
        //   align: "center",
        //   dataIndex: 'matKind'
        // },
        {
          title:'仓库',
          align:"center",
          dataIndex: 'stockName',
        },
        {
          title:'产品大类',
          align:"center",
          dataIndex: 'prodClassCode',
          customRender: (text) =>{
            return filterDictTextByCache('prod_code',text);
          }
        },
        {
          title: '钝化方式',
          align: 'center',
          dataIndex: 'surfaceTreatment'
        },
        {
          title: '锌层重量',
          align: 'center',
          dataIndex: 'platingWeight',

        },
        {
          title: '标准',
          align: "center",
          dataIndex: 'sgStd'
        },
        {
          title: '品名代码',
          align: "center",
          dataIndex: 'prodCode',
        },
        {
          title:'库位',
          align: "center",
          dataIndex: 'stockLocation'
        }
        // {
        //   title: '操作',
        //   dataIndex: 'action',
        //   align:"center",
        //   fixed:"right",
        //   width:147,
        //   scopedSlots: { customRender: 'action' },
        // }
      ],
      url: {
        list: "/om/salesOrder/querySalesOrderDetByMainId",
        delete: "/om/salesOrder/deleteSalesOrderDet",
        deleteBatch: "/om/salesOrder/deleteBatchSalesOrderDet"
      },
      totalParams: {
        matNum: 0,
        weight: 0
      },
      dictOptions:{
        orderType:[],
        productCode:[],
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
    primaryTableLoadData(){
      this.$emit('primaryTableLoadData')
    },
    batchDel: function () {
      if(!this.url.deleteBatch){
        this.$message.error("请设置url.deleteBatch属性!")
        return
      }
      if (this.selectedRowKeys.length <= 0) {
        this.$message.warning('请选择一条记录！');
        return;
      } else {
        var ids = "";
        for (var a = 0; a < this.selectedRowKeys.length; a++) {
          ids += this.selectedRowKeys[a] + ",";
        }
        var that = this;
        this.$confirm({
          title: "确认删除",
          content: "是否删除选中数据?",
          onOk: function () {
            that.loading = true;
            deleteAction(that.url.deleteBatch, {ids: ids}).then((res) => {
              if (res.success) {
                that.$message.success(res.message);
                that.loadData();
                that.onClearSelected();
                //刷新主表数据
                that.primaryTableLoadData()
              } else {
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.loading = false;
            });
          }
        });
      }
    },
    handleDelete: function (id) {
      if(!this.url.delete){
        this.$message.error("请设置url.delete属性!")
        return
      }
      var that = this;
      deleteAction(that.url.delete, {id: id}).then((res) => {
        if (res.success) {
          that.$message.success(res.message);
          that.loadData();
          //刷新主表数据
          this.primaryTableLoadData()
        } else {
          that.$message.warning(res.message);
        }
      });
    }
  },
}
</script>
<style scoped>
@import '~@assets/less/common.less'
</style>
