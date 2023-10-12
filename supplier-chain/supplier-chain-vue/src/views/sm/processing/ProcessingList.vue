<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="创建人">
              <j-input placeholder="请输入创建人" v-model="queryParam.createBy"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="创建日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开始时间" class="query-group-cust"
                      v-model="queryParam.createTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择结束时间" class="query-group-cust"
                      v-model="queryParam.createTime_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="牌号">
              <j-input placeholder="请输入牌号" v-model="queryParam.sgSign"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料号">
              <j-input placeholder="请输入材料号" v-model="queryParam.matNo"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="加工编号">
              <j-input placeholder="请输入加工编号" v-model="queryParam.processingNo"></j-input>
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
      <a-button v-has="'sm:processingDetYrm'"  @click="reportYrm" type="primary" icon="printer">打印加工委托书</a-button>
      <a-button v-has="'sm:processingYrmO'" @click="reportYrmO" type="primary" icon="printer">打印加工出库单</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('加工管理')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl"
                @change="handleImportExcel">

        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete"/>
            删除
          </a-menu-item>
        </a-menu>
<!--        <a-button style="margin-left: 8px"> 批量操作-->
<!--          <a-icon type="down"/>-->
<!--        </a-button>-->
      </a-dropdown>
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
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical"/>
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
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
        </span>

      </a-table>
    </div>

    <processing-modal ref="modalForm" @ok="modalFormOk"></processing-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import {mixinDevice} from '@/utils/mixin'
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import ProcessingModal from './modules/ProcessingModal'
  import JDate from '@/components/jeecg/JDate.vue'
  import JInput from '@/components/jeecg/JInput'
  import reportConfig from '@views/report_config.json'


  export default {
    name: 'ProcessingList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      JDate,
      JInput,
      ProcessingModal
    },
    mounted() {
      this.loadReportConfig()
    },
    data() {
      return {
        description: '加工管理管理页面',
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
            title: '创建人',
            align: "center",
            dataIndex: 'createBy'
          },
          {
            title: '创建日期',
            align: "center",
            dataIndex: 'createTime'
          },
          {
            title: '加工编号',
            align: "center",
            dataIndex: 'processingNo'
          },
          {
            title: '牌号',
            align: "center",
            dataIndex: 'sgSign'
          },
          {
            title: '材料号',
            align: "center",
            dataIndex: 'matNo'
          },
          {
            title: '材料厚度',
            align: "center",
            dataIndex: 'matThick'
          },
          {
            title: '材料宽度',
            align: "center",
            dataIndex: 'matWidth'
          },
          {
            title: '材料长度',
            align: "center",
            dataIndex: 'matLen'
          },
          {
            title: '卷重',
            align: "center",
            dataIndex: 'weight'
          },
          {
            title: '产品大类',
            align: "center",
            dataIndex: 'prodClassCode'
          },
          {
            title: '品名中文别名',
            align: "center",
            dataIndex: 'prodCnameOther'
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
          list: "/sm/processing/list",
          delete: "/sm/processing/delete",
          deleteBatch: "/sm/processing/deleteBatch",
          exportXlsUrl: "/sm/processing/exportXls",
          importExcelUrl: "sm/processing/importExcel",

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
      reportYrm() {
        if (this.selectionRows == null || this.selectedRowKeys.length === 0) {
          return this.$message.warning('请勾选记录！')
        }
        this.reportPrint(this.selectedRowKeys,this.tenantId, reportConfig.reportUrl.yrm.processingCoilUrl);
      },
      reportYrmO() {
        if (this.selectionRows == null || this.selectedRowKeys.length === 0) {
          return this.$message.warning('请勾选记录！')
        }
        this.reportPrint(this.selectedRowKeys,this.tenantId, reportConfig.reportUrl.yrm.processingDlyUrl);
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>