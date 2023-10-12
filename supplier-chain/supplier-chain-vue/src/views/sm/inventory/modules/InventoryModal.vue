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
    <inventory-form ref="realForm" @ok="submitCallback" :inputDisabled="inputDisabled" :disabled="disableSubmit"></inventory-form>
  </j-modal>
</template>

<script>

  import InventoryForm from './InventoryForm'
  export default {
    name: 'InventoryModal',
    components: {
      InventoryForm
    },
    data () {
      return {
        inputDisabled: false,
        title:'',
        width:896,
        visible: false,
        disableSubmit: false
      }
    },
    methods: {
      add () {
        this.inputDisabled = false
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