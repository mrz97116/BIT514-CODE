<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="提货单号">
              <a-input placeholder="请输入提货单号" v-model="queryParam.billOfLadingNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="顾客">
              <j-search-select-tag placeholder="请选择顾客" v-model="queryParam.customerId"
                                   dict="cm_customer_profile where del_flag=0,company_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="厚度">
              <a-input placeholder="请输入厚度" v-model="queryParam.thick"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="宽度">
              <a-input placeholder="请输入宽度" v-model="queryParam.width"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="长度">
              <a-input placeholder="请输入长度" v-model="queryParam.length"></a-input>
            </a-form-item>
          </a-col>

          <a-col :xl="8" :lg="7" :md="12" :sm="24">
            <a-form-item label="开单时间">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开单时间" class="query-group-cust" v-model="queryParam.billDate_begin" ></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开单时间" class="query-group-cust" v-model="queryParam.billDate_end" ></j-date>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="开单人">
              <j-search-select-tag placeholder="请选择开单人" v-model="queryParam.salesCreateBy"
                                   :dict="salesCreateByDict"/>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="车号">
              <a-input placeholder="请输入车号" v-model="queryParam.carNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item label="是否确认">
              <j-dict-select-tag placeholder="是否确认" v-model="queryParam.confirmStack"
                                 dictCode="yes_no_status"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="类型">
              <j-dict-select-tag placeholder="类型" v-model="queryParam.stackStatusType"
                                 dictCode="stack_status_type"></j-dict-select-tag>
            </a-form-item>
          </a-col>

          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="品名">
              <a-input placeholder="请输入品名" v-model="queryParam.productName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料号">
              <a-input placeholder="请输入材料号" v-model="queryParam.materialNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="材质">
              <a-input placeholder="请输入材质" v-model="queryParam.steelGradeName"></a-input>
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
      <a-button @click="confirmStack" type="primary" icon="plus">确认装车</a-button>
      <a-button @click="stackStatusType" type="primary" icon="plus">取消装车</a-button>

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

      </a-table>
    </div>

    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="获取装车实际明细" key="1">
        <GetActualDeliveryDetList :mainId="selectedMainId"/>
      </a-tab-pane>
    </a-tabs>

    <getActualDelivery-modal ref="modalForm" @ok="modalFormOk"></getActualDelivery-modal>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import GetActualDeliveryModal from './modules/GetActualDeliveryModal'
  import { getAction } from '@/api/manage'
  import GetActualDeliveryDetList from './GetActualDeliveryDetList'
  import JDate from '@/components/jeecg/JDate.vue'
  import '@/assets/less/TableExpand.less'
  import JDictSelectTag from '@comp/dict/JDictSelectTag'
  import JSearchSelectTag from '@comp/dict/JSearchSelectTag'
  import { filterDictTextByCache } from '@/components/dict/JDictSelectUtil'



  export default {
    name: 'GetActualDeliveryList',
    mixins: [JeecgListMixin],
    components: {
      JDate,
      GetActualDeliveryDetList,
      GetActualDeliveryModal,
      JDictSelectTag,
      JSearchSelectTag
    },
    data() {
      return {
        salesCreateByDict: '',
        description: '获取装车实际管理页面',
        // 表头
        queryParam: {
          confirmStack: '0'
        },
        columns: [
          {
            title: '提货单号',
            align: 'center',
            dataIndex: 'billOfLadingNo'
          },
          {
            title: '实提单号',
            align: 'center',
            dataIndex: 'stockOutNoteNo'
          },
          {
            title: '出库时间',
            align: 'center',
            dataIndex: 'stockOutDate',
            customRender: function (text) {
              return !text ? '' : (text.length > 10 ? text.substr(0, 10) : text)
            }
          },
          {
            title: '开单时间',
            align: 'center',
            dataIndex: 'billDate',
            customRender: function (text) {
              return !text ? '' : (text.length > 10 ? text.substr(0, 10) : text)
            }
          },
          {
            title: '客户名称',
            align: 'center',
            dataIndex: 'customerId_dictText'
          },
          {
            title: '操作人',
            align: 'center',
            dataIndex: 'operatorName'
          },
          {
            title: '开单人',
            align: 'center',
            dataIndex: 'salesCreateBy_dictText'
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
            title: '是否已确认',
            align: 'center',
            dataIndex: 'confirmStack',
            customRender: function (text) {
              return filterDictTextByCache('yes_no_status', text)
            }
          },
          {
            title: '类型',
            align: 'center',
            dataIndex: 'stackStatusType',
            customRender: function (text) {
              return filterDictTextByCache('stack_status_type', text)
            }
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
          list: '/sm/getActualDelivery/list',
          delete: '/sm/getActualDelivery/delete',
          deleteBatch: '/sm/getActualDelivery/deleteBatch',
          exportXlsUrl: '/sm/getActualDelivery/exportXls',
          importExcelUrl: 'sm/getActualDelivery/importExcel',
          stack: 'sm/getActualDelivery/stack',
          noStack: 'sm/getActualDelivery/noStack'
        },
        dictOptions: {},
        /* 分页参数 */
        ipagination: {
          current: 1,
          pageSize: 5,
          pageSizeOptions: ['5', '10', '50'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        selectedMainId: '',

      }
    },
    created() {
      this.initDictConfig()
      this.salesCreateByDict = 'sys_user where rel_tenant_ids =' + this.tenantId + 'and del_flag=0,realname,username'
    },
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      }
    },
    methods: {
      initDictConfig() {
      },
      clickThenSelect(record) {
        return {
          on: {
            click: () => {
              this.onSelectChange(record.id.split(','), [record])
            }
          }
        }
      },
      onClearSelected() {
        this.selectedRowKeys = []
        this.selectionRows = []
        this.selectedMainId = ''
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedMainId = selectedRowKeys[0]
        this.selectedRowKeys = selectedRowKeys
        this.selectionRows = selectionRows
      },
      loadData(arg) {
        if (!this.url.list) {
          this.$message.error('请设置url.list属性!')
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1
        }
        this.onClearSelected()
        var params = this.getQueryParams()//查询条件
        this.loading = true
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records
            this.ipagination.total = res.result.total
          }
          if (res.code === 510) {
            this.$message.warning(res.message)
          }
          this.loading = false
        })
      },
      stackStatusType() {
        let that = this
        if (this.selectionRows == null || this.selectedRowKeys.length === 0 || this.selectedRowKeys.length > 1) {
          that.$message.warning('请选择一条数据')
        } else {
          let minId = this.selectedRowKeys[0]
          that.$confirm({
            title: '取消装车',
            content: '确定取消装车？',
            onOk: function () {
              getAction(that.url.noStack, { id: minId }).then((res) => {
                if (res.code === 200) {
                  that.$message.success('操作成功,该装车单已确定取消！')
                } else {
                  that.$message.warning(res.message)
                }

              })
            }

          })
        }
      },
      confirmStack() {
        let that = this
        if (this.selectionRows == null || this.selectedRowKeys.length === 0 || this.selectedRowKeys.length > 1) {
          that.$message.warning('请选择一条装车数据')
        } else {
          let minId = this.selectedRowKeys[0]
          that.$confirm({
            title: '装车',
            content: '是否确认装车？',
            onOk: function () {
              getAction(that.url.stack, { id: minId }).then((res) => {
                if (res.code === 200) {
                  that.$message.success(res.result)
                } else {
                  that.$message.warning(res.message)
                }

              })
            }

          })
        }

      }

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>