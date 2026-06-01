<template>
  <div class="gc-stat">
    <span class="gc-stat__label">{{ label }}</span>
    <span class="gc-stat__value gc-mono">
      <slot>{{ value }}</slot>
    </span>
    <span v-if="delta !== null && delta !== ''" class="gc-stat__delta" :class="`gc-stat__delta--${deltaTone}`">
      {{ delta }}
    </span>
  </div>
</template>

<script setup>
/**
 * GcStat — un indicador. Valor en monoespaciado (identidad). Delta opcional
 * con tono semántico (positivo/negativo/neutral). Sin tarjeta: vive dentro de
 * un GcStatStrip que aporta los separadores hairline.
 */
defineProps({
  label: { type: String, default: '' },
  value: { type: [String, Number], default: '' },
  delta: { type: [String, Number], default: null },
  deltaTone: {
    type: String,
    default: 'neutral', // positive | negative | neutral
    validator: (v) => ['positive', 'negative', 'neutral'].includes(v),
  },
});
</script>

<style scoped>
.gc-stat {
  display: flex;
  flex-direction: column;
  gap: var(--gc-space-1);
  padding: var(--gc-space-3) var(--gc-space-5);
  min-width: 0;
}
.gc-stat__label {
  font-size: var(--gc-fs-xs);
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--gc-text-3);
}
.gc-stat__value {
  font-size: var(--gc-fs-xl);
  color: var(--gc-text);
}
.gc-stat__delta { font-size: var(--gc-fs-sm); font-family: var(--gc-font-mono); }
.gc-stat__delta--positive { color: var(--gc-success); }
.gc-stat__delta--negative { color: var(--gc-danger); }
.gc-stat__delta--neutral { color: var(--gc-text-3); }
</style>
