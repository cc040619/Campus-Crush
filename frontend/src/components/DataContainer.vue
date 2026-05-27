<template>
  <div v-if="loading" class="skeleton-wrapper">
    <el-skeleton :rows="rows" animated />
  </div>
  <div v-else-if="$slots.default && hasData">
    <slot></slot>
  </div>
  <el-empty v-else :description="emptyText" />
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  loading: {
    type: Boolean,
    default: false
  },
  data: {
    type: [Array, Object, String, Number],
    default: null
  },
  rows: {
    type: Number,
    default: 5
  },
  emptyText: {
    type: String,
    default: '暂无数据'
  }
})

const hasData = computed(() => {
  if (Array.isArray(props.data)) {
    return props.data.length > 0
  }
  return props.data !== null && props.data !== undefined
})
</script>

<style scoped>
.skeleton-wrapper {
  padding: 20px;
}
</style>