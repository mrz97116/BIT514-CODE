<template>
  <div>
    <iframe :src="url" id="mobsf" style="width:80%;height:80%;"></iframe>
  </div>
</template>

<script>
  import common from '@views/base/common/common.js'

  export default {
    name: 'unSettle',
    data() {
      return {
        // url: common.localhost + '/webroot/decision/view/report?viewlet=%25E5%25BA%2593%25E5%25AD%2598%25E7%25AE%25A1%25E7%2590%2586.cpt&op=write'
        url: common.reportUrl + '/webroot/decision/view/report?viewlet=SMS_ZX%252FFM%252FCSzhj%25E5%25AE%25A2%25E6%2588%25B7%25E6%259C%25AA%25E7%25BB%2593%25E7%25AE%2597%25E8%25A1%25A8.cpt&ref_t=design&ref_c=be90b0e5-413b-4ce0-b65f-59f87f723a8d&tenant_id=' +this.$ls.get("TENANT_ID")

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