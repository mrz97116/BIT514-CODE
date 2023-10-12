<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="8">
            <a-form-item label="牌号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['sgSign']" placeholder="请输入牌号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="品名代码" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prodCode']" placeholder="请输入品名代码"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="品名中文" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prodCname']" placeholder="请输入品名中文"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="产品大类代码" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['prodClassCode']" :trigger-change="true" dictCode="prod_code" placeholder="请选择产品大类代码"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="品名中文别名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prodCnameOther']" placeholder="请输入品名中文别名"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="产品状态码" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['matStatus']" placeholder="请输入产品状态码"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="仓库id" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['stockHouseId']" :trigger-change="true" dictCode="sm_stock,name,id" placeholder="请选择仓库id"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="库存类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['stockType']" :trigger-change="true" dictCode="stock_type" placeholder="请选择库存类型"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="材料号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['matNo']" placeholder="请输入材料号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="材料实际重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matActWt']" placeholder="请输入材料实际重量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="材料净重" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matNetWt']" placeholder="请输入材料净重" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="材料磅差重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matDiscrepWt']" placeholder="请输入材料磅差重量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="材料件数（根数）" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matNum']" placeholder="请输入材料件数（根数）" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="材料种类" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['matKind']" :trigger-change="true" dictCode="mat_kind" placeholder="请选择材料种类"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="可销售重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['availableWeight']" placeholder="请输入可销售重量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="可销售数量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['availableQty']" placeholder="请输入可销售数量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="预用重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['preuseWeight']" placeholder="请输入预用重量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="预用数量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['preuseQty']" placeholder="请输入预用数量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="材料长度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matLen']" placeholder="请输入材料长度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="材料宽度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matWidth']" placeholder="请输入材料宽度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="材料厚度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matThick']" placeholder="请输入材料厚度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="实际长度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matActLen']" placeholder="请输入实际长度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="实际宽度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matActWidth']" placeholder="请输入实际宽度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="实际厚度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matActThick']" placeholder="请输入实际厚度" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="规格" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['custMatSpecs']" placeholder="请输入规格"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="收货顾客编码" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['dealerId']" placeholder="请输入收货顾客编码"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="采购价" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['purchasePrice']" placeholder="请输入采购价" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="成本价" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['costPrice']" placeholder="请输入成本价" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="技术标准" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['technicalStandard']" placeholder="请输入技术标准"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="原材料号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['rawMaterialNo']" placeholder="请输入原材料号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="新增类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['addType']" placeholder="请输入新增类型"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="材料理论重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['matTheoryWt']" placeholder="请输入材料理论重量" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="码单号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['stackingNo']" placeholder="请输入码单号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="经销订货用户名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['orderCustCname']" placeholder="请输入经销订货用户名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="经销收货用户名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['consignUserName']" placeholder="请输入经销收货用户名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="计重方式" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['wtMode']" placeholder="请输入计重方式"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="切边标记" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['trimFlag']" placeholder="请输入切边标记"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="切边类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['trimType']" placeholder="请输入切边类型"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="脱脂标记" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['degreaseFlag']" placeholder="请输入脱脂标记"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="表面结构" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['strucCode']" placeholder="请输入表面结构"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="产品等级" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['prodClass']" placeholder="请输入产品等级"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="锌层结构(表面结构)" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['platingStructure']" placeholder="请输入锌层结构(表面结构)"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="表面处理方式(钝化方式)" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['surfaceTreatment']" placeholder="请输入表面处理方式(钝化方式)"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="涂油量要求" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['oilDemand']" placeholder="请输入涂油量要求"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="宽度精度等级（公差精度）" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['widthTolAccu']" placeholder="请输入宽度精度等级（公差精度）"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="表面质量等级" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['surfaceQualityLevel']" placeholder="请输入表面质量等级"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="不平度精度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['flatlessAccu']" placeholder="请输入不平度精度"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="锌层重量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['platingWeight']" placeholder="请输入锌层重量"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="优面标记" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['optimalFaceToward']" placeholder="请输入优面标记"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="光整要求" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['finishingRequirements']" placeholder="请输入光整要求"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="库位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['stockLocation']" placeholder="请输入库位"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="入库单号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['stockNo']" placeholder="请输入入库单号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="出售标识" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['sellFlag']" placeholder="请输入出售标识"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="租户id" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['tenantId']" placeholder="请输入租户id" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
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
  import JDictSelectTag from "@/components/dict/JDictSelectTag"

  export default {
    name: 'StockAllotForm',
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
          add: "/sm/stockAllot/add",
          edit: "/sm/stockAllot/edit",
          queryById: "/sm/stockAllot/queryById"
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
          this.form.setFieldsValue(pick(this.model,'sgSign','prodCode','prodCname','prodClassCode','prodCnameOther','matStatus','stockHouseId','stockType','matNo','matActWt','matNetWt','matDiscrepWt','matNum','matKind','availableWeight','availableQty','preuseWeight','preuseQty','matLen','matWidth','matThick','matActLen','matActWidth','matActThick','custMatSpecs','dealerId','purchasePrice','costPrice','technicalStandard','rawMaterialNo','addType','matTheoryWt','stackingNo','orderCustCname','consignUserName','wtMode','trimFlag','trimType','degreaseFlag','strucCode','prodClass','platingStructure','surfaceTreatment','oilDemand','widthTolAccu','surfaceQualityLevel','flatlessAccu','platingWeight','optimalFaceToward','finishingRequirements','stockLocation','stockNo','sellFlag','tenantId','remarks'))
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
        this.form.setFieldsValue(pick(row,'sgSign','prodCode','prodCname','prodClassCode','prodCnameOther','matStatus','stockHouseId','stockType','matNo','matActWt','matNetWt','matDiscrepWt','matNum','matKind','availableWeight','availableQty','preuseWeight','preuseQty','matLen','matWidth','matThick','matActLen','matActWidth','matActThick','custMatSpecs','dealerId','purchasePrice','costPrice','technicalStandard','rawMaterialNo','addType','matTheoryWt','stackingNo','orderCustCname','consignUserName','wtMode','trimFlag','trimType','degreaseFlag','strucCode','prodClass','platingStructure','surfaceTreatment','oilDemand','widthTolAccu','surfaceQualityLevel','flatlessAccu','platingWeight','optimalFaceToward','finishingRequirements','stockLocation','stockNo','sellFlag','tenantId','remarks'))
      },
    }
  }
</script>