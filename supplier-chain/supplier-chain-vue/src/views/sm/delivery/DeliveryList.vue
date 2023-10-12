<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="提单号">
              <a-input placeholder="请输入提单号" v-model="queryParam.saleBillNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="合同编号">
              <a-input placeholder="请输入合同编号" v-model="queryParam.contractNo"></a-input>
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
      <a-button v-has="'sm:add'" type="primary" icon="plus" @click="hSends">按件发货</a-button>
      <a-button v-has="'sm:add'" type="primary" icon="plus" @click="hSendw">按量发货</a-button>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{
        selectedRowKeys.length }}</a>项
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
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange,type:'radio'}"
        class="j-table-force-nowrap"
        @change="handleTableChange">

      </a-table>
    </div>

    <j-modal
      :visible.sync="modal.visible"
      :width="1200"
      :title="modal.title"
      :fullscreen.sync="modal.fullscreen"
      :switchFullscreen="modal.switchFullscreen"
      :okClose = "modal.okClose"
      @ok = "handleOk"
      @cancel="handleCancel"

    >

      <!--<template v-for="(i,k) of 30">-->
      <!--<p :key="k">这是主体内容，高度是自适应的</p>-->
      <!--</template>-->
      <template>
        <div>
        <a-form layout="inline">
          <a-row :gutter="24">
            <a-col :xl="6" :lg="7" :md="12" :sm="24">
              <a-form-item label="车号">
                <a-input placeholder="请输入车号" v-model="queryParam.carNo"></a-input>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
        </div>
        <div>
          <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
            <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择重量：<a style="font-weight: 600">{{ selectWt }}</a>吨
            <a style="margin-left: 24px" @click="onClearSelectedMat">清空</a>
          </div>
        <a-table
          ref="matTable"
          size="middle"
          :scroll="{x:true}"
          bordered
          rowKey="matNo"
          :columns="matColumns"
          :dataSource="matDataSource"
          :loading="loading"
          :rowSelection="{selectedMatRowKeys: selectedMatRowKeys,onChange: onSelectMatChange}"
          class="j-matTable-force-nowrap">
        </a-table>
        </div>
      </template>

    </j-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import {mixinDevice} from '@/utils/mixin'
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import { getAction,postAction } from '@/api/manage'
  import {filterMultiDictText, filterDictTextByCache} from '@/components/dict/JDictSelectUtil'

  export default {
    name: 'TableList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      JDictSelectTag
    },
    data() {
      return {
        //弹窗
        modal: {
          title: '材料信息',
          visible: false,
          fullscreen: true,
          switchFullscreen: true,
          okClose: false
        },
        matDataSource:[],
        selectedRowKeys:[],
        selectedKeys:[],
        selectedMatRowKeys:[],
        selectedMatRows:[],
        selectWt:0,
        // 表头
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
            title:'提单号',
            align:"center",
            dataIndex: 'saleBillNo'
          },
          {
            title:'合同编号',
            align:"center",
            dataIndex: 'contractNo'
          },
          {
            title:'订单编号',
            align:"center",
            dataIndex: 'orderNo'
          },
          {
            title:'顾客名称',
            align:"center",
            dataIndex: 'customerId'
          },
          {
            title:'产品大类',
            align:"center",
            dataIndex: 'prodClassCode',
            customRender: (text) => {
              //字典值翻译通用方法
              return filterDictTextByCache('prod_code', text);
            }
          },
          // {
          //   title:'订单条目id',
          //   align:"center",
          //   dataIndex: 'orderItemNo'
          // },
          {
            title:'数量',
            align:"center",
            dataIndex: 'qty'
          },
          {
            title:'重量',
            align:"center",
            dataIndex: 'weight'
          },
          {
            title:'单价',
            align:"center",
            dataIndex: 'price'
          },
          {
            title:'总价',
            align:"center",

            dataIndex: 'totalPrice'
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
            title:'标准',
            align:"center",
            dataIndex: 'sgStd'
          },
          {
            title:'物料种类',
            align:"center",
            dataIndex: 'matKind',
            customRender: function (text){
              return filterDictTextByCache('mat_kind', text)
            }
          }
        ],
        matColumns: [
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
            title:'牌号',
            align:"center",
            dataIndex: 'sgSign'
          },
          {
            title:'品名代码',
            align:"center",
            dataIndex: 'prodCode'
          },
          {
            title:'品名中文',
            align:"center",
            dataIndex: 'prodCname'
          },
          {
            title:'产品大类',
            align:"center",
            dataIndex: 'prodClassCode',
            customRender: (text) => {
              //字典值翻译通用方法
              return filterDictTextByCache('prod_code', text);
            }
          },
          {
            title:'品名中文别名',
            align:"center",
            dataIndex: 'prodCnameOther'
          },
          {
            title:'产品状态码',
            align:"center",
            dataIndex: 'matStatus'
          },
          // {
          //   title:'仓库',
          //   align:"center",
          //   dataIndex: 'stockHouseId'
          // },
          {
            title:'库存类型',
            align:"center",
            dataIndex: 'stockType_dictText'
          },
          {
            title:'材料号',
            align:"center",
            dataIndex: 'matNo'
          },
          {
            title:'材料实际重量',
            align:"center",
            dataIndex: 'matActWt'
          },
          {
            title:'材料净重',
            align:"center",
            dataIndex: 'matNetWt'
          },
          {
            title:'材料磅差重量',
            align:"center",
            dataIndex: 'matDiscrepWt'
          },
          {
            title:'材料件数（根数）',
            align:"center",
            dataIndex: 'matNum'
          },
          {
            title:'材料种类',
            align:"center",
            dataIndex: 'matKind_dictText'
          },
          {
            title:'材料长度',
            align:"center",
            dataIndex: 'matLen'
          },
          {
            title:'材料宽度',
            align:"center",
            dataIndex: 'matWidth'
          },
          {
            title:'材料厚度',
            align:"center",
            dataIndex: 'matThick'
          },
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
          {
            title:'规格',
            align:"center",
            dataIndex: 'custMatSpecs'
          },
          {
            title:'收货顾客编码',
            align:"center",
            dataIndex: 'dealerId'
          },
          {
            title:'采购价',
            align:"center",
            dataIndex: 'puechasePrice'
          },
          {
            title:'技术标准',
            align:"center",
            dataIndex: 'technincalStandard'
          }
        ],
        url: {
          list: "/sm/delivery/list",
          matList: "/sm/delivery/matList",
          send: "/sm/stack/addStack",
          sendMeasured: "/sm/stack/sendMeasured",

        },
        /* 分页参数 */
        ipagination:{
          current: 1,
          pageSize: 5,
          pageSizeOptions: ['5', '10', '50'],
          showTotal: (total, range) => {
            return range[0] + "-" + range[1] + " 共" + total + "条"
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        dictOptions: {},
      }
    },
    created() {
    },
    computed: {
    },
    methods: {
      initDictConfig() {
      },
      searchQuery(arg){
        if(!this.url.list){
          this.$message.error("请设置url.list属性!")
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        this.onClearSelected()
        var params = this.getQueryParams()//查询条件
        // console.log(params)
        this.loading = true;
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.dataSource = res.result;
            this.ipagination.total = res.result.total/1;
          }
          if(res.code===510){
            this.$message.warning(res.message)
          }
          this.loadData();
        })
      },
      onSelectMatChange(selectedMatRowKeys,selectedRows){
        this.selectedMatRowKeys = selectedMatRowKeys;
        this.selectWt = 0;
        for (var i = 0; i < selectedRows.length; i++) {
          this.selectWt = this.selectWt + selectedRows[i].matNetWt;
        }
        this.selectWt = this.selectWt.toFixed(3);
        console.log("this.selectWt",this.selectWt)
      },
      onClearSelectedMat(){
        this.selectedMatRowKeys=[];
        this.selectWt = 0;
      },
      hSends(){
        if(this.selectedRowKeys.length == 0 ){
          this.$message.warning("请勾选要发货的销售单！")
        // }
        // else if(this.selectedRowKeys.length > 1 ){
        //   this.$message.warning("只能勾选一条销售单！")
        }else{
          this.modal.visible=true
          var params = {id : this.selectedRowKeys[0]};
          this.loading = true;
          getAction(this.url.matList, params).then((res) => {
            if (res.success) {
              this.matDataSource = res.result;
            }
            if(res.code===510){
              this.$message.warning(res.message)
            }
            this.loading = false;
          })
        }
      },
      hSendw(){
        if(this.selectedRowKeys.length == 0 ){
          this.$message.warning("请勾选要发货的销售单！")
        // } else if(this.selectedRowKeys.length > 1 ){
        //   this.$message.warning("只能勾选一条销售单！")
        }else{
          var params = {
            id:this.selectedRowKeys[0]
          }
          getAction(this.url.sendMeasured,params).then(res=>{
            if(res.success){
              this.$message.success(res.message)
            }else{
              this.$message.error(res.message)
            }
          });
        }
      },
      handleOk() {
        if(this.selectedMatRowKeys.length == 0 ){
          this.$message.warning("请勾选材料号！")
        }else if(this.queryParam["carNo"] == undefined){
          this.$message.warning("请输入车号！")
        }else {
          var params = {
            saleBillNo:this.selectedRowKeys[0],
            matNos: this.selectedMatRowKeys,
            carNo:this.queryParam["carNo"]
          };
          postAction(this.url.send,params).then(res=>{
            if(res.success){
              this.$message.success(res.message)
            }else{
              this.$message.error(res.message)
            }
          });
          this.selectedRowKeys = [];
          this.selectedMatRowKeys = [];
          this.queryParam["carNo"] = undefined;
          this.modal.visible=false
        }
      },
      handleCancel(){
        this.queryParam["carNo"] = undefined;
        this.selectedMatRowKeys = [];
        this.modal.visible=false
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>