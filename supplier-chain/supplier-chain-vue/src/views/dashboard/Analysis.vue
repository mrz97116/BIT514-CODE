<template>
  <div>
    <div style="width: 100%;text-align: left;margin: 2px 2px 10px 10px">
      请选择首页样式：
      <a-radio-group v-model="indexStyle">
        <a-radio :value="1">销量统计</a-radio>
<!--        <a-radio :value="2">审核统计</a-radio>-->
<!--        <a-radio :value="3">事务处理</a-radio>-->
      </a-radio-group>
    </div>
    <index-chart v-if="indexStyle==1"></index-chart>
    <index-bdc v-if="indexStyle==2"></index-bdc>
    <index-task v-if="indexStyle==3"></index-task>

  </div>
</template>

<script>
  import IndexChart from './IndexChart'
  import IndexTask from "./IndexTask"
  import IndexBdc from './IndexBdc'

  export default {
    name: "Analysis",
    components: {
      IndexChart,
      IndexTask,
      IndexBdc
    },
    data() {
      return {
        indexStyle:1
      }
    },
    created() {

    },
    methods: {

    }
  }
</script>