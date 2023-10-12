<template>
  <j-modal
    :title="titleName"
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
            <a-form-item label="评分时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date v-decorator="['rateTime',validatorRules.rateTime]"  placeholder="请选择评分时间"  :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="星级评分" :labelCol="labelCol" :wrapperCol="wrapperCol">
               <span>
                  <a-rate v-decorator="['rate',validatorRules.rate]"  :tooltips="desc" />
              </span>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-textarea v-decorator="['remarks']" rows="3" placeholder="请输入备注"/>
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
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import JDate from '@/components/jeecg/JDate'

  export default {
    name: "CustomerRateModal",
    components: {
      JSearchSelectTag,
      JDate,
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
        titleName: "星级评价新增",
        width:800,
        visible: false,
        model: {},
        desc: ['1星级', '2星级', '3星级', '4星级', '5星级'],
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
          rateTime: {
            rules: [
              { required: true, message: '请选择评分时间!'},
            ]
          },
          rate: {
            rules: [
              { required: true, message: '请选择星级评分!'},
            ]
          },
        },
        url: {
          add: "/cm/customerProfile/addCustomerRate",
          edit: "/cm/customerProfile/editCustomerRate",
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
          this.form.setFieldsValue(pick(this.model,'createBy','createTime','updateBy','updateTime','sysOrgCode','customerId','rateTime','rate','remarks','delFlag','tenantId'))
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
            let formData = Object.assign(this.model, values);
            formData['customerId'] = this.mainId
            console.log("表单提交数据",formData)
            httpAction(this.url.add,formData,'post').then((res)=>{
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
        this.form.setFieldsValue(pick(row,'createBy','createTime','updateBy','updateTime','sysOrgCode','customerId','rateTime','rate','remarks','delFlag','tenantId'))
      },


    }
  }
</script>
