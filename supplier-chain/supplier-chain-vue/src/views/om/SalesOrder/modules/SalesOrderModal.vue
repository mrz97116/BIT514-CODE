<template>
  <j-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :maskClosable="false"
    fullscreen
    @ok="handleOk"
    :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
    @cancel="handleCancel">
    <sales-order-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit"
                      :editType="editType" :addShow="addShow" :newEditType="newEditType"/>
    <template v-if="action">
      <j-modal
        :title="`提交确认`"
        :visible="action"
        @ok="handleOk1"
        @cancel="handleCancel1"
        :width="200"
        :height = "600"
      ><span style="color:#ff0000">{{priceDesc}}</span>
      </j-modal>
    </template>

  </j-modal>
</template>

<script>

import SalesOrderForm from './SalesOrderForm'
import {JeecgListMixin} from '@/mixins/JeecgListMixin'
import { mapGetters } from "vuex";
import { getAction } from '@api/manage'

export default {
  name: 'SalesOrderModal',
  mixins: [JeecgListMixin],
  components: {
    SalesOrderForm
  },
  computed: {
    ...mapGetters(["userInfo"])
  },
  data() {
    return {
      addShow: true,
      editType: false,
      title: '',
      width: 800,
      visible: false,
      disableSubmit: false,
      newEditType: false,
      queryWhichContractControl: '/cm/customerConfiguration/queryWhichContractControl',
      priceDesc: '',
      url:{list: '/11'},
      action: false,
      queryIfStackContractControl: '/cm/customerConfiguration/queryIfStackContractControl',
      queryParamsFromBackEnd: '/om/salesOrder/queryParamsFromBackEnd'
    }
  },
  methods: {
    add() {
      this.visible = true;
      this.newEditType = false
      this.editType = false
      this.addShow = true
      this.$nextTick(() => {
        this.$refs.realForm.add();
        for (var item of this.$refs.realForm.$refs.salesOrderDet.columns) {
          if (item.key === 'matTheoryWt' || item.key === 'totalPrice' || item.key === 'custMatSpecs') {
            continue
          }
          item.disabled = false
        }
        getAction(this.queryParamsFromBackEnd).then((res) => {
          if (res.success) {
            for (var key in res.result) {
              if (key === 'currentDate') {
                this.$refs.realForm.form.setFieldsValue({
                  orderTime: res.result[key]
                })
              }
            }
          }
        })
      })
    },
    edit(record) {
      this.visible = true
      this.newEditType = true
      this.editType = true
      this.addShow = false
      this.$nextTick(() => {
        this.$refs.realForm.selectChange(record.customerId)
        this.$refs.realForm.edit(record);
        this.$refs.realForm.checkCarNo(record);
        for (var item of this.$refs.realForm.$refs.salesOrderDet.columns) {
          if (item.key === 'addPrice'
            || item.key === 'deliverExpense'
            || item.key === 'basicPrice'
            || item.key === 'qty'
            || item.key === 'weight'
            || item.key === 'price'
            || item.key === 'fleetName'
            || item.key === 'fleetDeliverExpense') {
            continue
          }
          item.disabled = true
        }
        if (this.tenantId === '2') {
          this.newEditType = false
        }
      })
      getAction(this.queryWhichContractControl, {tenantId: this.tenantId}).then((res) => {
        if (res.success && res.result[1] === '1') {
          this.$refs.realForm.$refs.salesOrderDet.columns.forEach(item => {
            if (item.key === 'contractListNo') {
              item.disabled = false
            }
          })
        }
      })
    },
    close() {
      this.$emit('close');
      this.visible = false;
    },
    handleOk() {
      let pricedesc = this.$refs.realForm.handleCheckPrice();
      if(this.userInfo.username =="gdjiangyue" && pricedesc !=='') {
        this.action = true;
        this.priceDesc = pricedesc;
      } else {
        this.$refs.realForm.handleOk();
      }
    },
    handleOk1(){
      this.action = false;
      this.$refs.realForm.handleOk();
    },
    copyAdd(record) {
      record.orderTime = ''
      this.visible = true
      this.$nextTick(() => {
        this.$refs.realForm.copyAdd(record);
      })
    },
    prepareOrder(record) {
      this.visible = true;
      record['prepareOrderId'] = record.id
      getAction(this.queryIfStackContractControl, {id: record.id}).then((res) => {
        this.$nextTick(() => {

          this.$refs.realForm.copyAdd(res.result);
        })
      })
    },
    submitCallback() {
      this.$emit('ok');
      this.visible = false;
    },
    handleCancel() {
      this.editType = false;
      this.action = false;
      this.priceDesc = '';
      this.close();
      this.$refs.realForm.carNo = '';
    },
    handleCancel1(){
      this.action = false;
      this.priceDesc = '';
    }
  }
}
</script>

<style scoped>
</style>