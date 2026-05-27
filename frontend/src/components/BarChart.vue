<template>
  <div class="bar-chart-container" @click="handleClick">
    <h3 class="chart-title">{{ chartData.title || '柱状图' }}</h3>
    <div class="chart-wrapper">
      <svg :width="width" :height="height" class="bar-chart">
        <defs>
          <linearGradient id="barGradient" x1="0%" y1="0%" x2="0%" y2="100%">
            <stop offset="0%" stop-color="#FF69B4" />
            <stop offset="100%" stop-color="#FFB6C1" />
          </linearGradient>
          <filter id="barGlow">
            <feGaussianBlur stdDeviation="1" result="coloredBlur"/>
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

        <g class="bars">
          <g v-for="(value, index) in chartData.data" :key="'bar-' + index"
            :transform="`translate(${getXPosition(index)}, 0)`">
            <rect :x="barWidth / 2 * -1" :y="getYPosition(value)"
              :width="barWidth" :height="chartHeight - (getYPosition(value) - padding)"
              fill="url(#barGradient)" rx="5" ry="5" filter="url(#barGlow)"
              class="bar" :style="{ animationDelay: `${index * 0.1}s` }" />
            <text :y="getYPosition(value) - 10" text-anchor="middle" class="bar-value">{{ value }}</text>
          </g>
        </g>

        <g class="x-labels">
          <text v-for="(label, index) in chartData.labels" :key="'x-' + index"
            :x="getXPosition(index)" :y="height - 10"
            text-anchor="middle" class="x-label">{{ formatLabel(label) }}</text>
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

const barWidth = computed(() => {
  const data = props.chartData.data || []
  if (data.length === 0) return 30
  return Math.min(40, chartWidth.value / (data.length * 1.8))
})

const getXPosition = (index) => {
  const data = props.chartData.data || []
  if (data.length === 0) return padding + chartWidth.value / 2
  const totalWidth = chartWidth.value
  const spacing = totalWidth / data.length
  return padding + spacing * index + spacing / 2
}

const getYPosition = (value) => {
  return padding + chartHeight.value * (1 - value / maxValue.value)
}

const formatLabel = (label) => {
  if (typeof label === 'string' && label.includes('-')) {
    return label.split('-')[1] + '月'
  }
  return label
}

const handleClick = () => {
  emit('click', props.chartData)
}
</script>

<style scoped>
.bar-chart-container {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 15px;
  padding: 20px;
  border: 2px solid rgba(255, 182, 193, 0.6);
  cursor: pointer;
  transition: all 0.3s ease;
}

.bar-chart-container:hover {
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

.bar-chart {
  overflow: visible;
}

.bar {
  animation: barGrow 0.5s ease-out forwards;
  opacity: 0;
}

@keyframes barGrow {
  from {
    opacity: 0;
    transform: scaleY(0);
    transform-origin: bottom;
  }
  to {
    opacity: 1;
    transform: scaleY(1);
  }
}

.bar:hover {
  opacity: 0.8;
}

.bar-value {
  font-size: 12px;
  fill: #FF69B4;
  font-weight: bold;
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