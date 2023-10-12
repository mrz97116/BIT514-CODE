<template>
  <div class="page-header-index-wide">

    <a-row :gutter="24">
      <a-col :sm="24" :md="12" :xl="8" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="本月销售额" :total="this.salesShow.salesThis">
          <a-tooltip title="指标说明" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <trend style="margin-right: 16px;" :flag="this.monthOnMonth"><!-- up  :flag="this.monthOnMonth" -->
              <span slot="term">月同比:</span>
              {{salesShow.salesMonththan}}%
            </trend>
            <trend :flag="this.yearOnYear"><!--down :flag="this.yearOnYear" -->
              <span slot="term">日同比:</span>
              {{salesShow.salesDaythan}}%
            </trend>
          </div>
          <template slot="footer">日均销售额: <span>{{salesShow.salesDayAvg}}</span></template>
        </chart-card>
      </a-col>
      <a-col :sm="32" :md="12" :xl="8" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="本月提单量" :total="this.countBill.monthCount">
          <a-tooltip title="指标说明" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <mini-area :dataSource="miniAreadataSource"  />
          </div>
          <template slot="footer">日均提单量<span> {{countBill.avgCount}}</span></template>
        </chart-card>
      </a-col>
      <a-col :sm="32" :md="12" :xl="8" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="本月来款金额" :total="this.monthPayment.monthFundAccount">
          <a-tooltip title="指标说明" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
<!--                        <mini-progress color="rgb(19, 194, 194)" :target="80" :percentage="78" :height="8" />-->
            <mini-area :dataSource="miniAreaFundPool"/>
          </div>
          <!--          <template slot="footer">-->
          <!--            <trend flag="down" style="margin-right: 16px;">-->
          <!--              <span slot="term">同周比</span>-->
          <!--              12%-->
          <!--            </trend>-->
          <!--            <trend flag="up">-->
          <!--              <span slot="term">日环比</span>-->
          <!--              80%-->
          <!--            </trend>-->
          <!--          </template>-->
          <template slot="footer">日均来款金额<span> {{monthPayment.avgOfMonthPayment}}</span></template>
        </chart-card>
      </a-col>
<!--
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="订单量" :total="8846 | NumberFormat">
          <a-tooltip title="指标说明" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <mini-area />
          </div>
          <template slot="footer">日订单量<span> {{ '34' | NumberFormat }}</span></template>
        </chart-card>
      </a-col>
      <a-col  :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading"  title="支付笔数" :total="6560 | NumberFormat">
          <a-tooltip title="指标说明" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <mini-bar :height="40" />
          </div>
          <template slot="footer">转化率 <span>60%</span></template>
        </chart-card>
      </a-col>
-->
<!--
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
               <chart-card :loading="loading" title="订单量" :total="8846 | NumberFormat">
                 <a-tooltip title="指标说明" slot="action">
                   <a-icon type="info-circle-o" />
                 </a-tooltip>
                 <div>
                   <mini-area />
                 </div>
                 <template slot="footer">日订单量<span> {{ '34' | NumberFormat }}</span></template>
               </chart-card>
      </a-col>
-->
<!--      <a-col  :sm="24" :md="12" :xl="8" :style="{ marginBottom: '24px' }">-->
<!--        <chart-card :loading="loading"  title="订单增长数" :total="88 | NumberFormat">-->
<!--          <a-tooltip title="指标说明" slot="action">-->
<!--            <a-icon type="info-circle-o" />-->
<!--          </a-tooltip>-->
<!--          <div>-->
<!--              <mini-progress color="rgb(19, 194, 194)" :target="80" :percentage="88" :height="8" />-->
<!--          </div>-->
<!--                <template slot="footer">-->
<!--                  <trend flag="down" style="margin-right: 16px;">-->
<!--                    <span slot="term">同周比</span>-->
<!--                    2%-->
<!--                  </trend>-->
<!--                  <trend flag="up">-->
<!--                    <span slot="term">同月比</span>-->
<!--                    6%-->
<!--                  </trend>-->
<!--                </template>-->
<!--        </chart-card>-->
<!--      </a-col>-->
    </a-row>

    <a-card :loading="loading" :bordered="false" :body-style="{padding: '0'}" :style="{ marginBottom: '24px' }">
      <div class="salesCard" style="height: 450px">
        <a-tabs default-active-key="1" size="large" :tab-bar-style="{marginBottom: '24px', paddingLeft: '16px'}">
<!--
          <div class="extra-wrapper" slot="tabBarExtraContent">
            <div class="extra-item">
              <a>今日</a>
              <a>本周</a>
              <a>本月</a>
              <a>本年</a>
            </div>
            <a-range-picker :style="{width: '256px'}" />
          </div>
-->
          <a-tab-pane loading="true" tab="销售额" key="1">
            <a-row>
              <a-col :xl="16" :lg="12" :md="12" :sm="24" :xs="24">
                <bar title="销售额排行" :dataSource="barData"/>
              </a-col>
              <a-col :xl="8" :lg="12" :md="12" :sm="24" :xs="24">
                <rank-list title="本月销售额排行榜" :list="rankList"/>
              </a-col>
            </a-row>
          </a-tab-pane>
          <a-tab-pane tab="销售趋势" key="2">
            <a-row>
              <a-col :xl="16" :lg="12" :md="12" :sm="24" :xs="24">
                <bar title="销售额趋势" :dataSource="barData"/>
              </a-col>
              <a-col :xl="8" :lg="12" :md="12" :sm="24" :xs="24">
                <rank-list title="本月销售额排行榜" :list="rankList"/>
              </a-col>
            </a-row>
          </a-tab-pane>

        </a-tabs>
      </div>
    </a-card>


    <a-card :loading="loading" :bordered="false" :body-style="{padding: '0'}" :style="{ marginBottom: '24px' }">
      <div class="salesCard">
        <a-tabs default-active-key="1" size="large" :tab-bar-style="{marginBottom: '24px', paddingLeft: '16px'}">
<!--          <a-tab-pane tab="最近一年销售统计" key="1">-->
<!--            <a-row>-->
<!--              <a-col :span="18">-->
<!--                  <pie title="饼图" :height="300"/>-->
<!--              </a-col>-->
<!--              <a-col :span="6">-->
<!--                <a-card :loading="loading" :bordered="false" title="销售明细" >-->
<!--                  <a-col  :style="{ marginBottom: '24px' }">-->
<!--                    <chart-card :loading="loading"  title="本月来款笔数" :total="this.fundPoolVO.payment">-->
<!--                      <a-tooltip title="指标说明" slot="action">-->
<!--                        <a-icon type="info-circle-o" />-->
<!--                      </a-tooltip>-->
<!--                      <div>-->
<!--                        <mini-bar :height="40" :list="sevenDayFundPool"/>-->
<!--                      </div>-->
<!--                      <template slot="footer">日均来款 <span>{{fundPoolVO.averageDailyPayment}}</span></template>-->
<!--                    </chart-card>-->
<!--                  </a-col>-->

<!--                </a-card>-->
<!--              </a-col>-->
<!--            </a-row>-->
<!--          </a-tab-pane>-->

          <a-tab-pane tab="最近一周访问量统计" key="1">

            <a-row>
              <a-col :span="24">
                <a-row>
                  <a-col :span="6">
                    <head-info title="今日IP" :content="loginfo.todayIp"></head-info>
                  </a-col>
                  <a-col :span="2">
                    <a-spin class='circle-cust'>
                      <a-icon slot="indicator" type="environment" style="font-size: 24px;margin-left:30px;margin-top:5px"  />
                    </a-spin>
                  </a-col>
                  <a-col :span="6">
                    <head-info title="今日访问" :content="loginfo.todayVisitCount"></head-info>
                  </a-col>
                  <a-col :span="2">
                    <a-spin class='circle-cust'>
                      <a-icon slot="indicator" type="team" style="font-size: 24px;margin-left:30px;margin-top:5px"  />
                    </a-spin>
                  </a-col>
                  <a-col :span="6">
                    <head-info title="总访问量" :content="loginfo.totalVisitCount"></head-info>
                  </a-col>
                  <a-col :span="2">
                    <a-spin class='circle-cust'>
                      <a-icon slot="indicator" type="rise" style="font-size: 24px;margin-left:30px;margin-top:5px"  />
                    </a-spin>
                  </a-col>
                </a-row>
                <line-chart-multid :fields="visitFields" :dataSource="visitInfo"></line-chart-multid>
              </a-col>
<!--              <a-col :span="6">-->
<!--                <a-card :loading="loading" :bordered="false" title="销售明细" >-->
<!--                  <a-col  :style="{ marginBottom: '24px' }">-->
<!--                    <chart-card :loading="loading"  title="本月来款笔数" :total="this.fundPoolVO.payment">-->
<!--                      <a-tooltip title="指标说明" slot="action">-->
<!--                        <a-icon type="info-circle-o" />-->
<!--                      </a-tooltip>-->
<!--                      <div>-->
<!--                        <mini-area :sourceData="this.miniBarFundPool" />-->
<!--                      </div>-->
<!--                      <template slot="footer">日均来款 <span>{{fundPoolVO.averageDailyPayment}}</span></template>-->
<!--                    </chart-card>-->
<!--                  </a-col>-->

<!--                </a-card>-->
<!--              </a-col>-->
            </a-row>

          </a-tab-pane>

        </a-tabs>
      </div>
    </a-card>


  </div>
  <!--
      <a-row>
        <a-col :span="24">
          <a-card :loading="loading" :bordered="false" title="最近一周访问量统计" :style="{ marginTop: '24px' }">
            <a-row>
              <a-col :span="6">
                <head-info title="今日IP" :content="loginfo.todayIp"></head-info>
              </a-col>
              <a-col :span="2">
                <a-spin class='circle-cust'>
                  <a-icon slot="indicator" type="environment" style="font-size: 24px"  />
                </a-spin>
              </a-col>
              <a-col :span="6">
                <head-info title="今日访问" :content="loginfo.todayVisitCount"></head-info>
              </a-col>
              <a-col :span="2">
                <a-spin class='circle-cust'>
                  <a-icon slot="indicator" type="team" style="font-size: 24px"  />
                </a-spin>
              </a-col>
              <a-col :span="6">
                <head-info title="总访问量" :content="loginfo.totalVisitCount"></head-info>
              </a-col>
              <a-col :span="2">
                <a-spin class='circle-cust'>
                  <a-icon slot="indicator" type="rise" style="font-size: 24px"  />
                </a-spin>
              </a-col>
            </a-row>
            <line-chart-multid :fields="visitFields" :dataSource="visitInfo"></line-chart-multid>
          </a-card>
        </a-col>
      </a-row>
-->

</template>

<script>
  import ChartCard from '@/components/ChartCard'
  import ACol from "ant-design-vue/es/grid/Col"
  import ATooltip from "ant-design-vue/es/tooltip/Tooltip"
  import MiniArea from '@/components/chart/MiniArea'
  import MiniBar from '@/components/chart/MiniBar'
  import MiniProgress from '@/components/chart/MiniProgress'
  import RankList from '@/components/chart/RankList'
  import Bar from '@/components/chart/Bar'
  import LineChartMultid from '@/components/chart/LineChartMultid'
  import HeadInfo from '@/components/tools/HeadInfo.vue'

  import Trend from '@/components/Trend'
  import { getLoginfo,getVisitInfo } from '@/api/api'
  import Pie from '@/components/chart/Pie'
  import { FormTypes, getRefPromise } from '@/utils/JEditableTableUtil'
  import { JEditableTableMixin } from '@/mixins/JEditableTableMixin'
  import { getAction, httpAction,postAction } from '@/api/manage'
  import moment from "dayjs";
  var rankList = []

  var barData = []

  var miniAreadataSourceData = []

  var sevenDayFundPoolAccountData = []

  var sevenDayFundPoolData = []


  // var sevenDayFundPool = []
  // for (let i = 0; i < 12; i += 1) {
  //   barData.push({
  //     x: `${i + 1}月`,
  //     y: Math.floor(Math.random() * 1000) + 200
  //   })
  // }



  export default {
    name: "IndexChart",
    mixins: [JEditableTableMixin],
    components: {
      ATooltip,
      ACol,
      ChartCard,
      MiniArea,
      MiniBar,
      MiniProgress,
      RankList,
      Bar,
      Trend,
      LineChartMultid,
      HeadInfo,
      Pie
    },
    data() {
      return {
        salesRanking:[],
        miniAreadataSource:[],
        sevenDaySalesStatistics:[],
        sevenDaySalesOrder:[],
        miniAreaFundPool:[],
        sevenDayFundPoolAccount:[],
        miniBarFundPool:[],
        sevenDayFundPool:[],
        fundPoolVO: {},
        salesShow: {},
        countBill: {},
        monthPayment: {},
        monthOnMonth: '',
        yearOnYear: '',
        loading: true,
        center: null,
        rankList,
        barData,
        loginfo:{},
        visitFields:['ip','visit'],
        visitInfo:[],
        indicator: <a-icon type="loading" style="font-size: 24px" spin />,
        url: {
          list: '/home/home/selectHome',
        }
      }
    },
    created() {
      setTimeout(() => {
        this.loading = !this.loading
      }, 1000)
      this.initLogInfo();
    },
    mounted() {
      this.query()
    },
    methods: {
      initLogInfo () {
        getLoginfo(null).then((res)=>{
          if(res.success){
            Object.keys(res.result).forEach(key=>{
              res.result[key] =res.result[key]+""
            })
            this.loginfo = res.result;
          }
        })
        getVisitInfo().then(res=>{
          if(res.success){
             this.visitInfo = res.result;
           }
         })
      },
      query(){
        var that = this
        getAction(this.url.list).then((res) => {
          console.log("---首页数据:",res);
          if (res.message) {
            that.salesRanking =res.result.salesRanking
            that.sevenDaySalesStatistics =res.result.sevenDaySalesStatistics
            that.sevenDayFundPool =res.result.sevenDayFundPool
            that.fundPoolVO =res.result.fundPoolVO
            that.salesShow =res.result.salesShow
            that.countBill =res.result.countBill
            that.monthPayment =res.result.monthPayment
            that.sevenDaySalesOrder =res.result.sevenDaySalesOrder
            that.sevenDayFundPoolAccount =res.result.sevenDayFundPoolAccount
            // that.form.setFieldsValue({salesRanking:res.result.salesRanking,sevenDaySalesStatistics:res.result.sevenDaySalesStatistics})
            // rankList = []
            //销售排行
            for (let i = 0; i < 8; i++) {
              rankList.push({
                name: that.salesRanking[i],
              })
            }
            //最近七天销售额
            for (let i = 0; i < that.sevenDaySalesStatistics.length; i++) {
              barData.push({
                x: that.sevenDaySalesStatistics[i].dateString,
                // y: Math.floor(Math.random() * 1000) + 200
                y: Math.floor(that.sevenDaySalesStatistics[i].account),
              })
            }
            //最近七天提单量
            for (let i = 0; i < that.sevenDaySalesOrder.length; i++) {
              miniAreadataSourceData.push({
                x: that.sevenDaySalesOrder[i].dateString,
                y: Math.floor(that.sevenDaySalesOrder[i].numberOfPayments),
              })
            }
            that.miniAreadataSource = miniAreadataSourceData
            console.log("---提单量图:",that.miniAreadataSource)
            //最近七天来款金额
            for (let i = 0; i < that.sevenDayFundPoolAccount.length; i++){
              sevenDayFundPoolAccountData.push({
                x: that.sevenDayFundPoolAccount[i].dateString,
                y: Math.floor(that.sevenDayFundPoolAccount[i].numberOfPayments),
              })
            }
            that.miniAreaFundPool = sevenDayFundPoolAccountData
            console.log("---来款金额图:",that.miniAreaFundPool)
            //最近七天来款笔数
            for (let i = 0; i < that.sevenDayFundPool.length; i++){
              sevenDayFundPoolData.push({
                x: that.sevenDayFundPool[i].dateString,
                y: Math.floor(that.sevenDayFundPool[i].numberOfPayments),
              })
            }
            that.miniBarFundPool = sevenDayFundPoolData
            console.log("---来款笔数图:",that.miniBarFundPool)

            // monthOnMonth
            if(that.salesShow.salesMonththan < 100){
              that.monthOnMonth = 'down'
            }else {
              that.monthOnMonth = 'up'
            }
            // yearOnYear
            if(that.salesShow.salesDaythan < 100){
              that.yearOnYear = 'down'
            }else {
              that.yearOnYear = 'up'
            }
            rankList = []
          }
          else {
            that.$message.warning(res.message)
          }
        })
      }
    }
  }
</script>

<style lang="less" scoped>
  .circle-cust{
    position: relative;
    top: 28px;
    left: -100%;
  }
  .extra-wrapper {
    line-height: 55px;
    padding-right: 24px;

    .extra-item {
      display: inline-block;
      margin-right: 24px;

      a {
        margin-left: 24px;
      }
    }
  }

  /* 首页访问量统计 */
  .head-info {
    position: relative;
    text-align: left;
    padding: 0 32px 0 0;
    min-width: 125px;

    &.center {
      text-align: center;
      padding: 0 32px;
    }

    span {
      color: rgba(0, 0, 0, .45);
      display: inline-block;
      font-size: .95rem;
      line-height: 42px;
      margin-bottom: 4px;
    }
    p {
      line-height: 42px;
      margin: 0;
      a {
        font-weight: 600;
        font-size: 1rem;
      }
    }
  }
</style>