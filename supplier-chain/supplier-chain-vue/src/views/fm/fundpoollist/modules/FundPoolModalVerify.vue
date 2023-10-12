<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    switchFullscreen
    @ok="handleOk"
    :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
    @cancel="handleCancel"
    cancelText="关闭">
    <fund-pool-form-verify ref="realForm" @ok="submitCallback" :disabled="disableSubmit"  :creditArrear="creditArrear"></fund-pool-form-verify>
  </j-modal>
</template>

<script>

  import FundPoolFormVerify from './FundPoolFormVerify'

  export default {
    name: 'FundPoolModalVerify',
    components: {
      FundPoolFormVerify
    },
    data() {
      return {
        creditArrear: '',
        title: '',
        width: 896,
        visible: false,
        disableSubmit: false,

      }
    },
    methods: {
      add() {
        this.visible = true
        this.$nextTick(() => {
          this.$refs.realForm.add();
        })
      },
      edit(record) {
        this.visible = true
        this.$nextTick(() => {
          this.$refs.realForm.edit(record);
        })
      },
      close() {
        this.$emit('close');
        this.visible = false;
      },
      handleOk() {
        this.$refs.realForm.submitForm();
      },
      submitCallback() {
        this.$emit('ok');
        this.visible = false;
      },
      handleCancel() {
        this.close()
      }
    }
  }
</script>