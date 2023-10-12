<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="产品大类">
              <j-dict-select-tag placeholder="请选择产品大类" v-model="queryParam.prodClassCode" dictCode="prod_code" @input="e =>getThicks(e)"/>
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
            <a-form-item label="卸货类型">
              <j-input placeholder="请输入卸货类型" v-model="queryParam.unloadType"></j-input>
            </a-form-item>
          </a-col>

          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="厚">
              <j-select-multiple placeholder="请输入厚度" :options="thicksOption" v-model="queryParam.matThicks"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="牌号">
              <j-input placeholder="请输入牌号" v-model="queryParam.steelNo"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="卸车时间">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开始时间" class="query-group-cust"
                      v-model="queryParam.unloadTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择结束时间" class="query-group-cust"
                      v-model="queryParam.unloadTime_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="宽">
              <a-input placeholder="请输入宽度" v-model="queryParam.matWidth"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="车牌号">
              <j-input placeholder="请输入车牌号" v-model="queryParam.carNo"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="产品名称">
              <j-input placeholder="请输入产品名称" v-model="queryParam.productName"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="船号">
              <j-input placeholder="请输入船号" v-model="queryParam.shipNo"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="长">
              <a-input placeholder="请输入长度" v-model="queryParam.matLen"></a-input>
            </a-form-item>
          </a-col>

          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="交货点">
              <j-input placeholder="请输入交货点" v-model="queryParam.actualDeliveryLocation"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="备注">
              <j-input placeholder="请输入备注" v-model="queryParam.remark"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料号">
              <a-textarea placeholder="请输入材料号" v-model="queryParam.matNos"></a-textarea>
            </a-form-item>
          </a-col>

          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="分单号">
              <j-input placeholder="请输入分单号" v-model="queryParam.dispatchShipBillNo"></j-input>
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
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tenantTokenHeader"
                :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <!--      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>-->
      <a-button type="primary" icon="download" @click="handleExportXls('船运管理')">导出物料信息</a-button>
<!--      <a-button @click="batchAddRemark" type="primary">批量添加备注</a-button>-->
      <a-input style="width: 5%;background: #07c160" default-value="件数:"/>
      <a-input-number v-model="rows"></a-input-number>
      <a-input style="width: 5%;background: #07c160" default-value="价格:"/>
      <a-input-number v-model="price"></a-input-number>
      <a-button type="primary" @click="batchRows">添加分货材料</a-button>
      <a-button type="primary" @click="batchSelectionRow">添加勾选项</a-button>
      <a-button type="primary" @click="preOrder">预开单{{this.preSelectionRows.length}}</a-button>
      <a-button type="primary" @click="cleanPre">清空预开</a-button>
      <a-button type="primary" @click="inventoryEntrust">入库委托</a-button>
      <a-button type="primary" @click="batchAddRemark">批量备注</a-button>
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
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a
        style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
        <span style="margin-left: 35%">重量：{{this.totalParams.lockWeight}}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
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
<!--          <a @click="handleEdit(record)">编辑</a>-->

          <!--          <a-divider type="vertical" />-->
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

    <div>
      <j-modal
        :visible="remarkVisible"
        title="批量添加备注"
        width="60%"
        @ok="handleRemarkOK"
        @cancel="handleCancel"
      >
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-textarea v-model="remark" placeholder="请输入备注"/>
            </a-form-item>
          </a-col>
        </a-row>
      </j-modal>
    </div>

    <div>
      <j-modal
        :visible="visible"
        title="批量添加备注"
        width="60%"
        @ok="handleOK"
        @cancel="handleCancel"
      >
        <!-- 字典搜索  -->
        <a-row :gutter="24">
          <!--<a-col :xl="8" :lg="9" :md="20" :sm="26">
            <a-form-item label="库位">
              <a-input placeholder="请输入库位" v-model="stockLocation"></a-input>
            </a-form-item>
          </a-col>-->
          <a-col :span="12">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入备注" v-model="batchAddRemarks"></a-input>
            </a-form-item>
          </a-col>
        </a-row>
        <a-table
          rowKey="id"
          size="middle"
          :scroll="{x:true}"
          bordered
          :columns="batchAddRemarksColumns"
          :dataSource="selectionRows"
          :pagination="ipagination"
          :loading="loading"
          class="j-matTable-force-nowrap">
        </a-table>
      </j-modal>
    </div>
    <div>
      <j-modal
        :visible="preVisible"
        title="预开单"
        width="60%"
        @ok="handlePreOK"
        @cancel="handleCancel"
      >
        <!-- 字典搜索  -->
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="客户名称" >
              <j-search-select-tag  v-model="customerId" v-decorator="['customerId', validatorRules.customerId]"
                                   dict="cm_customer_profile where del_flag=0,company_name,id"/>
            </a-form-item>
          </a-col>

          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="仓库">
              <j-search-select-tag  v-model="destination" v-decorator="['customerId', validatorRules.destination]"
                                    dict="sm_stock where del_flag=0,name,name"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="预开单号">
              <a-input v-model="prepareOrderNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="备注" >
              <a-textarea v-model="remark"></a-textarea>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="厚">
              <a-input v-model="matThick"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="单价">
              <a-input v-model="batchPrice"></a-input>
            </a-form-item>
          </a-col>
        </a-row>
        <div class="table-operator">
          <a-button type="primary" @click="updatePrice">修改单价</a-button>
        </div>
        <a-table
          rowKey="id"
          size="middle"
          :scroll="{x:true}"
          bordered
          :columns="batchAddRemarksColumns"
          :dataSource="preSelectionRows"
          :pagination="ipagination1"
          :loading="loading"
          class="j-matTable-force-nowrap">
          <template v-for="(col) in ['price']" :slot="col"
                    slot-scope="text, record, index">
            <a-input
              :key="col"
              v-if="record.editable && col==='price'"
              style="margin: -5px 0"
              :value="text"
              :placeholder="batchAddRemarksColumns.title='请输入成本价'"
              @change="e => handleChange(e.target.value, record.id, col)"
            />
            <template v-else><span>{{ text }}</span></template>
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
    <shipping-management-modal ref="modalForm" @ok="modalFormOk"></shipping-management-modal>
    <inventory-entrust-modal ref="inventoryEntrustModal" @loadAgan="loadData()"></inventory-entrust-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import {mixinDevice} from '@/utils/mixin'
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import ShippingManagementModal from './modules/ShippingManagementModal'
  import JInput from '@/components/jeecg/JInput'
  import JSearchSelectTag from '@comp/dict/JSearchSelectTag'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import JSelectMultiple from '@/components/jeecg/JSelectMultiple'
  import {filterMultiDictText, filterDictTextByCache} from '@/components/dict/JDictSelectUtil'
  import JDate from '@/components/jeecg/JDate.vue'
  import {getAction, postAction} from "../../../api/manage";
  import AFormItem from "ant-design-vue/es/form/FormItem";
  import ACol from "ant-design-vue/es/grid/Col";
  import InventoryEntrustModal from "./modules/InventoryEntrustModal";
  import ATextarea from "ant-design-vue/es/input/TextArea";

  export default {
    name: 'ShippingManagementList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      ATextarea,
      ACol,
      AFormItem,
      ShippingManagementModal,
      JDictSelectTag,
      JSearchSelectTag,
      JInput,
      JDate,
      InventoryEntrustModal,
      JSelectMultiple
    },
    data() {
      return {
        description: '船运管理管理页面',
        validatorRules: {
          customerId: {
            rules: [
              { required: true, message: '请输入顾客!' },
            ]
          },
          destination: {
            rules: [
              { required: true, message: '请输入仓库!' },
            ]
          }
        },
        // 表头
        ipagination:{
          current: 1,
          pageSize: 100,
          pageSizeOptions: ['10', '100','300','500'],
          showTotal: (total, range) => {
            return range[0] + "-" + range[1] + " 共" + total + "条"
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        ipagination1:{
          current: 1,
          pageSize: 100,
          pageSizeOptions: ['10', '100','300','500'],
          showTotal: (total, range) => {
            return range[0] + "-" + range[1] + " 共" + total + "条"
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        totalParams: {
          lockQty: 0,
          lockWeight: 0,
          matNum: 0,
          weight: 0
        },
        columns: [
          {
            title: '序号',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: "center",
            customRender: function (t, r, index) {
              return parseInt(index) + 1;
            }
          },
          {
            title: '创建时间',
            align: 'center',
            dataIndex: 'createTime'
          },
          {
            title: '产品大类',
            align: 'center',
            dataIndex: 'prodClassCode_dictText'
          },
          {
            title: '产品名',
            align: "center",
            dataIndex: 'productName'
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
            title: '牌号',
            align: "center",
            dataIndex: 'steelNo'
          },
          {
            title: '锌层重量',
            align: "center",
            dataIndex: 'platingWeight'
          },
          {
            title: '钝化方式',
            align: "center",
            dataIndex: 'surfaceTreatment'
          },
          {
            title: '分单号',
            align: "center",
            dataIndex: 'dispatchShipBillNo'
          },
          {
            title: '船号',
            align: "center",
            dataIndex: 'shipNo'
          },
          {
            title: '材料号',
            align: "center",
            dataIndex: 'matNo'
          },
          {
            title: '车牌号',
            align: "center",
            dataIndex: 'carNo'
          },
          {
            title: '重量',
            align: "center",
            dataIndex: 'weight'
          },
          {
            title: '交货点',
            align: "center",
            dataIndex: 'actualDeliveryLocation'
          },
          {
            title: '位置',
            align: "center",
            dataIndex: 'location'
          },
          {
            title: '备注',
            align: "center",
            dataIndex: 'remark'
          },
          {
            title: '卸车时间',
            align: "center",
            dataIndex: 'unloadTime'
          },
          {
            title: '卸货类型',
            align: "center",
            dataIndex: 'unloadType'
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
        thicksOption: [],
        batchAddRemarksColumns: [
          {
            title: '序号',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: "center",
            customRender: function (t, r, index) {
              return parseInt(index) + 1;
            }
          },
          {
            title: '开单人',
            align: "center",
            dataIndex: 'createBy'
          },
          {
            title: '创建日期',
            align: "center",
            dataIndex: 'createTime'
          },

          {
            title: '产品大类',
            align: 'center',
            dataIndex: 'prodClassCode_dictText'
          },
          {
            title: '产品名',
            align: "center",
            dataIndex: 'productName'
          },
          {
            title: '牌号',
            align: "center",
            dataIndex: 'steelNo'
          },
          {
            title: '厚',
            align: "center",
            dataIndex: 'matThick'
          },
          {
            title: '宽',
            align: "center",
            dataIndex: 'matWidth'
          },
          {
            title: '长',
            align: "center",
            dataIndex: 'matLen'
          },
          {
            title: '船号',
            align: "center",
            dataIndex: 'shipNo'
          },
          {
            title: '材料号',
            align: "center",
            dataIndex: 'matNo'
          },
          {
            title: '车牌号',
            align: "center",
            dataIndex: 'carNo'
          },
          {
            title: '数量',
            align: "center",
            dataIndex: 'quantity'
          },
          {
            title: '重量',
            align: "center",
            dataIndex: 'weight'
          },
          {
            title: '单价',
            align: "center",
            dataIndex: 'price',
            editable: false,
            scopedSlots: {customRender: 'price'}
          },
          {
            title: '备注',
            align: "center",
            dataIndex: 'remark'
          },
          {
            title: '操作',
            width: 100,
            align: 'center',
            scopedSlots: {customRender: 'operation'}
          },
        ],
        url: {
          list: "/sm/shippingManagement/list",
          delete: "/sm/shippingManagement/delete",
          deleteBatch: "/sm/shippingManagement/deleteBatch",
          exportXlsUrl: "/sm/shippingManagement/exportXls",
          importExcelUrl: "sm/shippingManagement/importExcel",
          batchAddRemarks: "/sm/shippingManagement/batchAddRemarks",
          prepareOrder: "/sm/prepareOrder/prepareOrder",
          getThicks: "/sm/shippingManagement/getThicks",
          updatePrepareCustomer: "/sm/shippingManagement/updatePrepareCustomer",
        },
        dictOptions: {},
        labelCol: {
          xs: {span: 24},
          sm: {span: 6}
        },
        wrapperCol: {
          xs: {span: 24},
          sm: {span: 16}
        },
        rows: 0,
        visible: false,
        preVisible: false,
        remarkVisible: false,
        keySelection: [],
        preSelectionRows: [],
        price: 0,
        previousData: [],
        customerId: '',
        remark: '',
        destination: '',
        matThick: '',
        batchPrice: 0,
        batchAddRemarks: '',
        prepareOrderNo: ''
      }
    },
    watch: {
      'dataSource': function () {
        let totalLockQty = 0  //锁定数
        let totalLockWeight = 0 //锁定量
        let thicksOptions = [];
        this.thicksOption = [];
        for (let i = 0; i < this.dataSource.length; i++) {
          totalLockQty += this.dataSource[i].quantity
          totalLockWeight += this.dataSource[i].weight
          if(thicksOptions.indexOf(this.dataSource[i].matThick)== -1) {
            thicksOptions.push(this.dataSource[i].matThick);
          }
        }
        thicksOptions.sort((a, b) => a - b);
        thicksOptions.forEach((item, index) =>{
          this.thicksOption.push({text:item, value:item});
        })
        //可售量、库存量保留三位小数
        this.totalParams.lockQty = totalLockQty.toFixed(3)
        this.totalParams.lockWeight = totalLockWeight.toFixed(3)
      },
    },
    created() {
      this.getThicks('');
    },
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig() {
      },
      batchAddRemark() {
        console.log('1', this.selectionRows.length)
        if (this.selectionRows.length === 0) {
          this.$message.warning('请勾选条目！')
        } else {
          this.remarkVisible = true
        }
      },
      handleOK() {
        if (this.batchAddRemarks === null || this.batchAddRemarks === '') {
          return this.$message.warning('请填入备注！')
        }
        let list = []
        for (let i = 0; i < this.selectionRows.length; i++) {
          //push() 方法可向数组的末尾添加一个或多个元素，并返回新的长度。
          list.push(this.selectionRows[i])
        }
        postAction(this.url.batchAddRemarks, {
          list: this.preSelectionRows,
          customerId: this.customerId,
          remark: this.remark,
        }).then((res) => {
          if (res.success) {
            this.$message.success('批量添加成功')
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
      saveRow(key) {
        let target = this.selectionRows.filter(item => item.id === key)[0]
        target.editable = false
        this.$nextTick(() => {
          this.reloadData();
        })
      },
      cancel(key) {
        let that = this
        let target = this.selectionRows.filter(item => item.id === key)[0]
        let price = 0;
        for (let i = 0; i < that.previousData.length; i++) {
          if (that.previousData[i].id === key) price = that.previousData[i].value
        }
        target.editable = false
        target.price = price
        this.$nextTick(() => {
          this.reloadData();
        })
      },
      toggle(key) {
        let that = this
        let target = this.selectionRows.filter(item => {
          if (item.id === key) {
            that.previousData.push({id: key, value: item.price})
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
          if (column === 'wtMode') {
            target['wtMode_dictText'] = filterDictTextByCache('wt_method_code', value)
            target[column] = value
          } else {
            target[column] = value
          }
          this.selectionRows = newData
        }
      },
      reloadData() {
        const newData = [...this.selectionRows];
        this.selectionRows = newData;
      },
      handleCancel() {
        this.visible = false;
        this.preVisible = false;
        this.remarkVisible = false;
        this.batchAddRemarks = "";
        this.remark = '';
        this.customerId = '';
        this.destination = '';
      },
      batchRows() {
        let keys = [];
        let selectRows = []
        if (this.rows > this.dataSource.length) {
          this.$message.warning('添加材料件数大于已有件数！！！')
          return;
        }
        for (let i = 0; i < this.rows; i++) {
          if (!this.keySelection.includes(this.dataSource[i].id)) {
            keys.push(this.dataSource[i].id);
            selectRows.push(this.dataSource[i])
            this.keySelection.push(this.dataSource[i].id)
            this.dataSource[i]['price'] = this.price;
            this.preSelectionRows.push(this.dataSource[i])
          }
        }
        this.$nextTick(() => {
          this.onSelectChange(keys, selectRows);
        })
      },
      preOrder() {
        this.preVisible = true;
      },
      cleanPre() {
        this.keySelection = [];
        this.preSelectionRows = [];
        this.selectionRows = [];
        this.selectedRowKeys = [];
        this.customerId = '';
      },
      handlePreOK() {
        if (this.customerId === null || this.customerId === '' ){
          this.$message.warning("请选择客户")
        } else  if (this.destination === null || this.destination === '' ){
          this.$message.warning("请选择仓库")
        } else {
          postAction(this.url.prepareOrder, {
            list: this.preSelectionRows,
            customerId: this.customerId,
            remark: this.remark,
            destination: this.destination,
            prepareOrderNo: this.prepareOrderNo
          }).then((res) => {
            if (res.success) {
              this.$message.success('新增预开单成功')
              this.handleCancel()
              this.cleanPre()
              this.loadData()
            }
            if (res.code !== 200) {
              this.$message.warning(res.message)
            }
            this.loading = false
          })
        }
      },
      handleRemarkOK(){
        if (this.selectionRows == null || this.selectedRowKeys.length === 0) {
          return this.$message.warning('请勾选记录')
        }
        var ids = ''
        for (var a = 0; a < this.selectedRowKeys.length; a++) {
          ids += this.selectedRowKeys[a] + ','
        }
        var remark = this.remark
        getAction(this.url.updatePrepareCustomer, { ids, remark }).then((res) => {
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
      inventoryEntrust(){
        if (this.selectionRows == null || this.selectedRowKeys.length === 0) {
          return this.$message.warning('请勾选记录')
        }
        this.$refs.inventoryEntrustModal.add()
        this.$refs.inventoryEntrustModal.selectedRows = this.selectionRows
        this.$refs.inventoryEntrustModal.title = "入库委托"
        this.$refs.modalForm.disableSubmit = false
      },
      getThicks(val) {
        getAction(this.url.getThicks, {prodClassCode: val? val: ''}).then((res) => {
            if(res.success) {
              this.thicksOption = res.result;
            }
        })
      },
      batchSelectionRow(){
        let keys = [];
        let selectRows = []
        if (this.rows > this.dataSource.length) {
          this.$message.warning('添加材料件数大于已有件数！！！')
          return;
        }
        for (let i = 0; i < this.selectionRows.length; i++) {
          if (!this.keySelection.includes(this.selectionRows[i].id)) {
            keys.push(this.selectionRows[i].id);
            selectRows.push(this.selectionRows[i])
            this.keySelection.push(this.selectionRows[i].id)
            this.selectionRows[i]['price'] = this.price;
            this.preSelectionRows.push(this.selectionRows[i])
          }
        }
        this.$nextTick(() => {
          this.onSelectChange(keys, selectRows);
        })
      },
      updatePrice(){
        for(let i = 0; i < this.preSelectionRows.length; i++) {
          if(this.matThick == this.preSelectionRows[i].matThick) {
            this.preSelectionRows[i].price = this.batchPrice;
          }
        }
      }

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>