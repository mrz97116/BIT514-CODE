<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <!--<a-col :span="24">-->
            <!--<a-form-item label="供应商id" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
              <!--<a-input v-decorator="['delaerId']" placeholder="请输入供应商id"></a-input>-->
            <!--</a-form-item>-->
          <!--</a-col>-->
          <a-col :span="24">
            <a-form-item label="仓库名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['name',validatorRules.name]" placeholder="请输入仓库名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="仓库地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['address',validatorRules.address]" placeholder="请输入仓库地址"></a-input>
            </a-form-item>
          </a-col>
          <a-col v-if="tenantId == 12" :span="24">
            <a-form-item label="是否是物流园仓库" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['logisticsParkWarehouse',validatorRules.logisticsParkWarehouse]"
                                 :trigger-change="true"
                                 dictCode="yn" placeholder="请选择是否是物流园仓库"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="是否是虚拟仓库" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['virtualStock',validatorRules.virtualStock]"
                                 :trigger-change="true"
                                 dictCode="yn" placeholder="请选择是否是虚拟仓库"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['remark']" placeholder="请输入备注"></a-input>
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
    name: 'StockForm',
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
        tenantId: '',
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
          name: {
            rules: [
              { required: true, message: '请输入仓库名称!' },
            ]
          },
          address: {
            rules: [
              { required: true, message: '请输入仓库地址!' },
            ]
          },
          logisticsParkWarehouse: {
            rules: [
              { required: true, message: '请选择是否是物流园仓库!' },
            ],
            initialValue: '0'
          },

        },
        url: {
          add: "/sm/stock/add",
          edit: "/sm/stock/edit",
          queryById: "/sm/stock/queryById"
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
      this.tenantId = this.$ls.get('TENANT_ID')
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
          this.form.setFieldsValue(pick(this.model,'delaerId','name','code','address','logisticsParkWarehouse','virtualStock','active','remark','tenantId','delFlag'))
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
        this.form.setFieldsValue(pick(row,'delaerId','name','code','address','logisticsParkWarehouse','virtualStock','active','remark','tenantId','delFlag'))
      },
    }
  }
</script>