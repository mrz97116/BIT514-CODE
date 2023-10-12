<template>
  <a-card :bordered="false" :class="'cust-erp-sub-tab'">
    <!-- 操作按钮区域 -->
    <div class="table-operator" v-if="mainId">
<!--      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>-->
<!--      <a-button type="primary" icon="download" @click="handleExportXls('订单明细表')">导出</a-button>-->
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

        <span slot="action" slot-scope="text, record">
<!--          <a @click="handleEdit(record)">编辑</a>-->
<!--          <a-divider type="vertical" />-->
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a>删除</a>
          </a-popconfirm>
        </span>

      </a-table>
    </div>

  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import {filterDictTextByCache} from '@/components/dict/JDictSelectUtil'
  import { deleteAction } from '@api/manage'



  export default {
    name: "OrderDetList",
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
            //this.queryParam['orderNo'] = this.parentRecord.orderNo
            this.url.list='/om/order/queryOrderDetByMainId?id='+ val
            this.loadData(1);
          }
        }
      }
    },
    data () {
      return {
        description: '订单主表管理页面',
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
            title:'标准',
            align:"center",
            dataIndex: 'sgStd'
          },
          {
            title:'牌号',
            align:"center",
            dataIndex: 'sgSign'
          },
          {
            title:'订货厚度',
            align:"center",
            dataIndex: 'orderThick'
          },
          {
            title:'订货宽度',
            align:"center",
            dataIndex: 'orderWidth'
          },
          {
            title:'订货长度',
            align:"center",
            dataIndex: 'orderLen'
          },
          {
            title:'品名中文',
            align:"center",
            dataIndex: 'prodCname'
          },
          {
            title:'品名中文别名',
            align:"center",
            dataIndex: 'prodCnameOther'
          },
          {
            title:'物料种类',
            align:"center",
            dataIndex: 'matKind',
            customRender:(text) => {
              return filterDictTextByCache('mat_kind',text);
            }
          },
          {
            title:'订货重量',
            align:"center",
            dataIndex: 'weight'
          },
          {
            title:'单价',
            align:"center",
            dataIndex: 'price'
          },
          {
            title:'成本价',
            align:"center",
            dataIndex: 'costPrice'
          },
          {
            title:'加价',
            align:"center",
            dataIndex: 'addPrice'
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
          {
            title:'计重方式',
            align:"center",
            dataIndex: 'weightType',
            customRender: (text) =>{
              return filterDictTextByCache('wt_method_code',text);
            }
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
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/om/order/queryOrderDetByMainId",
          delete: "/om/order/deleteOrderDet",
          deleteBatch: "/om/order/deleteBatchOrderDet",
          exportXlsUrl: "/om/order/exportOrderDet",
          importUrl: "/om/order/importOrderDet",
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
