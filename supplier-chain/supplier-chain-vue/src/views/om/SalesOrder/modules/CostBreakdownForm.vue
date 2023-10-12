<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-item label="应收/应付" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['direction',validatorRules.required]" :trigger-change="true" dictCode="direction"
                                 placeholder="请选择应收/应付"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择日期" v-decorator="['date']" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="费用类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['expenseType',validatorRules.required]" :trigger-change="true"
                                 dictCode="expense_type" placeholder="请选择费用类型"/>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="结算单位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['settlementUnit']" placeholder="请输入结算单位"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-textarea  v-decorator="['remarks']" rows="4" placeholder="请输入备注"></a-textarea>
            </a-form-item>
          </a-col>
<!--          <a-col :span="24">-->
<!--            <a-form-item label="提单id" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--              <a-input v-decorator="['salesOrderId']"  aria-valuetext="a"  placeholder="请输入提单id"></a-input>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
          <a-col v-if="showFlowSubmitButton" :span="24" style="text-align: center">
            <a-button @click="submitForm">提 交</a-button>
          </a-col>
        </a-row>
      </a-form>
    </j-form-container>
  </a-spin>
</template>

<script>

import {httpAction, getAction} from '@/api/manage'
import pick from 'lodash.pick'
import {validateDuplicateValue} from '@/utils/util'
import JFormContainer from '@/components/jeecg/JFormContainer'
import JDate from '@/components/jeecg/JDate'
import JDictSelectTag from "@/components/dict/JDictSelectTag"

export default {
  name: 'CostBreakdownForm',
  components: {
    JFormContainer,
    JDate,
    JDictSelectTag,
  },
  props: {
    //流程表单data
    formData: {
      type: Object,
      default: () => {
      },
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
  data() {
    return {
      addOrModify:false,
      zid:"",
      form: this.$form.createForm(this),
      model: {},
      labelCol: {
        xs: {span: 24},
        sm: {span: 5},
      },
      wrapperCol: {
        xs: {span: 24},
        sm: {span: 16},
      },
      confirmLoading: false,
      validatorRules: {
        required: {
          rules: [
            { required: true, message: '请选择'},
          ]
        },
      },
      url: {
        add: "/om/costBreakdown/add",
        edit: "/om/costBreakdown/edit",
        queryById: "/om/costBreakdown/queryById"
      }
    }
  },
  computed: {
    formDisabled() {
      if (this.formBpm === true) {
        if (this.formData.disabled === false) {
          return false
        }
        return true
      }
      return this.disabled
    },
    showFlowSubmitButton() {
      if (this.formBpm === true) {
        if (this.formData.disabled === false) {
          return true
        }
      }
      return false
    }
  },
  created() {
    //如果是流程中表单，则需要加载流程表单data
    this.showFlowData();
  },
  methods: {
    add(mid) {
      this.zid=mid
      console.log(mid)
      this.edit({});
    },
    edit(record) {
      this.form.resetFields();
      this.model = Object.assign({}, record);
      this.visible = true;
      this.$nextTick(() => {
        this.form.setFieldsValue(pick(this.model, 'direction', 'date', 'expenseType', 'settlementUnit', 'remarks', 'salesOrderId'))
      })
    },
    //渲染流程表单数据
    showFlowData() {
      if (this.formBpm === true) {
        let params = {id: this.formData.dataId};
        getAction(this.url.queryById, params).then((res) => {
          if (res.success) {
            this.edit(res.result);
          }
        });
      }
    },
    submitForm() {
      const that = this;
      // 触发表单验证
      this.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true;
          let httpurl = '';
          let method = '';
          if (!this.model.id) {
            this.addOrModify=true;
            httpurl += this.url.add;
            method = 'post';
          } else {
            this.addOrModify=false;
            httpurl += this.url.edit;
            method = 'put';
          }
          let formData = Object.assign(this.model, values);
         if (this.addOrModify){
           formData['salesOrderId'] = this.zid;
         }
          this.addOrModify=false;
          console.log("表单提交数据", formData)
          httpAction(httpurl, formData, method).then((res) => {
            if (res.success) {
              that.$message.success(res.message);
              that.$emit('ok');
            } else {
              that.$message.warning(res.message);
            }
          }).finally(() => {
            that.confirmLoading = false;
          })
        }

      })
    },
    popupCallback(row) {
      this.form.setFieldsValue(pick(row, 'direction', 'date', 'expenseType', 'settlementUnit', 'remarks', 'salesOrderId'))
    },
  }
}
</script>