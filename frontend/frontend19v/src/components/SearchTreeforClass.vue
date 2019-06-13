
<template>
  <div id="search-tree-for-class">
    <el-input prefix-icon="el-icon-search" placeholder="输入关键字进行过滤" v-model="filterText"></el-input>
    <el-tree
      class="filter-tree"
      :data="data"
      :props="dataProps"
      :filter-node-method="filterNode"
      ref="tree"
      @node-click="nodeClickHandler"
      accordion
    ></el-tree>
  </div>
</template>

<script>
export default {
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    }
  },
  props: ["nodes", "links"],
  computed: {
    data() {
      let data = [
        { name: 0, data: [] },
        { name: 1, data: [] },
        { name: 2, data: [] },
      ];
      let nodes = this.nodes;
      nodes.forEach(node => {
        if (node.type === "node") {
          data[0].data.push(node);
        } else if (node.type === "masterNode") {
          data[1].data.push(node);
        } else {
          data.forEach(ele => {
            if (node.type === ele.name) {
              ele.data.push(node);
            }
          });
        }
      });
      return data;
    }
  },
  data() {
    return {
      filterText: "",
      dataProps: {
        label: "name",
        children: "data"
      }
    };
  },
  methods: {
    filterNode(value, data) {
      if (!value) return true;
      const regExp1 = /^部署于[a-zA-Z0-9\.\-]{1,}的pod$/;
      const regExp1_another = /^部署在[a-zA-Z0-9\.\-]{1,}的pod$/;
      const regExp2 = /namespace为[a-zA-Z0-9\.\-]{1,}的pod/;
      const regExp3 = /namespace为[a-zA-Z0-9\.\-]{1,}的service/;
      const regExp4 = /名为[a-zA-Z0-9\.\-]{1,}的pod含有的container/;
      const regExp5 = /order服务对应的容器/;
      var startIndex = 0;
      var endIndex = 0;
      if (regExp1.test(value) || regExp1_another.test(value)) {
        startIndex = 3;
        endIndex = value.indexOf("的");
        let name = value.substring(startIndex, endIndex);
        let result = this.getLinkRelatedSearchResult(name, "master");
        return result.indexOf(data) !== -1;
      } else if (regExp2.test(value)) {
        startIndex = 10;
        endIndex = value.indexOf("的");
        let name = value.substring(startIndex, endIndex);
        let result = [];
        for (let i = 0; i < this.nodes.length; i++) {
          if (
            this.nodes[i].type === "pod" &&
            this.nodes[i].property.namespace === name
          ) {
            result.push(this.nodes[i]);
          }
        }
        return result.indexOf(data) !== -1;
      } else if (regExp3.test(value)) {
        startIndex = 10;
        endIndex = value.indexOf("的");
        let name = value.substring(startIndex, endIndex);
        let result = [];
        for (let i = 0; i < this.nodes.length; i++) {
          if (
            this.nodes[i].type === "service" &&
            this.nodes[i].namespace === name
          ) {
            result.push(this.nodes[i]);
          }
        }
        return result.indexOf(data) !== -1;
      } else if (regExp4.test(value)) {
        startIndex = 2;
        endIndex = value.indexOf("的");
        let name = value.substring(startIndex, endIndex);
        let result = this.getLinkRelatedSearchResult(name, "contains");
        return result.indexOf(data) !== -1;
      } else if (regExp5.test(value)) {
        let orderServiceId = ''
        let podId = ''
        let containerId = ''
        let result = []
        for (let i = 0; i < this.nodes.length; i++) {
          if (this.nodes[i].type === 'service' && this.nodes[i].name === 'orders') {
            orderServiceId = this.nodes[i].id
          }
        }
        for (let i = 0; i < this.links.length; i++) {
          if (this.links[i].tid === orderServiceId) {
            podId = this.links[i].sid
          }
        }
        for (let i = 0; i < this.links.length; i++) {
          if (this.links[i].type === 'contains' && this.links[i].sid === podId) {
            containerId = this.links[i].tid
          }
        }
        for (let i = 0; i < this.nodes.length; i++) {
          if (this.nodes[i].id === containerId) {
            result.push(this.nodes[i]);
          }
        }
        return result.indexOf(data) !== -1;
      }  else {
        return data.name.indexOf(value) !== -1;
      }
    },
    getLinkRelatedSearchResult(name, linkType) {
      let tid = "";
      for (let i = 0; i < this.nodes.length; i++) {
        if (this.nodes[i].name === name) {
          tid = this.nodes[i].id;
        }
      }
      let resultLinks = [];
      for (let i = 0; i < this.links.length; i++) {
        if (this.links[i].type !== linkType && this.links[i].tid === tid) {
          resultLinks.push(this.links[i]);
        }
      }
      let result = [];
      resultLinks.forEach(ele => {
        for (let i = 0; i < this.nodes.length; i++) {
          if (this.nodes[i].id === ele.sid) {
            result.push(this.nodes[i]);
          }
        }
      });
      return result;
    },
    nodeClickHandler(data, node) {
      if (data.hasOwnProperty("data")) {
        // 不传给父亲
      } else {
        this.$emit("focusNode", data);
      }
    }
  }
};
</script>

<style scope>
#search-tree-for-class {
  /* margin: 5px; */
  max-height: 88%;
  overflow: auto;
  position: absolute;
  width: 300px;
  float: right;
}

.el-tree-node__children {
  overflow: auto !important;
}

.el-tree {
  background-color: rgba(255, 255, 255, 0.582);
  border: 1px solid lightgray;
  margin-top: 5px;
  border-radius: 5px;

}
</style>

