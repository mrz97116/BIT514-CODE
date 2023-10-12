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
          <a-col :span="12">
            <a-form-item label="单位名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['companyName', validatorRules.companyName]" placeholder="请输入单位名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" v-has="'cm:orderMaxDueDays'">
            <a-form-item label="最大超期天数" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['orderMaxDueDays']" placeholder="请输入最大超期天数"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" v-has="'cm:canLadingBill_dictText'">
            <a-form-item label="是否可开单" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" placeholder="是否可开单" v-decorator="['canLadingBill']" :trigger-change="true"
                                 dictCode="yn"></j-dict-select-tag>
            </a-form-item>
          </a-col>
          <a-col :span="12" v-has="'cm:wmsSupervise_dictText'">
            <a-form-item label="物流园服务类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['wmsSupervise']" placeholder="物流园服务类型" :trigger-change="true" dictCode="wms_supervise" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="税号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['taxNo']" placeholder="请输入税号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="开票名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['billingName']" placeholder="请输入开票名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['address']" placeholder="请输入地址"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="传真" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['fax']" placeholder="请输入传真"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="区域" :labelCol="labelCol" :wrapperCol="wrapperCol">
		      <j-area-linkage type="cascader" v-decorator="['area']" placeholder="请输入省市区"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="座机号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['landLineNo']" placeholder="请输入座机"></a-input>
            </a-form-item>
          </a-col>

          <a-col :span="12" >
            <a-form-item label="营业执照" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-image-upload isMultiple v-decorator="['businessLicense']"></j-image-upload>
            </a-form-item>
          </a-col>
          <a-col :span="12" v-has="'cm:dealWay_dictText'">
            <a-form-item label="交易方式" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['dealWay']"
                                 placeholder="交易方式" :trigger-change="true" dictCode="deal_way" />
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
  import JDictSelectTag from "@/components/dict/JDictSelectTag"
  import JAreaLinkage from '@comp/jeecg/JAreaLinkage'
  import JImageUpload from '@/components/jeecg/JImageUpload'

  export default {
    name: "CustomerProfileModal",
    components: {
      JDictSelectTag,
      JAreaLinkage,
      JImageUpload,
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
        editDealWayStatus: 0,
        searchOptions: [{0:"工地交易"},{1:"市场交易"}],
        confirmLoading: false,
        validatorRules: {
          companyName: {
            rules: [
              { required: true, message: '请输入单位名称!'},
            ]
          },
          status: {
            rules: [
              { required: true, message: '请输入状态!'},
            ]
          },
          type: {
            rules: [
              { required: true, message: '请输入类型!'},
            ]
          },
          taxNo: {
            rules: [
              { required: true, message: '请输入税号!'},
            ]
          },
        },
        url: {
          add: "/cm/customerProfile/add",
          edit: "/cm/customerProfile/edit",
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
          this.form.setFieldsValue(pick(this.model,'companyName','wmsSupervise','alias','taxNo','address','mobile','area','status','type','businessLicense','fax','landLineNo','billingName','orderMaxDueDays','canLadingBill','dealWay'))
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
        this.form.setFieldsValue(pick(row,'companyName','wmsSupervise','alias','taxNo','address','mobile','area','status','type','businessLicenseString','fax','landLineNo','billingName','orderMaxDueDays','canLadingBill','dealWay'))
      },


    }
  }
</script>