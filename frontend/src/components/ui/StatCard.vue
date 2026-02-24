<template>
  <div :class="['stat-card', { 'stat-card--trend-up': trendUp, 'stat-card--trend-down': trendDown }]">
    <div class="stat-card__icon" :style="{ '--icon-color': iconColor }">
      <Icon :name="icon" :size="26" />
    </div>
    
    <div class="stat-card__content">
      <span class="stat-card__value">{{ formattedValue }}</span>
      <span class="stat-card__label">{{ label }}</span>
    </div>
    
    <div v-if="trend" class="stat-card__trend">
      <Icon :name="trendUp ? 'trending-up' : 'trending-down'" :size="16" />
      <span>{{ trend }}</span>
    </div>
    
    <!-- Decorative gradient line -->
    <div class="stat-card__accent" :style="{ '--accent-color': iconColor }"></div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import Icon from './Icon.vue';

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
    default: 'number'
  },
  color: {
    type: String,
    default: 'primary'
  }
});

const colorMap = {
  primary: 'var(--primary)',
  secondary: 'var(--secondary)',
  success: 'var(--success)',
  warning: 'var(--warning)',
  error: 'var(--error)',
  accent: 'var(--accent)',
};

const iconColor = computed(() => colorMap[props.color] || colorMap.primary);

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
.stat-card {
  position: relative;
  display: flex;
  align-items: flex-start;
  gap: var(--space-4);
  padding: var(--space-5);
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-xl);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  overflow: hidden;
  transition: all var(--duration-base) var(--ease-out);
}

.stat-card:hover {
  border-color: var(--border-light);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-card__icon {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: color-mix(in srgb, var(--icon-color) 15%, transparent);
  border-radius: var(--radius-lg);
  color: var(--icon-color);
  flex-shrink: 0;
}

.stat-card__content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
  min-width: 0;
}

.stat-card__value {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  line-height: 1.2;
  letter-spacing: -0.02em;
}

.stat-card__label {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

.stat-card__trend {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
}

.stat-card--trend-up .stat-card__trend {
  background: var(--success-soft);
  color: var(--success);
}

.stat-card--trend-down .stat-card__trend {
  background: var(--error-soft);
  color: var(--error);
}

/* Decorative accent line at top */
.stat-card__accent {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--accent-color), transparent);
  opacity: 0.6;
}
</style>
