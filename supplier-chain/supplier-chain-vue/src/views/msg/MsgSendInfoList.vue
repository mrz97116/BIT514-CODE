<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="电文序列号">
              <a-input placeholder="请输入电文序列号" v-model="queryParam.msgSeqNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="电文号">
              <a-input placeholder="请输入电文号" v-model="queryParam.msgIntfId"></a-input>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="电文发送方系统">
                <j-dict-select-tag placeholder="请选择电文发送方系统" v-model="queryParam.msgSrcSys" dictCode="elec_msg_systems"/>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="电文接收方系统">
                <j-dict-select-tag placeholder="请选择电文接收方系统" v-model="queryParam.msgDestSys" dictCode="elec_msg_systems"/>
              </a-form-item>
            </a-col>
            <a-col :xl="10" :lg="11" :md="12" :sm="24">
              <a-form-item label="电文发送时间">
                <j-date placeholder="请选择开始时间" class="query-group-cust" v-model="queryParam.msgSendTimeStart" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss"/>
                <span class="query-group-split-cust"></span>
                <j-date placeholder="请选择结束时间" class="query-group-cust" v-model="queryParam.msgSendTimeEnd" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss"/>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="电文处理结果">
                <j-dict-select-tag placeholder="请选择电文处理结果" v-model="queryParam.msgProcRt" dictCode="elec_msg_result_type"/>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="电文类型">
                <j-dict-select-tag placeholder="请选择电文类型" v-model="queryParam.msgType" dictCode="elec_msg_type"/>
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
      <a-button @click="handleReSend" type="primary" icon="reload">重发电文</a-button>
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

        <span slot="action" slot-scope="text, record">
          <a @click="handleShowMsgInfo(record)">详情</a>
          <!-- <a @click="handleEdit(record)">编辑</a>

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
          </a-dropdown> -->
        </span>

      </a-table>
    </div>

    <msg-send-info-modal ref="modalForm" @ok="modalFormOk"></msg-send-info-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import MsgSendInfoModal from './modules/MsgSendInfoModal'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import JInput from '@comp/jeecg/JInput'
  import JDate from '@comp/jeecg/JDate'
  import moment from 'moment'
  import { postAction } from '@/api/manage'

  export default {
    name: 'MsgSendInfoList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      JDictSelectTag,
      JInput,
      MsgSendInfoModal,
      JDate
    },
    data () {
      return {
        description: 'MSG_SEND_INFO管理页面',
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
            title:'电文序列号',
            align:"center",
            width:250,
            dataIndex: 'msgSeqNo'
          },
          {
            title:'电文号',
            width:80,
            align:"center",
            dataIndex: 'msgIntfId'
          },
          {
            title:'电文发送方系统',
            align:"center",
            width:150,
            ellipsis:true,
            dataIndex: 'msgSrcSys_dictText'
          },
          {
            title:'电文接收方系统',
            align:"center",
            width:150,
            ellipsis:true,
            dataIndex: 'msgDestSys_dictText'
          },
          {
            title:'电文接收时间',
            align:"center",
            dataIndex: 'msgSendTime',
            customRender:function (t,r,index) {
              if(!t){
                return "";
              }
              var date = new Date(
              t.substring(0,4),
              Number(t.substring(4,6)) - 1,
              t.substring(6,8),
              t.substring(8,10),
              t.substring(10,12),
              t.substring(12,14),
              );
              return moment(date.getTime()).format('YYYY-MM-DD HH:mm:ss');
            }
          },
          {
            title:'电文接收次数',
            width:100,
            align:"center",
            dataIndex: 'msgSendCount'
          },
          {
            title:'电文处理结果',
            align:"center",
            dataIndex: 'msgProcRt_dictText'
          },
          {
            title:'电文类型',
            align:"center",
            width:100,
            dataIndex: 'msgType_dictText'
          },
          {
            title:'补充信息',
            align:"center",
            width:150,
            dataIndex: 'msgRemark',
            ellipsis:true
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:60,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/msg/info/send/findList",
          reSend: "/msg/info/send/reSend",
        },
        dictOptions:{},
      }
    },
    created() {
      
    },
    methods: {
      initDictConfig(){
      },
      handleReSend(){
        if (this.selectedRowKeys.length <= 0) {
          this.$message.warning('请选择一条记录！');
          return;
        }
        postAction(this.url.reSend, this.selectionRows).then((res) => {
          if (res.success) {
            this.$message.success(res.message);
            this.loadData();
            this.onClearSelected();
          } else {
            this.$message.warning(res.message);
          }
        }).finally(() => {
          this.loading = false;
        }); 
      },
      handleShowMsgInfo(record){
        console.log(this.dataSource);
        this.$refs.modalForm.edit(record);
        this.$refs.modalForm.title = record.msgIntfId + '@' + record.msgSeqNo;
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>