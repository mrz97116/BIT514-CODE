<template>
  <a-card :bordered="false">
    <j-modal
      :visible="visible"
      title="选款项"
      fullscreen
      @ok="handleOK"
      @cancel="handleCancel"
    >
      <template>
        <!--<a-select> </a-select>-->
        <!--<p>11111</p>-->
        <div>
          <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
            <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
            <a style="margin-left: 24px" @click="onClearSelected">清空</a>
          </div>
          <a-table
            ref="table"
            size="middle"
            bordered
            rowKey="id"
            class="j-table-force-nowrap"
            :scroll="{x:true}"
            :columns="columns"
            :dataSource="dataSource"
            :expandedRowKeys= "expandedRowKeys"
            :pagination="pagination"
            :customRow="clickThenSelect"
            :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
            @change="handleTableChange"
            @expand="handleExpand">
            <template slot="htmlSlot" slot-scope="text">
              <div v-html="text"></div>
            </template>
          </a-table>
        </div>


      </template>

    </j-modal>
  </a-card>

</template>

<script>

    import '@/assets/less/TableExpand.less'
    import {JeecgListMixin} from '@/mixins/JeecgListMixin'
    import { getAction,postAction } from '@/api/manage'
    export default {
        name: "Fund",
        mixins: [JeecgListMixin],
        created() {
          this.loadData();
        },

        data(){
          return {
            visible:false,
            selectedMainId:'',
            expandedRowKeys: [],
            pagination:{
              current: 1,
              pageSize: 5,
              pageSizeOptions: ['5', '10', '50'],
              showTotal: (total, range) => {
                return range[0] + "-" + range[1] + " 共" + total + "条"
              },
              showQuickJumper: true,
              showSizeChanger: true,
              total: 0
            },
            loading:false,
            url: {
              list: "/fm/fundPool/queryByCustomerId",
            },

            dataSource:[],
            columns: [
              {
                title:'来款用途',
                align:"center",
                dataIndex: 'purpose',
                width:150
              },
              {
                title:'来款金额',
                align:"center",
                dataIndex: 'incomingAmount',
                width:150
              },
              {
                title:'剩余可用金额',
                align:"center",
                dataIndex: 'availableAmount',
                width:150
              },
            ]
          }

        },

        methods:{
            hOpend() {
              this.loadData();
              this.visible = true;
            },

            handleCancel() {
              this.visible = false;
            },

            handleOK() {
              console.log("selectionRows",this.selectionRows);
              let list = [];
              for(let i=0;i<this.selectionRows.length;i++) {
                list.push(this.selectionRows[i].id);
              }
              console.log(list);
              getAction(this.url.list, params).then((res) => {
                if (res.success) {
                  this.dataSource = res.result.records;
                }
                if(res.code===510){
                  this.$message.warning(res.message)
                }
                this.loading = false;
              })
            },

            onSelectChange(selectedRowKeys, selectionRows) {
              this.selectedMainId=selectedRowKeys[0]
              this.selectedRowKeys = selectedRowKeys;
              this.selectionRows = selectionRows;
              console.log("selectionRows",this.selectionRows);
            },

            onClearSelected() {
              this.selectedRowKeys = [];
              this.selectionRows = [];
              this.selectedMainId=''
            },

            handleExpand(expanded, record){
              this.expandedRowKeys=[];
              this.innerData=[];
              if(expanded===true){
                this.loading = true;
                this.expandedRowKeys.push(record.id);
                getAction(this.url.customerListByMainId, {mainId: record.id}).then((res) => {
                  if (res.success) {
                    this.loading = false;
                    this.innerData = res.result.records;
                  }
                });
              }
            },

            clickThenSelect(record) {
              return {
                on: {
                  click: () => {
                    this.onSelectChange(record.id.split(","), [record]);
                  }
                }
              }
            },

            loadData() {
              this.onClearSelected();
              console.log("777777");
              this.loading = true;
              var param = {customerId:"1305790702324875265"};
              getAction(this.url.list, param).then((res) => {
                if (res.success) {
                  this.dataSource = res.result.records;
                }
                if(res.code===510){
                  this.$message.warning(res.message)
                }
                this.loading = false;
              })
            },

            handleTableChange() {
              getAction(this.url.list, params).then((res) => {
                if (res.success) {
                  this.dataSource = res.result.records;
                }
                if(res.code===510){
                  this.$message.warning(res.message)
                }
                this.loading = false;
              })
            }


        }
    }

</script>

<style scoped>

</style>