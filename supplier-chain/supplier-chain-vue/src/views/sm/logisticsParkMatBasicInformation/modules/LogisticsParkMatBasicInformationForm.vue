<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
<!--          <a-col :span="24">-->
<!--            <a-form-item label="产品代码" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--              <a-input disabled v-decorator="['productCode']" placeholder="请输入产品代码"></a-input>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
          <a-col :span="24">
            <a-form-item label="品名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['productName']" placeholder="请输入品名"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="材质" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['steelGradeName']" placeholder="请输入材质"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="产地" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['steelMillsName']" placeholder="请输入产地"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="生成标准" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['standardName']" placeholder="请输入生成标准"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="长度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number disabled v-decorator="['length']" placeholder="请输入长度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="宽度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number disabled v-decorator="['width']" placeholder="请输入宽度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="厚度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number disabled v-decorator="['thick']" placeholder="请输入厚度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="包装" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number disabled v-decorator="['packageCount']" placeholder="请输入包装" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="单重" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number disabled v-decorator="['singleWeight']" placeholder="请输入单重" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="计重方式" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number disabled v-decorator="['weightMode']" placeholder="请输入计重方式" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="数量单位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['numberUnit']" placeholder="请输入数量单位"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="重量单位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input disabled v-decorator="['weightUnit']" placeholder="请输入重量单位"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="系统产品名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['sysProductName']" placeholder="请输入系统产品名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="系统牌号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['sysSgSign']" placeholder="请输入系统牌号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="系统厚度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['sysThick']" placeholder="请输入系统厚度"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="系统宽度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['sysWidth']" placeholder="请输入系统宽度"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="系统长度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['sysLength']" placeholder="请输入系统长度"></a-input>
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
    name: 'LogisticsParkMatBasicInformationForm',
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
          add: "/sm/logisticsParkMatBasicInformation/add",
          edit: "/sm/logisticsParkMatBasicInformation/edit",
          queryById: "/sm/logisticsParkMatBasicInformation/queryById"
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
          this.form.setFieldsValue(pick(this.model,'productCode','productName','sysSgSign','steelMillsName','standardName','length','width','thick','packageCount','singleWeight','weightMode','numberUnit','weightUnit','steelGradeName','sysProductName','sysThick','sysWidth','sysLength'))
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
        this.form.setFieldsValue(pick(row,'productCode','productName','sysSgSign','steelMillsName','standardName','length','width','thick','packageCount','singleWeight','weightMode','numberUnit','weightUnit','steelGradeName','sysProductName','sysThick','sysWidth','sysLength'))
      },
    }
  }
</script>