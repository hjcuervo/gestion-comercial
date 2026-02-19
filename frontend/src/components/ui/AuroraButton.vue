<template>
  <button 
    :class="[
      'aurora-btn',
      `aurora-btn--${variant}`,
      `aurora-btn--${size}`,
      { 
        'aurora-btn--icon-only': iconOnly,
        'aurora-btn--loading': loading,
        'aurora-btn--full': fullWidth
      }
    ]"
    :disabled="disabled || loading"
    :type="type"
    @click="$emit('click', $event)"
  >
    <span v-if="loading" class="aurora-btn__loader"></span>
    <span v-else-if="icon" class="material-icons-round aurora-btn__icon">{{ icon }}</span>
    <span v-if="!iconOnly && $slots.default" class="aurora-btn__label">
      <slot />
    </span>
    <span v-if="trailingIcon && !iconOnly" class="material-icons-round aurora-btn__icon aurora-btn__icon--trailing">
      {{ trailingIcon }}
    </span>
  </button>
</template>

<script setup>
defineProps({
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
</script>

<style scoped>
.aurora-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--aurora-space-2);
  border: none;
  border-radius: var(--aurora-radius-lg);
  font-family: var(--aurora-font-family);
  font-weight: var(--aurora-font-medium);
  cursor: pointer;
  transition: all var(--aurora-transition-fast);
  position: relative;
  overflow: hidden;
  white-space: nowrap;
}

.aurora-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.aurora-btn--full {
  width: 100%;
}

/* Sizes */
.aurora-btn--sm {
  height: 32px;
  padding: 0 var(--aurora-space-3);
  font-size: var(--aurora-text-sm);
}

.aurora-btn--md {
  height: 40px;
  padding: 0 var(--aurora-space-5);
  font-size: var(--aurora-text-sm);
}

.aurora-btn--lg {
  height: 48px;
  padding: 0 var(--aurora-space-6);
  font-size: var(--aurora-text-base);
}

.aurora-btn--icon-only.aurora-btn--sm { width: 32px; padding: 0; }
.aurora-btn--icon-only.aurora-btn--md { width: 40px; padding: 0; }
.aurora-btn--icon-only.aurora-btn--lg { width: 48px; padding: 0; }

/* Primary Variant */
.aurora-btn--primary {
  background: var(--aurora-gradient-primary);
  color: white;
  box-shadow: 0 4px 16px rgba(124, 58, 237, 0.3);
}

.aurora-btn--primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(124, 58, 237, 0.4);
}

.aurora-btn--primary:active:not(:disabled) {
  transform: translateY(0);
}

/* Secondary Variant */
.aurora-btn--secondary {
  background: var(--aurora-bg-overlay);
  border: 1px solid var(--aurora-border-light);
  color: var(--aurora-text-primary);
  backdrop-filter: blur(10px);
}

.aurora-btn--secondary:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.08);
  border-color: var(--aurora-primary);
  color: var(--aurora-primary-light);
}

/* Ghost Variant */
.aurora-btn--ghost {
  background: transparent;
  color: var(--aurora-text-secondary);
}

.aurora-btn--ghost:hover:not(:disabled) {
  background: var(--aurora-bg-overlay);
  color: var(--aurora-text-primary);
}

/* Danger Variant */
.aurora-btn--danger {
  background: var(--aurora-error);
  color: white;
}

.aurora-btn--danger:hover:not(:disabled) {
  background: #dc2626;
  box-shadow: 0 4px 16px rgba(239, 68, 68, 0.3);
}

/* Success Variant */
.aurora-btn--success {
  background: var(--aurora-success);
  color: white;
}

.aurora-btn--success:hover:not(:disabled) {
  background: #16a34a;
  box-shadow: 0 4px 16px rgba(34, 197, 94, 0.3);
}

/* Icon */
.aurora-btn__icon {
  font-size: 18px;
}

.aurora-btn--sm .aurora-btn__icon { font-size: 16px; }
.aurora-btn--lg .aurora-btn__icon { font-size: 20px; }

/* Loader */
.aurora-btn__loader {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Ripple effect on click */
.aurora-btn::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle, rgba(255,255,255,0.3) 0%, transparent 70%);
  opacity: 0;
  transform: scale(0);
  transition: transform 0.5s, opacity 0.3s;
}

.aurora-btn:active::after {
  transform: scale(2);
  opacity: 1;
  transition: transform 0s, opacity 0s;
}
</style>
