<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="12">
            <a-form-item label="顾客" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag v-decorator="['customerId']" dict="cm_customer_profile where  del_flag=0,company_name,id" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="额度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['amount']" placeholder="请输入额度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="剩余可用额度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['availableAmount']" placeholder="请输入剩余可用额度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="授信编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['creditNo']" placeholder="请输入授信编号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="授信类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['type']" :trigger-change="true" dictCode="credit_type" placeholder="请选择授信类型"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="结束时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择结束时间" v-decorator="['endTime']" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="账户类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['accountType']" placeholder="请输入账户类型"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="审核状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['status']" :trigger-change="true" dictCode="common_check_status" placeholder="请选择审核状态"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="是否滚动" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['scroll', validatorRules.scroll]" :trigger-change="true" dictCode="yn" placeholder="请选择是否滚动"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="产品大类" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['prodCode']" :trigger-change="true" dictCode="prod_code" placeholder="请选择产品大类"/>
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

  export default {
    name: 'CustomerCreditForm',
    components: {
      JFormContainer,
      JDate,
      JDictSelectTag,
      JSearchSelectTag,
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
          scroll: {
            rules: [
              { required: true, message: '请输入是否滚动!'},
            ]
          },
        },
        url: {
          add: "/cm/customerCredit/add",
          edit: "/cm/customerCredit/edit",
          queryById: "/cm/customerCredit/queryById"
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
          this.form.setFieldsValue(pick(this.model,'customerId','amount','availableAmount','creditNo','type','endTime','accountType','status','scroll','prodCode'))
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
        this.form.setFieldsValue(pick(row,'customerId','amount','availableAmount','creditNo','type','endTime','accountType','status','scroll','prodCode'))
      },
    }
  }
</script>