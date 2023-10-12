<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-item label="主体公司" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['mainCompany']" placeholder="请输入主体公司"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="分公司" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['branchCompany']" placeholder="请输入分公司"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="公司全称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['companyName']" placeholder="请输入公司全称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="详细地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['address']" placeholder="请输入详细地址"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="法人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['legalPerson']" placeholder="请输入法人"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="联系人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['contactPerson']" placeholder="请输入联系人"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="电子邮箱" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['email']" placeholder="请输入电子邮箱"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="联系电话" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['phone']" placeholder="请输入联系电话"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="传真" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['tax']" placeholder="请输入传真"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="指定联系人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['designatedContactPerson']" placeholder="请输入指定联系人"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="指定联系人电话" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['designatedContactPhone']" placeholder="请输入指定联系人电话"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="收件地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['receiveAddress']" placeholder="请输入收件地址"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="开户银行" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['bankName']" placeholder="请输入开户银行"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="银行账号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['bankAccount']" placeholder="请输入银行账号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="归属" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['owner']" placeholder="请输入归属"></a-input>
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

  export default {
    name: 'JxProtocolForm',
    components: {
      JFormContainer,
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
          add: "/cm/jxProtocol/add",
          edit: "/cm/jxProtocol/edit",
          queryById: "/cm/jxProtocol/queryById"
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
          this.form.setFieldsValue(pick(this.model,'mainCompany','branchCompany','companyName','address','legalPerson','contactPerson','email','phone','tax','designatedContactPerson','designatedContactPhone','receiveAddress','bankName','bankAccount','owner'))
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
        this.form.setFieldsValue(pick(row,'mainCompany','branchCompany','companyName','address','legalPerson','contactPerson','email','phone','tax','designatedContactPerson','designatedContactPhone','receiveAddress','bankName','bankAccount','owner'))
      },
    }
  }
</script>