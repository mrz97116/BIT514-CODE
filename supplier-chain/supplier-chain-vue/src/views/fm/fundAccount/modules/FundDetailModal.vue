<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    switchFullscreen
    :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
    @cancel="handleCancel"
    cancelText="关闭">
    <fund-detail-form ref="modalFormDetail" :disabled="disableSubmit" :customerId="customerId"></fund-detail-form>
  </j-modal>
</template>

<script>

  import FundDetailForm from './FundDetailForm'
  export default {
    name: 'FundDetailModal',
    components: {
      FundDetailForm
    },
    data () {
      return {
        customerId: '',
        title:'资金账户流水明细',
        width:1200,
        visible: false,
        disableSubmit: true
      }
    },
    methods: {
      detailOpen(record){
        this.visible = true;
        this.customerId = record.customerId
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
/*
      handleOk () {
        // this.$refs.realForm.submitForm();
        alert("确定")
      },
*/
/*
      submitCallback(){
        this.$emit('ok');
        this.visible = false;
      },
*/
      handleCancel () {
        this.close()
      }
    }
  }
</script>