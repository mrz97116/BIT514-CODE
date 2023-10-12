<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    fullscreen
    @ok="handleOk"
    :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
    @cancel="handleCancel"
    cancelText="关闭">
    <settle-stack-info-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit"></settle-stack-info-form>
  </j-modal>


<!--  </j-modal>-->
</template>

<script>

  import SettleStackInfoForm from './SettleStackInfoForm'
  export default {
    name: 'SettleStackInfoModal',
    components: {
      SettleStackInfoForm
    },
    data () {
      return {
        title:'',
        width:896,
        visible: false,
        disableSubmit: false
      }
    },
    methods: {
      hOpen(){
        this.visible = true;
        // this.$refs.realForm.add();
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      submitCallback(){
        this.$emit('ok');
        this.visible = false;
      },
      handleOk () {
        this.$refs.realForm.submitForm();
      },
      handleCancel () {
        this.close()
      }
    }
  }
</script>