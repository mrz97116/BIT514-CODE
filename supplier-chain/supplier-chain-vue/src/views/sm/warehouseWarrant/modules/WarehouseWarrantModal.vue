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
    <warehouse-warrant-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit"></warehouse-warrant-form>
  </j-modal>
</template>

<script>

import WarehouseWarrantForm from './WarehouseWarrantForm'
export default {
  name: 'WarehouseWarrantModal',
  components: {
    WarehouseWarrantForm
  },
  data () {
    return {
      title:'',
      width:1200,
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