<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    <div>
      重量
      <a-input-number style="width: 200px" placeholder="请输入重量" v-model="weight">
      </a-input-number>
      金额
      <a-input-number style="width: 200px" placeholder="请输入金额" v-model="amount">
      </a-input-number>
    </div>
    <a-table
      ref="table"
      size="middle"
      bordered
      rowKey="id"
      :columns="columns"
      :dataSource="dataSource"
      :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange, type:'checkbox'}"
      class="j-table-force-nowrap">

    </a-table>


  </j-modal>
</template>


<script>
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { getAction, postAction } from '@/api/manage'

  export default {
    components: { JeecgListMixin },
    watch: {
      weight:{
        immediate: false,
        handler(val) {
          // debugger;
            this.amount = val * this.price;
        }
      }
    },
    data() {
      return {
        //合同主表的合同编号
        contractNo: '',
        //合同明细的id
        superId: '',
        selectedRowKeys: [],
        amount: 0,
        weight: 0,
        price: 0,
        dataSource: [],
        title: '控款',
        width: 800,
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },

        /* 分页参数 */
        ipagination: {
          current: 1,
          pageSize: 5,
          pageSizeOptions: ['5', '10', '50'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },

        confirmLoading: false,
        url: {
          list: '/fm/fundPool/queryByCustomerId',
          // deductByContract: '/om/contract/deductByContract',
          deductByContract:'/om/salesOrder/addSaleOrder'
        },

        columns: [
          {
            title: '来款用途',
            align: 'center',
            dataIndex: 'purpose'
          },
          {
            title: '来款金额',
            align: 'center',
            dataIndex: 'incomingAmount'
          },
          {
            title: '剩余可用金额',
            align: 'center',
            dataIndex: 'availableAmount'
          }]

      }
    },

    methods: {
      /**
       * @param superId: 合同明细id
       * @param customerId: 合同主表客户id
       * @param contractNo: contractNo:合同编号
       */
      loadData(superId, customerId, contractNo, weight, price) {
        // debugger;
        this.weight = weight;
        this.price = price;
        this.amount = price * weight;
        this.contractNo = contractNo
        this.superId = superId
        this.visible = true
        this.loading = true
        let params = {}
        params.customerId = customerId;
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records
            this.ipagination.total = res.result.total
          }
          if (res.code === 510) {
            this.$message.warning(res.message)
          }
          this.loading = false
        })
      },

      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedRowKeys = selectedRowKeys
        this.selectionRows = selectionRows
        console.log('选中的mainId:  ' + this.selectedRowKeys)
      },
      handleOk() {

        console.log(this.parentId + '================>')



        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('请勾选内容!')
          return
        }

        var totalPrice = 0;
        for (var i = 0 ; i < this.selectionRows.length; i++) {
          totalPrice += this.selectionRows[i].availableAmount;
        }

        if(this.amount > totalPrice) {
          this.$message.warning("勾选的金额不足")
          return
        }
        //把选中的记录传过去后台处理
        console.log('请求地址:' + this.url.deductByContract + '?id=' + this.superId)

        console.log('合同明细id' + this.superId)

        postAction(this.url.deductByContract + '?id=' + this.superId +'&weight='+ this.weight, this.selectionRows).then((res) => {
          if (res.success) {
            this.$message.success('生成提单成功！')
          } else {
            this.$message.warning('错误')
          }
          if (res.code === 510) {
            this.$message.warning(res.message)
          }
          this.loading = false
        })
        this.handleCancel();
      },
      handleCancel() {
        this.visible = false;
        this.selectionRows = [];
        this.selectedRowKeys = [];
        this.weight = 0;
        this.amount = 0;
      }
    }

  }
</script>

<style>

</style>
