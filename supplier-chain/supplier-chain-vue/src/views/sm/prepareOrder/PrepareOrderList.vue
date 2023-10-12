<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="客户">
              <j-search-select-tag placeholder="请选择客户" v-model="queryParam.customerId"
                                   dict="cm_customer_profile where del_flag=0,company_name,id"/>            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="是否已生成提单">
              <j-dict-select-tag placeholder="请选择是否已生成提单" v-model="queryParam.prepareOrderStatus"
                                 dictCode="yes_no_status"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="预开单号">
              <a-input placeholder="请输入预开单号" v-model="queryParam.prepareId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="仓库">
              <j-dict-select-tag placeholder="请输入仓库" v-model="queryParam.destination" dictCode="sm_stock,name,name"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="创建人">
              <a-input placeholder="请输入创建人" v-model="queryParam.createBy"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="创建日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择开始时间" class="query-group-cust"
                      v-model="queryParam.createTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择结束时间" class="query-group-cust"
                      v-model="queryParam.createTime_end"></j-date>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="备注">
              <a-input placeholder="请输入备注" v-model="queryParam.remarks"></a-input>
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
      <a-button @click="handlePreWarehouse" type="primary">预开单入库</a-button>
      <a-button @click="cancelStorage" type="primary">取消入库</a-button>
      <a-button @click="updatePrepareOrderPrice" type="primary">批量修改明细单价</a-button>
      <a-button @click="handleAddSalesOrder()" type="primary">生成提单</a-button>
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

        <!--<span slot="action" slot-scope="text, record">-->
          <!--&lt;!&ndash;<a @click="handleEdit(record)">编辑</a>&ndash;&gt;-->

          <!--<a-divider type="vertical"/>-->
          <!--<a-dropdown>-->
            <!--<a class="ant-dropdown-link">更多 <a-icon type="down"/></a>-->
            <!--<a-menu slot="overlay">-->
              <!--&lt;!&ndash;<a-menu-item>&ndash;&gt;-->
                <!--&lt;!&ndash;<a @click="handleDetail(record)">详情</a>&ndash;&gt;-->
              <!--&lt;!&ndash;</a-menu-item>&ndash;&gt;-->
              <!--<a-menu-item>-->
                <!--<a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">-->
                  <!--<a>删除</a>-->
                <!--</a-popconfirm>-->
              <!--</a-menu-item>-->
            <!--</a-menu>-->
          <!--</a-dropdown>-->
        <!--</span>-->

      </a-table>
    </div>

    <sales-order-modal ref="modalForm" @ok="modalFormOk"/>

    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="预开明细" key="1">
        <prepare-order-det-list ref="podl" :mainId="selectedMainId"
                                :parentRecord="selectionRows[0]"></prepare-order-det-list>
      </a-tab-pane>
    </a-tabs>

    <j-modal
      :visible="preVisible"
      title="预开单材料入库"
      width="60%"
      @ok="handlePreOK"
      @cancel="handleCancel"
    >
      <a-row :gutter="24">
        <a-col :xl="6" :lg="7" :md="8" :sm="24">
          <a-form-item label="仓库">
            <a-input disabled="true" v-model="stockHouseId"></a-input>
          </a-form-item>
        </a-col>
        <a-col :xl="6" :lg="7" :md="8" :sm="24">
          <a-form-item label="备注">
            <a-input disabled="true" v-model="remark"></a-input>
          </a-form-item>
        </a-col>
      </a-row>

    </j-modal>

    <j-modal
      :visible="updatePriceVisible"
      title="批量修改明细单价"
      width="60%"
      @ok="handleUpdatePriceOK"
      @cancel="handleCancel"
    >
      <a-row :gutter="24">
        <a-col :xl="6" :lg="7" :md="8" :sm="24">
          <a-form-item label="单价">
            <a-input v-model="price"></a-input>
          </a-form-item>
        </a-col>
      </a-row>

    </j-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import {mixinDevice} from '@/utils/mixin'
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import PrepareOrderModal from './modules/PrepareOrderModal'
  import SalesOrderModal from '../../om/SalesOrder/modules/SalesOrderModal'
  import PrepareOrderDetList from './PrepareOrderDetList'
  import {getAction, postAction} from '@api/manage'
  import JSearchSelectTag from "../../../components/dict/JSearchSelectTag"
  import JDate from '@/components/jeecg/JDate.vue'

  export default {
    name: 'PrepareOrderList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      JSearchSelectTag,
      PrepareOrderModal,
      PrepareOrderDetList,
      SalesOrderModal,
      JDate
    },
    data() {
      return {
        description: '预开单信息表管理页面',
        queryParam: {
          prepareOrderStatus: '0'
        },
        prepareOrderId:'',
        // 表头
        columns: [
          {
            title: '#',
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
            align: "center",
            dataIndex: 'createTime'
          },
          {
            title: '创建人',
            align: "center",
            dataIndex: 'createBy_dictText'
          },
          {
            title: '客户名称',
            align: "center",
            dataIndex: 'customerId_dictText'
          },
          {
            title: '仓库',
            align: "center",
            dataIndex: 'destination'
          },
          {
            title: '预开单号',
            align: "center",
            dataIndex: 'prepareOrderNo'
          },
          {
            title: '提单生成标识',
            align: "center",
            dataIndex: 'prepareOrderStatus_dictText'
          },
          {
            title: '入库标识',
            align: "center",
            dataIndex: 'stockStatus_dictText'
          },
          {
            title: '备注',
            align: "center",
            dataIndex: 'remarks'
          },
          // {
          //   title: '操作',
          //   dataIndex: 'action',
          //   align: "center",
          //   fixed: "right",
          //   width: 147,
          //   scopedSlots: {customRender: 'action'}
          // }
        ],
        selectedMainId: '',
        preVisible: false,
        updatePriceVisible: false,
        price: 0,
        stockHouseId: '',
        remark: '',
        url: {
          list: "/sm/prepareOrder/list",
          delete: "/sm/prepareOrder/delete",
          deleteBatch: "/sm/prepareOrder/deleteBatch",
          exportXlsUrl: "/sm/prepareOrder/exportXls",
          prepareOrderById: "/sm/prepareOrder/salesOrderByPrepareOrder",
          cancelStorage: "/sm/prepareOrder/cancelStorage",
          importExcelUrl: "sm/prepareOrder/importExcel",
          putInStorageBatch: "sm/prepareOrder/putInStorageBatch",
          updatePreparePrice: "/sm/prepareOrder/updatePreparePrice"
        },
        dictOptions: {},
      }
    },
    created() {
    },
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig() {
      },
      handlePrepareOrder() {
        this.loading = true;
        getAction(this.url.prepareOrderById, {id: this.selectionRows[0].id}).then((res) => {
          if (res.success) {
            this.$message.success("生成提单成功！！！")
          }
          this.loading = true;
        })
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedMainId = selectedRowKeys[0];
        this.selectedRowKeys = selectedRowKeys;
        this.selectionRows = selectionRows;
      },
      handlePreWarehouse() {
        if (this.selectedRowKeys.length !== 1) {
          this.$message.warning("请勾选一条信息！！")
        }
        this.preVisible = true;
        debugger
        this.stockHouseId = this.selectionRows[0].destination;
        this.remark = this.selectionRows[0].remarks;
      },
      cancelStorage() {
        if (this.selectedRowKeys.length !== 1) {
          this.$message.warning("请勾选一条信息！！")
        }
        if (this.selectionRows[0].stockStatus !== 'on_stock') {
          this.$message.warning("未入库提单不需要取消！！！")
        } else if (this.selectionRows[0].prepareOrderStatus === '1') {
          this.$message.warning("已开提单不能取消！！！")
        }else {
          var that = this
          var prepareOrderId = this.selectedMainId
          this.$confirm({
            title: '取消入库',
            content: '选中数据是否取消入库?',
            onOk: function () {
              debugger
              getAction(that.url.cancelStorage, { prepareOrderId: prepareOrderId }).then((res) => {
                if (res.success) {
                  that.$emit('取消入库成功')
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
        }
      },
      handleCancel() {
        this.preVisible = false;
        this.updatePriceVisible = false;
        this.price = 0;
      },
      handlePreOK() {
        if (this.selectedRowKeys.length == 0) {
          this.$message.warning("请勾选！！")
        }
        this.loading = true;
        postAction(this.url.putInStorageBatch, {
          ids: this.selectedRowKeys,
          remark: this.remark,
          stockHouseId: this.stockHouseId
        }).then((res) => {
            if (res.success) {
              this.$message.success("入库成功！")
              this.preVisible = false;
            }
          }
        ).finally(() => {
          this.loadData();
        })

      },
      handleAddSalesOrder() {
        if (this.selectionRows.length !== 1) {
          this.$message.warning("请勾选一条预开单！！")
        } else {
          if (this.selectionRows[0].stockStatus !== 'on_stock') {
            this.$message.warning("生成提单前请先入库！！！")
          } else if (this.selectionRows[0].prepareOrderStatus == "1") {
            this.$message.warning("该预开单已经生成过提单")
          } else {
            this.selectionRows[0]['prepareOrderId'] = this.selectionRows[0].id
            this.selectionRows[0]['remark'] = this.selectionRows[0].remarks
            this.handleCobyAdd(this.selectionRows[0]);
          }
        }
      },
      updatePrepareOrderPrice() {
        this.updatePriceVisible = true;
      },
      handleUpdatePriceOK() {
        var ids = "";
        for (var a = 0; a < this.selectedRowKeys.length; a++) {
          ids += this.selectedRowKeys[a] + ",";
        }
        getAction(this.url.updatePreparePrice, {prepareOrderIds: ids, amount: this.price}).then((res)=>{
            if(res.success){
              this.$message.success("修改成功！！！")
              this.handleCancel();
            }
            if(res.code !== 200) {
              this.$message.warning(res.message);
            }
        }).finally(() => {
            this.$refs['podl'].loadData();
          }
        )

      }

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>