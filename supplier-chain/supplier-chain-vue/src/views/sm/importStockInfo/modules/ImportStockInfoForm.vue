<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
<!--          <a-col :span="24">-->
<!--            <a-form-item label="租户id" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--              <a-input-number v-decorator="['tenantId']" placeholder="请输入租户id" style="width: 100%"/>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
<!--          <a-col :span="24">-->
<!--            <a-form-item label="逻辑删除" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--              <a-input-number v-decorator="['delFlag']" placeholder="请输入逻辑删除" style="width: 100%"/>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
          <a-col :span="24">
            <a-form-item label="品名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prodCnameOther']" placeholder="请输入客户id"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="厂编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['goodsNo']" placeholder="请输入厂编号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="材料号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['matNo']" placeholder="请输入材料号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="规格" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['standards']" placeholder="请输入规格" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="材质" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['surfaces']" placeholder="请输入材质"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="抄码净重" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['copyCodeNetWeight']" placeholder="请输入抄码净重" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="数量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['qty']" placeholder="请输入数量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="仓库" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['stockId']" placeholder="请输入仓库"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="产地" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['sources']" placeholder="请输入产地"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="船号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['shipNo']" placeholder="请输入船号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['remarks']" placeholder="请输入备注"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="制单时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择制单时间" v-decorator="['preparationTime']" :trigger-change="true" style="width: 100%"/>
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
    name: 'ImportStockInfoForm',
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
          add: "/sm/importStockInfo/add",
          edit: "/sm/importStockInfo/edit",
          queryById: "/sm/importStockInfo/queryById"
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
          this.form.setFieldsValue(pick(this.model,'tenantId','delFlag','prodCnameOther','goodsNo','matNo','standards','surfaces','copyCodeNetWeight','qty','stockId','sources','shipNo','remarks','preparationTime'))
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
        this.form.setFieldsValue(pick(row,'tenantId','delFlag','prodCnameOther','goodsNo','matNo','standards','surfaces','copyCodeNetWeight','qty','stockId','sources','shipNo','remarks','preparationTime'))
      },
    }
  }
</script>