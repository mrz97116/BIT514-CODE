<template>
  <j-modal
    :title="title"
    width=1000px
    :visible="visible"
    switchFullscreen
    :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
    @cancel="handleCancel"
    cancelText="关闭">
    <finance-detail-form ref="realForm"  :disabled="disableSubmit" :customerId="customerId"></finance-detail-form>
  </j-modal>
</template>

<script>

  import FinanceDetailForm from './FinanceDetailForm'
  export default {
    name: 'FinanceDetailModal',
    components: {
      FinanceDetailForm
    },
    data () {
      return {
        customerId: '',
        title:'财务明细详情',
        width:800,
        visible: false,
        disableSubmit: false
      }
    },
    methods: {
      detailOpen(record){
        this.visible = true;
        this.customerId = record.customerId
      },
      // add () {
      //   this.visible=true
      //   this.$nextTick(()=>{
      //     this.$refs.realForm.add();
      //   })
      // },
      // edit (record) {
      //   this.visible=true
      //   this.$nextTick(()=>{
      //     this.$refs.realForm.edit(record);
      //   })
      // },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      // handleOk () {
      //   this.$refs.realForm.submitForm();
      // },
      // submitCallback(){
      //   this.$emit('ok');
      //   this.visible = false;
      // },
      handleCancel () {
        this.close()
      }
    }
  }
</script>