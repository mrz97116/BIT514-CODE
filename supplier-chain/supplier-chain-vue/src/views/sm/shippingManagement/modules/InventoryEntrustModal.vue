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
    <inventory-entrust-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit"></inventory-entrust-form>
  </j-modal>
</template>

<script>

  import InventoryEntrustForm from "./InventoryEntrustForm";
  export default {
    name: 'ShippingManagementModal',
    components: {
      InventoryEntrustForm
    },
    data () {
      return {
        title:'',
        width:800,
        visible: false,
        disableSubmit: false,
        selectedRows: []
      }
    },
    methods: {
      add () {
        this.visible=true
        this.$nextTick(()=>{
          this.$refs.realForm.edit();
          this.$refs.realForm.showShippingManagement(this.selectedRows)
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
        this.$emit("loadAgan")
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