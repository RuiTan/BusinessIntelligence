<template>
  <el-container>
    <!-- <el-header height="0px">
           
    </el-header>-->

    <el-main>
      <el-row type="flex" align="middle">
        <el-col>
          <i class="el-icon-menu" @click="handleCollapse"></i>
        </el-col>
      </el-row>
      <el-row type="flex">
        <el-col :style="{width:this.sideBarWidth()}" class="nav">
          <el-menu
            default-active="1-4-1"
            class="el-menu-vertical-demo"
            @open="handleOpen"
            @close="handleClose"
            @select="handleSelect"
            :collapse="isCollapse"
          >
            <el-menu-item>
              <i class="el-icon-location-outline" index="one-node"></i>
              <span slot="title" style="color:unset">单节点多跳查询</span>
            </el-menu-item>
            <el-menu-item>
              <i class="el-icon-share" index="duo-node-mul"></i>
              <span slot="title" style="color:unset">双节点多跳查询</span>
            </el-menu-item>
            <el-menu-item >
              <i class="el-icon-share" index="duo-node-short"></i>
              <span slot="title" style="color:unset">双节点最短路径查询</span>
            </el-menu-item>
          </el-menu>
        </el-col>

        <el-col style="width:100%;height:100%">
          <!-- <el-button>test</el-button> -->
          <keep-alive>
            <router-view></router-view>
          </keep-alive>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>


<style scoped>
svg {
  left: 50%;
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 250px;
  min-height: 700px;
}

.el-main {
  padding: 5px;
}

.el-header {
  box-shadow: 0px 1px 10px lightgray;
  /* opacity: 60; */
}
.nav {
  margin-top: 40px;
}
.el-icon-menu {
  color: white;
  position: fixed;
  z-index: 10;
  width: 16px;
  height: 16px;
  border-radius: 20px;
  padding: 10px;
  background-color: rgba(95, 95, 95, 0.705);
  cursor: pointer;
  top: 7px;
  left: 18px;

}
</style>

<script>
export default {
  name: "mainpage",
  data() {
    return {
      sidebar: 5,
      isCollapse: true
    };
  },
  methods: {
    sideBarWidth() {
      if (!this.isCollapse) {
        return "350px";
      } else {
        return "75px";
      }
    },
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
      this.$router.push({
        name: key
      });
    },
    handleOpen(key, keyPath) {
      console.log(key, keyPath);
    },
    handleClose(key, keyPath) {
      console.log(key, keyPath);
    },
    handleCollapse() {
      this.isCollapse = !this.isCollapse;
    }
  }
};
</script>