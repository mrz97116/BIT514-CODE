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
    <div class="table-page-search-wrapper">
      <a-row :gutter="24">
        <a-col :xl="3" :lg="7" :md="4" :sm="12">
          <a-form-item label="厚度">
            <a-input placeholder="请输入厚度" v-model="matThick"></a-input>
          </a-form-item>
        </a-col>
        <a-col :xl="3" :lg="7" :md="4" :sm="12">
          <a-form-item label="数量">
            <a-input placeholder="请输入数量" v-model="rows"></a-input>
          </a-form-item>
        </a-col>

      </a-row>

    </div>
    <a-button type="primary" @click="isOK">确定</a-button>
    <!-- table区域-begin -->
    <a-dropdown v-if="selectedRowKeys.length > 0">
      <a-menu slot="overlay">
        <a-menu-item key="1" @click="batchDel">
          <a-icon type="delete"/>
          删除
        </a-menu-item>
      </a-menu>
      <a-button style="margin-left: 8px"> 批量操作
        <a-icon type="down"/>
      </a-button>
    </a-dropdown>
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
        <span style="margin-left: 35%">总重量：{{this.totalParams.weight}}</span>
        <a style="margin-left: 10%"> 勾选重量：{{this.selectedParams.selectedWeight}}</a>
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


        <span slot="action" slot-scope="text, record">

          <a-divider type="vertical"/>
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

  </a-card>
</template>

<script>
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import {filterDictTextByCache} from '@/components/dict/JDictSelectUtil'
  import { deleteAction } from '@api/manage'
  import { FormTypes } from '@/utils/JEditableTableUtil'
  import { getAction } from '@/api/manage'
  import ACol from "ant-design-vue/es/grid/Col";

  export default {
      name: "PrepareOrderDetList",
      mixins:[JeecgListMixin],
      components: {ACol},
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
              this.url.list='/sm/shippingManagement/queryPrepareOrderDetById?id='+ val
              this.loadData();
            }
          }
        },
        'dataSource': function () {
          let totalWeight = 0
          for (let i = 0; i < this.dataSource.length; i++) {
            totalWeight += this.dataSource[i].weight
          }
          //可售量、库存量保留三位小数
          this.totalParams.weight = totalWeight.toFixed(3)
        },
        'selectionRows': function (selectionRows) {
          if (selectionRows) {
            let rows = selectionRows
            let selectedWeight = 0
            for (let i = 0; i < rows.length; i++) {
              selectedWeight += rows[i].weight
            }
            this.selectedParams.selectedWeight = selectedWeight.toFixed(3)
          }
        }
      },
      data () {
        return {
          description: '提单主表管理页面',
          disableMixinCreated: true,
          matThick: 0,
          rows: 0,
          // 表头
          url:{
            list:"",
            delete: "/sm/shippingManagement/delConnectFromPrepareOrder",
            deleteBatch: '/sm/shippingManagement/batchDelConnectFromPrepareOrder'
          },
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
              title: '开单人',
              align: "center",
              dataIndex: 'createBy'
            },
            {
              title: '创建日期',
              align: "center",
              dataIndex: 'createTime'
            },
            {
              title: '出厂时间',
              align: "center",
              dataIndex: 'deliveryTime',
              customRender: function (text) {
                return !text ? "" : (text.length > 10 ? text.substr(0, 10) : text)
              }
            },
            {
              title: '产品大类',
              align: 'center',
              dataIndex: 'prodClassCode',
              customRender: (text) =>{
                return filterDictTextByCache('prod_code',text);
              }
            },
            {
              title: '产品名',
              align: "center",
              dataIndex: 'productName'
            },
            // {
            //   title: '物料状态',
            //   align: "center",
            //   dataIndex: 'matStatus_dictText',
            // },
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
              sorter: (a, b) => a.matWidth - b.matWidth
            },
            {
              title: '材料长度',
              align: 'center',
              dataIndex: 'matLen'
            },
            {
              title: '牌号',
              align: "center",
              dataIndex: 'steelNo'
            },
            {
              title: '尺寸',
              align: "center",
              dataIndex: 'matSize'
            },
            {
              title: '材料号',
              align: "center",
              dataIndex: 'matNo'
            },
            {
              title: '车牌号',
              align: "center",
              dataIndex: 'carNo'
            },
            {
              title: '数量',
              align: "center",
              dataIndex: 'quantity'
            },
            {
              title: '重量',
              align: "center",
              dataIndex: 'weight'
            },
            {
              title: '单价',
              align: "center",
              dataIndex: 'price'
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
          totalParams: {
            weight: 0
          },
          selectedParams: {
            selectedWeight: 0,
          },
        }
      },
      methods: {
        clearList(){
          this.dataSource=[]
          this.selectedRowKeys=[]
          this.ipagination.current = 1
        },
        loadData(arg) {
          this.onClearSelected();
          debugger
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
              this.ipagination.total = res.result.total;
            }
            if(res.code!=200){
              this.$message.warning(res.message)
            }
            this.loading = false;
          })
        },
        handleDelete(id) {
          getAction(this.url.delete, {id: id}).then((res) => {
            this.loadData();
          })

        },
        batchDel() {
          var ids = "";
          for (var a = 0; a < this.selectedRowKeys.length; a++) {
            ids += this.selectedRowKeys[a] + ",";
          }
          getAction(this.url.deleteBatch,{ids: ids}).then((res)=> {
              if (res.success) {
                this.$message.success("删除成功！")
                this.loadData();
              }
              if (res.code != 200) {
                this.$message.warning(res.message)
              }
            }
          )

        },
        isOK() {
          var selectRows = [];
          var selecttKeys = [];
          let num = 0;
          for(let i=0;i<this.dataSource.length;i++) {
            if(this.dataSource[i].matThick ==this.matThick) {
              selectRows.push(this.dataSource[i]);
              selecttKeys.push(this.dataSource[i].id);
              num++;
            }
            if(num == this.rows) {
              break;
            }
          }
          this.onSelectChange(selecttKeys,selectRows);
        }
      }

    }
</script>

<style scoped>

</style>