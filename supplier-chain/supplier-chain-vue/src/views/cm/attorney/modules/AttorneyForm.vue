<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
<!--          <a-col :span="12">-->
<!--            <a-form-item label="委托书编号" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--              <a-input v-decorator="['delegateNo']" placeholder="请输入委托书编号"></a-input>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
          <a-col :span="12">
            <a-form-item label="委托人单位名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag v-decorator="['delegateUnitName',validatorRules.delegateName]" dict="cm_customer_profile where  del_flag=0,company_name,id" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="委托人法定代表人/委托代理人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['delegateLegalRepresentative', validatorRules.delegateLegalRepresentative]" placeholder="请输入委托人法定代表人/委托代理人"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="委托人身份证号码" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['delegatorIdCardNo', validatorRules.delegatorIdCardNo]" placeholder="请输入委托人身份证号码"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="委托人职务" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['delegatorTitle']" placeholder="请输入委托人职务"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="委托人联系电话" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['delegatorMobile']" placeholder="请输入委托人联系电话"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="受托人姓名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['trusteeName', validatorRules.trusteeName]" placeholder="请输入受托人姓名"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="受托人性别" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['trusteeSex']" :trigger-change="true" dictCode="sex" placeholder="请选择受托人性别"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="受托人职务" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['trusteeTitle']" placeholder="请输入受托人职务"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="受托人联系电话" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['trusteeMobile']" placeholder="请输入受托人联系电话"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="受托人身份证号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['trusteeIdCardNo', validatorRules.trusteeIdCardNo]" placeholder="请输入受托人身份证号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="委托起始时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择委托起始时间" v-decorator="['commissionStartTime', validatorRules.commissionStartTime]" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="委托终止时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择委托终止时间" v-decorator="['terminationTimeOfEntrustment', validatorRules.terminationTimeOfEntrustment]" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="委托事项" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-textarea v-decorator="['entrustedMatters']" rows="4" placeholder="请输入委托事项"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="附件" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-upload v-decorator="['enclosure']" :trigger-change="true"></j-upload>
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
  import JDictSelectTag from "@/components/dict/JDictSelectTag"
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import JUpload from '@/components/jeecg/JUpload'

  export default {
    name: 'AttorneyForm',
    components: {
      JFormContainer,
      JDate,
      JDictSelectTag,
      JSearchSelectTag,
      JUpload,
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
          delegateName: {
            rules: [
              { required: true, message: '请输入委托人身份证号码!'},
            ]
          },
          delegateLegalRepresentative: {
            rules: [
              { required: true, message: '请输入委托人法定代表人/委托代理人!'},
            ]
          },
          delegatorIdCardNo: {
            rules: [
              { required: true, message: '请输入委托人身份证号码!'},
            ]
          },
          trusteeName: {
            rules: [
              { required: true, message: '请输入受托人姓名!'},
            ]
          },
          trusteeIdCardNo: {
            rules: [
              { required: true, message: '请输入受托人身份证号!'},
            ]
          },
          commissionStartTime: {
            rules: [
              { required: true, message: '请输入委托起始时间!'},
            ]
          },
          terminationTimeOfEntrustment: {
            rules: [
              { required: true, message: '请输入委托终止时间!'},
            ]
          },
        },
        url: {
          add: "/cm/attorney/add",
          edit: "/cm/attorney/edit",
          queryById: "/cm/attorney/queryById"
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
          this.form.setFieldsValue(pick(this.model,'delegateNo','delegateUnitName','delegateLegalRepresentative','delegatorIdCardNo','delegatorTitle','delegatorMobile','trusteeName','trusteeSex','trusteeTitle','trusteeMobile','trusteeIdCardNo','commissionStartTime','terminationTimeOfEntrustment','entrustedMatters','enclosure'))
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
        this.form.setFieldsValue(pick(row,'delegateNo','delegateUnitName','delegateLegalRepresentative','delegatorIdCardNo','delegatorTitle','delegatorMobile','trusteeName','trusteeSex','trusteeTitle','trusteeMobile','trusteeIdCardNo','commissionStartTime','terminationTimeOfEntrustment','entrustedMatters','enclosure'))
      },
    }
  }
</script>
<style>
  .ant-col-sm-5 {
    display: block;
    box-sizing: border-box;
    width: 30%;
  }
</style>