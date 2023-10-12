<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="客户">
              <j-search-select-tag placeholder="请选择客户" v-model="queryParam.customerId" dict="cm_customer_profile where  del_flag=0,company_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="创建日期">
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择开始时间" class="query-group-cust" v-model="queryParam.createTime_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date :show-time="true" date-format="YYYY-MM-DD" placeholder="请选择结束时间" class="query-group-cust" v-model="queryParam.createTime_end"></j-date>
            </a-form-item>
          </a-col>
          <!--          <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
          <!--            <a-form-item label="生效标识">-->
          <!--              <j-dict-select-tag placeholder="请选择生效标识" v-model="queryParam.active" dictCode="valid_status"/>-->
          <!--            </a-form-item>-->
          <!--          </a-col>-->
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="审核状态">
              <j-dict-select-tag placeholder="请选择审核状态" v-model="queryParam.status" dictCode="common_check_status"/>
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
      <a-button @click="handleAdd" type="primary" icon="plus" v-has="'credit'">新增</a-button>
      <a-button @click="creditAdjust" type="primary" icon="plus" v-has="'credit'">调整</a-button>
      <a-button type="primary" @click="updateBatch('approve')" v-has="'credit:examine'"><a-icon type="check"/>审核</a-button>
      <a-button type="primary"  @click="updateBatch('reject')" v-has="'credit:examine'"><a-icon type="close"/>弃审</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('授信额度表')" v-has="'credit'">导出</a-button>
      <!--
            <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
              <a-button type="primary" icon="import">导入</a-button>
            </a-upload>
      -->
      <!--      <a-dropdown v-if="selectedRowKeys.length > 0">-->
      <!--        <a-menu slot="overlay">-->
      <!--          <a-menu-item key="1" @click="batchDel" v-has="'credit'"><a-icon type="delete"/>删除</a-menu-item>-->
      <!--        </a-menu>-->
      <!--        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>-->
      <!--      </a-dropdown>-->
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
<!--          <a  @click="updateSingle('approve',record)" :disabled="record.status === 'approve' || record.status === 'reject' ? true : false" v-has="'credit:yes'">审核</a>-->
          <!--          <a-divider type="vertical" />-->
          <a @click="handleDetail(record)">详情</a>
          <!--
                    <a-dropdown>
                      <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
                      <a-menu slot="overlay">
                        <a-menu-item>
                          <a @click="handleDetail(record)">详情</a>
                        </a-menu-item>
                      </a-menu>
                    </a-dropdown>
          -->
        </span>

      </a-table>
    </div>

    <j-modal
      :visible.sync="modal.visible"
      :width="500"
      :title="modal.title"
      :fullscreen.sync="modal.fullscreen"
      :switchFullscreen="modal.switchFullscreen"
      :okClose = "modal.okClose"
      @ok = "handleOk"
      @cancel="handleCancel"

    >

      <div>
        <template>
          <div>
            <a-form layout="inline">
              <a-row :gutter="24">
                <a-col :span="24">
                  <a-form-item label="授信编号">
                    <a-input disabled placeholder="请输入授信编号" v-model="creditNo"></a-input>
                  </a-form-item>
                </a-col>
                <a-col :span="24">
                  <a-form-item label="调整类型">
                    <j-dict-select-tag v-model="creditAdjustStatus" dictCode="credit_adjust_status"
                                       placeholder="请选择调整类型" style="width: 300%"></j-dict-select-tag>
                  </a-form-item>
                </a-col>
                <a-col :span="24">
                  <a-form-item label="额度">
                    <a-input :disabled="creditAdjustStatus == 'reduce' && availAmount == 0" placeholder="请输入调整额度" v-model="selectValue"></a-input>
                  </a-form-item>
                </a-col>
                <a-col :span="24">
                  <a-form-item label="备注">
                    <a-input  placeholder="请输入备注" v-model="remark"></a-input>
                  </a-form-item>
                </a-col>

              </a-row>
            </a-form>
          </div>
          <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
            <i class="anticon anticon-info-circle ant-alert-icon"></i> 可调整金额：<a style="font-weight: 600">{{ availAmount }}</a> 元
          </div>
        </template>
      </div>
    </j-modal>

    <credit-modal ref="modalForm" @ok="modalFormOk"></credit-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import CreditModal from './modules/CreditModal'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import JDate from '@/components/jeecg/JDate.vue'
  import {filterMultiDictText,filterDictTextByCache} from '@/components/dict/JDictSelectUtil'
  import {getAction} from "../../../api/manage";
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'

  export default {
    name: 'CreditList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      JDictSelectTag,
      JDate,
      CreditModal,
      JSearchSelectTag
    },
    data () {
      return {
        description: '授信额度表管理页面',
        modal: {
          title: '授信额度调整',
          visible: false,
          fullscreen: false,
          switchFullscreen: false,
          okClose: false,
        },
        availAmount:0,
        remark : '',
        creditAdjustStatus: '',
        creditNo: '',
        selectValue: '',
        selectedRowKeys:[],
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
            title:'客户',
            align:"center",
            dataIndex: 'customerText'
          },
          {
            title:'额度',
            align:"center",
            dataIndex: 'amount'
          },
          {
            title:'可用金额',
            align:"center",
            dataIndex: 'availAmount'
          },
          {
            title:'授信编号',
            align:"center",
            dataIndex: 'creditNo'
          },
          {
            title: '创建日期',
            align: "center",
            dataIndex: 'createTime',
          },
          // {
          //   title:'失效时间',
          //   align:"center",
          //   dataIndex: 'endTime',
          //   customRender:function (text) {
          //     return !text?"":(text.length>10?text.substr(0,10):text)
          //   }
          // },
          // {
          //   title:'生效标识',
          //   align:"center",
          //   dataIndex: 'active',
          //   customRender:function (text) {
          //     return filterDictTextByCache("valid_status",text);
          //   }
          // },
          {
            title:'审核状态',
            align:"center",
            dataIndex: 'status',
            customRender:function (text) {
              return filterDictTextByCache("common_check_status",text);
            }
          },
          {
            title:'审核人',
            align:"center",
            dataIndex: 'checker'
          },
          {
            title:'审核日期',
            align:"center",
            dataIndex: 'checkDate',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/fm/credit/list",
          delete: "/fm/credit/delete",
          deleteBatch: "/fm/credit/deleteBatch",
          exportXlsUrl: "/fm/credit/exportXls",
          importExcelUrl: "fm/credit/importExcel",
          creditAdjust: "fm/credit/creditAdjust"

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
      updateBatch(param) {
        var that = this;
        if(that.selectedRowKeys.length == 0){
          that.$message.warning("请勾选一条记录！")
        }else {
          var ids = "";
          for (var a = 0; a < that.selectedRowKeys.length; a++) {
            ids += that.selectedRowKeys[a] + ",";
          }
          var title = ''
          var content = ''
          if (param == 'approve'){
            title = "确认审核"
            content = "是否审核通过选中数据?"
          }
          if(param == 'reject'){
            title = "确认审核"
            content = "是否弃审选中数据?"
          }
          that.$confirm({
            title: title,
            content: content,
            onOk: function () {
              that.loading = true;
              // var id = record.id + ",";
              getAction("/fm/credit/passBatch", {ids: ids,tag:param}).then((res) => {
                if (res.success) {
                  that.$message.success("操作成功！")
                }
                if(res.code===500){
                  that.$message.warning(res.message)
                }
                that.loading = false;
                that.loadData()
              }).finally(() => {
                that.loading = false;
              });
            }
          });
          // getAction("/fm/credit/passBatch", {ids: ids,tag:param}).then((res) => {
          //   if (res.success) {
          //     this.$message.success("操作成功！")
          //   }
          //   if(res.code===500){
          //     this.$message.warning(res.message)
          //   }
          //   this.loading = false;
          //   this.loadData()
          // })
        }

      },
      creditAdjust(){
        debugger
        if(this.selectedRowKeys.length == 0 ){
          this.$message.warning("请勾选要调整的记录！")
        }else if (this.selectedRowKeys.length > 1){
          this.$message.warning("只能勾选一条授信信息！")
        }else {
          this.creditNo = this.selectionRows[0].creditNo;
          this.modal.visible=true;
        }
        console.log("111",this.selectionRows);
        this.availAmount = this.selectionRows[0].availAmount;
        this.creditNo = this.selectionRows[0].creditNo;
      },
      handleOk() {
        debugger
        if(this.selectValue == undefined){
          this.$message.warning("请输入调整额度！")
        }else if (this.creditAdjustStatus == undefined) {
          this.$message.warning("请输入调整类型！")
        } else {
          let params = {
            id : this.selectedRowKeys[0],
            creditNo : this.creditNo,
            adjustStatus : this.creditAdjustStatus,
            adjustAmount : this.selectValue,
            remark : this.remark
          };
          getAction(this.url.creditAdjust,params).then(res=>{
            if(res.success){
              this.$message.success("操作成功,请前往授信调整页面进行审核")
              this.loadData();
            }else{
              this.$message.warning(res.message)
            }
          });
        }

        this.selectValue = '';
        this.creditNo = '';
        this.selectedRowKeys = [];
        this.modal.visible=false;
      },
      handleCancel(){
        this.selectValue = '';
        this.creditNo = '';
        this.selectedRowKeys = [];
        this.modal.visible=false;
      },
      updateSingle(param,record) {
        var that = this;
        this.$confirm({
          title: "确认审核",
          content: "是否审核通过选中数据?",
          onOk: function () {
            that.loading = true;
            var id = record.id + ",";
            getAction("/fm/credit/passBatch", {ids: id,tag:param}).then((res) => {
              if (res.success) {
                that.$message.success("操作成功！")
              }
              if(res.code===500){
                that.$message.warning(res.message)
              }
              that.loading = false;
              that.loadData()
            }).finally(() => {
              that.loading = false;
            });
          }
        });
        // var id = record.id + ",";
        // getAction("/fm/credit/passBatch", {ids: id,tag:param}).then((res) => {
        //   if (res.success) {
        //     this.$message.success("操作成功！")
        //   }
        //   if(res.code===500){
        //     this.$message.warning(res.message)
        //   }
        //   this.loading = false;
        //   this.loadData()
        // })
      }

    },
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>