<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row>
          <a-col :span="24">
            <a-form-item label="联系人姓名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['contactName']" placeholder="请输入联系人姓名"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="联系电话" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['contactPhone', validatorRules.contactPhone]" placeholder="请输入联系电话"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="身份证号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['idCardNo']" placeholder="请输入身份证号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-textarea v-decorator="['address']" rows="4" placeholder="请输入地址"/>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>

  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { validateDuplicateValue } from '@/utils/util'

  export default {
    name: "CustomerAddressModal",
    components: {
    },
    props:{
      mainId:{
        type:String,
        required:false,
        default:''
      }
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"操作",
        width:800,
        visible: false,
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
          contactPhone: {
            rules: [
              { required: false},
              { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号码!'},
            ]
          },
        },
        url: {
          add: "/cm/customerProfile/addCustomerAddress",
          edit: "/cm/customerProfile/editCustomerAddress",
        }

      }
    },
    created () {
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
          this.form.setFieldsValue(pick(this.model,'createBy','createTime','updateBy','updateTime','sysOrgCode','customerId','contactName','contactPhone','idCardNo','address','tenantId','delFlag'))
        })
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
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
            formData['customerId'] = this.mainId
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
              that.close();
            })
          }

        })
      },
      handleCancel () {
        this.close()
      },
      popupCallback(row){
        this.form.setFieldsValue(pick(row,'createBy','createTime','updateBy','updateTime','sysOrgCode','customerId','contactName','contactPhone','idCardNo','address','tenantId','delFlag'))
      },


    }
  }
</script>
