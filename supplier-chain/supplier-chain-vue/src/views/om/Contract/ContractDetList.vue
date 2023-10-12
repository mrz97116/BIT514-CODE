<template>
  <a-card :bordered="false" :class="'cust-erp-sub-tab'">
    <!-- 操作按钮区域 -->
<!--    <div class="table-operator" v-if="mainId">-->
<!--      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>-->
      <!--      <a-button type="primary" icon="download" @click="handleExportXls('合同明细表')">导出</a-button>-->
<!--      <a-button type="primary" icon="plus" v-show="selectedRowKeys.length > 0" @click="handleSaleOrder">生成销售单</a-button>-->
<!--      <a-button @click="toControlDevice" v-show="selectedRowKeys.length > 0 && selectionRows[0].controlRecordId == null" type="primary">控款生成提单</a-button>-->
      <!--      <a-upload-->
      <!--        name="file"-->
      <!--        :showUploadList="false"-->
      <!--        :multiple="false"-->
      <!--        :headers="tokenHeader"-->
      <!--        :action="importExcelUrl"-->
      <!--        @change="handleImportExcel">-->
      <!--        <a-button type="primary" icon="import">导入</a-button>-->
      <!--      </a-upload>-->
<!--      <a-dropdown v-if="selectedRowKeys.length > 0">-->
<!--        <a-menu slot="overlay">-->
<!--          <a-menu-item key="1" @click="batchDel">-->
<!--            <a-icon type="delete"/>-->
<!--            删除-->
<!--          </a-menu-item>-->
<!--        </a-menu>-->
<!--        <a-button style="margin-left: 8px"> 批量操作-->
<!--          <a-icon type="down"/>-->
<!--        </a-button>-->
<!--      </a-dropdown>-->
<!--    </div>-->

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
        bordered
        rowKey="id"
        :scroll="{x:true}"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        @change="handleTableChange">
<!--        :rowSelection="{selectedRowKeys: selectedRowKeys, type:'radio', onChange: onSelectChange}"-->


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

<!--        <span slot="action" slot-scope="text, record">-->
<!--          <a @click="handleEdit(record)">编辑</a>-->
          <!--          <a-divider type="vertical"/>-->
<!--          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">-->
<!--            <a>删除</a>-->
<!--          </a-popconfirm>-->
<!--        </span>-->

      </a-table>
    </div>

    <j-modal
      :visible="visible"
      title="生成装车单的重量"
      @ok="handleOK1"
      @cancel="handleCancel"
    >
      <a-row :gutter="24">
        <a-col :span="30">
          <a-form-item>
            <a-input-number style="width: 150px" placeholder="请输入金额" v-model="weight"></a-input-number>
          </a-form-item>
        </a-col>
      </a-row>
    </j-modal>

    <contractDet-modal ref="modalForm" @ok="modalFormOk" :mainId="mainId" :parentRecord="parentRecord"></contractDet-modal>
    <!--    控款弹窗-->
    <ControlDeviceModal ref="ControlDeviceModal"></ControlDeviceModal>

  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import ContractDetModal from './modules/ContractDetModal'
  import { getAction } from '@/api/manage'
  import ControlDeviceModal from './modules/ControlDeviceModal'
  import {postAction} from "../../../api/manage";
  import { filterDictTextByCache } from '@/components/dict/JDictSelectUtil'

  export default {
    name: 'ContractDetList',
    mixins: [JeecgListMixin],
    components: { ContractDetModal, ControlDeviceModal },
    props: {
      mainId: {
        type: String,
        default: '',
        required: false
      },
      parentRecord: {
        type: Object,
        default: function() {
          return {}
        },
        required: true
      }

    },
    watch: {
      mainId: {
        immediate: true,
        handler(val) {
          if (!this.mainId) {
            this.clearList()
          } else {
            // this.queryParam['parentId'] = val
            this.url.list='/om/contract/queryContractDetByMainId?id='+ val
            this.loadDataHere(1)
            // 重新加载完数据之后要把选中行取消
            // this.selectedRowKeys = []
          }
        }
      }
    },
    data() {
      return {
        description: '合同主档表管理页面',
        disableMixinCreated: true,
        visible: false,
        weight: 0,
        // 表头
        columns: [
          {
            title: '序号',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: 'center',
            customRender: function(t, r, index) {
              return parseInt(index) + 1
            }
          },
          // {
          //   title: '合同编号',
          //   align: 'center',
          //   dataIndex: 'contractNo'
          // },
          {
            title: '合同明细号',
            align: 'center',
            dataIndex: 'contractListNo'
          },
          {
            title: '合同类型',
            align: 'center',
            dataIndex: 'contractType',
            customRender: function (text){
              return filterDictTextByCache('contract_type', text)
            }
          },
          {
            title: '订货长度',
            align: 'center',
            dataIndex: 'orderLen'
          },
          {
            title: '订货宽度',
            align: 'center',
            dataIndex: 'orderWidth'
          },
          {
            title: '订货厚度',
            align: 'center',
            dataIndex: 'orderThick'
          },
          // {
          //   title: '预交重量',
          //   align: 'center',
          //   dataIndex: 'preWeight'
          // },
          // {
          //   title: '预交件数',
          //   align: 'center',
          //   dataIndex: 'preQty'
          // },
          {
            title: '已交重量',
            align: 'center',
            dataIndex: 'delivyWeight'
          },
          {
            title: '已交重量',
            align: 'center',
            dataIndex: 'delivyQty'
          },
          {
            title: '品名中文',
            align: 'center',
            dataIndex: 'prodCname'
          },
          {
            title: '品名中文别名',
            align: 'center',
            dataIndex: 'prodCnameOther'
          },
          {
            title: '标准',
            align: 'center',
            dataIndex: 'sgStd'
          },
          {
            title: '物料种类',
            align: 'center',
            dataIndex: 'matKind',
            customRender: function (text){
              return filterDictTextByCache('mat_kind', text)
            }
          },
          {
            title: '过磅价格',
            align: 'center',
            dataIndex: 'weightingPrice'
          },
          {
            title: '产品大类',
            align: 'center',
            dataIndex: 'prodClassCode',
            customRender: function (text){
              return filterDictTextByCache('prod_code', text)
            }
          },
          // {
          //   title: '加价',
          //   align: 'center',
          //   dataIndex: 'addPrice'
          // },
          {
            title: '物料重量',
            align: 'center',
            dataIndex: 'weight'
          },
          {
            title: '单价',
            align: 'center',
            dataIndex: 'price'
          },
          {
            title: '过磅重量',
            align: 'center',
            dataIndex: 'weightingWeight'
          },
          {
            title: '计重方式',
            align: 'center',
            dataIndex: 'weightType',
            customRender:(text) =>{
              return filterDictTextByCache('wt_method_code',text);
            }
          },
          // {
          //   title: '操作',
          //   dataIndex: 'action',
          //   align: 'center',
          //   fixed: 'right',
          //   width: 147,
          //   scopedSlots: { customRender: 'action' }
          // }
        ],
        url: {
          list: '/om/contract/queryContractDetByMainId',
          delete: '/om/contract/deleteContractDet',
          deleteBatch: '/om/contract/deleteBatchContractDet',
          exportXlsUrl: '/om/contract/exportContractDet',
          importUrl: '/om/contract/importContractDet',
          addSaleOrder: 'om/salesOrder/addSaleOrder'
        },
        dictOptions: {
          productCode: []
        }

      }
    },
    computed: {
      importExcelUrl() {
        return `${window._CONFIG['domianURL']}/${this.url.importUrl}/${this.mainId}`
      }
    },
    methods: {
      toControlDevice() {
        /**
         * this.parentRecord.customerId: 合同主表的客户id
         * this.parentRecord.contractNo: 合同主表的合同编号
         *
         */
        // debugger;
        this.$refs.ControlDeviceModal.loadData(this.selectedRowKeys[0],
          this.parentRecord.customerId,
          this.parentRecord.contractNo,
          this.selectionRows[0].weight,
          this.selectionRows[0].price)
      },

      clearList() {
        this.dataSource = []
        this.selectedRowKeys = []
        this.ipagination.current = 1
      },

      //生成销售单
      handleSaleOrder() {
        this.weight = this.selectionRows[0].weight || 0;
        this.visible = true;
      },

      handleOK1() {
        if(this.weight > this.selectionRows[0].weight || 0) {
          this.$message.warning("选择的重量已超过上限")
        } else {
          var param = { parentId: this.mainId, id: this.selectedRowKeys[0], weight:this.weight}
          postAction(this.url.addSaleOrder, param).then((res) => {
            if (res.success) {
              this.$message.success('销售单已生成')
            }
            if (res.code === 510) {
              this.$message.warning(res.message)
            } else {
              this.$message.warning('销售单已存在')
            }
            this.loading = false
          })
          this.handleCancel();
        }
      },
      handleCancel() {
        this.visible = false;
      },
      loadDataHere(arg){
        this.onClearSelected();
        if(!this.url.list){
          this.$message.error("请设置url.list属性!")
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        var params = this.getQueryParams();//查询条件
        this.loading = true;
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.dataSource = res.result;
            debugger
          }
          if(res.code!=200){
            this.$message.warning(res.message)
          }
          this.loading = false;
        })
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>
