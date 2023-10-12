<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-item label="产品名称（旧系统产品中文名）" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['oldProdCname', validatorRules.oldProdCname]" placeholder="请输入产品名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="牌号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['sgSign']" placeholder="请输入牌号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="材料厚度（规格）" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['matThick']" placeholder="请输入材料厚度"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="物料编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['matCode', validatorRules.matCode]" placeholder="请输入物料编号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="产线类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['productionLineType', validatorRules.productionLineType]" placeholder="请输入产线类型"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="品种" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['variety', validatorRules.variety]" placeholder="请输入品种"></a-input>
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
    name: 'SettleMatCodingForm',
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
        matThick:'',
        sgSign:'',
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
          oldProdCname: {
            rules: [
              { required: true, message: '请输入产品名称!'},
            ]
          },
          matCode: {
            rules: [
              { required: true, message: '请输入物料编号!'},
            ]
          },
          productionLineType: {
            rules: [
              { required: true, message: '请输入产线类型!'},
            ]
          },
          variety: {
            rules: [
              { required: true, message: '请输入品种!'},
            ]
          },
        },
        url: {
          add: "/fm/settleMatCoding/add",
          edit: "/fm/settleMatCoding/edit",
          queryById: "/fm/settleMatCoding/queryById"
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
          this.form.setFieldsValue(pick(this.model,'oldProdCname','sgSign','matThick','matCode','productionLineType','variety'))
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
        this.form.setFieldsValue(pick(row,'oldProdCname','sgSign','matThick','matCode','productionLineType','variety'))
      },
    }
  }
</script>