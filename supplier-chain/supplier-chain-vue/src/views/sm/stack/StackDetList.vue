<template>
  <a-card :bordered="false" :class="'cust-erp-sub-tab'">
    <!-- 操作按钮区域 -->
<!--    <div class="table-operator" v-if="mainId">-->
<!--      <a-dropdown v-if="selectedRowKeys.length > 0">-->
<!--        <a-menu slot="overlay">-->
<!--          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>-->
<!--        </a-menu>-->
<!--        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>-->
<!--      </a-dropdown>-->
<!--    </div>-->

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

<!--        <span slot="action" slot-scope="text, record">-->
<!--          <a @click="handleEdit(record)">编辑</a>-->
          <!--          <a-divider type="vertical" />-->
<!--          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">-->
<!--            <a>删除</a>-->
<!--          </a-popconfirm>-->
<!--        </span>-->

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
  name: "StackDetList",
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
          this.url.list='/sm/stack/queryStackDetByStackId?id='+ val
          this.loadData(1);
        }
      }
    }
  },
  data () {
    return {
      description: '装车单主表管理页面',
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
          title:'品名中文',
          align:"center",
          dataIndex: 'prodCname'
        },
        {
          title:'合同明细号',
          align:"center",
          dataIndex: 'contractListNo'
        },
        {
          title:'品名中文别名',
          align:"center",
          dataIndex: 'prodCnameOther'
        },
        {
          title:'产品名称',
          align:"center",
          dataIndex: 'oldProdCname'
        },
        {
          title:'数量',
          align:"center",
          dataIndex: 'qty'
        },
        {
          title:'单重',
          align:"center",
          dataIndex: 'matTheoryWt'
        },
        {
          title:'重量',
          align:"center",
          dataIndex: 'weight'
        },
        {
          title: '优惠后的单价',
          align:"center",
          dataIndex: 'discountPrice'
        },
        {
          title:'单价',
          align:"center",
          dataIndex: 'price'
        },
        {
          title:'结算金额',
          align:"center",
          dataIndex: 'discountTotalAmount'
        },
        {
          title:'总价',
          align:"center",
          dataIndex: 'totalAmount'
        },
        {
          title:'材料厚度',
          align:"center",
          dataIndex: 'matThick',
          sorter: (a, b) => a.matThick - b.matThick
        },
        {
          title:'材料宽度',
          align:"center",
          dataIndex: 'matWidth',
          sorter: (a, b) => a.matWidth - b.matWidth
        },
        {
          title:'材料长度',
          align:"center",
          dataIndex: 'matLen',
        },
        {
          title:'牌号',
          align:"center",
          dataIndex: 'sgSign'
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
          title:'加价',
          align: 'center',
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
          title:'材料号',
          align:"center",
          dataIndex: 'matNo'
        },
        {
          title:'物料种类',
          align: "center",
          dataIndex: 'matKind'
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
        list: "/sm/stack/queryStackDetByMainId",
        delete: "/sm/stack/deleteStackDet",
        deleteBatch: "/sm/stack/deleteBatchStackDet"
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
