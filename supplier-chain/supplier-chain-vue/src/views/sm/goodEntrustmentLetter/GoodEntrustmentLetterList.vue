<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="产品大类">
              <j-dict-select-tag placeholder="请选择产品大类" v-model="queryParam.prodClassCode1" dictCode="prod_code"/>
            </a-form-item>
          </a-col>
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="创建日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择开始时间"
                      class="query-group-cust"
                      v-model="queryParam.createTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择结束时间"
                      class="query-group-cust"
                      v-model="queryParam.createTime_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="提货点">
              <j-search-select-tag placeholder="请选择提货点" v-model="queryParam.pickupSpot"
                                   dict="sm_stock where del_flag=0,name,name"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="目的地">
              <j-search-select-tag placeholder="请选择目的地" v-model="queryParam.departureSpot"
                                   dict="sm_stock where del_flag=0,name,name"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="委托单位">
              <j-dict-select-tag type="list" v-model="queryParam.clientUnit" dictCode="gd_main_name"
                                 placeholder="请选择委托单位"/>
            </a-form-item>
          </a-col>
          <a-col a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="车队名称">
              <j-search-select-tag placeholder="请选择车队" v-model="queryParam.entrustedToPick"
                                   dict="sm_fleet where del_flag=0,fleet_name,fleet_name"/>
            </a-form-item>
          </a-col>


          <!--          <a-col :xl="5" :lg="7" :md="8" :sm="24">-->
          <!--            <a-form-item label="运输方式">-->
          <!--              <j-dict-select-tag placeholder="请输入运输方式" v-model="queryParam.departureSpot" dictCode="trnp_mode_code"/>-->
          <!--            </a-form-item>-->
          <!--          </a-col>-->
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="船号">
              <j-input placeholder="请输入船号" v-model="queryParam.shipNo"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="派船分单号">
              <j-input placeholder="请输入派船分单号" v-model="queryParam.dispatchShipBillNo"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="是否到货">
              <j-dict-select-tag placeholder="请选择是否到货" v-model="queryParam.arrivalStatus" dictCode="yes_no_status"/>
            </a-form-item>
          </a-col>

          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="备注">
              <j-input placeholder="请输入备注" v-model="queryParam.remark"></j-input>
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
      <!--      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>-->
      <a-button @click="rpPrint()" type="primary" icon="printer">船运委托单</a-button>
      <a-button @click="rpPrintCar()" type="primary" icon="printer">车运委托单</a-button>
      <a-button type="primary" @click="addRemark">添加备注</a-button>
      <!--      <a-button @click="arrivalConfirm('arrived')" type="primary" icon="check">到货</a-button>-->
      <!--      <a-button @click="arrivalConfirm('pending')" type="primary" icon="close">取消到货</a-button>-->
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
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a
        style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
        <a style="margin-left: 10%"> 勾选重量：{{this.selectedParams.selectedWeight}}</a>
        <a style="margin-left: 10%"> 重量合计：{{this.totalParams.num}}</a>
      </div>
      <div>
        <j-modal
          :visible="visible"
          title="批量添加备注"
          width="60%"
          @ok="handleOK"
          @cancel="handleCancel"
        >
          <a-row :gutter="24">
            <a-col :span="20">
              <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input v-model="remark" placeholder="请输入备注"/>
              </a-form-item>
            </a-col>
          </a-row>
        </j-modal>
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
        @change="handleTableChange"
        :rowClassName="tableRowClass">

        <span slot="action" slot-scope="text, record">
<!--          <a @click="handleEdit(record)">编辑</a>-->

          <!--          <a-divider type="vertical"/>-->
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay">
<!--              <a-menu-item>-->
              <!--                <a @click="handleDetail(record)">详情</a>-->
              <!--              </a-menu-item>-->
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
    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="船运材料明细" key="1">
        <shipping-management-det-List ref="sml" :mainId="selectedMainId" :parentRecord="selectionRows[0]"/>
      </a-tab-pane>
      <!--      <a-tab-pane tab="费用明细表" key="2">-->
      <!--        <CostBreakdownList ref="sodw" :mainId="selectedMainId" :parentRecord="selectionRows[0]"/>-->
      <!--      </a-tab-pane>-->
    </a-tabs>

    <good-entrustment-letter-modal ref="modalForm" @ok="modalFormOk"></good-entrustment-letter-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import GoodEntrustmentLetterModal from './modules/GoodEntrustmentLetterModal'
  import JInput from '@/components/jeecg/JInput'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import reportConfig from '@views/report_config.json'
  import { filterMultiDictText, filterDictTextByCache } from '@/components/dict/JDictSelectUtil'
  import { getAction, postAction, putAction } from '../../../api/manage'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import JDate from '@/components/jeecg/JDate.vue'
  import ShippingManagementDetList from './ShippingManagementDetList'

  export default {
    name: 'GoodEntrustmentLetterList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      GoodEntrustmentLetterModal,
      JInput,
      JDictSelectTag,
      JSearchSelectTag,
      JDate,
      ShippingManagementDetList
    },
    data() {
      return {
        description: '提货委托管理管理页面',
        queryParam: {
          createStackStatus: '0'
        },
        remark: '',
        visible: false,
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: 'center',
            customRender: function (t, r, index) {
              return parseInt(index) + 1
            }
          },
          {
            title: '单号',
            align: 'center',
            dataIndex: 'letterNo'
          },
          {
            title: '创建日期',
            align: 'center',
            dataIndex: 'createTime',
            sorter: true
          },
          {
            title: '提货点',
            align: 'center',
            dataIndex: 'pickupSpot'
          },
          {
            title: '目的地',
            align: 'center',
            dataIndex: 'departureSpot'
          },
          {
            title: '船号',
            align: 'center',
            dataIndex: 'shipNo'
          },
          {
            title: '车队',
            align: 'center',
            dataIndex: 'entrustedToPick'
          },
          {
            title: '产品大类',
            align: 'center',
            dataIndex: 'prodClassCode_dictText',
          },
          {
            title: '到货状态',
            align: 'center',
            dataIndex: 'arrivalStatus',
            customRender: (text) => {
              return filterDictTextByCache('yes_no_status', text)
            }
          },
          {
            title: '滞留时长(天)',
            align: 'center',
            dataIndex: 'stayDays',
            sorter: (a, b) => a.stayDays - b.stayDays
          },
          {
            title: '数量',
            align: 'center',
            dataIndex: 'num'
          },
          {
            title: '重量',
            align: 'center',
            dataIndex: 'weight'
          },
          {
            title: '提货人',
            align: 'center',
            dataIndex: 'consignee'
          },
          {
            title: '提货人电话',
            align: 'center',
            dataIndex: 'consigneePhone'
          },
          {
            title: '提货人身份证号',
            align: 'center',
            dataIndex: 'consigneeIdentity'
          },
          // {
          //   title: '运输方式',
          //   align: "center",
          //   dataIndex: 'transportMode',
          //   customRender: (text) => {
          //     //字典值翻译通用方法
          //     return filterDictTextByCache('trnp_mode_code', text)
          //   }
          // },
          {
            title: '委托日期',
            align: 'center',
            dataIndex: 'consignationDate',
            customRender: function (text) {
              return !text ? '' : (text.length > 10 ? text.substr(0, 10) : text)
            }
          },
          {
            title: '备注',
            align: 'center',
            dataIndex: 'remark'
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
          list: '/sm/goodEntrustmentLetter/list',
          delete: '/sm/goodEntrustmentLetter/delete',
          deleteBatch: '/sm/goodEntrustmentLetter/deleteBatch',
          exportXlsUrl: '/sm/goodEntrustmentLetter/exportXls',
          importExcelUrl: 'sm/goodEntrustmentLetter/importExcel',
          queryCompanyReportName: '/bd/tenantReport/queryCompanyReportName',
          addRemarks: '/sm/goodEntrustmentLetter/addRemarks',
          arrivalCheck: '/sm/goodEntrustmentLetter/arrivalCheck'
        },
        totalParams: {
          num: 0,
        },
        selectedParams: {
          selectedWeight: 0
        },
        dictOptions: {},
        rpName: '',
        reportUrl: '',
        selectedMainId: ''
      }
    },
    created() {
      this.loadData()
      this.queryReportUrl()
    },
    mounted() {
      this.loadReportConfig()
    },
    watch: {
      'dataSource': function () {
        let totalNum = 0
        for (let i = 0; i < this.dataSource.length; i++) {
          totalNum += this.dataSource[i].weight
        }
        this.totalParams.num = totalNum.toFixed(3)
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
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      },
    },
    methods: {
      initDictConfig() {
      },
      rpPrint() {
        let ids = []
        for (let i = 0; i < this.selectionRows.length; i++) {
          ids.push(this.selectionRows[i].id)
        }
        this.reportPrint(ids, this.tenantId, reportConfig.reportUrl.gd.shippingGoodEntrustmentLetter)
      },
      rpPrintCar() {
        let ids = []
        for (let i = 0; i < this.selectionRows.length; i++) {
          ids.push(this.selectionRows[i].id)
        }
        this.reportPrint(ids, this.tenantId, reportConfig.reportUrl.gd.cartageGoodEntrustmentLetter)
      },
      queryReportUrl() {
        let that = this
        getAction(that.url.queryCompanyReportName, { tenantId: that.tenantId }).then((res) => {
          if (res.success) {
            that.rpName = res.message
          }
        })
      },
      judgeAction(transportMode) {
        if (this.rpName !== 'empty') {
          if (transportMode === 'shipping') {
            this.reportUrl = reportConfig.reportUrl.sm.shippingGoodEntrustmentLetter.replace('xxx', this.rpName)
          } else {
            this.reportUrl = reportConfig.reportUrl.sm.carGoodEntrustmentLetter.replace('xxx', this.rpName)
          }
        } else {
          return this.$message.warning('打印失败，请联系系统维护人员！')
        }
      },
      addRemark() {
        console.log('1', this.selectionRows.length)
        if (this.selectionRows.length === 0) {
          this.$message.warning('请勾选条目！')
        } else {
          this.remark
          this.visible = true
        }
      },
      handleOK() {
        if (this.batchAddRemarks === null || this.batchAddRemarks === '') {
          return this.$message.warning('请填入备注！')
        }
        var ids = ''
        for (var a = 0; a < this.selectedRowKeys.length; a++) {
          ids += this.selectedRowKeys[a] + ','
        }
        var remark = this.remark
        getAction(this.url.addRemarks, { ids, remark }).then((res) => {
          if (res.success) {
            this.$message.success('添加成功！')
          }
          if (res.code !== 200) {
            this.$message.warning(res.message)
          }
          this.loading = false
        }).finally(() => {
          this.handleCancel()
          this.loadData()
        })
      },
      handleCancel() {
        this.visible = false
        this.remark = ''
      },
      tableRowClass(record, index) {
        debugger
        let a = record.arrivalStatus
        if (record.stayDays > 2) {
          debugger
          return 'new-row-font-class'
        }
        return ''
      },
      arrivalConfirm(ref) {
        var that = this
        if (that.selectionRows == null || that.selectedRowKeys.length === 0) {
          return that.$message.warning('请勾选记录')
        }
        var content = ''
        if (ref === 'arrived') {
          content = '选中的提货单是否到货?'
        } else {
          content = '选中的提货单是否取消到货?'
        }
        that.$confirm({
          title: '确认到货',
          content: content,
          onOk: function () {
            that.loading = true
            putAction(that.url.arrivalCheck, { ids: that.selectedRowKeys, arrivalStatus: ref }).then((res) => {
              if (res.success) {
                that.$message.success('审核成功！')
                that.loadData()
              } else {
                that.$message.warning(res.message)
              }
            }).finally(() => {
              that.loading = false
            })
          }
        })
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedMainId = selectedRowKeys[0]
        this.selectedRowKeys = selectedRowKeys
        this.selectionRows = selectionRows
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>