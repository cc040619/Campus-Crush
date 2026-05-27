import { ref } from 'vue'

const loadedImages = new Set()

export function useImagePreload() {
  const isPreloading = ref(false)
  const preloadedCount = ref(0)

  const preloadImage = (src, options = {}) => {
    return new Promise((resolve, reject) => {
      if (!src || src.trim() === '') {
        reject(new Error('Invalid image source'))
        return
      }

      if (loadedImages.has(src)) {
        resolve(src)
        return
      }

      const img = new Image()
      img.src = src
      
      if (options.crossOrigin) {
        img.crossOrigin = options.crossOrigin
      }

      img.onload = () => {
        loadedImages.add(src)
        resolve(src)
      }

      img.onerror = () => {
        reject(new Error(`Failed to load image: ${src}`))
      }
    })
  }

  const preloadImages = async (sources, options = {}) => {
    const { 
      concurrency = 3, 
      priority = 'sequential',
      onProgress 
    } = options

    isPreloading.value = true
    preloadedCount.value = 0

    const validSources = sources.filter(src => src && src.trim() !== '')
    const total = validSources.length

    if (priority === 'sequential') {
      for (let i = 0; i < validSources.length; i++) {
        try {
          await preloadImage(validSources[i], options)
          preloadedCount.value++
          onProgress?.({ 
            loaded: preloadedCount.value, 
            total, 
            current: validSources[i] 
          })
        } catch (error) {
          console.warn('Preload failed:', error.message)
          preloadedCount.value++
        }
      }
    } else {
      const batches = []
      for (let i = 0; i < validSources.length; i += concurrency) {
        batches.push(validSources.slice(i, i + concurrency))
      }

      for (const batch of batches) {
        const promises = batch.map(src => 
          preloadImage(src, options).catch(error => {
            console.warn('Preload failed:', error.message)
            return null
          })
        )
        
        await Promise.all(promises)
        preloadedCount.value += batch.length
        onProgress?.({ 
          loaded: preloadedCount.value, 
          total, 
          current: batch[batch.length - 1] 
        })
      }
    }

    isPreloading.value = false
    return { loaded: preloadedCount.value, total }
  }

  const isImageLoaded = (src) => {
    return loadedImages.has(src)
  }

  const clearCache = () => {
    loadedImages.clear()
    preloadedCount.value = 0
  }

  return {
    isPreloading,
    preloadedCount,
    preloadImage,
    preloadImages,
    isImageLoaded,
    clearCache
  }
}

export default useImagePreload