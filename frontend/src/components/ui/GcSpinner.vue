<template>
  <span class="gc-spinner" :style="spinnerStyle" role="status" aria-label="Cargando">
    <svg viewBox="0 0 24 24" fill="none">
      <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-opacity="0.18" stroke-width="2.5" />
      <path d="M21 12a9 9 0 0 0-9-9" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" />
    </svg>
  </span>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  size: { type: [Number, String], default: 18 },
  color: { type: String, default: 'currentColor' },
});

const spinnerStyle = computed(() => ({
  width: typeof props.size === 'number' ? `${props.size}px` : props.size,
  height: typeof props.size === 'number' ? `${props.size}px` : props.size,
  color: props.color,
}));
</script>

<style scoped>
.gc-spinner {
  display: inline-flex;
  animation: gc-spin 0.8s linear infinite;
}
.gc-spinner svg { width: 100%; height: 100%; }
@keyframes gc-spin { to { transform: rotate(360deg); } }
</style>
