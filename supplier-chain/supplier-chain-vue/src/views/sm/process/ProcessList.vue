<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="品名中文">
              <j-input placeholder="请输入品名中文" v-model="queryParam.prodCname"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="品名中文别名">
              <j-input placeholder="请输入品名中文别名" v-model="queryParam.prodCnameOther"></j-input>
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
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="仓库">
              <j-dict-select-tag placeholder="请选择仓库" v-model="queryParam.stockHouseId" dictCode="sm_stock,name,id"/>
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
<!--          <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
<!--            <a-form-item label="新增类型">-->
<!--              <j-dict-select-tag placeholder="请选择新增类型" v-model="queryParam.addType"-->
<!--                                 dictCode="mat_type"></j-dict-select-tag>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="创建日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择开始时间" class="query-group-cust" v-model="queryParam.createTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择结束时间" class="query-group-cust" v-model="queryParam.createTime_end"></j-date>
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
      <a-button v-has="'sm:machining'" @click="machining" type="primary">加工</a-button>
      <a-button v-has="'sm:machiningYrm'" @click="machiningYrm" type="primary">加工</a-button>
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
<!--          <a @click="handleEdit(record)">编辑</a>-->
          <a @click="handleDetail(record)">详情</a>
<!--          <a-divider type="vertical" />-->
<!--          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">-->
<!--                  <a v-has="'sm:add'">删除</a>-->
<!--                </a-popconfirm>-->
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

    <j-modal
      :visible="modal.visible"
      :title="modal.title"
      width=90%
      @ok = "handleOk"
      @cancel="handleCancel"

    >
      <a-row :gutter="24">
        <a-col :span="30">
          <a-table
            size="middle"
            :scroll="{x:true}"
            bordered
            :columns="machiningColumns"
            :dataSource="machiningDataSource"
            :loading="loading"
            class="j-matTable-force-nowrap">
          </a-table>
        </a-col>
      </a-row>
    </j-modal>

    <process-modal ref="modalForm" @ok="modalFormOk" :selectionRecord="selectionRows[0]"></process-modal>
    <process-detail-form ref="detailForm"></process-detail-form>
    <process-add-detail-modal ref="modalForm1" @ok="modalFormOk" :selectionRecord="selectionRows"></process-add-detail-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import ProcessModal from './modules/ProcessModal'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import { filterDictTextByCache, filterMultiDictText } from '@/components/dict/JDictSelectUtil'
  import JDate from '@/components/jeecg/JDate.vue'
  import ProcessDetailForm from '@views/sm/process/modules/ProcessDetailForm'
  import ProcessAddDetailForm from '@views/sm/process/modules/ProcessAddDetailForm'
  import {getAction, postAction} from "../../../api/manage";
  import JInput from '@/components/jeecg/JInput'
  import ProcessAddDetailModal from "./modules/ProcessAddDetailModal";


  export default {
    name: 'ProcessList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      ProcessAddDetailModal,
      JDictSelectTag,
      ProcessModal,
      JDate,
      JInput,
      ProcessDetailForm,
      ProcessAddDetailForm
    },
    data () {
      return {
        machiningDataSource: [],
        modal: {
          title: '加工信息',
          visible: false,
          fullscreen: false,
          switchFullscreen: false,
          okClose: false,
        },
        description: '物料信息表管理页面',
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
            title:'创建日期',
            align:"center",
            dataIndex: 'createTime'
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
            dataIndex: 'prodClassCode_dictText'
          },
          {
            title:'品名中文别名',
            align:"center",
            dataIndex: 'prodCnameOther'
          },
          {
            title:'仓库',
            align:"center",
            dataIndex: 'stockHouseId_dictText'
          },
          // {
          //   title:'库存类型',
          //   align:"center",
          //   dataIndex: 'stockType_dictText'
          // },
          {
            title:'材料号',
            align:"center",
            dataIndex: 'matNo'
          },
          // {
          //   title:'材料实际重量',
          //   align:"center",
          //   dataIndex: 'matActWt'
          // },
          // {
          //   title:'材料净重',
          //   align:"center",
          //   dataIndex: 'matNetWt'
          // },
          // {
          //   title:'材料磅差重量',
          //   align:"center",
          //   dataIndex: 'matDiscrepWt'
          // },
          {
            title:'材料件数（根数）',
            align:"center",
            dataIndex: 'matNum'
          },
          {
            title:'材料种类',
            align:"center",
            dataIndex: 'matKind_dictText'
          },
          // {
          //   title:'可销售数量',
          //   align:"center",
          //   dataIndex: 'availableQty'
          // },
          // {
          //   title:'预用数量',
          //   align:"center",
          //   dataIndex: 'preuseQty'
          // },
          // {
          //   title:'预用重量',
          //   align:"center",
          //   dataIndex: 'preuseWeight'
          // },
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
          },          // {
          //   title:'实际长度',
          //   align:"center",
          //   dataIndex: 'matActLen'
          // },
          // {
          //   title:'实际宽度',
          //   align:"center",
          //   dataIndex: 'matActWidth'
          // },
          // {
          //   title:'实际厚度',
          //   align:"center",
          //   dataIndex: 'matActThick'
          // },
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
            title:'技术标准',
            align:"center",
            dataIndex: 'technicalStandard'
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
            title:'原材料号',
            align:"center",
            dataIndex: 'rawMaterialNo'
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
        machiningColumns: [
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
            title:'创建日期',
            align:"center",
            dataIndex: 'createTime'
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
            dataIndex: 'prodClassCode_dictText'
          },
          {
            title:'品名中文别名',
            align:"center",
            dataIndex: 'prodCnameOther'
          },
          {
            title:'仓库',
            align:"center",
            dataIndex: 'stockHouseId_dictText'
          },
          // {
          //   title:'库存类型',
          //   align:"center",
          //   dataIndex: 'stockType_dictText'
          // },
          {
            title:'材料号',
            align:"center",
            dataIndex: 'matNo'
          },
          // {
          //   title:'材料净重',
          //   align:"center",
          //   dataIndex: 'matNetWt'
          // },
          // {
          //   title:'材料磅差重量',
          //   align:"center",
          //   dataIndex: 'matDiscrepWt'
          // },
          {
            title:'材料件数（根数）',
            align:"center",
            dataIndex: 'matNum'
          },
          {
            title:'材料种类',
            align:"center",
            dataIndex: 'matKind_dictText'
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
            title:'技术标准',
            align:"center",
            dataIndex: 'technicalStandard'
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
            title:'原材料号',
            align:"center",
            dataIndex: 'rawMaterialNo'
          },
        ],
        url: {
          list: "/sm/process/list",
          delete: "/sm/process/delete",
          deleteBatch: "/sm/process/deleteBatch",
          exportXlsUrl: "/sm/process/exportXls",
          importExcelUrl: "sm/process/importExcel",
          machining: "/sm/process/add",

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
      },
      processMat(){
        if (this.selectionRows.length === 0) {
          this.$message.warning('请勾选一条记录！')
        }else {
          this.$refs.modalForm.add();
          this.$refs.modalForm.title = "加工";
          this.$refs.modalForm.disableSubmit = false;
        }
      },
      handleDetail: function (record) {
        this.$refs.detailForm.edit(record)
      },
      machining(){
        if(this.selectedRowKeys.length == 0 ){
          this.$message.warning("请勾选要加工材料！")
        }else {
          this.modal.visible=true;
          this.machiningDataSource = this.selectionRows;
        }
      },
      machiningYrm() {
        if(this.selectedRowKeys.length == 0 ) {
          this.$message.warning("请勾选要加工材料！")
        } else if (this.selectedRowKeys.length > 1) {
          this.$message.warning("请勾选一条加工材料！")
        } else {
          this.$refs['modalForm1'].visible= true;
        }

      },
      handleOk() {
        debugger
        // let params = {id:}
        postAction(this.url.machining,this.selectedRowKeys).then(res=>{
            if(res.success){
              this.$message.success("加工成功")
              this.loadData();
            }else{
              this.$message.warning(res.message)
            }
          });
        this.selectedRowKeys = [];
        this.selectValue = '';
        this.modal.visible=false;
      },
      handleCancel(){
        this.selectValue = '';
        this.selectedRowKeys = [];
        this.modal.visible=false;
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>
<style type="text/css">
  .ant-table-fixed {font-size: 13px;}
</style>