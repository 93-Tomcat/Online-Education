<template>
  <div class="app-container">
    <el-input v-model="filterText" placeholder="Filter keyword" style="margin-bottom:30px;" />

    <el-tree
      ref="subjectTree"
      :data="subjectList"
      :props="defaultProps"
      :filter-node-method="filterNode"
      class="filter-tree"
      default-expand-all
    />

  </div>
</template>

<script>
import subject from '@/api/edu/subject'
export default {

  data() {
    return {
      filterText: '', // 过滤文本
      subjectList: [], // 数据列表
      defaultProps: {// 属性列表数据属性的key
        children: 'children',
        label: 'title'
      }
    }
  },

  // 监听 filterText的变化
  watch: {
    filterText(val) {
      this.$refs.subjectTree.filter(val)// 调用tree的filter方法
    }
  },

  created() {
    this.fetchNodeList()
  },

  methods: {
    // 获取远程数据
    fetchNodeList() {
      subject.getNestedTreeList().then(response => {
        if (response.success === true) {
          this.subjectList = response.data.items
        }
      })
    },

    // 过滤节点
    filterNode(value, data) {
      if (!value) return true
      return data.title.toLowerCase().indexOf(value.toLowerCase()) !== -1
    }
  }
}
</script>

