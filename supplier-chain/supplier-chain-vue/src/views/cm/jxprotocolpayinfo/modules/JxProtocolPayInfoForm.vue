<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-item label="账号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['accountNumber']" placeholder="请输入账号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="账号名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['numberName']" placeholder="请输入账号名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="交易时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择交易时间" v-decorator="['traTime']" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="借方发生额（支取）" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['debitDraw']" placeholder="请输入借方发生额（支取）"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="贷方发生额（收入）" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['creditIncome']" placeholder="请输入贷方发生额（收入）"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="余额" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['balance']" placeholder="请输入余额"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="币种" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['current']" placeholder="请输入币种"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="对方户名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['accountName']" placeholder="请输入对方户名"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="对方账号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['oppositeNumber']" placeholder="请输入对方账号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="对方开户机构" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['mechanism']" placeholder="请输入对方开户机构"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="记账日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择记账日期" v-decorator="['bookkeepingDate']" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="摘要" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['aabstract']" placeholder="请输入摘要"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="账户明细编号-交易流水号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['serialNumber']" placeholder="请输入账户明细编号-交易流水号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="企业流水号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['enterpriseNumber']" placeholder="请输入企业流水号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="凭证种类" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['voucherType']" placeholder="请输入凭证种类"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="凭证号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['voucherNo']" placeholder="请输入凭证号"></a-input>
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
    name: 'JxProtocolPayInfoForm',
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
          add: "/cm/jxProtocolPayInfo/add",
          edit: "/cm/jxProtocolPayInfo/edit",
          queryById: "/cm/jxProtocolPayInfo/queryById"
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
          this.form.setFieldsValue(pick(this.model,'accountNumber','numberName','traTime','debitDraw','creditIncome','balance','current','accountName','oppositeNumber','mechanism','bookkeepingDate','aabstract','serialNumber','enterpriseNumber','voucherType','voucherNo'))
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
        this.form.setFieldsValue(pick(row,'accountNumber','numberName','traTime','debitDraw','creditIncome','balance','current','accountName','oppositeNumber','mechanism','bookkeepingDate','aabstract','serialNumber','enterpriseNumber','voucherType','voucherNo'))
      },
    }
  }
</script>