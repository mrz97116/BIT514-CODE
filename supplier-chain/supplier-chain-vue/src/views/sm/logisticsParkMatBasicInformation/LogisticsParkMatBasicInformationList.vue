<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <!--          <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
          <!--            <a-form-item label="产品代码">-->
          <!--              <a-input placeholder="请输入产品代码" v-model="queryParam.productCode"></a-input>-->
          <!--            </a-form-item>-->
          <!--          </a-col>-->
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="品名">
              <a-input placeholder="请输入品名" v-model="queryParam.productName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="材质">
              <a-input placeholder="请输入材质" v-model="queryParam.steelGradeName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="产地">
              <a-input placeholder="请输入产地" v-model="queryParam.steelMillsName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="长度">
              <a-input placeholder="请输入长度" v-model="queryParam.length"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="宽度">
              <a-input placeholder="请输入宽度" v-model="queryParam.width"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="厚度">
              <a-input placeholder="请输入厚度" v-model="queryParam.thick"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="系统产品名称">
              <a-input placeholder="请输入系统产品名称" v-model="queryParam.sysProductName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="系统牌号">
              <a-input placeholder="请输入系统牌号" v-model="queryParam.sysSgSign"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="系统厚度">
              <a-input placeholder="请输入系统厚度" v-model="queryParam.sysThick"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="系统宽度">
              <a-input placeholder="请输入系统宽度" v-model="queryParam.sysWidth"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="系统长度">
              <a-input placeholder="请输入系统长度" v-model="queryParam.sysLength"></a-input>
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
      <!--      <a-button type="primary" icon="download" @click="handleExportXls('物流园材料基础信息')">导出</a-button>-->
      <!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
      <!--        <a-button type="primary" icon="import">导入</a-button>-->
      <!--      </a-upload>-->
      <a-button @click="getLogisticsParkMatBasicInformation" type="primary" icon="plus">获取物流园物料基础信息</a-button>
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

    <logistics-park-mat-basic-information-modal ref="modalForm"
                                                @ok="modalFormOk"></logistics-park-mat-basic-information-modal>
    <j-modal
      :visible="steelVisible"
      title="产地"
      width="60%"
      @ok="handleSteelOK"
      @cancel="handleCancel"
    >
      <a-row :gutter="24">
        <a-col :xl="6" :lg="7" :md="8" :sm="24">
          <a-form-item label="产地">
            <a-input v-model="steelMillsName"></a-input>
          </a-form-item>
        </a-col>
      </a-row>

    </j-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import LogisticsParkMatBasicInformationModal from './modules/LogisticsParkMatBasicInformationModal'
  import { getAction } from '@/api/manage'

  export default {
    name: 'LogisticsParkMatBasicInformationList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      LogisticsParkMatBasicInformationModal
    },
    data() {
      return {
        description: '物流园材料基础信息管理页面',
        // 表头
        columns: [
          // {
          //   title: '#',
          //   dataIndex: '',
          //   key:'rowIndex',
          //   width:60,
          //   align:"center",
          //   customRender:function (t,r,index) {
          //     return parseInt(index)+1;
          //   }
          // },
          // {
          //   title:'产品代码',
          //   align:"center",
          //   dataIndex: 'productCode'
          // },
          {
            title: '品名',
            align: 'center',
            dataIndex: 'productName'
          },
          {
            title: '材质',
            align: 'center',
            dataIndex: 'steelGradeName'
          },
          {
            title: '产地',
            align: 'center',
            dataIndex: 'steelMillsName'
          },
          {
            title: '生成标准',
            align: 'center',
            dataIndex: 'standardName'
          },
          {
            title: '长度',
            align: 'center',
            dataIndex: 'length'
          },
          {
            title: '宽度',
            align: 'center',
            dataIndex: 'width'
          },
          {
            title: '厚度',
            align: 'center',
            dataIndex: 'thick'
          },
          {
            title: '包装',
            align: 'center',
            dataIndex: 'packageCount'
          },
          {
            title: '单重',
            align: 'center',
            dataIndex: 'singleWeight'
          },
          {
            title: '计重方式',
            align: 'center',
            dataIndex: 'weightMode'
          },
          {
            title: '数量单位',
            align: 'center',
            dataIndex: 'numberUnit'
          },
          {
            title: '重量单位',
            align: 'center',
            dataIndex: 'weightUnit'
          },
          {
            title: '系统产品名称',
            align: 'center',
            dataIndex: 'sysProductName'
          },
          {
            title: '系统牌号',
            align: 'center',
            dataIndex: 'sysSgSign'
          },
          {
            title: '系统厚度',
            align: 'center',
            dataIndex: 'sysThick'
          },
          {
            title: '系统宽度',
            align: 'center',
            dataIndex: 'sysWidth'
          },
          {
            title: '系统长度',
            align: 'center',
            dataIndex: 'sysLength'
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
        steelMillsName: '',
        steelVisible: false,
        url: {
          list: '/sm/logisticsParkMatBasicInformation/list',
          delete: '/sm/logisticsParkMatBasicInformation/delete',
          deleteBatch: '/sm/logisticsParkMatBasicInformation/deleteBatch',
          exportXlsUrl: '/sm/logisticsParkMatBasicInformation/exportXls',
          importExcelUrl: 'sm/logisticsParkMatBasicInformation/importExcel',
          getLogisticsParkMatBasicInformation: 'sm/logisticsParkMatBasicInformation/getLogisticsParkMatBasicInformation'

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
      getLogisticsParkMatBasicInformation() {
        this.steelVisible = true
      },
      handleSteelOK() {
        debugger
        var that = this
        var steelMillsName = that.steelMillsName
        getAction(this.url.getLogisticsParkMatBasicInformation, { steelMillsName }).then((res) => {
          if (res.message === '操作成功') {
            that.$message.success(res.message)
          } else {
            that.$message.warning(res.message)
          }
        }).finally(() => {
          that.loadData()
          this.steelVisible = false
          that.steelMillsName = ''
        })
      },
      handleCancel() {
        this.steelVisible = false
        this.steelMillsName = ''
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>