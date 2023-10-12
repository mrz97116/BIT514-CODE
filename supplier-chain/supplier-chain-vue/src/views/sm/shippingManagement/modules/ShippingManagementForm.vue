<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-item label="派船分单号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['dispatchShipBillNo']" placeholder="请输入派船分单号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="收货单位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['consigneeUnit']" placeholder="请输入收货单位"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="装船地点" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['loadingSite']" placeholder="请输入装船地点"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="目的地" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['destinationName']" placeholder="请输入目的地"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="出发时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择出发时间" v-decorator="['departureTime']" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="到达时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择到达时间" v-decorator="['arrivalTime']" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="代理公司" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['agencyCompany']" placeholder="请输入代理公司"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="运输公司" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['transportCompany']" placeholder="请输入运输公司"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="销售合同" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['salesContract']" placeholder="请输入销售合同"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="客户订单" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['customerOrder']" placeholder="请输入客户订单"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="船号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['shipNo']" placeholder="请输入船号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="部门" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['departmentName']" placeholder="请输入部门"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="承运人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['carrierName']" placeholder="请输入承运人"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="出厂时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择出厂时间" v-decorator="['deliveryTime']" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="产品名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['productName']" placeholder="请输入产品名"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="物料状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['matStatus']" :trigger-change="true" dictCode="shipping_mat_status" ></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="规格" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matSpecs']" placeholder="请输入规格" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="钢号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['steelNo']" placeholder="请输入钢号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="尺寸" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['matSize']" placeholder="请输入尺寸"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="材料号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['matNo']" placeholder="请输入材料号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="车牌号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['carNo']" placeholder="请输入车牌号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="数量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['quantity']" placeholder="请输入数量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['weight']" placeholder="请输入重量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="制单人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['documentMaker']" placeholder="请输入制单人"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="制单时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择制单时间" v-decorator="['documentMakeTime']" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="授权人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['authorizer']" placeholder="请输入授权人"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="授权时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择授权时间" v-decorator="['authorizeTime']" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="验收人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['acceptor']" placeholder="请输入验收人"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="验收时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择验收时间" v-decorator="['acceptTime']" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['remark']" placeholder="请输入备注"></a-input>
            </a-form-item>
          </a-col>
          <a-col v-if="showFlowSubmitButton" :span="24" style="text-align: center">
            <a-button @click="submitForm">提 交</a-button>
          </a-col>
        </a-row>
      </a-form>
    </j-form-container>
  </a-spin>
</template>

<script>

  import { httpAction, getAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { validateDuplicateValue } from '@/utils/util'
  import JFormContainer from '@/components/jeecg/JFormContainer'
  import JDate from '@/components/jeecg/JDate'  

  export default {
    name: 'ShippingManagementForm',
    components: {
      JFormContainer,
      JDate,
    },
    props: {
      //流程表单data
      formData: {
        type: Object,
        default: ()=>{},
        required: false
      },
      //表单模式：true流程表单 false普通表单
      formBpm: {
        type: Boolean,
        default: false,
        required: false
      },
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    data () {
      return {
        form: this.$form.createForm(this),
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        validatorRules: {
        },
        url: {
          add: "/sm/shippingManagement/add",
          edit: "/sm/shippingManagement/edit",
          queryById: "/sm/shippingManagement/queryById"
        }
      }
    },
    computed: {
      formDisabled(){
        if(this.formBpm===true){
          if(this.formData.disabled===false){
            return false
          }
          return true
        }
        return this.disabled
      },
      showFlowSubmitButton(){
        if(this.formBpm===true){
          if(this.formData.disabled===false){
            return true
          }
        }
        return false
      }
    },
    created () {
      //如果是流程中表单，则需要加载流程表单data
      this.showFlowData();
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'dispatchShipBillNo','consigneeUnit','loadingSite','destinationName','departureTime','arrivalTime','agencyCompany','transportCompany','salesContract','customerOrder','shipNo','departmentName','carrierName','deliveryTime','productName','matStatus','matSpecs','steelNo','matSize','matNo','carNo','quantity','weight','documentMaker','documentMakeTime','authorizer','authorizeTime','acceptor','acceptTime','remark'))
        })
      },
      //渲染流程表单数据
      showFlowData(){
        if(this.formBpm === true){
          let params = {id:this.formData.dataId};
          getAction(this.url.queryById,params).then((res)=>{
            if(res.success){
              this.edit (res.result);
            }
          });
        }
      },
      submitForm () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            let formData = Object.assign(this.model, values);
            console.log("表单提交数据",formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
            })
          }
         
        })
      },
      popupCallback(row){
        this.form.setFieldsValue(pick(row,'dispatchShipBillNo','consigneeUnit','loadingSite','destinationName','departureTime','arrivalTime','agencyCompany','transportCompany','salesContract','customerOrder','shipNo','departmentName','carrierName','deliveryTime','productName','matStatus','matSpecs','steelNo','matSize','matNo','carNo','quantity','weight','documentMaker','documentMakeTime','authorizer','authorizeTime','acceptor','acceptTime','remark'))
      },
    }
  }
</script>