<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="单位名称">
              <j-search-select-tag placeholder="请选择单位名称" v-model="queryParam.id" dict="cm_customer_profile where  del_flag=0,company_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24" v-has="'cm:orderMaxDueDays'">
            <a-form-item label="最大超期天数">
              <a-input placeholder="请输入最大超期天数" v-model="queryParam.orderMaxDueDays"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24" v-has="'cm:canLadingBill_dictText'">
            <a-form-item label="是否可开单">
              <j-dict-select-tag placeholder="请选择状态" v-model="queryParam.canLadingBill" dictCode="yes_no_status"/>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="已存在交易方式" v-has="'cm:dealWay_dictText'">
              <j-dict-select-tag placeholder="请选择状态"  v-model="queryParam.editDealWayStatus"  dictCode="yes_no_status">
              </j-dict-select-tag>
            </a-form-item>
          </a-col>

          <a-col :span="6">
            <a-form-item label="批量查询单位" >
              <a-textarea placeholder="请输入单位名称" v-model="queryParam.companyNames"></a-textarea>
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
      <a-button @click="handleAdd" v-has="'cmustomer:add'" type="primary" icon="plus">新增</a-button>
<!--      <a-button type="primary" icon="download" @click="handleExportXls('客户基础信息')">导出</a-button>-->
    </div>

    <!-- table区域-begin -->
    <div>
      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        class="j-table-force-nowrap"
        :scroll="{x:true}"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange, type:'radio'}"
        :customRow="clickThenSelect"
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
        <template slot="pcaSlot" slot-scope="text">
          <div>{{ getPcaText(text) }}</div>
        </template>

        <span slot="action" slot-scope="text, record">
          <a v-has="'cmustomer:add'" @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" v-has="'cm:customerProfileDelYrm'" @confirm="() => handleDelete(record.id)">
              <a>删除</a>
          </a-popconfirm>
        </span>

      </a-table>
    </div>

    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="顾客银行卡信息" key="1" >
        <CustomerBankList :mainId="selectedMainId" />
      </a-tab-pane>
      <a-tab-pane tab="顾客收货地址信息" key="2" forceRender>
        <CustomerAddressList :mainId="selectedMainId" />
      </a-tab-pane>
      <a-tab-pane tab="顾客星级评分" key="3" >
        <CustomerRateList :mainId="selectedMainId" />
      </a-tab-pane>
    </a-tabs>

    <customerProfile-modal ref="modalForm" @ok="modalFormOk"></customerProfile-modal>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import CustomerProfileModal from './modules/CustomerProfileModal'
  import { getAction,getFileAccessHttpUrl } from '@/api/manage'
  import CustomerBankList from './CustomerBankList'
  import CustomerRateList from './CustomerRateList'
  import CustomerAddressList from './CustomerAddressList'
  import {initDictOptions,filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import Area from '@/components/_util/Area'
  import '@/assets/less/TableExpand.less'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import { colAuthFilter } from '@/utils/authFilter'
  import JDictSelectTag from "@/components/dict/JDictSelectTag"

  export default {
    name: "CustomerProfileList",
    mixins:[JeecgListMixin],
    components: {
      JDictSelectTag,
      JSearchSelectTag,
      CustomerBankList,
      CustomerRateList,
      CustomerAddressList,
      CustomerProfileModal
    },
    data () {
      return {
        description: '客户基础信息管理页面',
        // 表头
        columns: [
          {
            title:'单位名称',
            align:"center",
            dataIndex: 'companyName'
          },
          {
            title:'助记码',
            align:"center",
            dataIndex: 'alias'
          },
          {
            title:'税号',
            align:"center",
            dataIndex: 'taxNo'
          },
          {
            title:'开票名称',
            align:"center",
            dataIndex: 'billingName'
          },
          {
            title:'地址',
            align:"center",
            dataIndex: 'address'
          },
          {
            title:'物流园服务类型',
            align:"center",
            dataIndex: 'wmsSupervise_dictText'
          },
          {
            title:'手机号',
            align:"center",
            dataIndex: 'mobile'
          },
          {
            title:'传真',
            align:"center",
            dataIndex: 'fax'
          },
          {
            title:'区域',
            align:"center",
            dataIndex: 'area',
            scopedSlots: {customRender: 'pcaSlot'}
          },
          {
            title:'座机号',
            align:"center",
            dataIndex: 'landLineNo'
          },
          {
            title:'营业执照',
            align:"center",
            dataIndex: 'businessLicense',
            scopedSlots: {customRender: 'imgSlot'}
          },
          {
            title:'最大超期天数',
            align:"center",
            dataIndex: 'orderMaxDueDays',
            sorter: (a, b) => a.orderMaxDueDays - b.orderMaxDueDays
          },
          {
            title:'是否可开单',
            align:"center",
            dataIndex: 'canLadingBill_dictText',
          },
          {
            title:'交易方式',
            align:"center",
            dataIndex: 'dealWay_dictText',
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
        editDealWayStatus: 0,
        searchOptions: [{0:"是"},{1:"否"}],
        url: {
          list: "/cm/customerProfile/list",
          delete: "/cm/customerProfile/delete",
          deleteBatch: "/cm/customerProfile/deleteBatch",
          exportXlsUrl: "/cm/customerProfile/exportXls",
          importExcelUrl: "cm/customerProfile/importExcel",
        },
        dictOptions:{
         status:[],
         type:[],
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
        selectedMainId:'',
        orderMaxDueDays:''
      }
    },
    created() {
      this.pcaData = new Area()
      this.disableMixinCreated = true
      this.columns = colAuthFilter(this.columns, 'cm:');
      this.loadData();
      this.initDictConfig();
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
      getPcaText(code){
        return this.pcaData.getText(code);
      },
      initDictConfig(){
        initDictOptions('status').then((res) => {
          if (res.success) {
            this.$set(this.dictOptions, 'status', res.result)
          }
        })
        initDictOptions('CUSTOMER_TPYE').then((res) => {
          if (res.success) {
            this.$set(this.dictOptions, 'type', res.result)
          }
        })
      },
      clickThenSelect(record) {
        return {
          on: {
            click: () => {
              this.onSelectChange(record.id.split(","), [record]);
            }
          }
        }
      },
      onClearSelected() {
        this.selectedRowKeys = [];
        this.selectionRows = [];
        this.selectedMainId=''
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedMainId=selectedRowKeys[0]
        this.selectedRowKeys = selectedRowKeys;
        this.selectionRows = selectionRows;
      },
      loadData(arg) {
        if(!this.url.list){
          this.$message.error("请设置url.list属性!")
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        this.onClearSelected()
        var params = this.getQueryParams();//查询条件
        this.loading = true;
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records;
            this.ipagination.total = res.result.total;
          }
          if(res.code===510){
            this.$message.warning(res.message)
          }
          this.loading = false;
        })
      },
      // subString(){
      //
      // }

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>