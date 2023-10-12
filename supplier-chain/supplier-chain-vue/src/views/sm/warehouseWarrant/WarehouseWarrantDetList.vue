<template>
  <a-card :bordered="false" :class="'cust-erp-sub-tab'">
    <!-- 操作按钮区域 -->
    <div class="table-operator" v-if="mainId">
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

        <!--        <span slot="action" slot-scope="text, record">-->
        <!--          <a @click="handleEdit(record)">编辑</a>-->
        <!--                    <a-divider type="vertical" />-->
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
  import { getAction,postAction } from '@/api/manage'



  export default {
    name: "WarehouseWarrantDetList",
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
            this.url.list='/sm/mat/queryMatByWarehouseWarrantId?warehouseWarrantId='+ val
            this.loadData(1);
          }
        }
      }
    },
    data () {
      return {
        description: '材料信息',
        disableMixinCreated:true,
        // 表头
        columns: [
          {
            title: '序号',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: 'center',
            customRender: function (t, r, index) {
              return parseInt(index) + 1
            }
          },
          {
            title: '牌号',
            align: 'center',
            dataIndex: 'sgSign'
          },
          {
            title: '产品名称',
            align: 'center',
            dataIndex: 'oldProdCname'
          },
          {
            title: '材料厚度',
            align: 'center',
            dataIndex: 'matThick',
            sorter: (a, b) => a.stayDays - b.stayDays
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
            title: '品名中文',
            align: 'center',
            dataIndex: 'prodCname'
          },
          {
            title: '品名中文别名',
            align: 'center',
            dataIndex: 'prodCnameOther'
          },
          /*
                    {
                      title:'产品状态码',
                      align:"center",
                      dataIndex: 'matStatus'
                    },
          */
          // {
          //   title:'仓库',
          //   align:"center",
          //   dataIndex: 'stockHouseId'
          // },
          {
            title: '材料号',
            align: 'center',
            dataIndex: 'matNo'
          },
          {
            title: '材料净重',
            align: 'center',
            dataIndex: 'matNetWt'
          },
          {
            title: '材料件数',
            align: 'center',
            dataIndex: 'matNum'
          },
          {
            title: '材料种类',
            align: 'center',
            dataIndex: 'matKind',
            customRender: (text) => {
              //字典值翻译通用方法
              return filterDictTextByCache('mat_kind', text)
            }
          },
          {
            title: '车号',
            align: 'center',
            dataIndex: 'carNo'
          },
          /*
                    {
                      title:'实际长度',
                      align:"center",
                      dataIndex: 'matActLen'
                    },
                    {
                      title:'实际宽度',
                      align:"center",
                      dataIndex: 'matActWidth'
                    },
                    {
                      title:'实际厚度',
                      align:"center",
                      dataIndex: 'matActThick'
                    },
          */
          {
            title: '材料规格',
            align: 'center',
            dataIndex: 'custMatSpecs'
          },
          /*
                    {
                      title:'收货顾客编码',
                      align:"center",
                      dataIndex: 'dealerId'
                    },
          */
          {
            title: '采购价',
            align: 'center',
            dataIndex: 'puechasePrice'
          },
          {
            title: '成本价',
            align: 'center',
            dataIndex: 'costPrice'
          },
          {
            title: '技术标准',
            align: 'center',
            dataIndex: 'technicalStandard'
          },
          {
            title: '柳钢装车时间',
            align: 'center',
            dataIndex: 'carLoadingTime'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: 'center',
            fixed: 'right',
            width: 147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/sm/mat/queryMatByWarehouseWarrantId",
          delete: "/om/salesOrder/deleteSalesOrderDet",
          deleteBatch: "/sm/warehouseWarrant/removeDetail"
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
      loadData(arg) {
        this.onClearSelected();
        if(!this.url.list){
          this.$message.error("请设置url.list属性!")
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        var params = this.getQueryParams();//查询条件
        this.loading = true;
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.dataSource = res.result;
          }
          if(res.code!=200){
            this.$message.warning(res.message)
          }
          this.loading = false;
        })
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
          var that = this;
          this.$confirm({
            title: "确认删除",
            content: "是否删除选中数据?",
            onOk: function () {
              that.loading = true;
              postAction(that.url.deleteBatch, that.selectedRowKeys).then((res) => {
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
