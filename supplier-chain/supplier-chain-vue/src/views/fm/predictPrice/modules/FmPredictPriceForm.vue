<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="12">
            <a-form-item label="创建日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择创建日期" v-decorator="['createTime']" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="物料" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['matName']" placeholder="请输入物料"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="品名中文" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prodCname', validatorRules.prodCname]" placeholder="请输入品名中文"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="钢号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['sgSign']" placeholder="请输入钢号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="规格" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['custMatSpecs']" placeholder="请输入规格"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="部门" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['department']" placeholder="请输入部门"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="计量单位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['measuringUnit']" placeholder="请输入计量单位"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="生产线" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prodLine']" placeholder="请输入生产线"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="数量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matNum', validatorRules.matNum]" placeholder="请输入数量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="单价" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['predictPrice', validatorRules.predictPrice]" placeholder="请输入单价" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择时间" v-decorator="['predictTime', validatorRules.predictTime]" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="材料厚度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matThick', validatorRules.matThick]" placeholder="请输入材料厚度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="材料长度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matLen']" placeholder="请输入材料长度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="材料宽度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matWidth']" placeholder="请输入材料宽度" style="width: 100%"/>
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
    name: 'FmPredictPriceForm',
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
          prodCname: {
            rules: [
              { required: true, message: '请输入品名中文!'},
            ]
          },
          matNum: {
            rules: [
              { required: true, message: '请输入数量!'},
            ]
          },
          predictPrice: {
            rules: [
              { required: true, message: '请输入单价!'},
            ]
          },
          predictTime: {
            rules: [
              { required: true, message: '请输入时间!'},
            ]
          },
          matThick: {
            rules: [
              { required: true, message: '请输入材料厚度!'},
            ]
          },
        },
        url: {
          add: "/fm/fmPredictPrice/add",
          edit: "/fm/fmPredictPrice/edit",
          queryById: "/fm/fmPredictPrice/queryById"
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
          this.form.setFieldsValue(pick(this.model,'createTime','matName','prodCname','sgSign','custMatSpecs','department','measuringUnit','prodLine','matNum','predictPrice','predictTime','matThick','matLen','matWidth'))
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
        this.form.setFieldsValue(pick(row,'createTime','matName','prodCname','sgSign','custMatSpecs','department','measuringUnit','prodLine','matNum','predictPrice','predictTime','matThick','matLen','matWidth'))
      },
    }
  }
</script>