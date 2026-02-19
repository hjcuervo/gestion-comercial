<template>
  <button 
    :class="['md-button', `md-button--${variant}`, { 'md-button--icon-only': iconOnly }]"
    :disabled="disabled || loading"
    :type="type"
    @click="$emit('click', $event)"
  >
    <span v-if="loading" class="md-button__loader">
      <span class="material-icons spinning">sync</span>
    </span>
    <span v-else-if="icon" class="material-icons md-button__icon">{{ icon }}</span>
    <span v-if="!iconOnly" class="md-button__label"><slot /></span>
  </button>
</template>

<script setup>
defineProps({
  variant: {
    type: String,
    default: 'filled',
    validator: (v) => ['filled', 'outlined', 'text', 'tonal'].includes(v)
  },
  icon: String,
  iconOnly: Boolean,
  disabled: Boolean,
  loading: Boolean,
  type: {
    type: String,
    default: 'button'
  }
});

defineEmits(['click']);
</script>

<style scoped>
.md-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--md-sys-spacing-sm);
  padding: 0 var(--md-sys-spacing-lg);
  height: 40px;
  border: none;
  border-radius: var(--md-sys-shape-corner-full);
  font: var(--md-sys-typescale-label-large);
  cursor: pointer;
  transition: all var(--md-sys-motion-duration-short) var(--md-sys-motion-easing-standard);
  position: relative;
  overflow: hidden;
}

.md-button:disabled {
  opacity: 0.38;
  cursor: not-allowed;
}

.md-button--icon-only {
  width: 40px;
  padding: 0;
}

/* Filled */
.md-button--filled {
  background-color: var(--md-sys-color-primary);
  color: var(--md-sys-color-on-primary);
}

.md-button--filled:hover:not(:disabled) {
  box-shadow: var(--md-sys-elevation-1);
}

.md-button--filled:active:not(:disabled) {
  box-shadow: var(--md-sys-elevation-0);
}

/* Outlined */
.md-button--outlined {
  background-color: transparent;
  color: var(--md-sys-color-primary);
  border: 1px solid var(--md-sys-color-outline);
}

.md-button--outlined:hover:not(:disabled) {
  background-color: rgba(26, 115, 232, 0.08);
}

/* Text */
.md-button--text {
  background-color: transparent;
  color: var(--md-sys-color-primary);
  padding: 0 var(--md-sys-spacing-md);
}

.md-button--text:hover:not(:disabled) {
  background-color: rgba(26, 115, 232, 0.08);
}

/* Tonal */
.md-button--tonal {
  background-color: var(--md-sys-color-primary-container);
  color: var(--md-sys-color-on-primary-container);
}

.md-button--tonal:hover:not(:disabled) {
  box-shadow: var(--md-sys-elevation-1);
}

/* Icon */
.md-button__icon {
  font-size: 18px;
}

/* Loader */
.md-button__loader {
  display: flex;
}

.spinning {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
