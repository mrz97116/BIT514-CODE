<template>
  <div>
    <iframe :src="url" id="mobsf" style="width:80%;height:80%;"></iframe>
  </div>
</template>

<script>
  import common from '@views/base/common/common.js'

export default {
  name: 'salesOrderDetReport',
  data() {
    return {
      // url: common.localhost + '/webroot/decision/view/report?viewlet=%25E6%25AF%258F%25E6%2597%25A5%25E4%25BB%25B7%25E6%25A0%25BC%25E5%25A1%25AB%25E6%258A%25A5.cpt&op=write'
      url: common.reportUrl +'/webroot/decision/view/report?viewlet=SMS_ZX%252FSM%252FSH%252FsalesOrderDet.cpt&ref_t=design&ref_c=b8b0462d-3987-4b9a-b74a-f2f87c69eddf'
    }
  },
  mounted(){
    /**
     * iframe-宽高自适应显示
     */
    function changeMobsfIframe(){
      const mobsf = document.getElementById('mobsf');
      const deviceWidth = document.body.clientWidth;
      const deviceHeight = document.body.clientHeight;
      mobsf.style.width = (Number(deviceWidth)-240) + 'px'; //数字是页面布局宽度差值
      mobsf.style.height = (Number(deviceHeight)-64) + 'px'; //数字是页面布局高度差
    }

    changeMobsfIframe()

    window.onresize = function(){
      changeMobsfIframe()
    }
  }
}

</script>

<style scoped>

</style>