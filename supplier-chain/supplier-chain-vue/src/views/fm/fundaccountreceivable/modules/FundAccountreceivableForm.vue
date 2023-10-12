<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
<!--          <a-col :span="24">-->
<!--            <a-form-item label="流水号" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--              <a-input v-decorator="['serialNo']" placeholder="请输入流水号" disabled="disabled"></a-input>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
          <a-col :span="24">
            <a-form-item label="外部单据号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['outTradeNo']" placeholder="请输入外部单据号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="顾客" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag v-decorator="['customerId']" placeholder="请输入顾客" dict="cm_customer_profile,company_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="总金额" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['totalAmount']" placeholder="请输入总金额" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="已收金额" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['receivedAmount']" placeholder="请输入已收金额" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="待收金额" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['pendingAmount']" placeholder="请输入待收金额" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['status']" placeholder="请输入状态" :trigger-change="true" dictCode="fundaccountreceivable_status"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['type']" placeholder="请输入类型" :trigger-change="true" dictCode="fundaccountreceivable_type"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['comment']" placeholder="请输入备注"></a-input>
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
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'

  export default {
    name: 'FundAccountreceivableForm',
    components: {
      JFormContainer,
      JSearchSelectTag,
      JDictSelectTag
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
          add: "/fm/fundAccountreceivable/add",
          edit: "/fm/fundAccountreceivable/edit",
          queryById: "/fm/fundAccountreceivable/queryById"
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
          this.form.setFieldsValue(pick(this.model,'customerId','totalAmount','receivedAmount','pendingAmount','status','type','serialNo','outTradeNo','comment'))
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
        this.form.setFieldsValue(pick(row,'customerId','totalAmount','receivedAmount','pendingAmount','status','type','serialNo','outTradeNo','comment'))
      },
    }
  }
</script>