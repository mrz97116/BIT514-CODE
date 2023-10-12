<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="产品大类">
              <j-dict-select-tag placeholder="请选择产品大类" v-model="queryParam.prodClassCode" dictCode="prod_code"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料厚度">
              <a-input placeholder="请输入材料厚度" v-model="queryParam.matThick"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="物料种类">
              <j-dict-select-tag placeholder="请选择物料种类" v-model="queryParam.matKind" dictCode="mat_kind"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="品名中文">
              <j-input placeholder="请输入品名中文" v-model="queryParam.prodCname"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24" v-has="'sm:goodsNo'">
            <a-form-item label="货物编码">
              <j-input placeholder="请输入货物编码" v-model="queryParam.goodsNo"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="仓库">
              <j-dict-select-tag placeholder="请选择仓库" v-model="queryParam.stockHouseId" dictCode="sm_stock,name,id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料宽度">
              <a-input placeholder="请输入材料宽度" v-model="queryParam.matWidth"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="品名中文别名">
              <j-input placeholder="请输入品名中文别名" v-model="queryParam.prodCnameOther"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="牌号">
              <j-input placeholder="请输入牌号" v-model="queryParam.sgSign"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="备注">
              <j-input placeholder="请输入备注" v-model="queryParam.remark"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料长度">
              <a-input placeholder="请输入材料长度" v-model="queryParam.matLen"></a-input>
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
          <a-col v-has="'sm:prepareCustomerId_dictText'" :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="冷卷分货客户">
              <j-search-select-tag placeholder="请选择顾客" v-model="queryParam.prepareCustomerId"
                                   dict="sm_prepare_customer where del_flag=0,company_name,customer_id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="库位">
              <a-textarea placeholder="请输入库位" v-model="queryParam.stockLocations"></a-textarea>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
          <a-form-item label="材料号">
            <a-textarea placeholder="请输入材料号" v-model="queryParam.matNos"></a-textarea>
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
      <a-button type="primary" icon="edit" @click="handleAllot">调拨</a-button>
    <!--      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>-->
      <a-button type="primary" icon="download" @click="handleExportXls('库存信息')">导出</a-button>
      <a-button v-has="'sm:inventoryLock'" type="primary" icon="edit" @click="locking()">锁定</a-button>
      <a-button v-has="'sm:guangdong'" type="primary" icon="edit" @click="prepareInventory()">自动分货</a-button>
      <a-button  v-has="'sm:yrmInventory'" type="primary" icon="edit" @click="yrmInventory()">库存报表查询</a-button>
      <a-button v-has="'sm:guangdong'" type="primary" icon="edit" @click="prepareCustomer()">选择客户分货</a-button>
      <!--<a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
      <!--<a-button type="primary" icon="import">导入</a-button>-->
      <!--</a-upload>-->
      <a-dropdown v-has="'sm:inventoryBatchOperation'" v-if="selectedRowKeys.length > 0">
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
        <span style="margin-left: 5%">勾选可售数：{{this.selectParams.availableQty}}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;勾选可售量：{{this.selectParams.availableWeight}}</span>
        <span style="margin-left: 35%">可售数：{{this.totalParams.availableQty}}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可售量：{{this.totalParams.availableWeight}}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;库存数：{{this.totalParams.matNum}}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;库存量：{{this.totalParams.weight}}</span>
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
        :customRow="customRow"
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
          <a v-has="'sm:add'" @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"/>
          <a @click="handleDetail(record)">详情</a>
        </span>

      </a-table>
    </div>

    <div>
      <j-modal
        :visible="pcVisible"
        title="选择客户分货"
        width="60%"
        @ok="handleOK1"
        @cancel="handleCancel"
      >
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="分货客户" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag placeholder="请选择客户" v-model="prepareCustomerId" :dictOptions="searchOptions"
                                   dict="sm_prepare_customer where del_flag=0,company_name,customer_id">
              </j-search-select-tag>
            </a-form-item>
          </a-col>
        </a-row>
      </j-modal>
    </div>
    <div>
      <j-modal
        :visible="lockVisible"
        title="库存锁定"
        width="60%"
        @ok="handleOKLock"
        @cancel="handleCancel"
      >
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入锁定单备注" v-model="remark"></a-input>
            </a-form-item>
          </a-col>
        </a-row>
      </j-modal>
    </div>
    <div>
      <j-modal
        :visible="visible"
        title="调拨"
        width="60%"
        @ok="handleOK"
        @cancel="handleCancel"
      >
        <!-- 字典搜索  -->
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="仓库" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag placeholder="请选择仓库" v-model="selectValue" :dictOptions="searchOptions">
              </j-search-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="调拨数" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入要调拨的数量" v-model="allotNumber"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="库位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入库位" v-model="stockLocation"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入重量" v-model="allotWeight"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入备注" v-model="remarks"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="入库时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择入库时间" class="query-group-cust"
                      v-model="warehouseTime"></j-date>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="卸货人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-col :span="12">
                <a-input v-model="dischargerName"
                         placeholder="选择卸货人后自动填写"/>
              </a-col>
              <a-button @click="Discharger()" type="primary">添加卸货人信息</a-button>
            </a-form-item>
          </a-col>
        </a-row>
        <div>
          <discharger-info-modal ref="discharger" @ok="discharger" :disabled="disableSubmit"></discharger-info-modal>
        </div>
        <a-table
          rowKey="id"
          size="middle"
          :scroll="{x:true}"
          bordered
          :columns="tableColumns"
          :dataSource="selectionRows"

          :loading="loading"
          class="j-matTable-force-nowrap">
          <template v-for="(col) in ['costPrice','wtMode','stockLocation']" :slot="col"
                    slot-scope="text, record, index">
            <a-input
              :key="col"
              v-if="record.editable && col==='costPrice'"
              style="margin: -5px 0"
              :value="text"
              :placeholder="tableColumns.title='请输入成本价'"
              @change="e => handleChange(e.target.value, record.id, col)"
            />
            <j-dict-select-tag
              :key="col"
              v-else-if="record.editable && col==='wtMode'"
              v-model="text"
              dictCode="wt_method_code"
              @input="e => handleChange(e, record.id, col)"/>
            <template v-else><span>{{ text }}</span></template>
            <a-input
              :key="col"
              v-if="record.editable && col==='stockLocation'"
              style="margin: -5px 0"
              :value="text"
              :placeholder="tableColumns.title='请输入库位'"
              @change="e => handleChange(e.target.value, record.id, col)"
            />
          </template>
          <template slot="operation" slot-scope="text, record, index">
            <template v-if="record.editable">
              <span>
                <a @click="saveRow(record.id)">保存</a>
                <a-divider type="vertical"/>
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
    <inventory-modal ref="modalForm" @ok="modalFormOk"></inventory-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import common from '@views/base/common/common.js'
  import InventoryModal from './modules/InventoryModal'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import { filterMultiDictText, filterDictTextByCache } from '@/components/dict/JDictSelectUtil'
  import JDate from '@/components/jeecg/JDate.vue'
  import JInput from '@/components/jeecg/JInput'
  import { getAction, postAction } from '@api/manage'
  import { colAuthFilter } from '@/utils/authFilter'
  import DischargerInfoModal from '../stockMan/DischargerInfoList'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'

  export default {
    name: 'InventoryList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      JDictSelectTag,
      JSearchSelectTag,
      JInput,
      InventoryModal,
      JDate,
      DischargerInfoModal
    },
    data() {
      return {
        description: '库存信息管理页面',
        tenantId: '',
        visible: false,
        pcVisible: false,
        lockVisible: false,
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },
        // 表头
        selectValue: '',
        stockLocation: '',
        remarks: '',
        remark: '',
        allotNumber: '',
        allotWeight: '',
        dischargerName: '',
        searchOptions: [],
        disableSubmit: false,
        warehouseTime: '',
        prepareCustomerId: '',
        ipagination:{
          current: 1,
          pageSize: 100,
          pageSizeOptions: ['10', '100','300','500','1000','1500'],
          showTotal: (total, range) => {
            return range[0] + "-" + range[1] + " 共" + total + "条"
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
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
            title: '创建日期',
            align: 'center',
            dataIndex: 'createTime'
          },
          {
            title: '产品大类',
            align: 'center',
            dataIndex: 'prodClassCode_dictText'
          },
          {
            title: '牌号',
            align: 'center',
            dataIndex: 'sgSign'
          },
          {
            title: '产品名称',
            align: 'center',
            dataIndex: 'oldProdCname'
          },
          {
            title: '规格',
            align: 'center',
            dataIndex: 'custMatSpecs'
          },
          {
            title: '材料厚度',
            align: 'center',
            dataIndex: 'matThick',
            sorter: (a, b) => a.matThick - b.matThick
          },
          {
            title: '材料宽度',
            align: 'center',
            dataIndex: 'matWidth',
            sorter: (a, b) => a.matWidth - b.matWidth
          },
          {
            title: '材料长度',
            align: 'center',
            dataIndex: 'matLen'
          },
          {
            title: '材料号',
            align: 'center',
            dataIndex: 'matNo'
          },
          {
            title: '备注',
            align: 'center',
            dataIndex: 'remark'
          },
          {
            title: '仓库',
            align: 'center',
            dataIndex: 'stockHouseId_dictText'
          },
          {
            title: '可售数',
            align: 'center',
            dataIndex: 'availableQty'
          },
          {
            title: '可售量',
            align: 'center',
            dataIndex: 'availableWeight'
          },
          {
            title: '库存数',
            align: 'center',
            dataIndex: 'matNum'
          },
          {
            title: '库存量',
            align: 'center',
            dataIndex: 'weight'
          },
          {
            title: '库位',
            align: 'center',
            dataIndex: 'stockLocation'
          },
          {
            title: '在库时长(天)',
            align: 'center',
            dataIndex: 'inStockDays'
          },
          // {
          //   title: '锁定数',
          //   align: 'center',
          //   dataIndex: 'lockQty'
          // },
          // {
          //   title: '锁定量',
          //   align: 'center',
          //   dataIndex: 'lockWeight'
          // },
          {
            title: '计重',
            align: 'center',
            dataIndex: 'wtMode_dictText'
          },
          {
            title: '单重',
            align: 'center',
            dataIndex: 'pieceWeight'
          },
          {
            title: '预分货客户',
            align: 'center',
            dataIndex: 'prepareCustomerId_dictText'
          },
          // {
          //   title: '物料种类',
          //   align: 'center',
          //   dataIndex: 'matKind_dictText'
          // },
          {
            title: '入库时间',
            align: 'center',
            dataIndex: 'stockTime',
          },
          {
            title: '钝化方式',
            align: 'center',
            dataIndex: 'surfaceTreatment'
          },
          {
            title: '锌层重量',
            align: 'center',
            dataIndex: 'platingWeight',
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
        tableColumns: [
          {
            title: '仓库',
            align: 'center',
            width: 125,
            dataIndex: 'stockHouseId_dictText'
          },
          {
            title: '品名中文',
            align: 'center',
            dataIndex: 'prodCname'
          },
          {
            title: '货物编码',
            align: 'center',
            width: 150,
            dataIndex: 'goodsNo'
          },
          {
            title: '牌号',
            align: 'center',
            dataIndex: 'sgSign'
          },
          {
            title: '产品名称',
            align: 'center',
            dataIndex: 'oldProdCname'
          },
          {
            title: '规格',
            align: 'center',
            dataIndex: 'custMatSpecs'
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
            title: '材料号',
            align: 'center',
            dataIndex: 'matNo'
          },
          {
            title: '可售数',
            align: 'center',
            dataIndex: 'availableQty'
          },
          {
            title: '可售量',
            align: 'center',
            dataIndex: 'availableWeight'
          },
          {
            title: '库存数',
            align: 'center',
            dataIndex: 'matNum'
          },
          {
            title: '库存量',
            align: 'center',
            dataIndex: 'weight'
          },
          {
            title: '计重',
            align: 'center',
            dataIndex: 'wtMode_dictText'
          },
          {
            title: '单重',
            align: 'center',
            dataIndex: 'pieceWeight'
          },
          {
            title: '产品大类',
            align: 'center',
            dataIndex: 'prodClassCode_dictText'
          },
          {
            title: '物料种类',
            align: 'center',
            dataIndex: 'matKind_dictText'
          },
          {
            title: '品名中文别名',
            align: 'center',
            dataIndex: 'prodCnameOther'
          },

        ],
        url: {
          list: '/sm/inventory/list',
          delete: '/sm/inventory/delete',
          deleteBatch: '/sm/inventory/deleteBatch',
          exportXlsUrl: '/sm/inventory/exportXls',
          importExcelUrl: 'sm/inventory/importExcel',
          unlocking: '/sm/inventoryLock/unlocking',
          locking: '/sm/inventoryLock/locking',
          allot: '/sm/inventory/allot',
          queryWarehouse: 'sm/stock/queryWarehouse',
          autoPrepareGoods: '/sm/inventory/autoPrepareGoods',
          updatePrepareCustomer: '/sm/inventory/updatePrepareCustomer'
        },
        selectParams: {
          availableQty: 0,
          availableWeight: 0
        },
        totalParams: {
          availableQty: 0,
          availableWeight: 0,
          matNum: 0,
          weight: 0
        },
        dictOptions: {},
      }
    },
    created() {
      this.disableMixinCreated = true
      this.columns = colAuthFilter(this.columns, 'sm:');
      this.loadData();
      this.initDictConfig();
    },
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      },
    },
    methods: {

      initDictConfig() {
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedMainId = selectedRowKeys[0]
        this.selectedRowKeys = selectedRowKeys
        this.selectionRows = selectionRows
      },
      prepareInventory() {
        postAction(this.url.autoPrepareGoods).then((res) => {
          if (res.success) {
            this.$message.success('自动分货成功！！！')
          }
          if (res.code !== 200) {
            this.$message.warning(res.message)
          }
        })
      },
      prepareCustomer() {
        if (this.selectionRows.length === 0) {
          return this.$message.warning('请勾选条目')
        }
        this.pcVisible = true
      },
      locking() {
        this.lockVisible = true
      },
      yrmInventory() {
        window.open(common.reportUrl +'/webroot/decision/view/report?viewlet=SMS_ZX%252FSM%252FYRM%252Finventory_man.cpt&ref_t=design&ref_c=3000de37-6032-4f37-81b5-4aef227dd0b6');
      },
      handleOKLock(){
        debugger
        let ids = []
        for (var a = 0; a < this.selectedRowKeys.length; a++) {
          ids += this.selectedRowKeys[a] + ','
        }
        var remark = this.remark
        getAction(this.url.locking, { ids, remark }).then((res) => {
          if (res.success) {
            this.$message.success('锁定成功！')
          }
          if (res.code !== 200) {
            this.$message.warning(res.message)
          }
        }).finally(() => {
          this.handleCancel()
          this.loadData()
        })
      },
      Discharger() {
        this.$refs['discharger'].hOpend()
      },
      discharger(ref) {
        this.dischargerName = ref.dischargerName
      },
      handleOK1() {
        debugger
        if (this.prepareCustomerId === null || this.prepareCustomerId === '') {
          return this.$message.warning('请选择客户！')
        }

        var inventoryIds = ''
        for (var a = 0; a < this.selectedRowKeys.length; a++) {
          inventoryIds += this.selectedRowKeys[a] + ','
        }
        var customerId = this.prepareCustomerId
        getAction(this.url.updatePrepareCustomer, { inventoryIds, customerId }).then((res) => {
          if (res.success) {
            this.$message.success('分货成功！')
          }
          if (res.code !== 200) {
            this.$message.warning(res.message)
          }
        }).finally(() => {
          this.handleCancel()
          this.loadData()
        })
      },

      handleOK() {

        if (this.selectValue === null || this.selectValue === '') {

          return this.$message.warning('请选择仓库！')
        }

        let select_stockHouseId = this.selectValue

        let list = []
        var MaxMatNum = ''

        for (let i = 0; i < this.selectionRows.length; i++) {
          list.push(this.selectionRows[i])
          MaxMatNum = this.selectionRows[i].matNum
          if (select_stockHouseId === this.selectionRows[i].stockHouseId) {
            return this.$message.warning('不能选择原仓库')
          }
        }
        if (this.allotNumber === null || this.allotNumber === '') {
          return this.$message.warning('请输入调拨数')
        } else if (this.allotNumber <= 0) {
          return this.$message.warning('请输入有效的调拨数')
        } else if (this.allotNumber > MaxMatNum) {
          return this.$message.warning('最大可调拨数为：' + MaxMatNum)
        }

        if (this.allotWeight === null || this.allotWeight === '') {
          return this.$message.warning('请输入合计重量')
        }
        postAction(this.url.allot, {
          list: list,
          warehouse: this.selectValue,
          allotNumber: this.allotNumber,
          remarks: this.remarks,
          stockLocation: this.stockLocation,
          allotWeight: this.allotWeight,
          dischargerName: this.dischargerName,
          warehouseTime: this.warehouseTime
        }).then((res) => {
          if (res.success) {

            this.$message.success('调拨成功')
          }
          if (res.code !== 200) {
            this.$message.warning(res.message)
          }
          this.loading = false
        }).finally(() => {
          this.handleCancel()
          this.loadData()
        })
      },
      handleCancel() {
        this.visible = false
        this.selectValue = ''
        this.pcVisible = false
        this.prepareCustomerId = ''
        this.lockVisible = false
        this.remark = ''
      },
      handleAllot() {
        if (this.selectionRows.length === 0) {
          return this.$message.warning('请勾选条目')
        } else if (this.selectionRows.length > 1) {
          return this.$message.warning('只能勾选一条记录')
        }
        //若数据没有计重方式，则给它赋上默认值0（实重）
        for (let i = 0; i < this.selectionRows.length; i++) {
          if (this.selectionRows[i].wtMode !== '0' && this.selectionRows[i].wtMode !== '1') {
            this.selectionRows[i].wtMode = '0'
            this.selectionRows[i].wtMode_dictText = filterDictTextByCache('wt_method_code', this.selectionRows[i].wtMode)
          }
        }

        this.visible = true
        getAction(this.url.queryWarehouse).then((res) => {
          if (res.success) {
            this.searchOptions = res.result
          }
          if (res.code === 510) {
            this.$message.warning(res.message)
          }
        })
      }
    },
    watch: {
      'dataSource': function () {
        let totalAvailableQty = 0  //可售数
        let totalAvailableWeight = 0 //可售量
        let totalMatNum = 0 //库存数
        let totalWeight = 0 //库存量
        let totalAvailableWeight1 = 0
        let totalWeight1 = 0

        for (let i = 0; i < this.dataSource.length; i++) {
          totalAvailableQty += this.dataSource[i].availableQty
          totalAvailableWeight += this.dataSource[i].availableWeight
          totalMatNum += this.dataSource[i].matNum
          totalWeight += this.dataSource[i].weight
        }
        //可售量、库存量保留三位小数
        totalAvailableWeight1 = totalAvailableWeight.toFixed(3)
        totalWeight1 = totalWeight.toFixed(3)
        this.totalParams.availableQty = totalAvailableQty
        this.totalParams.availableWeight = totalAvailableWeight1
        this.totalParams.matNum = totalMatNum
        this.totalParams.weight = totalWeight1
      },
      'selectionRows': function (selectionRows) {
        if (selectionRows) {
          let rows = selectionRows
          let availableQty = 0
          let availableWeight = 0
          for (let i = 0; i < rows.length; i++) {
            availableQty += rows[i].availableQty
            availableWeight += rows[i].availableWeight
          }
          this.selectParams.availableQty = availableQty.toFixed(3)
          this.selectParams.availableWeight = availableWeight.toFixed(3)
        }

      }
    }

  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>