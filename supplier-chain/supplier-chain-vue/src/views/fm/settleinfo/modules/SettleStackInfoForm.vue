<template>
  <a-skeleton active :loading="loading" :paragraph="{rows: 17}">
  <div class="table-page-search-wrapper">
    <a-form layout="inline" @keyup.enter.native="searchQuery">
      <a-row :gutter="24">
        <a-col :xl="6" :lg="7" :md="12" :sm="24">
          <a-form-item label="顾客">
            <j-search-select-tag placeholder="请选择顾客" v-model="queryParam.customerId"
                                 :dictOptions="this.selectUnsettleCustomerId"/>
          </a-form-item>
        </a-col>
        <a-col :xl="6" :lg="7" :md="8" :sm="24">
          <a-form-item label="装车单号">
            <a-input placeholder="请输入装车单号" v-model="queryParam.stackingNo"></a-input>
          </a-form-item>
        </a-col>
        <a-col :xl="6" :lg="7" :md="8" :sm="24">
          <a-form-item label="提单号">
            <a-input placeholder="请输入提单号" v-model="queryParam.saleBillNo"></a-input>
          </a-form-item>
        </a-col>
        <a-col :xl="10" :lg="11" :md="12" :sm="24">
          <a-form-item label="提单生成时间">
            <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择开始时间" class="query-group-cust" v-model="queryParam.createTimeBegin"></j-date>
            <span class="query-group-split-cust"></span>
            <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择结束时间" class="query-group-cust" v-model="queryParam.createTimeEnd"></j-date>
          </a-form-item>
        </a-col>
        <a-col :xl="6" :lg="7" :md="8" :sm="24">
          <a-form-item label="备注">
            <a-input placeholder="请输入备注" v-model="queryParam.remark"></a-input>
          </a-form-item>
        </a-col>
        <a-col :xl="6" :lg="7" :md="8" :sm="24">
          <a-form-item label="产品大类">
            <j-dict-select-tag placeholder="请选择产品大类" v-model="queryParam.prodCode" dictCode="prod_code"/>
          </a-form-item>
        </a-col>
        <a-col v-has="'fm:createBy'" :xl="5" :lg="7" :md="8" :sm="24">
          <a-form-item label="开单人">
            <j-search-select-tag placeholder="请选择创建人" v-model="queryParam.createBy"
                                 :dict="createByDict"/>
          </a-form-item>
        </a-col>
<!--        <a-col v-has="'fm:oldNo'" :xl="6" :lg="7" :md="8" :sm="24">-->
<!--          <a-form-item label="旧系统销售计划单号">-->
<!--            <a-input placeholder="请输入旧系统销售计划单号" v-model="queryParam.oldNo"></a-input>-->
<!--          </a-form-item>-->
<!--        </a-col>-->
        <a-col :xl="6" :lg="7" :md="8" :sm="24">
          <a-form-item label="车号">
            <a-input placeholder="请输入车号" v-model="queryParam.carNo"></a-input>
          </a-form-item>
        </a-col>
        <a-col :xl="10" :lg="11" :md="12" :sm="24">
          <a-form-item label="装车单生成时间">
            <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择开始时间" class="query-group-cust" v-model="queryParam.stackTimeBegin"></j-date>
            <span class="query-group-split-cust"></span>
            <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择结束时间" class="query-group-cust" v-model="queryParam.stackTimeEnd"></j-date>
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
    <a-table
      size="middle"
      :scroll="{x:true}"
      bordered
      rowKey="stackId"
      :columns="columns"
      :dataSource="dataSource"
      @change="handleTableChange"
      :pagination="ipagination"
      :rowSelection="{selectedRowKeys: selectedStackRowKeys, onChange: onSelectChange}"
      class="j-matTable-force-nowrap">
      <span slot="action" slot-scope="text, record">
          <a @click="handleOpen(record)">编辑</a>
      </span>
    </a-table>
    <j-modal
      :visible="visible"
      title="生成结算单"
      width=50%
      @ok="handleOK1"
      @cancel="handleCancel"
    >
      <a-row :gutter="24">
        <a-col :span="30">
          <a-form-item>
            每吨优惠金额:
            <a-input-number style="width: 150px" placeholder="请输入优惠金额" v-model="changerNumber"></a-input-number>
            总价:
            <a-input-number style="width: 150px" placeholder="总价" disabled v-model="totalPriceShow"></a-input-number>
            <div></div>
            总重量:
            <a-input-number style="width: 150px;border:1px solid #cdcdcd; margin-left:15px" placeholder="总重量" disabled v-model="totalWeight"></a-input-number>
            备注:
            <a-input style="width: 150px" placeholder="请输入备注" v-model="remark"></a-input>
          </a-form-item>
          <a-table
            rowKey="stackDetailId"
            size="middle"
            :scroll="{x:true}"
            bordered
            :columns="tableColumns"
            :dataSource="selectionRows"
            :pagination="tableIpagination"
            :loading="loading1"
            class="j-matTable-force-nowrap">
          </a-table>
        </a-col>
      </a-row>
    </j-modal>
    <stack-modal ref="modalForm" @ok="modalFormOk" :editType="editType"></stack-modal>
  </div>
  </a-skeleton>
</template>

<script>

import { httpAction, getAction,getActionTimeOut } from '@/api/manage'
import pick from 'lodash.pick'
import { validateDuplicateValue } from '@/utils/util'
import { mixinDevice } from '@/utils/mixin'
import {filterObj} from '@/utils/util'
import StackModal from './StackModal'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import JFormContainer from '@/components/jeecg/JFormContainer'
import JDictSelectTag from '@/components/dict/JDictSelectTag'
import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
import { filterDictTextByCache } from '@/components/dict/JDictSelectUtil'
import JDate from '@/components/jeecg/JDate.vue'
import { colAuthFilter } from "@/utils/authFilter"
import {math} from '@/views/utils/math.js'

export default {
  name: 'SettleStackInfoForm',
  mixins: [JeecgListMixin],
  components: {
    JFormContainer,
    JDictSelectTag,
    JSearchSelectTag,
    JDate,
    StackModal
  },
  data() {
    return {
      createByDict: '',
      editType: false,
      form: this.$form.createForm(this),
      model: {},
      loading1: false,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      OdataSource:[],
      confirmLoading: false,
      validatorRules: {},
      totalPriceShow: 0,
      totalWeight: 0,
      remark: '',
      visible: false,
      changerNumber: 0,
      selectedStackRowKeys: [],
      selectionRows: [],
      stackDetailId: [],
      selectUnsettleCustomerId: [],
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
          title: '提单号',
          align: 'center',
          dataIndex: 'saleBillNo'
        },
        {
          title: '装车单号',
          align: 'center',
          dataIndex: 'stackingNo'
        },
        // {
        //   title: '旧系统销售计划单号',
        //   align: 'center',
        //   dataIndex: 'oldNo'
        // },
        {
          title: '顾客姓名',
          align: 'center',
          dataIndex: 'customerId'
        },
        {
          title: '业务员',
          align: 'center',
          dataIndex: 'salesMan'
        },
        {
          title: '开单人',
          align: 'center',
          dataIndex: 'createBy'
        },
        {
          title: '装车单生成时间',
          align: 'center',
          dataIndex: 'stackCreateTime'
        },
        {
          title: '提单生成时间',
          align: 'center',
          dataIndex: 'createTime'
        },
        {
          title: '状态',
          align: 'center',
          dataIndex: 'state',
          customRender: function (text) {
            return filterDictTextByCache('settled_state', text)
          }
        },
        // {
        //   title: '牌号',
        //   align: 'center',
        //   dataIndex: 'sgSign'
        // },
        {
          title: '总价',
          align: 'center',
          dataIndex: 'totalAmount'
        },
        {
          title: '重量',
          align: 'center',
          dataIndex: 'weight'
        },
        {
          title: '车号',
          align: 'center',
          dataIndex: 'carNo'
        },
        {
          title: '备注',
          align: 'center',
          dataIndex: 'remark'
        },
        {
          title: '操作',
          dataIndex: 'action',
          align:"center",
          scopedSlots: { customRender: 'action' },
        }
      ],
      tableColumns: [
        {
          title: '装车单号',
          align: 'center',
          dataIndex: 'stackingNo'
        },
        {
          title: '顾客姓名',
          align: 'center',
          dataIndex: 'customerId'
        },
        {
          title: '业务员',
          align: 'center',
          dataIndex: 'salesMan'
        },
        {
          title: '开单人',
          align: 'center',
          dataIndex: 'createBy'
        },
        {
          title: '产品名称',
          align: 'center',
          dataIndex: 'oldProdCname'
        },
        {
          title: '单价',
          align: 'center',
          dataIndex: 'price'
        },
        {
          title: '总价',
          align: 'center',
          dataIndex: 'totalAmount'
        },
        {
          title: '数量',
          align: 'center',
          dataIndex: 'qty'
        },
        {
          title: '重量',
          align: 'center',
          dataIndex: 'weight'
        },
        {
          title: '长度',
          align: 'center',
          dataIndex: 'matLen'
        },
        {
          title: '宽度',
          align: 'center',
          dataIndex: 'matWidth'
        },
        {
          title: '厚度',
          align: 'center',
          dataIndex: 'matThick'
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
        {
          title: '牌号',
          align: 'center',
          dataIndex: 'sgSign'
        },
      ],
      url: {
        list: '/sm/stack/listStackDetail',
        settle: '/fm/settleInfo/settle',
        stackList: '/sm/stack/list',
        settleStackDetail: '/sm/stack/settleStackDetail',
        selectUnsettleCustomerId: 'sm/stack/selectUnsettleCustomerId',
      },
      dictOptions: {},
      totalPrice: 0,

      /* 分页参数 */
      ipagination: {
        current: 1,
        pageSize: 100,
        pageSizeOptions: ['5', '10', '50', '100'],
        showTotal: (total, range) => {
          return range[0] + '-' + range[1] + ' 共' + total + '条'
        },
        showQuickJumper: true,
        showSizeChanger: true,
        total: 0
      },
      tableIpagination: {
        current: 1,
        pageSize: 10,
        pageSizeOptions: ['5', '10', '50'],
        showTotal: (total, range) => {
          return range[0] + '-' + range[1] + ' 共' + total + '条'
        },
        showQuickJumper: true,
        showSizeChanger: true,
        total: 0
      }
    }
  },
  computed: {},
  created() {
    this.columns = colAuthFilter(this.columns,'fm:');
    this.tableColumns = colAuthFilter(this.tableColumns,'fm:');
    this.createByDict = "sys_user where del_flag = 0 and rel_tenant_ids ="+ this.tenantId +",realname,username";
    this.selectUnsettleCustomerId1();
  },
  methods: {
    initDictConfig() {
    },
    clickThenSelect(record) {
      return {
        on: {
          click: () => {
            let rowkey = record.stackDetailId
            if (!this.selectedRowKeys || this.selectedRowKeys.length == 0) {
              let arr1 = [], arr2 = []
              arr1.push(rowkey)
              arr2.push(record)
              this.selectedRowKeys = arr1
              this.selectionRows = arr2
            } else {
              if (this.selectedRowKeys.indexOf(rowkey) > -1) {
                let rowkey_index = this.selectedRowKeys.indexOf(rowkey)
                this.selectedRowKeys.splice(rowkey_index, 1)
                this.selectionRows.splice(rowkey_index, 1)
              } else {
                this.selectedRowKeys.push(rowkey)
                this.selectionRows.push(record)
              }
            }
            this.onSelectChange(this.selectedRowKeys, this.selectionRows)
            // this.onSelectChange(this.selectedRowKeys)
          }
        }
      }
    },
    submitForm() {
      let ids = ''
      for(let i = 0; i < this.selectionRows.length; i++){
        ids += this.selectionRows[i].stackId + ","
      }
      // let id = this.selectionRows[0].stackId


      getAction(this.url.settleStackDetail, {
        stackIds: ids
      }).then((res) => {
        if (res.success) {
          this.selectionRows = res.result
        }else{
          this.$message.warning(res.message)
        }
      })
      this.visible = true
      this.loading1= false
      for (let i = 0; i < this.selectionRows.length; i++) {
        if (this.selectionRows[i].totalAmount !== null) {
          this.totalPriceShow += this.selectionRows[i].totalAmount
        }
        if (this.selectionRows[i].weight !== null) {
          this.totalWeight += this.selectionRows[i].weight
        }
        this.totalWeight=math.toFixed(this.totalWeight,3)
        this.totalPriceShow=math.toFixed(this.totalPriceShow,2)
      }
    },
    handleCancel() {
      this.visible = false
      this.loading1 = false
      this.totalWeight = 0
      this.totalPriceShow = 0
      this.changerNumber = 0
      this.remark= ''
      this.selectedRowKeys = []
      this.selectionRows = []
      this.selectedStackRowKeys = []
    },
    handleOK1() {
      this.loading1 = true
      const that = this
      that.$confirm({
        title: '确认提交',
        content: '是否提交数据?',
        onOk: function () {
          let ids = ''
          for (let a = 0; a < that.selectedRowKeys.length; a++) {
            ids += that.selectedRowKeys[a] + ','
          }
          // stackId = that.selectedRowKeys[0]
          getActionTimeOut(that.url.settle, {
            stackIds: ids,
            discount: that.changerNumber,
            remark: that.remark
          }).then((res) => {
            if (res.success) {
              that.$message.success('创建成功')
              that.loadData()
              that.loading = true
              that.changerNumber = 0
              that.remark=''
              that.visible = false
              that.handleCancel()
            }else{
              that.$message.warning(res.message)
            }
            that.$emit('ok')
          }).finally(
            that.loading = false,
          )
        }
      })
    },
    handleOpen(record) {
     let queryParam ={};
     let OdataSource = [];
     queryParam.id =record.stackId;
      getAction(this.url.stackList, filterObj(queryParam)).then((res) => {
        this.OdataSource = res.result.records;
        this.editType = true
        this.$refs.modalForm.edit(this.OdataSource[0]);
        this.$refs.modalForm.title = "编辑";
        this.$refs.modalForm.disableSubmit = false;
        if(res.code!=200){
          this.$message.warning(res.message)
        }
        this.loading = false;
      })
    },
    onSelectChange(selectedRowKeys, selectionRows) {
      this.selectedRowKeys = selectedRowKeys
      this.selectedStackRowKeys = selectedRowKeys
      this.selectionRows = selectionRows
    },
    selectUnsettleCustomerId1(){
      getAction(this.url.selectUnsettleCustomerId).then((res) => {
        if (res.code=200) {
          this.selectUnsettleCustomerId = res.result;
        }
      })
    }
  }
}
</script>