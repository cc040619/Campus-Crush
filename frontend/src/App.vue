<template>
  <router-view />
</template>

<script setup>
</script>

<style>
/* 修复页面加载时向右弹的问题 */
html {
  overflow-y: scroll; /* 强制始终显示垂直滚动条占位 */
  scrollbar-gutter: stable; /* 现代浏览器标准，稳定滚动条占位，避免跳动 */
}

body {
  scrollbar-gutter: stable;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 修复全局 border-box 导致 Element Plus 表单组件选中值不显示 */
.el-select__wrapper,
.el-select__input,
.el-select__selected-item,
.el-input__wrapper,
.el-input__inner {
  box-sizing: content-box;
}

body {
  font-family: 'Microsoft YaHei', Arial, sans-serif;
  background-color: #FFFDFD;
  color: #333333;
}

#app {
  min-height: 100vh;
}
</style>
