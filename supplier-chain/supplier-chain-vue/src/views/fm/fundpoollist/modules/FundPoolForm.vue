<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="12">
            <a-form-item label="顾客" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag :disabled="inputDisabled"  v-decorator="['customerId', validatorRules.customerId]"
                                   dict="cm_customer_profile where  del_flag=0,company_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="来款方式" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag :disabled="inputDisabled" type="list" v-decorator="['paymentMethod',validatorRules.paymentMethod]"
                                 :trigger-change="true" dictCode="payment_method" placeholder="请选择来款方式"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="来款金额"  :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="['incomingAmount',validatorRules.incomingAmount]" placeholder="请输入来款金额"
                              :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"  :parser="value => value.replace(/\$\s?|(,*)/g, '')" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12" v-show="this.form.getFieldValue('paymentMethod') != 'advance_charge'&& this.form.getFieldValue('paymentMethod') != 'sales_return'?true:false">
            <a-form-item label="* 到账银行" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag v-decorator="['paymentBank']" dict="fm_bank  where del_flag=0,bank_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :span="12" v-show="this.form.getFieldValue('paymentMethod') != 'acceptance_bill' ?false:true">
            <a-form-item label="* 承兑银行" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-search-select-tag v-decorator="['acceptanceBank',validatorRules.acceptanceBank]" dict="fm_bank  where del_flag=0,bank_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :span="12" v-show="this.form.getFieldValue('paymentMethod') != 'acceptance_bill' ?false:true">
            <a-form-item label="* 承兑汇票号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['acceptanceTicketNo',validatorRules.acceptanceTicketNo]" placeholder="请输入承兑汇票号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="来款日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择来款日期"  v-decorator="['incomingDate',validatorRules.incomingDate]" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12" v-show="this.form.getFieldValue('paymentMethod') != 'acceptance_bill' ?false:true">
            <a-form-item label="* 汇票到期日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="汇票到期日期"  v-decorator="['ticketDate']" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12" v-show="this.form.getFieldValue('paymentMethod') != 'acceptance_bill' ?false:true">
            <a-form-item label="承兑出票人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['acceptancePeople']" placeholder="请输入承兑出票人"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" v-show="this.form.getFieldValue('paymentMethod') != 'acceptance_bill' ?false:true">
            <a-form-item label="* 出票日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="出票日期"  v-decorator="['issueTicketsDate',validatorRules.issueTicketsDate]" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="来款用途" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['purpose']" placeholder="请输入来款用途"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['remarks']" placeholder="请输入备注"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="进账方式" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag :disabled="inputDisabled" v-decorator="['incomeMethod',validatorRules.incomeMethod ]" :trigger-change="true"
                                 dictCode="income_method" placeholder="请输入进账方式"/>
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

  import {getAction, httpAction} from '@/api/manage'
  import pick from 'lodash.pick'
  import JFormContainer from '@/components/jeecg/JFormContainer'
  import JDate from '@/components/jeecg/JDate'
  import JDictSelectTag from "@/components/dict/JDictSelectTag"
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'

  export default {
    name: 'FundPoolForm',
    components: {
      JFormContainer,
      JDate,
      JDictSelectTag,
      JSearchSelectTag,
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
      },
      inputDisabled: {
        type: Boolean,
        default: false,
        required: false
      },
    },
    data() {
      return {
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
          customerId: {
            rules: [
              {required: true, message: '请输入顾客!'},
            ]
          },
          paymentMethod: {
            rules: [
              {required: true, message: '请输入来款方式!'},
            ]
          },
          incomingAmount: {
            rules: [
              {required: true, message: '请输入来款金额!'},
            ]
          },
          paymentBank: {
            rules: [
              {required: true, message: '请输入汇款银行!'},
            ]
          },
          incomeMethod: {
            initialValue: 'normal_payment'
          }
        },
        url: {
          add: "/fm/fundPool/add",
          edit: "/fm/fundPool/edit",
          queryById: "/fm/fundPool/queryById"
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
      add() {
        this.edit({});
      },
      edit(record) {
        this.inputDisabled
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'customerId', 'paymentMethod', 'incomingAmount', 'purpose', 'paymentBank', 'acceptanceBank', 'acceptanceTicketNo', 'incomingDate', 'prepaidAmount', 'availableAmount', 'incomingType', 'acceptancePeople', 'receiptCode', 'settleAmount', 'verifyDate','remarks','issueTicketsDate','incomeMethod','ticketDate'))
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
              httpurl += this.url.add;
              method = 'post';
            } else {
              httpurl += this.url.edit;
              method = 'put';
            }

            let formData = Object.assign(this.model, values);
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
        this.form.setFieldsValue(pick(row, 'customerId', 'paymentMethod', 'incomingAmount', 'purpose', 'paymentBank', 'acceptanceBank', 'acceptanceTicketNo', 'incomingDate', 'prepaidAmount', 'availableAmount', 'incomingType', 'acceptancePeople', 'receiptCode', 'settleAmount', 'verifyDate','remarks','issueTicketsDate','incomeMethod','ticketDate'))
      },
    }
  }
</script>