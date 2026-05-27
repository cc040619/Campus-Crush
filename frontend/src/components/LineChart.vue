<template>
  <div class="line-chart-container" @click="handleClick">
    <h3 class="chart-title">{{ chartData.title || '折线图' }}</h3>
    <div class="chart-wrapper">
      <svg :width="width" :height="height" class="line-chart">
        <defs>
          <linearGradient id="lineGradient" x1="0%" y1="0%" x2="0%" y2="100%">
            <stop offset="0%" stop-color="rgba(255, 182, 193, 0.8)" />
            <stop offset="100%" stop-color="rgba(255, 182, 193, 0.1)" />
          </linearGradient>
          <filter id="glow">
            <feGaussianBlur stdDeviation="2" result="coloredBlur"/>
            <feMerge>
              <feMergeNode in="coloredBlur"/>
              <feMergeNode in="SourceGraphic"/>
            </feMerge>
          </filter>
        </defs>
        
        <g class="grid-lines">
          <line v-for="i in 5" :key="'h-' + i"
            :x1="padding" :y1="padding + (chartHeight / 4) * (i - 1)"
            :x2="width - padding" :y2="padding + (chartHeight / 4) * (i - 1)"
            stroke="#FFE6E6" stroke-width="1" stroke-dasharray="4" />
        </g>

        <g class="data-line">
          <path :d="areaPath" fill="url(#lineGradient)" opacity="0.6" />
          <path :d="linePath" fill="none" stroke="#FF69B4" stroke-width="3" filter="url(#glow)" />
        </g>

        <g class="data-points">
          <g v-for="(point, index) in dataPoints" :key="'point-' + index"
            :transform="`translate(${point.x}, ${point.y})`">
            <circle r="6" fill="#FF69B4" class="data-point" />
            <circle r="10" fill="#FFB6C1" fill-opacity="0.3" />
          </g>
        </g>

        <g class="x-labels">
          <text v-for="(label, index) in chartData.labels" :key="'x-' + index"
            :x="getXPosition(index)" :y="height - 10"
            text-anchor="middle" class="x-label">{{ label }}</text>
        </g>

        <g class="y-labels">
          <text v-for="(label, index) in yLabels" :key="'y-' + index"
            :x="padding - 10" :y="padding + (chartHeight / 4) * index + 4"
            text-anchor="end" class="y-label">{{ label }}</text>
        </g>
      </svg>
    </div>
    <div class="chart-hint">点击查看详情</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  chartData: {
    type: Object,
    required: true
  },
  width: {
    type: Number,
    default: 600
  },
  height: {
    type: Number,
    default: 300
  }
})

const emit = defineEmits(['click'])

const padding = 50
const chartHeight = computed(() => props.height - padding * 2)
const chartWidth = computed(() => props.width - padding * 2)

const maxValue = computed(() => {
  const data = props.chartData.data || []
  return data.length > 0 ? Math.max(...data) * 1.2 : 10
})

const yLabels = computed(() => {
  const labels = []
  for (let i = 4; i >= 0; i--) {
    labels.push(Math.round(maxValue.value * i / 4))
  }
  return labels
})

const getXPosition = (index) => {
  const data = props.chartData.data || []
  if (data.length <= 1) return padding + chartWidth.value / 2
  return padding + (chartWidth.value / (data.length - 1)) * index
}

const getYPosition = (value) => {
  return padding + chartHeight.value * (1 - value / maxValue.value)
}

const dataPoints = computed(() => {
  const data = props.chartData.data || []
  return data.map((value, index) => ({
    x: getXPosition(index),
    y: getYPosition(value)
  }))
})

const linePath = computed(() => {
  const points = dataPoints.value
  if (points.length === 0) return ''
  return points.map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`).join(' ')
})

const areaPath = computed(() => {
  const points = dataPoints.value
  if (points.length === 0) return ''
  const line = points.map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`).join(' ')
  return `${line} L ${points[points.length - 1].x} ${padding + chartHeight.value} L ${points[0].x} ${padding + chartHeight.value} Z`
})

const handleClick = () => {
  emit('click', props.chartData)
}
</script>

<style scoped>
.line-chart-container {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 15px;
  padding: 20px;
  border: 2px solid rgba(255, 182, 193, 0.6);
  cursor: pointer;
  transition: all 0.3s ease;
}

.line-chart-container:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(255, 182, 193, 0.4);
}

.chart-title {
  color: #FF69B4;
  font-size: 18px;
  margin-bottom: 20px;
  text-align: center;
}

.chart-wrapper {
  display: flex;
  justify-content: center;
}

.line-chart {
  overflow: visible;
}

.data-point {
  transition: all 0.3s ease;
}

.data-point:hover {
  r: 8;
}

.x-label {
  font-size: 12px;
  fill: #999;
}

.y-label {
  font-size: 12px;
  fill: #999;
}

.chart-hint {
  text-align: center;
  font-size: 12px;
  color: #FF9999;
  margin-top: 10px;
}
</style>