<template>
  <j-modal
    :visible="writeOffVisible"
    title="冲红"
    width=70%
    @ok="handleOK1"
    @cancel="handleCancel"
  >
    <a-row :gutter="24">
      <a-col :span="30">
        <a-table
          rowKey="stackDetailId"
          size="middle"
          :scroll="{x:true}"
          bordered
          :columns="tableColumns"
          :dataSource="selectionRows"
          :pagination="tableIpagination"
          :loading="loading"
          class="j-matTable-force-nowrap">
        </a-table>
      </a-col>
    </a-row>
  </j-modal>
</template>

<script>
  import {filterDictTextByCache} from '@/components/dict/JDictSelectUtil'
  import { getAction } from '@api/manage'
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
    export default {
        name: "WriteOffModal",
      mixins: [JeecgListMixin],
      data () {
        return {
          writeOffVisible: false,
          loading: false,
          selectionRows:[],
          stackId: '',
          //专栏
          tableColumns:[
            {
              title:'品名中文别名',
              align:"center",
              dataIndex: 'prodCnameOther'
            },
            {
              title:'产品名称',
              align:"center",
              dataIndex: 'oldProdCname'
            },
            {
              title:'数量',
              align:"center",
              dataIndex: 'qty'
            },
            {
              title:'重量',
              align:"center",
              dataIndex: 'weight'
            },
            {
              title: '优惠后的单价',
              align:"center",
              dataIndex: 'discountPrice'
            },
            {
              title:'单价',
              align:"center",
              dataIndex: 'price'
            },
            {
              title:'结算金额',
              align:"center",
              dataIndex: 'discountTotalAmount'
            },
            {
              title:'总价',
              align:"center",
              dataIndex: 'totalAmount'
            },
            {
              title:'材料厚度',
              align:"center",
              dataIndex: 'matThick'
            },
            {
              title:'材料宽度',
              align:"center",
              dataIndex: 'matWidth'
            },
            {
              title:'材料长度',
              align:"center",
              dataIndex: 'matLen',
            },
            {
              title:'牌号',
              align:"center",
              dataIndex: 'sgSign'
            },
            {
              title:'计重方式',
              align:"center",
              dataIndex: 'wtMode',
              customRender: (text) =>{
                return filterDictTextByCache('wt_method_code',text);
              }
            },
            {
              title:'加价',
              align: 'center',
              dataIndex: 'addPrice'
            },
            {
              title:'材料号',
              align:"center",
              dataIndex: 'matNo'
            },
            {
              title:'产品大类',
              align:"center",
              dataIndex: 'prodClassCode',
              customRender: (text) =>{
                return filterDictTextByCache('prod_code',text);
              }
            }
          ],
          //页数
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
          url: {
            stackWriteOff: 'sm/stack/stackWriteOff',
            list: '/sm/stack/list',
          }


        }
      },

      methods:{
        handleOK1(){
          let stackId = this.stackId
          let that = this;
          this.$confirm({
            title: "确认冲红",
            content: "是否冲红该装车单？",
            onOk: function () {
              getAction(that.url.stackWriteOff,{id: stackId}).then((res)=>{
                if(res.code === 2000){
                  that.$message.success("操作成功")
                }else {
                  that.$message.warning(res.message)
                }

              })
            }

          })
          this.eliminate();
        },
        handleCancel(){
          this.eliminate();
        },
        eliminate(){
          this.writeOffVisible = false;
          this.loading = false;
          this.selectionRows = [];
          this.stackId = '';
        }
      }
    }

</script>

<style scoped>

</style>