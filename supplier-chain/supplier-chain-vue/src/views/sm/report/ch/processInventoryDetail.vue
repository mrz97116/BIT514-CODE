<template>
  <div>
    <iframe :src="url" id="mobsf" style="width:80%;height:80%;"></iframe>
  </div>
</template>

<script>
  import common from '@views/base/common/common.js'

export default {
  name: 'processInventoryDetail',
  data() {
    return {
      // url: common.localhost + '/webroot/decision/view/report?viewlet=SMS_ZX%252FSM%252FCH%252FprocessInventoryDetail.cpt&ref_t=design&ref_c=d47493af-837f-4b7a-b656-ed4b65e796a6'
      url: common.reportUrl +'/webroot/decision/view/report?viewlet=SMS_ZX%252FSM%252FCH%252FprocessInventoryDetail.cpt&ref_t=design&ref_c=d47493af-837f-4b7a-b656-ed4b65e796a6'
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