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
    <mat-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit" :inputDisabled="inputDisabled"></mat-form>
  </j-modal>
</template>

<script>

  import MatForm from './MatForm'
  export default {
    name: 'MatModal',
    components: {
      MatForm
    },
    data () {
      return {
        title:'',
        width:1024,
        visible: false,
        disableSubmit: false,
        inputDisabled: false
      }
    },
    methods: {
      add () {
        this.visible=true
        this.$nextTick(()=>{
          this.$refs.realForm.add();
        })
      },
      edit (record) {
        this.inputDisabled = true
        this.visible=true
        this.$nextTick(()=>{
          this.$refs.realForm.edit(record);
        })
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        this.$refs.realForm.submitForm();
      },
      submitCallback(){
        this.$emit('ok');
        this.visible = false;
      },
      handleCancel () {
        this.close()
      }
    }
  }
</script>