<template>
  <a-form-item v-if="item.view=='date'" :label="item.label">
    <template v-if="single_mode===item.mode">
      <j-date :placeholder=" '请选择'+item.label " v-model="queryParam[item.field]"></j-date>
    </template>
    <template v-else>
      <j-date placeholder="开始日期" v-model="queryParam[item.field+'_begin']" style="width:calc(50% - 15px);"></j-date>
      <span class="group-query-strig">~</span>
      <j-date placeholder="结束日期" v-model="queryParam[item.field+'_end']" style="width:calc(50% - 15px);"></j-date>
    </template>
  </a-form-item>

  <a-form-item v-else-if="item.view=='datetime'" :label="item.label">
    <template v-if="single_mode===item.mode">
      <j-date :placeholder=" '请选择'+item.label " :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" v-model="queryParam[item.field]"></j-date>
    </template>
    <template v-else>
      <j-date placeholder="请选择开始时间" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" v-model="queryParam[item.field+'_begin']" style="width:calc(50% - 15px);"></j-date>
      <span class="group-query-strig">~</span>
      <j-date placeholder="请选择开始时间" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" v-model="queryParam[item.field+'_end']" style="width:calc(50% - 15px);"></j-date>
    </template>
  </a-form-item>

  <a-form-item v-else-if="item.view=='time'" :label="item.label">
    <template v-if="single_mode===item.mode">
      <j-time :placeholder=" '请选择'+item.label " date-format="HH:mm:ss" v-model="queryParam[item.field]"></j-time>
    </template>
    <template v-else>
      <j-time placeholder="请选择开始时间" date-format="HH:mm:ss" v-model="queryParam[item.field+'_begin']" style="width:calc(50% - 15px);"></j-time>
      <span class="group-query-strig">~</span>
      <j-time placeholder="请选择结束时间" date-format="HH:mm:ss" v-model="queryParam[item.field+'_end']" style="width:calc(50% - 15px);"></j-time>
    </template>
  </a-form-item>

  <a-form-item v-else-if=" item.view=='list' || item.view=='radio' || item.view=='switch'" :label="item.label">
    <j-dict-select-tag
      v-if="item.config==='1'"
      :placeholder=" '请选择'+item.label "
      v-model="queryParam[item.field]"
      :dictCode="getDictCode(item)">
    </j-dict-select-tag>
    <a-select v-else :placeholder=" '请选择'+item.label " v-model="queryParam[item.field]">
      <a-select-option
        v-for="(op,opIndex) in dictOptions[item.field]"
        :key="opIndex"
        :value="op.value">
        {{ op.text }}
      </a-select-option>
    </a-select>
  </a-form-item>

  <a-form-item v-else-if=" item.view=='sel_tree' " :label="item.label">
    <j-tree-select
      :placeholder=" '请选择'+item.label "
      v-model="queryParam[item.field]"
      :dict="item.dict"
      :pidField="item.pidField"
      :pidValue="item.pidValue"
      :hasChildField="item.hasChildField"
      load-triggle-change>
    </j-tree-select>
  </a-form-item>

  <a-form-item v-else-if=" item.view=='cat_tree' " :label="item.label">
    <j-category-select :pcode="item.pcode" v-model="queryParam[item.field]" :placeholder=" '请选择'+item.label "/>
  </a-form-item>

  <a-form-item v-else-if=" item.view=='sel_search' " :label="item.label">
    <j-search-select-tag
      v-if="item.config==='1'"
      v-model="queryParam[item.field]"
      :placeholder=" '请选择qq'+item.label "
      :dict="getDictCode(item)">
    </j-search-select-tag>
    <j-search-select-tag
      v-else
      v-model="queryParam[item.field]"
      :placeholder=" '请选择'+item.label "
      :dictOptions="dictOptions[item.field]"/>
  </a-form-item>

  <a-form-item v-else-if=" item.view=='sel_user' " :label="item.label">
    <j-select-user-by-dep v-model="queryParam[item.field]" :placeholder=" '请选择'+item.label " trigger-change></j-select-user-by-dep>
  </a-form-item>

  <a-form-item v-else-if=" item.view=='sel_depart' " :label="item.label">
    <j-select-depart v-model="queryParam[item.field]" :placeholder=" '请选择'+item.label " :trigger-change="true"/>
  </a-form-item>

  <a-form-item v-else-if=" item.view=='popup' " :label="item.label">
    <j-popup
      :placeholder=" '请选择'+item.label "
      v-model="queryParam[item.field]"
      :code="item.dictTable"
      :org-fields="item.dictCode"
      :dest-fields="item.dictText"
      :field="getPopupField(item.dictText)"
      :multi="true"/>
  </a-form-item>

  <a-form-item v-else-if=" item.view=='pca' " :label="item.label">
    <j-area-linkage :placeholder=" '请选择'+item.label " v-model="queryParam[item.field]" type="cascader"/>
  </a-form-item>

  <a-form-item v-else-if=" item.view=='checkbox' || item.view=='list_multi'" :label="item.label">
    <j-select-multiple
      v-if="item.config==='1'"
      :placeholder=" '请选择'+item.label "
      v-model="queryParam[item.field]"
      :dictCode="getDictCode(item)">
    </j-select-multiple>
    <j-select-multiple
      v-else
      :placeholder=" '请选择'+item.label "
      :options="dictOptions[item.field]"
      v-model="queryParam[item.field]">
    </j-select-multiple>
  </a-form-item>

  <a-form-item v-else :label="item.label">
    <template v-if="item.view==='integer'">
      <a-input :placeholder=" '请输入'+item.label " v-model="queryParam[item.field]"></a-input>
    </template>
    <template v-else-if="single_mode===item.mode">
      <j-input :placeholder=" '请输入'+item.label " v-model="queryParam[item.field]"></j-input>
    </template>
    <template v-else>
      <j-input :placeholder=" '请输入开始'+item.label " v-model="queryParam[item.field+'_begin']" style="width:calc(50% - 15px);"></j-input>
      <span class="group-query-strig">~</span>
      <j-input :placeholder=" '请输入结束'+item.label " v-model="queryParam[item.field+'_end']" style="width:calc(50% - 15px);"></j-input>
    </template>
  </a-form-item>

</template>

<script>
import JInput from '@comp/jeecg/JInput'

  export default {
    name: 'OnlineQueryFormItem',
    component: {
      JInput
    },
    props:{
      item:{
        type:Object,
        default:()=>{},
        required:true
      },
      dictOptions:{
        type:Object,
        default:()=>{},
        required:true
      },
      queryParam:{
        type:Object,
        default:()=>{},
        required:true
      }
    },
    components:{
      JInput
    },
    data(){
      return {
        single_mode:"single"
      }
    },
    methods:{
      getDictCode(item){
        console.log('item',item)
        if(item.dictTable && item.dictTable.length>0){
          return item.dictTable+','+item.dictText+','+item.dictCode
        }else{
          return item.dictCode
        }
      },
      getPopupField(fields){
        return fields.split(',')[0]
      }
    }

  }
</script>

<style scoped>
  .group-query-strig{
    width:30px;text-align: center;display: inline-block;
  }
</style>