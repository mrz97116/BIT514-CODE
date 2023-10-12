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
    <fund-pool-form ref="realForm" @ok="submitCallback" :inputDisabled="inputDisabled" :disabled="disableSubmit"></fund-pool-form>
  </j-modal>
</template>

<script>

  import FundPoolForm from './FundPoolForm'

  export default {
    name: 'FundPoolModal',
    components: {
      FundPoolForm
    },
    data() {
      return {
        title: '',
        width: 896,
        visible: false,
        disableSubmit: false,
        inputDisabled: false
      }
    },
    methods: {
      add() {
        this.inputDisabled = false
        this.visible = true
        this.$nextTick(() => {
          this.$refs.realForm.add();
        })
      },
      edit(record) {
        this.inputDisabled = true
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