<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="顾客名称">
              <a-input placeholder="请输入顾客名称" v-model="queryParam.custName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="产品大类">
              <j-dict-select-tag v-model="queryParam.prodClassCode" title="产品大类" dictCode="prod_code" placeholder="请选择产品大类"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="采购状态">
              <j-dict-select-tag placeholder="请选择采购状态" v-model="queryParam.status" dictCode="purchaseApply_status"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="品名中文">
              <a-input placeholder="请输入品名中文" v-model="queryParam.prodCname"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="厚度">
              <a-input placeholder="请输入厚度" v-model="queryParam.matThick"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="宽度">
              <a-input placeholder="请输入宽度" v-model="queryParam.matWidth"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="长度">
              <a-input placeholder="请输入长度" v-model="queryParam.matLen"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="牌号">
              <j-input placeholder="请输入牌号" v-model="queryParam.sgSign"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="合同编号">
              <a-input placeholder="请输入合同编号" v-model="queryParam.contractNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="创建日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开始时间" class="query-group-cust" v-model="queryParam.createTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择结束时间" class="query-group-cust" v-model="queryParam.createTime_end"></j-date>
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
      <a-button v-has="'sm:add'" @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button v-has="'sm:add'" @click="changeStatus('commit')" type="primary" icon="plus">提交</a-button>
      <a-button v-has="'sm:add'" @click="changeStatus('verify')" type="primary" icon="plus">审核</a-button>
<!--      <a-button type="primary" icon="download" @click="handleExportXls('采购单信息表')">导出</a-button>-->
<!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
<!--        <a-button type="primary" icon="import">导入</a-button>-->
<!--      </a-upload>-->
      <a-dropdown v-has="'sm:add'" v-if="selectedRowKeys.length > 0">
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
<!--          <a @click="handleEdit(record)" :disabled="record.status === 'verified' ? true : false">编辑</a>-->
<!--          <a-divider type="vertical"/>-->
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a v-has="'sm:add'">删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <purchase-apply-modal ref="modalForm" @ok="modalFormOk"></purchase-apply-modal>
  </a-card>
</template>

<script>

import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import PurchaseApplyModal from './modules/PurchaseApplyModal'
import JInput from '@/components/jeecg/JInput'
import { filterDictTextByCache } from '@/components/dict/JDictSelectUtil'
import { getAction, postAction } from '@api/manage'
import JDate from '@/components/jeecg/JDate.vue'

export default {
  name: 'PurchaseApplyList',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
    PurchaseApplyModal,
    JInput,
    JDate
  },
  data() {
    return {
      description: '采购申请单管理页面',
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
          title:'创建日期',
          align:"center",
          dataIndex: 'createTime'
        },
        {
          title: '顾客名称',
          align: 'center',
          dataIndex: 'custName'
        },
        {
          title: '产品大类',
          align: 'center',
          dataIndex: 'prodClassCode',
          customRender: (text) => {
            //字典值翻译通用方法
            return filterDictTextByCache('prod_code', text)
          }
        },
        {
          title: '物料种类',
          align: 'center',
          dataIndex: 'matKind_dictText'
        },
        {
          title: '牌号',
          align: 'center',
          dataIndex: 'sgSign'
        },
        {
          title: '品名中文',
          align: 'center',
          dataIndex: 'prodCname'
        },
        {
          title: '厚度',
          align: 'center',
          dataIndex: 'matThick'
        },
        {
          title: '宽度',
          align: 'center',
          dataIndex: 'matWidth'
        },
        {
          title: '长度',
          align: 'center',
          dataIndex: 'matLen'
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
          title: '合同编号',
          align: 'center',
          dataIndex: 'contractNo'
        },
        {
          title: '采购状态',
          align: 'center',
          dataIndex: 'status',
          customRender: (text) => {
            //字典值翻译通用方法
            return filterDictTextByCache('purchaseApply_status', text)
          }
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
        list: '/sm/purchaseApply/list',
        delete: '/sm/purchaseApply/delete',
        deleteBatch: '/sm/purchaseApply/deleteBatch',
        exportXlsUrl: '/sm/purchaseApply/exportXls',
        importExcelUrl: 'sm/purchaseApply/importExcel',
        commit: 'sm/purchaseApply/commit',
        verify: 'sm/purchaseApply/verify',

      },
      dictOptions: {},
    }
  },
  created() {
  },
  computed: {
    importExcelUrl: function () {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    },
  },
  methods: {
    initDictConfig() {
    },
    changeStatus(s) {
      var actionUrl = ''
      if (this.selectionRows.length == 0) {
        this.$message.warning('请勾选一条记录！')
      } else if (s == 'commit') {
        actionUrl = this.url.commit
      } else if (s == 'verify') {
        actionUrl = this.url.verify
      }
      postAction(actionUrl, this.selectionRows).then((res) => {
        if (res.success) {
          this.$message.success(res.message)
        } else {
          this.$message.warning(res.message)
        }
        this.loadData()
        this.onClearSelected()
      })
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>