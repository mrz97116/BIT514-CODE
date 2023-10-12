<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
<!--          <a-col :xl="10" :lg="11" :md="12" :sm="24">-->
<!--            <a-form-item label="创建日期">-->
<!--              <j-date placeholder="请选择开始日期" class="query-group-cust" v-model="queryParam.createTime_begin"></j-date>-->
<!--              <span class="query-group-split-cust"></span>-->
<!--              <j-date placeholder="请选择结束日期" class="query-group-cust" v-model="queryParam.createTime_end"></j-date>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
<!--          <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
<!--            <a-form-item label="物料">-->
<!--              <a-input placeholder="请输入物料" v-model="queryParam.matName"></a-input>-->
<!--            </a-form-item>-->
<!--          </a-col>-->

          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="时间">
              <j-date placeholder="请选择开始日期" class="query-group-cust" v-model="queryParam.predictTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date placeholder="请选择结束日期" class="query-group-cust" v-model="queryParam.predictTime_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="生产线">
              <a-input placeholder="请输入生产线" v-model="queryParam.prodLine"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="品名中文">
              <a-input placeholder="请输入品名中文" v-model="queryParam.prodCname"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="钢号">
              <a-input placeholder="请输入钢号" v-model="queryParam.sgSign"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="部门">
              <a-input placeholder="请输入部门" v-model="queryParam.department"></a-input>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="规格">
                <a-input placeholder="请输入规格" v-model="queryParam.custMatSpecs"></a-input>
              </a-form-item>
            </a-col>
<!--            <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
<!--              <a-form-item label="计量单位">-->
<!--                <a-input placeholder="请输入计量单位" v-model="queryParam.measuringUnit"></a-input>-->
<!--              </a-form-item>-->
<!--            </a-col>-->
<!--            <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
<!--              <a-form-item label="数量">-->
<!--                <a-input placeholder="请输入数量" v-model="queryParam.matNum"></a-input>-->
<!--              </a-form-item>-->
<!--            </a-col>-->
<!--            <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
<!--              <a-form-item label="单价">-->
<!--                <a-input placeholder="请输入单价" v-model="queryParam.predictPrice"></a-input>-->
<!--              </a-form-item>-->
<!--            </a-col>-->
<!--            <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
<!--              <a-form-item label="材料厚度">-->
<!--                <a-input placeholder="请输入材料厚度" v-model="queryParam.matThick"></a-input>-->
<!--              </a-form-item>-->
<!--            </a-col>-->
<!--            <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
<!--              <a-form-item label="材料长度">-->
<!--                <a-input placeholder="请输入材料长度" v-model="queryParam.matLen"></a-input>-->
<!--              </a-form-item>-->
<!--            </a-col>-->
<!--            <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
<!--              <a-form-item label="材料宽度">-->
<!--                <a-input placeholder="请输入材料宽度" v-model="queryParam.matWidth"></a-input>-->
<!--              </a-form-item>-->
<!--            </a-col>-->
          </template>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('预测单价表')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
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

    <fm-predict-price-modal ref="modalForm" @ok="modalFormOk"></fm-predict-price-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import FmPredictPriceModal from './modules/FmPredictPriceModal'
  import JDate from '@/components/jeecg/JDate.vue'

  export default {
    name: 'FmPredictPriceList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      JDate,
      FmPredictPriceModal
    },
    data () {
      return {
        description: '预测单价表管理页面',
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
          // {
          //   title:'创建日期',
          //   align:"center",
          //   dataIndex: 'createTime',
          //   customRender:function (text) {
          //     return !text?"":(text.length>10?text.substr(0,10):text)
          //   }
          // },
          {
            title:'物料',
            align:"center",
            dataIndex: 'matCode'
          },
          {
            title:'预测时间',
            align:"center",
            dataIndex: 'predictTime',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          },
          {
            title:'品名中文',
            align:"center",
            dataIndex: 'prodCname'
          },
          {
            title:'钢号',
            align:"center",
            dataIndex: 'sgSign'
          },
          // {
          //   title:'规格',
          //   align:"center",
          //   dataIndex: 'custMatSpecs'
          // },
          {
            title:'部门',
            align:"center",
            dataIndex: 'department'
          },
          {
            title:'计量单位',
            align:"center",
            dataIndex: 'measuringUnit'
          },
          {
            title:'生产线',
            align:"center",
            dataIndex: 'prodLine'
          },
          {
            title:'数量',
            align:"center",
            dataIndex: 'matNum'
          },
          {
            title:'单价',
            align:"center",
            dataIndex: 'predictPrice'
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
          list: "/fm/fmPredictPrice/list",
          delete: "/fm/fmPredictPrice/delete",
          deleteBatch: "/fm/fmPredictPrice/deleteBatch",
          exportXlsUrl: "/fm/fmPredictPrice/exportXls",
          importExcelUrl: "fm/fmPredictPrice/importExcel",

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
    methods: {
      initDictConfig(){
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>