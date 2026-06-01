<template>
  <button
    :class="[
      'gc-btn',
      `gc-btn--${variant}`,
      `gc-btn--${size}`,
      { 'gc-btn--full': fullWidth, 'gc-btn--loading': loading },
    ]"
    :disabled="disabled || loading"
    :type="type"
    @click="$emit('click', $event)"
  >
    <GcSpinner v-if="loading" :size="size === 'sm' ? 12 : 14" class="gc-btn__spinner" />
    <GcIcon v-else-if="icon" :name="icon" :size="size === 'sm' ? 14 : 16" />
    <span v-if="$slots.default" class="gc-btn__label"><slot /></span>
  </button>
</template>

<script setup>
import GcIcon from './GcIcon.vue';
import GcSpinner from './GcSpinner.vue';

defineProps({
  variant: {
    type: String,
    default: 'default', // default | primary | danger | ghost
    validator: (v) => ['default', 'primary', 'danger', 'ghost'].includes(v),
  },
  size: {
    type: String,
    default: 'md', // sm | md | lg
    validator: (v) => ['sm', 'md', 'lg'].includes(v),
  },
  type: { type: String, default: 'button' },
  icon: { type: String, default: '' },
  loading: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
  fullWidth: { type: Boolean, default: false },
});

defineEmits(['click']);
</script>

<style scoped>
.gc-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--gc-space-2);
  font-family: var(--gc-font-sans);
  font-weight: var(--gc-fw-medium);
  border: 1px solid transparent;
  border-radius: var(--gc-radius-md);
  white-space: nowrap;
  transition: background var(--gc-t-fast), border-color var(--gc-t-fast), color var(--gc-t-fast);
}
.gc-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.gc-btn--full { width: 100%; }

/* Tamaños */
.gc-btn--sm { height: 28px; padding: 0 var(--gc-space-3); font-size: var(--gc-fs-sm); }
.gc-btn--md { height: 34px; padding: 0 var(--gc-space-4); font-size: var(--gc-fs-md); }
.gc-btn--lg { height: 42px; padding: 0 var(--gc-space-6); font-size: var(--gc-fs-lg); }

/* default: superficie con borde fino */
.gc-btn--default {
  background: var(--gc-surface);
  border-color: var(--gc-border-strong);
  color: var(--gc-text);
}
.gc-btn--default:not(:disabled):hover { background: var(--gc-surface-2); }

/* primary: grafito, texto claro */
.gc-btn--primary {
  background: var(--gc-primary);
  color: var(--gc-primary-text);
}
.gc-btn--primary:not(:disabled):hover { background: var(--gc-primary-hover); }

/* danger: borde/texto rojo (destructivo sobrio) */
.gc-btn--danger {
  background: transparent;
  border-color: var(--gc-danger);
  color: var(--gc-danger);
}
.gc-btn--danger:not(:disabled):hover { background: var(--gc-danger-soft); }

/* ghost: sin borde, solo texto */
.gc-btn--ghost {
  background: transparent;
  color: var(--gc-text-2);
}
.gc-btn--ghost:not(:disabled):hover { background: var(--gc-surface-2); color: var(--gc-text); }

.gc-btn__spinner { display: inline-flex; }
</style>
