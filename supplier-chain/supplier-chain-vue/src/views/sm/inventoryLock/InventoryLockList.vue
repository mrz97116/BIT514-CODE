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
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="锁定日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开始时间" class="query-group-cust"
                      v-model="queryParam.createTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择结束时间" class="query-group-cust"
                      v-model="queryParam.createTime_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="锁定单号">
              <a-input placeholder="请输入锁定单号" v-model="queryParam.lockNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料厚度">
              <a-input placeholder="请输入材料厚度" v-model="queryParam.matThick"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="钢号">
              <j-input placeholder="请输入钢号" v-model="queryParam.sgSign"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="品名中文">
              <j-input placeholder="请输入品名中文" v-model="queryParam.prodCname"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
          <a-form-item label="锁定状态">
            <j-dict-select-tag placeholder="请输入锁定状态" v-model="queryParam.lockStatus"
                               dictCode="inventory_lock"></j-dict-select-tag>
          </a-form-item>
        </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料宽度">
              <a-input placeholder="请输入材料宽度" v-model="queryParam.matWidth"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label=" 仓 库  ">
              <j-dict-select-tag placeholder="请选择仓库" v-model="queryParam.stockHouseId" dictCode="sm_stock,name,id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="备注">
              <j-input placeholder="请输入备注" v-model="queryParam.remark"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料号">
              <a-textarea placeholder="请输入材料号" v-model="queryParam.matNos"></a-textarea>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="材料长度">
              <a-input placeholder="请输入材料长度" v-model="queryParam.matLen"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="厂编">
              <j-input placeholder="请输入厂编" v-model="queryParam.stockLocation"></j-input>
            </a-form-item>
          </a-col>

          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <!--<a @click="handleToggleSearch" style="margin-left: 8px">-->
              <!--{{ toggleSearchStatus ? '收起' : '展开' }}-->
              <!--<a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>-->
              <!--</a>-->
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <!--<a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>-->
      <a-button @click="unlocking()" type="primary" icon="plus">解锁</a-button>
      <a-button @click="bulkEditing()" type="primary" icon="plus">批量修改备注</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('库存锁定')">导出</a-button>
      <!--<a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
      <!--<a-button type="primary" icon="import">导入</a-button>-->
      <!--</a-upload>-->
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
        <!--<span style="margin-left: 5%">勾选可售数：{{this.selectParams.availableQty}}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;勾选可售量：{{this.selectParams.availableWeight}}</span>-->
        <span style="margin-left: 35%">锁定数：{{this.totalParams.lockQty}}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;锁定量：{{this.totalParams.lockWeight}}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
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
          <!--<a @click="handleEdit(record)">编辑</a>-->
          <!--<a-divider type="vertical"/>-->
          <a @click="unlocking(record)">解锁</a>
          <a-divider type="vertical"/>
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <!--<a-menu-item>-->
              <!--<a-popconfirm title="确定删除吗?" @confirm="() => delete1(record.id)">-->
              <!--<a>删除</a>-->
              <!--</a-popconfirm>-->
              <!--</a-menu-item>-->
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>
    <div>
      <j-modal
        :visible="remakeVisible"
        title="添加备注"
        width="60%"
        @ok="remarkHandleOK"
        @cancel="handleCancel"
      >
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-textarea placeholder="请输入备注" v-model="remark"></a-textarea>
            </a-form-item>
          </a-col>
        </a-row>
      </j-modal>
    </div>
    <inventory-lock-modal ref="modalForm" @ok="modalFormOk"></inventory-lock-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { filterMultiDictText, filterDictTextByCache } from '@/components/dict/JDictSelectUtil'
  import { getAction, postAction } from '@api/manage'
  import JDate from '@/components/jeecg/JDate.vue'
  import JInput from '@/components/jeecg/JInput'
  import InventoryLockModal from './modules/InventoryLockModal'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'

  export default {
    name: 'InventoryLockList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      JDate,
      JInput,
      JDictSelectTag,
      InventoryLockModal,
    },
    data() {
      return {
        description: '库存锁定管理页面',
        remakeVisible: false,
        remark: '',
        queryParam: {
          lockStatus: 'lock'
        },
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },
        // 表头
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
            title: '锁定单号',
            align: 'center',
            dataIndex: 'lockNo'
          },
          {
            title: '锁定人',
            align: 'center',
            dataIndex: 'createBy'
          },
          {
            title: '锁定日期',
            align: 'center',
            dataIndex: 'createTime',
            customRender: function (text) {
              return !text ? '' : (text.length > 10 ? text.substr(0, 10) : text)
            }
          },
          {
            title: '仓库',
            align: 'center',
            dataIndex: 'stockHouseId_dictText'
          },
          {
            title: '品名中文',
            align: 'center',
            dataIndex: 'prodCname'
          },
          {
            title: '钢号',
            align: 'center',
            dataIndex: 'sgSign'
          },
          {
            title: '厂编',
            align: 'center',
            dataIndex: 'stockLocation'
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
            title: '产品大类',
            align: 'center',
            dataIndex: 'prodClassCode',
            customRender: (text) => {
              return filterDictTextByCache('prod_code', text)
            }
          },
          {
            title: '锁定数量',
            align: 'center',
            dataIndex: 'lockQty'
          },
          {
            title: '锁定重量',
            align: 'center',
            dataIndex: 'lockWeight'
          },
          {
            title: '锁定状态',
            align: 'center',
            dataIndex: 'lockStatus',
            customRender: (text) => {
              return filterDictTextByCache('inventory_lock', text)
            }
          },
          {
            title: '解锁人',
            align: 'center',
            dataIndex: 'updateBy'
          },
          {
            title: '备注',
            align: 'center',
            dataIndex: 'remark'
          },
          {
            title: '解锁数量',
            align: 'center',
            dataIndex: 'unlockQty'
          },
          {
            title: '解锁重量',
            align: 'center',
            dataIndex: 'unlockWeight'
          },
          {
            title: '解锁日期',
            align: 'center',
            dataIndex: 'unlockTime',
            customRender: function (text) {
              return !text ? '' : (text.length > 10 ? text.substr(0, 10) : text)
            }
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
        selectedMainId: '',
        url: {
          list: '/sm/inventoryLock/list',
          delete: '/sm/inventoryLock/delete',
          deleteBatch: '/sm/inventoryLock/deleteBatch',
          exportXlsUrl: '/sm/inventoryLock/exportXls',
          importExcelUrl: 'sm/inventoryLock/importExcel',
          removeAll: 'sm/warehouseWarrant/removeAll',
          unlocking: '/sm/inventoryLock/unlocking',
          bulkEditing: '/sm/inventoryLock/bulkEditing'

        },
        selectParams: {
          availableQty: 0,
          availableWeight: 0
        },
        totalParams: {
          lockQty: 0,
          lockWeight: 0,
          matNum: 0,
          weight: 0
        },
        dictOptions: {},
      }
    },
    created() {
    },
    watch: {
      'dataSource': function () {
        let totalLockQty = 0  //锁定数
        let totalLockWeight = 0 //锁定量
        for (let i = 0; i < this.dataSource.length; i++) {
          totalLockQty += this.dataSource[i].lockQty
          totalLockWeight += this.dataSource[i].lockWeight
        }
        //可售量、库存量保留三位小数
        this.totalParams.lockQty = totalLockQty.toFixed(3)
        this.totalParams.lockWeight = totalLockWeight.toFixed(3)
      },
      // 'selectionRows': function (selectionRows) {
      //   if (selectionRows) {
      //     let rows = selectionRows
      //     let availableQty = 0
      //     let availableWeight = 0
      //     for (let i = 0; i < rows.length; i++) {
      //       availableQty += rows[i].availableQty
      //       availableWeight += rows[i].availableWeight
      //     }
      //     this.selectParams.availableQty = availableQty.toFixed(3)
      //     this.selectParams.availableWeight = availableWeight.toFixed(3)
      //   }
      //
      // }
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
      unlocking() {
        if (this.selectionRows.length === 0) {
          return this.$message.warning('请勾选条目')
        }
        let ids = []
        for (var a = 0; a < this.selectedRowKeys.length; a++) {
          ids += this.selectedRowKeys[a] + ','
        }
        let that = this
        this.$confirm({
          title: '确认解锁',
          content: '是否解锁选中数据?',
          onOk: function () {
            debugger
            getAction(that.url.unlocking, { ids }).then((res) => {
              if (res.success) {
                that.$emit('ok')
                that.$message.success(res.message)
                that.visible = false
              } else {
                that.$message.warning(res.message)
                that.visible = true
              }
            }).finally(() => {
              that.loadData()
              that.selectedMainId = ''
            })
          }
        })
      },
      bulkEditing() {
        if (this.selectionRows.length === 0) {
          return this.$message.warning('请勾选条目')
        }
        this.remakeVisible = true
      },

      handleOk() {
        this.getAllTable().then(tables => {
          /** 一次性验证主表和所有的次表 */
          return validateFormAndTables(this.form, tables)
        }).then(allValues => {
          if (typeof this.classifyIntoFormData !== 'function') {
            throw this.throwNotFunction('classifyIntoFormData')
          }
          let formData = this.classifyIntoFormData(allValues)
          // 发起请求
          return this.unlocking(formData)
        }).catch(e => {
          console.error(e)
        })
      },
      remarkHandleOK() {
        debugger
        let ids = []
        for (var a = 0; a < this.selectedRowKeys.length; a++) {
          ids += this.selectedRowKeys[a] + ','
        }
        var remark = this.remark
        getAction(this.url.bulkEditing, { ids, remark }).then((res) => {
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
      close() {
        this.$emit('close')
        this.visible = false
        this.remarkVisible = false
      },
      /** 整理成formData */
      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
        }
      },
      submitCallback() {
        this.$emit('ok')
        this.visible = false
        this.loadData()

      },
      handleCancel() {
        this.remakeVisible = false
        this.remake = ''
        this.close()
        this.loadData()
      }
    },

  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>