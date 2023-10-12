<template>
    <j-modal
      :visible="visible"
      title="提货人信息 "
      :width="width"
      switchFullscreen
      @ok="handleOK"
      @cancel="handleCancel"
    >
      <template>
        <a-card :bordered="false">
          <!-- 查询区域 -->
          <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
              <a-row :gutter="24">
                <a-col :xl="6" :lg="7" :md="8" :sm="24">
                  <a-form-item label="提货人名字">
                    <j-input placeholder="请输入提货人名字" v-model="queryParam.consigneeName"></j-input>
                  </a-form-item>
                </a-col>
                <a-col :xl="6" :lg="7" :md="8" :sm="24">
                  <a-form-item label="身份证">
                    <j-input placeholder="请输入身份证" v-model="queryParam.idCard"></j-input>
                  </a-form-item>
                </a-col>
                <a-col :xl="6" :lg="7" :md="8" :sm="24">
                  <a-form-item label="助记码">
                    <a-input placeholder="请输入助记码" v-model="queryParam.mnemonicCode"></a-input>
                  </a-form-item>
                </a-col>
                <a-col :xl="5" :lg="7" :md="8" :sm="24">
                  <a-form-item label="手机号码">
                    <j-input placeholder="请输入手机号码" v-model="queryParam.phone"></j-input>
                  </a-form-item>
                </a-col>
                <a-col :xl="10" :lg="7" :md="8" :sm="24">
                  <a-form-item label="备注">
                    <j-input placeholder="请输入备注" v-model="queryParam.remark"></j-input>
                  </a-form-item>
                </a-col>
                <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: right;overflow: hidden;" class="table-page-search-submitButtons">
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
            <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
            <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tenantTokenHeader" :action="importExcelUrl" @change="handleImportExcel">
              <a-button type="primary" icon="import">导入</a-button>
            </a-upload>
            <a-dropdown v-if="selectedRowKeys.length > 0">
              <a-menu slot="overlay">
                <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
              </a-menu>
              <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
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
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

            </a-table>
          </div>

          <consignee-modal ref="modalForm" @ok="modalFormOk"></consignee-modal>
        </a-card>
      </template>

    </j-modal>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import { getAction,postAction } from '@/api/manage'
  import { mixinDevice } from '@/utils/mixin'
  import ConsigneeModal from './ConsigneeModal'
  import JInput from '@/components/jeecg/JInput'
  export default {
    name: "ConsigneeListModal",
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      ConsigneeModal,
      JInput
    },
    created() {
      this.loadData();
    },

    data () {
      return {
        description: '提货人信息管理页面',
        width:1000,
        visible:false,
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title:'提货人名字',
            align:"center",
            dataIndex: 'consigneeName'
          },
          {
            title:'身份证',
            align:"center",
            dataIndex: 'idCard'
          },
          {
            title:'手机号',
            align:"center",
            dataIndex: 'phone'
          },
          {
            title:'助记码',
            align:"center",
            dataIndex: 'mnemonicCode'
          },
          {
            title:'备注',
            align:"center",
            dataIndex: 'remark'
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
          list: "/sm/consignee/list",
          delete: "/sm/consignee/delete",
          deleteBatch: "/sm/consignee/deleteBatch",
          exportXlsUrl: "/sm/consignee/exportXls",
          importExcelUrl: "sm/consignee/importExcel",

        },
        dictOptions:{},
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig(){
      },
      handleOK(){
        this.$emit("ok",this.selectionRows[0]);
        this.handleCancel();
      },
      hOpend() {
        this.loadData();
        this.visible = true;
      },
      handleCancel() {
        this.visible = false;
        this.selectedRowKeys = [];
        this.queryParam = {};
      },
    }
  }

</script>

<style scoped>

</style>