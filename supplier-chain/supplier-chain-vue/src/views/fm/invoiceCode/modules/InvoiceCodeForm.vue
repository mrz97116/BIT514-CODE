<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="12">
            <a-form-item label="货物名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['mat', validatorRules.mat]" placeholder="请输入货物名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="开票中文名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['invoiceCname', validatorRules.invoiceCname]" placeholder="请输入开票中文名"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="产品中文名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['productCname', validatorRules.productCname]" placeholder="请输入产品中文名"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="品名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prodCname',validatorRules.prodCname]" placeholder="请输入品名"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="品名别名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prodCnameOther',validatorRules.prodCnameOther]" placeholder="请输入品名别名"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="产品大类" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['prodClassCode', validatorRules.prodClassCode]" :trigger-change="true" dictCode="prod_code" placeholder="请选择产品大类"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="牌号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['sgSign',validatorRules.sgSign]" placeholder="请输入牌号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="产品编码" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['productCode',validatorRules.productCode]" placeholder="请输入产品编码"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="截面描述" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['crossDesc']" placeholder="请输入截面描述"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="直径(外径)起" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['diameterStart',validatorRules.diameterStart]" placeholder="请输入直径(外径)起" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="直径(外径)止" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['diameterEnd',validatorRules.diameterEnd]" placeholder="请输入直径(外径)止" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="产线类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['productionLineType']" placeholder="请输入产线类型"></a-input>
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
  import JDictSelectTag from "@/components/dict/JDictSelectTag"

  export default {
    name: 'InvoiceCodeForm',
    components: {
      JFormContainer,
      JDictSelectTag,
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
          prodClassCode: {
            rules: [
              { required: true, message: '请选择产品大类!'},
            ]
          },
          mat: {
            rules: [
              { required: true, message: '请输入货物名称!'},
            ]
          },
          productCname: {
            rules: [
              { required: true, message: '请输入产品中文名!'},
            ]
          },
          invoiceCname: {
            rules: [
              { required: true, message: '请输入开票中文名!'},
            ]
          },
          prodCname: {
            rules: [
              {required: true, message: '请输入品名!'}
            ]
          },
          prodCnameOther: {
            rules: [
              {required: true, message: '请输入品名别名!'}
            ]
          },
          sgSign: {
            rules: [
              {required: true, message: '请输入牌号!'}
            ]
          },
          productCode: {
            rules: [
              {required: true, message: '请输入产品编号!'}
            ]
          },
          diameterStart: {
            rules: [
              {required: true, message: '请输入直径(外径)起!'}
            ]
          },
          diameterEnd: {
            rules: [
              {required: true, message: '请输入直径(外径)止!'}
            ]
          }
        },
        url: {
          add: "/fm/invoiceCode/add",
          edit: "/fm/invoiceCode/edit",
          queryById: "/fm/invoiceCode/queryById"
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
          this.form.setFieldsValue(pick(this.model,'prodClassCode','sgSign','mat','diameterStart','crossDesc','productCname','prodCname','prodCnameOther','productCode','invoiceCname','diameterEnd','productionLineType'))
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
        this.form.setFieldsValue(pick(row,'prodClassCode','sgSign','mat','diameterStart','crossDesc','productCname','prodCname','prodCnameOther','productCode','invoiceCname','diameterEnd','productionLineType'))
      },
    }
  }
</script>