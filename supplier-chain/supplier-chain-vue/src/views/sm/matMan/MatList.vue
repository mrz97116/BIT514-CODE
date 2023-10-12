<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="牌号">
              <j-input placeholder="请输入牌号" v-model="queryParam.sgSign"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24" v-has="'sm:goodsNo'">
            <a-form-item label="货物编码">
              <j-input placeholder="请输入货物编码" v-model="queryParam.goodsNo"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="产品大类">
              <j-dict-select-tag placeholder="请选择产品大类" v-model="queryParam.prodClassCode" dictCode="prod_code"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="库存类型">
              <j-dict-select-tag placeholder="请选择库存类型" v-model="queryParam.stockType" dictCode="stock_type"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料种类">
              <j-dict-select-tag placeholder="请选择材料种类" v-model="queryParam.matKind" dictCode="mat_kind"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料厚度">
              <a-input placeholder="请输入材料厚度" v-model="queryParam.matThick"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料宽度">
              <a-input placeholder="请输入材料宽度" v-model="queryParam.matWidth"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料长度">
              <a-input placeholder="请输入材料长度" v-model="queryParam.matLen"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="新增类型">
              <j-dict-select-tag placeholder="请选择新增类型" v-model="queryParam.addType"
                                 dictCode="mat_type"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="创建日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开始时间" class="query-group-cust" v-model="queryParam.createTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择结束时间" class="query-group-cust" v-model="queryParam.createTime_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="入库时间">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开始时间" class="query-group-cust" v-model="queryParam.storageTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择结束时间" class="query-group-cust" v-model="queryParam.storageTime_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料号">
              <a-input placeholder="请输入材料号" v-model="queryParam.matNo"></a-input>
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
      <!--<a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>-->
<!--      <a-button type="primary" icon="download" @click="handleExportXls('物料信息表')">导出</a-button>-->
<!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
<!--        <a-button type="primary" icon="import">导入</a-button>-->
<!--      </a-upload>-->
      <a-dropdown v-has="'sm:add'" v-if="selectedRowKeys.length > 0">
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
          <a @click="handleDetail(record)">详情</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a v-has="'sm:add'">删除</a>
          </a-popconfirm>
<!--          <a-dropdown>-->
<!--            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>-->
<!--            <a-menu slot="overlay">-->
<!--              <a-menu-item>-->
<!--                <a @click="handleDetail(record)">详情</a>-->
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

    <mat-modal ref="modalForm" @ok="modalFormOk"></mat-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import MatModal from './modules/MatModal'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import {filterMultiDictText, filterDictTextByCache} from '@/components/dict/JDictSelectUtil'
  import JDate from '@/components/jeecg/JDate.vue'
  import JInput from '@/components/jeecg/JInput'
  import { colAuthFilter } from '@/utils/authFilter'


  export default {
    name: 'MatList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      JDictSelectTag,
      MatModal,
      JInput,
      JDate
    },
    data () {
      return {
        visible: false,
        description: '物料信息表管理页面',
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
            title: '创建日期',
            align: "center",
            dataIndex: 'createTime',
          },
          {
            title:'牌号',
            align:"center",
            dataIndex: 'sgSign'
          },
          {
            title:'品名中文',
            align:"center",
            dataIndex: 'prodCname'
          },
          {
            title:'产品大类',
            align:"center",
            dataIndex: 'prodClassCode',
            customRender: (text) => {
              //字典值翻译通用方法
              return filterDictTextByCache('prod_code', text);
            }
          },
          {
            title:'品名中文别名',
            align:"center",
            dataIndex: 'prodCnameOther'
          },
          {
            title:'仓库',
            align:"center",
            dataIndex: 'stockHouseId'
          },
          {
            title:'库存类型',
            align:"center",
            dataIndex: 'stockType',
            customRender: (text) => {
              //字典值翻译通用方法
              return filterDictTextByCache('stock_type', text);
            }
          },
          {
            title:'材料号',
            align:"center",
            dataIndex: 'matNo'
          },
          {
            title:'材料净重',
            align:"center",
            dataIndex: 'matNetWt'
          },
          {
            title:'材料磅差重量',
            align:"center",
            dataIndex: 'matDiscrepWt'
          },
          {
            title:'材料件数',
            align:"center",
            dataIndex: 'matNum'
          },
          {
            title:'材料种类',
            align:"center",
            dataIndex: 'matKind',
            customRender: (text) => {
              //字典值翻译通用方法
              return filterDictTextByCache('mat_kind', text)
            }
          },
          {
            title:'材料厚度',
            align:"center",
            dataIndex: 'matThick'
          },
          {
            title:'材料宽度',
            align:"center",
            dataIndex: 'matWidth'
          },
          {
            title:'材料长度',
            align:"center",
            dataIndex: 'matLen'
          },
          {
            title:'货物编码',
            align:"center",
            dataIndex: 'goodsNo'
          },
          {
            title:'规格',
            align:"center",
            dataIndex: 'custMatSpecs'
          },
          {
            title:'采购价',
            align:"center",
            dataIndex: 'purchasePrice'
          },
          {
            title:'成本价',
            align:"center",
            dataIndex: 'costPrice'
          },
          {
            title:'技术标准',
            align:"center",
            dataIndex: 'technicalStandard'
          },
          {
            title:'原材料号',
            align:"center",
            dataIndex: 'rawMaterialNo'
          },
          {
            title:'新增类型',
            align:"center",
            dataIndex: 'addType',
            customRender: (text) => {
              //字典值翻译通用方法
              return filterDictTextByCache('mat_type', text);
            }
          },
          {
            title:'库区',
            align:"center",
            dataIndex: 'stockLocation'
          },
          {
            title:'入库单号',
            align:"center",
            dataIndex: 'stockNo'
          },
          {
            title:'入库时间',
            align:"center",
            dataIndex: 'storageTime'
          },
          {
            title:'卸货人',
            align:"center",
            dataIndex: 'dischargerName'
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
          list: "/sm/mat/list",
          delete: "/sm/mat/delete",
          deleteBatch: "/sm/mat/deleteBatch",
          exportXlsUrl: "/sm/mat/exportXls",
          importExcelUrl: "sm/mat/importExcel",

        },
        dictOptions:{},
      }
    },
    created() {
      this.columns = colAuthFilter(this.columns,'sm:');
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig(){
      },
      onSelectChange(selectedRowKeys){
        this.selectedRowKeys = selectedRowKeys;
      },
      onClearSelected(){
        this.selectedRowKeys=[];
      },
      handleEdit: function (record) {
        this.$refs.modalForm.edit(record);
        this.$refs.modalForm.title = "详情";
        this.$refs.modalForm.disableSubmit = false;
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>