<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="牌号">
              <a-input placeholder="请输入牌号" v-model="queryParam.sgSign"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="产品大类代码">
              <j-dict-select-tag placeholder="请选择产品大类代码" v-model="queryParam.prodClassCode" dictCode="prod_code"/>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
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
              <a-form-item label="材料宽度">
                <a-input placeholder="请输入材料宽度" v-model="queryParam.matWidth"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="材料厚度">
                <a-input placeholder="请输入材料厚度" v-model="queryParam.matThick"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="实际长度">
                <a-input placeholder="请输入实际长度" v-model="queryParam.matActLen"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="新增类型">
                <a-input placeholder="请输入新增类型" v-model="queryParam.addType"></a-input>
              </a-form-item>
            </a-col>
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
      <a-button @click="handleAllot" type="primary" >分货</a-button>

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

    <div>
      <j-modal
        :visible="allotTable"
        title="分货"
        width="60%"
        @ok="handleAllotOK"
        @cancel="handleAllotCancel"
      >
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="客户名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag :disabled="editType" v-decorator="['customerId',validatorRules.customerId]"
                                   
                                   v-model="customValue"
                                   dict="cm_customer_profile where del_flag=0,company_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入备注" v-model="remarks"></a-input>
            </a-form-item>
          </a-col>
        </a-row>
        <!-- <div>
          <discharger-info-modal ref="discharger" @ok="discharger" :disabled="disableSubmit"></discharger-info-modal>
        </div> -->
        <a-table
          rowKey="id"
          size="middle"
          :scroll="{x:true}"
          bordered
          :columns="allotTableColumns"
          :dataSource="selectionRows"
          :pagination="tableIpagination"
          :loading="loading"
          class="j-matTable-force-nowrap">
          <template v-for="(col) in ['matNum']" :slot="col" slot-scope="text, record, index">
            <a-input
              :key="col"
              v-if="record.editable && col==='matNum'"
              :value="text"
              :placeholder="allotTableColumns.title='请输入件数'"
              @change="e => handleChange(e.target.value, record.id, col)"
            />
            <template v-else><span>{{ text }}</span></template>
          </template>
          <template slot="allotAction" slot-scope="text, record, index">
            <template v-if="record.editable">
              <span>
                <a @click="saveRow(record.id)">保存</a>
                <a-divider type="vertical" />
                <a @click="cancel(record.id)">取消</a>
              </span>
            </template>
            <span v-else>
              <a @click="toggle(record.id)">编辑</a>
            </span>
          </template>
        </a-table>
      </j-modal>
    </div>
    <stock-allot-modal ref="modalForm" @ok="modalFormOk"></stock-allot-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import StockAllotModal from './modules/StockAllotModal'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import { filterMultiDictText, filterDictTextByCache } from '@/components/dict/JDictSelectUtil'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import JDate from '@/components/jeecg/JDate.vue'
  import JInput from '@/components/jeecg/JInput'
  import Template1 from "../../jeecg/JVxeDemo/layout-demo/Template1";
  import { getAction, postAction } from '@api/manage'

  export default {
    name: 'StockAllotList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      JDictSelectTag,
      StockAllotModal,
      Template1,
      JSearchSelectTag,
      JInput,
      JDate
    },
    data () {
      return {
        allotTable:false,
        remarks:"",
        selectValue:"",
        customValue:"",
        previousData: [],
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },
        description: '物料信息表管理页面',
        validatorRules: {
          customerId: {
            rules: [
              {required: true, message: '请选择客户!'}
            ]
          },
        },
        tableIpagination: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['5', '10', '50'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
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
          title: '分货状态',
          align: 'center',
          dataIndex: 'allotStatus',
          width:160,
          customRender: (text) => {
            //字典值翻译通用方法
            return filterDictTextByCache('allot_status', text)
            }
          },
          {
            title:'牌号',
            align:"center",
            width:60,
            dataIndex: 'sgSign'
          },
          {
            title:'品名代码',
            align:"center",
            dataIndex: 'prodCode'
          },
          {
            title:'品名中文',
            align:"center",
            dataIndex: 'prodCname'
          },
          {
            title:'产品大类代码',
            align:"center",
            dataIndex: 'prodClassCode_dictText'
          },
          {
            title:'品名中文别名',
            align:"center",
            dataIndex: 'prodCnameOther'
          },
          {
            title:'产品状态码',
            align:"center",
            dataIndex: 'matStatus'
          },
          {
            title:'仓库id',
            align:"center",
            dataIndex: 'stockHouseId_dictText'
          },
          {
            title:'库存类型',
            align:"center",
            dataIndex: 'stockType_dictText'
          },
          {
            title:'材料号',
            align:"center",
            dataIndex: 'matNo'
          },
          {
            title:'材料实际重量',
            align:"center",
            dataIndex: 'matActWt'
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
            title:'可销售重量',
            align:"center",
            dataIndex: 'availableWeight'
          },
          {
            title:'可销售数量',
            align:"center",
            dataIndex: 'availableQty'
          },
          {
            title:'预用重量',
            align:"center",
            dataIndex: 'preuseWeight'
          },
          {
            title:'预用数量',
            align:"center",
            dataIndex: 'preuseQty'
          },
          {
            title:'材料长度',
            align:"center",
            dataIndex: 'matLen'
          },
          {
            title:'材料宽度',
            align:"center",
            dataIndex: 'matWidth'
          },
          {
            title:'材料厚度',
            align:"center",
            dataIndex: 'matThick'
          },
          {
            title:'实际长度',
            align:"center",
            dataIndex: 'matActLen'
          },
          {
            title:'实际宽度',
            align:"center",
            dataIndex: 'matActWidth'
          },
          {
            title:'实际厚度',
            align:"center",
            dataIndex: 'matActThick'
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
            dataIndex: 'addType'
          },
          {
            title:'材料理论重量',
            align:"center",
            dataIndex: 'matTheoryWt'
          },
          {
            title:'码单号',
            align:"center",
            dataIndex: 'stackingNo'
          },
          {
            title:'计重方式',
            align:"center",
            dataIndex: 'wtMode'
          },
          {
            title:'产品等级',
            align:"center",
            dataIndex: 'prodClass'
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
        // 分货框
        allotTableColumns: [
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
            title: '牌号',
            align: 'center',
            dataIndex: 'sgSign'
          },
          {
            title: '材料号',
            align: 'center',
            dataIndex: 'matNo'
          },
          {
            title: '品名中文',
            align: 'center',
            dataIndex: 'prodCname'
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
            title: '品名中文别名',
            align: 'center',
            dataIndex: 'prodCnameOther'
          },
          {
            title: '材料厚度',
            align: 'center',
            dataIndex: 'matThick'
          },
          {
            title: '材料宽度',
            align: 'center',
            dataIndex: 'matWidth'
          },
          {
            title: '材料长度',
            align: 'center',
            dataIndex: 'matLen'
          },
          {
            title: '计重方式',
            align: 'center',
            dataIndex: 'wtMode_dictText',
            editable: false,
            scopedSlots: {customRender: 'wtMode'}
          },
          {
            title: '材料净重',
            align: 'center',
            dataIndex: 'matNetWt'
          },
          {
            title: '成本价',
            align: 'center',
            dataIndex: 'costPrice',
            width: 125,
            editable: false,
            scopedSlots: {customRender: 'costPrice'}
          },
          {
            title: '件数',
            width: 100,
            align: 'center',
            dataIndex: 'matNum',
            editable: false,
            scopedSlots: {customRender: 'matNum'}
          },
          {
            title: '操作',
            align:"center",
            width:100,
            scopedSlots: { customRender: 'allotAction' }
          }
        ],

        url: {
          list: "/sm/stockAllot/showList",
          delete: "/sm/stockAllot/delete",
          deleteBatch: "/sm/stockAllot/deleteBatch",
          exportXlsUrl: "/sm/stockAllot/exportXls",
          importExcelUrl: "sm/stockAllot/importExcel",
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
    props: {
      //流程表单data
      formData: {
        type: Object,
        default: () => {
        },
        required: false
      },
      //表单模式：false流程表单 true普通表单
      formBpm: {
        type: Boolean,
        default: false,
        required: false
      },
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      },
      editType: {
        type: Boolean,
        default: false,
        required: false
      },
      addShow: {
        type: Boolean,
        default: false,
        required: false
      },
      newEditType: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    methods: {
      initDictConfig(){
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedMainId = selectedRowKeys[0]
        this.selectedRowKeys = selectedRowKeys
        this.selectionRows = selectionRows
      },
      toggle(key) {
        let that = this
        let target = this.selectionRows.filter(item => {
          if (item.id === key) {
            that.previousData.push({id: key, value: item.matNum})
            return true
          }
          return false
        })[0]
        target.editable = true;
        this.$nextTick(() => {
          this.reloadData();
        })
      },
      handleChange(value, key, column) {
        const newData = [...this.selectionRows]
        const target = newData.filter(item => key === item.id)[0]
        if (target) {
          target[column] = value
          this.selectionRows = newData
        }
      },
      cancel(key) {
        let that = this
        let target = this.selectionRows.filter(item => item.id === key)[0]
        let matNum = 0;
        for (let i = 0; i < that.previousData.length; i++) {
          if (that.previousData[i].id === key) matNum = that.previousData[i].value
        }
        target.editable = false
        target.matNum = matNum
        this.$nextTick(() => {
          this.reloadData();
        })
      },
      saveRow(key) {
        let target = this.selectionRows.filter(item => item.id === key)[0]
        target.editable = false
        this.$nextTick(() => {
          this.reloadData();
        })
      },
      handleAllot(){
        if (this.selectionRows.length === 0){
          return this.$message.warning("请勾选条目");
        }

        for (let i = 0; i < this.selectionRows.length;i++){

          if (this.selectionRows[i].allotStatus === 'alloted'){

            return this.$message.warning("勾选的第"+ (i+1) +"条记录已完成分货");
          }
        }
        this.allotTable = true;
      },
      handleAllotCancel(){
        this.allotTable = false;
        this.selectValue = "";
      },
      handleAllotOK(){

      },
      reloadData() {
        debugger
        const newData = [...this.selectionRows];
        this.selectionRows = newData;
      },
      handlePrepareOrder(){

      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>