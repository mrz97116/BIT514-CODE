<template>
  <div>
    <iframe :src="url" id="mobsf" style="width:80%;height:80%;"></iframe>
  </div>
</template>

<script>
  import common from '@views/base/common/common.js'
  import reportConfig from '@views/report_config.json'

  export default {
    name: 'salesAmount',
    data() {
      return {
//        url: common.reportUrl +'/webroot/decision/view/report?viewlet=SMS_ZX%252FSM%252FYRM%252Finventory.cpt&ref_t=design&ref_c=0412a57b-e6aa-4f6c-87f3-0ba051e6f645'
        url: common.reportUrl + '/webroot/decision/view/report?viewlet=SMS_ZX%252FSM%252FCH%252FsalesAmount.cpt&ref_t=design&ref_c=62a70ad0-331a-43ba-b1e4-1e3050b018ae'
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