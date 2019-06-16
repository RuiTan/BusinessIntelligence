<template>
  <!-- 时间线面板 -->
  <div id="timelineContainer">
    <div class="timeline" v-for="timeStamp in allTimeStamps" :key="timeStamp.id">
      <div class="time">
        <div class="line">----------------</div>
        <!-- radio 有用 name 来区分多个表单，没有 name 默认是一个 -->
        <input type="radio" :value="timeStamp" v-model="pickedTimeStamp" class="timePoint">
        <div class="timeText">{{timeStamp.time.substring(5,11)}}</div>
        <div class="timeText">{{timeStamp.time.substring(11)}}</div>
      </div>
    </div>
    <div class="timeline">
      <div class="time">
        <div class="line">----------------</div>
        <input type="radio" value="now" v-model="pickedTimeStamp" class="timePoint">
        <div class="timeText">now</div>
        <div class="timeText">&emsp;</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    allTimeStamps: Array,
  },
  data() {
    return {
      pickedTimeStamp: "now"
    };
  },
  computed: {
    // orderedTimeStamps() {
    //   return this.allTimeStamps.reverse()
    // }
  },
  watch: {
    pickedTimeStamp(newVal) {
      this.$emit("click", newVal);
    },
    allTimeStamps(newVal){
      this.pickedTimeStamp = newVal[newVal.length-1]
    }
  },
  mounted(){
    var tag = this.allTimeStamps[0]
    // this.pickedTimeStamp = tag
    this.allTimeStamps = this.allTimeStamps.reverse()
  }
};
</script>

<style scoped>
#timelineContainer {
  position: fixed;
  bottom: -110px;
  width: 70%;
  height: 100px;
  text-align: center;
  white-space: nowrap; /* scroll x effects */
  overflow: auto;
  border: 1px solid lightgray;
  background-color: rgb(255, 255, 255);
  border-radius: 10px;
  padding-bottom: 20px;
  transition: bottom .3s;
}

#timelineContainer:hover {
  bottom: 0px;
}

.timeline {
  display: inline-block;
}

.timePoint {
  position: relative;
  cursor: pointer;
  outline: none;
  appearance: none;
  height: 25px;
  width: 25px;
  background: rgb(153, 153, 153);
  border: 4px lightgray solid;
  border-radius: 25px;
  z-index: 2;
}
.timePoint:hover {
  border-color: darkcyan;
  background-color: rgb(161, 216, 216);
}

.timePoint:checked {
  border-color: darkcyan;
  background-color: rgb(161, 216, 216);
  /* border-color: darkcyan; */
}

.line {
  position: relative;
  top: 25px;
  color: lightgray;
}

.timeText {
  font-size: 14px;
  text-align: center;
  color: gray;
  /* height: 19px; */
}
</style>
