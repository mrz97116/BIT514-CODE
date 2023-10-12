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
            <a-form-item label="材料号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['materialNo']" placeholder="请输入材料号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="产品代码" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['productCode']" placeholder="请输入产品代码"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="品名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['productName']" placeholder="请输入品名"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="材质" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['steelGradeName']" placeholder="请输入材质"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="产地" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['steelMillsName']" placeholder="请输入产地"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="生产标准" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['standardName']" placeholder="请输入生产标准"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="长度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['length']" placeholder="请输入长度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="宽度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['width']" placeholder="请输入宽度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="厚度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['thick']" placeholder="请输入厚度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="包装" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['packageCount']" placeholder="请输入包装"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="计量方式" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['weightMode']" placeholder="请输入计量方式" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="数量单位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['numberUnit']" placeholder="请输入数量单位"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="重量单位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['weightUnit']" placeholder="请输入重量单位"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="数量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['quantity']" placeholder="请输入数量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['weight']" placeholder="请输入重量" style="width: 100%"/>
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
    name: "GetActualDeliveryDetModal",
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
        },
        url: {
          add: "/sm/getActualDelivery/addGetActualDeliveryDet",
          edit: "/sm/getActualDelivery/editGetActualDeliveryDet",
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
          this.form.setFieldsValue(pick(this.model,'createBy','createTime','updateBy','updateTime','sysOrgCode','getActualDeliveryId','materialNo','productCode','productName','steelGradeName','steelMillsName','standardName','length','width','thick','packageCount','weightMode','numberUnit','weightUnit','originalCustomerName','quantity','weight','isOwenership','tenantId','delFlag'))
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
            formData['getActualDeliveryId'] = this.mainId
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
        this.form.setFieldsValue(pick(row,'createBy','createTime','updateBy','updateTime','sysOrgCode','getActualDeliveryId','materialNo','productCode','productName','steelGradeName','steelMillsName','standardName','length','width','thick','packageCount','weightMode','numberUnit','weightUnit','originalCustomerName','quantity','weight','isOwenership','tenantId','delFlag'))
      },


    }
  }
</script>
