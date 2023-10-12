<template>
  <a-card :bordered="false">
    <div>
        <a-tabs default-active-key="1" @change="callback">
        <a-tab-pane key="1" tab="分货" name = "StockAllotList">
            <StockAllotList></StockAllotList>
        </a-tab-pane>
        <a-tab-pane key="2" tab="预开单" name = "PrepareOrderList" force-render>
            <PrepareOrderList></PrepareOrderList>
        </a-tab-pane>
        </a-tabs>
    </div>
  </a-card>
</template>

<script>
import StockAllotList from './StockAllotList';
import PrepareOrderList from './PrepareOrderList';

export default {
  components:{
      StockAllotList: () => import('./StockAllotList.vue'),
      PrepareOrderList: () => import('./PrepareOrderList.vue'),

  },
  data() {
    return {};
  },
  methods: {
    callback(key) {
      console.log(key);
    },
  },
};
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>