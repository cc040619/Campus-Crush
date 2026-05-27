<template>
  <div 
    class="safe-image-wrapper" 
    :class="[className, { 'image-loading': isLoading, 'image-loaded': hasLoaded, 'image-error': hasError }]"
    :style="wrapperStyles"
    :data-safe-image="src"
  >
    <img 
      v-show="!hasError && isValidSrc && shouldLoad"
      :src="displaySrc"
      :alt="alt"
      class="main-image"
      @error="handleError"
      @load="handleLoad"
    />
    
    <div v-if="hasError || !isValidSrc" class="img-error-wrapper">
      <img :src="fallback" class="fallback-img" :alt="alt || '加载失败'" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  src: {
    type: String,
    default: ''
  },
  fallback: {
    type: String,
    default: 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'
  },
  alt: {
    type: String,
    default: ''
  },
  className: {
    type: String,
    default: ''
  },
  style: {
    type: [String, Object],
    default: ''
  },
  lazy: {
    type: Boolean,
    default: true
  },
  rootMargin: {
    type: String,
    default: '100px'
  },
  threshold: {
    type: Number,
    default: 0.1
  }
})

const emit = defineEmits(['error', 'load'])

const hasError = ref(false)
const isLoaded = ref(false)
const isLoading = ref(false)
const hasLoaded = ref(false)
const shouldLoad = ref(false)
let observer = null

const isValidSrc = computed(() => {
  if (!props.src) return false
  const trimmed = props.src.trim()
  return trimmed !== '' && trimmed !== 'null' && trimmed !== 'undefined' && trimmed !== 'NaN'
})

const displaySrc = computed(() => {
  if (!isValidSrc.value) return ''
  return props.src
})

const wrapperStyles = computed(() => {
  if (typeof props.style === 'string') {
    return props.style
  }
  return { ...props.style }
})

const handleError = () => {
  hasError.value = true
  isLoading.value = false
  hasLoaded.value = false
  emit('error')
}

const handleLoad = () => {
  isLoaded.value = true
  isLoading.value = false
  hasLoaded.value = true
  hasError.value = false
  emit('load')
}

const loadImage = () => {
  if (shouldLoad.value || !isValidSrc.value) return
  shouldLoad.value = true
  isLoading.value = true
}

onMounted(() => {
  if (!props.lazy || !isValidSrc.value) {
    loadImage()
    return
  }

  observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        loadImage()
        if (observer) {
          observer.unobserve(entry.target)
        }
      }
    })
  }, {
    rootMargin: props.rootMargin,
    threshold: props.threshold
  })

  const wrapper = document.querySelector(`[data-safe-image="${props.src}"]`)
  if (wrapper) {
    observer.observe(wrapper)
  }
})

onUnmounted(() => {
  if (observer) {
    observer.disconnect()
  }
})
</script>

<style scoped>
.safe-image-wrapper {
  position: relative;
  overflow: hidden;
  background: #f5f5f5;
  width: 100%;
  height: 100%;
}

.main-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0;
  transition: opacity 0.3s ease-in-out;
}

.image-loaded .main-image {
  opacity: 1;
}

.image-loading {
  background: #f5f5f5;
}

.img-error-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
}

.fallback-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
