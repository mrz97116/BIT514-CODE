<template>
  <div class="index-container-ty">
    <a-spin :spinning="loading">
      <a-row type="flex" justify="start" :gutter="3">
        <a-col :sm="24" :lg="12">
          <a-card>
            <div slot="title" class="index-md-title">
              <img src="../../assets/daiban.png"/>
              我的待办【{{ dataSource1.length }}】
            </div>
            <div slot="extra">
              <a v-if="dataSource1 && dataSource1.length>0" slot="footer" @click="goPage">更多 <a-icon type="double-right" /></a>
            </div>
            <a-table
              :class="'my-index-table tytable1'"
              ref="table1"
              size="small"
              rowKey="id"
              :columns="columns"
              :dataSource="dataSource1"
              :pagination="false">
              <template slot="ellipsisText" slot-scope="text">
                <j-ellipsis :value="text" :length="textMaxLength"></j-ellipsis>
              </template>

              <template slot="dayWarnning" slot-scope="text,record">
                <a-icon type="bulb" theme="twoTone" style="font-size:22px" :twoToneColor="getTipColor(record)"/>
              </template>

              <span slot="action" slot-scope="text, record">
                <a @click="handleData">办理</a>
              </span>

            </a-table>
          </a-card>
        </a-col>

        <a-col :sm="24" :lg="12">
          <a-card>
            <div slot="title" class="index-md-title">
              <img src="../../assets/zaiban.png"/>
              我的在办【{{ dataSource2.length }}】
            </div>
            <div slot="extra">
              <a v-if="dataSource2 && dataSource2.length>0" slot="footer" @click="goPage">更多 <a-icon type="double-right" /></a>
            </div>
            <a-table
              :class="'my-index-table tytable2'"
              ref="table2"
              size="small"
              rowKey="id"
              :columns="columns"
              :dataSource="dataSource2"
              :pagination="false">
              <template slot="ellipsisText" slot-scope="text">
                <j-ellipsis :value="text" :length="textMaxLength"></j-ellipsis>
              </template>

              <template slot="dayWarnning" slot-scope="text,record">
                <a-icon type="bulb" theme="twoTone" style="font-size:22px" :twoToneColor="getTipColor(record)"/>
              </template>

              <span slot="action" slot-scope="text, record">
                <a @click="handleData">办理</a>
              </span>

            </a-table>
          </a-card>
        </a-col>

        <a-col :span="24">
          <div style="height: 5px;"></div>
        </a-col>

        <a-col :sm="24" :lg="12">
          <a-card>
            <div slot="title" class="index-md-title">
              <img src="../../assets/guaz.png"/>
              还未转账的公司【{{ dataSource4.length }}】
            </div>
            <a-table
              :class="'my-index-table tytable4'"
              ref="table4"
              size="small"
              rowKey="id"
              :columns="columns1"
              :dataSource="dataSource4"
              :pagination="false">
              <template slot="ellipsisText" slot-scope="text">
                <j-ellipsis :value="text" :length="textMaxLength"></j-ellipsis>
              </template>

              <template slot="dayWarnning" slot-scope="text,record">
                <a-icon type="bulb" theme="twoTone" style="font-size:22px" :twoToneColor="getTipColor(record)"/>
              </template>

              <span slot="action" slot-scope="text, record">
                <a @click="handleData">办理</a>
              </span>

            </a-table>
          </a-card>
        </a-col>

        <a-col :sm="24" :lg="12">
          <a-card>
            <div slot="title" class="index-md-title">
              <img src="../../assets/duban.png"/>
              我的督办【{{ dataSource3.length }}】
            </div>
            <a-table
              :class="'my-index-table tytable3'"
              ref="table3"
              size="small"
              rowKey="id"
              :columns="columns"
              :dataSource="dataSource3"
              :pagination="false">
              <template slot="ellipsisText" slot-scope="text">
                <j-ellipsis :value="text" :length="textMaxLength"></j-ellipsis>
              </template>

              <template slot="dayWarnning" slot-scope="text,record">
                <a-icon type="bulb" theme="twoTone" style="font-size:22px" :twoToneColor="getTipColor(record)"/>
              </template>

              <span slot="action" slot-scope="text, record">
                <a @click="handleData">办理</a>
              </span>

            </a-table>
          </a-card>
        </a-col>

      </a-row>
    </a-spin>

  </div>
</template>

<script>
  import noDataPng from '@/assets/nodata.png'
  import JEllipsis from '@/components/jeecg/JEllipsis'

  const tempSs1=[{
    id:"001",
    orderNo:"13869523021",
    orderTitle:"年底前收齐尾款",
    corporateName:"广西多鑫钢材有限公司"
  },{
    id:"002",
    orderNo:"15548596021",
    orderTitle:"一月份有一笔来款",
    corporateName:"广西沧海钢材有限公司"
  },{
    id:"003",
    orderNo:"18895842013",
    orderTitle:"黄老板十月份需要进一批棒材",
    corporateName:"广西金旋钢材有限公司"
  },{
    id:"004",
    orderNo:"13854150001",
    orderTitle:"下个月约李老板出来聚聚，维护一下交情",
    corporateName:"广西兴宇钢材有限公司"
  },{
    id:"005",
    orderNo:"13385694110",
    orderTitle:"以红利相诱，答应退保后扣一年费用",
    corporateName:"广西德睿钢材有限公司"
  }]

  const tempSs2=[{
    id:"001",
    orderTitle:"下午和王老板一起吃个饭",
    orderNo:"14952103000",
    corporateName:"广西晨光钢材有限公司"
  },{
    id:"002",
    orderTitle:"棒线类钢材规格有改变，去熟悉一下",
    orderNo:"17369555200",
    corporateName:"广西龙泽钢材有限公司"
  },{
    id:"003",
    orderTitle:"委托了他们的经理代购钢材",
    orderNo:"17799693253",
    corporateName:"广西聚兴源钢材有限公司"
  },{
    id:"004",
    orderTitle:"安排一下明天去他们公司谈生意的酒店",
    orderNo:"13866665230",
    corporateName:"广西广盛钢材有限公司"
  },{
    id:"005",
    orderTitle:"收钱",
    orderNo:"15541112302",
    corporateName:"广西万宏钢材有限公司"
  }]

  const tempSs4=[{
    id:"001",
    orderTitle:"￥2050000",
    orderNo:"13578556300",
    corporateName:"广西鑫隆钢材有限公司"
  },{
    id:"002",
    orderTitle:"￥120000",
    orderNo:"15788966632",
    corporateName:"广西吴航钢材有限公司"
  },{
    id:"003",
    orderTitle:"￥50000",
    orderNo:"17458899966",
    corporateName:"广西新旭钢材有限公司"
  },{
    id:"004",
    orderTitle:"￥609000",
    orderNo:"13744201855",
    corporateName:"广西铭顺钢材有限公司"
  },{
    id:"005",
    orderTitle:"￥854000",
    orderNo:"13966587854",
    corporateName:"广西鹿钢钢材有限公司"
  }
  ]

  //4-7天
  const tip_green = "rgba(0, 255, 0, 1)"
  //1-3天
  const tip_yellow = "rgba(255, 255, 0, 1)"
  //超期
  const tip_red = "rgba(255, 0, 0, 1)"

  export default {
    name: "IndexTask",
    components:{ JEllipsis },
    data() {
      return {
        loading:false,
        textMaxLength:8,
        dataSource1:[],
        dataSource2:[],
        dataSource3:[],
        dataSource4:[],
        columns: [
          // {
          //   title: '',
          //   dataIndex: '',
          //   key:'rowIndex',
          //   width:50,
          //   fixed:'left',
          //   align:"center",
          //   scopedSlots: {customRender: "dayWarnning"}
          // },
          // {
          //   title:'公司名称',
          //   align:"center",
          //   dataIndex: 'restDay',
          //   width:80
          // },
          {
            title:'公司名称',
            align:"center",
            dataIndex: 'corporateName',
          },
          {
            title:'事项',
            align:"center",
            dataIndex: 'orderTitle',
            scopedSlots: {customRender: "ellipsisText"}
          },
          {
            title:'负责人联系',
            align:"center",
            dataIndex: 'orderNo',
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' }
          }
        ],
        columns1: [
          {
            title:'公司名称',
            align:"center",
            dataIndex: 'corporateName',
          },
          {
            title:'欠款',
            align:"center",
            dataIndex: 'orderTitle',
            scopedSlots: {customRender: "ellipsisText"}
          },
          {
            title:'负责人联系',
            align:"center",
            dataIndex: 'orderNo',
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' }
          }
        ]

      }
    },
    created() {
      this.mock();
    },
    mounted(){

    },
    methods: {
      getTipColor(rd){
        let num = rd.restDay
        if(num<=0){
          return tip_red
        }else if(num>=1 && num<4){
          return tip_yellow
        }else if(num>=4){
          return tip_green
        }
      },
      goPage(){
        this.$message.success("请根据具体业务跳转页面")
        //this.$router.push({ path: '/comp/mytask' })
      },
      mock(){
        this.dataSource1=tempSs1
        this.dataSource2=tempSs2
        this.dataSource3=tempSs1
        this.dataSource4=tempSs4
        // this.ifNullDataSource(this.dataSource4,'.tytable4')
      },

      ifNullDataSource(ds,tb){
        this.$nextTick(()=>{
          if(!ds || ds.length==0){
            var tmp = document.createElement('img');
            tmp.src=noDataPng
            tmp.width=300
            let tbclass=`${tb} .ant-table-placeholder`
            document.querySelector(tbclass).innerHTML=""
            document.querySelector(tbclass).appendChild(tmp)
          }
        })
      },
      handleData(){
        this.$message.success("办理完成")
      }




    }
  }
</script>

<style>
  .my-index-table{height:270px}
  .my-index-table table{font-size: 14px !important;}

  .index-container-ty .ant-card-head-title{padding-top: 6px;padding-bottom: 6px;}
  .index-container-ty .ant-card-extra{padding:0}
  .index-container-ty .ant-card-extra a{color:#fff}
  .index-container-ty .ant-card-extra a:hover{color:#152ede}
  .index-container-ty .ant-card-head-wrapper,.index-container-ty .ant-card-head{
    line-height:24px;
    min-height:24px;
    /*background: #90aeff;*/
    background: #7196fb;
  }
  .index-container-ty .ant-card-body{padding: 10px 12px 0px 12px}

  /* .index-container-ty .ant-card-actions{background: #fff}
   .index-container-ty .ant-card-actions li {margin:2px 0;}
   .index-container-ty .ant-card-actions > li > span{width: 100%}*/


  .index-container-ty .ant-table-footer{text-align: right;padding:6px 12px 6px 6px;background: #fff;border-top: 2px solid #f7f1f1;}

  .index-md-title{
    postion:relative;
    padding-left:24px;
    width: 100%;
    color: #fff;
    font-size: 21px;
    font-family: cursive;
  }
  .index-md-title img{
    position: absolute;
    height:32px;
    top: 2px;
    left:14px;
  }

  .index-container-ty .ant-card-body{
    /*border-left:1px solid #90aeff;
    /*border-right:1px solid #90aeff;
    border-bottom:1px solid #90aeff;*/
  }


  .index-container-ty .ant-table-thead > tr > th,
  .index-container-ty .ant-table-tbody > tr > td{
    border-bottom: 1px solid #90aeff;
  }

  .index-container-ty .ant-table-small > .ant-table-content > .ant-table-fixed-left > .ant-table-body-outer > .ant-table-body-inner > table > .ant-table-thead > tr > th,
  .index-container-ty .ant-table-small > .ant-table-content > .ant-table-fixed-right > .ant-table-body-outer > .ant-table-body-inner > table > .ant-table-thead > tr > th{
    border-bottom: 1px solid #90aeff;
  }

  .index-container-ty  .ant-table-small > .ant-table-content > .ant-table-scroll > .ant-table-body > table > .ant-table-thead > tr > th{
    border-bottom: 1px solid #90aeff;
  }

  .index-container-ty .ant-table-small{
    border: 1px solid #90aeff;
  }

  .index-container-ty .ant-table-placeholder {
    padding: 0
  }
</style>