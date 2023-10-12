<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="顾客">
              <j-search-select-tag placeholder="请选择顾客" v-model="queryParam.customerId"
                                   dict="cm_customer_profile where  del_flag=0,company_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="到账银行">
              <j-search-select-tag placeholder="请选择到账银行" v-model="queryParam.paymentBank" dict="fm_bank  where del_flag=0,bank_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="来款日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择来款日期" class="query-group-cust" v-model="queryParam.incomingDate_begin" ></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择来款日期" class="query-group-cust" v-model="queryParam.incomingDate_end" ></j-date>
            </a-form-item>
          </a-col>


          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="创建日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择开始时间" class="query-group-cust" v-model="queryParam.createTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择结束时间" class="query-group-cust" v-model="queryParam.createTime_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="汇款到期日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择汇款到期日期" class="query-group-cust" v-model="queryParam.ticketDate_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择汇款到期日期" class="query-group-cust" v-model="queryParam.ticketDate_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="助记码">
              <a-input placeholder="请输入助记码" v-model="queryParam.alias"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="来款方式">
              <j-dict-select-tag placeholder="来款方式" v-model="queryParam.paymentMethod"
                                 dictCode="payment_method"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="进账方式">
              <j-dict-select-tag placeholder="请选择进账方式" v-model="queryParam.incomeMethod" dictCode="income_method" ></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="审核状态">
              <j-dict-select-tag placeholder="审核状态" v-model="queryParam.status" dictCode="common_check_status" ></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="期限">
              <j-dict-select-tag placeholder="期限" v-model="queryParam.term" dictCode="fund_pool_term"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="收据编号">
              <a-input placeholder="请输入收据编号" v-model="queryParam.receiptCode"></a-input>
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
      <a-button @click="handleAdd" type="primary" icon="plus" v-has="'fundPool'">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('来款资金表')" v-has="'fundPool'">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tenantTokenHeader"
                :action="importExcelUrl"
                @change="handleImportExcel">
        <a-button type="primary" icon="import" v-has="'importYRM'">导入</a-button>
      </a-upload>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tenantTokenHeader"
                :action="importExcelUrlCH"
                @change="handleImportExcel">
        <a-button type="primary" icon="import" v-has="'importCH'">岑海导入</a-button>
      </a-upload>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tenantTokenHeader"
                :action="importExcelUrlHK"
                @change="handleImportExcel">
        <a-button type="primary" icon="import" v-has="'importNNAndSH'">银承导入</a-button>
      </a-upload>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchAudit" v-has="'fundPool'">
            <a-icon type="check"/>
            批量审核
          </a-menu-item>
          <!--          <a-menu-item key="2" @click="batchDel"  v-has="'fundPool'">-->
          <!--            <a-icon type="delete"/>-->
          <!--            删除-->
          <!--          </a-menu-item>-->
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作
          <a-icon type="down"/>
        </a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{
        selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
        <a style="margin-left: 12%"> 来款合计：{{this.totalParams.totalIncome}}</a>
        <a style="margin-left: 12%"> 可用金额合计：{{this.totalParams.totalavailAmount}}</a>
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
          <img v-else :src="getImgView(text)" height="25px" alt=""
               style="max-width:80px;font-size: 12px;font-style: italic;"/>
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
          <a @click="editFundPool(record)" :disabled="record.status === 'approve'" v-has="'fundPool:examine'">编辑</a>
          <a-divider type="vertical"/>
          <a @click="handleStatus(record)" :disabled="record.status === 'approve'" v-has="'fundPool:examine'">审核</a>
          <a-divider type="vertical"/>
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)" v-has="'fundPool'">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <fund-pool-modal ref="modalForm" @ok="modalFormOk"></fund-pool-modal>
    <fund-pool-modal-verify ref="modalFormVerify" @ok="modalFormOk"></fund-pool-modal-verify>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import FundPoolModal from './modules/FundPoolModal'
  import FundPoolModalVerify from './modules/FundPoolModalVerify'
  import JDate from '@/components/jeecg/JDate.vue'
  import { filterDictTextByCache } from '@/components/dict/JDictSelectUtil'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import { getAction, httpAction, postAction } from '@/api/manage'

  export default {
    name: 'FundPoolList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      JDate,
      JSearchSelectTag,
      FundPoolModal,
      FundPoolModalVerify
    },
    data() {
      return {
        description: '来款资金表管理页面',
        term: "",
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
            title: '顾客',
            align: 'center',
            dataIndex: 'customerNameText'
          },
          {
            title: '来款方式',
            align: 'center',
            dataIndex: 'paymentMethod',
            customRender: function (text) {
              return filterDictTextByCache('payment_method', text)
            }
          },
          {
            title: '来款金额',
            align: 'center',
            dataIndex: 'incomeAccountString'
          },
          {
            title: '可用金额',
            align: 'center',
            dataIndex: 'availAmountString'
          },
          {
            title: '来款日期',
            align: 'center',
            dataIndex: 'incomingDate',
            customRender: function (text) {
              return !text ? '' : (text.length > 10 ? text.substr(0, 10) : text)
            }
          },
          {
            title: '到账银行',
            align: 'center',
            dataIndex: 'paymentBankName'
          },
          {
            title: '期限',
            align: "center",
            dataIndex: 'term'
          },
          {
            title: '创建日期',
            align: 'center',
            dataIndex: 'createTime',
          },
          {
            title: '更新日期',
            align: 'center',
            dataIndex: 'updateTime',
          },
          {
            title: '审核状态',
            align: 'center',
            dataIndex: 'status',
            customRender: function (text) {
              return filterDictTextByCache('common_check_status', text)
            }
          },
          {
            title: '审核日期',
            align: 'center',
            dataIndex: 'verifyDate'
          },
          {
            title: '承兑银行',
            align: 'center',
            dataIndex: 'acceptanceBankName'
          },
          {
            title: '承兑汇票号',
            align: 'center',
            dataIndex: 'acceptanceTicketNo'
          },
          {
            title: '承兑出票人',
            align: 'center',
            dataIndex: 'acceptancePeople'
          },
          {
            title: '汇票到期日期',
            align: 'center',
            dataIndex: 'ticketDate'
          },
          {
            title: '出票日期',
            align: 'center',
            dataIndex: 'issueTicketsDate'
          },
          {
            title: '汇票到期剩余天数',
            align: 'center',
            dataIndex: 'gapDays'
          },
          {
            title: '承兑银行类型',
            align: 'center',
            dataIndex: 'acceptanceBankType'
          },
          {
            title: '收据编号',
            align: 'center',
            dataIndex: 'receiptCode'
          },
          {
            title: '进账方式',
            align: 'center',
            dataIndex: 'incomeMethod_dictText'
          },
          {
            title: '来款用途',
            align: 'center',
            dataIndex: 'purpose'
          },
          // {
          //   title: '助记码',
          //   align: "center",
          //   dataIndex: 'mnemonicCode'
          // },
          {
            title: '备注',
            align: 'center',
            dataIndex: 'remarks'
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
          list: '/fm/fundPool/list',
          delete: '/fm/fundPool/delete',
          deleteBatch: '/fm/fundPool/deleteBatch',
          exportXlsUrl: '/fm/fundPool/exportXls',
          importExcelUrl: 'fm/fundPool/importExcel',
          creditRecordUrl: '/fm/fundAccount/creditRecord',
          batchAudit: '/fm/fundPool/batchAudit',
          editFundPool: '/fm/fundPool/editFundPool',
          importExcelUrlCH: 'fm/fundPool/importExcelCH',
          importExcelUrlHK: 'fm/fundPool/importExcelHK'

        },
        /* 分页参数 */
        ipagination: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['5', '10', '50', '200'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        totalParams: {
          totalIncome: 0,
          totalavailAmount: 0,
        },
        dictOptions: {},
      }
    },
    created() {

    },
    watch: {
      'dataSource': function () {
        let totalIncome = 0
        let totalavailAmount = 0
        for (let i = 0; i < this.dataSource.length; i++) {
          this.dataSource[i].incomeAccountString = (this.dataSource[i].incomingAmount + '').replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,')
          this.dataSource[i].availAmountString = (this.dataSource[i].availAmount + '').replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,')

          totalIncome += this.dataSource[i].incomingAmount
          totalavailAmount += this.dataSource[i].availAmount
        }
        totalIncome = totalIncome.toFixed(2)
        totalavailAmount = totalavailAmount.toFixed(2)
        this.totalParams.totalIncome = (totalIncome + '').replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,')
        this.totalParams.totalavailAmount = (totalavailAmount + '').replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,')
        // this.totalParams.totalIncome = totalIncome;
      }
    },
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      },
      importExcelUrlCH: function () {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrlCH}`
      },
      importExcelUrlHK: function () {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrlHK}`
      },
    },
    methods: {
      initDictConfig() {
      },
      handleStatus: function (record) {
        debugger
        let that = this
        that.$refs.modalFormVerify.edit(record)
        that.$refs.modalFormVerify.title = '来款类型审核'
        that.$refs.modalFormVerify.disableSubmit = false
        that.$refs.modalFormVerify.disableData = true
      },
      editFundPool: function (record) {
        let that = this
        that.$refs.modalForm.edit(record)
        that.$refs.modalForm.title = '编辑'
        that.loadData()
      },
      batchAudit() {
        var that = this
        if (that.selectedRowKeys.length <= 0) {
          that.$message.warning('请选择审核数据')
          return
        }
        that.$confirm({
          title: '确认审核',
          onOk: function () {
            that.loading = true
            postAction(that.url.batchAudit, that.selectedRowKeys).then((res) => {
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
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>