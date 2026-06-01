<template>
  <div class="gc-stat-strip">
    <slot />
  </div>
</template>

<script setup>
/**
 * GcStatStrip — contenedor horizontal de GcStat separados por líneas finas.
 * Reemplaza las KPI cards/gradientes del sistema viejo (los datos van en
 * franja, no en tarjetas flotantes).
 */
</script>

<style scoped>
.gc-stat-strip {
  display: flex;
  flex-wrap: wrap;
  align-items: stretch;
  background: var(--gc-surface);
  border: 1px solid var(--gc-border);
  border-radius: var(--gc-radius-lg);
  overflow: hidden;
}
.gc-stat-strip > :deep(.gc-stat) {
  flex: 1 1 0;
  border-left: 1px solid var(--gc-border);
}
.gc-stat-strip > :deep(.gc-stat:first-child) {
  border-left: none;
}
</style>
