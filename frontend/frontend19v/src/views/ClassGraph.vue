<template>
  <div id="class-graph">

    <transition name="flow-fade" >
    
   
    <el-card class="flow-graph-container" v-if="isFlowVisibel">
      <div slot="header" class="clearfix">
        <span>流程图</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="isFlowVisibel = false">关闭</el-button>
      </div>
      <div v-html="flowGraphSvg">

      </div>
    </el-card>
    </transition>
    <!-- <div class=""  ></div> -->
    <!-- 搜索和树 在 ../components/SearchTree 下 -->
    <search-tree v-on:focusNode="focusNode" :nodes="nodes" :links="links"></search-tree>
    <!-- 是否显示属性节点切换按钮 -->
    <!-- <div id="switch-p-node">
      <el-switch
        v-model="propertyNodeSwitch"
        active-color="#409EFF"
        inactive-color="lightgray"
        active-text="显示属性节点"
        inactive-text="隐藏属性节点">
      </el-switch>
    </div>-->
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
        class="noselect"
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
    <!-- <el-card class="display-property">
      <div slot="header" class="clearfix">
        <span style="font-weight: bold;font-size:16px;">{{currentNode.name}}</span>
        <span style="color:#555;">（{{currentNode.type}}）</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="closeDisplayProps">关闭</el-button>
      </div>
      <el-form ref="propsForm" :model="propsForm" label-position="left">
        <el-form-item v-for="(value, key, index) in currentNode.property" :key="key" :label="key">
          <el-input :placeholder="key" v-model="propertyValues[index]"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button
            @click="getData"
            style="width:100%"
            type="primary"
            plain
            v-if="currentNode.type === 'environment'"
          >导入</el-button>
          <el-button @click="updatePropsHandler" style="width:100%" type="primary" plain v-else>确定</el-button>
        </el-form-item>
      </el-form>
    </el-card>-->
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
    <!-- 时间线 -->
    <!-- <timeline v-if="showTimeline" :allTimeStamps="allTimeStamps" @click="getDatabyTimeStamp"></timeline> -->
  </div>
</template>

<script>
import D3Network from "../components/vue-d3-network/src/vue-d3-network.vue";
import SearchTree from "../components/SearchTreeforClass.vue";
import Timeline from "../components/Timeline";
import axios from "axios";
import { setTimeout } from "timers";
import flowSvg from "@/lib/flowSvg.js";

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

const nodeIcons = {
  masterServer: "",
  server:
    '<svg t="1554884263168" class="icon" style="" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="15590" xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M784 48v928H240V48h544m0-48H240a48 48 0 0 0-48 48v928a48 48 0 0 0 48 48h544a48 48 0 0 0 48-48V48a48 48 0 0 0-48-48zM680 152v83H343v-83h337m24-24H319v131h385V128z m-23 216v83H344v-83h337m24-24H320v131h385V320zM432 528v32h-96v-32h96m16-16H320v64h128v-64z m64 192a16 16 0 1 0 16 16 16 16 0 0 0-16-16z" fill="" p-id="15591"></path><path d="M512 848m-48 0a48 48 0 1 0 96 0 48 48 0 1 0-96 0Z" fill="#1890FF" p-id="15592"></path></svg>',
  pod:
    '<svg t="1554883368654" class="icon" style="" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2427" xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M512 580.266667c-2.133333 0-4.266667 0-4.266667-2.133334L277.333333 465.066667c-4.266667-2.133333-6.4-8.533333-4.266666-14.933334 2.133333-4.266667 8.533333-6.4 14.933333-4.266666L512 556.8l224-113.066667c4.266667-2.133333 10.666667 0 14.933333 4.266667 2.133333 4.266667 0 10.666667-4.266666 14.933333l-230.4 115.2c0 2.133333-2.133333 2.133333-4.266667 2.133334z" fill="#ED1E79" p-id="2428"></path><path d="M512 866.133333c-6.4 0-10.666667-4.266667-10.666667-10.666666V569.6c0-6.4 4.266667-10.666667 10.666667-10.666667s10.666667 4.266667 10.666667 10.666667v285.866667c0 6.4-4.266667 10.666667-10.666667 10.666666z" fill="#ED1E79" p-id="2429"></path><path d="M742.4 981.333333c-2.133333 0-4.266667 0-4.266667-2.133333L512 868.266667 288 981.333333H277.333333L49.066667 870.4c-4.266667 0-6.4-4.266667-6.4-8.533333V569.6c0-4.266667 2.133333-8.533333 6.4-8.533333L273.066667 448V168.533333c0-4.266667 2.133333-8.533333 6.4-8.533333l230.4-115.2c2.133333-2.133333 6.4-2.133333 8.533333 0L746.666667 157.866667c4.266667 2.133333 6.4 6.4 6.4 8.533333V448l224 110.933333c4.266667 2.133333 6.4 6.4 6.4 8.533334v292.266666c0 4.266667-2.133333 8.533333-6.4 10.666667L746.666667 981.333333h-4.266667zM512 844.8c2.133333 0 4.266667 0 4.266667 2.133333L740.266667 960 960 855.466667V576l-224-110.933333c-4.266667-2.133333-6.4-6.4-6.4-8.533334V174.933333L512 66.133333 294.4 174.933333v279.466667c0 4.266667-2.133333 8.533333-6.4 8.533333L64 576v279.466667L281.6 960l224-113.066667c2.133333-2.133333 4.266667-2.133333 6.4-2.133333zM281.6 454.4z" fill="#108EE9" p-id="2430"></path></svg>',
  container:
    '<svg t="1554884950404" class="icon" style="" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="17333" xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M998.4 455.2c-3.2-0.8-70.4-24-131.2-13.6-5.6-30.4-21.6-80.8-72-100l-5.6-2.4-5.6 1.6c-2.4 0.8-24 4.8-40 29.6-19.2 28.8-23.2 72-12 128.8h-672c-11.2 0-41.6-0.8-44 24.8-0.8 6.4-1.6 15.2-1.6 17.6-0.8 4-0.8 4.8-0.8 16 0 298.4 216 332.8 378.4 332.8 317.6 0 432.8-170.4 473.6-316 34.4 6.4 120.8 11.2 146.4-96l4-17.6-17.6-5.6z m-130.4 79.2c-17.6-4-36 7.2-40.8 25.6-64 240-272.8 290.4-436 290.4-234.4 0-338.4-90.4-338.4-292.8v-8c0.8-2.4 0.8-4.8 1.6-11.2h687.2c9.6 0 18.4-4 24-12 6.4-7.2 8-17.6 6.4-27.2-16.8-77.6 0-110.4 14.4-119.2 41.6 21.6 42.4 84 42.4 85.6v28l26.4-9.6c36-12.8 84-4.8 110.4 1.6-19.2 54.4-63.2 56.8-97.6 48.8zM71.2 382.4h85.6v85.6H71.2z" p-id="17334"></path><path d="M196 382.4h85.6v85.6H196zM321.6 382.4h85.6v85.6H321.6zM446.4 382.4h85.6v85.6H446.4zM196 252h85.6v85.6H196zM321.6 252h85.6v85.6H321.6zM446.4 252h85.6v85.6H446.4zM446.4 121.6h85.6v85.6H446.4zM572 382.4h85.6v85.6H572zM228.8 607.2c-28.8 0-52 23.2-52 52s23.2 52 52 52 52-23.2 52-52-23.2-52-52-52z m0 64c-6.4 0-12-5.6-12-12s5.6-12 12-12 12 5.6 12 12-5.6 12-12 12z" p-id="17335"></path></svg>',
  service:
    '<svg t="1554884138254" class="icon" style="" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="12163" xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M306.820741 191.525926c-51.579259 11.188148-103.632593 63.241481-114.820741 114.820741-21.428148 99.176296 53.475556 186.785185 148.954074 186.785185l152.651852 0 0-6.447407 0-31.478519L493.605926 333.937778l-0.18963 0C489.623704 241.682963 403.816296 170.571852 306.820741 191.525926zM455.68 455.205926l-114.725926 0c-63.905185 0-115.579259-52.242963-114.725926-116.337778 0.853333-61.819259 51.294815-112.260741 113.114074-113.114074 64.094815-0.853333 116.337778 50.820741 116.337778 114.725926L455.68 455.205926z" p-id="12164"></path><path d="M832.663704 306.346667c-11.188148-51.579259-63.241481-103.632593-114.820741-114.820741-96.900741-20.954074-182.802963 50.157037-186.595556 142.506667l-0.18963 0 0 121.173333 0 31.478519 0 6.447407 152.651852 0C779.093333 493.131852 854.091852 405.522963 832.663704 306.346667zM568.983704 340.48c0-63.905185 52.242963-115.579259 116.337778-114.725926 61.819259 0.853333 112.260741 51.294815 113.114074 113.114074 0.853333 64.094815-50.820741 116.337778-114.725926 116.337778l-114.725926 0L568.983704 340.48z" p-id="12165"></path><path d="M192 717.842963c11.188148 51.579259 63.241481 103.632593 114.820741 114.820741 96.900741 20.954074 182.802963-50.157037 186.595556-142.506667l0.18963 0L493.605926 568.983704l0-31.478519 0-6.447407-152.651852 0C245.475556 531.057778 170.571852 618.666667 192 717.842963zM455.68 683.70963c0 63.905185-52.242963 115.579259-116.337778 114.725926-61.819259-0.853333-112.260741-51.294815-113.114074-113.114074-0.853333-64.094815 50.820741-116.337778 114.725926-116.337778l114.725926 0L455.68 683.70963z" p-id="12166"></path><path d="M683.70963 531.057778l-152.651852 0 0 6.447407 0 31.478519 0 121.173333 0.18963 0c3.887407 92.34963 89.694815 163.460741 186.595556 142.506667 51.579259-11.188148 103.632593-63.241481 114.820741-114.820741C854.091852 618.666667 779.093333 531.057778 683.70963 531.057778zM798.435556 685.321481C797.582222 747.140741 747.140741 797.582222 685.321481 798.435556c-64.094815 0.853333-116.337778-50.820741-116.337778-114.725926l0-114.725926 114.725926 0C747.614815 568.983704 799.288889 621.226667 798.435556 685.321481z" p-id="12167"></path></svg>',
  namespace:
    '<svg t="1554884521660" class="icon" style="" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="16549" xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M988.576 411.328c-10.848-74.88-133.44-104.224-244-115.424V231.168h-27.36v-0.032l27.008-0.192c-0.928-127.136-318.496-130.784-354.72-130.784-31.264 0-306.496 2.912-348.8 103.904l-5.792 38.432h-0.224c0-2.112 0.256-3.872 0.512-4.96l-0.8 3.488 0.288 7.936-0.352 411.264c-0.224 86.624 138.272 121.44 245.856 135.392 6.368 123.488 318.08 128.128 357.056 128.224 35.968 0 351.52-3.616 352-130.208l0.384-372.448-1.056-9.856z m-53.664 382.176c-0.16 35.84-127.424 75.968-300.576 75.968-171.872-0.544-300.16-41.472-300.064-77.76l0.096-97.44h114.272v-54.368h-114.368v-207.808h24.64v-54.368H279.904v263.52h0.096l-0.064 99.424c-130.912-17.76-191.264-52.672-191.232-80.384l0.352-412.48 3.168-25.536c21.312-37.632 152.448-67.776 297.28-67.776 171.552 0 300.064 40.576 300.352 76.768l0.16 24.672 0.16 0.064-0.032 121.728h-127.04v54.368h127.072v208.064h-24.64v54.368h79.008v-297.888h-0.032v-45.728c119.936 13.088 186.56 43.104 190.176 67.968l0.576 4.16-0.352 370.464z" fill="" p-id="16550"></path><path d="M490.464 640.16h43.072v54.368h-43.072v-54.368zM578.464 640.16h43.104v54.368h-43.104v-54.368zM484 377.728h43.104v54.368h-43.104v-54.368zM401.728 377.728H444.8v54.368h-43.072v-54.368z" fill="" p-id="16551"></path></svg>',
  environment:
    '<svg t="1554883839636" class="icon" style="" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5763" xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="16"><defs><style type="text/css"></style></defs><circle cx="512" cy="512" r="512" stroke="" stroke-width="0" fill="white"/><path d="M896 166.4h-128v25.6h128v-25.6z m-486.4-76.8v204.8h512v102.4h-230.4v51.2h230.4v76.8h51.2v-435.2H409.6z m512 153.6H460.8v-102.4h460.8v102.4z m-153.6 76.8v25.6h128v-25.6h-128z" fill="#999999" p-id="5764"></path><path d="M768 524.8h-102.4c0-112.64-92.16-204.8-204.8-204.8s-204.8 92.16-204.8 204.8c-112.64 0-204.8 92.16-204.8 204.8s92.16 204.8 204.8 204.8h512c112.64 0 204.8-92.16 204.8-204.8s-92.16-204.8-204.8-204.8z m0 358.4H256c-92.16 0-153.6-61.44-153.6-153.6s61.44-153.6 153.6-153.6h51.2v-51.2c0-92.16 61.44-153.6 153.6-153.6s153.6 61.44 153.6 153.6v51.2h153.6c92.16 0 153.6 61.44 153.6 153.6s-61.44 153.6-153.6 153.6z" fill="#333333" p-id="5765"></path></svg>',
  serviceServer: "",
  // serviceDatabase: '<svg version="1.1" xmlns="http://www.w3.org/2000/svg" width="24" height="32" viewBox="0 0 24 32"><path d="M12 30c-6.626 0-12-1.793-12-4 0-1.207 0-2.527 0-4 0-0.348 0.174-0.678 0.424-1 1.338 1.723 5.99 3 11.576 3s10.238-1.277 11.576-3c0.25 0.322 0.424 0.652 0.424 1 0 1.158 0 2.387 0 4 0 2.207-5.375 4-12 4zM12 22c-6.626 0-12-1.793-12-4 0-1.208 0-2.526 0-4 0-0.212 0.080-0.418 0.188-0.622v0c0.061-0.128 0.141-0.254 0.236-0.378 1.338 1.722 5.99 3 11.576 3s10.238-1.278 11.576-3c0.096 0.124 0.176 0.25 0.236 0.378v0c0.107 0.204 0.188 0.41 0.188 0.622 0 1.158 0 2.386 0 4 0 2.207-5.375 4-12 4zM12 14c-6.626 0-12-1.792-12-4 0-0.632 0-1.3 0-2 0-0.636 0-1.296 0-2 0-2.208 5.374-4 12-4s12 1.792 12 4c0 0.624 0 1.286 0 2 0 0.612 0 1.258 0 2 0 2.208-5.375 4-12 4zM12 4c-4.418 0-8 0.894-8 2s3.582 2 8 2 8-0.894 8-2-3.582-2-8-2z"></path></svg>',
  containerNetwork: "",
  containerStorage: ""
};

export default {
  components: {
    D3Network,
    SearchTree,
    Timeline
  },
  data() {
    return {
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
      allTimeStamps: ["2019-09-03 09:32:11", "2019-03-11 09:32:11"], // 不太确定是什么数据格式的 这涉及到排序
      nodeSize: 40,
      fontSize: 14,
      linkWidth: 3,
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
        "masterServer",
        "server",
        "pod",
        "container",
        "service",
        "namespace",
        "environment",
        "serviceServer",
        "serviceDatabase",
        "containerNetwork",
        "containerStorage"
      ],
      propertyNodes: [], // 属性节点数组
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
        "manage",
        "deployed-in",
        "provides",
        "contains",
        "supervises",
        "has",
        "profile"
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
        "linkContains"
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
        }
      },
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
      flowGraphSvg: '',
      isFlowVisibel: false
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
        canvas: this.canvas
      };
    }
  },
  watch: {
    // 显示/隐藏属性节点
    // 别写成 key-value 的形式
    propertyNodeSwitch(newVal) {
      // 当不显示属性时
      if (newVal === false) {
        // 不渲染 node 和 label
        this.propertyNodes.forEach(propertNode => {
          this.nodes.remove(propertNode);
        });
        // 隐藏 link
        document.getElementsByClassName("profile").forEach(x => {
          x.style.visibility = "hidden";
        });
        // 当显示属性时
      } else {
        // 重新渲染节点和 label
        this.nodes = this.nodes.concat(this.propertyNodes);
        // 显示边
        document.getElementsByClassName("profile").forEach(x => {
          x.style.visibility = "visible";
        });
      }
    }
  },
  created() {
    // this.nodes.push(this.initialNode);
    this.getData();
  },
  methods: {
    showOrHidFlowGraph(e) {
      console.log(e);
      // 先从 e.target 拿到数据 然后转成 svg 显示出来
      this.isFlowVisibel = !this.isFlowVisibel;
      if (this.isFlowVisibel) {
        this.flowGraphSvg = flowSvg.svg1;
      }
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
        .get("/sample_data.json")
        .then(response => {
          console.log(response);

          response.data.nodes.map(node => {
            node.id = node.index;
          });
          response.data.links.map(link => {
            link.sid = link.source.index;
            link.tid = link.target.index;
            link.id = link.index;
          });

          this.nodes = response.data.nodes;
          this.links = response.data.links;

          console.log(this.nodes);
          console.log(this.links);

          // this.allTimeStamps = response.data.timeList;
          // this.showTimeline = true

          // this.propertyNodes = this.nodes.filter(node => {
          //   if (this.allPropertyNodeTypes.indexOf(node.type) !== -1) {
          //     return true;
          //   } else {
          //     return false;
          //   }
          // });
          // this.$nextTick(() => {
          //   this.addDblClickEvent();
          // });
        })
        .catch(function(error) {
          // handle error
          console.log(error);
        });
      // .then(function() {
      //   // always executed
      // });
      // let displayProps = document.getElementsByClassName("display-property")[0];
      // displayProps.style.right = "-420px";
      // displayProps.style.display = 'none'
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

            this.nodes = response.data.nodeList;
            this.links = response.data.linkList;

            this.propertyNodes = this.nodes.filter(node => {
              if (this.allPropertyNodeTypes.indexOf(node.type) !== -1) {
                return true;
              } else {
                return false;
              }
            });
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
          .get(reqUrl + "/api/getAllByTime?time=" + currentTimeStamp)
          .then(response => {
            response.data.nodeList.forEach(x => {
              x.svgSym = nodeIcons[x.type];
            });

            this.nodes = response.data.nodeList;
            this.links = response.data.linkList;

            this.propertyNodes = this.nodes.filter(node => {
              if (this.allPropertyNodeTypes.indexOf(node.type) !== -1) {
                return true;
              } else {
                return false;
              }
            });
          })
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
          // axios.post(reqUrl + '/api/delLinks', removeLinkList)
          //   .then(response => {
          //     console.log('删除边的请求成功')
          //     axios.post(reqUrl + '/api/delNodes', removeNodeList)
          //       .then(response => {
          //         console.log('删除节点的请求成功')
          //       })
          //       .catch(error => {
          //         console.log(error)
          //       })
          //   })
          //   .catch(error => {
          //     console.error(error)
          //   })
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
                // axios.post(reqUrl + '/api/modifyOneNode', node)
                //   .then(response => {
                //     if (response) {
                //       _this.$message({
                //         type: "success",
                //         message: "修改成功"
                //       });
                //     }
                //   })
                //   .catch(error => {
                //     console.log(error)
                //   })
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
              // axios.post(reqUrl + '/api/modifyOneLink', link)
              //   .then(response => {
              //     if (response) {
              //       this.$message({
              //         type: "success",
              //         message: "修改成功"
              //       });
              //     }
              //   })
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
      // displayProps.style.display = 'none'
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

      // axios
      //   .post(reqUrl + "/api/addNewNode", newNode)
      //   .then(response => {
      //     console.log(response.data);
      //     axios
      //       .post(reqUrl + "/api/addNewLink", newLink)
      //       .then(response => {
      //         console.log(response.data);
      //       })
      //       .catch(function(error) {
      //         console.log(error);
      //       });
      //   })
      //   .catch(function(error) {
      //     console.log(error);
      //   });

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
            label.setAttribute("style", "font-size:15px;");
            setTimeout(() => {
              label.setAttribute("style", "font-size:0px;");
            }, 1000);
            break;
          }
        }
      }
    }
  },
  mounted() {
    var el = document.getElementsByClassName("net-svg")[0];
    el.onmousedown = e => {
      this.staCoor = getCoordInDocument(e);
    };
    el.onmouseup = e => {
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
        this.showOrHidFlowGraph(e);
        let property = this.currentNode.property;
        this.propertyKeys = Object.keys(property);
        for (var key in property) {
          this.propertyValues.push(property[key]);
        }
        let displayProps = document.getElementsByClassName(
          "display-property"
        )[0];
        // displayProps.style.display = 'block'
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
  }
};
</script>

<style>
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

/* 被选中的图标 */
#class-graph .node-svg.selected {
  /* stroke:tomato !important; */
  stroke-width: 20px !important;
  fill: tomato !important;
}

/* 被选中的 link */
#class-graph .link.selected {
  marker-end: url(#m-end-selected);
}

/* 被选中的所有元素 */
#class-graph .selected {
  stroke: tomato !important;
  stroke-width: 4px;
}

#class-graph .nodesInit {
  fill: lightblue;
}

#class-graph .nodesServer {
  fill: darkcyan;
}

#class-graph .nodesPod {
  fill: rgb(7, 244, 188);
}

#class-graph .nodesContainer {
  fill: dimgray;
}

#class-graph .nodesContainerNetwork,
#class-graph .nodesContainerStorage {
  fill: rgb(200, 255, 195);
  r: 12;
}

#class-graph .nodesService {
  fill: cornflowerblue;
}

#class-graph .nodesServiceDatabase,
#class-graph .nodesServiceServer {
  fill: lightgoldenrodyellow;
  r: 12;
}

#class-graph .nodesNamespace {
  fill: darkgoldenrod;
}

#class-graph .linkManage {
  /* color: rgb(8, 113, 241); */
  /* fill: rgb(1, 1, 77); */
  fill: gray;
  text-anchor: middle;
}

#class-graph .linkDeployed {
  fill: rgb(0, 71, 70);
  text-anchor: middle;
}

#class-graph .linkProvides {
  fill: rgb(2, 75, 58);
  text-anchor: middle;
}

#class-graph .linkContains {
  fill: rgb(1, 78, 18);
  text-anchor: middle;
}

#class-graph .linkService {
  fill: rgb(68, 82, 2);
  text-anchor: middle;
}

#class-graph .linkNamespace {
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

#class-graph #switch-p-node {
  background: white;
  border: 1px lightgray solid;
  border-radius: 10px;
  padding: 10px 20px;
  position: fixed;
  bottom: 90px;
  right: 60px;
}
#class-graph .flow-graph-container {
  position: absolute;
  z-index: 10;
  width: 400px;
  height: 95%;
    right: 0;
    overflow: auto;
}

#class-graph .flow-graph-container .node {
  stroke: none;
  stroke-width: 1px;

}

#class-graph .flow-fade-enter-active {
  transition: all .3s linear;
}
#class-graph .flow-fade-leave-active {
  transition: all .3s linear;
}
#class-graph .flow-fade-enter, .flow-fade-leave-to
/* .flow-fade-leave-active for below version 2.1.8 */ {
  transform: translateX(400px);
  /* opacity: 0; */
}
</style>