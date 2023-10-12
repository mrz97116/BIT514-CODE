<template>
  <a-drawer
    :title="title"
    :width="width"
    placement="right"
    :closable="false"
    @close="close"
    :visible="visible">
    
    <div>
      {{errorMsg}}
    </div>
    
    <div class="drawer-footer">
      <a-button type="primary" @click="handleCancel" style="margin-bottom: 0;">关闭</a-button>
    </div>
  </a-drawer>
</template>

<script>

  export default {
    name: 'MsgRecvInfoModal',
    data () {
      return {
        title:"操作",
        width:400,
        visible: false,
        errorMsg:"",
      }
    },
    methods: {
      edit (record) {
        this.visible=true
        this.errorMsg = record.message;
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleCancel () {
        this.close()
      }
    }
  }
</script>

<style lang="less" scoped>
/** Button按钮间距 */
  .ant-btn {
    margin-left: 30px;
    margin-bottom: 30px;
    float: right;
  }
  .drawer-footer{
    position: absolute;
    bottom: 0px;
    width: 100%;
    border-top: 1px solid #e8e8e8;
    padding: 10px 16px;
    text-align: right;
    left: 0;
    background: #fff;
    border-radius: 0 0 2px 2px;
  }
</style>