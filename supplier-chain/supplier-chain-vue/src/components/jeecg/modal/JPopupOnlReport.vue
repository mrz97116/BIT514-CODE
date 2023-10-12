<template>
  <j-modal
    :title="title"
    :width="modalWidth"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    wrapClassName="j-popup-modal"
    @ok="handleSubmit"
    @cancel="handleCancel"
    cancelText="关闭">

    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchByquery">
        <a-row :gutter="24" v-if="showSearchFlag">
          <template v-for="(item,index) in queryInfo">
            <template v-if=" item.hidden==='1'">
<!--              <a-col :md="8" :sm="24" :key=" 'query'+index " v-show="toggleSearchStatus">-->
              <a-col :md="8" :sm="24" :key=" 'query'+index ">
                <online-query-form-item :queryParam="queryParam" :item="item" :dictOptions="dictOptions">
                </online-query-form-item>
              </a-col>

<!--              <a-col v-if="item.view === 'string' || item.view === 'integer'" :md="8" :sm="24" :key="'query'+index">-->
<!--                <a-form-item :label="item.label">-->
<!--                  <a-input :disabled="item.disabled" :placeholder="'请输入'+item.label"-->
<!--                           v-model="queryParam[item.field]" @change="valueDealWith()"/>-->
<!--                </a-form-item>-->
<!--              </a-col>-->

<!--              <a-col v-else-if="item.view === 'list'" :xl="8" :lg="9" :md="20" :sm="26" :key="'query'+index">-->
<!--                <a-form-item :label="item.label">-->
<!--                  <j-search-select-tag :disabled="item.disabled" :placeholder="'请选择'+item.label" v-model="queryParam[item.field]"-->
<!--                                       :dictOptions="dictOptions[item.field]">-->
<!--                  </j-search-select-tag>-->
<!--                </a-form-item>-->
<!--              </a-col>-->

            </template>
            <template v-else>

<!--              <a-col v-if="item.view === 'string' || item.view === 'integer'" :md="8" :sm="24" :key="'query'+index">-->
<!--                <a-form-item :label="item.label">-->
<!--                  <a-input :placeholder="'请输入'+item.label"-->
<!--                           v-model="queryParam[item.field]" @change="valueDealWith()"/>-->
<!--                </a-form-item>-->
<!--              </a-col>-->
<!--              <a-col v-else-if="item.view === 'list'" :xl="8" :lg="9" :md="20" :sm="26" :key="'query'+index">-->
<!--                <a-form-item :label="item.label">-->
<!--                  <j-search-select-tag :placeholder="'请选择'+item.label" v-model="queryParam[item.field]"-->
<!--                                       :dictOptions="dictOptions[item.field]">-->
<!--                  </j-search-select-tag>-->
<!--                </a-form-item>-->
<!--              </a-col>-->

              <a-col :md="8" :sm="24" :key=" 'query'+index ">
                <online-query-form-item :queryParam="queryParam" :item="item" :dictOptions="dictOptions"></online-query-form-item>
              </a-col>
            </template>
          </template>

          <a-col :md="8" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchByquery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
<!--              <a @click="handleToggleSearch" style="margin-left: 8px">-->
<!--                {{ toggleSearchStatus ? '收起' : '展开' }}-->
<!--                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>-->
<!--              </a>-->
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
      <i class="anticon anticon-info-circle ant-alert-icon"></i>
      已选择&nbsp;<a style="font-weight: 600">{{ table.selectedRowKeys.length }}</a>项&nbsp;&nbsp;
      <a style="margin-left: 24px" @click="onClearSelected">清空</a>

      <a v-if="!showSearchFlag" style="margin-left: 24px" @click="onlyReload">刷新</a>
    </div>

    <a-table
      ref="table"
      size="middle"
      bordered
      :rowKey="combineRowKey"
      :columns="table.columns"
      :dataSource="table.dataSource"
      :pagination="table.pagination"
      :loading="table.loading"
      :rowSelection="{fixed:true,selectedRowKeys: table.selectedRowKeys, onChange: handleChangeInTableSelect}"
      @change="handleChangeInTable"
      style="min-height: 300px"
      :scroll="tableScroll"
      :customRow="clickThenCheck">
    </a-table>


  </j-modal>
</template>

<script>
  import { getAction } from '@/api/manage'
  import {filterObj} from '@/utils/util'
  import { filterMultiDictText } from '@/components/dict/JDictSelectUtil'
  import { httpGroupRequest } from '@/api/GroupRequest.js'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import JInput from '../JInput'
  import OnlineQueryFormItem from '@/views/base/common/OnlineQueryFormItem'

  const MODAL_WIDTH = 1200;
  export default {
    name: 'JPopupOnlReport',
    props: ['multi', 'code', 'groupId','initQueryParam'],
    components:{
      JInput,
      JSearchSelectTag,
      OnlineQueryFormItem
    },
    data(){
      return {
        visible:false,
        title:"",
        confirmLoading:false,
        queryInfo:[],
        toggleSearchStatus:false,
        queryParam:{

        },
        dictOptions: {},
        url: {
          getColumns: '/online/cgreport/api/getRpColumns/',
          getData: '/online/cgreport/api/getData/',
          getQueryInfo: '/online/cgreport/api/getQueryInfo/'
        },
        table: {
          loading: true,
          // 表头
          columns: [],
          //数据集
          dataSource: [],
          // 选择器
          selectedRowKeys: [],
          selectionRows: [],
          // 分页参数
          pagination: {
            current: 1,
            pageSize: 10,
            pageSizeOptions: ['10', '20', '30'],
            showTotal: (total, range) => {
              return range[0] + '-' + range[1] + ' 共' + total + '条'
            },
            showQuickJumper: true,
            showSizeChanger: true,
            total: 0
          }
        },
        sorter:{
          column: "",
          order: "",
        },
        cgRpConfigId:"",
        modalWidth:MODAL_WIDTH,
        tableScroll:{x:MODAL_WIDTH-100}

      }
    },
    mounted() {
      // if(this.initQueryParam){
      //   this.queryParam.sg_sign="HRB400E";
      //   this.queryParam.stock_house_id = this.initQueryParam;
      // }
      this.loadColumnsInfo()
    },
    watch: {
      code() {
        this.loadColumnsInfo()
      },
      initQueryParam:{
        immediate: true,
        handler(val) {
          //val的值为 ‘value-模块’  例如，在提单管理传仓库id，那么val为 ‘1234649645646-salesOrder’
          let msg = val.split('-')
          if (val !== '') {
            if (msg[1] === 'process') this.queryParam.prod_class_code = msg[0]
            if (msg[1] === 'customerId')  this.queryParam.customer_id = msg[0]
            if (msg[8] === 'salesOrderContract') {
              this.queryParam.prod_cname_other = msg[0]
              this.queryParam.sg_sign = msg[1]
              this.queryParam.order_len = msg[2]
              this.queryParam.order_width = msg[3]
              this.queryParam.order_thick = msg[4]
              this.queryParam.prod_class_code = msg[5]
              this.queryParam.prod_cname = msg[6]
              this.queryParam.customer_id = msg[7]
            }
            this.loadColumnsInfo()
          }
        }
      }
    },
    computed:{
      showSearchFlag(){
        return this.queryInfo && this.queryInfo.length>0
      }
    },
    methods:{
      loadColumnsInfo(){
        let url = `${this.url.getColumns}${this.code}`
        //缓存key
        let groupIdKey
        if (this.groupId) {
          groupIdKey = this.groupId + url
        }
        httpGroupRequest(() => getAction(url), groupIdKey).then(res => {
          if(res.success){
            this.initDictOptionData(res.result.dictOptions);
            this.cgRpConfigId = res.result.cgRpConfigId
            this.title = res.result.cgRpConfigName
            let currColumns = res.result.columns
            for(let a=0;a<currColumns.length;a++){
              if(currColumns[a].customRender){
                let dictCode = currColumns[a].customRender;
                currColumns[a].customRender=(text)=>{
                  return filterMultiDictText(this.dictOptions[dictCode], text+"");
                }
              }
            }
            this.table.columns = [...currColumns]
            this.initQueryInfo()
            this.loadData(1)
          }
        })
      },
      initQueryInfo() {
        let url = `${this.url.getQueryInfo}${this.cgRpConfigId}`
        //缓存key
        let groupIdKey
        if (this.groupId) {
          groupIdKey = this.groupId + url
        }
        httpGroupRequest(() => getAction(url), groupIdKey).then((res) => {
          // console.log("获取查询条件", res);
          if (res.success) {
            this.queryInfo = res.result
            // for (let i in this.queryInfo){
            //   if(this.queryInfo[i].field === 'prod_class_code')
            //     this.queryInfo[i].view = 'sel_search';
            // }
            // if(this.initQueryParam.split('-')[1] === 'salesOrder'){
            //   for (let i in this.queryInfo) {
            //     if(this.queryInfo[i].field === 'stock_house_id')
            //       this.queryInfo[i].disabled= true
            //   }
            // }
            // if(this.initQueryParam.split('-')[1] === 'process'){
            //   for (let i in this.queryInfo) {
            //     if(this.queryInfo[i].field === 'prod_class_code')
            //       this.queryInfo[i].disabled= true
            //   }
            // }
            // if(this.initQueryParam.split('-')[1] === 'customerId'){
            //   for (let i in this.queryInfo) {
            //     if (this.queryInfo[i].field === 'prod_class_code'
            //       || this.queryInfo[i].field === 'prod_cname_other'
            //       || this.queryInfo[i].field === 'sg_sign'
            //       || this.queryInfo[i].field === 'order_len'
            //       || this.queryInfo[i].field === 'order_width'
            //       || this.queryInfo[i].field === 'order_thick'
            //       || this.queryInfo[i].field === 'prod_cname'
            //       || this.queryInfo[i].field === 'customer_id')
            //       this.queryInfo[i].disabled= true
            //   }
            //   this.queryInfo[0].hidden = '1'
            //   this.queryInfo[1].hidden = '1'
            // }
            // if(this.initQueryParam.split('-')[8] === 'salesOrderContract'){
            //   for (let j in this.queryInfo) {
            //     if (this.queryInfo[j].field === 'prod_class_code'
            //       || this.queryInfo[j].field === 'prod_cname_other'
            //       || this.queryInfo[j].field === 'sg_sign'
            //       || this.queryInfo[j].field === 'order_len'
            //       || this.queryInfo[j].field === 'order_width'
            //       || this.queryInfo[j].field === 'order_thick'
            //       || this.queryInfo[j].field === 'prod_cname'
            //       || this.queryInfo[j].field === 'customer_id')
            //       this.queryInfo[j].disabled = true
            //   }
            //   this.queryInfo[0].hidden = '1'
            //   this.queryInfo[1].hidden = '1'
            // }

          } else {
            this.$message.warning(res.message)
          }
        })
      },
      loadData(arg) {
        if (arg == 1) {
          this.table.pagination.current = 1
        }
        let params = this.getQueryParams();//查询条件
        this.table.loading = true
        let url = `${this.url.getData}${this.cgRpConfigId}`
        //缓存key
        let groupIdKey
        if (this.groupId) {
          groupIdKey = this.groupId + url + JSON.stringify(params)
        }
        httpGroupRequest(() => getAction(url, params), groupIdKey).then(res => {
          this.table.loading = false
          // console.log("data",res)
          let data = res.result
          if (data) {
            this.table.pagination.total = Number(data.total)
            this.table.dataSource = data.records
          } else {
            this.table.pagination.total = 0
            this.table.dataSource = []
          }
        })
      },
      getQueryParams() {
        let param = Object.assign({}, this.queryParam, this.sorter);
        param.pageNo = this.table.pagination.current;
        param.pageSize = this.table.pagination.pageSize;
        return filterObj(param);
      },
      handleChangeInTableSelect(selectedRowKeys, selectionRows) {
        //update-begin-author:taoyan date:2020902 for:【issue】开源online的几个问题 LOWCOD-844
        if(!selectedRowKeys || selectedRowKeys.length==0){
          this.table.selectionRows = []
        }else if(selectedRowKeys.length == selectionRows.length){
          this.table.selectionRows = selectionRows
        }else{
          //当两者长度不一的时候 需要判断
          let keys = this.table.selectedRowKeys
          let rows = this.table.selectionRows;
          //这个循环 添加新的记录
          for(let i=0;i<selectionRows.length;i++){
            let combineKey = this.combineRowKey(selectionRows[i])
            if(keys.indexOf(combineKey)<0){
              //如果 原来的key 不包含当前记录 push
              rows.push(selectionRows[i])
            }
          }
          //这个循环 移除取消选中的数据
          this.table.selectionRows = rows.filter(item=>{
            let combineKey = this.combineRowKey(item)
            return selectedRowKeys.indexOf(combineKey)>=0
          })
        }
        //update-end-author:taoyan date:2020902 for:【issue】开源online的几个问题 LOWCOD-844
        this.table.selectedRowKeys = selectedRowKeys
      },
      handleChangeInTable(pagination, filters, sorter) {
        //分页、排序、筛选变化时触发
        if (Object.keys(sorter).length > 0) {
          this.sorter.column = sorter.field
          this.sorter.order = 'ascend' == sorter.order ? 'asc' : 'desc'
        }
        this.table.pagination = pagination
        this.loadData()
      },
      handleCancel() {
        this.close()
      },
      handleSubmit() {
        // if(!this.multi){
        //   if(this.table.selectionRows && this.table.selectionRows.length>1){
        //     this.$message.warning("请选择一条记录")
        //     return false
        //   }
        // }
        // if(!this.table.selectionRows || this.table.selectionRows.length==0){
        //   this.$message.warning("请选择一条记录")
        //   return false
        // }
        this.$emit('ok', this.table.selectionRows);
        this.close()
      },
      close() {
        this.$emit('close');
        this.visible = false;
        this.onClearSelected()
      },
      show(){
        this.visible = true;
      },
      handleToggleSearch(){
        this.toggleSearchStatus = !this.toggleSearchStatus;
      },
      searchByquery(){
        this.loadData(1);
      },
      onlyReload(){
        this.loadData();
      },
      searchReset(){
        Object.keys(this.queryParam).forEach(key=>{
          this.queryParam[key]=""
        })
        this.loadData(1);
      },
      onClearSelected(){
        this.table.selectedRowKeys = []
        this.table.selectionRows = []
      },
      combineRowKey(record){
        let res = ''
         Object.keys(record).forEach(key=>{
           res+=record[key]
         })
        if(res.length>50){
          res = res.substring(0,150)
        }
        return res
      },

      clickThenCheck(record){
        return {
          on: {
            click: () => {
              let rowKey = this.combineRowKey(record)
              if(!this.table.selectedRowKeys || this.table.selectedRowKeys.length==0){
                let arr1=[],arr2=[]
                arr1.push(record)
                arr2.push(rowKey)
                this.table.selectedRowKeys=arr2
                this.table.selectionRows=arr1
              }else{
                if(this.table.selectedRowKeys.indexOf(rowKey)<0){
                  this.table.selectedRowKeys.push(rowKey)
                  this.table.selectionRows.push(record)
                }else{
                  let rowKey_index = this.table.selectedRowKeys.indexOf(rowKey)
                  this.table.selectedRowKeys.splice(rowKey_index,1);
                  this.table.selectionRows.splice(rowKey_index,1);
                }
              }
            }
          }
        }
      },
      //防止字典中有垃圾数据
      initDictOptionData(dictOptions){
        let obj = { }
        Object.keys(dictOptions).map(k=>{
          obj[k] = dictOptions[k].filter(item=>{
            return item!=null
          });
        });
        this.dictOptions  = obj
      }
    }
  }
</script>

<style scoped>

</style>