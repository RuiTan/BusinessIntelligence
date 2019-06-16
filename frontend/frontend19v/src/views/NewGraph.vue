<template>
  <div id="new-graph">
    <!-- 搜索和树 在 ../components/SearchTree 下 -->
    <div v-if="type==='one-node'" class="search" style="position:absolute;">
      <el-card class="card">
        <h4 style="margin:0">单节点多跳查询</h4>
        <el-input
          style="width:260px;display:block;margin-bottom:5px;"
          v-model="searchInput"
          placeholder="请输入内容"
          @input="clearTreeData"
        ></el-input>
        <span style="position:absolute;color:grey;font-size:11px">跳数</span>

        <el-input
          style="width:120px;margin-right:20px;margin-top:10px"
          v-model="jumpNo"
          type="number"
        ></el-input>
        <span style="position:absolute;color:grey;font-size:11px">结果限制</span>
        <el-input style="width:120px;" v-model="resLimit" type="number"></el-input>
      </el-card>
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
          <span
            class="one-res"
            style="width: 220px;font-size:14px;padding-top:5px;
            display: inline-block;
            overflow: auto;"
          >{{ node.label }}</span>
          <a :href="data.uri" target="_blank">
            <i
              v-if="node.level>1"
              style="position:absolute;padding-top:5px;right:20px"
              class="el-icon-position"
            ></i>
          </a>
        </span>
      </el-tree>
    </div>

    <div v-if="type==='duo-node-mul'" style="position:absolute;">
      <el-card class="card" style="width:300px">
        <h4 style="margin-top:0">双节点多跳查询</h4>
        <div style="overflow:auto;max-width:100%">
          <el-tag
            v-for="tag in tags"
            @close="handleClose(tag)"
            :key="tag.id"
            style="margin-bottom:5px;max-width:275px:overflow:auto;display:inline-block"
            closable
          >{{tag.label}}</el-tag>
        </div>
        <el-popover placement="right" width="200" trigger="manual" v-model="popVisible">
          <el-input placeholder="请输入内容" @input="clearTreeData" v-model="searchInput2"></el-input>
          <el-button
            slot="reference"
            style="margin-top:10px;margin-bottom:10px;"
            size="small"
            @click="addSearchNode"
          >+ New Node</el-button>
          <el-button
            slot="reference"
            size="mini"
            icon="el-icon-search"
            type="primary"
            @click="getRoutes"
            circle
          ></el-button>
        </el-popover>
        <br>
        <span style="position:absolute;color:grey;font-size:11px">跳数</span>

        <el-input
          style="width:120px;margin-right:20px;margin-top:10px"
          v-model="jumpNo2"
          type="number"
        ></el-input>
        <span style="position:absolute;color:grey;font-size:11px">结果限制</span>
        <el-input style="width:120px;" v-model="resLimit2" type="number"></el-input>
      </el-card>
      <el-tree
        v-loading="treeLoading"
        style="width:300px;max-height:600px;overflow:auto"
        :data="treeData"
        :props="defaultProps"
        ref="tree"
        accordion
        disable
        @node-click="clickTreeNode2"
      >
        <span slot-scope="{ node, data }">
          <span
            class="one-res"
            style="width: 220px;font-size:14px;padding-top:5px;
            display: inline-block;
            overflow: auto;"
          >{{ node.label }}</span>
          <a :href="data.uri" target="_blank">
            <i
              v-if="node.level>1"
              style="position:absolute;padding-top:5px;right:20px"
              class="el-icon-position"
            ></i>
          </a>
        </span>
      </el-tree>
    </div>

    <div v-if="type==='duo-node-short'" style="position:absolute;">
      <el-card class="card" style="width:300px">
        <h4 style="margin-top:0">双节点最短路径查询</h4>
        <div style="overflow:auto;max-width:100%">
          <el-tag
            v-for="tag in tags3"
            @close="handleClose3(tag)"
            :key="tag.id"
            style="margin-bottom:5px;max-width:275px:overflow:auto;display:inline-block"
            closable
          >{{tag.label}}</el-tag>
        </div>
        <el-popover placement="right" width="200" trigger="manual" v-model="popVisible3">
          <el-input placeholder="请输入内容" @input="clearTreeData" v-model="searchInput3"></el-input>
          <el-button
            slot="reference"
            style="margin-top:10px;margin-bottom:10px;"
            size="small"
            @click="addSearchNode3"
          >+ New Node</el-button>
          <el-button
            slot="reference"
            size="mini"
            icon="el-icon-search"
            type="primary"
            @click="getShortestRoute"
            circle
          ></el-button>
        </el-popover>
        <br>
        <h4
          style="margin-top: 0;
        margin-bottom: 5px;
        font-size: 12px;
        color: grey;"
        >最短路径数量</h4>
        <el-switch v-model="shortType" active-text="所有" inactive-color="#13ce66" inactive-text="单条"></el-switch>
      </el-card>
      <el-tree
        v-loading="treeLoading"
        style="width:300px;max-height:600px;overflow:auto"
        :data="treeData"
        :props="defaultProps"
        ref="tree"
        accordion
        disable
        @node-click="clickTreeNode3"
      >
        <span slot-scope="{ node, data }">
          <span
            class="one-res"
            style="width: 220px;font-size:14px;padding-top:5px;
            display: inline-block;
            overflow: auto;"
          >{{ node.label }}</span>
          <a :href="data.uri" target="_blank">
            <i
              v-if="node.level>1"
              style="position:absolute;padding-top:5px;right:20px"
              class="el-icon-position"
            ></i>
          </a>
        </span>
      </el-tree>
    </div>
    <!-- 是否显示属性节点切换按钮 -->

    <!-- <div id="switch-p-node">
      <el-switch
        v-model="propertyNodeSwitch"
        active-color="#409EFF"
        inactive-color="lightgray"
        active-text="显示属性节点"
        inactive-text="隐藏属性节点"
      ></el-switch>
    </div>-->
    <!-- 节点和关系图 -->
    <div v-loading="pageLoading" @mouseover="showLinkLabel">
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
    <!-- <div id="button-group">
      <el-button type="primary" @click="getData">展示</el-button>
      <el-radio-group v-model="radio">
        <el-tooltip class="item" effect="dark" content="查看" placement="top-start">
          <el-radio-button label="1">
            <i class="el-icon-view"></i>
          </el-radio-button>
        </el-tooltip>
        <el-tooltip class="item" effect="dark" content="添加节点" placement="top-start">
          <el-radio-button label="2">
            <i class="el-icon-plus"></i>
          </el-radio-button>
        </el-tooltip>
        <el-tooltip class="item" effect="dark" content="添加关系" placement="top-start">
          <el-radio-button label="3">
            <i class="el-icon-share"></i>
          </el-radio-button>
        </el-tooltip>
        <el-tooltip class="item" effect="dark" content="删除" placement="top-start">
          <el-radio-button label="4">
            <i class="el-icon-delete"></i>
          </el-radio-button>
        </el-tooltip>
        <el-tooltip class="item" effect="dark" content="修改" placement="top-start">
          <el-radio-button label="5">
            <i class="el-icon-edit"></i>
          </el-radio-button>
        </el-tooltip>
      </el-radio-group>
    </div>-->
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
    <el-card class="display-type-selection" style="display: none">
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
import Menu from "./MainPage.vue";
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
  ns0__AcademicQualification:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560424089057" class="icon" style="" viewBox="0 0 1280 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1408" xmlns:xlink="http://www.w3.org/1999/xlink" width="160" height="128"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M672 768L128 424.416v384.48a63.648 63.648 0 0 1 3.104 108.544L192 1024H0l60.896-106.56A63.648 63.648 0 0 1 64 808.896V384h0.032L64 383.968 672 0l608 383.968L672 768z m0 75.68l34.176-21.568L1088 580.96v180.288L672 1024 256 761.248v-180.32l381.824 241.184 34.176 21.568z" p-id="1409"></path></svg>',
  ns0__AcademicDegree:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560423559358" class="icon" style="" viewBox="0 0 1501 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1002" xmlns:xlink="http://www.w3.org/1999/xlink" width="187.625" height="128"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M1403.062529 303.010087L775.870856 23.143025a89.682024 89.682024 0 0 0-73.125688 0L75.553496 303.010087A89.682024 89.682024 0 0 0 22.389508 384.918026c0 32.059699 17.161237 61.251725 44.359332 77.247969 8.244563 9.476766 17.586907 16.802777 25.271377 22.694951 16.982007 12.926937 21.552363 17.766137 21.552363 27.063674 0 9.275133-4.570355 14.114333-21.552363 27.041269-17.900559 13.688663-45.031444 34.389685-45.031443 74.604332 0 40.192244 27.130885 60.893265 45.031443 74.604332 12.299633 9.364748 18.034981 14.495196 20.320159 20.140929a59.638658 59.638658 0 0 0-54.485806 51.797362l-15.794611 123.825268a59.77308 59.77308 0 0 0 59.325006 67.39034h86.007817a59.77308 59.77308 0 0 0 59.325006-67.39034l-15.772207-123.825268a59.728273 59.728273 0 0 0-58.160013-52.133417c-3.114115-35.353044-27.668574-54.553017-44.448948-67.367936-16.9372-12.904533-21.529959-17.743733-21.529958-27.04127 0-9.297537 4.592759-14.114333 21.507555-27.04127 17.96777-13.711066 45.076251-34.412088 45.076251-74.604331 0-0.492881-0.112019-0.896148-0.112019-1.366626l106.238361 47.473447a83.565813 83.565813 0 0 0-0.985763 11.13464v235.14927c0 170.133723 326.332342 188.504759 466.669139 188.504759 140.29199 0 466.646735-18.371037 466.646735-188.504759v-239.719625c0-4.099878-0.403267-8.132544-0.940955-12.120403l192.089352-85.582147a89.682024 89.682024 0 0 0 0.067211-163.883089zM101.384965 891.510564l15.794611-123.825268H171.598171l15.794611 123.825268H101.384965z m1020.735125-186.846885c-15.861822 20.096122-54.754651 43.62001-120.621538 62.461525-11.851559 3.382959-24.576862 6.586689-37.795048 9.566381a29.931348 29.931348 0 0 0 13.106167 58.361647c14.33837-3.20373 28.161455-6.721111 41.110795-10.417722 42.096558-12.030789 77.023932-26.638003 104.199624-43.306359v22.9862c0 54.553017-168.767097 98.777928-376.919904 98.777928-208.197614 0-376.964711-44.22491-376.964711-98.777928v-24.644074c82.131976 51.998995 230.982181 78.838632 378.734604 78.838632 34.479299 0 68.667351-1.433837 101.668005-4.279107a29.908944 29.908944 0 1 0-5.130448-59.593851c-31.27557 2.688444-63.738536 4.05507-96.537557 4.05507-209.833085 0-351.536508-54.060136-378.734604-98.643505v-102.36252l333.479123 149.029434a89.83885 89.83885 0 0 0 73.125688 0.044807l347.279804-154.719974v112.623416z m-383.797841-39.878592L112.116339 384.94043 739.308012 105.073368l627.191673 279.867062-628.177436 279.844657z" p-id="1003"></path></svg>',
  ns0__DirectorRole:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560424312482" class="icon" style="" viewBox="0 0 1129 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2476" xmlns:xlink="http://www.w3.org/1999/xlink" width="141.125" height="128"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M767.732319 789.251237h111.885651a529.31061 529.31061 0 0 0-115.263331-28.147334c-158.328752-23.22155-130.603628-103.863661-131.588785-108.789445 0-12.384827 5.91094-64.879604 5.91094-64.879604a274.57724 274.57724 0 0 0 75.012644-111.041231c38.421111-23.925234 53.057724-27.302914 59.390874-55.028038 0 0 12.6663-104.426608-28.147333-104.426608 0 0-17.029137 2.673997-11.681144-21.814183a661.462342 661.462342 0 0 0 0.985157-128.211105c-2.95547-27.162177 5.347993-104.989555-52.072568-115.826278a259.096207 259.096207 0 0 0-63.3315 0.985156c-18.99945-2.673997-42.221001-52.072567-77.405168-52.072567l-74.027488 11.82188a118.781748 118.781748 0 0 0-49.257834 14.073667 304.976361 304.976361 0 0 0-53.62067 8.162727c-57.420561 10.97746-77.968114 84.442001-80.360638 111.463441s-3.37768 103.722925-1.970313 144.536559c5.91094 24.347444-14.073667 24.347444-14.073667 24.347443-34.62122-2.251787-18.99945 104.567345-18.999451 104.567345a56.294667 56.294667 0 0 0 49.257834 59.813084 211.105003 211.105003 0 0 0 86.271578 103.582188c39.265531 24.206707 3.37768 44.050577 3.940627 75.575591-4.925783-2.53326 17.029137 80.360638-141.721825 103.582188S57.856563 835.835074 28.161126 864.123145s-28.147334 159.595382-28.147334 159.595382h672.439802a106.819131 106.819131 0 0 1-12.24409-49.680044v-76.420011a107.663551 107.663551 0 0 1 107.522815-108.367235z" p-id="2477"></path><path d="M1076.227096 840.197911h-37.435954a219.549203 219.549203 0 0 0-71.353491-223.630566 96.686091 96.686091 0 1 0-39.969213 36.873007A165.506322 165.506322 0 0 1 982.777948 840.197911H756.191912a53.902144 53.902144 0 0 0-53.620671 53.620671v75.997801a53.902144 53.902144 0 0 0 53.620671 53.62067h320.035184a53.902144 53.902144 0 0 0 53.620671-53.62067v-75.997801a53.902144 53.902144 0 0 0-53.620671-53.620671z" p-id="2478"></path></svg>',
  ns0__DirectorShip:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560424312482" class="icon" style="" viewBox="0 0 1129 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2476" xmlns:xlink="http://www.w3.org/1999/xlink" width="141.125" height="128"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M767.732319 789.251237h111.885651a529.31061 529.31061 0 0 0-115.263331-28.147334c-158.328752-23.22155-130.603628-103.863661-131.588785-108.789445 0-12.384827 5.91094-64.879604 5.91094-64.879604a274.57724 274.57724 0 0 0 75.012644-111.041231c38.421111-23.925234 53.057724-27.302914 59.390874-55.028038 0 0 12.6663-104.426608-28.147333-104.426608 0 0-17.029137 2.673997-11.681144-21.814183a661.462342 661.462342 0 0 0 0.985157-128.211105c-2.95547-27.162177 5.347993-104.989555-52.072568-115.826278a259.096207 259.096207 0 0 0-63.3315 0.985156c-18.99945-2.673997-42.221001-52.072567-77.405168-52.072567l-74.027488 11.82188a118.781748 118.781748 0 0 0-49.257834 14.073667 304.976361 304.976361 0 0 0-53.62067 8.162727c-57.420561 10.97746-77.968114 84.442001-80.360638 111.463441s-3.37768 103.722925-1.970313 144.536559c5.91094 24.347444-14.073667 24.347444-14.073667 24.347443-34.62122-2.251787-18.99945 104.567345-18.999451 104.567345a56.294667 56.294667 0 0 0 49.257834 59.813084 211.105003 211.105003 0 0 0 86.271578 103.582188c39.265531 24.206707 3.37768 44.050577 3.940627 75.575591-4.925783-2.53326 17.029137 80.360638-141.721825 103.582188S57.856563 835.835074 28.161126 864.123145s-28.147334 159.595382-28.147334 159.595382h672.439802a106.819131 106.819131 0 0 1-12.24409-49.680044v-76.420011a107.663551 107.663551 0 0 1 107.522815-108.367235z" p-id="2477"></path><path d="M1076.227096 840.197911h-37.435954a219.549203 219.549203 0 0 0-71.353491-223.630566 96.686091 96.686091 0 1 0-39.969213 36.873007A165.506322 165.506322 0 0 1 982.777948 840.197911H756.191912a53.902144 53.902144 0 0 0-53.620671 53.620671v75.997801a53.902144 53.902144 0 0 0 53.620671 53.62067h320.035184a53.902144 53.902144 0 0 0 53.620671-53.62067v-75.997801a53.902144 53.902144 0 0 0-53.620671-53.620671z" p-id="2478"></path></svg>',
  ns0__Major:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560424591822" class="icon" style="" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1241" xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M512 339.2L32 185.6v614.4L512 953.6l480-153.6V185.6z" p-id="1242"></path><path d="M512 224h-6.4L179.2 128c-19.2-6.4-25.6-19.2-19.2-38.4 0-12.8 19.2-19.2 32-12.8l320 96 320-96c12.8-6.4 32 6.4 32 19.2 6.4 12.8-6.4 32-19.2 32L518.4 224H512z" p-id="1243"></path></svg>',
  ns0__OfficeRole:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560426116238" class="icon" style="" viewBox="0 0 1036 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2074" xmlns:xlink="http://www.w3.org/1999/xlink" width="16.1875" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M394.679201 93.856639c0-51.741481 42.115159-93.856639 93.856639-93.856639s93.856639 42.115159 93.856639 93.856639-42.115159 93.856639-93.856639 93.856639-93.856639-42.115159-93.856639-93.856639z m-38.505288 356.173913V291.19624c0-44.521739 31.285546-81.823737 68.587544-81.823737h127.548766c37.301998 0 68.587544 37.301998 68.587544 81.823737v158.834312c0 12.032902-9.626322 21.659224-21.659224 21.659225H377.833137c-12.032902-1.20329-21.659224-9.626322-21.659224-21.659225z m103.482961-208.169212l6.016451 39.708578c0 1.20329 2.40658 2.40658 4.813161 2.40658h36.098708c2.40658 0 4.813161-1.20329 4.813161-2.40658l6.016451-39.708578c0-1.20329-2.40658-2.40658-4.813161-2.406581h-48.13161c-2.40658-1.20329-4.813161 0-4.813161 2.406581z m-9.626322 162.444183c0 2.40658 0 4.813161 2.406581 7.219741l31.285546 26.472386c2.40658 2.40658 6.016451 2.40658 9.626322 0l31.285547-26.472386c2.40658-1.20329 2.40658-3.609871 2.40658-7.219741l-15.642773-102.279671c0-3.609871-3.609871-6.016451-7.219742-6.016451h-31.285546c-3.609871 0-7.219741 2.40658-7.219742 6.016451l-15.642773 102.279671z m246.674501-44.521739h104.686251v69.790834h-21.659224c-7.219741 0-13.236193 6.016451-13.236193 13.236193V457.250294c0 7.219741 6.016451 13.236193 13.236193 13.236192h119.125734c7.219741 0 13.236193-6.016451 13.236193-13.236192v-14.439483c0-7.219741-6.016451-13.236193-13.236193-13.236193h-21.659224v-69.790834h104.686251c14.439483 0 26.472385-12.032902 26.472386-26.472386V149.207991c0-14.439483-12.032902-26.472385-26.472386-26.472386H696.705053c-14.439483 0-26.472385 12.032902-26.472386 26.472386v182.900117c-1.20329 15.642773 10.829612 27.675676 26.472386 27.675676zM40.911868 416.338425h194.93302c7.219741 0 14.439483-6.016451 14.439483-14.439483v-9.626322c0-7.219741-6.016451-14.439483-14.439483-14.439483H40.911868c-7.219741 0-14.439483 6.016451-14.439483 14.439483v9.626322c1.20329 8.423032 7.219741 14.439483 14.439483 14.439483z m0 54.148061h194.93302c7.219741 0 14.439483-6.016451 14.439483-14.439482v-12.032903c0-7.219741-6.016451-14.439483-14.439483-14.439483H40.911868c-7.219741 0-14.439483 6.016451-14.439483 14.439483v12.032903c1.20329 8.423032 7.219741 14.439483 14.439483 14.439482z m0-105.889541h194.93302c7.219741 0 14.439483-6.016451 14.439483-14.439483v-9.626322c0-7.219741-6.016451-14.439483-14.439483-14.439483H40.911868c-7.219741 0-14.439483 6.016451-14.439483 14.439483v9.626322c1.20329 8.423032 7.219741 14.439483 14.439483 14.439483z m979.478261 125.142185H14.439483c-7.219741 0-14.439483 4.813161-14.439483 10.829613v62.571092c0 6.016451 6.016451 10.829612 14.439483 10.829613h1007.153937c7.219741 0 14.439483-4.813161 14.439482-10.829613v-62.571092c-1.20329-6.016451-7.219741-10.829612-15.642773-10.829613z m-43.318449 101.076381H57.757932c-7.219741 0-14.439483 4.813161-14.439483 10.829612v411.525265c0 6.016451 6.016451 10.829612 14.439483 10.829612h918.110458c7.219741 0 14.439483-4.813161 14.439483-10.829612V601.645123c0-6.016451-6.016451-10.829612-13.236193-10.829612z" p-id="2075"></path></svg>',
  ns0__OfficeShip:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560426116238" class="icon" style="" viewBox="0 0 1036 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2074" xmlns:xlink="http://www.w3.org/1999/xlink" width="16.1875" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M394.679201 93.856639c0-51.741481 42.115159-93.856639 93.856639-93.856639s93.856639 42.115159 93.856639 93.856639-42.115159 93.856639-93.856639 93.856639-93.856639-42.115159-93.856639-93.856639z m-38.505288 356.173913V291.19624c0-44.521739 31.285546-81.823737 68.587544-81.823737h127.548766c37.301998 0 68.587544 37.301998 68.587544 81.823737v158.834312c0 12.032902-9.626322 21.659224-21.659224 21.659225H377.833137c-12.032902-1.20329-21.659224-9.626322-21.659224-21.659225z m103.482961-208.169212l6.016451 39.708578c0 1.20329 2.40658 2.40658 4.813161 2.40658h36.098708c2.40658 0 4.813161-1.20329 4.813161-2.40658l6.016451-39.708578c0-1.20329-2.40658-2.40658-4.813161-2.406581h-48.13161c-2.40658-1.20329-4.813161 0-4.813161 2.406581z m-9.626322 162.444183c0 2.40658 0 4.813161 2.406581 7.219741l31.285546 26.472386c2.40658 2.40658 6.016451 2.40658 9.626322 0l31.285547-26.472386c2.40658-1.20329 2.40658-3.609871 2.40658-7.219741l-15.642773-102.279671c0-3.609871-3.609871-6.016451-7.219742-6.016451h-31.285546c-3.609871 0-7.219741 2.40658-7.219742 6.016451l-15.642773 102.279671z m246.674501-44.521739h104.686251v69.790834h-21.659224c-7.219741 0-13.236193 6.016451-13.236193 13.236193V457.250294c0 7.219741 6.016451 13.236193 13.236193 13.236192h119.125734c7.219741 0 13.236193-6.016451 13.236193-13.236192v-14.439483c0-7.219741-6.016451-13.236193-13.236193-13.236193h-21.659224v-69.790834h104.686251c14.439483 0 26.472385-12.032902 26.472386-26.472386V149.207991c0-14.439483-12.032902-26.472385-26.472386-26.472386H696.705053c-14.439483 0-26.472385 12.032902-26.472386 26.472386v182.900117c-1.20329 15.642773 10.829612 27.675676 26.472386 27.675676zM40.911868 416.338425h194.93302c7.219741 0 14.439483-6.016451 14.439483-14.439483v-9.626322c0-7.219741-6.016451-14.439483-14.439483-14.439483H40.911868c-7.219741 0-14.439483 6.016451-14.439483 14.439483v9.626322c1.20329 8.423032 7.219741 14.439483 14.439483 14.439483z m0 54.148061h194.93302c7.219741 0 14.439483-6.016451 14.439483-14.439482v-12.032903c0-7.219741-6.016451-14.439483-14.439483-14.439483H40.911868c-7.219741 0-14.439483 6.016451-14.439483 14.439483v12.032903c1.20329 8.423032 7.219741 14.439483 14.439483 14.439482z m0-105.889541h194.93302c7.219741 0 14.439483-6.016451 14.439483-14.439483v-9.626322c0-7.219741-6.016451-14.439483-14.439483-14.439483H40.911868c-7.219741 0-14.439483 6.016451-14.439483 14.439483v9.626322c1.20329 8.423032 7.219741 14.439483 14.439483 14.439483z m979.478261 125.142185H14.439483c-7.219741 0-14.439483 4.813161-14.439483 10.829613v62.571092c0 6.016451 6.016451 10.829612 14.439483 10.829613h1007.153937c7.219741 0 14.439483-4.813161 14.439482-10.829613v-62.571092c-1.20329-6.016451-7.219741-10.829612-15.642773-10.829613z m-43.318449 101.076381H57.757932c-7.219741 0-14.439483 4.813161-14.439483 10.829612v411.525265c0 6.016451 6.016451 10.829612 14.439483 10.829612h918.110458c7.219741 0 14.439483-4.813161 14.439483-10.829612V601.645123c0-6.016451-6.016451-10.829612-13.236193-10.829612z" p-id="2075"></path></svg>',
  ns0__Person:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560426199385" class="icon" style="" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3533" xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M795.4944 1024h100.5568V532.3776h-56.4736L795.4944 1024z" fill="#3AAEDA" p-id="3534"></path><path d="M839.6032 532.3776h-146.0224l-179.4304 483.0464V1024h281.3696l44.0832-491.6224z" fill="#3AAEDA" p-id="3535"></path><path d="M419.5328 772.2496l-89.088-239.872h-202.496V1024h386.2016v-8.576l-2.1248 5.76-92.4928-248.9344zM512 0c-5.632 0-10.9056 1.2544-16.4608 1.6384-2.3552 0.1792-4.6592 0.5888-6.9888 0.8448C372.48 14.3616 281.5744 111.1552 281.5744 230.4c0 105.0368 70.6816 192.7168 166.784 220.4416 2.3552 0.6656 4.6592 1.5104 7.04 2.1248 1.4592 0.384 2.7904 1.0496 4.2752 1.3824a230.9632 230.9632 0 0 0 52.3264 6.528c0.7168 0 1.408-0.128 2.1504-0.1536V0.1024C513.4336 0.1024 512.7424 0 512 0z" fill="#79C3E0" p-id="3536"></path><path d="M742.4512 230.4256c0-126.5408-102.0416-229.1456-228.2752-230.3232v460.6208a233.1648 233.1648 0 0 0 50.1504-6.3744c101.9648-23.7568 178.1248-114.7136 178.1248-223.9232z" fill="#3AAEDA" p-id="3537"></path><path d="M440.3456 540.1344l-5.4784 61.3376 79.2832 189.7984v-251.136h-73.8048z" fill="#F8FBFF" p-id="3538"></path><path d="M637.3632 540.1344h-123.2128v251.136l3.7376 8.9344 119.4752-260.0704z m-228.1472 0l25.6256 61.3376 5.504-61.3376h-31.1296z" fill="#F8FBFF" p-id="3539"></path><path d="M553.2928 723.1232l32.64-71.04 51.4304-111.9488h-123.2128v104.5504h9.1392v54.4h-9.1392v92.1856l3.7376 8.9344 35.4048-77.0816z" fill="#CCCCCC" p-id="3540"></path><path d="M476.8256 540.1344h-67.6096l25.6512 61.3376 18.048 43.2128h61.2352v-104.5504h-37.3248z m37.3248 251.136v-92.1856h-38.528l38.528 92.1856z" fill="#F8FBFF" p-id="3541"></path><path d="M452.9152 644.6848l22.7072 54.4h38.528v-54.4h-61.2352z" fill="#DD394E" p-id="3542"></path><path d="M514.1504 644.6848h9.1136v54.4h-9.1136v-54.4z" fill="#B8141B" p-id="3543"></path></svg>',
  ns0__TenurelnOrganization:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560426295305" class="icon" style="" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5639" xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M237.71392 465.188571h137.508571v146.285715H237.71392z m207.725714 0h139.702857v146.285715H445.439634z m207.725715 0h137.508571v146.285715H653.165349z m-416.182858 219.428572h138.24v146.285714H237.71392z m207.725715 0h140.434285v146.285714H445.439634z m207.725714 0h137.508571v146.285714H653.165349z m146.285714-474.697143a44.617143 44.617143 0 0 0 42.422857-46.08V46.08a42.422857 42.422857 0 1 0-84.845714 0v116.297143a44.617143 44.617143 0 0 0 42.422857 46.08z m-573.44 0a44.617143 44.617143 0 0 0 42.422857-46.08V46.08A44.617143 44.617143 0 0 0 226.011063 0a44.617143 44.617143 0 0 0-42.422857 46.08v116.297143a44.617143 44.617143 0 0 0 42.422857 46.08z m0 0" p-id="5640"></path><path d="M940.616777 92.03809567h-46.811428v49.005714a98.742857 98.742857 0 0 1-93.622858 102.4 98.742857 98.742857 0 0 1-93.62285699-102.4v-49.005714H320.365349v49.005714a98.742857 98.742857 0 0 1-93.622858 102.4 98.742857 98.742857 0 0 1-93.62285699-102.4v-49.005714H83.382491a55.588571 55.588571 0 0 0-52.66285701 57.782857v795.062857a55.588571 55.588571 0 0 0 52.66285701 57.782857h857.234286a55.588571 55.588571 0 0 0 52.66285699-57.782857V149.82095267a55.588571 55.588571 0 0 0-52.66285699-57.782857z m-40.96 811.154285H124.342491V337.79809567h775.314286z m0 0" p-id="5641"></path></svg>',
  ns4__AssetClass:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560426356616" class="icon" style="" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6718" xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M512 614.4c-7.168 0-16.384-4.096-21.504-9.216L354.304 477.184c-13.312-13.312-14.336-30.72-2.048-43.008 6.144-6.144 13.312-9.216 21.504-9.216s15.36 3.072 21.504 9.216l114.688 109.568 121.856-116.736c6.144-6.144 13.312-9.216 21.504-9.216s15.36 3.072 21.504 9.216c12.288 12.288 12.288 30.72 0 43.008L531.456 604.16c-5.12 7.168-13.312 10.24-19.456 10.24z" fill="#EEAF0A" p-id="6719"></path><path d="M350.208 621.568c-18.432 0-30.72-12.288-30.72-30.72s12.288-30.72 30.72-30.72H675.84c18.432 0 30.72 12.288 30.72 30.72 0 17.408-12.288 30.72-27.648 30.72H350.208zM350.208 763.904c-18.432 0-30.72-12.288-30.72-30.72s12.288-30.72 30.72-30.72H675.84c18.432 0 30.72 12.288 30.72 30.72 0 17.408-12.288 30.72-27.648 30.72H350.208z" fill="#EEAF0A" p-id="6720"></path><path d="M514.048 907.264c-18.432 0-30.72-12.288-30.72-30.72V605.184c0-18.432 12.288-30.72 30.72-30.72s30.72 12.288 30.72 30.72v271.36c0 18.432-12.288 30.72-30.72 30.72z" fill="#EEAF0A" p-id="6721"></path><path d="M253.952 1024c-2.048-1.024-205.824-45.056-191.488-267.264-10.24-181.248 66.56-332.8 227.328-450.56-51.2-38.912-76.8-79.872-76.8-122.88 0-67.584 28.672-100.352 52.224-116.736 25.6-15.36 53.248-23.552 80.896-23.552 15.36 0 31.744 2.048 48.128 7.168C423.936 16.384 464.896 1.024 519.168 1.024h3.072l3.072 1.024c34.816 8.192 60.416 22.528 77.824 40.96 23.552-9.216 45.056-14.336 64.512-14.336 15.36 0 30.72 3.072 45.056 8.192 58.368 19.456 87.04 78.848 96.256 103.424v1.024c17.408 51.2-34.816 108.544-83.968 154.624C804.864 350.208 975.872 495.616 962.56 757.76v19.456c0 153.6-115.712 238.592-193.536 246.784H253.952z m510.976-51.2c51.2-6.144 146.432-70.656 146.432-196.608v-21.504c13.312-266.24-182.272-399.36-241.664-434.176l-30.72-17.408 26.624-23.552c94.208-81.92 98.304-112.64 95.232-122.88-5.12-13.312-26.624-59.392-65.536-72.704h-1.024c-9.216-3.072-17.408-5.12-27.648-5.12-17.408 0-36.864 6.144-61.44 18.432l-22.528 11.264-11.264-22.528c-7.168-14.336-26.624-25.6-55.296-33.792-43.008 1.024-72.704 14.336-92.16 43.008L411.648 112.64l-19.456-8.192c-16.384-7.168-31.744-10.24-46.08-10.24-18.432 0-35.84 5.12-53.248 15.36-18.432 12.288-28.672 37.888-28.672 73.728 0 39.936 45.056 75.776 83.968 101.376l31.744 21.504-31.744 20.48C183.296 437.248 103.424 580.608 113.664 754.688V757.76C102.4 928.768 242.688 968.704 262.144 972.8h502.784z" fill="#EEAF0A" p-id="6722"></path></svg>',
  ns4__Instrument:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560426430784" class="icon" style="" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="7641" xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M861.821517 585.142857H642.934744v-193.964021c0-25.64515-19.865961-45.87231-45.149912-45.87231s-45.149912 20.58836-45.149911 45.87231V585.142857H127.503351c-40.454321 0-73.323457 32.146737-73.323457 72.601058V859.654321h-0.722398c0 39.731922 32.869136 72.239859 73.684656 72.239859h734.318165c40.454321 0 73.684656-31.785538 73.684657-72.239859v-201.910406c0.361199-40.454321-32.869136-72.601058-73.323457-72.601058zM191.074427 804.752028c-25.283951 0-45.87231-20.22716-45.872311-45.87231s20.58836-45.87231 45.872311-45.872311c25.283951 0 45.87231 20.58836 45.87231 45.872311 0 24.922751-20.58836 45.87231-45.87231 45.87231z m165.068077 0c-25.283951 0-45.87231-20.22716-45.87231-45.87231s20.58836-45.87231 45.87231-45.872311c25.283951 0 45.87231 20.58836 45.872311 45.872311 0 24.922751-20.58836 45.87231-45.872311 45.87231z" p-id="7642"></path><path d="M595.617637 215.997178c-113.055379 0-204.077601 91.383422-204.077602 204.438801 0 27.089947 22.033157 48.400705 48.400706 48.400705 26.728748 0 48.400705-22.033157 48.400705-48.400705 0-59.236684 48.039506-106.914991 106.914991-106.914991 59.236684 0 106.914991 47.678307 106.914992 106.914991 0 27.089947 22.033157 48.400705 48.400705 48.400705 26.728748 0 48.400705-22.033157 48.400706-48.400705 1.083598-112.69418-90.299824-204.438801-203.355203-204.438801z" p-id="7643"></path><path d="M595.617637 71.51746c-199.020811 0-360.115697 161.094885-360.115697 360.115697 0 27.089947 22.033157 48.400705 48.400706 48.400705 27.089947 0 48.400705-22.033157 48.400705-48.400705 0-145.202116 117.75097-262.591887 262.591887-262.591887s262.591887 117.75097 262.591887 262.591887c0 27.089947 22.033157 48.400705 48.400706 48.400705 27.089947 0 48.400705-22.033157 48.400705-48.400705 1.444797-199.020811-159.650088-360.115697-358.670899-360.115697z" p-id="7644"></path><path d="M757.79612 674.359083c-18.059965 0-32.507937-14.447972-32.507937-32.507937v-96.079012c0-17.698765 14.447972-32.507937 32.507937-32.507936 17.698765 0 32.507937 14.447972 32.507936 32.507936v96.079012c0 17.698765-14.447972 32.507937-32.507936 32.507937z" p-id="7645"></path></svg>',
  ns4__Quote:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560426502654" class="icon" style="" viewBox="0 0 1316 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="8716" xmlns:xlink="http://www.w3.org/1999/xlink" width="20.5625" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M1120.548571 38.034286c-257.462857 134.582857-386.194286 345.234286-386.194285 626.102857 0 228.205714 84.845714 342.308571 260.388571 342.308571 58.514286 0 108.251429-20.48 152.137143-58.514285 40.96-40.96 61.44-90.697143 61.44-152.137143 0-61.44-20.48-111.177143-61.44-149.211429-43.885714-40.96-96.548571-58.514286-155.062857-58.514286-20.48 0-38.034286 0-52.662857 5.851429 14.628571-160.914286 108.251429-292.571429 277.942857-397.897143L1120.548571 38.034286z m-678.765714 0C184.32 172.617143 58.514286 383.268571 58.514286 664.137143c0 228.205714 84.845714 342.308571 257.462857 342.308571 58.514286 0 111.177143-20.48 152.137143-58.514285 40.96-40.96 61.44-90.697143 61.44-152.137143 0-61.44-20.48-111.177143-61.44-149.211429-40.96-40.96-93.622857-58.514286-152.137143-58.514286-23.405714 0-40.96 0-52.662857 5.851429 11.702857-160.914286 105.325714-292.571429 275.017143-397.897143L441.782857 38.034286z" p-id="8717"></path></svg>',
  ns5__Activity:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560426566232" class="icon" style="" viewBox="0 0 1137 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="9568" xmlns:xlink="http://www.w3.org/1999/xlink" width="17.765625" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M1024 56.086213v79.692478L92.263526 322.467281A113.765137 113.765137 0 0 0 0.853239 434.070881v154.948117c0 54.26597 38.281969 101.023442 91.410287 111.660482l22.297967 4.43684v132.308855c0 54.03844 37.940673 100.625264 90.78458 111.546717l227.359626 46.871237a113.765137 113.765137 0 0 0 136.575048-111.546717v-88.054217l454.719253 91.068993v79.749361a56.882569 56.882569 0 0 0 56.825686 56.939451A56.882569 56.882569 0 0 0 1137.651372 967.060549V56.086213a56.882569 56.882569 0 0 0-113.651372 0zM455.629375 816.833685a56.882569 56.882569 0 0 1-68.315965 55.744917l-113.708255-23.378735a56.996334 56.996334 0 0 1-45.39229-55.8018v-65.471837l227.41651 45.506055v43.4014z m568.370625-43.4014L160.238196 600.395512a56.882569 56.882569 0 0 1-45.676703-55.8018V478.496167a56.882569 56.882569 0 0 1 45.676703-55.8018l863.761804-173.036773v523.774691z" fill="" p-id="9569"></path></svg>',
  ns5__BusinessClassification:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560426617569" class="icon" style="" viewBox="0 0 1077 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="10468" xmlns:xlink="http://www.w3.org/1999/xlink" width="16.828125" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M776.744421 196.284632V37.295158a36.648421 36.648421 0 0 0-35.893895-35.84H35.907368A35.031579 35.031579 0 0 0 0.013474 37.349053v235.789473a36.648421 36.648421 0 0 0 35.893894 35.893895h348.645053A433.421474 433.421474 0 0 1 684.476632 188.631579c33.306947 0 64.080842 2.533053 92.267789 7.706947z m-430.618947 156.294736H35.853474A35.031579 35.031579 0 0 0 0.013474 388.581053v235.843368a36.648421 36.648421 0 0 0 35.893894 35.893895h220.429474c-2.533053-12.826947-2.533053-28.240842-2.533053-43.600842 2.533053-99.974737 35.893895-192.242526 92.26779-264.084211z m-79.494737 348.698948H38.494316a35.031579 35.031579 0 0 0-35.893895 35.893895v235.843368a36.648421 36.648421 0 0 0 35.893895 35.84H515.247158c-125.574737-56.373895-220.429474-169.175579-248.616421-307.577263z m420.378947-471.686737c-215.309474 0-389.658947 171.762526-389.658947 384.538947 0 212.722526 174.349474 384.485053 389.658947 384.485053 215.363368 0 389.658947-171.762526 389.658948-384.485053a388.419368 388.419368 0 0 0-389.658948-384.538947z m171.762527 364.005053v46.133894h-138.401685v61.54779h138.401685v46.133895h-138.401685v112.801684h-61.547789v-112.801684H517.834105v-46.133895h140.988632V639.730526H517.834105v-46.133894h107.681684L494.767158 370.580211h71.787789l120.454737 217.896421 120.508632-217.896421h71.787789l-130.748631 223.016421h110.214737z" p-id="10469"></path></svg>',
  ns5__BusinessSector:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560426617569" class="icon" style="" viewBox="0 0 1077 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="10468" xmlns:xlink="http://www.w3.org/1999/xlink" width="16.828125" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M776.744421 196.284632V37.295158a36.648421 36.648421 0 0 0-35.893895-35.84H35.907368A35.031579 35.031579 0 0 0 0.013474 37.349053v235.789473a36.648421 36.648421 0 0 0 35.893894 35.893895h348.645053A433.421474 433.421474 0 0 1 684.476632 188.631579c33.306947 0 64.080842 2.533053 92.267789 7.706947z m-430.618947 156.294736H35.853474A35.031579 35.031579 0 0 0 0.013474 388.581053v235.843368a36.648421 36.648421 0 0 0 35.893894 35.893895h220.429474c-2.533053-12.826947-2.533053-28.240842-2.533053-43.600842 2.533053-99.974737 35.893895-192.242526 92.26779-264.084211z m-79.494737 348.698948H38.494316a35.031579 35.031579 0 0 0-35.893895 35.893895v235.843368a36.648421 36.648421 0 0 0 35.893895 35.84H515.247158c-125.574737-56.373895-220.429474-169.175579-248.616421-307.577263z m420.378947-471.686737c-215.309474 0-389.658947 171.762526-389.658947 384.538947 0 212.722526 174.349474 384.485053 389.658947 384.485053 215.363368 0 389.658947-171.762526 389.658948-384.485053a388.419368 388.419368 0 0 0-389.658948-384.538947z m171.762527 364.005053v46.133894h-138.401685v61.54779h138.401685v46.133895h-138.401685v112.801684h-61.547789v-112.801684H517.834105v-46.133895h140.988632V639.730526H517.834105v-46.133894h107.681684L494.767158 370.580211h71.787789l120.454737 217.896421 120.508632-217.896421h71.787789l-130.748631 223.016421h110.214737z" p-id="10469"></path></svg>',
  ns5__EconomicSector:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560426695272" class="icon" style="" viewBox="0 0 1260 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="11376" xmlns:xlink="http://www.w3.org/1999/xlink" width="19.6875" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M326.624492 580.954585v433.797907h158.79877V580.954585l-76.5952-72.562216-82.20357 72.562216zM1.055508 1014.752492H161.161846V690.727385L1.055508 837.049108v177.703384z m652.996923-316.053661l-6.333046 316.053661H809.590154l5.608369-414.9248-161.161846 98.871139z m323.221661 316.053661h160.374154V328.404677L977.226831 487.266462v527.501784h0.047261zM895.023262 0l152.213661 146.242954-380.297846 320.023631-258.158277-214.882462L0 580.938831v142.414769l408.828062-329.570462 258.158276 214.850954L1126.305477 222.129231 1260.307692 344.111262V0H895.023262z" p-id="11377"></path></svg>',
  ns5__Industry:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560426785745" class="icon" style="" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="12163" xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M256 0q14.857143 0 25.714286 10.857143t10.857143 25.714286v509.142857l306.285714-245.142857q9.714286-8 22.857143-8 14.857143 0 25.714285 10.857142t10.857143 25.714286v216.571429l306.285715-245.142857q9.714286-8 22.857142-8 14.857143 0 25.714286 10.857142t10.857143 25.714286v658.285714q0 14.857143-10.857143 25.714286t-25.714286 10.857143H36.571429q-14.857143 0-25.714286-10.857143t-10.857143-25.714286V36.571429q0-14.857143 10.857143-25.714286T36.571429 0h219.428571z" p-id="12164"></path></svg>',
  ns5__IndustryGroup:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560426785745" class="icon" style="" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="12163" xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M256 0q14.857143 0 25.714286 10.857143t10.857143 25.714286v509.142857l306.285714-245.142857q9.714286-8 22.857143-8 14.857143 0 25.714285 10.857142t10.857143 25.714286v216.571429l306.285715-245.142857q9.714286-8 22.857142-8 14.857143 0 25.714286 10.857142t10.857143 25.714286v658.285714q0 14.857143-10.857143 25.714286t-25.714286 10.857143H36.571429q-14.857143 0-25.714286-10.857143t-10.857143-25.714286V36.571429q0-14.857143 10.857143-25.714286T36.571429 0h219.428571z" p-id="12164"></path></svg>',
  ns6__Currency:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560424591822" class="icon" style="" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1241" xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M512 339.2L32 185.6v614.4L512 953.6l480-153.6V185.6z" p-id="1242"></path><path d="M512 224h-6.4L179.2 128c-19.2-6.4-25.6-19.2-19.2-38.4 0-12.8 19.2-19.2 32-12.8l320 96 320-96c12.8-6.4 32 6.4 32 19.2 6.4 12.8-6.4 32-19.2 32L518.4 224H512z" p-id="1243"></path></svg>',
  ns6__CurrencySubunit:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560424591822" class="icon" style="" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1241" xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M512 339.2L32 185.6v614.4L512 953.6l480-153.6V185.6z" p-id="1242"></path><path d="M512 224h-6.4L179.2 128c-19.2-6.4-25.6-19.2-19.2-38.4 0-12.8 19.2-19.2 32-12.8l320 96 320-96c12.8-6.4 32 6.4 32 19.2 6.4 12.8-6.4 32-19.2 32L518.4 224H512z" p-id="1243"></path></svg>',
  ns7__Organization:
    '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1560426845361" class="icon" style="" viewBox="0 0 1194 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="13658" xmlns:xlink="http://www.w3.org/1999/xlink" width="18.65625" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M462.421333 258.816h269.824V74.922667H462.421333v183.893333z m5.632 74.922667a80.213333 80.213333 0 0 1-80.384-80.554667V80.554667A80.213333 80.213333 0 0 1 468.053333 0h258.56c44.288 0 80.384 36.181333 80.384 80.554667v172.629333c0 44.373333-36.096 80.554667-80.384 80.554667h-258.56zM597.333333 635.733333a37.461333 37.461333 0 0 1-37.376-37.461333V425.813333a37.461333 37.461333 0 0 1 74.752 0V598.186667A37.461333 37.461333 0 0 1 597.333333 635.733333z m414.122667 0.170667a37.290667 37.290667 0 0 1-28.672-13.397333L816.64 424.362667a37.973333 37.973333 0 0 1 4.693333-52.906667 37.461333 37.461333 0 0 1 52.650667 4.693333l166.058667 198.314667a37.376 37.376 0 0 1-28.586667 61.44z m-827.306667 0a37.376 37.376 0 0 1-28.672-61.525333l166.314667-198.229334a37.290667 37.290667 0 0 1 52.565333-4.693333 37.546667 37.546667 0 0 1 4.608 52.821333L212.821333 622.592a37.290667 37.290667 0 0 1-28.672 13.397333z m-109.397333 313.173333h183.637333V765.184H74.752v183.893333zM80.469333 1024A80.64 80.64 0 0 1 0 943.445333V770.816c0-44.373333 36.096-80.554667 80.469333-80.554667h172.288c44.288 0 80.469333 36.096 80.469334 80.554667v172.629333c0 44.373333-36.181333 80.554667-80.469334 80.554667H80.469333z m425.045334-74.922667h183.637333V765.184H505.514667v183.893333zM511.146667 1024a80.64 80.64 0 0 1-80.384-80.554667V770.816c0-44.373333 36.096-80.554667 80.384-80.554667h172.373333c44.288 0 80.384 36.096 80.384 80.554667v172.629333c0 44.373333-36.096 80.554667-80.384 80.554667H511.146667z m425.130666-74.922667h183.637334V765.184h-183.637334v183.893333z m5.632 74.922667a80.64 80.64 0 0 1-80.469333-80.554667V770.816c0-44.373333 36.096-80.554667 80.469333-80.554667h172.288c44.373333 0 80.469333 36.096 80.469334 80.554667v172.629333c0 44.373333-36.096 80.554667-80.469334 80.554667h-172.288z" p-id="13659"></path></svg>'
};

export default {
  props: {
    type: String
  },
  components: {
    D3Network,
    SearchTree,
    Timeline,
    Diff,
    Menu
  },
  data() {
    return {
      pageLoading: false,
      shortType: 0,
      index: 0,
      tags: [],
      tags3: [],
      popVisible: false,
      popVisible3: false,
      page: 1,
      resLimit: 50,
      jumpNo: 5,
      resLimit2: 50,
      jumpNo2: 5,
      treeLoading: false,
      searchInput: "",
      searchInput2: "",
      searchInput3: "",
      defaultProps: {
        children: "children",
        label: "label"
      },
      treeData: [
        {
          label: "Quote",
          children: []
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
        "ns4__Quote",
        "ns7__Organization",
        "ns4__Instrument",
        "ns4__AssetClass",
        "ns6__Currency",
        "ns6__CurrencySubunit",
        "ns5__Activity",
        "ns5__BusinessSector",
        "ns5__EconomicSector",
        "ns5__Industry",
        "ns0__AcademicQualification",
        "ns0__Person",
        "ns5__IndustryGroup",
        "ns0__Major",
        "ns0__AcademicDegree"
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
        "nodesmasterServer",
        "nodesEnvironment",
        "nodesServiceServer",
        "nodesServiceDatabase",
        "nodesContainerNetwork",
        "nodesContainerStorage",
        "nodesContainer",
        "nodesNamespace",
        "nodesServer"
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
    type(newVal) {
      this.popVisible = false;
      this.popVisible = false;
      this.treeData = [
        {
          label: "Quote",
          children: []
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
      ];
      if (newVal == "one-node") {
        this.page = 1;
      } else if (newVal == "duo-node-mul") {
        this.page = 2;
      } else {
        this.page = 3;
      }
      var historyData = new FormData();
      this.allTimeStamps = [];
      this.showTimeline = true;
      historyData.append("type", this.page);
      axios({
        method: "POST",
        url: "http://10.60.42.201:8080/getHistory",
        data: historyData
      }).then(response => {
        for (let res of response.data) {
          var temp = JSON.parse(res.result);
          if (temp.nodes.length != 0) {
            this.allTimeStamps.push(res);
          }
        }

        this.allTimeStamps = this.allTimeStamps.reverse();

        // var result = this.allTimeStamps[0].result;
        // result = JSON.parse(result)
        // this.showNodeAndLink(result.nodes,result.relations)
      });
    },
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
    showNodeAndLink(nodes, relations) {
      this.nodes = [];
      this.links = [];
      // API GET
      // .get(reqUrl + "/api/getNodesAndLinks")

      // API GET LOC
      nodes.map(x => {
        // console.log(x.properties.label)
        var la = x.properties.label.toString();
        x.svgSym = nodeIcons[la];
      });
      // this.allTimeStamps = response.data.timeList;
      let allNodes = nodes;
      this.normalNodes = [];
      this.nodes = nodes;
      this.links = relations;
      console.log(this.links);
      //属性节点
      this.propertyNodes = allNodes.filter(node => {
        if (this.allPropertyNodeTypes.indexOf(node.properties.label) !== -1) {
          return true;
        } else {
          this.normalNodes.push(node);
          return false;
        }
      });
      // console.log("hello",this.propertyNodes
      this.propertyNodesCopy = JSON.parse(JSON.stringify(this.propertyNodes));
      this.nodes = this.normalNodes.concat(this.propertyNodes);
      this.showTimeline = true;
      let displayProps = document.getElementsByClassName("display-property")[0];
      displayProps.style.right = "-420px";
      displayProps.style.display = "none";
    },
    getShortestRoute() {
      var data = new FormData();
      data.append("sourceType", this.tags3[0].type);
      data.append("targetType", this.tags3[1].type);
      data.append("source", this.tags3[0].id);
      data.append("target", this.tags3[1].id);
      this.pageLoading = true;
      if (this.shortType == false) {
        axios({
          method: "POST",
          url: "http://10.60.42.201:8080/searchMinPath",
          data: data
        }).then(response => {
          console.log("one", response.data);
          this.pageLoading = false;
          if (response.data.nodes.length == 0) {
            this.$message({
              type: "warning",
              message: "当前条件下两节点间无路径"
            });
          }
          this.showNodeAndLink(response.data.nodes, response.data.relations);
        });
      } else {
        axios({
          method: "POST",
          url: "http://10.60.42.201:8080/searchAllMinPaths",
          data: data
        }).then(response => {
          console.log("all", response.data);
          this.pageLoading = false;
          if (response.data.nodes.length == 0) {
            this.$message({
              type: "warning",
              message: "当前条件下两节点间无路径"
            });
          }
          var historyData = new FormData();
          this.allTimeStamps = [];
          this.showTimeline = true;
          historyData.append("type", this.page);
          axios({
            method: "POST",
            url: "http://10.60.42.201:8080/getHistory",
            data: historyData
          }).then(response => {
            for (let res of response.data) {
              var temp = JSON.parse(res.result);
              if (temp.nodes.length != 0) {
                this.allTimeStamps.push(res);
              }
            }

            this.allTimeStamps = this.allTimeStamps.reverse();

            // var result = this.allTimeStamps[0].result;
            // result = JSON.parse(result)
            // this.showNodeAndLink(result.nodes,result.relations)
          });
          this.showNodeAndLink(response.data.nodes, response.data.relations);
        });
      }
    },
    getRoutes() {
      if (this.tags.length < 2) {
        this.$message({
          type: "warning",
          message: "节点数为2时才能进行搜索"
        });
        return;
      }
      this.pageLoading = true;
      var data = new FormData();
      data.append("sourceType", this.tags[0].type);
      data.append("targetType", this.tags[1].type);
      data.append("step", this.jumpNo2);
      data.append("limit", this.resLimit2);
      data.append("sourceId", this.tags[0].id);
      data.append("targetId", this.tags[1].id);
      axios({
        method: "POST",
        url: "http://10.60.42.201:8080/searchByTwoNodes",
        data: data
      }).then(response => {
        console.log(response.data);
        this.pageLoading = false;
        if (response.data.nodes.length == 0) {
          this.$message({
            type: "warning",
            message: "当前条件下两节点间无路径"
          });
        }
        var historyData = new FormData();
        this.allTimeStamps = [];
        this.showTimeline = true;
        historyData.append("type", this.page);
        axios({
          method: "POST",
          url: "http://10.60.42.201:8080/getHistory",
          data: historyData
        }).then(response => {
          for (let res of response.data) {
            var temp = JSON.parse(res.result);
            if (temp.nodes.length != 0) {
              this.allTimeStamps.push(res);
            }
          }

          this.allTimeStamps = this.allTimeStamps.reverse();

          // var result = this.allTimeStamps[0].result;
          // result = JSON.parse(result)
          // this.showNodeAndLink(result.nodes,result.relations)
        });
        this.showNodeAndLink(response.data.nodes, response.data.relations);
      });
    },
    addSearchNode() {
      if (this.popVisible == true) {
        this.popVisible = false;
        return;
      }
      if (this.tags.length >= 2) {
        this.$message({
          type: "warning",
          message: "最多搜索两项"
        });
      } else {
        this.popVisible = true;
      }
    },
    addSearchNode3() {
      if (this.popVisible3 == true) {
        this.popVisible3 = false;
        return;
      }
      if (this.tags3.length >= 2) {
        this.$message({
          type: "warning",
          message: "最多搜索两项"
        });
      } else {
        this.popVisible3 = true;
      }
    },
    handleClose(tag) {
      this.tags.splice(this.tags.indexOf(tag), 1);
    },

    handleClose3(tag) {
      this.tags3.splice(this.tags.indexOf(tag), 1);
    },
    showHistory(value) {
      this.nodes = [];
      this.links = [];
      if (value == "now") {
        return;
      }
      console.log(value);
      var res = JSON.parse(value.result);
      console.log("res", res);
      // this.allTimeStamps = response.data.timeList;
      res.nodes.map(x => {
        // console.log(x.properties.label)
        var la = x.properties.label.toString();
        x.svgSym = nodeIcons[la];
      });
      let allNodes = res.nodes;
      this.normalNodes = [];
      this.nodes = res.nodes;
      this.links = res.relations;
      // 属性节点;
      this.propertyNodes = allNodes.filter(node => {
        if (this.allPropertyNodeTypes.indexOf(node.properties.label) !== -1) {
          return true;
        } else {
          this.normalNodes.push(node);
          return false;
        }
      });

      console.log("hello", this.propertyNodes);

      this.propertyNodesCopy = JSON.parse(JSON.stringify(this.propertyNodes));

      this.nodes = this.normalNodes.concat(this.propertyNodes);

      this.showTimeline = true;
    },
    clearTreeData(value) {
      this.treeData = [
        {
          label: "Quote",
          children: []
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
      ];
    },
    clickTreeNode(data, node) {
      if (node.level == 1) {
        this.searchNode(data, node);
      } else {
        console.log(data);
        this.pageLoading = true;
        var fdata = new FormData();
        fdata.append("type", this.index);
        fdata.append("id", data.id);
        if (this.jumpNo == "") {
          fdata.append("step", -1);
        } else {
          fdata.append("step", this.jumpNo);
        }
        if (this.resLimit == "") {
          fdata.append("limit", -1);
        } else {
          fdata.append("limit", this.resLimit);
        }
        axios({
          method: "POST",
          url: "http://10.60.42.201:8080/searchANode",
          data: fdata
        }).then(response => {
          console.log(response.data);
          this.pageLoading = false;
          var historyData = new FormData();
          this.allTimeStamps = [];
          this.showTimeline = true;
          historyData.append("type", this.page);
          axios({
            method: "POST",
            url: "http://10.60.42.201:8080/getHistory",
            data: historyData
          }).then(response => {
            for (let res of response.data) {
              var temp = JSON.parse(res.result);
              if (temp.nodes.length != 0) {
                this.allTimeStamps.push(res);
              }
            }

            this.allTimeStamps = this.allTimeStamps.reverse();

            // var result = this.allTimeStamps[0].result;
            // result = JSON.parse(result)
            // this.showNodeAndLink(result.nodes,result.relations)
          });
          this.showNodeAndLink(response.data.nodes, response.data.relations);
        });
      }
    },
    searchNode(data, node) {
      console.log("node", node);
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
        if (ele.substring(5) == data.label) {
          this.index = this.allNodeType.indexOf(ele) + 1;
          index = this.index;
          search = ele;
          console.log("index", index);
        }
      }
      if (this.treeData[index - 1].children.length > 0) {
        this.treeLoading = false;
        return;
      }
      var data = new FormData();
      data.append("type", this.index);
      data.append("name", this.searchInput);
      axios({
        method: "POST",
        url: "http://10.60.42.201:8080/selectByTypeAndName",
        data: data
      })
        .then(response => {
          console.log("response", response.data);
          if (response.data.length == 0) {
            this.$message({
              type: "warning",
              message:
                search.substring(4) + "标签下无" + this.searchInput + "项"
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
              message:
                search.substring(4) +
                "标签下有" +
                response.data.length +
                "个" +
                this.searchInput +
                "项"
            });
            this.treeLoading = false;
          }
        })
        .catch(err => {
          console.log(err);
          this.$message({
            type: "warning",
            message: "网络错误"
          });
          this.treeLoading = false;
        });
    },
    clickTreeNode2(data, node) {
      if (node.level == 1) {
        this.searchNode2(data, node);
      } else {
        if (this.tags.length >= 2) {
          this.$message({
            type: "warning",
            message: "最多搜索两项"
          });
        } else {
          var tag = {};
          tag["label"] = data.label;
          tag["id"] = data.id;
          tag["type"] = this.index;
          this.tags.push(tag);
          this.popVisible = false;
        }
      }
    },
    searchNode2(data, node) {
      console.log("node", node);
      if (this.searchInput2 == "") {
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
        if (ele.substring(5) == data.label) {
          this.index = this.allNodeType.indexOf(ele) + 1;
          index = this.index;
          search = ele;
          console.log("index", index);
        }
      }
      if (this.treeData[index - 1].children.length > 0) {
        this.treeLoading = false;
        return;
      }
      var data = new FormData();
      data.append("type", index);
      data.append("name", this.searchInput2);
      axios({
        method: "POST",
        url: "http://10.60.42.201:8080/selectByTypeAndName",
        data: data
      })
        .then(response => {
          console.log("response", response.data);
          if (response.data.length == 0) {
            this.$message({
              type: "warning",
              message:
                search.substring(4) + "标签下无" + this.searchInput2 + "项"
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
              message:
                search.substring(4) +
                "标签下有" +
                response.data.length +
                "个" +
                this.searchInput2 +
                "项"
            });
            this.treeLoading = false;
            this.popVisible = false;
          }
        })
        .catch(err => {
          this.$message({
            type: "warning",
            message: "网络错误"
          });
          this.treeLoading = false;
        });
    },
    clickTreeNode3(data, node) {
      if (node.level == 1) {
        this.searchNode3(data, node);
      } else {
        if (this.tags3.length >= 2) {
          this.$message({
            type: "warning",
            message: "最多搜索两项"
          });
        } else {
          var tag = {};
          tag["label"] = data.label;
          tag["id"] = data.id;
          tag["type"] = this.index;
          this.tags3.push(tag);
          this.popVisible3 = false;
        }
      }
    },
    searchNode3(data, node) {
      console.log("node", node);
      if (this.searchInput3 == "") {
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
        if (ele.substring(5) == data.label) {
          this.index = this.allNodeType.indexOf(ele) + 1;
          index = this.index;
          search = ele;
        }
      }
      if (this.treeData[index - 1].children.length > 0) {
        this.treeLoading = false;
        return;
      }
      var data = new FormData();
      data.append("type", index);
      data.append("name", this.searchInput3);
      axios({
        method: "POST",
        url: "http://10.60.42.201:8080/selectByTypeAndName",
        data: data
      })
        .then(response => {
          console.log("response", response.data);
          if (response.data.length == 0) {
            this.$message({
              type: "warning",
              message:
                search.substring(4) + "标签下无" + this.searchInput3 + "项"
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
              message:
                search.substring(4) +
                "标签下有" +
                response.data.length +
                "个" +
                this.searchInput2 +
                "项"
            });
            this.treeLoading = false;
            this.popVisible3 = false;
          }
        })
        .catch(err => {
          this.$message({
            type: "warning",
            message: "网络错误"
          });
          this.treeLoading = false;
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
            var la = x.properties.label.toString();
            x.svgSym = nodeIcons[la];
          });

          // this.allTimeStamps = response.data.timeList;
          let allNodes = response.data.nodes;
          this.nodes = [];
          this.normalNodes = [];
          this.nodes = response.data.nodes;
          this.links = response.data.relations;
          console.log(this.links);
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
          _this.closeDisplayProps();
          console.log(node);
          let property = node.properties;
          _this.propertyKeys = Object.keys(property);
          for (var key in property) {
            _this.propertyValues.push(property[key].toString());
          }
          let displayProps = document.getElementsByClassName(
            "display-property"
          )[0];
          displayProps.style.display = "block";
          displayProps.style.right = "0px";
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
        if (node.properties.label == element) {
          console.log(element);
          console.log("element");
          node._cssClass = this.styleList[index];
          // node._linkLabelClass = this.linkStyleList[index];
          // console.log(node)
        }
        //  node._cssClass = this.styleList[1];
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
    this.type = "one-node";

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
        this.showNewNodeInfoCard = false;
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
          this.closeDisplayProps();
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
.card {
  background: transparent;
}

.card input {
  border: none;
  border-bottom: #d3d3d3 solid 1px;
  background: transparent;
  border-radius: 0px;
}

a {
  color: unset;
}

.one-res::-webkit-scrollbar {
  display: none;
}

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

#new-graph .Currency,
#new-graph .ns6_CurrencySubunit {
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
#new-graph .nodesmasterServer {
  fill: rgb(20, 190, 233);
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