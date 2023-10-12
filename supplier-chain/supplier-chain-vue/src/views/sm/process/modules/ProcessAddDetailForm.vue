<template>
  <div>
    <a-card class="card" title="加工单" :bordered="false">
      <a-form :form= "mainForm">
        <a-row>
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item label="委托人">
              <a-input placeholder="请输入委托人" v-decorator="[ 'name']" />
            </a-form-item>
          </a-col>
          <a-col :xl="{span: 7, offset: 1}" :lg="{span: 8}" :md="{span: 12}" :sm="24">
            <a-form-item label="单位">
              <a-input placeholder="请输入单位" v-decorator="[ 'description']" />
            </a-form-item>
          </a-col>
          <a-col :xl="{span: 7, offset: 1}" :lg="{span: 8}" :md="{span: 16}" :sm="24">
          <a-form-item label="备注">
            <a-textarea placeholder="请输入备注"  v-decorator="[ 'remark' ]" />
          </a-form-item>
          </a-col>
        </a-row>
        <a-table
          ref="table"
          size="middle"
          :scroll="{x:true}"
          bordered
          rowKey="id"
          :pagination="false"
          :columns="column"
          :dataSource="selectionRecord"
          class="j-table-force-nowrap"
        >
        </a-table>
      </a-form>
    </a-card>
    <a-card  title="加工明细">
      <form :autoFormCreate="(form) => this.form = form">
        <a-table
          :columns="columns"
          :dataSource="data"
          :pagination="false"
        >
          <template v-for="(col, i) in ['len', 'cakeNum','cakeWeight','weight','num']" :slot="col" slot-scope="text, record, index">
            <a-input
              :key="col"
              v-if="record.editable"
              style="margin: -5px 0"
              :value="text"
              :placeholder="columns[i].title"
              @change="e => handleChange(e.target.value, record.key, col, record)"
            />
            <template v-else>{{ text }}</template>
          </template>
          <template slot="operation" slot-scope="text, record, index">
            <template v-if="record.editable">
                <span v-if="record.isNew">
                  <a @click="saveRow(record.key)">添加</a>
                  <a-divider type="vertical" />
                  <a-popconfirm title="是否要删除此行？" @confirm="remove(record.key)">
                    <a>删除</a>
                  </a-popconfirm>
                </span>
              <span v-else>
                  <a @click="saveRow(record.key)">保存</a>
                  <a-divider type="vertical" />
                  <a @click="cancel(record.key)">取消</a>
                </span>
            </template>
            <span v-else>
                <a @click="toggle(record.key)">编辑</a>
                <a-divider type="vertical" />
                <a-popconfirm title="是否要删除此行？" @confirm="remove(record.key)">
                  <a>删除</a>
                </a-popconfirm>
              </span>
          </template>
        </a-table>
      </form>
    </a-card>
  </div>
</template>

<script>
    import ARow from "ant-design-vue/es/grid/Row";
    import ACol from "ant-design-vue/es/grid/Col";
    import { math } from '@/views/utils/math.js';
    import { postAction } from '@api/manage';
    export default {
      name: "ProcessAddDetailForm",
      components: {ACol, ARow},
      props: {
        selectionRecord: {
          type: Array,
          default: function() {
            return []
          },
          required: false
        }
      },
      created () {
         for(let i;i<8;i++) {
           this.newMember();
         }
      },
      watch: {
        "selectionRecord":function() {
          this.dataSource = this.selectionRecord;
        }
      },
      data() {
          return {
          // table
            columns: [
              {
                title: '加工长度',
                dataIndex: 'len',
                key: 'len',
                width: '20%',
                scopedSlots: { customRender: 'len' }
              },
              {
                title: '块数',
                dataIndex: 'cakeNum',
                key: 'cakeNum',
                width: '20%',
                scopedSlots: { customRender: 'cakeNum' }
              },
              {
                title: '块重',
                dataIndex: 'cakeWeight',
                key: 'cakeWeight',
                width: '20%',
                scopedSlots: { customRender: 'cakeWeight' }
              },
              {
                title: '重量',
                dataIndex: 'weight',
                key: 'weight',
                width: '20%',
                scopedSlots: { customRender: 'weight' }
              },
              {
                title: '余卷',
                dataIndex: 'num',
                key: 'num',
                width: '20%',
                scopedSlots: { customRender: 'num' }
              },
            ],
            column:[
              {
                title:'品名中文',
                align:"center",
                dataIndex: 'prodCname'
              },
              {
                title:'产品大类',
                align:"center",
                dataIndex: 'prodClassCode_dictText'
              },
              {
                title:'牌号',
                align:"center",
                dataIndex: 'sgSign'
              },
              {
                title:'厚度',
                align:"center",
                dataIndex: 'matThick'
              },
              {
                title:'宽度',
                align:"center",
                dataIndex: 'matWidth'
              },
              {
                title:'长度',
                align:"center",
                dataIndex: 'matLen'
              },
              {
                title:'重量',
                align:"center",
                dataIndex: 'weight'
              },
              {
                title:'材料号',
                align:"center",
                dataIndex: 'matNo'
              },
            ],
            mainForm: this.$form.createForm(this),
            data: [
              {
                key: 1,
                len: 0,
                cakeNum: 0,
                cakeWeight: 0,
                weight: 0,
                num: 0,
                editable: true,
                isNew: true
              },
              {
                key: 2,
                len: 0,
                cakeNum: 0,
                cakeWeight: 0,
                weight: 0,
                num: 0,
                editable: true,
                isNew: true
              },
              {
                key: 3,
                len: 0,
                cakeNum: 0,
                cakeWeight: 0,
                weight: 0,
                num: 0,
                editable: true,
                isNew: true
              },
              {
                key: 4,
                len: 0,
                cakeNum: 0,
                cakeWeight: 0,
                weight: 0,
                num: 0,
                editable: true,
                isNew: true
              },
              {
                key: 5,
                len: 0,
                cakeNum: 0,
                cakeWeight: 0,
                weight: 0,
                num: 0,
                editable: true,
                isNew: true
              },
              {
                key: 6,
                len: 0,
                cakeNum: 0,
                cakeWeight: 0,
                weight: 0,
                num: 0,
                editable: true,
                isNew: true
              },
              {
                key: 7,
                len: 0,
                cakeNum: 0,
                cakeWeight: 0,
                weight: 0,
                num: 0,
                editable: true,
                isNew: true
              },
              {
                key: 8,
                len: 0,
                cakeNum: '余下',
                cakeWeight: 0,
                weight: 0,
                num: 0,
                editable: true,
                isNew: true
              },

            ],
            dataSource: [],
            url: {
              process: '/sm/process/addYrm'
            }
        }
      },
      methods: {
        newMember () {
          this.data.push({
            key: this.num += 1,
            len: '',
            cakeNum: '',
            cakeWeight: '',
            weight: '',
            editable: true,
            isNew: true
          })
        },
        remove (key) {
          const newData = this.data.filter(item => item.key !== key)
          this.data = newData
        },
        saveRow (key) {
          let target = this.data.filter(item => item.key === key)[0]
          target.editable = false
          target.isNew = false
        },
        toggle (key) {
          let target = this.data.filter(item => item.key === key)[0]
          target.editable = !target.editable
        },
        getRowByKey (key, newData) {
          const data = this.data
          return (newData || data).filter(item => item.key === key)[0]
        },
        cancel (key) {
          let target = this.data.filter(item => item.key === key)[0]
          this.data = [
            {
              key: 1,
              len: 0,
              cakeNum: 0,
              cakeWeight: 0,
              weight: 0,
              num: 0,
              editable: true,
              isNew: true
            },
            {
              key: 2,
              len: 0,
              cakeNum: 0,
              cakeWeight: 0,
              weight: 0,
              num: 0,
              editable: true,
              isNew: true
            },
            {
              key: 3,
              len: 0,
              cakeNum: 0,
              cakeWeight: 0,
              weight: 0,
              num: 0,
              editable: true,
              isNew: true
            },
            {
              key: 4,
              len: 0,
              cakeNum: 0,
              cakeWeight: 0,
              weight: 0,
              num: 0,
              editable: true,
              isNew: true
            },
            {
              key: 5,
              len: 0,
              cakeNum: 0,
              cakeWeight: 0,
              weight: 0,
              num: 0,
              editable: true,
              isNew: true
            },
            {
              key: 6,
              len: 0,
              cakeNum: 0,
              cakeWeight: 0,
              weight: 0,
              num: 0,
              editable: true,
              isNew: true
            },
            {
              key: 7,
              len: 0,
              cakeNum: 0,
              cakeWeight: 0,
              weight: 0,
              num: 0,
              editable: true,
              isNew: true
            },
            {
              key: 8,
              len: 0,
              cakeNum: '余下',
              cakeWeight: 0,
              weight: 0,
              num: 0,
              editable: true,
              isNew: true
            },
          ];
          this.mainForm.resetFields();
          this.$nextTick(() => {
            this.mainForm.setFieldsValue({})
          })
          target.editable = false;
        },
        handleChange (value, key, column, record) {
          const newData = [...this.data]
          const target = newData.filter(item => key === item.key)[0]
          if (target) {
            target[column] = value
            this.data = newData
          }
          let mat = this.selectionRecord[0];
          let cakeWeight =  (mat.matThick * (mat.matWidth + 10) * record.len * 7.85 /1000000).toFixed(3)
          let weight = 0;
          if(record.cakeNum !== '余下') {
            weight = (record.cakeNum * cakeWeight).toFixed(3)
          }
          let availableNum = 0;
          if(record.cakeNum === '余下') {
            let numWeight = mat.weight*1000;
            let totalWeight = 0.0;
            this.data.forEach(item =>{
              if(item.key !== 8) {
                totalWeight = totalWeight + item.weight;
              }
            })
            availableNum = ((numWeight - totalWeight)/cakeWeight).toFixed(3)
            weight = math.subtract(numWeight, totalWeight).toFixed(3);
          }
          this.data.forEach(item => {
            if(item.key === key) {
              item.cakeWeight = cakeWeight;
              item.weight = parseFloat(weight);
              item.num = availableNum;
            }
          })

        },
        submit() {
          this.mainForm.validateFields((err, values) => {
              let listData = this.data.filter(x => x.weight>0);
              listData[listData.length - 1].cakeNum = 0;
              let params = {
                customerId: values.name,
                salesManId: values.description,
                remarks: values.remark,
                processDtoList: listData,
                inventoryId: this.selectionRecord[0].id
              }
              postAction(this.url.process, params).then(res=>{
                if(res.success) {
                  this.$message.success("加工成功")
                }
                if(res.code == 500){
                  this.$message.warning(res.message);
                }
              })
          })


        }
      }
    }

</script>

<style scoped>
  .card{
    margin-bottom: 24px;
  }
</style>