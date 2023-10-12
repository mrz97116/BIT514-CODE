<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-item label="创建人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['createBy']" placeholder="请输入创建人"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="创建日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择创建日期" v-decorator="['createTime']" :trigger-change="true" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="调拨单号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['allotNum']" placeholder="请输入调拨单号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="发货仓" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['allotStockHouseId']" placeholder="请输入发货仓"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="接收仓" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['receiveStockHouseId']" placeholder="请输入接收仓"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="库位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['stockLocation']" placeholder="请输入库位"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="调拨数量合计" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['allotNumberSum']" placeholder="请输入调拨数量合计"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="调拨重量合计" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['allotWeightSum']" placeholder="请输入调拨重量合计"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="调拨时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择调拨时间" v-decorator="['allotCreateTime']" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="牌号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['sgSign']" placeholder="请输入牌号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="材料号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['matNo']" placeholder="请输入材料号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="材料长" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['matLen']" placeholder="请输入材料长"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="材料宽" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['matWidth']" placeholder="请输入材料宽"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="材料厚" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['matChick']" placeholder="请输入材料厚"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="品名中文" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prodCname']" placeholder="请输入品名中文"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="卸货人名字" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['dischargerName']" placeholder="请输入卸货人名字"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['remarks']" placeholder="请输入备注"></a-input>
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
    name: 'InventoryAllotForm',
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
          add: "/sm/inventoryAllot/add",
          edit: "/sm/inventoryAllot/edit",
          queryById: "/sm/inventoryAllot/queryById"
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
          this.form.setFieldsValue(pick(this.model,'createBy','createTime','allotNum','allotStockHouseId','receiveStockHouseId','stockLocation','allotNumberSum','allotWeightSum','allotCreateTime','sgSign','matNo','matLen','matWidth','matChick','prodCname','dischargerName','remarks'))
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
        this.form.setFieldsValue(pick(row,'createBy','createTime','allotNum','allotStockHouseId','receiveStockHouseId','stockLocation','allotNumberSum','allotWeightSum','allotCreateTime','sgSign','matNo','matLen','matWidth','matChick','prodCname','dischargerName','remarks'))
      },
    }
  }
</script>