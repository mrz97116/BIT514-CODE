<template>
  <div>
    <iframe :src="url" id="mobsf" style="width:80%;height:80%;"></iframe>
  </div>
</template>

<script>
  import common from '@views/base/common/common.js'

  export default {
    name: 'predictDet',
    data() {
      return {
        url: common.reportUrl +'/webroot/decision/view/report?viewlet=SMS_ZX%252FFM%252FNN%252FpredictDet.cpt&ref_t=design&ref_c=9b947c74-2dcd-4caf-b6fa-33adfabfd1d0'

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