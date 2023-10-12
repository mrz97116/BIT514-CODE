<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="12">
            <a-form-item label="材料厚度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number :disabled="inputDisabled" v-decorator="['matThick',validatorRules.matThick]" placeholder="请输入材料厚度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="材料宽度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number :disabled="inputDisabled" v-decorator="['matWidth']" placeholder="请输入材料宽度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="材料长度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number :disabled="inputDisabled" v-decorator="['matLen']" placeholder="请输入材料长度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="规格" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input :disabled="inputDisabled" v-decorator="['custMatSpecs']" placeholder="请输入规格"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="物料种类" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag :disabled="inputDisabled" type="list" v-decorator="['matKind']" :trigger-change="true" dictCode="mat_kind" placeholder="请选择物料种类"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="材料总件数" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number :disabled="inputDisabled" v-decorator="['matNum']" placeholder="请输入材料总件数" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="品名中文别名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input :disabled="inputDisabled" v-decorator="['prodCnameOther']" placeholder="请输入品名中文别名"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="产品大类" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag :disabled="inputDisabled" type="list" v-decorator="['prodClassCode']" :trigger-change="true" dictCode="prod_code" placeholder="请选择产品大类"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="牌号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input :disabled="inputDisabled" v-decorator="['sgSign']" placeholder="请输入牌号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="品名中文" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input :disabled="inputDisabled" v-decorator="['prodCname']" placeholder="请输入品名中文"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="仓库" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag :disabled="inputDisabled" type="list" v-decorator="['stockHouseId']" :trigger-change="true" dictCode="sm_stock,name,id" placeholder="请选择仓库"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="单重" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['pieceWeight',validatorRules.pieceWeight]" placeholder="请输入单重" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="计重方式" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag :disabled="inputDisabled" type="radio" v-decorator="['wtMode']" :trigger-change="true" dictCode="wt_method_code" placeholder="请选择计重方式"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="可销售重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number :disabled="inputDisabled" v-decorator="['availableWeight']" placeholder="请输入可销售重量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="可销售数量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number :disabled="inputDisabled" v-decorator="['availableQty']" placeholder="请输入可销售数量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number :disabled="inputDisabled" v-decorator="['weight']" placeholder="请输入重量" style="width: 100%"/>
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
    name: 'InventoryForm',
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
      },
      inputDisabled: {
        typeL: Boolean,
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
          pieceWeight: {
            rules: [
              {required: true, message:'请输入单重'}
            ]
          },
          matThick: {
            rules: [
              { required: true, message: '请输入材料厚度!'},
              { required: false, message: '输入的值必须大于0!',pattern:/^(?!(0[0-9]{0,}$))[0-9]{1,}[.]{0,}[0-9]{0,}|0$/}
            ]
          }
        },
        url: {
          add: "/sm/inventory/add",
          edit: "/sm/inventory/edit",
          queryById: "/sm/inventory/queryById",
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
        this.inputDisabled
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'matLen','matWidth','matThick','custMatSpecs','matKind','matNum',
            'prodCnameOther','prodClassCode','sgSign','prodCname','stockHouseId','availableWeight','availableQty',
            'weight','wtMode','pieceWeight'))
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
        this.form.setFieldsValue(pick(row,'matLen','matWidth','matThick','custMatSpecs','matKind','matNum','prodCnameOther',
          'prodClassCode','sgSign','prodCname','stockHouseId','availableWeight','availableQty','weight','wtMode','pieceWeight'))
      },
    }
  }
</script>