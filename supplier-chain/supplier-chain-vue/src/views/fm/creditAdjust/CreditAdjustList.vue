<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="客户">
              <j-search-select-tag placeholder="请选择客户" v-model="queryParam.customerId" dict="cm_customer_profile where  del_flag=0,company_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="授信编号">
              <a-input placeholder="请输入授信编号" v-model="queryParam.creditNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="审核状态">
              <j-dict-select-tag placeholder="请选择审核状态" v-model="queryParam.status" dictCode="common_check_status"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="审核人">
              <a-input placeholder="请输入审核人" v-model="queryParam.checker"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="审核时间">
              <j-date placeholder="请选择开始日期" class="query-group-cust" v-model="queryParam.checkDate_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date placeholder="请选择结束日期" class="query-group-cust" v-model="queryParam.checkDate_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="调整类型">
              <j-dict-select-tag placeholder="请选择审核状态" v-model="queryParam.adjustType" dictCode="credit_adjust_status"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="备注">
              <a-input placeholder="请输入备注" v-model="queryParam.remark"></a-input>
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
      <!--      <a-button type="primary" icon="download" @click="handleExportXls('授信调整')">导出</a-button>-->
      <!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl"-->
      <!--                @change="handleImportExcel">-->
      <!--        <a-button type="primary" icon="import">导入</a-button>-->
      <!--      </a-upload>-->
      <a-button type="primary" @click="updateBatch('approve')" v-has="'credit:examine'">
        <a-icon type="check"/>
        审核
      </a-button>
      <a-button type="primary" @click="updateBatch('reject')" v-has="'credit:examine'">
        <a-icon type="close"/>
        弃审
      </a-button>

      <!--      <a-dropdown v-if="selectedRowKeys.length > 0">-->
      <!--        <a-menu slot="overlay">-->
      <!--          <a-menu-item key="1" @click="batchDel">-->
      <!--            <a-icon type="delete"/>-->
      <!--            删除-->
      <!--          </a-menu-item>-->
      <!--        </a-menu>-->
      <!--        <a-button style="margin-left: 8px"> 批量操作-->
      <!--          <a-icon type="down"/>-->
      <!--        </a-button>-->
      <!--      </a-dropdown>-->
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
<!--          <a @click="handleEdit(record)">编辑</a>-->
            <a @click="handleDetail(record)">详情</a>
          <!--          <a-divider type="vertical"/>-->
          <!--          <a-dropdown>-->
          <!--            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>-->
          <!--            <a-menu slot="overlay">-->
          <!--              <a-menu-item>-->
          <!--                -->
          <!--              </a-menu-item>-->
          <!--              <a-menu-item>-->
          <!--                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">-->
          <!--                  <a>删除</a>-->
          <!--                </a-popconfirm>-->
          <!--              </a-menu-item>-->
          <!--            </a-menu>-->
          <!--          </a-dropdown>-->
        </span>

      </a-table>
    </div>

    <credit-adjust-modal ref="modalForm" @ok="modalFormOk"></credit-adjust-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import {mixinDevice} from '@/utils/mixin'
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import CreditAdjustModal from './modules/CreditAdjustModal'
  import JDate from '@/components/jeecg/JDate.vue'
  import JInput from '@comp/jeecg/JInput'
  import {getAction} from "../../../api/manage";

  export default {
    name: 'CreditAdjustList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      JDate,
      JInput,
      CreditAdjustModal
    },
    data() {
      return {
        description: '授信调整管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: "center",
            customRender: function (t, r, index) {
              return parseInt(index) + 1;
            }
          },
          {
            title:'客户',
            align:"center",
            dataIndex: 'customerId_dictText'
          },
          {
            title: '授信编号',
            align: "center",
            dataIndex: 'creditNo'
          },
          {
            title: '审核状态',
            align: "center",
            dataIndex: 'status_dictText'
          },
          {
            title: '审核人',
            align: "center",
            dataIndex: 'checker'
          },
          {
            title: '审核时间',
            align: "center",
            dataIndex: 'checkDate',
            customRender: function (text) {
              return !text ? "" : (text.length > 10 ? text.substr(0, 10) : text)
            }
          },
          {
            title: '调整类型',
            align: "center",
            dataIndex: 'adjustType_dictText'
          },
          {
            title: '调整额度',
            align: "center",
            dataIndex: 'adjustAmount'
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
        url: {
          list: "/fm/creditAdjust/list",
          delete: "/fm/creditAdjust/delete",
          deleteBatch: "/fm/creditAdjust/deleteBatch",
          exportXlsUrl: "/fm/creditAdjust/exportXls",
          importExcelUrl: "fm/creditAdjust/importExcel",

        },
        dictOptions: {},
      }
    },
    created() {
    },
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig() {
      },
      updateBatch(param) {
        var that = this;
        if (that.selectedRowKeys.length == 0) {
          that.$message.warning("请勾选一条记录！")
        } else {
          var ids = "";
          for (var a = 0; a < that.selectedRowKeys.length; a++) {
            ids += that.selectedRowKeys[a] + ",";
          }
          var title = ''
          var content = ''
          if (param == 'approve') {
            title = "确认审核"
            content = "是否审核通过选中数据?"
          }
          if (param == 'reject') {
            title = "确认审核"
            content = "是否弃审选中数据?"
          }
          that.$confirm({
            title: title,
            content: content,
            onOk: function () {
              that.loading = true;
              getAction("/fm/creditAdjust/passBatch", {ids: ids, tag: param}).then((res) => {
                if (res.success) {
                  that.$message.success("操作成功！")
                }
                if (res.code === 500) {
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