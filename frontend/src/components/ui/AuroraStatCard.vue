<template>
  <div :class="['aurora-stat', { 'aurora-stat--trend-up': trendUp, 'aurora-stat--trend-down': trendDown }]">
    <div class="aurora-stat__icon" :style="{ background: iconGradient }">
      <span class="material-icons-round">{{ icon }}</span>
    </div>
    
    <div class="aurora-stat__content">
      <span class="aurora-stat__value">{{ formattedValue }}</span>
      <span class="aurora-stat__label">{{ label }}</span>
    </div>
    
    <div v-if="trend" class="aurora-stat__trend">
      <span class="material-icons-round aurora-stat__trend-icon">
        {{ trendUp ? 'trending_up' : 'trending_down' }}
      </span>
      <span class="aurora-stat__trend-value">{{ trend }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  icon: {
    type: String,
    required: true
  },
  value: {
    type: [String, Number],
    required: true
  },
  label: {
    type: String,
    required: true
  },
  trend: String,
  trendUp: Boolean,
  trendDown: Boolean,
  format: {
    type: String,
    default: 'number', // number, currency, percent
  },
  color: {
    type: String,
    default: 'primary' // primary, secondary, success, warning, error
  }
});

const colorMap = {
  primary: 'linear-gradient(135deg, rgba(124, 58, 237, 0.3) 0%, rgba(124, 58, 237, 0.1) 100%)',
  secondary: 'linear-gradient(135deg, rgba(45, 212, 191, 0.3) 0%, rgba(45, 212, 191, 0.1) 100%)',
  success: 'linear-gradient(135deg, rgba(34, 197, 94, 0.3) 0%, rgba(34, 197, 94, 0.1) 100%)',
  warning: 'linear-gradient(135deg, rgba(234, 179, 8, 0.3) 0%, rgba(234, 179, 8, 0.1) 100%)',
  error: 'linear-gradient(135deg, rgba(239, 68, 68, 0.3) 0%, rgba(239, 68, 68, 0.1) 100%)',
};

const iconGradient = computed(() => colorMap[props.color] || colorMap.primary);

const formattedValue = computed(() => {
  if (props.format === 'currency') {
    return new Intl.NumberFormat('es-CO', { 
      style: 'currency', 
      currency: 'COP',
      notation: 'compact',
      maximumFractionDigits: 1
    }).format(props.value);
  }
  if (props.format === 'percent') {
    return `${props.value}%`;
  }
  if (typeof props.value === 'number') {
    return new Intl.NumberFormat('es-CO').format(props.value);
  }
  return props.value;
});
</script>

<style scoped>
.aurora-stat {
  display: flex;
  align-items: flex-start;
  gap: var(--aurora-space-4);
  padding: var(--aurora-space-5);
  background: var(--aurora-bg-overlay);
  border: 1px solid var(--aurora-border);
  border-radius: var(--aurora-radius-xl);
  backdrop-filter: blur(20px);
  transition: all var(--aurora-transition-base);
}

.aurora-stat:hover {
  border-color: var(--aurora-border-light);
  transform: translateY(-2px);
}

.aurora-stat__icon {
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--aurora-radius-lg);
  flex-shrink: 0;
}

.aurora-stat__icon .material-icons-round {
  font-size: 26px;
  color: var(--aurora-primary-light);
}

.aurora-stat__content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--aurora-space-1);
  min-width: 0;
}

.aurora-stat__value {
  font-size: var(--aurora-text-2xl);
  font-weight: var(--aurora-font-bold);
  color: var(--aurora-text-primary);
  line-height: 1.2;
}

.aurora-stat__label {
  font-size: var(--aurora-text-sm);
  color: var(--aurora-text-tertiary);
}

.aurora-stat__trend {
  display: flex;
  align-items: center;
  gap: var(--aurora-space-1);
  padding: var(--aurora-space-1) var(--aurora-space-2);
  border-radius: var(--aurora-radius-full);
  font-size: var(--aurora-text-xs);
  font-weight: var(--aurora-font-medium);
}

.aurora-stat--trend-up .aurora-stat__trend {
  background: var(--aurora-success-bg);
  color: var(--aurora-success);
}

.aurora-stat--trend-down .aurora-stat__trend {
  background: var(--aurora-error-bg);
  color: var(--aurora-error);
}

.aurora-stat__trend-icon {
  font-size: 16px;
}
</style>
