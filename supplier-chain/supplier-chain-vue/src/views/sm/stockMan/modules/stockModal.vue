<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :fullscreen=true
    switchFullscreen
    @ok="handleOk"
    :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
    @cancel="handleCancel"
    cancelText="关闭">
    <stock-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit"></stock-form>
  </j-modal>
</template>

<script>

  import StockForm from './stockForm'
  export default {
    name: 'stockModal',
    components: {
      StockForm
    },
    props: {
      selectionRecord: {
        type: Object,
        default: function() {
          return {}
        },
        required: false
      }
    },
    data () {
      return {
        title:'',
        width:1024,
        visible: false,
        disableSubmit: false
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