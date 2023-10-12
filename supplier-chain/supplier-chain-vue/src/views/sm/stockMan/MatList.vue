<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="产品大类">
              <j-dict-select-tag placeholder="请选择产品大类" v-model="queryParam.prodClassCode" dictCode="prod_code"/>
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
            <a-form-item label="牌号">
              <j-input placeholder="请输入牌号" v-model="queryParam.sgSign"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="品名中文">
              <j-input placeholder="请输入品名中文" v-model="queryParam.prodCname"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="库存类型">
              <j-dict-select-tag placeholder="请选择库存类型" v-model="queryParam.stockType" dictCode="stock_type"/>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:arrivalStatus'" :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="到货状态">
              <j-dict-select-tag placeholder="请选择到货状态" v-model="queryParam.arrivalStatus" dictCode="arrival_status"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料宽度">
              <a-input placeholder="请输入材料宽度" v-model="queryParam.matWidth"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="车号">
              <j-input placeholder="请输入车号" v-model="queryParam.carNo"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="品名中文别名">
              <j-input placeholder="请输入品名中文别名" v-model="queryParam.prodCnameOther"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="钝化方式">
              <j-input placeholder="请输入钝化方式" v-model="queryParam.surfaceTreatment"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料长度">
              <a-input placeholder="请输入材料长度" v-model="queryParam.matLen"></a-input>
            </a-form-item>
          </a-col>
          <a-col v-has="'sm:actualDeliveryLocation'" :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="交货点">
              <j-input placeholder="请输入交货点" v-model="queryParam.actualDeliveryLocation"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="备注">
              <j-input placeholder="请输入备注" v-model="queryParam.remarks"></j-input>
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
      <a-button v-has="'sm:add'" @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button icon="plus" @click="handleQuery" type="primary">入库</a-button>
      <!--<a-button v-has="'sm:readyInventory'"@click="readyInventory" type="primary" icon="plus">提交入库</a-button>-->
      <a-button type="primary" icon="download" @click="handleExportXls('物料信息表')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tenantTokenHeader"
                :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
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
        <span style="margin-left: 5%">勾选材料重量：{{ this.selectParams.matNetWt }}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;勾选材料件数：{{
            this.selectParams.matNum
          }}</span>
        <span
          style="margin-left: 30%">材料重量:{{ this.totalParams.matNetWt }}&nbsp;&nbsp;&nbsp;材料件数：{{
            this.totalParams.matNum
          }}</span>
      </div>

      <a-table
        ref="table"
        size="middle"
        :scroll="{x:true}"
        bordered
        rowKey="id"
        :columns="columns"
        :customRow="customRow"
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
          <a @click="handleDetail(record)">详情</a>
          <a-divider type="vertical"/>
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a v-has="'sm:add'">删除</a>
          </a-popconfirm>
        </span>

      </a-table>
    </div>

    <div>
      <j-modal
        :visible="visible"
        title="入库"
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
          <!--<a-col :xl="8" :lg="9" :md="20" :sm="26">
            <a-form-item label="库位">
              <a-input placeholder="请输入库位" v-model="stockLocation"></a-input>
            </a-form-item>
          </a-col>-->
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
          :pagination="tableIpagination"
          :loading="loading"
          class="j-matTable-force-nowrap">
          <template v-for="(col) in ['purchasePrice','costPrice','wtMode','stockLocation']" :slot="col"
                    slot-scope="text, record, index">
            <template v-if="!record.editable"><span>{{text}}</span></template>
            <a-input
              :key="col"
              v-else-if="record.editable && col==='purchasePrice'"
              style="margin: -5px 0"
              :value="text"
              :placeholder="tableColumns.title='请输入采购价'"
              @change="e => handleChange(e.target.value, record.id, col)"
            />
            <a-input
              :key="col"
              v-else-if="record.editable && col==='costPrice'"
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
            <a-input
              :key="col"
              v-else-if="record.editable && col==='stockLocation'"
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
    <div>
      <j-modal
        :visible="visible1"
        title="提交入库"
        width="60%"
        @ok="readyInventoryOK"
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
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入备注" v-model="remarks"></a-input>
            </a-form-item>
          </a-col>
          <!--<a-col :span="12">-->
            <!--<a-form-item label="入库时间" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
              <!--<j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择入库时间" class="query-group-cust"-->
                      <!--v-model="warehouseTime"></j-date>-->
            <!--</a-form-item>-->
          <!--</a-col>-->
          <!--<a-col :span="12">-->
            <!--<a-form-item label="卸货人" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
              <!--<a-col :span="12">-->
                <!--<a-input v-model="dischargerName"-->
                         <!--placeholder="选择卸货人后自动填写"/>-->
              <!--</a-col>-->
              <!--<a-button @click="Discharger()" type="primary">添加卸货人信息</a-button>-->
            <!--</a-form-item>-->
          <!--</a-col>-->
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
          :pagination="tableIpagination"
          :loading="loading"
          class="j-matTable-force-nowrap">
          <template v-for="(col) in ['costPrice','wtMode','stockLocation']" :slot="col"
                    slot-scope="text, record, index">
            <a-input
              :key="col"
              v-if="record.editable && col==='costPrice'"
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
    <!--<mat-modal ref="modalForm" @ok="modalFormOk"></mat-modal>-->
    <stock-modal ref="modalForm" @ok="modalFormOk"></stock-modal>
    <mat-detail-form ref="detailForm"></mat-detail-form>

  </a-card>
</template>

<script>

import '@/assets/less/TableExpand.less'
import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
import {mixinDevice} from '@/utils/mixin'
import {JeecgListMixin} from '@/mixins/JeecgListMixin'
import MatModal from '../matMan/modules/MatModal'
import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
import {filterMultiDictText, filterDictTextByCache} from '@/components/dict/JDictSelectUtil'
import {getAction, postAction} from '../../../api/manage'
import JDate from '@/components/jeecg/JDate.vue'
import StockModal from './modules/stockModal'
import MatDetailForm from '@views/sm/stockMan/modules/MatDetailForm'
import JInput from "../../../components/jeecg/JInput";
import Template1 from "../../jeecg/JVxeDemo/layout-demo/Template1";
import DischargerInfoModal from "./DischargerInfoList";
import { colAuthFilter } from '@/utils/authFilter'

export default {
  name: 'MatList',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
    DischargerInfoModal,
    JInput,
    Template1,
    JDictSelectTag,
    MatModal,
    JSearchSelectTag,
    JDate,
    StockModal,
    MatDetailForm
  },
  data() {
    return {
      labelCol: {
        xs: {span: 24},
        sm: {span: 6}
      },
      wrapperCol: {
        xs: {span: 24},
        sm: {span: 16}
      },
      previousData: [],
      description: '物料信息表管理页面',
      // 表头
      selectValue: '',
      stockLocation: '',
      remarks: '',
      dischargerName: '',
      warehouseTime: '',
      searchOptions: [],
      disableSubmit: false,
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
      columns: [
        {
          title: '序号',
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
          title: '库存类型',
          align: 'center',
          dataIndex: 'stockType',
          customRender: (text) => {
            //字典值翻译通用方法
            return filterDictTextByCache('stock_type', text)
          }
        },
        {
          title: '材料号',
          align: 'center',
          dataIndex: 'matNo'
        },
        {
          title: '材料实际重量',
          align: 'center',
          dataIndex: 'matActWt'
        },
        {
          title: '材料净重',
          align: 'center',
          dataIndex: 'matNetWt'
        },
        {
          title: '材料件数',
          align: 'center',
          dataIndex: 'matNum'
        },
        {
          title: '材料种类',
          align: 'center',
          dataIndex: 'matKind',
          customRender: (text) => {
            //字典值翻译通用方法
            return filterDictTextByCache('mat_kind', text)
          }
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
          title: '车号',
          align: 'center',
          dataIndex: 'carNo'
        },
        {
          title: '交货点',
          align: "center",
          dataIndex: 'actualDeliveryLocation'
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
          title: '采购价',
          align: 'center',
          dataIndex: 'puechasePrice'
        },
        {
          title: '成本价',
          align: 'center',
          dataIndex: 'costPrice'
        },
        {
          title: '技术标准',
          align: 'center',
          dataIndex: 'technicalStandard'
        },
        {
          title: '柳钢装车时间',
          align: 'center',
          dataIndex: 'carLoadingTime'
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
          title: '备注',
          align: 'center',
          dataIndex: 'remarks'
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          fixed: 'right',
          width: 147,
          scopedSlots: {customRender: 'action'}
        }
      ],
      tableColumns: [
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
          title: '钝化方式',
          align: 'center',
          dataIndex: 'surfaceTreatment',

        },
        {
          title: '锌层重量',
          align: 'center',
          dataIndex: 'platingWeight',

        },
        {
          title: '采购价',
          align: 'center',
          dataIndex: 'purchasePrice',
          width: 125,
          editable: false,
          scopedSlots: {customRender: 'purchasePrice'}
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
          title: '库位',
          align: 'center',
          dataIndex: 'stockLocation',
          width: 125,
          editable: false,
          scopedSlots: {customRender: 'stockLocation'}
        },
        {
          title: '操作',
          width: 100,
          align: 'center',
          scopedSlots: {customRender: 'operation'}
        },
      ],
      visible: false,
      visible1: false,
      url: {
        list: '/sm/mat/noWarehouseList',
        delete: '/sm/mat/delete',
        deleteBatch: '/sm/mat/deleteBatch',
        exportXlsUrl: '/sm/mat/exportXls',
        importExcelUrl: 'sm/mat/importExcel',
        queryStockList: 'sm/stock/list',
        queryWarehouse: 'sm/stock/queryWarehouse',
        updateWarehouse: 'sm/mat/updateWarehouse',
        readyWarehouse: 'sm/mat/readyWarehouse'

      },
      dictOptions: {},
      totalParams: {
        matNetWt: 0,
        matNum: 0
      }, selectParams: {
        matNetWt: 0,
        matNum: 0
      }
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
  watch: {
    'dataSource': function () {
      let matNetWt = 0;
      let matNum = 0;

      for (let i = 0; i < this.dataSource.length; i++) {
        matNetWt += this.dataSource[i].matNetWt
        matNum += this.dataSource[i].matNum
      }
      //可售量、库存量保留三位小数
      matNetWt = matNetWt.toFixed(3);
      this.totalParams.matNetWt = matNetWt;
      this.totalParams.matNum = matNum;
    }, 'selectionRows': function (selectionRows) {
      if (selectionRows) {
        debugger
        let rows = selectionRows;
        let matNetWt = 0;
        let matNum = 0;
        for (let i = 0; i < rows.length; i++) {
          matNetWt += rows[i].matNetWt;
          matNum += rows[i].matNum;
        }
        this.selectParams.matNetWt = matNetWt.toFixed(3);
        this.selectParams.matNum = matNum;
      }

    }
  },
  methods: {
    initDictConfig() {
    },
    Discharger() {
      this.$refs['discharger'].hOpend()
    },
    discharger(ref) {
      this.dischargerName = ref.dischargerName
    },
    handleOK() {
      if (this.selectValue === null || this.selectValue === '') {
        return this.$message.warning('请选择仓库！')
      }
      let list = []
      for (let i = 0; i < this.selectionRows.length; i++) {
        list.push(this.selectionRows[i])
      }
      postAction(this.url.updateWarehouse, {
        list: list,
        warehouse: this.selectValue,
        remarks: this.remarks,
        dischargerName: this.dischargerName,
        warehouseTime: this.warehouseTime
      }).then((res) => {
        if (res.success) {
          this.$message.success('入库成功')
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
    readyInventoryOK() {
      if (this.selectValue === null || this.selectValue === '') {
        return this.$message.warning('请选择仓库！')
      }
      let list = []
      for (let i = 0; i < this.selectionRows.length; i++) {
        list.push(this.selectionRows[i])
      }
      postAction(this.url.readyWarehouse, {
        list: list,
        warehouse: this.selectValue,
        remarks: this.remarks,
        dischargerName: this.dischargerName,
        warehouseTime: this.warehouseTime
      }).then((res) => {
        if (res.success) {
          this.$message.success('添加成功，请前往入库确认界面入库')
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
      this.visible = false;
      this.visible1 = false;
      this.selectValue = "";
    },
    handleQuery() {
      console.log('1', this.selectionRows.length)
      if (this.selectionRows.length === 0) {
        this.$message.warning('请勾选条目！')
      } else {
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
            console.log('res', res)
            this.searchOptions = res.result
          }
          if (res.code === 510) {
            this.$message.warning(res.message)
          }
          // this.loading = false;
        })
      }
    },
    readyInventory() {
      console.log('1', this.selectionRows.length)
      if (this.selectionRows.length === 0) {
        this.$message.warning('请勾选条目！')
      } else {
        //若数据没有计重方式，则给它赋上默认值0（实重）
        for (let i = 0; i < this.selectionRows.length; i++) {
          if (this.selectionRows[i].wtMode !== '0' && this.selectionRows[i].wtMode !== '1') {
            this.selectionRows[i].wtMode = '0'
            this.selectionRows[i].wtMode_dictText = filterDictTextByCache('wt_method_code', this.selectionRows[i].wtMode)
          }
        }
        this.visible1 = true
        getAction(this.url.queryWarehouse).then((res) => {
          if (res.success) {
            console.log('res', res)
            this.searchOptions = res.result
          }
          if (res.code === 510) {
            this.$message.warning(res.message)
          }
          // this.loading = false;
        })
      }
    },
    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '入库新增'
      this.$refs.modalForm.disableSubmit = false
    },
    handleDetail: function (record) {
      this.$refs.detailForm.edit(record)
    },
    /*

    添加部分
    *
    * */
    toggle(key) {
      let that = this
      let target = this.selectionRows.filter(item => {
        if (item.id === key) {
          that.previousData.push({id: key, value: item.costPrice})
          that.previousData.push({id: key, value: item.purchasePrice})
          that.previousData.push({id: key, value: item.stockLocation})
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
      let costPrice = 0;
      let purchasePrice = 0;
      let stockLocation = null;
      for (let i = 0; i < that.previousData.length; i++) {
        if (that.previousData[i].id === key) costPrice = that.previousData[i].value
        if (that.previousData[i].id === key) purchasePrice = that.previousData[i].value
        if (that.previousData[i].id === key) stockLocation = that.previousData[i].value
      }
      target.editable = false
      target.costPrice = costPrice
      target.purchasePrice = purchasePrice
      target.stockLocation = stockLocation
      this.$nextTick(() => {
        this.reloadData();
      })
    },
    reloadData() {
      const newData = [...this.selectionRows];
      this.selectionRows = newData;
    },
    updateArrivalStatus(param) {
      var that = this;
      if (that.selectedRowKeys.length == 0) {
        that.$message.warning("请勾选一条记录！")
      } else {
        var ids = "";
        for (var a = 0; a < that.selectedRowKeys.length; a++) {
          ids += that.selectedRowKeys[a] + ",";
        }
        var title = ''
        var content = ''
        if (param == 'arrived') {
          title = "确认审核"
          content = "选中数据是否到货?"
        }
        if (param == 'pending') {
          title = "确认审核"
          content = "选中数据是否取消到货?"
        }
        that.$confirm({
          title: title,
          content: content,
          onOk: function () {
            that.loading = true;
            getAction("/sm/mat/updateArrivalStatus", {ids: ids, status: param}).then((res) => {
              if (res.success) {
                that.$message.success("操作成功！")
              }
              if (res.code === 500) {
                that.$message.warning(res.message)
              }
              that.loading = false;
              that.loadData()
            }).finally(() => {
              that.loading = false;
            });
          }
        });
      }
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>