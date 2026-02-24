<template>
  <button 
    :class="[
      'btn',
      `btn--${variant}`,
      `btn--${size}`,
      { 
        'btn--icon-only': iconOnly,
        'btn--loading': loading,
        'btn--full': fullWidth
      }
    ]"
    :disabled="disabled || loading"
    :type="type"
    @click="$emit('click', $event)"
  >
    <span v-if="loading" class="btn__loader">
      <Icon name="loader" :size="loaderSize" spin />
    </span>
    <Icon v-else-if="icon" :name="icon" :size="iconSize" class="btn__icon" />
    <span v-if="!iconOnly && $slots.default" class="btn__label">
      <slot />
    </span>
    <Icon v-if="trailingIcon && !iconOnly && !loading" :name="trailingIcon" :size="iconSize" class="btn__icon btn__icon--trailing" />
  </button>
</template>

<script setup>
import { computed } from 'vue';
import Icon from './Icon.vue';

const props = defineProps({
  variant: {
    type: String,
    default: 'primary',
    validator: (v) => ['primary', 'secondary', 'ghost', 'danger', 'success'].includes(v)
  },
  size: {
    type: String,
    default: 'md',
    validator: (v) => ['sm', 'md', 'lg'].includes(v)
  },
  icon: String,
  trailingIcon: String,
  iconOnly: Boolean,
  disabled: Boolean,
  loading: Boolean,
  fullWidth: Boolean,
  type: {
    type: String,
    default: 'button'
  }
});

defineEmits(['click']);

const iconSize = computed(() => {
  const sizes = { sm: 16, md: 18, lg: 20 };
  return sizes[props.size];
});

const loaderSize = computed(() => {
  const sizes = { sm: 14, md: 16, lg: 18 };
  return sizes[props.size];
});
</script>

<style scoped>
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  border: none;
  border-radius: var(--radius-lg);
  font-family: var(--font-body);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out);
  position: relative;
  overflow: hidden;
  white-space: nowrap;
}

.btn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(255,255,255,0.1) 0%, transparent 50%);
  opacity: 0;
  transition: opacity var(--duration-fast);
}

.btn:hover::before {
  opacity: 1;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn:disabled::before {
  display: none;
}

.btn--full {
  width: 100%;
}

/* Sizes */
.btn--sm {
  height: 36px;
  padding: 0 var(--space-4);
  font-size: var(--text-sm);
}

.btn--md {
  height: 44px;
  padding: 0 var(--space-6);
  font-size: var(--text-sm);
}

.btn--lg {
  height: 52px;
  padding: 0 var(--space-8);
  font-size: var(--text-base);
}

.btn--icon-only.btn--sm { width: 36px; padding: 0; }
.btn--icon-only.btn--md { width: 44px; padding: 0; }
.btn--icon-only.btn--lg { width: 52px; padding: 0; }

/* Primary */
.btn--primary {
  background: var(--gradient-primary);
  color: var(--bg-deep);
  box-shadow: 0 4px 20px var(--primary-glow);
}

.btn--primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px var(--primary-glow);
}

.btn--primary:active:not(:disabled) {
  transform: translateY(0);
}

/* Secondary */
.btn--secondary {
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  color: var(--text-primary);
  backdrop-filter: blur(10px);
}

.btn--secondary:hover:not(:disabled) {
  background: var(--glass-hover);
  border-color: var(--primary);
  color: var(--primary);
}

/* Ghost */
.btn--ghost {
  background: transparent;
  color: var(--text-secondary);
}

.btn--ghost:hover:not(:disabled) {
  background: var(--glass-bg);
  color: var(--text-primary);
}

/* Danger */
.btn--danger {
  background: var(--error);
  color: white;
}

.btn--danger:hover:not(:disabled) {
  box-shadow: 0 4px 20px var(--error-soft);
  transform: translateY(-2px);
}

/* Success */
.btn--success {
  background: var(--success);
  color: white;
}

.btn--success:hover:not(:disabled) {
  box-shadow: 0 4px 20px var(--success-soft);
  transform: translateY(-2px);
}

/* Icon */
.btn__icon {
  flex-shrink: 0;
}

/* Loader */
.btn__loader {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Label */
.btn__label {
  position: relative;
}
</style>
