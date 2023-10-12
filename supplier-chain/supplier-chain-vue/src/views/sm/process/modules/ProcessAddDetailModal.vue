<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    @ok="handleOk"
    :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
    @cancel="handleCancel"
    cancelText="关闭">
    <processAddDetailForm ref="realForm" @ok="submitCallback" :disabled="disableSubmit"
                  :selectionRecord="selectionRecord"></processAddDetailForm>
  </j-modal>
</template>

<script>

  import processAddDetailForm from './ProcessAddDetailForm'

  export default {
    name: 'ProcessAddDetailModal',
    components: {
      processAddDetailForm,
    },
    props: {
      selectionRecord: {
        type: Array,
        default: function() {
          return []
        },
        required: false
      }
    },
    data () {
      return {
        title:'',
        width:900,
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
        this.$refs.realForm.cancel();
      },
      handleOk () {
        this.$refs.realForm.submit();
      },
      submitCallback(){
        this.$emit('ok');
        this.visible = false;
      },
      handleCancel () {
        this.close()
      },
    }
  }
</script>