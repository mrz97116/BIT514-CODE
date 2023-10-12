<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-item label="收料人姓名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['receivingName', validatorRules.receivingName]" placeholder="请输入收料人姓名"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="身份证" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['idCard']" placeholder="请输入身份证"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="发货仓库" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag v-decorator="['stockId',validatorRules.stockId]"
                                   dict="sm_stock where del_flag=0,name,id"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="收料城市" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['receivingCity']" placeholder="请输入收料城市"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="电话" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['phone', validatorRules.phone]" placeholder="请输入电话"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="收料地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['address', validatorRules.address]" placeholder="请输入收料地址"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="运费" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['deliverExpense']" placeholder="请输入运费"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="车队运费" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['fleetDeliverExpense']" placeholder="请输入车队运费"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['remarks']" placeholder="请输入备注"/>
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
  import JSearchSelectTag from '@comp/dict/JSearchSelectTag'

  export default {
    name: 'ReceivingForm',
    components: {
      JFormContainer,
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
          receivingName: {
            rules: [
              { required: true, message: '请输入收料人姓名!'},
            ]
          },
          idCard: {
            rules: [
              { required: true, message: '请输入身份证!'},
            ]
          },
          phone: {
            rules: [
              { required: true, message: '请输入电话!'},
            ]
          },
          address: {
            rules: [
              { required: true, message: '请输入收料地址!'},
            ]
          },
          // fleetDeliverExpense: {
          //   rules: [
          //     { required: true, message: '请输入车队运费!'},
          //   ]
          // },
          // deliverExpense: {
          //   rules: [
          //     { required: true, message: '请输入运费!'},
          //   ]
          // }
        },
        url: {
          add: "/sm/receiving/add",
          edit: "/sm/receiving/edit",
          queryById: "/sm/receiving/queryById"
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
          this.form.setFieldsValue(pick(this.model,'receivingName','idCard','phone','address','deliverExpense','fleetDeliverExpense','remarks'))
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
        this.form.setFieldsValue(pick(row,'receivingName','idCard','phone','address','deliverExpense','fleetDeliverExpense','remarks'))
      },
      /** 当点击了复制新增按钮时调用此方法 */
      copyAdd(record) {
        if (typeof this.editBefore === 'function') this.editBefore(record)
        this.visible = true
        this.form.resetFields()
        this.model = Object.assign({}, record)
        this.cobyAddAfter(this.model)
      },
      cobyAddAfter() {
        let fieldval = pick(this.model, 'receivingName','idCard','phone','stockId','receivingCity','address','deliverExpense','fleetDeliverExpense','remarks')
        this.$nextTick(() => {
          this.form.setFieldsValue(fieldval)
        })
        delete this.model.id
      }
    }
  }
</script>