<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :maskClosable="false"
    fullscreen
    @ok="handleOk"
    :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
    @cancel="handleCancel">
    <good-entrustment-letter-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit"></good-entrustment-letter-form>
  </j-modal>
</template>

<script>

  import GoodEntrustmentLetterForm from './GoodEntrustmentLetterForm'
  export default {
    name: 'GoodEntrustmentLetterModal',
    components: {
      GoodEntrustmentLetterForm
    },
    data () {
      return {
        title:'',
        width:1200,
        visible: false,
        disableSubmit: false,
        addShow: false
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
        this.$refs.realForm.handleOk();
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