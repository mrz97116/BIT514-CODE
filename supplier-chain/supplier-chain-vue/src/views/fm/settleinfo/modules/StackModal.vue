<template>
  <j-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :maskClosable="false"
    :fullscreen=true
    switchFullscreen
    @ok="handleOk"
    :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
    @cancel="handleCancel">
    <stack-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit" :editType="editType"/>
  </j-modal>
</template>

<script>

  import StackForm from './StackForm'
  import { FormTypes } from '../../../../utils/JEditableTableUtil'

  export default {
    name: 'StackModal',
    components: {
      StackForm
    },
    props:{
      editType: {
        type: Boolean,
        default: false,
      }
    },
    data() {
      return {
        title:'',
        width:800,
        visible: false,
        disableSubmit: false
      }
    },
    methods:{
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
          let tenantId = this.$ls.get('TENANT_ID')
          debugger
          if (tenantId + '' !== '2' && tenantId + '' !== '1' ) {
            for (var item of this.$refs.realForm.$refs.stackDet.columns) {
              if (item.key === 'addPrice'
                || item.key === 'deliverExpense'
                || item.key === 'deliverTotalPrice'
                || item.key === 'basicPrice'
                || item.key === 'fleetDeliverExpense') {
                item.type = FormTypes.hidden
              }
            }
          } else {
            for (var item of this.$refs.realForm.$refs.stackDet.columns) {
              if (item.key === 'price'){
                item.disabled = true
              }
            }
          }
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

<style scoped>
</style>