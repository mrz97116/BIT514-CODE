<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="回调电文号">
              <a-input placeholder="请输入回调电文号" v-model="queryParam.intfId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="序列号">
              <a-input placeholder="请输入序列号" v-model="queryParam.seqNo"></a-input>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="源系统">
                <j-dict-select-tag placeholder="请选择源系统" v-model="queryParam.srcSystem" dictCode="elec_msg_systems"/>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="目标系统">
                <j-dict-select-tag placeholder="请选择目标系统" v-model="queryParam.destSystem" dictCode="elec_msg_systems"/>
              </a-form-item>
            </a-col>
            <a-col :xl="10" :lg="11" :md="12" :sm="24">
              <a-form-item label="发送时间">
                <j-date placeholder="请选择开始时间" class="query-group-cust" v-model="queryParam.msgSendTimeStart" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss"/>
                <span class="query-group-split-cust"></span>
                <j-date placeholder="请选择结束时间" class="query-group-cust" v-model="queryParam.msgSendTimeEnd" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss"/>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="源系统接口">
                <a-input placeholder="请输入源系统接口" v-model="queryParam.srcIntfId"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="源系统序列号">
                <a-input placeholder="请输入源系统序列号" v-model="queryParam.srcSeqNo"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="状态">
                <j-dict-select-tag placeholder="请选择状态" v-model="queryParam.status" dictCode="elec_msg_callback_status"/>
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
          <a v-if="record.status == 'E'" @click="handleShowMsgInfo(record)">详情</a>
          <span v-else>-</span>
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

    <msg-callback-log-modal ref="modalForm" @ok="modalFormOk"></msg-callback-log-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import MsgCallbackLogModal from './modules/MsgCallbackLogModal'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import JInput from '@comp/jeecg/JInput'
  import JDate from '@comp/jeecg/JDate'
  import moment from 'moment'
  import { postAction } from '@/api/manage'

  export default {
    name: 'MsgCallbackLogList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      JDictSelectTag,
      JInput,
      MsgCallbackLogModal,
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
            title:'回调电文号',
            width:100,
            align:"center",
            dataIndex: 'intfId'
          },
          {
            title:'序列号',
            align:"center",
            width:250,
            dataIndex: 'seqNo'
          },
          {
            title:'源系统',
            align:"center",
            width:150,
            ellipsis:true,
            dataIndex: 'srcSystem_dictText'
          },
          {
            title:'目标系统',
            align:"center",
            width:150,
            ellipsis:true,
            dataIndex: 'destSystem_dictText'
          },
          {
            title:'发送时间',
            align:"center",
            dataIndex: 'sendTime',
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
            title:'源系统接口',
            width:100,
            align:"center",
            dataIndex: 'srcIntfId'
          },
          {
            title:'源系统序列号',
            align:"center",
            dataIndex: 'srcSeqNo'
          },
          {
            title:'状态',
            align:"center",
            width:100,
            dataIndex: 'status_dictText'
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
          list: "/msg/callback/findByPage",
        },
        dictOptions:{},
      }
    },
    created() {
      
    },
    methods: {
      initDictConfig(){
      },
      handleShowMsgInfo(record){
        console.log(this.dataSource);
        this.$refs.modalForm.edit(record);
        this.$refs.modalForm.title = record.intfId + '@' + record.seqNo;
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>