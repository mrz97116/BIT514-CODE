<template>
  <a-drawer :title="title" :width="width" placement="right" :closable="false" @close="close" :visible="visible">

    <a-tabs default-active-key="1" style="padding-bottom: 35px;">
      <a-tab-pane key="1" tab="电文BODY详情">
        <a-table ref="table" size="middle" bordered rowKey="key" :columns="columns" :dataSource="tableData" :pagination="false"
          class="j-table-force-nowrap">
        </a-table>
      </a-tab-pane>
      <a-tab-pane key="2" tab="电文原生JSON">
        <div v-html="msgInfo"></div>
      </a-tab-pane>
    </a-tabs>

    <div class="drawer-footer">
      <a-button type="primary" @click="handleCancel" style="margin-bottom: 0;">关闭</a-button>
    </div>
  </a-drawer>
</template>

<script>
  export default {
    name: 'MsgRecvInfoModal',
    data() {
      return {
        title: "操作",
        width: 400,
        visible: false,
        disableSubmit: false,
        msgInfo: '',
        body: {},
        tableData: [],
        columns: [{
            title: '字段名',
            align: "left",
            dataIndex: 'key'
          },
          {
            title: '字段值',
            align: "left",
            dataIndex: 'value'
          },
        ]
      }
    },
    methods: {
      edit(record) {
        this.visible = true
        this.msgInfo = this.jsonFormat(record.msgInfo);
        let body = JSON.parse(record.msgInfo).BODY[0];
        this.tableData = [];
        for (var key in body) {
          this.tableData.push({
            'key': key,
            'value': body[key]
          });
        }
      },
      close() {
        this.$emit('close');
        this.visible = false;
      },
      handleCancel() {
        this.close()
      },
      jsonFormat(format) {
        let msg = '',
          pos = 0,
          prevChar = '',
          outOfQuotes = true;
        for (let i = 0; i < format.length; i++) { //循环每一个字符
          let char = format.substring(i, i + 1); //获取到该字符
          if (char == '"' && prevChar != '\\') { //如果转移
            outOfQuotes = !outOfQuotes;
          } else if ((char == '}' || char == ']') && outOfQuotes) { //如果是关闭
            msg += "<br/>";
            pos--;
            for (let j = 0; j < pos; j++) msg += '    ';
          }
          msg += char;
          if ((char == ',' || char == '{' || char == '[') && outOfQuotes) {
            msg += "<br/>";
            if (char == '{' || char == '[') pos++;
            for (let k = 0; k < pos; k++) msg += '    ';
          }
          prevChar = char;
        }
        return msg;
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

  .drawer-footer {
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
