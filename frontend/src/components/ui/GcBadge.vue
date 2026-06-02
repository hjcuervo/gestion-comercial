<template>
  <span class="gc-badge" :class="[`gc-badge--${tone}`, { 'gc-badge--soft': soft }]">
    <span class="gc-badge__dot"></span>
    <slot>{{ label }}</slot>
  </span>
</template>

<script setup>
/**
 * GcBadge — marca de estado. El color comunica el estado; por defecto es
 * texto coloreado con un punto (sin relleno). Con `soft` añade un tinte muy
 * sutil de fondo para casos que necesiten algo más de presencia.
 *
 * tone: success | warning | danger | info | accent | neutral
 */
defineProps({
  tone: {
    type: String,
    default: 'neutral',
    validator: (v) => ['success', 'warning', 'danger', 'info', 'accent', 'neutral'].includes(v),
  },
  label: { type: String, default: '' },
  soft: { type: Boolean, default: false },
});
</script>

<style scoped>
.gc-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--gc-space-2);
  font-family: var(--gc-font-mono);
  font-size: var(--gc-fs-xs);
  font-weight: var(--gc-fw-medium);
  letter-spacing: 0.02em;
  text-transform: uppercase;
  padding: 2px 8px;
  border-radius: var(--gc-radius-sm);
  line-height: 1.6;
}
.gc-badge__dot {
  width: 6px;
  height: 6px;
  border-radius: var(--gc-radius-full);
  background: currentColor;
  flex-shrink: 0;
}

.gc-badge--success { color: var(--gc-success); }
.gc-badge--warning { color: var(--gc-warning); }
.gc-badge--danger  { color: var(--gc-danger); }
.gc-badge--info    { color: var(--gc-info); }
.gc-badge--accent  { color: var(--gc-accent); }
.gc-badge--neutral { color: var(--gc-text-2); }

.gc-badge--soft.gc-badge--success { background: var(--gc-success-soft); }
.gc-badge--soft.gc-badge--warning { background: var(--gc-warning-soft); }
.gc-badge--soft.gc-badge--danger  { background: var(--gc-danger-soft); }
.gc-badge--soft.gc-badge--info    { background: var(--gc-info-soft); }
.gc-badge--soft.gc-badge--accent  { background: var(--gc-accent-soft); }
.gc-badge--soft.gc-badge--neutral { background: var(--gc-neutral-soft); }
</style>
