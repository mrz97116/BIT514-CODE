<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="顾客">
              <j-search-select-tag placeholder="请选择顾客" v-model="queryParam.customerId" dict="cm_customer_profile where  del_flag=0,company_name,id"/>
            </a-form-item>
          </a-col>
          <a-col v-has="'fm:refundReceiptCode'" :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="来款收据编号">
              <j-input placeholder="请输入来款收据编号" v-model="queryParam.receiptCode"></j-input>
            </a-form-item>
          </a-col>
<!--          <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
<!--            <a-form-item label="来款日期">-->
<!--              <j-date placeholder="请选择来款日期" v-model="queryParam.refundDate"></j-date>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="退款日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开始时间" class="query-group-cust" v-model="queryParam.refundDate_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择结束时间" class="query-group-cust" v-model="queryParam.refundDate_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="审核状态">
              <j-dict-select-tag placeholder="审核状态" v-model="queryParam.status" dictCode="common_check_status" ></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="审核日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开始时间" class="query-group-cust" v-model="queryParam.approveDate_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择结束时间" class="query-group-cust" v-model="queryParam.approveDate_end"></j-date>
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
      <a-button type="primary" icon="download" @click="handleExportXls('退款记录表')" v-has="'refundRecords'">导出</a-button>
<!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tenantTokenHeader-->
<!--" :action="importExcelUrl" @change="handleImportExcel">-->
<!--        <a-button type="primary" icon="import" v-has="'refundRecords'">导入</a-button>-->
<!--      </a-upload>-->
      <a-button type="primary" @click="updateBatch('approve')" v-has="'refundRecords:examine'"><a-icon type="check"/>审核</a-button>
      <a-button type="primary"  @click="updateBatch('reject')" v-has="'refundRecords:examine'"><a-icon type="close"/>弃审</a-button>
<!--      <a-dropdown v-if="selectedRowKeys.length > 0">-->
<!--        <a-menu slot="overlay">-->
<!--          <a-menu-item key="1" @click="batchDel"><a-icon type="delete" v-has="'refundRecords'"/>删除</a-menu-item>-->
<!--        </a-menu>-->
<!--        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>-->
<!--      </a-dropdown>-->
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 10%"> 勾选金额：{{this.selectedParams.selectedAmount}}</a>
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
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        class="j-table-force-nowrap"
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
          <a @click="handleDetail(record)">详情</a>

<!--          <a-divider type="vertical" />-->
<!--          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">-->
<!--              <a v-has="'refundRecords'">删除</a>-->
<!--          </a-popconfirm>-->
<!--
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
-->
        </span>

      </a-table>
    </div>

    <refund-records-modal ref="modalForm" @ok="modalFormOk"></refund-records-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import RefundRecordsModal from './modules/RefundRecordsModal'
  import JDate from '@/components/jeecg/JDate.vue'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import {getAction} from "../../../api/manage"
  import {filterMultiDictText,filterDictTextByCache} from '@/components/dict/JDictSelectUtil'
  import JInput from '@/components/jeecg/JInput'

  export default {
    name: 'RefundRecordsList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      RefundRecordsModal,
      JDate,
      JInput,
      JSearchSelectTag
    },
    data () {
      return {
        description: '退款记录表管理页面',
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
            title:'顾客名称',
            align:"center",
            dataIndex: 'customerId'
          },
          {
            title:'退款金额',
            align:"center",
            dataIndex: 'refundAmountString'
          },
          {
            title:'退款日期',
            align:"center",
            dataIndex: 'refundDate'
          },
          {
            title:'审核状态',
            align:"center",
            dataIndex: 'status',
            customRender:function (text) {
              return filterDictTextByCache("common_check_status",text);
            }
          },
          {
            title:'审核日期',
            align:"center",
            dataIndex: 'approveDate'
          },
          {
            title:'收据编号',
            align:"center",
            dataIndex: 'receiptCode'
          },
          {
            title:'备注',
            align:"center",
            dataIndex: 'remarks'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/fm/refundRecords/list",
          delete: "/fm/refundRecords/delete",
          deleteBatch: "/fm/refundRecords/deleteBatch",
          exportXlsUrl: "/fm/refundRecords/exportXls",
          importExcelUrl: "fm/refundRecords/importExcel",

        },
        totalParams: {
          num: 0,
        },
        selectedParams: {
          selectedAmount: 0
        },
        dictOptions:{},
      }
    },
    created() {
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    watch: {
      'dataSource':function() {
        for(let i =0;i<this.dataSource.length;i++) {
          debugger
          this.dataSource[i].refundAmountString = (this.dataSource[i].refundAmount.toFixed(2) + '').replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,');
        }
      },
      'selectionRows': function (selectionRows) {
        if (selectionRows) {
          let rows = selectionRows
          let selectedAmount = 0
          for (let i = 0; i < rows.length; i++) {
            selectedAmount += rows[i].refundAmount;
          }
          this.selectedParams.selectedAmount = selectedAmount.toFixed(2).replace(/(\d{1,3})(?=(\d{3})+(?:$|\.))/g, '$1,');
        }
      }
    },
    methods: {
      initDictConfig(){
      },
      updateBatch(param) {
        var that = this;
        if(that.selectedRowKeys.length == 0){
          that.$message.warning("请勾选一条记录！")
        }else {
          var ids = "";
          for (var a = 0; a < that.selectedRowKeys.length; a++) {
            ids += that.selectedRowKeys[a] + ",";
          }
          var title = ''
          var content = ''
          if (param == 'approve'){
            title = "确认审核"
            content = "是否审核通过选中数据?"
          }
          if(param == 'reject'){
            title = "确认审核"
            content = "是否弃审选中数据?"
          }
          that.$confirm({
            title: title,
            content: content,
            onOk: function () {
              that.loading = true;
              // var id = record.id + ",";
              getAction("/fm/refundRecords/passBatch", {ids: ids,tag:param}).then((res) => {
                if (res.success) {
                  that.$message.success("操作成功！")
                }
                if(res.code===500){
                  that.$message.warning(res.message)
                }
                that.loading = false;
                that.loadData()
              }).finally(() => {
                that.loading = false;
              });
            }
          });
        }
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>