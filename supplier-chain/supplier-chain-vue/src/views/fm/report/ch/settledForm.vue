<template>
  <div>
    <iframe :src="url" id="mobsf" style="width:80%;height:80%;"></iframe>
  </div>
</template>

<script>
  import common from '@views/base/common/common.js'

  export default {
    name: 'StackDetReport',
    data() {
      return {
        // url: common.localhost + '/webroot/decision/view/report?viewlet=%25E5%25BA%2593%25E5%25AD%2598%25E7%25AE%25A1%25E7%2590%2586.cpt&op=write'
        url: common.reportUrl +'/webroot/decision/view/report?viewlet=SMS_ZX%252FFM%252FsettledForm.cpt&ref_t=design&ref_c=41df67be-844f-473e-a10c-8ef37fad72ff'

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