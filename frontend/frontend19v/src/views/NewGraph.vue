<template>
  <div id="new-graph">
    <!-- 搜索和树 在 ../components/SearchTree 下 -->
    <div class="search" style="position:absolute;">
      <el-input
        style="width:300px;display:block;margin-bottom:5px;"
        v-model="searchInput"
        placeholder="请输入内容"
        @input="clearTreeData"
      ></el-input>
      <el-input
        style="width:145px;display:inline-block; margin-right:10px;"
        v-model="jumpNo"
        placeholder="请输入跳数"
      ></el-input>
      <el-input style="width:145px;display:inline-block" v-model="resLimit" placeholder="结果数量" ></el-input>
      <el-tree
        v-loading="treeLoading"
        style="width:300px;max-height:600px;overflow:auto"
        :data="treeData"
        :props="defaultProps"
        ref="tree"
        accordion
        @node-click="clickTreeNode"
      >
      <span slot-scope="{ node, data }">
            <span class="one-res" style="width: 220px;font-size:14px;padding-top:5px;
            display: inline-block;
            overflow: auto;">
                {{ node.label }} 
            </span>              
            <a :href="data.uri" target="_blank"><i v-if="node.level>1" style="position:absolute;padding-top:5px;right:20px" class="el-icon-position"></i></a>
        </span>
      </el-tree>
    </div>
    <!-- 是否显示属性节点切换按钮 -->
    <div id="switch-p-node">
      <el-switch
        v-model="propertyNodeSwitch"
        active-color="#409EFF"
        inactive-color="lightgray"
        active-text="显示属性节点"
        inactive-text="隐藏属性节点"
      ></el-switch>
    </div>
    <!-- 节点和关系图 -->
    <div @mouseover="showLinkLabel">
      <d3-network
        ref="net"
        :net-nodes="nodes"
        :net-links="links"
        :options="options"
        :selection="selection"
        @node-click="clickNode"
        @link-click="clickLink"
        :class="svgClass"
        :link-cb="lcb"
        :node-cb="ncb"
      />
    </div>
    <!-- 箭头 -->
    <svg height="0">
      <defs>
        <!-- 普通箭头 -->
        <marker
          id="m-end"
          :markerWidth="10"
          :markerHeight="10"
          :refX="nodeSize / 8 + 4.8"
          refY="1.8"
          orient="auto"
          markerUnits="strokeWidth"
        >
          <path d="M0,0 L0,3.6 L3.6,1.8 z"></path>
        </marker>
        <!-- 高亮箭头 -->
        <marker
          id="m-end-selected"
          markerWidth="10"
          markerHeight="10"
          :refX="nodeSize / 8 + 3.3"
          refY="1.8"
          orient="auto"
          markerUnits="strokeWidth"
        >
          <path d="M0,0 L0,3.6 L3.6,1.8 z"></path>
        </marker>
      </defs>
    </svg>
    <!-- 右下角的对节点进行操作的 button -->
    <div id="button-group">
      <el-button type="primary" @click="getData">展示</el-button>
      <el-radio-group v-model="radio">
        <!-- 普通点击 -->
        <el-tooltip class="item" effect="dark" content="查看" placement="top-start">
          <el-radio-button label="1">
            <i class="el-icon-view"></i>
          </el-radio-button>
        </el-tooltip>
        <!-- 添加节点 -->
        <el-tooltip class="item" effect="dark" content="添加节点" placement="top-start">
          <el-radio-button label="2">
            <i class="el-icon-plus"></i>
          </el-radio-button>
        </el-tooltip>
        <!-- 添加关系 -->
        <el-tooltip class="item" effect="dark" content="添加关系" placement="top-start">
          <el-radio-button label="3">
            <i class="el-icon-share"></i>
          </el-radio-button>
        </el-tooltip>
        <!-- 删除 -->
        <el-tooltip class="item" effect="dark" content="删除" placement="top-start">
          <el-radio-button label="4">
            <i class="el-icon-delete"></i>
          </el-radio-button>
        </el-tooltip>
        <!-- 修改 -->
        <el-tooltip class="item" effect="dark" content="修改" placement="top-start">
          <el-radio-button label="5">
            <i class="el-icon-edit"></i>
          </el-radio-button>
        </el-tooltip>
      </el-radio-group>
    </div>
    <!-- 属性卡片 -->
    <el-card class="display-property" style="right: -420px; display: none">
      <div slot="header" class="clearfix">
        <span style="font-weight: bold;font-size:16px;">{{currentNode.name}}</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="closeDisplayProps">关闭</el-button>
      </div>
      <el-form
        ref="propsForm"
        :model="propsForm"
        label-position="left"
        v-if="currentNode.type !== 'environment'"
      >
        <el-form-item v-for="(value, key, index) in currentNode.properties" :key="key" :label="key">
          <el-input
            :placeholder="key"
            v-model="propertyValues[index]"
            readonly="readonly"
            v-if="key !=='uri'"
          ></el-input>
          <a :href="propertyValues[index]" v-if="key ==='uri'" target="blank">
            <el-input :placeholder="key" v-model="propertyValues[index]" readonly="readonly"></el-input>
          </a>
        </el-form-item>
      </el-form>
    </el-card>
    <!-- 添加节点时选择类型 -->
    <el-card class="display-type-selection">
      <div slot="header" class="clearfix">
        <span>添加节点</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="closeDisplayTypeCard">关闭</el-button>
      </div>
      <label>节点类型：</label>
      <el-select v-model="newNodeType" placeholder="请选择节点类型">
        <el-option
          v-for="(item,index) in selectNodeInfo"
          :key="index"
          :label="item.type"
          :value="item.type"
        ></el-option>
      </el-select>
      <br>
      <label>节点&#32;ID：</label>
      <el-input
        v-model="newNodeId"
        placeholder="请输入节点id"
        :minlength="1"
        style="margin-top:10px; width:200px"
      ></el-input>
      <br>
      <label>节点名称：</label>
      <el-input
        v-model="newNodeName"
        placeholder="请输入节点名"
        :minlength="1"
        :maxlength="20"
        style="margin-top:10px; width:200px"
      ></el-input>
      <el-button
        @click="updateTypeHandler"
        style="margin-top: 20px; width: 100%"
        type="primary"
        plain
      >确定</el-button>
    </el-card>
    <!-- 点击空白处新增节点 -->
    <transition name="infocard">
      <el-card v-if="showNewNodeInfoCard" class="new-node-info-card">
        <p>请选择节点类型:</p>
        <el-select v-model="newNodeType" placeholder="请选择">
          <el-option v-for="type in allNodeType" :key="type" :label="type" :value="type"></el-option>
        </el-select>
        <p>请输入节点标识符 (ID)：</p>
        <el-input v-model="newNodeId" placeholder="请输入内容"></el-input>
        <br>
        <br>
        <div>
          <el-button type="primary" @click="addNewNodeByClickBlank">添加节点</el-button>
          <el-button @click="showNewNodeInfoCard = false">取消</el-button>
        </div>
      </el-card>
    </transition>
    <div id="visual"></div>
    <!-- 时间线 -->
    <timeline v-if="showTimeline" :allTimeStamps="allTimeStamps" v-on:click="showHistory"></timeline>
  </div>
</template>

<script>
import D3Network from "../components/vue-d3-network/src/vue-d3-network.vue";
import SearchTree from "../components/SearchTree.vue";
import Timeline from "../components/Timeline";
import Diff from "../components/Diff";
import axios from "axios";
import { setTimeout } from "timers";
import { URLSearchParams, URL } from "url";
// import { console } from 'jsondiffpatch';
// import { console } from 'jsondiffpatch';

HTMLCollection.prototype.forEach = Array.prototype.forEach;

const reqUrl = "http://10.60.38.173:9990/bbs";

Array.prototype.indexOf = function(val) {
  for (var i = 0; i < this.length; i++) {
    if (this[i] == val) return i;
  }
  return -1;
};

Array.prototype.remove = function(val) {
  var index = this.indexOf(val);
  if (index > -1) {
    this.splice(index, 1);
  }
};

var getCoordInDocument = function(e) {
  e = e || window.event;
  var x =
    e.pageX ||
    e.clientX +
      (document.documentElement.scrollLeft || document.body.scrollLeft);
  var y =
    e.pageY ||
    e.clientY + (document.documentElement.scrollTop || document.body.scrollTop);
  return {
    x: x,
    y: y
  };
};

function addEvent(obj, xEvent, fn) {
  if (obj.attachEvent) {
    obj.attachEvent("on" + xEvent, fn);
  } else {
    obj.addEventListener(xEvent, fn, false);
  }
}

var timer = null;

//node的图标svg样式
const nodeIcons = {
  ns0_AcademicQualification: "",
  ns0_AcademicDegree: "",
  ns0_DirectorRole: "",
  ns0_DirectorShip: "",
  ns0_Major: "",
  ns0_OfficeRole: "",
  ns0_OfficeShip: "",
  ns0_Person: "",
  ns0_TenurelnOrganization: "",
  ns4_AssetClass: "",
  ns4_Instrument: "",
  ns4_Quote: "",
  ns5_Activity: "",
  ns5_BusinessClassification: "",
  ns5_BusinessSector: "",
  ns5_EconomicSector: "",
  ns5_Industry: "",
  ns5_IndustryGroup: "",
  ns6_Currency: "",
  ns6_CurrencySubunit: "",
  ns7_Organization: ""
};

export default {
  components: {
    D3Network,
    SearchTree,
    Timeline,
    Diff
  },
  data() {
    return {
      page:1,
      resLimit: "",
      jumpNo: "",
      treeLoading: false,
      searchInput: "",
      defaultProps: {
        children: "children",
        label: "label"
      },
      treeData: [
        {
          label: "Quote",
          children: [],
        },
        {
          label: "Organization",
          children: []
        },
        {
          label: "Instrument",
          children: []
        },
        {
          label: "AssetClass",
          children: []
        },
        {
          label: "Currency",
          children: []
        },
        {
          label: "CurrencySubunit",
          children: []
        },
        {
          label: "Activity",
          children: []
        },
        {
          label: "BusinessSector",
          children: []
        },
        {
          label: "EconomicSector",
          children: []
        },
        {
          label: "Industry",
          children: []
        },
        {
          label: "AcademicQualification",
          children: []
        },
        {
          label: "Person",
          children: []
        },
        {
          label: "IndustryGroup",
          children: []
        },
        {
          label: "Major",
          children: []
        },
        {
          label: "AcademicDegree",
          children: []
        }
      ],
      // Above added by Chengwei Liang
      history: {},
      now: {},
      radio: "1",
      // initialNode: {
      //   name: "Environment",
      //   id: "http://environment/10.60.38.181/environment",
      //   type: "environment",
      //   property: {
      //     masterName: "192.168.199.191", // 192.168.199.191
      //     podName: "sock-shop", // sock-shop
      //     serviceName: "sock-shop", // sock-shop
      //     address: "10.60.38.181", // 10.60.38.181
      //     namespace: "sock-shop" // sock-shop
      //   },
      //   svgSym: nodeIcons["environment"]
      // },
      nodes: [],
      links: [],
      selection: {
        links: {},
        nodes: {}
      },
      allTimeStamps: [], // 不太确定是什么数据格式的 这涉及到排序
      nodeSize: 40,
      fontSize: 14,
      linkWidth: 1,
      canvas: false,
      nodeOperations:
        '<input type="button" value="开机">\t<input type="button" value="关机">',
      notify: {},
      sourceNodeId: 0,
      targetNodeId: 0,
      offset_X: 0,
      offset_Y: 0,
      finCoor: 0,
      staCoor: 0,
      force: 3000,
      moveable: false,
      focusedNode: {},
      allNodeType: [
        "ns4_Quote",
        "ns7_Organization",
        "ns4_Instrument",
        "ns4_AssetClass",
        "ns6_Currency",
        "ns6_CurrencySubunit",
        "ns5_Activity",
        "ns5_BusinessSector",
        "ns5_EconomicSector",
        "ns5_Industry",
        "ns0_AcademicQualification",
        "ns0_Person",
        "ns5_IndustryGroup",
        "ns0_Major",
        "ns0_AcademicDegree"
      ],
      propertyNodes: [], // 属性节点数组
      propertyNodesCopy: [], // 属性节点的深拷贝
      normalNodes: [],
      allPropertyNodeTypes: [
        // 属性节点的类型
        "serviceServer",
        "serviceDatabase",
        "containerNetwork",
        "containerStorage"
      ],
      propertyNodeSwitch: true, // 是否显示属性节点
      // 增加节点时可以选择的类型
      selectNodeInfo: [],
      allLinkType: [
        "ns0_hasHolder",
        "ns0_hasPositionType",
        "ns0_hasQualification",
        "ns0_hasTenureInOrganization",
        "ns0_holdsPosition",
        "ns0_inSubject",
        "ns0_isPositionIn",
        "ns0_isTenureIn",
        "ns0_withDegree",
        "ns1_hasPublicationStatus",
        "ns2_hasGender",
        "ns2_hasURL",
        "ns4_hasAssetClass",
        "ns4_hasInstrumentStatus",
        "ns4_hasOrganizationPrimaryQuote",
        "ns4_hasPrimaryInstrument",
        "ns4_hasPrimaryQuote",
        "ns4_isIssuedBy",
        "ns4_isQuoteOf",
        "ns4_isQuotedIn",
        "ns6_isCurrencyOf",
        "ns6_isCurrencySubunitOf",
        "ns6_isPrimaryCurrencyOf",
        "ns7_hasActivityStatus",
        "ns7_hasHoldingClassification",
        "ns7_hasPrimaryBusinessSector",
        "ns7_hasPrimaryEconomicSector",
        "ns7_hasPrimaryIndustryGroup",
        "ns7_isIncorporatedIn",
        "ns9_isDomiciledIn",
        "skos_broader"
      ],
      styleList: [
        "nodesmasterServer",
        "nodesServer",
        "nodesPod",
        "nodesContainer",
        "nodesService",
        "nodesNamespace",
        "nodesEnvironment",
        "nodesServiceServer",
        "nodesServiceDatabase",
        "nodesContainerNetwork",
        "nodesContainerStorage"
      ],
      linkStyleList: [
        "linkManage",
        "linkDeployed",
        "linkProvides",
        "linkContains",
        "linkSupervises",
        "linkHas",
        "linkProfile"
      ],
      currentNode: {},
      propertyValues: [],
      propertyKeys: [],
      propsForm: {},
      newNodeType: "",
      newNodeName: "",
      newNodeId: "",
      oldNode: {},
      // 用于新增节点时
      properties: {
        masterServer: {
          status: "",
          roles: "",
          age: 5,
          version: ""
        },
        server: {
          status: "",
          roles: "",
          age: 1,
          version: ""
        },
        pod: {
          namespace: "",
          node: "",
          startTime: "2019-4-1",
          labels: "",
          annotations: "",
          status: ""
        },
        container: {
          port: "",
          hostPort: "",
          command: "",
          state: "",
          startedTime: "",
          ready: "",
          restartCount: "",
          environment: "",
          mounts: "",
          args: "",
          lastState: "",
          liveness: "",
          readiness: ""
        },
        service: {
          type: "",
          clusterIP: "",
          externalIP: "",
          ports: "",
          age: 0
        },
        namespace: {
          status: "",
          age: 2
        },
        environment: {
          name: "",
          dataPort: "",
          type: ""
        },
        eventNode: {
          start: "",
          end: "",
          discription: ""
        }
      },
      // 添加节点时点击空白处需要填写的属性
      propertiesToFill: "",
      // 用于新增节点时判断节点间是否有关系
      allNodeRelation: [
        {
          sNodeType: "masterServer",
          tNodeType: "server",
          relName: "manage",
          relType: "manage"
        },
        {
          sNodeType: "pod",
          tNodeType: "server",
          relName: "deployed-in",
          relType: "deployed-in"
        },
        {
          sNodeType: "environment",
          tNodeType: "server",
          relName: "has",
          relType: "has"
        },
        {
          sNodeType: "pod",
          tNodeType: "container",
          relName: "contains",
          relType: "contains"
        },
        {
          sNodeType: "pod",
          tNodeType: "service",
          relName: "provides",
          relType: "provides"
        },
        {
          sNodeType: "namespace",
          tNodeType: "namespace",
          relName: "supervises",
          relType: "supervises"
        },
        {
          sNodeType: "container",
          tNodeType: "containerNetwork",
          relName: "profile",
          relType: "profile"
        },
        {
          sNodeType: "container",
          tNodeType: "containerStorage",
          relName: "profile",
          relType: "profile"
        },
        {
          sNodeType: "service",
          tNodeType: "serviceServer",
          relName: "profile",
          relType: "profile"
        },
        {
          sNodeType: "service",
          tNodeType: "serviceServer",
          relName: "profile",
          relType: "profile"
        }
      ],
      showTimeline: false,
      showNewNodeInfoCard: false,
      svgClass: {
        noselect: true,
        crosshair: false
      }
    };
  },
  computed: {
    // 节点数量
    id() {
      return this.nodes.length;
    },
    options() {
      return {
        force: this.force,
        size: {
          h: 800
          // w: 1240
        },
        offset: {
          x: this.offset_X,
          y: this.offset_Y
        },
        nodeSize: this.nodeSize,
        fontSize: this.fontSize,
        nodeLabels: true,
        linkLabels: true,
        canvas: this.canvas,
        profileLinks: []
      };
    }
  },
  watch: {
    // 监听被选择的 radio
    radio(newVal) {
      // 添加节点 鼠标变成十字
      if (newVal === "2") {
        this.svgClass.crosshair = true;
      } else {
        this.svgClass.crosshair = false;
      }
    },
    // 显示/隐藏属性节点
    // 别写成 key-value 的形式
    propertyNodeSwitch(newVal) {
      // 当不显示属性时
      if (newVal === false) {
        // 不渲染 node 和 label
        this.propertyNodes.forEach(propertNode => {
          this.nodes.remove(propertNode);
        });
        // this.nodes = JSON.parse(JSON.stringify(this.normalNodes))
        // 隐藏 link
        document.getElementsByClassName("profile").forEach(x => {
          x.style.visibility = "hidden";
        });
        // 当显示属性时
      } else {
        // 重新渲染节点和 label
        // this.nodes = this.nodes.concat(this.propertyNodes);
        // this.propertyNodes = JSON.parse(JSON.stringify(this.propertyNodesCopy))
        this.nodes = this.normalNodes.concat(this.propertyNodes);
        // 显示边
        document.getElementsByClassName("profile").forEach(x => {
          x.style.visibility = "visible";
        });
      }
    }
  },
  created() {
    // getData();
  },
  methods: {
    showHistory(value){
      this.nodes = []
      this.links = []
      var res = JSON.parse(value.result)
      console.log("res",res)
      // this.allTimeStamps = response.data.timeList;
          let allNodes = res.nodes;
          this.normalNodes = [];
          this.nodes = res.nodes;
          this.links = res.relations;
          属性节点
          this.propertyNodes = allNodes.filter(node => {
            if (
              this.allPropertyNodeTypes.indexOf(node.properties.label) !== -1
            ) {
              return true;
            } else {
              this.normalNodes.push(node);
              return false;
            }
          });

          console.log("hello",this.propertyNodes)

          this.propertyNodesCopy = JSON.parse(
            JSON.stringify(this.propertyNodes)
          );

          this.nodes = this.normalNodes.concat(this.propertyNodes);

          this.showTimeline = true;
      
    },
    clearTreeData(value){
      this.treeData = [
        {
          label: "Quote",
          children: [],
        },
        {
          label: "Organization",
          children: []
        },
        {
          label: "Instrument",
          children: []
        },
        {
          label: "AssetClass",
          children: []
        },
        {
          label: "Currency",
          children: []
        },
        {
          label: "CurrencySubunit",
          children: []
        },
        {
          label: "Activity",
          children: []
        },
        {
          label: "BusinessSector",
          children: []
        },
        {
          label: "EconomicSector",
          children: []
        },
        {
          label: "Industry",
          children: []
        },
        {
          label: "AcademicQualification",
          children: []
        },
        {
          label: "Person",
          children: []
        },
        {
          label: "IndustryGroup",
          children: []
        },
        {
          label: "Major",
          children: []
        },
        {
          label: "AcademicDegree",
          children: []
        }
      ]
    },
    clickTreeNode(data,node){
      if(node.level == 1){
        this.searchNode(data,node)
      }
      else{
        console.log("leaf",data,node)
      }
    },
    searchNode(data, node) {
      console.log("node",node)
      if (this.searchInput == "") {
        this.$message({
          type: "warning",
          message: "搜索项不能为空"
        });
        return;
      }
      this.treeLoading = true;
      var index = 0;
      var search = "";
      for (let ele of this.allNodeType) {
        if (ele.substring(4) == data.label) {
          index = this.allNodeType.indexOf(ele) + 1;
          search = ele;
        }
      }
      if(this.treeData[index - 1].children.length > 0){
        this.treeLoading = false
        return
      }
      var data = new FormData();
      data.append("type", index);
      data.append("name", this.searchInput);
      axios({
        method: "POST",
        url: "http://218.78.28.138:9900/selectByTypeAndName",
        data: data
      }).then(response => {
        console.log("response",response.data);
        if (response.data.length == 0) {
          this.$message({
            type: "warning",
            message: search.substring(4)+"标签下无"+this.searchInput+"项"
          });
          this.treeLoading = false;
        } else {
          for (let item of response.data) {
            this.treeData[index - 1].children.push({
              label: item.name,
              children: [],
              id: item.id,
              uri: item.uri
            });
          }
          this.$message({
              type: "success",
              message: search.substring(4)+"标签下有"+response.data.length+"个"+this.searchInput+"项"
          });
          this.treeLoading = false;
        }
      });
    },
    getData() {
      this.nodes = [];
      // this.nodes.push(this.initialNode) // 等后端有 env 和其他节点的关系
      this.links = [];
      // var envPropertyValues = new FormData();
      // envPropertyValues.append("masterName", this.propertyValues[0]); // 192.168.199.191
      // envPropertyValues.append("podName", this.propertyValues[1]); // sock-shop
      // envPropertyValues.append("serviceName", this.propertyValues[2]); // sock-shop
      // envPropertyValues.append("address", this.propertyValues[3]); // 10.60.38.181
      // envPropertyValues.append("namespace", this.propertyValues[4]); // sock-shop

      // console.log(envPropertyValues);

      // API POST
      // axios({
      //   method: 'post',
      //   url: reqUrl + '/api/storeEnvironment',
      //   data: envPropertyValues,
      //   config: { headers: {'Content-Type': 'multipart/form-data' }}
      // })
      axios
        // API GET
        // .get(reqUrl + "/api/getNodesAndLinks")

        // API GET LOCAL
        .get("/response.json")
        .then(response => {
          response.data.nodes.map(x => {
            // console.log(x.properties.label)
            x.svgSym = nodeIcons[x.properties.label];
          });

          // this.allTimeStamps = response.data.timeList;
          let allNodes = response.data.nodes;
          this.nodes = [];
          this.normalNodes = [];
          this.nodes = response.data.nodes;
          this.links = response.data.relations;
          //属性节点
          this.propertyNodes = allNodes.filter(node => {
            if (
              this.allPropertyNodeTypes.indexOf(node.properties.label) !== -1
            ) {
              return true;
            } else {
              this.normalNodes.push(node);
              return false;
            }
          });

          // console.log("hello",this.propertyNodes)

          this.propertyNodesCopy = JSON.parse(
            JSON.stringify(this.propertyNodes)
          );

          this.nodes = this.normalNodes.concat(this.propertyNodes);

          this.showTimeline = true;

          // // this.$nextTick(() => {
          // //   this.addDblClickEvent();
          // // });
        })
        .catch(function(error) {
          // handle error
          console.log(error);
        });
      // .then(function() {
      //   // always executed
      // });
      let displayProps = document.getElementsByClassName("display-property")[0];
      displayProps.style.right = "-420px";
      displayProps.style.display = "none";

      //   //测试jsondiffpatch结果是否正确
      //   this.history = {
      //   "linkList": [{
      //       "name": "deployed_in",
      //       "type": "deployed_in",
      //       "tid": "http://server/10.60.38.181/192.168.199.188",
      //       "sid": "http://pods/10.60.38.181/sock-shop/catalogue-db-99cbcbb88-ddvvm"
      //     }, {
      //       "name": "contains",
      //       "type": "contains",
      //       "tid": "http://containers/10.60.38.181/sock-shop/catalogue-db",
      //       "sid": "http://pods/10.60.38.181/sock-shop/catalogue-db-99cbcbb88-ddvvm"
      //     }, {
      //       "name": "provides",
      //       "type": "provides",
      //       "tid": "http://services/10.60.38.181/sock-shop/catalogue-db",
      //       "sid": "http://pods/10.60.38.181/sock-shop/catalogue-db-99cbcbb88-ddvvm"
      //   }]
      // }
      // this.now = {
      //   "linkList": [
      //     {
      //       "name": "provides",
      //       "type": "provides",
      //       "tid": "http://server/unset-base/TestService2",
      //       "sid": "http://pods/10.60.38.181/sock-shop/catalogue-6759fc9bf5-q78f7"
      //     }, {
      //       "name": "deployed_in",
      //       "type": "deployed_in",
      //       "tid": "http://server/10.60.38.181/192.168.199.188",
      //       "sid": "http://pods/10.60.38.181/sock-shop/catalogue-db-99cbcbb88-ddvvm"
      //     }, {
      //       "name": "contains",
      //       "type": "contains",
      //       "tid": "http://containers/10.60.38.181/sock-shop/catalogue-db",
      //       "sid": "http://pods/10.60.38.181/sock-shop/catalogue-db-99cbcbb88-ddvvm"
      //     }, {
      //       "name": "provides",
      //       "type": "provides",
      //       "tid": "http://services/10.60.38.181/sock-shop/catalogue-db",
      //       "sid": "http://pods/10.60.38.181/sock-shop/catalogue-db-99cbcbb88-ddvvm"
      //   }]
      // }

      //         var delta = jsondiffpatch.diff(this.history, this.now)
      //         const diff = {
      //           linkList: delta.linkList
      //         }
      //         jsondiffpatch.formatters.html.hideUnchanged();
      //         const visualdiff = document.getElementById('visual');
      //         visualdiff.innerHTML = jsondiffpatch.formatters.html.format(diff, this.history);
    },
    getDatabyTimeStamp(currentTimeStamp) {
      console.log(currentTimeStamp);
      if (currentTimeStamp === "now") {
        axios
          // API GET
          .get(reqUrl + "/api/getNodesAndLinks")
          // API GET LOCAL
          // .get("/response.json")
          .then(response => {
            console.log(response);
            response.data.nodeList.forEach(x => {
              x.svgSym = nodeIcons[x.type];
            });

            let allNodes = response.data.nodeList;
            this.nodes = [];
            this.normalNodes = [];
            // this.nodes = response.data.nodeList;
            this.links = response.data.linkList;

            this.propertyNodes = allNodes.filter(node => {
              if (this.allPropertyNodeTypes.indexOf(node.type) !== -1) {
                return true;
              } else {
                this.normalNodes.push(node);
                return false;
              }
            });
            this.propertyNodesCopy = JSON.parse(
              JSON.stringify(this.propertyNodes)
            );

            this.nodes = this.normalNodes.concat(this.propertyNodes);

            console.log("可显示");
            this.propertyNodeSwitch = true;
            // this.$nextTick(() => {
            //   this.addDblClickEvent();
            // });
          })
          .catch(function(error) {
            // handle error
            console.log(error);
          });
      } else {
        axios
          .all([
            axios.get(reqUrl + "/api/getAllByTime?time=" + currentTimeStamp),
            axios.get(reqUrl + "/api/getNodesAndLinks")
          ])
          .then(
            axios.spread((response, res) => {
              this.history = response.data;
              this.now = res.data;
              var delta = jsondiffpatch.diff(this.history, this.now);
              //只展示nodelist和linklist
              const diff = {
                linkList: delta.linkList,
                nodeList: delta.nodeList
              };
              //隐藏不变的部分
              jsondiffpatch.formatters.html.hideUnchanged();
              //展示diff
              const visualdiff = document.getElementById("visual");
              visualdiff.innerHTML = jsondiffpatch.formatters.html.format(
                diff,
                response.data
              );

              response.data.nodeList.forEach(x => {
                x.svgSym = nodeIcons[x.type];
              });

              let allNodes = response.data.nodeList;
              this.nodes = [];
              this.normalNodes = [];
              // this.nodes = response.data.nodeList;
              this.links = response.data.linkList;

              this.propertyNodes = allNodes.filter(node => {
                if (this.allPropertyNodeTypes.indexOf(node.type) !== -1) {
                  return true;
                } else {
                  this.normalNodes.push(node);
                  return false;
                }
              });
              this.propertyNodesCopy = JSON.parse(
                JSON.stringify(this.propertyNodes)
              );

              this.nodes = this.normalNodes.concat(this.propertyNodes);

              console.log("可显示");
              this.propertyNodeSwitch = true;
            })
          )
          .catch(error => {
            console.error(error);
          });
      }
    },
    // yyyy-MM-ddThh:mm:ss -> yyyyMMdd hh:mm:ss
    frontTimeFottoEnd(time) {
      let timeArr = time.split("");
      timeArr.splice(4, 1);
      timeArr.splice(6, 1);
      return timeArr.join("");
    },
    // yyyyMMdd hh:mm:ss -> yyyy-MM-dd hh:mm:ss
    endTimeFottoFront(time) {
      let timeArr = time.split("");
      timeArr.splice(4, 0, "-");
      timeArr.splice(7, 0, "-");
      return timeArr.join("");
    },
    clickNode(e, node) {
      clearTimeout(timer);
      let _this = this;
      this.currentNode = node;
      timer = setTimeout(function() {
        //在此写单击事件要执行的代码
        _this.moveable = false;
        if (e.preventDefault) {
          /*FF 和 Chrome*/
          e.preventDefault(); // 阻止默认事件
        }
        // 普通点击
        if (_this.radio === "1") {
          // console.log(document.getElementsByClassName("nodesNamespace"))
          // 显示连接的节点
          _this.displayNodeRelation(node);
          // 如果已经有弹出框则关掉
          if (_this.notify.hasOwnProperty("message")) {
            _this.notify.close();
          }
          // 有「操作」的结点弹出框
          if (node.id === 14) {
            // 弹出操作框 只能插入 html 片段 不能操作 dom
            // 暂时这么写吧 。。感觉之后都不能实现。。。
            _this.notify = _this.$notify({
              title: "请选择对结点的操作：",
              dangerouslyUseHTMLString: true,
              message: _this.nodeOperations,
              offset: 50,
              duration: 0
            });
          }
        }
        // 增加节点
        if (_this.radio === "2") {
          _this.displayOneNode(node);
          // 选择类型
          let typeCard = document.querySelector(".display-type-selection");
          // typeCard.style.display = 'block'
          typeCard.style.right = "0px";
          _this.oldNode = node;
          _this.updateSelectType();
        }
        // 增加边
        if (_this.radio === "3") {
          _this.displayOneNode(node);
          // 节点的 id 必须唯一且从1开始
          if (_this.sourceNodeId) {
            if (node.id !== _this.sourceNodeId) {
              _this.targetNodeId = node.id;
              _this.links.push({
                sid: _this.sourceNodeId,
                tid: _this.targetNodeId
              });
              _this.sourceNodeId = 0;
              _this.targetNodeId = 0;
            }
          } else {
            _this.sourceNodeId = node.id;
          }
        }
        if (_this.radio === "4") {
          // 删除
          let removeLinkList = []; // 要删除的边们
          let removeNodeList = _this.relationalPropertyNodes(node); // 要删除的节点（点击的节点的第一层且是属性节点
          removeNodeList.push(node);
          _this.links.forEach(element => {
            if (node.id == element.sid || node.id == element.tid) {
              removeLinkList.push(element);
            }
          });

          removeLinkList.forEach(link => {
            _this.links.remove(link);
          });

          removeNodeList.forEach(node => {
            _this.nodes.remove(node);
          });
          // console.log(JSON.stringify(removeNodeList))
          // console.log(JSON.stringify(removeLinkList))
          // 删除请求（先删除关系->怕后端出问题
          axios
            .post(reqUrl + "/api/delLinks", removeLinkList)
            .then(response => {
              console.log("删除边的请求成功");
              axios
                .post(reqUrl + "/api/delNodes", removeNodeList)
                .then(response => {
                  console.log("删除节点的请求成功");
                })
                .catch(error => {
                  console.log(error);
                });
            })
            .catch(error => {
              console.error(error);
            });
        }
        // 修改节点名
        if (_this.radio === "5") {
          _this.displayOneNode(node);
          _this
            .$prompt("请输入新的节点名", "修改", {
              confirmButtonText: "确定",
              cancelButtonText: "取消"
            })
            .then(({ value }) => {
              if (value) {
                node.name = value;
                axios
                  .post(reqUrl + "/api/modifyOneNode", node)
                  .then(response => {
                    if (response) {
                      _this.$message({
                        type: "success",
                        message: "修改成功"
                      });
                    }
                  })
                  .catch(error => {
                    console.log(error);
                  });
              }
            })
            .catch(() => {
              _this.$message({
                type: "info",
                message: "取消输入"
              });
            });
        }
      }, 0);
    },
    clickLink(e, link) {
      this.displayOneLink(link);
      // 修改关系名
      if (this.radio === "5") {
        this.$prompt("请输入新的关系名", "修改", {
          confirmButtonText: "确定",
          // inputPattern: /[\w!#$%&'/,
          // inputErrorMessage: '邮箱格式不正确',
          cancelButtonText: "取消"
        })
          .then(({ value }) => {
            // value 不为空
            if (value) {
              link.name = value;
              axios.post(reqUrl + "/api/modifyOneLink", link).then(response => {
                if (response) {
                  this.$message({
                    type: "success",
                    message: "修改成功"
                  });
                }
              });
            }
          })
          .catch(() => {
            this.$message({
              type: "info",
              message: "取消输入"
            });
          });
      }
      if (this.radio === "4") {
        // 删除
        this.links.forEach(element => {
          if (link.sid == element.sid && link.tid == element.tid) {
            this.links.remove(element);
          }
        });
      }
    },
    lcb(link) {
      link._color = "lightgray";
      link._svgAttrs = {
        "stroke-width": this.linkWidth,
        opacity: 1,
        "marker-end": "url(#m-end)"
      };
      this.allLinkType.forEach((element, index, array) => {
        if (link.type == element) {
          link._linkLabelClass = this.linkStyleList[index];
          // console.log(link)
        }
      });
      return link;
    },
    ncb(node) {
      this.allNodeType.forEach((element, index, array) => {
        if (node.type == element) {
          node._cssClass = this.styleList[index];
          // node._linkLabelClass = this.linkStyleList[index];
          // console.log(node)
        }
      });

      // node._cssClass = "nodesInit";
      return node;
    },
    focusNode(node) {
      this.focusedNode = node;
      // 定位
      this.focusNodePosition(node);
      // 高亮
      this.displayOneNode(node);
    },
    displayNodeRelation(node) {
      this.selection = {
        nodes: {},
        links: {}
      };
      // let
      // console.log(this.selection.nodes.node.id)
      this.selection.nodes[node.id] = node;
      for (let link of this.links) {
        if (link.sid === node.id) {
          this.selection.links[link.id] = link;
          for (let node of this.nodes) {
            if (node.id === link.tid) {
              this.selection.nodes[node.id] = node;
            }
          }
        }
        if (link.tid === node.id) {
          this.selection.links[link.id] = link;
          for (let node of this.nodes) {
            if (node.id === link.sid) {
              this.selection.nodes[node.id] = node;
            }
          }
        }
      }
    },
    // 返回它第一层连着的节点且是属性节点
    relationalPropertyNodes(node) {
      let relproNodes = [];
      this.links.forEach(link => {
        if (link.sid === node.id) {
          let anotherNode = this.findNodebyId(link.tid);
          if (this.allPropertyNodeTypes.indexOf(anotherNode.type) !== -1) {
            relproNodes.push(anotherNode);
          }
        } else if (link.tid === node.id) {
          let anotherNode = this.findNodebyId(link.sid);
          if (this.allPropertyNodeTypes.indexOf(anotherNode.type) !== -1) {
            relproNodes.push(anotherNode);
          }
        }
      });
      return relproNodes;
    },
    findNodebyId(id) {
      return this.nodes.find(node => {
        return node.id === id;
      });
    },
    focusNodePosition(node) {
      let netSvg = document.getElementsByClassName("net-svg")[0];
      console.log(document.getElementsByClassName("net-svg"));
      // offset 是与中心的偏差 是一个相对值
      this.offset_X += netSvg.scrollWidth / 2 - node.x;
      this.offset_Y += netSvg.scrollHeight / 2 - node.y - 150;
    },
    displayOneNode(node) {
      this.selection = {
        nodes: {},
        links: {}
      };
      this.selection.nodes[node.id] = node;
    },
    displayOneLink(link) {
      this.selection = {
        nodes: {},
        links: {}
      };
      this.selection.links[link.id] = link;
    },
    closeDisplayProps() {
      let displayProps = document.getElementsByClassName("display-property")[0];
      displayProps.style.right = "-420px";
      displayProps.style.display = "none";
      this.propertyValues = [];
      this.propertyKeys = [];
    },
    closeDisplayTypeCard() {
      let typeCard = document.querySelector(".display-type-selection");
      typeCard.style.right = "-360px";
      // typeCard.style.display = 'none'
      this.newNodeType = "";
      this.oldNode = {};
      this.newNodeName = "";
      this.newNodeId = "";
      this.selectNodeInfo = [];
    },
    updatePropsHandler() {
      this.$confirm("确认要修改属性吗", "修改属性", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          let index = this.nodes.indexOf(this.currentNode);
          var i = 0;
          for (var key in this.currentNode.property) {
            this.nodes[index].property[key] = this.propertyValues[i++];
          }
          this.$message({
            message: "修改成功",
            type: "success"
          });
        })
        .catch(() => {
          this.$message({
            message: "已取消修改属性",
            type: "info"
          });
        });
    },
    // 更新增加节点时的选择列表
    updateSelectType() {
      let temp = {};
      for (let rel of this.allNodeRelation) {
        if (rel.sNodeType == this.oldNode.type) {
          temp = {
            type: rel.tNodeType,
            direction: "target",
            relName: rel.relName,
            relType: rel.relType
          };
          this.selectNodeInfo.push(temp);
        } else if (rel.tNodeType == this.oldNode.type) {
          temp = {
            type: rel.sNodeType,
            direction: "source",
            relName: rel.relName,
            relType: rel.relType
          };
          this.selectNodeInfo.push(temp);
        }
      }
    },
    // 根据两边的节点类型来确定关系方向
    updateTypeHandler() {
      for (let info of this.selectNodeInfo) {
        if (info.type == this.newNodeType) {
          // 如果新节点是 source
          if (info.direction == "source") {
            this.addNewNode(
              this.newNodeId,
              this.oldNode.id,
              info.relName,
              info.relType,
              this.newNodeType,
              this.properties[this.newNodeType],
              nodeIcons[this.newNodeType]
            );
            break;
          } else {
            this.addNewNode(
              this.oldNode.id,
              this.newNodeId,
              info.relName,
              info.relType,
              this.newNodeType,
              this.properties[this.newNodeType],
              nodeIcons[this.newNodeType]
            );
          }
        }
      }
    },
    addNewNode(sid, tid, linkName, linkType, newNodeType, property, svgSym) {
      // 新节点的 id 已经自增过了 这里不必增加
      let newNode = {
        id: this.newNodeId,
        name: this.newNodeName ? this.newNodeName : newNodeType + "-" + this.id,
        type: newNodeType,
        property: property,
        svgSym: svgSym
      };
      this.nodes.push(newNode);

      let newLink = {
        sid: sid,
        tid: tid,
        name: linkName,
        type: linkType
      };
      this.links.push(newLink);

      axios
        .post(reqUrl + "/api/addNewNode", newNode)
        .then(response => {
          console.log(response.data);
          axios
            .post(reqUrl + "/api/addNewLink", newLink)
            .then(response => {
              console.log(response.data);
            })
            .catch(function(error) {
              console.log(error);
            });
        })
        .catch(function(error) {
          console.log(error);
        });

      this.$message({
        message: "添加成功",
        type: "success"
      });
      this.closeDisplayTypeCard();
    },
    // 把事件监听器加在每个节点上台费性能惹
    // 加在画布上就可以 搜 mousedown 可以看到
    // addDblClickEvent() {
    //   let onDblClick = e => {
    //     clearTimeout(timer);
    //     var e = e || window.event;
    //     let property = this.currentNode.property;
    //     this.propertyKeys = Object.keys(property);
    //     for (var key in property) {
    //       this.propertyValues.push(property[key]);
    //     }
    //     let displayProps = document.getElementsByClassName(
    //       "display-property"
    //     )[0];
    //     displayProps.style.display = 'block'
    //     displayProps.style.right = "-20px";
    //   };
    //   let list = document.getElementsByTagName("circle");
    //   for (var i = 0; i < list.length; i++) {
    //     addEvent(list[i], "dblclick", onDblClick);
    //   }
    // },
    showLinkLabel(e) {
      // 功能：hover 上 link 后显示 label
      // 思路：监听鼠标的 mouseover 事件，当鼠标移动到 link 上时获取到 link 的 id，
      //      通过 id 搜索到 label，改变 label 的字体大小
      if (e.target.id.indexOf("link") != -1) {
        let linkid = e.target.id;
        // console.log(document.querySelectorAll('[*|href]:not([href])'))
        let labels = document.querySelectorAll("[*|href]:not([href])");
        for (let label of labels) {
          // console.log(label)
          if (label.href.animVal.indexOf(linkid) != -1) {
            // console.log(label)
            // console.log(label.href.animVal)
            // console.log(linkid)
            // console.log(label.innerHTML)
            // label.attr('font-size', '20px');
            label.setAttribute("style", "font-size:10px;");
            setTimeout(() => {
              label.setAttribute("style", "font-size:0px;");
            }, 1000);
            break;
          }
        }
      }
    },
    addNewNodeByClickBlank() {
      let newNode = {
        id: this.newNodeId,
        name: this.newNodeName
          ? this.newNodeName
          : this.newNodeType + "-" + this.id,
        type: this.newNodeType,
        property: this.properties[this.newNodeType]
      };
      this.nodes.push(newNode);
      this.currentNode = newNode;
      // 显示属性面板
      this.showNewNodeInfoCard = false;
      let property = this.currentNode.property;
      this.propertyKeys = Object.keys(property);
      for (var key in this.propertyKeys) {
        this.propertyValues.push(property[key]);
      }
      let displayProps = document.getElementsByClassName("display-property")[0];
      // displayProps.style.display = 'block'
      displayProps.style.right = "0px";
    }
  },
  mounted() {
    var historyData = new FormData()
    this.showTimeline = true

    historyData.append("type",this.page)
    axios({
      method:'POST',
      url:"http://218.78.28.138:9900/getHistory",
      data:historyData
    }).then(response=>{
      console.log("hi",response.data)
      for(let res of response.data){
        var temp = JSON.parse(res.result)
        if(temp.nodes.length != 0){
          this.allTimeStamps.push(res)
        }
      }
      this.allTimeStamps = this.allTimeStamps.reverse()
      
    })

    var el = document.getElementsByClassName("net-svg")[0];
    el.onmousedown = e => {
      this.staCoor = getCoordInDocument(e);
    };
    el.onmouseup = e => {
      // 添加事件节点
      if (
        this.radio === "2" &&
        e.target.localName !== "circle" &&
        e.target.localName !== "path"
      ) {
        this.showNewNodeInfoCard = true;
      } else {
        // 点击空白处取消高亮
        this.selection = {
          nodes: {},
          links: {}
        };
        // 如果是双击显示属性面板
        // console.log(e)
        if (
          e.detail === 2 &&
          (e.target.localName === "path" || e.target.localName === "circle")
        ) {
          let property = this.currentNode.properties;
          this.propertyKeys = Object.keys(property);
          for (var key in property) {
            this.propertyValues.push(property[key].toString());
          }
          let displayProps = document.getElementsByClassName(
            "display-property"
          )[0];
          displayProps.style.display = "block";
          displayProps.style.right = "0px";
        }
        this.finCoor = getCoordInDocument(e);
        if (this.moveable) {
          this.offset_X += this.finCoor.x - this.staCoor.x;
          this.offset_Y += this.finCoor.y - this.staCoor.y;
        } else {
          this.moveable = true;
        }
        this.staCoor = 0;
        this.finCoor = 0;
      }
    };
    el.onmousemove = e => {
      if (this.moveable) {
        if (this.staCoor) {
          this.finCoor = getCoordInDocument(e);
          this.offset_X += this.finCoor.x - this.staCoor.x;
          this.offset_Y += this.finCoor.y - this.staCoor.y;
          this.staCoor = this.finCoor;
        }
      }
    };

    let onMouseWheel = ev => {
      var ev = ev || window.event;
      var down = true; // 定义一个标志，当滚轮向下滚时，执行一些操作
      down = ev.wheelDelta ? ev.wheelDelta < 0 : ev.detail > 0;
      if (down) {
        if (this.nodeSize > 33.5) {
          this.force = Math.max(0, this.force - 80);
          this.nodeSize = Math.max(0, this.nodeSize - 0.2);
          // this.linkWidth = Math.max(0, this.linkWidth - 0.5);
          this.fontSize = Math.max(0, this.fontSize - 0.1);
        }

        // 这两段代码会影响 namespace 节点的缩放 所以被我注释掉惹（cyl）

        // var list = document.getElementsByClassName("nodesNamespace");
        // for (var i = 0; i < list.length; i++) {
        //   let element = list[i];
        //   this.$set(
        //     element.attributes[0],
        //     "value",
        //     parseFloat(element.attributes[0].value) - 0.5
        //   );
        // }
      } else {
        this.force = this.force + 80;
        this.nodeSize = this.nodeSize + 0.2;
        this.fontSize = this.fontSize + 0.1;

        // var list = document.getElementsByClassName("nodesNamespace");
        // for (var i = 0; i < list.length; i++) {
        //   let element = list[i];
        //   this.$set(
        //     element.attributes[0],
        //     "value",
        //     parseFloat(element.attributes[0].value) + 0.5
        //   );
        // }
      }
      if (ev.preventDefault) {
        /*FF 和 Chrome*/
        ev.preventDefault(); // 阻止默认事件
      }
      return false;
    };

    addEvent(el, "mousewheel", onMouseWheel);
    addEvent(el, "DOMMouseScroll", onMouseWheel);

    //引入jsondiffpatch
    const jsondiff = document.createElement("script");
    jsondiff.type = "text/javascript";
    jsondiff.src =
      "https://cdn.jsdelivr.net/npm/jsondiffpatch/dist/jsondiffpatch.umd.min.js";
    document.body.appendChild(jsondiff);
  }
};
</script>

<style>

a{
  color:unset
}

.one-res::-webkit-scrollbar {display:none}

#button-group {
  position: fixed;
  right: 60px;
  bottom: 40px;
}

.noselect {
  -webkit-touch-callout: none;
  -webkit-user-select: none;
  -khtml-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
}

.crosshair {
  cursor: crosshair;
}

/* 被选中的图标 */
#new-graph .node-svg.selected {
  /* stroke:tomato !important; */
  stroke-width: 20px !important;
  fill: tomato !important;
}

/* 被选中的 link */
#new-graph .link.selected {
  marker-end: url(#m-end-selected);
}

/* 被选中的所有元素 */
#new-graph .selected {
  stroke: tomato !important;
  stroke-width: 4px;
}

#new-graph .nodesInit {
  fill: lightblue;
}

#new-graph .nodesServer {
  fill: darkcyan;
}

#new-graph .nodesPod {
  fill: rgb(7, 244, 188);
}

#new-graph .nodesContainer {
  fill: dimgray;
}

#new-graph .nodesContainerNetwork,
#new-graph .nodesContainerStorage {
  fill: rgb(200, 255, 195);
  r: 12;
}

#new-graph .nodesService {
  fill: cornflowerblue;
}

#new-graph .nodesServiceDatabase,
#new-graph .nodesServiceServer {
  fill: lightgoldenrodyellow;
  r: 12;
}

#new-graph .nodesNamespace {
  fill: darkgoldenrod;
}

#new-graph .linkManage {
  /* color: rgb(8, 113, 241); */
  /* fill: rgb(1, 1, 77); */
  fill: gray;
  text-anchor: middle;
}

#new-graph .linkDeployed {
  fill: rgb(0, 71, 70);
  text-anchor: middle;
}

#new-graph .linkProvides {
  fill: rgb(2, 75, 58);
  text-anchor: middle;
}

#new-graph .linkContains {
  fill: rgb(1, 78, 18);
  text-anchor: middle;
}

#new-graph .linkService {
  fill: rgb(68, 82, 2);
  text-anchor: middle;
}

#new-graph .linkNamespace {
  fill: rgb(76, 2, 78);
  text-anchor: middle;
}

#m-end path {
  fill: lightgray;
  /* z-index: -1; */
}

#m-end-selected {
  fill: tomato;
}

.display-property {
  /* display: none; */
  position: absolute;
  z-index: 10;
  top: 0;
  right: -420px;
  width: 400px;
  height: 83%;
  transition: right linear 0.2s;
  overflow: auto;
}

.display-type-selection {
  /* display: none; */
  position: absolute;
  z-index: 10;
  top: 0;
  right: -360px;
  width: 340px;
  height: 350px;
  transition: right linear 0.2s;
  overflow: auto;
}

.node-svg:hover {
  stroke-width: 30px;
}

#new-graph #switch-p-node {
  background: white;
  border: 1px lightgray solid;
  border-radius: 10px;
  padding: 10px 20px;
  position: fixed;
  bottom: 90px;
  right: 60px;
}

.new-node-info-card {
  position: absolute;
  right: 10px;
  top: 5px;
}

/* 可以设置不同的进入和离开动画 */
/* 设置持续时间和动画函数 */
.infocard-enter-active {
  transition: all 0.3s linear;
}
.infocard-leave-active {
  transition: all 0.3s linear;
}
.infocard-enter, .infocard-leave-to
/* .infocard-leave-active for below version 2.1.8 */ {
  transform: translateX(400px);
  /* opacity: 0; */
}

/* jsonpatchdiff */
.jsondiffpatch-delta {
  font-family: "Bitstream Vera Sans Mono", "DejaVu Sans Mono", Monaco, Courier,
    monospace;
  font-size: 12px;
  margin: 0;
  padding: 0 0 0 12px;
  display: inline-block;
}
.jsondiffpatch-delta pre {
  font-family: "Bitstream Vera Sans Mono", "DejaVu Sans Mono", Monaco, Courier,
    monospace;
  font-size: 12px;
  margin: 0;
  padding: 0;
  display: inline-block;
}
ul.jsondiffpatch-delta {
  list-style-type: none;
  padding: 0 0 0 20px;
  margin: 0;
}
.jsondiffpatch-delta ul {
  list-style-type: none;
  padding: 0 0 0 20px;
  margin: 0;
}
.jsondiffpatch-added .jsondiffpatch-property-name,
.jsondiffpatch-added .jsondiffpatch-value pre,
.jsondiffpatch-modified .jsondiffpatch-right-value pre,
.jsondiffpatch-textdiff-added {
  background: #bbffbb;
}
.jsondiffpatch-deleted .jsondiffpatch-property-name,
.jsondiffpatch-deleted pre,
.jsondiffpatch-modified .jsondiffpatch-left-value pre,
.jsondiffpatch-textdiff-deleted {
  background: #ffbbbb;
  text-decoration: line-through;
}
.jsondiffpatch-unchanged,
.jsondiffpatch-movedestination {
  color: gray;
}
.jsondiffpatch-unchanged,
.jsondiffpatch-movedestination > .jsondiffpatch-value {
  transition: all 0.5s;
  -webkit-transition: all 0.5s;
  overflow-y: hidden;
}
.jsondiffpatch-unchanged-showing .jsondiffpatch-unchanged,
.jsondiffpatch-unchanged-showing
  .jsondiffpatch-movedestination
  > .jsondiffpatch-value {
  max-height: 100px;
}
.jsondiffpatch-unchanged-hidden .jsondiffpatch-unchanged,
.jsondiffpatch-unchanged-hidden
  .jsondiffpatch-movedestination
  > .jsondiffpatch-value {
  max-height: 0;
}
.jsondiffpatch-unchanged-hiding
  .jsondiffpatch-movedestination
  > .jsondiffpatch-value,
.jsondiffpatch-unchanged-hidden
  .jsondiffpatch-movedestination
  > .jsondiffpatch-value {
  display: block;
}
.jsondiffpatch-unchanged-visible .jsondiffpatch-unchanged,
.jsondiffpatch-unchanged-visible
  .jsondiffpatch-movedestination
  > .jsondiffpatch-value {
  max-height: 100px;
}
.jsondiffpatch-unchanged-hiding .jsondiffpatch-unchanged,
.jsondiffpatch-unchanged-hiding
  .jsondiffpatch-movedestination
  > .jsondiffpatch-value {
  max-height: 0;
}
.jsondiffpatch-unchanged-showing .jsondiffpatch-arrow,
.jsondiffpatch-unchanged-hiding .jsondiffpatch-arrow {
  display: none;
}
.jsondiffpatch-value {
  display: inline-block;
}
.jsondiffpatch-property-name {
  display: inline-block;
  padding-right: 5px;
  vertical-align: top;
}
.jsondiffpatch-property-name:after {
  content: ": ";
}
.jsondiffpatch-child-node-type-array > .jsondiffpatch-property-name:after {
  content: ": [";
}
.jsondiffpatch-child-node-type-array:after {
  content: "],";
}
div.jsondiffpatch-child-node-type-array:before {
  content: "[";
}
div.jsondiffpatch-child-node-type-array:after {
  content: "]";
}
.jsondiffpatch-child-node-type-object > .jsondiffpatch-property-name:after {
  content: ": {";
}
.jsondiffpatch-child-node-type-object:after {
  content: "},";
}
div.jsondiffpatch-child-node-type-object:before {
  content: "{";
}
div.jsondiffpatch-child-node-type-object:after {
  content: "}";
}
.jsondiffpatch-value pre:after {
  content: ",";
}
li:last-child > .jsondiffpatch-value pre:after,
.jsondiffpatch-modified > .jsondiffpatch-left-value pre:after {
  content: "";
}
.jsondiffpatch-modified .jsondiffpatch-value {
  display: inline-block;
}
.jsondiffpatch-modified .jsondiffpatch-right-value {
  margin-left: 5px;
}
.jsondiffpatch-moved .jsondiffpatch-value {
  display: none;
}
.jsondiffpatch-moved .jsondiffpatch-moved-destination {
  display: inline-block;
  background: #ffffbb;
  color: #888;
}
.jsondiffpatch-moved .jsondiffpatch-moved-destination:before {
  content: " => ";
}
ul.jsondiffpatch-textdiff {
  padding: 0;
}
.jsondiffpatch-textdiff-location {
  color: #bbb;
  display: inline-block;
  min-width: 60px;
}
.jsondiffpatch-textdiff-line {
  display: inline-block;
}
.jsondiffpatch-textdiff-line-number:after {
  content: ",";
}
.jsondiffpatch-error {
  background: red;
  color: white;
  font-weight: bold;
}
</style>