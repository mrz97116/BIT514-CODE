<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="委托书编号">
              <a-input placeholder="请输入委托书编号" v-model="queryParam.delegateNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="委托人单位名称">
              <j-search-select-tag placeholder="请选择委托人单位名称" v-model="queryParam.delegateUnitName" dict="cm_customer_profile where  del_flag=0,company_name,id"/>
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
      <a-button @click="copyAdd" v-has="'cmustomer:add'" type="primary" icon="copy">复制新增</a-button>
<!--      <a-button type="primary" icon="download" @click="handleExportXls('委托书')">导出</a-button>-->
<!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
<!--        <a-button type="primary" icon="import">导入</a-button>-->
<!--      </a-upload>-->
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon  type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button v-has="'cmustomer:add'" style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
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
         <a @click="handleEdit(record)">编辑</a>
         <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a v-has="'cmustomer:add'">删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <attorney-modal ref="modalForm" @ok="modalFormOk"></attorney-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import AttorneyModal from './modules/AttorneyModal'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import {filterDictTextByCache} from '@/components/dict/JDictSelectUtil'
  // import {getAction} from '../../../api/manage'
  import { getAction, postAction } from '../../../api/manage'
  import pick from "lodash.pick";

  export default {
    name: 'AttorneyList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      JSearchSelectTag,
      AttorneyModal
    },
    data() {
      return {
        description: '委托书管理页面',
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
            title: '委托书编号',
            align: "center",
            dataIndex: 'delegateNo'
          },
          {
            title: '委托人单位名称',
            align: "center",
            dataIndex: 'delegateUnitName'
          },
          {
            title: '委托人法定代表人/委托代理人',
            align: "center",
            dataIndex: 'delegateLegalRepresentative'
          },
          {
            title: '委托人身份证号码',
            align: "center",
            dataIndex: 'delegatorIdCardNo'
          },
          {
            title: '委托人职务',
            align: "center",
            dataIndex: 'delegatorTitle'
          },
          {
            title: '委托人联系电话',
            align: "center",
            dataIndex: 'delegatorMobile'
          },
          {
            title: '受托人姓名',
            align: "center",
            dataIndex: 'trusteeName'
          },
          {
            title: '受托人性别',
            align: "center",
            dataIndex: 'trusteeSex',
            customRender: (text) => {
              //字典值翻译通用方法
              return filterDictTextByCache('sex', text);
            }
          },
          {
            title: '受托人职务',
            align: "center",
            dataIndex: 'trusteeTitle'
          },
          {
            title: '受托人联系电话',
            align: "center",
            dataIndex: 'trusteeMobile'
          },
          {
            title: '受托人身份证号',
            align: "center",
            dataIndex: 'trusteeIdCardNo'
          },
          {
            title: '委托起始时间',
            align: "center",
            dataIndex: 'commissionStartTime',
            customRender: function (text) {
              return !text ? "" : (text.length > 10 ? text.substr(0, 10) : text)
            }
          },
          {
            title: '委托终止时间',
            align: "center",
            dataIndex: 'terminationTimeOfEntrustment',
            customRender: function (text) {
              return !text ? "" : (text.length > 10 ? text.substr(0, 10) : text)
            }
          },
          {
            title: '委托事项',
            align: "center",
            dataIndex: 'entrustedMatters'
          },
          {
            title: '附件',
            align: "center",
            dataIndex: 'enclosure',
            scopedSlots: {customRender: 'fileSlot'}
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
          list: "/cm/attorney/list",
          delete: "/cm/attorney/delete",
          deleteBatch: "/cm/attorney/deleteBatch",
          exportXlsUrl: "/cm/attorney/exportXls",
          importExcelUrl: "cm/attorney/importExcel",
          copyAdd: "cm/attorney/copyAdd",
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
      copyAdd() {
        if (this.selectedRowKeys.length <= 0) {
          this.$message.warning('请选择一条记录！');
          return;
        }
        postAction(this.url.copyAdd,this.selectedRowKeys).then((res) => {
            if (res.success) {
              this.$message.success("复制成功！")
              this.loadData();
            } else {
              this.$message.warning(res.message)
            }
          })
        }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>