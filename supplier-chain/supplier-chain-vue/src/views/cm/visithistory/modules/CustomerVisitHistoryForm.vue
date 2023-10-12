<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-item label="顾客" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag v-decorator="['customerId', validatorRules.customerId]" dict="cm_customer_profile where  del_flag=0,company_name,id" />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="拜访地点" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['visitLocation', validatorRules.visitLocation]" placeholder="请输入拜访地点"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="拜访时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择拜访时间" v-decorator="['visitTime']" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
<!--          <a-col :span="24">-->
<!--            <a-form-item label="业务员姓名" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--              <a-input v-decorator="['salesmanName']" placeholder="请输入业务员姓名"></a-input>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
          <a-col :span="24">
            <a-form-item label="业务员" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag v-decorator="['salesmanName']" dict="cm_salesman_info,name,id" placeholder="请输入业务员姓名"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="拜访客户姓名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['customerName']" placeholder="请输入拜访客户姓名"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="客户职级" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['customerTitle']" :trigger-change="true" dictCode="customer_level" placeholder="请输入客户职级"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-textarea v-decorator="['remark']" rows="4" placeholder="请输入备注"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
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
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import JUpload from '@/components/jeecg/JUpload'

  export default {
    name: 'CustomerVisitHistoryForm',
    components: {
      JFormContainer,
      JDate,
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
          customerId: {
            rules: [
              { required: true, message: '请输入顾客!'},
            ]
          },
          visitLocation: {
            rules: [
              { required: true, message: '请输入拜访地点!'},
            ]
          },
        },
        url: {
          add: "/cm/customerVisitHistory/add",
          edit: "/cm/customerVisitHistory/edit",
          queryById: "/cm/customerVisitHistory/queryById"
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
          this.form.setFieldsValue(pick(this.model,'customerId','visitLocation','visitTime','salesmanName','customerName','customerTitle','remark','enclosure'))
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
        this.form.setFieldsValue(pick(row,'customerId','visitLocation','visitTime','salesmanName','customerName','customerTitle','remark','enclosure'))
      },
    }
  }
</script>